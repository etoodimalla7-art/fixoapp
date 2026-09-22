import os
import time
from collections import defaultdict
from contextlib import asynccontextmanager
from typing import Dict, List
from fastapi import FastAPI, Request, Response, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.staticfiles import StaticFiles
from starlette.middleware.base import BaseHTTPMiddleware

from backend.database import connect_to_mongo, close_mongo_connection
from backend.routes import (
    auth, workers, bookings, reels, wallet, media, chat,
    notifications, admin, enterprise
)
from backend.routes.media import MEDIA_DIR

ENVIRONMENT = os.getenv("ENVIRONMENT", "development").lower()

# Rate limiting sliding window cache
RATE_LIMIT_BUCKET: Dict[str, List[float]] = defaultdict(list)
# Endpoint -> (limit, window_seconds)
RATE_LIMIT_RULES = {
    "/api/v1/auth/login": (10, 60),          # 10 requests per minute
    "/api/v1/auth/register": (5, 60),        # 5 registrations per minute
    "/api/v1/auth/refresh": (30, 60),        # 30 token refreshes per minute
    "/api/v1/auth/google": (10, 60),         # 10 google sign-ins per minute
    "/api/v1/auth/otp/request": (5, 60),     # 5 OTP requests per minute
    "/api/v1/auth/otp/verify": (10, 60),     # 10 OTP verifications per minute
    "/api/v1/wallet/withdraw/request": (5, 60) # 5 withdrawals per minute
}

class SecurityHeadersMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        # 1. Path-based rate limiting
        path = request.url.path
        client_ip = request.client.host if request.client else "unknown"

        if path in RATE_LIMIT_RULES:
            limit, window_seconds = RATE_LIMIT_RULES[path]
            now = time.time()
            key = f"{client_ip}:{path}"

            # Filter timestamps within window
            timestamps = [t for t in RATE_LIMIT_BUCKET[key] if now - t < window_seconds]
            if len(timestamps) >= limit:
                return JSONResponse(
                    status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                    content={"detail": "Too many requests. Please slow down and try again later."}
                )
            timestamps.append(now)
            RATE_LIMIT_BUCKET[key] = timestamps

        # 2. Process request
        response = await call_next(request)

        # 3. Add Authoritative Security Headers
        response.headers["X-Content-Type-Options"] = "nosniff"
        response.headers["X-Frame-Options"] = "DENY"
        response.headers["X-XSS-Protection"] = "1; mode=block"
        response.headers["Referrer-Policy"] = "strict-origin-when-cross-origin"
        response.headers["Cache-Control"] = "no-store, no-cache, must-revalidate"
        if ENVIRONMENT == "production":
            response.headers["Strict-Transport-Security"] = "max-age=31536000; includeSubDomains; preload"

        return response

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    try:
        await connect_to_mongo()
    except Exception as e:
        # Avoid crashing when MongoDB container is starting asynchronously
        pass
    yield
    # Shutdown
    await close_mongo_connection()

app = FastAPI(
    title="FIXO Authoritative Platform API",
    description="Hardened API for skilled trades, reels, escrow bookings, and African mobile payments",
    version="2.1.0",
    lifespan=lifespan
)

# Custom generic exception handler to avoid leaking internal tracebacks
@app.exception_handler(Exception)
async def generic_exception_handler(request: Request, exc: Exception):
    # For HTTPExceptions, let FastAPI handle them normally
    if isinstance(exc, HTTPException):
        return JSONResponse(
            status_code=exc.status_code,
            content={"detail": exc.detail}
        )
    # Generic unhandled exceptions: return sanitized error
    return JSONResponse(
        status_code=500,
        content={"detail": "An internal server error occurred. Please try again later."}
    )

# Security Headers & Rate Limiting Middleware
app.add_middleware(SecurityHeadersMiddleware)

# Allowed CORS Origins: specific origins or configured via ALLOWED_ORIGINS env
allowed_origins_env = os.getenv("ALLOWED_ORIGINS", "")
if allowed_origins_env:
    origins = [o.strip() for o in allowed_origins_env.split(",") if o.strip()]
else:
    origins = [
        "https://fixo.cm",
        "https://api.fixo.cm",
        "http://localhost",
        "http://localhost:3000",
        "http://localhost:8080"
    ]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allow_headers=["Authorization", "Content-Type", "Accept", "X-Requested-With"],
)

# Include Routers under /api/v1
app.include_router(auth.router, prefix="/api/v1")
app.include_router(workers.router, prefix="/api/v1")
app.include_router(bookings.router, prefix="/api/v1")
app.include_router(reels.router, prefix="/api/v1")
app.include_router(wallet.router, prefix="/api/v1")
app.include_router(media.router, prefix="/api/v1")
app.include_router(chat.router, prefix="/api/v1")
app.include_router(notifications.router, prefix="/api/v1")
app.include_router(admin.router, prefix="/api/v1")
app.include_router(enterprise.router, prefix="/api/v1")

# Static Public Media Delivery
if os.path.exists(MEDIA_DIR):
    app.mount("/media", StaticFiles(directory=MEDIA_DIR), name="media")

@app.get("/health", tags=["Health"])
async def health_check():
    return {
        "status": "healthy",
        "service": "fixo-backend",
        "version": "2.1.0",
        "environment": ENVIRONMENT,
        "security_hardened": True,
        "currency": "XAF (Central African Franc)"
    }
