import uuid
import time
from fastapi import APIRouter, HTTPException, Depends, Query, status
from typing import List, Optional
from pydantic import BaseModel, Field
from backend.database import get_database
from backend.models import (
    AdminDashboardStatsModel, DisputeModel, ResolveDisputeRequest,
    WorkerProfileModel, BookingModel, VerificationStatus, EscrowStatus,
    JobStatus
)
from backend.security import get_current_user_claims

router = APIRouter(prefix="/admin", tags=["Admin Portal & Platform Governance"])

PLATFORM_COMMISSION_PERCENT = 0.10

def require_admin(claims: dict = Depends(get_current_user_claims)):
    if claims.get("role") != "ADMIN":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Forbidden: Authoritative platform administrator privileges required."
        )
    return claims

class SuspendUserRequest(BaseModel):
    reason: str = Field(..., min_length=5, max_length=500)

@router.get("/stats", response_model=AdminDashboardStatsModel)
async def get_admin_stats(admin: dict = Depends(require_admin)):
    db = get_database()
    total_users = await db.users.count_documents({})
    total_workers = await db.workers.count_documents({})
    total_bookings = await db.bookings.count_documents({})
    active_disputes = await db.disputes.count_documents({"status": "OPEN"})
    verified_workers_count = await db.workers.count_documents({"background_verified": True})

    pipeline = [{"$group": {"_id": None, "total_gmv": {"$sum": "$price_amount_xaf"}}}]
    cursor = db.bookings.aggregate(pipeline)
    gmv_result = await cursor.to_list(length=1)
    total_gmv = gmv_result[0]["total_gmv"] if gmv_result else 0.0

    return AdminDashboardStatsModel(
        total_users=total_users,
        total_workers=total_workers,
        total_bookings=total_bookings,
        total_gmv_xaf=total_gmv,
        active_disputes=active_disputes,
        verified_workers_count=verified_workers_count
    )

@router.get("/disputes", response_model=List[DisputeModel])
async def list_admin_disputes(
    status: Optional[str] = None,
    admin: dict = Depends(require_admin)
):
    db = get_database()
    query = {}
    if status:
        query["status"] = status
    cursor = db.disputes.find(query, {"_id": 0}).sort("created_at", -1)
    disputes = await cursor.to_list(length=100)
    return disputes

@router.post("/disputes/{dispute_id}/resolve")
async def resolve_dispute(
    dispute_id: str,
    req: ResolveDisputeRequest,
    admin: dict = Depends(require_admin)
):
    db = get_database()
    dispute = await db.disputes.find_one({"id": dispute_id})
    if not dispute:
        raise HTTPException(status_code=404, detail="Dispute not found")

    booking = await db.bookings.find_one({"id": dispute["booking_id"]})
    if not booking:
        raise HTTPException(status_code=404, detail="Associated booking not found")

    now = int(time.time() * 1000)
    price_xaf = booking["price_amount_xaf"]

    if req.resolution.value == "RESOLVED_REFUND_CUSTOMER":
        if booking["escrow_status"] == EscrowStatus.HOLDING.value:
            await db.users.update_one(
                {"id": booking["customer_id"]},
                {"$inc": {"escrow_locked_xaf": -price_xaf, "balance_xaf": price_xaf}}
            )
            await db.transactions.insert_one({
                "id": f"txn_{uuid.uuid4().hex[:12]}",
                "user_id": booking["customer_id"],
                "type": "REFUND",
                "amount_xaf": price_xaf,
                "currency": "XAF",
                "description": f"Dispute resolution refund for job #{booking['id'][:8]}. Admin note: {req.admin_notes}",
                "status": "COMPLETED",
                "payment_provider": "FIXO_ESCROW",
                "reference_code": f"DSP-REF-{uuid.uuid4().hex[:6].upper()}",
                "created_at": now
            })

        await db.bookings.update_one(
            {"id": booking["id"]},
            {"$set": {"status": JobStatus.CANCELLED.value, "escrow_status": EscrowStatus.REFUNDED.value}}
        )

    elif req.resolution.value == "RESOLVED_RELEASE_WORKER":
        worker_payout = price_xaf * (1.0 - PLATFORM_COMMISSION_PERCENT)
        if booking["escrow_status"] == EscrowStatus.HOLDING.value:
            await db.users.update_one(
                {"id": booking["customer_id"]},
                {"$inc": {"escrow_locked_xaf": -price_xaf}}
            )
            worker = await db.workers.find_one({"id": booking["worker_id"]})
            if worker:
                await db.users.update_one(
                    {"id": worker["user_id"]},
                    {"$inc": {"balance_xaf": worker_payout}}
                )
            await db.transactions.insert_one({
                "id": f"txn_{uuid.uuid4().hex[:12]}",
                "user_id": worker["user_id"] if worker else "",
                "type": "PAYOUT",
                "amount_xaf": worker_payout,
                "currency": "XAF",
                "description": f"Dispute arbitration payout for job #{booking['id'][:8]}. Admin note: {req.admin_notes}",
                "status": "COMPLETED",
                "payment_provider": "FIXO_ESCROW",
                "reference_code": f"DSP-PAY-{uuid.uuid4().hex[:6].upper()}",
                "created_at": now
            })

        await db.bookings.update_one(
            {"id": booking["id"]},
            {"$set": {"status": JobStatus.COMPLETED.value, "escrow_status": EscrowStatus.RELEASED.value}}
        )

    await db.disputes.update_one(
        {"id": dispute_id},
        {"$set": {
            "status": req.resolution.value,
            "admin_notes": req.admin_notes,
            "resolved_at": now
        }}
    )

    # Cryptographic/auditable ledger of administrative action
    await db.audit_logs.insert_one({
        "id": f"aud_{uuid.uuid4().hex[:12]}",
        "event": "DISPUTE_RESOLVED",
        "resource_id": dispute_id,
        "resolution": req.resolution.value,
        "actor_id": admin["sub"],
        "admin_notes": req.admin_notes,
        "timestamp": now
    })

    return {"status": "success", "message": f"Dispute resolved with: {req.resolution.value}"}

