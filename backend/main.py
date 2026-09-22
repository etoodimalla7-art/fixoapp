import os
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from backend.database import connect_to_mongo, close_mongo_connection
from backend.routes import auth, workers, bookings, reels, wallet, media, chat, notifications, admin, enterprise
from backend.routes.media import MEDIA_DIR

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    try:
        await connect_to_mongo()
    except Exception as e:
        print(f"Notice: MongoDB connection deferred: {e}")
    yield
    # Shutdown
    await close_mongo_connection()

app = FastAPI(
    title="FIXO Authoritative Platform API",
    description="Production-grade API for skilled trades, reels, escrow bookings, and African mobile payments",
    version="2.0.0",
    lifespan=lifespan
)

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include Routers
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

# Static Media Delivery for uploaded videos and thumbnails
if os.path.exists(MEDIA_DIR):
    app.mount("/media", StaticFiles(directory=MEDIA_DIR), name="media")

@app.get("/health", tags=["Health"])
async def health_check():
    return {
        "status": "healthy",
        "service": "fixo-backend",
        "version": "2.0.0",
        "currency": "XAF (Central African Franc)"
    }
