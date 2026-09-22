import uuid
import time
from fastapi import APIRouter, HTTPException, Depends, Query
from typing import List, Optional
from backend.database import get_database
from backend.models import (
    ChatMessageModel, ConversationModel, SendChatMessageRequest,
    ReportConversationRequest, UserRole, MessageDeliveryStatus,
    NotificationCategory
)
from backend.security import get_current_user_claims

router = APIRouter(prefix="/chat", tags=["Realtime Chat & Messaging"])

@router.get("/conversations", response_model=List[ConversationModel])
async def list_conversations(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    worker = await db.workers.find_one({"user_id": user_id})
    worker_id = worker["id"] if worker else None

    query = {"$or": [{"customer_id": user_id}]}
    if worker_id:
        query["$or"].append({"worker_id": worker_id})

    cursor = db.conversations.find(query, {"_id": 0}).sort("last_message_time", -1)
    conversations = await cursor.to_list(length=50)
    return conversations

@router.post("/conversations", response_model=ConversationModel)
async def get_or_create_conversation(
    worker_id: str,
    booking_id: Optional[str] = None,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    customer_id = claims["sub"]
    customer = await db.users.find_one({"id": customer_id})
    if not customer:
        raise HTTPException(status_code=404, detail="Customer not found")

    worker = await db.workers.find_one({"id": worker_id})
    if not worker:
        raise HTTPException(status_code=404, detail="Worker not found")

    # Check existing conversation
    existing_query = {"customer_id": customer_id, "worker_id": worker_id}
    if booking_id:
        existing_query["booking_id"] = booking_id

    existing = await db.conversations.find_one(existing_query, {"_id": 0})
    if existing:
        return ConversationModel(**existing)

    conv_id = f"cnv_{uuid.uuid4().hex[:12]}"
    conv_doc = {
        "id": conv_id,
        "booking_id": booking_id,
        "customer_id": customer_id,
        "customer_name": customer["name"],
        "worker_id": worker_id,
        "worker_name": worker["name"],
        "last_message": "Conversation initiated",
        "last_message_time": int(time.time() * 1000),
        "customer_unread": 0,
        "worker_unread": 0,
        "created_at": int(time.time() * 1000)
    }
    await db.conversations.insert_one(conv_doc)
    return ConversationModel(**conv_doc)

@router.get("/conversations/{conversation_id}/messages", response_model=List[ChatMessageModel])
async def get_conversation_messages(
    conversation_id: str,
    limit: int = Query(default=50, le=100),
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    conv = await db.conversations.find_one({"id": conversation_id})
    if not conv:
        raise HTTPException(status_code=404, detail="Conversation not found")

    user_id = claims["sub"]
    worker = await db.workers.find_one({"user_id": user_id})
    worker_id = worker["id"] if worker else None

    # Object-level authorization
    if conv["customer_id"] != user_id and conv["worker_id"] != worker_id:
        raise HTTPException(status_code=403, detail="Unauthorized to view this conversation")

    cursor = db.messages.find({"conversation_id": conversation_id}, {"_id": 0}).sort("created_at", 1).limit(limit)
    messages = await cursor.to_list(length=limit)
    return messages

@router.post("/conversations/{conversation_id}/messages", response_model=ChatMessageModel)
async def send_chat_message(
    conversation_id: str,
    req: SendChatMessageRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    conv = await db.conversations.find_one({"id": conversation_id})
    if not conv:
        raise HTTPException(status_code=404, detail="Conversation not found")

    sender_id = claims["sub"]
    sender = await db.users.find_one({"id": sender_id})
    worker = await db.workers.find_one({"user_id": sender_id})
    worker_id = worker["id"] if worker else None

    is_customer = (conv["customer_id"] == sender_id)
    is_worker = (conv["worker_id"] == worker_id)

    if not is_customer and not is_worker:
        raise HTTPException(status_code=403, detail="Unauthorized to send messages in this conversation")

    recipient_id = conv["worker_id"] if is_customer else conv["customer_id"]
    sender_role = UserRole.CUSTOMER if is_customer else UserRole.WORKER
    sender_name = sender["name"] if sender else "User"

    msg_id = f"msg_{uuid.uuid4().hex[:12]}"
    now = int(time.time() * 1000)

    msg_doc = {
        "id": msg_id,
        "conversation_id": conversation_id,
        "booking_id": conv.get("booking_id"),
        "sender_id": sender_id,
        "sender_name": sender_name,
        "sender_role": sender_role.value,
        "recipient_id": recipient_id,
        "text": req.text,
        "attachment_url": req.attachment_url,
        "attachment_type": req.attachment_type,
        "delivery_status": MessageDeliveryStatus.SENT.value,
        "is_read": False,
        "created_at": now
    }
    await db.messages.insert_one(msg_doc)

    # Update conversation last message & unread counter
    update_data = {
        "last_message": req.text[:60],
        "last_message_time": now
    }
    inc_field = "worker_unread" if is_customer else "customer_unread"

    await db.conversations.update_one(
        {"id": conversation_id},
        {"$set": update_data, "$inc": {inc_field: 1}}
    )

    # Insert notification for recipient
    target_user_id = recipient_id
    if not is_customer:
        # recipient is customer_id directly
        target_user_id = conv["customer_id"]
    else:
        # recipient is worker_id, find worker user_id
        target_worker = await db.workers.find_one({"id": conv["worker_id"]})
        if target_worker:
            target_user_id = target_worker["user_id"]

    await db.notifications.insert_one({
        "id": f"notif_{uuid.uuid4().hex[:12]}",
        "user_id": target_user_id,
        "category": NotificationCategory.MESSAGES.value,
        "title": f"New message from {sender_name}",
        "body": req.text[:100],
        "reference_id": conversation_id,
        "deep_link": f"fixo://chat/{conversation_id}",
        "is_read": False,
        "created_at": now
    })

    return ChatMessageModel(**msg_doc)

@router.put("/conversations/{conversation_id}/read")
async def mark_messages_read(
    conversation_id: str,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    conv = await db.conversations.find_one({"id": conversation_id})
    if not conv:
        raise HTTPException(status_code=404, detail="Conversation not found")

    user_id = claims["sub"]
    worker = await db.workers.find_one({"user_id": user_id})
    worker_id = worker["id"] if worker else None

    is_customer = (conv["customer_id"] == user_id)
    is_worker = (conv["worker_id"] == worker_id)

    if not is_customer and not is_worker:
        raise HTTPException(status_code=403, detail="Unauthorized")

    reset_field = "customer_unread" if is_customer else "worker_unread"
    await db.conversations.update_one(
        {"id": conversation_id},
        {"$set": {reset_field: 0}}
    )

    await db.messages.update_many(
        {"conversation_id": conversation_id, "recipient_id": user_id},
        {"$set": {"is_read": True, "delivery_status": MessageDeliveryStatus.READ.value}}
    )

    return {"status": "success"}

@router.post("/conversations/{conversation_id}/report")
async def report_conversation(
    conversation_id: str,
    req: ReportConversationRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    report_id = f"rpt_{uuid.uuid4().hex[:12]}"
    await db.safety_reports.insert_one({
        "id": report_id,
        "conversation_id": conversation_id,
        "reporter_id": claims["sub"],
        "reported_user_id": req.reported_user_id,
        "reason": req.reason,
        "status": "PENDING_REVIEW",
        "created_at": int(time.time() * 1000)
    })
    return {"status": "success", "message": "Report submitted and sent to safety team."}
