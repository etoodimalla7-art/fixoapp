import os
import time
import uuid
import secrets
import hashlib
import re
from typing import Optional, Dict, Any
from fastapi import APIRouter, HTTPException, status, Depends
import httpx

from backend.database import get_database
from backend.models import (
    RegisterRequest, LoginRequest, RefreshTokenRequest, GoogleAuthRequest,
    TokenResponse, UserModel, UserRole, VerificationStatus,
    RequestOtpRequest, VerifyOtpRequest, ChangePhoneRequest, VerifyPhoneChangeRequest,
    ChangeUsernameRequest, PasswordResetRequest, PasswordResetConfirmRequest,
    LogoutRequest
)
from backend.security import (
    get_password_hash, verify_password, create_access_token, create_refresh_token,
    decode_token, get_current_user_claims, hash_token, REFRESH_TOKEN_EXPIRE_SECONDS
)

router = APIRouter(prefix="/auth", tags=["Authentication & Identity"])

GOOGLE_CLIENT_ID = os.getenv("GOOGLE_CLIENT_ID", "")
ENVIRONMENT = os.getenv("ENVIRONMENT", "development").lower()

RESERVED_USERNAMES = {
    "admin", "administrator", "root", "system", "fixo", "support",
    "help", "security", "official", "moderator", "billing", "api"
}

USERNAME_REGEX = re.compile(r"^[a-zA-Z0-9_.-]{3,30}$")
PHONE_REGEX = re.compile(r"^\+?[0-9]{8,15}$")

def validate_username_format(username: str) -> str:
    username = username.strip()
    if not USERNAME_REGEX.match(username):
        raise HTTPException(
            status_code=400,
            detail="Username must be 3-30 characters long and contain only letters, numbers, underscores, dots, or hyphens."
        )
    if username.lower() in RESERVED_USERNAMES:
        raise HTTPException(status_code=400, detail="This username is reserved and cannot be claimed.")
    return username

def validate_password_strength(password: str) -> None:
    if len(password) < 8:
        raise HTTPException(status_code=400, detail="Password must be at least 8 characters long.")
    if not any(c.isdigit() for c in password):
        raise HTTPException(status_code=400, detail="Password must contain at least one numerical digit.")
    if not any(c.isalpha() for c in password):
        raise HTTPException(status_code=400, detail="Password must contain at least one alphabetic letter.")

async def create_and_store_session(db, user_id: str, refresh_token: str, device_id: Optional[str] = "unknown") -> str:
    session_id = f"ses_{uuid.uuid4().hex[:16]}"
    now = int(time.time() * 1000)
    expires_at = now + (REFRESH_TOKEN_EXPIRE_SECONDS * 1000)
    tok_hash = hash_token(refresh_token)

    session_doc = {
        "id": session_id,
        "user_id": user_id,
        "token_hash": tok_hash,
        "device_id": device_id or "unknown",
        "created_at": now,
        "expires_at": expires_at,
        "last_used_at": now,
        "is_revoked": False,
        "revoked_at": None,
        "revocation_reason": None
    }
    await db.sessions.insert_one(session_doc)
    return session_id

