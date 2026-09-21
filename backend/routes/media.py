import os
import uuid
import shutil
from fastapi import APIRouter, UploadFile, File, HTTPException, Depends
from backend.security import get_current_user_claims

router = APIRouter(prefix="/media", tags=["Media Storage & Upload"])

MEDIA_DIR = os.getenv("MEDIA_STORAGE_DIR", "/tmp/fixo_media")
PUBLIC_BASE_URL = os.getenv("PUBLIC_BASE_URL", "https://fixo.cm")

os.makedirs(MEDIA_DIR, exist_ok=True)

ALLOWED_VIDEO_MIMES = {"video/mp4", "video/webm", "video/quicktime", "video/3gpp"}
ALLOWED_IMAGE_MIMES = {"image/jpeg", "image/png", "image/webp"}

MAX_VIDEO_SIZE = 50 * 1024 * 1024  # 50 MB
MAX_IMAGE_SIZE = 10 * 1024 * 1024  # 10 MB

@router.post("/upload")
async def upload_media_file(
    file: UploadFile = File(...),
    claims: dict = Depends(get_current_user_claims)
):
    """
    Authoritative media storage endpoint for worker video reels and craftsmanship thumbnails.
    Validates MIME type, file size, generates a unique storage key, and returns public URL.
    """
    content_type = file.content_type or ""
    is_video = content_type in ALLOWED_VIDEO_MIMES
    is_image = content_type in ALLOWED_IMAGE_MIMES

    if not is_video and not is_image:
        raise HTTPException(
            status_code=400,
            detail=f"Unsupported media format '{content_type}'. Allowed: MP4, WebM, QuickTime, JPEG, PNG, WebP."
        )

    ext = os.path.splitext(file.filename or "")[1].lower()
    if not ext:
        ext = ".mp4" if is_video else ".jpg"

    file_id = f"fixo_media_{uuid.uuid4().hex[:16]}{ext}"
    dest_path = os.path.join(MEDIA_DIR, file_id)

    # Read and validate size
    contents = await file.read()
    file_size = len(contents)

    if is_video and file_size > MAX_VIDEO_SIZE:
        raise HTTPException(status_code=413, detail="Video exceeds maximum allowed size of 50 MB.")
    if is_image and file_size > MAX_IMAGE_SIZE:
        raise HTTPException(status_code=413, detail="Thumbnail image exceeds maximum allowed size of 10 MB.")

    with open(dest_path, "wb") as buffer:
        buffer.write(contents)

    public_url = f"{PUBLIC_BASE_URL}/media/{file_id}"

    return {
        "status": "success",
        "file_id": file_id,
        "media_type": "video" if is_video else "image",
        "size_bytes": file_size,
        "content_type": content_type,
        "url": public_url
    }
