from enum import Enum
from typing import Optional, List
from pydantic import BaseModel, Field, EmailStr
import time

class UserRole(str, Enum):
    CUSTOMER = "CUSTOMER"
    WORKER = "WORKER"
    ENTERPRISE = "ENTERPRISE"
    ADMIN = "ADMIN"

class VerificationStatus(str, Enum):
    UNVERIFIED = "UNVERIFIED"
    PENDING = "PENDING"
    VERIFIED_PRO = "VERIFIED_PRO"
    MASTER_CRAFTSMAN = "MASTER_CRAFTSMAN"

class ServiceCategory(str, Enum):
    PLUMBING = "PLUMBING"
    ELECTRICAL = "ELECTRICAL"
    HVAC = "HVAC"
    CARPENTRY = "CARPENTRY"
    MASONRY = "MASONRY"
    APPLIANCES = "APPLIANCES"
    AUTO_TECH = "AUTO_TECH"
    IT_NETWORKING = "IT_NETWORKING"

class JobStatus(str, Enum):
    REQUESTED = "REQUESTED"
    ACCEPTED = "ACCEPTED"
    EN_ROUTE = "EN_ROUTE"
    IN_PROGRESS = "IN_PROGRESS"
    COMPLETION_REQUESTED = "COMPLETION_REQUESTED"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"
    DISPUTED = "DISPUTED"

class EscrowStatus(str, Enum):
    HOLDING = "HOLDING"
    RELEASED = "RELEASED"
    REFUNDED = "REFUNDED"

class PaymentMethod(str, Enum):
    FIXO_WALLET = "FIXO_WALLET"
    MTN_MOMO = "MTN_MOMO"
    ORANGE_MONEY = "ORANGE_MONEY"
    CREDIT_CARD = "CREDIT_CARD"

# Auth Schemas
class RegisterRequest(BaseModel):
    name: str = Field(..., min_length=2, max_length=100)
    email: EmailStr
    password: str = Field(..., min_length=8)
    phone: str = Field(..., regex=r"^\+?[0-9]{8,15}$")
    role: UserRole = UserRole.CUSTOMER
    category: Optional[ServiceCategory] = None

class LoginRequest(BaseModel):
    email: EmailStr
    password: str

class RefreshTokenRequest(BaseModel):
    refresh_token: str

class GoogleAuthRequest(BaseModel):
    id_token: str
    role: Optional[UserRole] = UserRole.CUSTOMER
    category: Optional[ServiceCategory] = None

class TokenResponse(BaseModel):
    access_token: str
    refresh_token: str
    token_type: str = "bearer"
    user_id: str
    role: UserRole
    name: str
    email: str

# Domain Models
class UserModel(BaseModel):
    id: str
    role: UserRole
    name: str
    email: str
    phone: str
    password_hash: str
    avatar_url: str = ""
    rating: float = 0.0
    balance_xaf: float = 0.0
    escrow_locked_xaf: float = 0.0
    fixo_points: int = 0
    verification_status: VerificationStatus = VerificationStatus.UNVERIFIED
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

class WorkerProfileModel(BaseModel):
    id: str
    user_id: str
    name: str
    category: ServiceCategory
    hourly_rate_xaf: float
    emergency_callout_available: bool = False
    bio: str
    skills: List[str] = []
    certifications: List[str] = []
    completed_jobs: int = 0
    rating: float = 0.0
    review_count: int = 0
    working_days: List[str] = ["Mon", "Tue", "Wed", "Thu", "Fri"]
    slot_intervals: List[str] = ["08:00 - 10:00", "10:30 - 12:30", "13:30 - 15:30", "16:00 - 18:00"]
    avatar_url: str = ""
    location_city: str = "Douala"
    location_distance_km: float = 0.0
    background_verified: bool = False
    phone: str

class ServiceItemModel(BaseModel):
    id: str
    worker_id: str
    name: str
    category: ServiceCategory
    description: str
    price_xaf: float
    duration_estimate_minutes: int = 60

class CreateBookingRequest(BaseModel):
    worker_id: str
    service_id: str
    date: str
    time_slot: str
    address: str
    notes: str = ""
    payment_method: PaymentMethod = PaymentMethod.MTN_MOMO

class BookingModel(BaseModel):
    id: str
    customer_id: str
    customer_name: str
    worker_id: str
    worker_name: str
    service_title: str
    category: ServiceCategory
    date: str
    time_slot: str
    status: JobStatus
    address: str
    notes: str
    price_amount_xaf: float
    escrow_status: EscrowStatus
    payment_method: PaymentMethod
    customer_rating: float = 0.0
    customer_review_text: str = ""
    points_earned: int = 0
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

class CreateReelRequest(BaseModel):
    title: str = Field(..., min_length=3, max_length=120)
    description: str = Field(..., max_length=500)
    category: ServiceCategory
    service_id: str
    video_url: str
    thumbnail_url: str
    tags: List[str] = []
    is_published: bool = True