@router.post("/register", response_model=TokenResponse)
async def register(req: RegisterRequest):
    validate_password_strength(req.password)
    db = get_database()

    normalized_email = req.email.strip().lower()
    existing_user = await db.users.find_one({"email": normalized_email})
    if existing_user:
        raise HTTPException(status_code=400, detail="Email is already registered.")

    username_clean = None
    username_lower = None
    if req.username:
        username_clean = validate_username_format(req.username)
        username_lower = username_clean.lower()
        existing_username = await db.users.find_one({"username_lower": username_lower})
        if existing_username:
            raise HTTPException(status_code=400, detail="Username is already taken.")

    user_id = f"usr_{uuid.uuid4().hex[:12]}"
    pwd_hash = get_password_hash(req.password)
    now_ms = int(time.time() * 1000)

    user_doc = {
        "id": user_id,
        "role": req.role.value,
        "name": req.name.strip(),
        "email": normalized_email,
        "phone": req.phone.strip(),
        "username": username_clean,
        "username_lower": username_lower,
        "password_hash": pwd_hash,
        "current_verified_phone": None,
        "pending_phone": None,
        "phone_verification_status": "UNVERIFIED",
        "is_phone_verified": False,
        "is_email_verified": False,
        "avatar_url": "",
        "rating": 5.0,
        "balance_xaf": 0.0,
        "escrow_locked_xaf": 0.0,
        "fixo_points": 0,
        "verification_status": VerificationStatus.PENDING.value if req.role == UserRole.WORKER else VerificationStatus.UNVERIFIED.value,
        "created_at": now_ms
    }
    await db.users.insert_one(user_doc)

    if req.role == UserRole.WORKER:
        worker_id = f"wrk_{uuid.uuid4().hex[:12]}"
        worker_doc = {
            "id": worker_id,
            "user_id": user_id,
            "name": req.name.strip(),
            "category": req.category.value if req.category else "PLUMBING",
            "hourly_rate_xaf": 15000.0,
            "emergency_callout_available": True,
            "bio": "Certified craftsperson on FIXO",
            "skills": [],
            "certifications": [],
            "completed_jobs": 0,
            "rating": 5.0,
            "review_count": 0,
            "working_days": ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
            "slot_intervals": ["08:00 - 10:00", "10:30 - 12:30", "13:30 - 15:30", "16:00 - 18:00"],
            "avatar_url": "",
            "location_city": "Douala",
            "location_distance_km": 1.5,
            "background_verified": False,
            "phone": req.phone.strip()
        }
        await db.workers.insert_one(worker_doc)

    refresh_tok, _ = create_refresh_token(user_id)
    session_id = await create_and_store_session(db, user_id, refresh_tok)
    access_tok = create_access_token(user_id, req.role.value, session_id=session_id)

    return TokenResponse(
        access_token=access_tok,
        refresh_token=refresh_tok,
        user_id=user_id,
        role=req.role,
        name=req.name.strip(),
        email=normalized_email,
        username=username_clean,
        is_phone_verified=False,
        is_email_verified=False
    )

@router.post("/login", response_model=TokenResponse)
async def login(req: LoginRequest):
    db = get_database()
    normalized_email = req.email.strip().lower()
    user = await db.users.find_one({"email": normalized_email})

    if not user or not verify_password(req.password, user.get("password_hash", "")):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email or password."
        )

    # Check if account is suspended
    if user.get("is_suspended", False):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Account has been suspended. Please contact FIXO governance support."
        )

    refresh_tok, _ = create_refresh_token(user["id"])
    session_id = await create_and_store_session(db, user["id"], refresh_tok)
    access_tok = create_access_token(user["id"], user["role"], session_id=session_id)

    return TokenResponse(
        access_token=access_tok,
        refresh_token=refresh_tok,
        user_id=user["id"],
        role=UserRole(user["role"]),
        name=user["name"],
        email=user["email"],
        username=user.get("username"),
        is_phone_verified=user.get("is_phone_verified", False),
        is_email_verified=user.get("is_email_verified", False)
    )

@router.post("/refresh", response_model=TokenResponse)
async def refresh_token(req: RefreshTokenRequest):
    claims = decode_token(req.refresh_token)
    if claims.get("type") != "refresh":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid token type for refresh."
        )

    db = get_database()
    tok_hash = hash_token(req.refresh_token)
    session = await db.sessions.find_one({"token_hash": tok_hash})

    now_ms = int(time.time() * 1000)

    # Detect token reuse attack: if token was already revoked, revoke ALL sessions for this user
    if not session or session.get("is_revoked", False):
        if session and session.get("is_revoked", False):
            # Token reuse detected! Invalidate all active sessions of this user
            await db.sessions.update_many(
                {"user_id": claims["sub"]},
                {"$set": {"is_revoked": True, "revoked_at": now_ms, "revocation_reason": "REUSE_ATTACK_DETECTED"}}
            )
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Refresh token is revoked or invalid. Please sign in again."
        )

    if session.get("expires_at", 0) < now_ms:
        await db.sessions.update_one(
            {"id": session["id"]},
            {"$set": {"is_revoked": True, "revoked_at": now_ms, "revocation_reason": "EXPIRED"}}
        )
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Session has expired. Please sign in again."
        )

    user = await db.users.find_one({"id": claims["sub"]})
    if not user or user.get("is_suspended", False):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="User account inactive or not found.")

    # Invalidate previous refresh token (Rotation)
    await db.sessions.update_one(
        {"id": session["id"]},
        {"$set": {"is_revoked": True, "revoked_at": now_ms, "revocation_reason": "ROTATED"}}
    )

    # Issue rotated tokens
    new_refresh_tok, _ = create_refresh_token(user["id"])
    new_session_id = await create_and_store_session(db, user["id"], new_refresh_tok, device_id=session.get("device_id"))
    new_access_tok = create_access_token(user["id"], user["role"], session_id=new_session_id)

    return TokenResponse(
        access_token=new_access_tok,
        refresh_token=new_refresh_tok,
        user_id=user["id"],
        role=UserRole(user["role"]),
        name=user["name"],
        email=user["email"],
        username=user.get("username"),
        is_phone_verified=user.get("is_phone_verified", False),
        is_email_verified=user.get("is_email_verified", False)
    )