@router.get("/workers/pending", response_model=List[WorkerProfileModel])
async def list_pending_workers(admin: dict = Depends(require_admin)):
    db = get_database()
    cursor = db.workers.find({"background_verified": False}, {"_id": 0})
    workers = await cursor.to_list(length=100)
    return workers

@router.post("/workers/{worker_id}/verify")
async def verify_worker(
    worker_id: str,
    status: VerificationStatus,
    admin: dict = Depends(require_admin)
):
    db = get_database()
    is_verified = (status in [VerificationStatus.VERIFIED_PRO, VerificationStatus.MASTER_CRAFTSMAN])
    result = await db.workers.update_one(
        {"id": worker_id},
        {"$set": {
            "background_verified": is_verified,
            "verification_status": status.value
        }}
    )
    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="Worker not found")

    worker = await db.workers.find_one({"id": worker_id})
    if worker:
        await db.users.update_one(
            {"id": worker["user_id"]},
            {"$set": {"verification_status": status.value}}
        )

    now = int(time.time() * 1000)
    await db.audit_logs.insert_one({
        "id": f"aud_{uuid.uuid4().hex[:12]}",
        "event": "WORKER_VERIFICATION_DECISION",
        "resource_id": worker_id,
        "decision": status.value,
        "actor_id": admin["sub"],
        "timestamp": now
    })

    return {"status": "success", "message": f"Worker verification updated to {status.value}"}

@router.post("/users/{user_id}/suspend")
async def suspend_user(
    user_id: str,
    req: SuspendUserRequest,
    admin: dict = Depends(require_admin)
):
    """
    Administrative user suspension.
    Instantly marks user suspended and immediately revokes all active authentication sessions.
    """
    db = get_database()
    now_ms = int(time.time() * 1000)

    user = await db.users.find_one({"id": user_id})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    await db.users.update_one(
        {"id": user_id},
        {"$set": {
            "is_suspended": True,
            "suspension_reason": req.reason,
            "suspended_at": now_ms,
            "suspended_by": admin["sub"]
        }}
    )

    # Invalidate all active sessions for this user immediately
    res = await db.sessions.update_many(
        {"user_id": user_id, "is_revoked": False},
        {"$set": {
            "is_revoked": True,
            "revoked_at": now_ms,
            "revocation_reason": f"ADMIN_SUSPENSION: {req.reason}"
        }}
    )

    await db.audit_logs.insert_one({
        "id": f"aud_{uuid.uuid4().hex[:12]}",
        "event": "USER_SUSPENDED",
        "resource_id": user_id,
        "actor_id": admin["sub"],
        "reason": req.reason,
        "sessions_revoked": res.modified_count,
        "timestamp": now_ms
    })

    return {
        "status": "success",
        "message": f"User {user_id} suspended and {res.modified_count} session(s) revoked."
    }

@router.post("/users/{user_id}/unsuspend")
async def unsuspend_user(
    user_id: str,
    admin: dict = Depends(require_admin)
):
    db = get_database()
    now_ms = int(time.time() * 1000)

    user = await db.users.find_one({"id": user_id})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    await db.users.update_one(
        {"id": user_id},
        {"$set": {
            "is_suspended": False,
            "unsuspended_at": now_ms,
            "unsuspended_by": admin["sub"]
        }}
    )

    await db.audit_logs.insert_one({
        "id": f"aud_{uuid.uuid4().hex[:12]}",
        "event": "USER_UNSUSPENDED",
        "resource_id": user_id,
        "actor_id": admin["sub"],
        "timestamp": now_ms
    })

    return {"status": "success", "message": f"User {user_id} suspension removed."}