class UpdateReelRequest(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    category: Optional[ServiceCategory] = None
    tags: Optional[List[str]] = None
    thumbnail_url: Optional[str] = None
    is_published: Optional[bool] = None

class ReelModel(BaseModel):
    id: str
    worker_id: str
    worker_name: str
    worker_avatar: str
    title: str
    description: str
    category: ServiceCategory
    service_id: str
    video_url: str
    thumbnail_url: str
    likes_count: int = 0
    bookings_count: int = 0
    tags: List[str] = []
    is_published: bool = True
    public_share_url: str
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

class LedgerTransactionModel(BaseModel):
    id: str
    user_id: str
    type: str # DEPOSIT, ESCROW_HOLD, ESCROW_RELEASE, PAYOUT, COMMISSION, REFUND
    amount_xaf: float
    currency: str = "XAF"
    description: str
    status: str = "COMPLETED"
    payment_provider: str # MTN_MOMO, ORANGE_MONEY, FIXO_ESCROW
    reference_code: str
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

# Webhook payload models for MTN and Orange Money
class MtnMomoWebhook(BaseModel):
    financial_transaction_id: str
    external_id: str
    amount: str
    currency: str
    payer: dict
    status: str # SUCCESSFUL, FAILED

class OrangeMoneyWebhook(BaseModel):
    notif_token: str
    txnid: str
    order_id: str
    status: str # SUCCESS, FAILED
    amount: float

# Chat & Communication Models
class MessageDeliveryStatus(str, Enum):
    SENT = "SENT"
    DELIVERED = "DELIVERED"
    READ = "READ"
    FAILED = "FAILED"

class ChatMessageModel(BaseModel):
    id: str
    conversation_id: str
    booking_id: Optional[str] = None
    sender_id: str
    sender_name: str
    sender_role: UserRole
    recipient_id: str
    text: str
    attachment_url: Optional[str] = None
    attachment_type: Optional[str] = None # image, document
    delivery_status: MessageDeliveryStatus = MessageDeliveryStatus.SENT
    is_read: bool = False
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

class ConversationModel(BaseModel):
    id: str
    booking_id: Optional[str] = None
    customer_id: str
    customer_name: str
    worker_id: str
    worker_name: str
    last_message: str = ""
    last_message_time: int = Field(default_factory=lambda: int(time.time() * 1000))
    customer_unread: int = 0
    worker_unread: int = 0
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

class SendChatMessageRequest(BaseModel):
    text: str = Field(..., max_length=2000)
    attachment_url: Optional[str] = None
    attachment_type: Optional[str] = None

class ReportConversationRequest(BaseModel):
    reason: str = Field(..., min_length=5, max_length=500)
    reported_user_id: str

# Notification Models
class NotificationCategory(str, Enum):
    JOBS = "JOBS"
    MESSAGES = "MESSAGES"
    PAYMENTS = "PAYMENTS"
    TRUST = "TRUST"
    SYSTEM = "SYSTEM"

class NotificationModel(BaseModel):
    id: str
    user_id: str
    category: NotificationCategory
    title: str
    body: str
    reference_id: Optional[str] = None # booking_id or conversation_id
    deep_link: Optional[str] = None
    is_read: bool = False
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

class DeviceTokenRequest(BaseModel):
    device_token: str
    platform: str = "android"

class NotificationPreferencesModel(BaseModel):
    jobs_enabled: bool = True
    messages_enabled: bool = True
    payments_enabled: bool = True
    trust_enabled: bool = True
    system_enabled: bool = True

# Disputes & Trust Models
class DisputeReason(str, Enum):
    WORKER_NO_SHOW = "WORKER_NO_SHOW"
    POOR_QUALITY = "POOR_QUALITY"
    INCOMPLETE_JOB = "INCOMPLETE_JOB"
    OVERCHARGING = "OVERCHARGING"
    UNPROFESSIONAL_BEHAVIOR = "UNPROFESSIONAL_BEHAVIOR"
    OTHER = "OTHER"

class DisputeStatus(str, Enum):
    OPEN = "OPEN"
    UNDER_REVIEW = "UNDER_REVIEW"
    RESOLVED_REFUND_CUSTOMER = "RESOLVED_REFUND_CUSTOMER"
    RESOLVED_RELEASE_WORKER = "RESOLVED_RELEASE_WORKER"
    DISMISSED = "DISMISSED"

class DisputeModel(BaseModel):
    id: str
    booking_id: str
    claimant_id: str
    claimant_role: UserRole
    respondent_id: str
    reason: DisputeReason
    description: str
    evidence_urls: List[str] = []
    status: DisputeStatus = DisputeStatus.OPEN
    admin_notes: Optional[str] = None
    resolved_at: Optional[int] = None
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

class CreateDisputeRequest(BaseModel):
    reason: DisputeReason
    description: str = Field(..., min_length=10, max_length=1000)
    evidence_urls: List[str] = []

class ResolveDisputeRequest(BaseModel):
    resolution: DisputeStatus # RESOLVED_REFUND_CUSTOMER or RESOLVED_RELEASE_WORKER
    admin_notes: str

# Review & Rating Request
class ReviewBookingRequest(BaseModel):
    rating: float = Field(..., ge=1.0, le=5.0)
    review_text: str = Field(..., max_length=1000)

class CancelBookingRequest(BaseModel):
    reason: str = Field(..., min_length=3, max_length=500)

# Live Location Tracking Models
class WorkerLocationModel(BaseModel):
    booking_id: str
    worker_id: str
    latitude: float = 0.0
    longitude: float = 0.0
    speed_kmh: float = 0.0
    bearing_degrees: float = 0.0
    accuracy_meters: float = 0.0
    is_active: bool = True
    updated_at: int = Field(default_factory=lambda: int(time.time() * 1000))

class UpdateLocationRequest(BaseModel):
    latitude: float
    longitude: float
    speed_kmh: float = 0.0
    bearing_degrees: float = 0.0
    accuracy_meters: float = 0.0

# Enterprise Models
class EnterpriseProjectModel(BaseModel):
    id: str
    org_id: str
    org_name: str
    title: str
    description: str
    location: str
    budget_xaf: float
    status: str = "ACTIVE"
    created_at: int = Field(default_factory=lambda: int(time.time() * 1000))

# Admin Stats
class AdminDashboardStatsModel(BaseModel):
    total_users: int
    total_workers: int
    total_bookings: int
    total_gmv_xaf: float
    active_disputes: int
    verified_workers_count: int

