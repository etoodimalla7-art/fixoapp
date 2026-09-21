import os
import uuid
import time
from fastapi import APIRouter, HTTPException, Depends, Query
from typing import List, Optional
from backend.database import get_database
from backend.models import CreateReelRequest, UpdateReelRequest, ReelModel, ServiceCategory
from backend.security import get_current_user_claims

router = APIRouter(prefix="/reels", tags=["Reels & Video Showcase"])

PUBLIC_BASE_URL = os.getenv("PUBLIC_BASE_URL", "https://fixo.cm")

@router.get("", response_model=List[ReelModel])
async def list_reels(
    category: Optional[ServiceCategory] = None,
    limit: int = Query(default=20, le=50)
):
    db = get_database()
    filter_query = {"is_published": True}
    if category:
        filter_query["category"] = category.value

    cursor = db.reels.find(filter_query, {"_id": 0}).sort("created_at", -1).limit(limit)
    reels = await cursor.to_list(length=limit)
    return reels

@router.get("/my-reels", response_model=List[ReelModel])
async def list_worker_reels(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    worker = await db.workers.find_one({"user_id": user_id})
    if not worker:
        return []

    cursor = db.reels.find({"worker_id": worker["id"]}, {"_id": 0}).sort("created_at", -1)
    reels = await cursor.to_list(length=100)
    return reels

@router.get("/{reel_id}", response_model=ReelModel)
async def get_reel_by_id(reel_id: str):
    db = get_database()
    reel = await db.reels.find_one({"id": reel_id, "is_published": True}, {"_id": 0})
    if not reel:
        raise HTTPException(status_code=404, detail="Reel not found or unpublished")
    return reel

@router.post("", response_model=ReelModel)
async def create_reel(req: CreateReelRequest, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    worker = await db.workers.find_one({"user_id": user_id})
    if not worker:
        raise HTTPException(status_code=403, detail="Only verified craftspersons can publish reels.")

    reel_id = f"rel_{uuid.uuid4().hex[:12]}"
    public_url = f"{PUBLIC_BASE_URL}/reels/{reel_id}"

    reel_doc = {
        "id": reel_id,
        "worker_id": worker["id"],
        "worker_name": worker["name"],
        "worker_avatar": worker.get("avatar_url", ""),
        "title": req.title,
        "description": req.description,
        "category": req.category.value,
        "service_id": req.service_id,
        "video_url": req.video_url,
        "thumbnail_url": req.thumbnail_url,
        "likes_count": 0,
        "bookings_count": 0,
        "tags": req.tags,
        "is_published": req.is_published,
        "public_share_url": public_url,
        "created_at": int(time.time() * 1000)
    }
    await db.reels.insert_one(reel_doc)
    return ReelModel(**reel_doc)

@router.put("/{reel_id}", response_model=ReelModel)
async def update_reel(reel_id: str, req: UpdateReelRequest, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    worker = await db.workers.find_one({"user_id": user_id})
    if not worker:
        raise HTTPException(status_code=403, detail="Unauthorized")

    update_fields = {}
    if req.title is not None:
        update_fields["title"] = req.title
    if req.description is not None:
        update_fields["description"] = req.description
    if req.category is not None:
        update_fields["category"] = req.category.value
    if req.tags is not None:
        update_fields["tags"] = req.tags
    if req.thumbnail_url is not None:
        update_fields["thumbnail_url"] = req.thumbnail_url
    if req.is_published is not None:
        update_fields["is_published"] = req.is_published

    if not update_fields:
        reel = await db.reels.find_one({"id": reel_id, "worker_id": worker["id"]}, {"_id": 0})
        if not reel:
            raise HTTPException(status_code=404, detail="Reel not found")
        return ReelModel(**reel)

    result = await db.reels.update_one(
        {"id": reel_id, "worker_id": worker["id"]},
        {"$set": update_fields}
    )
    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="Reel not found or unauthorized")

    updated = await db.reels.find_one({"id": reel_id}, {"_id": 0})
    return ReelModel(**updated)

@router.post("/{reel_id}/like")
async def like_reel(reel_id: str):
    db = get_database()
    result = await db.reels.update_one({"id": reel_id}, {"$inc": {"likes_count": 1}})
    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="Reel not found")
    return {"status": "success", "message": "Liked reel"}

@router.delete("/{reel_id}")
async def delete_reel(reel_id: str, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    worker = await db.workers.find_one({"user_id": user_id})
    if not worker:
        raise HTTPException(status_code=403, detail="Unauthorized")

    result = await db.reels.delete_one({"id": reel_id, "worker_id": worker["id"]})
    if result.deleted_count == 0:
        raise HTTPException(status_code=404, detail="Reel not found or not owned by you.")
    return {"status": "success", "message": "Reel deleted."}
