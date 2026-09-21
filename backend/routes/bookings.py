import uuid
import time
from fastapi import APIRouter, HTTPException, Depends
from typing import List
from backend.database import get_database
from backend.models import (
    CreateBookingRequest, BookingModel, JobStatus, EscrowStatus,
    LedgerTransactionModel, PaymentMethod
)
from backend.security import get_current_user_claims

router = APIRouter(prefix="/bookings", tags=["Bookings & Escrow"])

PLATFORM_COMMISSION_PERCENT = 0.10 # 10% platform fee

@router.post("", response_model=BookingModel)
async def create_booking(req: CreateBookingRequest, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    customer = await db.users.find_one({"id": claims["sub"]})
    if not customer:
        raise HTTPException(status_code=404, detail="Customer not found")

    worker = await db.workers.find_one({"id": req.worker_id})
    if not worker:
        raise HTTPException(status_code=404, detail="Worker not found")

    service = await db.services.find_one({"id": req.service_id, "worker_id": req.worker_id})
    price_xaf = service["price_xaf"] if service else worker.get("hourly_rate_xaf", 15000.0)
    service_title = service["name"] if service else "General Service Callout"

    # Escrow logic: verify customer wallet balance if using FIXO_WALLET
    if req.payment_method == PaymentMethod.FIXO_WALLET:
        if customer.get("balance_xaf", 0.0) < price_xaf:
            raise HTTPException(status_code=400, detail="Insufficient wallet balance for escrow lock.")
        # Deduct balance and lock in escrow
        await db.users.update_one(
            {"id": customer["id"]},
            {"$inc": {"balance_xaf": -price_xaf, "escrow_locked_xaf": price_xaf}}
        )

    booking_id = f"bk_{uuid.uuid4().hex[:12]}"
    booking_doc = {
        "id": booking_id,
        "customer_id": customer["id"],
        "customer_name": customer["name"],
        "worker_id": worker["id"],
        "worker_name": worker["name"],
        "service_title": service_title,
        "category": worker["category"],
        "date": req.date,
        "time_slot": req.time_slot,
        "status": JobStatus.REQUESTED.value,
        "address": req.address,
        "notes": req.notes,
        "price_amount_xaf": price_xaf,
        "escrow_status": EscrowStatus.HOLDING.value,
        "payment_method": req.payment_method.value,
        "customer_rating": 0.0,
        "customer_review_text": "",
        "points_earned": 0,
        "created_at": int(time.time() * 1000)
    }
    await db.bookings.insert_one(booking_doc)

    # Record ledger entry
    await db.transactions.insert_one({
        "id": f"txn_{uuid.uuid4().hex[:12]}",
        "user_id": customer["id"],
        "type": "ESCROW_HOLD",
        "amount_xaf": price_xaf,
        "currency": "XAF",
        "description": f"Escrow lock for job #{booking_id[:8]} with {worker['name']}",
        "status": "COMPLETED",
        "payment_provider": req.payment_method.value,
        "reference_code": f"ESC-{uuid.uuid4().hex[:8].upper()}",
        "created_at": int(time.time() * 1000)
    })

    return BookingModel(**booking_doc)

@router.put("/{booking_id}/status")
async def update_job_status(booking_id: str, new_status: JobStatus, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    booking = await db.bookings.find_one({"id": booking_id})
    if not booking:
        raise HTTPException(status_code=404, detail="Booking not found")

    user_id = claims["sub"]
    role = claims.get("role")

    # Authoritative validation of state transitions
    current_status = JobStatus(booking["status"])

    # Worker actions
    if new_status in [JobStatus.ACCEPTED, JobStatus.EN_ROUTE, JobStatus.IN_PROGRESS, JobStatus.COMPLETION_REQUESTED]:
        worker = await db.workers.find_one({"id": booking["worker_id"]})
        if not worker or worker.get("user_id") != user_id:
            raise HTTPException(status_code=403, detail="Only assigned worker can advance to this status.")

    # Escrow release upon completion
    if new_status == JobStatus.COMPLETED:
        if booking["customer_id"] != user_id and role != "ADMIN":
            raise HTTPException(status_code=403, detail="Only the customer or admin can approve completion and release escrow.")

        if booking["escrow_status"] == EscrowStatus.HOLDING.value:
            worker = await db.workers.find_one({"id": booking["worker_id"]})
            total_price = booking["price_amount_xaf"]
            commission = total_price * PLATFORM_COMMISSION_PERCENT
            worker_payout = total_price - commission

            # Unlock customer escrow
            await db.users.update_one(
                {"id": booking["customer_id"]},
                {"$inc": {"escrow_locked_xaf": -total_price, "fixo_points": 50}}
            )

            # Credit worker balance
            if worker:
                await db.users.update_one(
                    {"id": worker["user_id"]},
                    {"$inc": {"balance_xaf": worker_payout}}
                )
                await db.workers.update_one(
                    {"id": worker["id"]},
                    {"$inc": {"completed_jobs": 1}}
                )

            # Record ledger entries
            await db.transactions.insert_one({
                "id": f"txn_{uuid.uuid4().hex[:12]}",
                "user_id": worker["user_id"] if worker else "",
                "type": "PAYOUT",
                "amount_xaf": worker_payout,
                "currency": "XAF",
                "description": f"Payout for completed job #{booking_id[:8]} (90% after commission)",
                "status": "COMPLETED",
                "payment_provider": "FIXO_ESCROW",
                "reference_code": f"PAY-{uuid.uuid4().hex[:8].upper()}",
                "created_at": int(time.time() * 1000)
            })

            await db.bookings.update_one(
                {"id": booking_id},
                {"$set": {"status": new_status.value, "escrow_status": EscrowStatus.RELEASED.value}}
            )
            return {"status": "success", "message": "Job marked as completed and escrow released."}

    await db.bookings.update_one({"id": booking_id}, {"$set": {"status": new_status.value}})
    return {"status": "success", "new_status": new_status.value}