@router.post("/logout")
async def logout(req: LogoutRequest, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    now_ms = int(time.time() * 1000)

    if req.refresh_token:
        tok_hash = hash_token(req.refresh_token)
        await db.sessions.update_one(
            {"token_hash": tok_hash, "user_id": claims["sub"]},
            {"$set": {"is_revoked": True, "revoked_at": now_ms, "revocation_reason": "USER_LOGOUT"}}
        )
    elif "sid" in claims:
        await db.sessions.update_one(
            {"id": claims["sid"], "user_id": claims["sub"]},
            {"$set": {"is_revoked": True, "revoked_at": now_ms, "revocation_reason": "USER_LOGOUT"}}
        )

    return {"status": "success", "message": "Successfully logged out."}

@router.post("/logout-all")
async def logout_all_devices(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    now_ms = int(time.time() * 1000)
    res = await db.sessions.update_many(
        {"user_id": claims["sub"], "is_revoked": False},
        {"$set": {"is_revoked": True, "revoked_at": now_ms, "revocation_reason": "LOGOUT_ALL_DEVICES"}}
    )
    return {"status": "success", "message": f"Terminated {res.modified_count} active session(s)."}

@router.post("/google", response_model=TokenResponse)
async def google_auth(req: GoogleAuthRequest):
    """
    Cryptographically verifies Google identity token without synthetic fallbacks.
    Fails securely if Google OAuth client ID is unconfigured or token verification fails.
    """
    if not GOOGLE_CLIENT_ID:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="BLOCKED BY EXTERNAL CONFIGURATION: GOOGLE_CLIENT_ID is not configured in backend environment."
        )

    # Perform authoritative Google verification via Google's tokeninfo API
    try:
        async with httpx.AsyncClient(timeout=10.0) as client:
            resp = await client.get(
                "https://oauth2.googleapis.com/tokeninfo",
                params={"id_token": req.id_token}
            )
            if resp.status_code != 200:
                raise HTTPException(
                    status_code=status.HTTP_401_UNAUTHORIZED,
                    detail="Google ID token validation failed with provider."
                )
            token_info = resp.json()
    except httpx.RequestError as e:
        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail=f"Network failure connecting to Google identity services: {str(e)}"
        )

    # Validate Audience, Issuer, and Email Verification
    aud = token_info.get("aud")
    if aud != GOOGLE_CLIENT_ID:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Google token audience mismatch."
        )

    iss = token_info.get("iss")
    if iss not in ["accounts.google.com", "https://accounts.google.com"]:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid Google token issuer."
        )

    email_verified = token_info.get("email_verified")
    if str(email_verified).lower() != "true":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Google email is not verified by Google."
        )

    google_email = token_info.get("email", "").lower().strip()
    google_name = token_info.get("name", "Google User").strip()
    google_picture = token_info.get("picture", "")

    if not google_email:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No email present in verified Google identity credential."
        )

    db = get_database()
    user = await db.users.find_one({"email": google_email})
    now_ms = int(time.time() * 1000)

    if not user:
        user_id = f"usr_goog_{uuid.uuid4().hex[:10]}"
        user_doc = {
            "id": user_id,
            "role": req.role.value if req.role else UserRole.CUSTOMER.value,
            "name": google_name,
            "email": google_email,
            "phone": "",
            "password_hash": "",
            "username": None,
            "username_lower": None,
            "current_verified_phone": None,
            "pending_phone": None,
            "phone_verification_status": "UNVERIFIED",
            "is_phone_verified": False,
            "is_email_verified": True,
            "avatar_url": google_picture,
            "rating": 5.0,
            "balance_xaf": 0.0,
            "escrow_locked_xaf": 0.0,
            "fixo_points": 50,
            "verification_status": VerificationStatus.UNVERIFIED.value,
            "created_at": now_ms
        }
        await db.users.insert_one(user_doc)
        user = user_doc
    else:
        # Update verified email flag if signing in with Google
        await db.users.update_one({"id": user["id"]}, {"$set": {"is_email_verified": True}})

    refresh_tok, _ = create_refresh_token(user["id"])
    session_id = await create_and_store_session(db, user["id"], refresh_tok)
    access_tok = create_access_token(user["id"], user["role"], session_id=session_id)

    return TokenResponse(
        access_token=access_tok,
        refresh_token=refresh_tok,
        user_id=user["id"],
        role=UserRole(user["role"]),
        name=user["name"],
        email=user["email"],
        username=user.get("username"),
        is_phone_verified=user.get("is_phone_verified", False),
        is_email_verified=True
    )

