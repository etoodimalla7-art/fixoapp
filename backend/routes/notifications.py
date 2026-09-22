import uuid
import time
from fastapi import APIRouter, HTTPException, Depends
from typing import List
from backend.database import get_database
from backend.models import (
    NotificationModel, DeviceTokenRequest, NotificationPreferencesModel
)
from backend.security import get_current_user_claims

router = APIRouter(prefix="/notifications", tags=["Notifications"])

@router.get("", response_model=List[NotificationModel])
async def list_notifications(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    cursor = db.notifications.find({"user_id": user_id}, {"_id": 0}).sort("created_at", -1).limit(50)
    notifs = await cursor.to_list(length=50)
    return notifs

@router.put("/{notif_id}/read")
async def mark_notification_read(notif_id: str, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    result = await db.notifications.update_one(
        {"id": notif_id, "user_id": user_id},
        {"$set": {"is_read": True}}
    )
    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="Notification not found")
    return {"status": "success"}

@router.put("/read-all")
async def mark_all_notifications_read(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    await db.notifications.update_many(
        {"user_id": user_id, "is_read": False},
        {"$set": {"is_read": True}}
    )
    return {"status": "success"}

@router.post("/device-token")
async def register_device_token(req: DeviceTokenRequest, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    await db.device_tokens.update_one(
        {"user_id": user_id, "device_token": req.device_token},
        {"$set": {
            "platform": req.platform,
            "updated_at": int(time.time() * 1000)
        }},
        upsert=True
    )
    return {"status": "success", "message": "Device token registered"}

@router.delete("/device-token")
async def unregister_device_token(device_token: str, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    await db.device_tokens.delete_one({"user_id": user_id, "device_token": device_token})
    return {"status": "success", "message": "Device token removed"}

@router.get("/preferences", response_model=NotificationPreferencesModel)
async def get_notification_preferences(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    pref = await db.notification_preferences.find_one({"user_id": user_id}, {"_id": 0})
    if not pref:
        return NotificationPreferencesModel()
    return NotificationPreferencesModel(**pref)

@router.put("/preferences", response_model=NotificationPreferencesModel)
async def update_notification_preferences(
    req: NotificationPreferencesModel,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    user_id = claims["sub"]
    doc = req.model_dump()
    doc["user_id"] = user_id
    doc["updated_at"] = int(time.time() * 1000)
    await db.notification_preferences.update_one(
        {"user_id": user_id},
        {"$set": doc},
        upsert=True
    )
    return req
