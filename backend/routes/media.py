import os
import uuid
import shutil
import time
from typing import Optional
from fastapi import APIRouter, UploadFile, File, Form, HTTPException, Depends, status
from fastapi.responses import FileResponse
from backend.database import get_database
from backend.security import get_current_user_claims, security_bearer

router = APIRouter(prefix="/media", tags=["Media Storage & Authorization"])

MEDIA_DIR = os.getenv("MEDIA_STORAGE_DIR", "/tmp/fixo_media")
PUBLIC_BASE_URL = os.getenv("PUBLIC_BASE_URL", "https://fixo.cm")

os.makedirs(MEDIA_DIR, exist_ok=True)

ALLOWED_VIDEO_MIMES = {"video/mp4", "video/webm", "video/quicktime", "video/3gpp"}
ALLOWED_IMAGE_MIMES = {"image/jpeg", "image/png", "image/webp"}
ALLOWED_DOC_MIMES = {"application/pdf", "image/jpeg", "image/png"}

MAX_VIDEO_SIZE = 50 * 1024 * 1024  # 50 MB
MAX_IMAGE_SIZE = 10 * 1024 * 1024  # 10 MB
MAX_DOC_SIZE = 10 * 1024 * 1024    # 10 MB

@router.post("/upload")
async def upload_media_file(
    file: UploadFile = File(...),
    is_private: bool = Form(False),
    purpose: str = Form("REEL"),  # REEL, THUMBNAIL, CNI, RCCM, DISPUTE_ATTACHMENT
    booking_id: Optional[str] = Form(None),
    claims: dict = Depends(get_current_user_claims)
):
    """
    Authoritative media storage endpoint with logical privacy separation.
    Protects sensitive identity documents, CNI, RCCM, and dispute attachments with strict authorization.
    """
    content_type = file.content_type or ""
    is_video = content_type in ALLOWED_VIDEO_MIMES
    is_image = content_type in ALLOWED_IMAGE_MIMES
    is_doc = content_type in ALLOWED_DOC_MIMES

    if not is_video and not is_image and not is_doc:
        raise HTTPException(
            status_code=400,
            detail=f"Unsupported media format '{content_type}'. Allowed: MP4, WebM, JPEG, PNG, WebP, PDF."
        )

    ext = os.path.splitext(file.filename or "")[1].lower()
    if not ext:
        if is_video:
            ext = ".mp4"
        elif is_image:
            ext = ".jpg"
        else:
            ext = ".pdf"

    # Auto-classify sensitive purposes as private
    if purpose in ["CNI", "RCCM", "DISPUTE_ATTACHMENT", "IDENTITY_VERIFICATION"]:
        is_private = True

    file_id = f"fixo_media_{uuid.uuid4().hex[:16]}{ext}"
    dest_path = os.path.join(MEDIA_DIR, file_id)

    contents = await file.read()
    file_size = len(contents)

    if is_video and file_size > MAX_VIDEO_SIZE:
        raise HTTPException(status_code=413, detail="Video exceeds maximum allowed size of 50 MB.")
    if is_image and file_size > MAX_IMAGE_SIZE:
        raise HTTPException(status_code=413, detail="Image exceeds maximum allowed size of 10 MB.")
    if is_doc and file_size > MAX_DOC_SIZE:
        raise HTTPException(status_code=413, detail="Document exceeds maximum allowed size of 10 MB.")

    with open(dest_path, "wb") as buffer:
        buffer.write(contents)

    db = get_database()
    now_ms = int(time.time() * 1000)

    media_record = {
        "file_id": file_id,
        "owner_id": claims["sub"],
        "is_private": is_private,
        "purpose": purpose,
        "booking_id": booking_id,
        "content_type": content_type,
        "file_size": file_size,
        "created_at": now_ms
    }
    await db.media_files.insert_one(media_record)

    public_url = f"{PUBLIC_BASE_URL}/media/{file_id}" if not is_private else f"/api/v1/media/secure/{file_id}"

    return {
        "status": "success",
        "file_id": file_id,
        "media_type": "video" if is_video else ("document" if is_doc and not is_image else "image"),
        "is_private": is_private,
        "size_bytes": file_size,
        "content_type": content_type,
        "url": public_url
    }

@router.get("/secure/{file_id}")
async def get_secure_media(
    file_id: str,
    claims: dict = Depends(get_current_user_claims)
):
    """
    Object-level authorization check for sensitive private media files.
    Allows access only to the file owner, platform admin, or participants of the associated booking.
    """
    db = get_database()
    record = await db.media_files.find_one({"file_id": file_id})
    dest_path = os.path.join(MEDIA_DIR, file_id)

    if not os.path.exists(dest_path):
        raise HTTPException(status_code=404, detail="Media file not found on storage node.")

    if not record:
        # If not tracked as private, serve file
        return FileResponse(dest_path)

    if record.get("is_private", False):
        caller_id = claims["sub"]
        caller_role = claims.get("role")

        is_owner = (record["owner_id"] == caller_id)
        is_admin = (caller_role == "ADMIN")

        is_authorized_booking_participant = False
        if record.get("booking_id"):
            booking = await db.bookings.find_one({"id": record["booking_id"]})
            if booking and (booking.get("customer_id") == caller_id or booking.get("worker_id") == caller_id):
                is_authorized_booking_participant = True

        if not is_owner and not is_admin and not is_authorized_booking_participant:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Access denied: You are not authorized to view this private document."
            )

    return FileResponse(dest_path, media_type=record.get("content_type", "application/octet-stream"))

@router.delete("/{file_id}")
async def delete_media_file(
    file_id: str,
    claims: dict = Depends(get_current_user_claims)
):
    """
    Authoritatively deletes media file. Requires caller to be file owner or admin.
    """
    db = get_database()
    record = await db.media_files.find_one({"file_id": file_id})
    dest_path = os.path.join(MEDIA_DIR, file_id)

    if not os.path.exists(dest_path) and not record:
        raise HTTPException(status_code=404, detail="Media file not found.")

    if record:
        caller_id = claims["sub"]
        caller_role = claims.get("role")
        if record["owner_id"] != caller_id and caller_role != "ADMIN":
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You are not authorized to delete this media asset."
            )
        await db.media_files.delete_one({"file_id": file_id})

    if os.path.exists(dest_path):
        os.remove(dest_path)

    return {"status": "success", "message": "Media asset deleted successfully."}
