import uuid
import time
from fastapi import APIRouter, HTTPException, Depends
from typing import List
from backend.database import get_database
from backend.models import (
    CreateBookingRequest, BookingModel, JobStatus, EscrowStatus,
    LedgerTransactionModel, PaymentMethod, CancelBookingRequest,
    ReviewBookingRequest, CreateDisputeRequest, DisputeModel,
    DisputeStatus, UpdateLocationRequest, WorkerLocationModel
)
from backend.security import get_current_user_claims

router = APIRouter(prefix="/bookings", tags=["Bookings & Escrow"])

PLATFORM_COMMISSION_PERCENT = 0.10 # 10% platform fee

@router.get("", response_model=List[BookingModel])
async def list_bookings(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    role = claims.get("role")

    query = {}
    if role == "CUSTOMER":
        query["customer_id"] = user_id
    elif role == "WORKER":
        worker = await db.workers.find_one({"user_id": user_id})
        if worker:
            query["worker_id"] = worker["id"]
        else:
            return []
    elif role != "ADMIN":
        # Check both customer_id and worker_id
        worker = await db.workers.find_one({"user_id": user_id})
        if worker:
            query["$or"] = [{"customer_id": user_id}, {"worker_id": worker["id"]}]
        else:
            query["customer_id"] = user_id

    cursor = db.bookings.find(query, {"_id": 0}).sort("created_at", -1)
    bookings = await cursor.to_list(length=100)
    return bookings

@router.get("/{booking_id}", response_model=BookingModel)
async def get_booking(booking_id: str, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    booking = await db.bookings.find_one({"id": booking_id}, {"_id": 0})
    if not booking:
        raise HTTPException(status_code=404, detail="Booking not found")

    user_id = claims["sub"]
    role = claims.get("role")
    worker = await db.workers.find_one({"user_id": user_id})
    worker_id = worker["id"] if worker else None

    # Object-level authorization check
    if role != "ADMIN" and booking["customer_id"] != user_id and booking["worker_id"] != worker_id:
        raise HTTPException(status_code=403, detail="Unauthorized to view this booking")

    return BookingModel(**booking)

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

@router.post("/{booking_id}/release")
async def release_escrow(
    booking_id: str,
    rating: float = 5.0,
    review: str = "",
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    booking = await db.bookings.find_one({"id": booking_id})
    if not booking:
        raise HTTPException(status_code=404, detail="Booking not found")

    user_id = claims["sub"]
    role = claims.get("role")
    if booking["customer_id"] != user_id and role != "ADMIN":
        raise HTTPException(status_code=403, detail="Only customer or admin can approve release")

    if booking["escrow_status"] != EscrowStatus.HOLDING.value:
        raise HTTPException(status_code=400, detail="Escrow is not in HOLDING state")

    total_price = booking["price_amount_xaf"]
    commission = total_price * PLATFORM_COMMISSION_PERCENT
    worker_payout = total_price - commission

    # Unlock customer escrow & award points
    await db.users.update_one(
        {"id": booking["customer_id"]},
        {"$inc": {"escrow_locked_xaf": -total_price, "fixo_points": 50}}
    )

    worker = await db.workers.find_one({"id": booking["worker_id"]})
    if worker:
        await db.users.update_one(
            {"id": worker["user_id"]},
            {"$inc": {"balance_xaf": worker_payout}}
        )
        await db.workers.update_one(
            {"id": worker["id"]},
            {"$inc": {"completed_jobs": 1}}
        )

    await db.transactions.insert_one({
        "id": f"txn_{uuid.uuid4().hex[:12]}",
        "user_id": worker["user_id"] if worker else "",
        "type": "PAYOUT",
        "amount_xaf": worker_payout,
        "currency": "XAF",
        "description": f"Escrow release for job #{booking_id[:8]}",
        "status": "COMPLETED",
        "payment_provider": "FIXO_ESCROW",
        "reference_code": f"PAY-{uuid.uuid4().hex[:8].upper()}",
        "created_at": int(time.time() * 1000)
    })

    update_payload = {
        "status": JobStatus.COMPLETED.value,
        "escrow_status": EscrowStatus.RELEASED.value
    }
    if rating > 0:
        update_payload["customer_rating"] = rating
        update_payload["customer_review_text"] = review

    await db.bookings.update_one({"id": booking_id}, {"$set": update_payload})
    return {"status": "success", "message": "Escrow released successfully", "payout_xaf": worker_payout}

@router.post("/{booking_id}/cancel")
async def cancel_booking(
    booking_id: str,
    req: CancelBookingRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    booking = await db.bookings.find_one({"id": booking_id})
    if not booking:
        raise HTTPException(status_code=404, detail="Booking not found")

    user_id = claims["sub"]
    role = claims.get("role")
    worker = await db.workers.find_one({"user_id": user_id})
    worker_id = worker["id"] if worker else None

    is_customer = (booking["customer_id"] == user_id)
    is_worker = (booking["worker_id"] == worker_id)

    if not is_customer and not is_worker and role != "ADMIN":
        raise HTTPException(status_code=403, detail="Unauthorized to cancel this booking")

    current_status = booking["status"]
    if current_status in [JobStatus.COMPLETED.value, JobStatus.CANCELLED.value, JobStatus.DISPUTED.value]:
        raise HTTPException(status_code=400, detail=f"Cannot cancel a job in state: {current_status}")

    # Determine Refund policy
    # If customer cancels before artisan is EN_ROUTE: 100% refund
    # If artisan cancels: 100% refund to customer
    # If customer cancels after artisan is EN_ROUTE: 2,000 XAF callout fee to artisan, remainder refunded
    price_xaf = booking["price_amount_xaf"]
    refund_amount_xaf = price_xaf
    callout_fee_xaf = 0.0

    if is_customer and current_status in [JobStatus.EN_ROUTE.value, JobStatus.IN_PROGRESS.value]:
        callout_fee_xaf = min(2000.0, price_xaf * 0.2)
        refund_amount_xaf = price_xaf - callout_fee_xaf

    # Process refund from escrow holding
    if booking["escrow_status"] == EscrowStatus.HOLDING.value:
        # Deduct escrow lock and return refund to customer balance
        await db.users.update_one(
            {"id": booking["customer_id"]},
            {"$inc": {"escrow_locked_xaf": -price_xaf, "balance_xaf": refund_amount_xaf}}
        )

        # If callout compensation to worker
        if callout_fee_xaf > 0:
            target_worker = await db.workers.find_one({"id": booking["worker_id"]})
            if target_worker:
                await db.users.update_one(
                    {"id": target_worker["user_id"]},
                    {"$inc": {"balance_xaf": callout_fee_xaf}}
                )

        # Ledger record
        await db.transactions.insert_one({
            "id": f"txn_{uuid.uuid4().hex[:12]}",
            "user_id": booking["customer_id"],
            "type": "REFUND",
            "amount_xaf": refund_amount_xaf,
            "currency": "XAF",
            "description": f"Refund for cancelled job #{booking_id[:8]}. Reason: {req.reason}",
            "status": "COMPLETED",
            "payment_provider": "FIXO_ESCROW",
            "reference_code": f"REF-{uuid.uuid4().hex[:8].upper()}",
            "created_at": int(time.time() * 1000)
        })

    await db.bookings.update_one(
        {"id": booking_id},
        {"$set": {
            "status": JobStatus.CANCELLED.value,
            "escrow_status": EscrowStatus.REFUNDED.value,
            "cancellation_reason": req.reason,
            "cancelled_by": "CUSTOMER" if is_customer else ("WORKER" if is_worker else "ADMIN")
        }}
    )

    return {
        "status": "success",
        "message": "Booking cancelled and refund processed",
        "refund_amount_xaf": refund_amount_xaf
    }

@router.post("/{booking_id}/review")
async def review_booking(
    booking_id: str,
    req: ReviewBookingRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    booking = await db.bookings.find_one({"id": booking_id})
    if not booking:
        raise HTTPException(status_code=404, detail="Booking not found")

    user_id = claims["sub"]
    if booking["customer_id"] != user_id:
        raise HTTPException(status_code=403, detail="Only customer of the booking can leave a review")

    if booking["status"] != JobStatus.COMPLETED.value:
        raise HTTPException(status_code=400, detail="Reviews can only be submitted after job completion")

    if booking.get("customer_rating", 0.0) > 0.0:
        raise HTTPException(status_code=400, detail="A review has already been submitted for this booking")

    # Update booking
    await db.bookings.update_one(
        {"id": booking_id},
        {"$set": {
            "customer_rating": req.rating,
            "customer_review_text": req.review_text
        }}
    )

    # Insert into reviews collection
    review_doc = {
        "id": f"rev_{uuid.uuid4().hex[:12]}",
        "booking_id": booking_id,
        "worker_id": booking["worker_id"],
        "customer_id": user_id,
        "customer_name": booking["customer_name"],
        "rating": req.rating,
        "review_text": req.review_text,
        "created_at": int(time.time() * 1000)
    }
    await db.reviews.insert_one(review_doc)

    # Recompute worker rating
    cursor = db.reviews.find({"worker_id": booking["worker_id"]})
    all_reviews = await cursor.to_list(length=500)
    avg_rating = sum(r["rating"] for r in all_reviews) / len(all_reviews) if all_reviews else req.rating

    await db.workers.update_one(
        {"id": booking["worker_id"]},
        {"$set": {"rating": round(avg_rating, 2), "review_count": len(all_reviews)}}
    )

    return {"status": "success", "message": "Review submitted successfully"}

@router.post("/{booking_id}/dispute")
async def create_dispute(
    booking_id: str,
    req: CreateDisputeRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    booking = await db.bookings.find_one({"id": booking_id})
    if not booking:
        raise HTTPException(status_code=404, detail="Booking not found")

    user_id = claims["sub"]
    role = claims.get("role")
    worker = await db.workers.find_one({"user_id": user_id})
    worker_id = worker["id"] if worker else None

    is_customer = (booking["customer_id"] == user_id)
    is_worker = (booking["worker_id"] == worker_id)

    if not is_customer and not is_worker:
        raise HTTPException(status_code=403, detail="Unauthorized to file dispute for this booking")

    # Check existing open dispute
    existing_dispute = await db.disputes.find_one({"booking_id": booking_id, "status": "OPEN"})
    if existing_dispute:
        raise HTTPException(status_code=400, detail="An open dispute already exists for this booking")

    dispute_id = f"dsp_{uuid.uuid4().hex[:12]}"
    claimant_role = "CUSTOMER" if is_customer else "WORKER"
    respondent_id = booking["worker_id"] if is_customer else booking["customer_id"]

    dispute_doc = {
        "id": dispute_id,
        "booking_id": booking_id,
        "claimant_id": user_id,
        "claimant_role": claimant_role,
        "respondent_id": respondent_id,
        "reason": req.reason.value,
        "description": req.description,
        "evidence_urls": req.evidence_urls,
        "status": DisputeStatus.OPEN.value,
        "admin_notes": None,
        "resolved_at": None,
        "created_at": int(time.time() * 1000)
    }
    await db.disputes.insert_one(dispute_doc)

    # Lock booking in DISPUTED status
    await db.bookings.update_one(
        {"id": booking_id},
        {"$set": {"status": JobStatus.DISPUTED.value}}
    )

    # Audit log
    await db.audit_logs.insert_one({
        "event": "DISPUTE_CREATED",
        "dispute_id": dispute_id,
        "booking_id": booking_id,
        "actor_id": user_id,
        "timestamp": int(time.time() * 1000)
    })

    return {"status": "success", "dispute_id": dispute_id, "message": "Dispute filed and escalated to arbitration."}

@router.post("/{booking_id}/location")
async def update_worker_location(
    booking_id: str,
    req: UpdateLocationRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    booking = await db.bookings.find_one({"id": booking_id})
    if not booking:
        raise HTTPException(status_code=404, detail="Booking not found")

    user_id = claims["sub"]
    worker = await db.workers.find_one({"user_id": user_id})
    if not worker or worker["id"] != booking["worker_id"]:
        raise HTTPException(status_code=403, detail="Only assigned worker can broadcast location")

    # Authoritative lifecycle check: location streaming ONLY allowed when EN_ROUTE
    if booking["status"] != JobStatus.EN_ROUTE.value:
        raise HTTPException(status_code=400, detail="Location streaming is only permitted while status is EN_ROUTE")

    location_doc = {
        "booking_id": booking_id,
        "worker_id": worker["id"],
        "latitude": req.latitude,
        "longitude": req.longitude,
        "speed_kmh": req.speed_kmh,
        "bearing_degrees": req.bearing_degrees,
        "accuracy_meters": req.accuracy_meters,
        "is_active": True,
        "updated_at": int(time.time() * 1000)
    }
    await db.worker_locations.update_one(
        {"booking_id": booking_id},
        {"$set": location_doc},
        upsert=True
    )
    return {"status": "success"}

@router.get("/{booking_id}/location", response_model=WorkerLocationModel)
async def get_worker_location(booking_id: str, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    booking = await db.bookings.find_one({"id": booking_id})
    if not booking:
        raise HTTPException(status_code=404, detail="Booking not found")

    user_id = claims["sub"]
    role = claims.get("role")
    worker = await db.workers.find_one({"user_id": user_id})
    worker_id = worker["id"] if worker else None

    # Object-level authorization check: ONLY customer or worker of this booking
    if role != "ADMIN" and booking["customer_id"] != user_id and booking["worker_id"] != worker_id:
        raise HTTPException(status_code=403, detail="Unauthorized to track worker for this booking")

    # If job is no longer EN_ROUTE, tracking is inactive
    if booking["status"] != JobStatus.EN_ROUTE.value:
        return WorkerLocationModel(
            booking_id=booking_id,
            worker_id=booking["worker_id"],
            latitude=0.0,
            longitude=0.0,
            is_active=False
        )

    loc = await db.worker_locations.find_one({"booking_id": booking_id}, {"_id": 0})
    if not loc:
        return WorkerLocationModel(
            booking_id=booking_id,
            worker_id=booking["worker_id"],
            latitude=0.0,
            longitude=0.0,
            is_active=True
        )

    return WorkerLocationModel(**loc)