# --- OTP & Phone Verification Lifecycle ---

@router.post("/otp/request")
async def request_otp(req: RequestOtpRequest):
    clean_phone = req.phone.strip()
    db = get_database()
    now_ms = int(time.time() * 1000)

    # 1. Rate-limiting / resend cooldown: 60 seconds
    recent_otp = await db.otps.find_one({
        "phone": clean_phone,
        "purpose": req.purpose,
        "created_at": {"$gt": now_ms - 60000}
    })
    if recent_otp:
        raise HTTPException(
            status_code=status.HTTP_429_TOO_MANY_REQUESTS,
            detail="Rate limit exceeded: Please wait 60 seconds before requesting a new verification code."
        )

    # Invalidate previous pending OTPs for this phone + purpose
    await db.otps.update_many(
        {"phone": clean_phone, "purpose": req.purpose, "is_used": False},
        {"$set": {"is_used": True, "invalidated": True}}
    )

    # Generate secure 6-digit OTP
    otp_code = "".join(secrets.choice("0123456789") for _ in range(6))
    otp_hash = hashlib.sha256(otp_code.encode("utf-8")).hexdigest()

    otp_doc = {
        "phone": clean_phone,
        "purpose": req.purpose,
        "otp_hash": otp_hash,
        "created_at": now_ms,
        "expires_at": now_ms + (5 * 60 * 1000),  # 5 minutes expiry
        "attempts": 0,
        "max_attempts": 3,
        "is_used": False
    }
    await db.otps.insert_one(otp_doc)

    # In development/test mode, expose debug token only if explicitly in local testing
    debug_code = otp_code if ENVIRONMENT in ["test", "development"] else None

    return {
        "status": "success",
        "message": f"Verification code dispatched to {clean_phone[:4]}***{clean_phone[-2:]}.",
        "expires_in_seconds": 300,
        "debug_code": debug_code
    }

