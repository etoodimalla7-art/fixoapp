from fastapi import APIRouter, HTTPException, Query, Depends
from typing import Optional, List
from pydantic import BaseModel
from backend.database import get_database
from backend.models import WorkerProfileModel, ServiceItemModel, ServiceCategory
from backend.security import get_current_user_claims

router = APIRouter(prefix="/workers", tags=["Workers"])

class UpdateAvailabilityRequest(BaseModel):
    working_days: List[str]
    slot_intervals: List[str]
    emergency_available: bool = False

@router.get("", response_model=List[WorkerProfileModel])
async def list_workers(
    category: Optional[ServiceCategory] = None,
    city: Optional[str] = None,
    verified_only: bool = False,
    emergency_only: bool = False,
    query: Optional[str] = None,
    limit: int = Query(default=20, le=50)
):
    db = get_database()
    filter_query = {}
    if category:
        filter_query["category"] = category.value
    if city:
        filter_query["location_city"] = {"$regex": city, "$options": "i"}
    if verified_only:
        filter_query["background_verified"] = True
    if emergency_only:
        filter_query["emergency_callout_available"] = True
    if query:
        filter_query["$or"] = [
            {"name": {"$regex": query, "$options": "i"}},
            {"skills": {"$in": [query]}},
            {"bio": {"$regex": query, "$options": "i"}}
        ]

    cursor = db.workers.find(filter_query, {"_id": 0}).limit(limit)
    workers = await cursor.to_list(length=limit)
    return workers

@router.get("/{worker_id}", response_model=WorkerProfileModel)
async def get_worker_profile(worker_id: str):
    db = get_database()
    worker = await db.workers.find_one({"id": worker_id}, {"_id": 0})
    if not worker:
        raise HTTPException(status_code=404, detail="Worker not found")
    return worker

@router.get("/{worker_id}/services", response_model=List[ServiceItemModel])
async def get_worker_services(worker_id: str):
    db = get_database()
    cursor = db.services.find({"worker_id": worker_id}, {"_id": 0})
    services = await cursor.to_list(length=100)
    return services

@router.put("/profile/availability")
async def update_availability(
    req: UpdateAvailabilityRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    result = await db.workers.update_one(
        {"user_id": claims["sub"]},
        {"$set": {
            "working_days": req.working_days,
            "slot_intervals": req.slot_intervals,
            "emergency_callout_available": req.emergency_available
        }}
    )
    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="Worker profile not found")
    return {"status": "success", "message": "Availability updated"}
