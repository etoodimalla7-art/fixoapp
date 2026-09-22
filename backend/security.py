import os
import time
import hashlib
import uuid
from typing import Optional, Dict, Any
from passlib.context import CryptContext
import jwt
from fastapi import HTTPException, Security, status
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer

ENVIRONMENT = os.getenv("ENVIRONMENT", "development").lower()
JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY", "")

# Enforce fail-fast security policy: No hard-coded fallback JWT secrets allowed
if not JWT_SECRET_KEY or len(JWT_SECRET_KEY) < 32:
    raise RuntimeError(
        "CRITICAL SECURITY FAILURE: JWT_SECRET_KEY environment variable is mandatory "
        "and must be at least 32 characters in length."
    )

ALGORITHM = "HS256"
JWT_ISSUER = "fixo-auth"
ACCESS_TOKEN_EXPIRE_SECONDS = 900  # 15 minutes short-lived
REFRESH_TOKEN_EXPIRE_SECONDS = 3600 * 24 * 30  # 30 days

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
security_bearer = HTTPBearer(auto_error=True)

def verify_password(plain_password: str, hashed_password: str) -> bool:
    if not plain_password or not hashed_password:
        return False
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password: str) -> str:
    if len(password) < 8:
        raise ValueError("Password must be at least 8 characters.")
    return pwd_context.hash(password)

def hash_token(token: str) -> str:
    """Produces a secure SHA-256 digest of a token for database session storage."""
    return hashlib.sha256(token.encode("utf-8")).hexdigest()

def create_access_token(user_id: str, role: str, session_id: Optional[str] = None) -> str:
    now = int(time.time())
    payload: Dict[str, Any] = {
        "iss": JWT_ISSUER,
        "sub": user_id,
        "role": role,
        "type": "access",
        "jti": f"atk_{uuid.uuid4().hex[:16]}",
        "iat": now,
        "exp": now + ACCESS_TOKEN_EXPIRE_SECONDS,
    }
    if session_id:
        payload["sid"] = session_id
    return jwt.encode(payload, JWT_SECRET_KEY, algorithm=ALGORITHM)

def create_refresh_token(user_id: str, session_id: Optional[str] = None) -> tuple[str, str]:
    """
    Returns (refresh_token, jti_id).
    Tokens include a cryptographically unique identifier for session binding and rotation.
    """
    now = int(time.time())
    token_id = f"rtk_{uuid.uuid4().hex[:16]}"
    payload: Dict[str, Any] = {
        "iss": JWT_ISSUER,
        "sub": user_id,
        "type": "refresh",
        "jti": token_id,
        "iat": now,
        "exp": now + REFRESH_TOKEN_EXPIRE_SECONDS,
    }
    if session_id:
        payload["sid"] = session_id
    encoded_token = jwt.encode(payload, JWT_SECRET_KEY, algorithm=ALGORITHM)
    return encoded_token, token_id

def decode_token(token: str) -> dict:
    try:
        payload = jwt.decode(
            token,
            JWT_SECRET_KEY,
            algorithms=[ALGORITHM],
            issuer=JWT_ISSUER,
            options={"require": ["exp", "iss", "sub", "type", "jti"]}
        )
        return payload
    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authentication token has expired. Please log in again."
        )
    except jwt.InvalidIssuerError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token issuer is invalid."
        )
    except jwt.InvalidTokenError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authentication token."
        )

async def get_current_user_claims(credentials: HTTPAuthorizationCredentials = Security(security_bearer)) -> dict:
    token = credentials.credentials
    claims = decode_token(token)
    if claims.get("type") != "access":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Access token required for this endpoint."
        )
    return claims