@router.post("/otp/verify")
async def verify_otp(req: VerifyOtpRequest):
    clean_phone = req.phone.strip()
    db = get_database()
    now_ms = int(time.time() * 1000)

    otp_doc = await db.otps.find_one({
        "phone": clean_phone,
        "purpose": req.purpose,
        "is_used": False
    }, sort=[("created_at", -1)])

    if not otp_doc:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="No active verification code found for this phone number."
        )

    if otp_doc.get("expires_at", 0) < now_ms:
        await db.otps.update_one({"_id": otp_doc["_id"]}, {"$set": {"is_used": True}})
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Verification code has expired. Please request a new code."
        )

    if otp_doc.get("attempts", 0) >= otp_doc.get("max_attempts", 3):
        await db.otps.update_one({"_id": otp_doc["_id"]}, {"$set": {"is_used": True}})
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Maximum verification attempts exceeded. Please request a new code."
        )

    submitted_hash = hashlib.sha256(req.otp.strip().encode("utf-8")).hexdigest()
    if submitted_hash != otp_doc["otp_hash"]:
        await db.otps.update_one({"_id": otp_doc["_id"]}, {"$inc": {"attempts": 1}})
        remaining = otp_doc.get("max_attempts", 3) - (otp_doc.get("attempts", 0) + 1)
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Incorrect verification code. {remaining} attempt(s) remaining."
        )

    # Code matches: mark single-use consumed
    await db.otps.update_one({"_id": otp_doc["_id"]}, {"$set": {"is_used": True, "verified_at": now_ms}})

    # Update user phone verification if purpose is PHONE_VERIFICATION
    if req.purpose == "PHONE_VERIFICATION":
        await db.users.update_many(
            {"phone": clean_phone},
            {"$set": {
                "is_phone_verified": True,
                "phone_verification_status": "VERIFIED",
                "current_verified_phone": clean_phone
            }}
        )

    return {"status": "success", "message": "Phone number successfully verified."}

# --- Phone Number Change Architecture ---

@router.post("/phone/change-request")
async def request_phone_change(
    req: ChangePhoneRequest,
    claims: dict = Depends(get_current_user_claims)
):
    clean_phone = req.new_phone.strip()
    if not PHONE_REGEX.match(clean_phone):
        raise HTTPException(status_code=400, detail="Invalid phone number format.")

    db = get_database()
    user = await db.users.find_one({"id": claims["sub"]})
    if not user:
        raise HTTPException(status_code=404, detail="User not found.")

    if user.get("phone") == clean_phone and user.get("is_phone_verified", False):
        raise HTTPException(status_code=400, detail="New phone is identical to current verified phone.")

    now_ms = int(time.time() * 1000)

    # Set pending phone state without changing verified phone
    await db.users.update_one(
        {"id": user["id"]},
        {"$set": {
            "pending_phone": clean_phone,
            "phone_verification_status": "PENDING_VERIFICATION"
        }}
    )

    # Dispatch OTP with PHONE_CHANGE purpose
    otp_code = "".join(secrets.choice("0123456789") for _ in range(6))
    otp_hash = hashlib.sha256(otp_code.encode("utf-8")).hexdigest()

    await db.otps.insert_one({
        "phone": clean_phone,
        "user_id": user["id"],
        "purpose": "PHONE_CHANGE",
        "otp_hash": otp_hash,
        "created_at": now_ms,
        "expires_at": now_ms + (5 * 60 * 1000),
        "attempts": 0,
        "max_attempts": 3,
        "is_used": False
    })

    debug_code = otp_code if ENVIRONMENT in ["test", "development"] else None

    return {
        "status": "success",
        "message": f"Verification code dispatched to {clean_phone}.",
        "debug_code": debug_code
    }

@router.post("/phone/change-verify")
async def verify_phone_change(
    req: VerifyPhoneChangeRequest,
    claims: dict = Depends(get_current_user_claims)
):
    clean_phone = req.new_phone.strip()
    db = get_database()
    now_ms = int(time.time() * 1000)

    user = await db.users.find_one({"id": claims["sub"]})
    if not user or user.get("pending_phone") != clean_phone:
        raise HTTPException(status_code=400, detail="No pending phone change request for this number.")

    otp_doc = await db.otps.find_one({
        "phone": clean_phone,
        "purpose": "PHONE_CHANGE",
        "is_used": False
    }, sort=[("created_at", -1)])

    if not otp_doc or otp_doc.get("expires_at", 0) < now_ms:
        raise HTTPException(status_code=400, detail="Verification code has expired.")

    if otp_doc.get("attempts", 0) >= otp_doc.get("max_attempts", 3):
        await db.otps.update_one({"_id": otp_doc["_id"]}, {"$set": {"is_used": True}})
        raise HTTPException(
            status_code=400,
            detail="Maximum verification attempts exceeded. Please request a new code."
        )

    submitted_hash = hashlib.sha256(req.otp.strip().encode("utf-8")).hexdigest()
    if submitted_hash != otp_doc["otp_hash"]:
        await db.otps.update_one({"_id": otp_doc["_id"]}, {"$inc": {"attempts": 1}})
        raise HTTPException(status_code=400, detail="Invalid verification code.")

    # Mark OTP as used
    await db.otps.update_one({"_id": otp_doc["_id"]}, {"$set": {"is_used": True}})

    # Authoritatively commit the phone number change
    await db.users.update_one(
        {"id": user["id"]},
        {"$set": {
            "phone": clean_phone,
            "current_verified_phone": clean_phone,
            "pending_phone": None,
            "phone_verification_status": "VERIFIED",
            "is_phone_verified": True
        }}
    )

    # Sync worker phone if user is a worker
    if user.get("role") == UserRole.WORKER.value:
        await db.workers.update_one({"user_id": user["id"]}, {"$set": {"phone": clean_phone}})

    return {"status": "success", "message": "Phone number successfully updated and verified."}

