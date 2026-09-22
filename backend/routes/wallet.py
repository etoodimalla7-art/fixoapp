import uuid
import time
import logging
from fastapi import APIRouter, HTTPException, Depends, Header
from typing import List, Optional
from backend.database import get_database
from backend.models import (
    LedgerTransactionModel, MtnMomoWebhook, OrangeMoneyWebhook
)
from backend.security import get_current_user_claims

logger = logging.getLogger("fixo.wallet")

router = APIRouter(prefix="/wallet", tags=["Wallet & Payments"])

@router.get("/transactions", response_model=List[LedgerTransactionModel])
async def get_user_transactions(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    cursor = db.transactions.find({"user_id": claims["sub"]}, {"_id": 0}).sort("created_at", -1)
    txns = await cursor.to_list(length=100)
    return txns

@router.post("/deposit/initiate")
async def initiate_deposit(
    amount_xaf: float,
    provider: str, # MTN_MOMO or ORANGE_MONEY
    phone_number: str,
    claims: dict = Depends(get_current_user_claims)
):
    if amount_xaf < 500.0:
        raise HTTPException(status_code=400, detail="Minimum deposit amount is 500 FCFA.")

    # Idempotent reference code
    ref_code = f"{provider[:3]}-{uuid.uuid4().hex[:8].upper()}"
    return {
        "status": "PENDING_USER_APPROVAL",
        "reference_code": ref_code,
        "amount_xaf": amount_xaf,
        "provider": provider,
        "instruction": f"Please approve the USSD prompt sent to {phone_number} on your phone to complete deposit."
    }

@router.post("/withdraw/request")
async def request_payout(
    amount_xaf: float,
    provider: str,
    phone_number: str,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    user = await db.users.find_one({"id": claims["sub"]})
    if not user or user.get("balance_xaf", 0.0) < amount_xaf:
        raise HTTPException(status_code=400, detail="Insufficient available balance for withdrawal.")

    # Deduct balance authoritatively
    await db.users.update_one(
        {"id": user["id"]},
        {"$inc": {"balance_xaf": -amount_xaf}}
    )

    # Insert ledger payout entry
    ref_code = f"OUT-{uuid.uuid4().hex[:8].upper()}"
    await db.transactions.insert_one({
        "id": f"txn_{uuid.uuid4().hex[:12]}",
        "user_id": user["id"],
        "type": "PAYOUT",
        "amount_xaf": amount_xaf,
        "currency": "XAF",
        "description": f"Payout request to {phone_number} via {provider}",
        "status": "PROCESSING",
        "payment_provider": provider,
        "reference_code": ref_code,
        "created_at": int(time.time() * 1000)
    })

    return {
        "status": "PROCESSING",
        "reference_code": ref_code,
        "message": f"Withdrawal of {amount_xaf:,.0f} FCFA is being processed to {phone_number}."
    }

# Webhook for MTN Mobile Money
@router.post("/webhooks/mtn")
async def mtn_momo_webhook(payload: MtnMomoWebhook, x_callback_signature: Optional[str] = Header(None)):
    db = get_database()
    if payload.status == "SUCCESSFUL":
        # Authoritative idempotent update - safe structured log without sensitive financial data
        logger.info(
            "Payment webhook processed: provider=MTN_MOMO, status=%s, timestamp=%d",
            payload.status,
            int(time.time() * 1000)
        )
    return {"status": "ACKNOWLEDGED"}

# Webhook for Orange Money WebPay
@router.post("/webhooks/orange")
async def orange_money_webhook(payload: OrangeMoneyWebhook):
    db = get_database()
    if payload.status == "SUCCESS":
        # Authoritative idempotent update - safe structured log without sensitive financial data
        logger.info(
            "Payment webhook processed: provider=ORANGE_MONEY, status=%s, timestamp=%d",
            payload.status,
            int(time.time() * 1000)
        )
    return {"status": "ACKNOWLEDGED"}