# --- Username Security & Case-Insensitive Uniqueness ---

@router.put("/username")
async def update_username(
    req: ChangeUsernameRequest,
    claims: dict = Depends(get_current_user_claims)
):
    clean_username = validate_username_format(req.new_username)
    lower_username = clean_username.lower()

    db = get_database()
    # Check case-insensitive uniqueness excluding the current user
    conflict = await db.users.find_one({
        "username_lower": lower_username,
        "id": {"$ne": claims["sub"]}
    })
    if conflict:
        raise HTTPException(status_code=400, detail="Username is already taken.")

    await db.users.update_one(
        {"id": claims["sub"]},
        {"$set": {
            "username": clean_username,
            "username_lower": lower_username
        }}
    )
    return {"status": "success", "username": clean_username}

# --- Password Reset Architecture ---

@router.post("/password/reset-request")
async def request_password_reset(req: PasswordResetRequest):
    db = get_database()
    normalized_email = req.email.strip().lower()
    user = await db.users.find_one({"email": normalized_email})

    # Avoid account enumeration: always return success message
    if not user:
        return {"status": "success", "message": "If an account exists for this email, password reset instructions have been dispatched."}

    now_ms = int(time.time() * 1000)
    reset_token = secrets.token_urlsafe(32)
    token_hash = hashlib.sha256(reset_token.encode("utf-8")).hexdigest()

    await db.password_resets.insert_one({
        "user_id": user["id"],
        "token_hash": token_hash,
        "created_at": now_ms,
        "expires_at": now_ms + (3600 * 1000),  # 1 hour single-use
        "is_used": False
    })

    debug_token = reset_token if ENVIRONMENT in ["test", "development"] else None

    return {
        "status": "success",
        "message": "If an account exists for this email, password reset instructions have been dispatched.",
        "debug_token": debug_token
    }

@router.post("/password/reset-confirm")
async def confirm_password_reset(req: PasswordResetConfirmRequest):
    validate_password_strength(req.new_password)
    db = get_database()
    now_ms = int(time.time() * 1000)

    token_hash = hashlib.sha256(req.token.strip().encode("utf-8")).hexdigest()
    reset_doc = await db.password_resets.find_one({
        "token_hash": token_hash,
        "is_used": False
    })

    if not reset_doc or reset_doc.get("expires_at", 0) < now_ms:
        raise HTTPException(status_code=400, detail="Password reset link is invalid or has expired.")

    # Mark token as consumed
    await db.password_resets.update_one({"_id": reset_doc["_id"]}, {"$set": {"is_used": True}})

    # Update password hash
    new_pwd_hash = get_password_hash(req.new_password)
    await db.users.update_one({"id": reset_doc["user_id"]}, {"$set": {"password_hash": new_pwd_hash}})

    # Revoke ALL existing active sessions for security
    await db.sessions.update_many(
        {"user_id": reset_doc["user_id"], "is_revoked": False},
        {"$set": {"is_revoked": True, "revoked_at": now_ms, "revocation_reason": "PASSWORD_RESET"}}
    )

    return {"status": "success", "message": "Password successfully reset. All active sessions revoked. Please log in."}

@router.get("/me")
async def get_me(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user = await db.users.find_one({"id": claims["sub"]}, {"password_hash": 0, "_id": 0})
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user
