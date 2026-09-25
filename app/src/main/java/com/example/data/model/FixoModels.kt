package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER,
    WORKER,
    ENTERPRISE,
    ADMIN
}

enum class VerificationStatus {
    UNVERIFIED,
    PENDING,
    VERIFIED_PRO,
    MASTER_CRAFTSMAN
}

enum class ArtisanKycStatus {
    NOT_STARTED,
    IN_REVIEW,
    APPROVED
}

val VerificationStatus.artisanKycStatus: ArtisanKycStatus
    get() = when (this) {
        VerificationStatus.UNVERIFIED -> ArtisanKycStatus.NOT_STARTED
        VerificationStatus.PENDING -> ArtisanKycStatus.IN_REVIEW
        VerificationStatus.VERIFIED_PRO, VerificationStatus.MASTER_CRAFTSMAN -> ArtisanKycStatus.APPROVED
    }

enum class CameroonMobileOperator(val label: String, val badgeEmoji: String) {
    MTN_MOMO("MTN MoMo", "🟡"),
    ORANGE_MONEY("Orange Money", "🟠"),
    UNKNOWN("Non détecté", "⚪")
}

fun detectCameroonOperator(phoneNumber: String): CameroonMobileOperator {
    val digits = phoneNumber.filter { it.isDigit() }.removePrefix("237")
    return when {
        digits.startsWith("67") || digits.startsWith("68") ||
            (digits.length >= 3 && digits.substring(0, 3) in listOf("650", "651", "652", "653", "654")) ->
            CameroonMobileOperator.MTN_MOMO

        digits.startsWith("69") ||
            (digits.length >= 3 && digits.substring(0, 3) in listOf("655", "656", "657", "658", "659")) ->
            CameroonMobileOperator.ORANGE_MONEY

        else -> CameroonMobileOperator.UNKNOWN
    }
}

enum class ServiceCategory(val displayName: String, val iconKey: String) {
    CLEANING("Cleaning", "cleaning"),
    PLUMBING("Plumbing", "plumbing"),
    ELECTRICAL("Electrical", "electrical"),
    BEAUTY("Beauty", "beauty"),
    HAIR_BRAIDING("Hair & Braiding", "hair"),
    AC_COOLING("AC & Cooling", "ac"),
    APPLIANCE_REPAIR("Appliance Repair", "appliances"),
    PAINTING("Painting", "painting"),
    MOVING("Moving", "moving"),
    CAR_SERVICES("Car Services", "car"),
    TUTORING("Tutoring", "tutoring"),
    PHOTOGRAPHY("Photography", "photography"),
    EVENTS("Events", "events"),
    TECH_SUPPORT("Tech Support", "tech"),
    CONSTRUCTION("Construction", "construction"),
    OTHER("Other Trade", "other");

    companion object {
        // Compatibility aliases for existing seed references
        val HVAC = AC_COOLING
        val CARPENTRY = CONSTRUCTION
        val MASONRY = CONSTRUCTION
        val APPLIANCES = APPLIANCE_REPAIR
        val AUTO_TECH = CAR_SERVICES
        val IT_NETWORKING = TECH_SUPPORT
    }
}

enum class JobStatus(val displayName: String) {
    REQUESTED("Requested"),
    DISPATCHED("Dispatched"),
    ACCEPTED("Accepted"),
    SCHEDULED("Scheduled"),
    ON_THE_WAY("On The Way"),
    ARTISAN_EN_ROUTE("Artisan En Route"),
    ARRIVED("Arrived"),
    ON_SITE("On Site"),
    IN_PROGRESS("In Progress"),
    WORK_IN_PROGRESS("Work In Progress"),
    COMPLETION_REQUESTED("Completion Requested"),
    COMPLETED_PENDING_HANDSHAKE("Completed Pending Handshake"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    DISPUTED("Disputed");

    companion object {
        val EN_ROUTE = ON_THE_WAY
    }
}

val JobStatus.isDispatched: Boolean
    get() = this == JobStatus.REQUESTED || this == JobStatus.DISPATCHED

val JobStatus.isEnRoute: Boolean
    get() = this == JobStatus.ON_THE_WAY || this == JobStatus.ARTISAN_EN_ROUTE

val JobStatus.isOnSite: Boolean
    get() = this == JobStatus.ARRIVED || this == JobStatus.ON_SITE

val JobStatus.isWorking: Boolean
    get() = this == JobStatus.IN_PROGRESS || this == JobStatus.WORK_IN_PROGRESS

val JobStatus.isCompletionPending: Boolean
    get() = this == JobStatus.COMPLETION_REQUESTED || this == JobStatus.COMPLETED_PENDING_HANDSHAKE

enum class EscrowStatus {
    HOLDING,
    RELEASED,
    REFUNDED
}

enum class PaymentMethod(val label: String) {
    FIXO_WALLET("FIXO Escrow Wallet"),
    MTN_MOMO("MTN Mobile Money"),
    ORANGE_MONEY("Orange Money"),
    CREDIT_CARD("Credit / Debit Card")
}

enum class SubscriptionTier(val title: String, val priceMonthly: Double) {
    STARTER("Starter Free", 0.0),
    PRO("Pro Craftsman", 10000.0),
    PREMIUM("Master Artisan Elite", 25000.0)
}

enum class LoyaltyTier(val title: String, val minPoints: Int, val discountPercent: Int) {
    BRONZE("Bronze Artisan Club", 0, 0),
    SILVER("Silver Guild", 250, 5),
    GOLD("Gold Guild Master", 750, 10),
    PLATINUM("Platinum Patron", 1500, 15)
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val role: UserRole,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val rating: Double = 5.0,
    val balance: Double = 0.0,
    val escrowLocked: Double = 0.0,
    val fixoPoints: Int = 0,
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val loyaltyTier: LoyaltyTier = LoyaltyTier.BRONZE,
    val username: String = "",
    val region: String = "Littoral",
    val city: String = "Douala",
    val quarter: String = "Akwa",
    val isPhoneVerified: Boolean = true,
    val isEmailVerified: Boolean = false,
    val organizationId: String? = null
)

@Entity(tableName = "workers")
data class WorkerProfile(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val category: ServiceCategory,
    val hourlyRate: Double,
    val emergencyCalloutAvailable: Boolean = true,
    val bio: String,
    val skills: String, // comma-separated
    val certifications: String, // comma-separated
    val completedJobs: Int,
    val rating: Double,
    val reviewCount: Int,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.PRO,
    val workingDays: String = "Mon,Tue,Wed,Thu,Fri,Sat", // comma-separated
    val slotIntervals: String = "08:00 - 10:00,10:30 - 12:30,13:30 - 15:30,16:00 - 18:00",
    val avatarUrl: String,
    val locationCity: String,
    val locationDistanceKm: Double,
    val backgroundVerified: Boolean = true,
    val phone: String = "+1 (555) 234-8921"
)

@Entity(tableName = "services")
data class ServiceItem(
    @PrimaryKey val id: String,
    val workerId: String,
    val name: String,
    val category: ServiceCategory,
    val description: String,
    val price: Double,
    val durationEstimateMinutes: Int = 60
)

@Entity(tableName = "reels")
data class Reel(
    @PrimaryKey val id: String,
    val workerId: String,
    val workerName: String,
    val workerAvatar: String,
    val title: String,
    val description: String,
    val category: ServiceCategory,
    val serviceId: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val likesCount: Int,
    val bookingsCount: Int,
    val tags: String, // comma-separated
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val priceAmount: Double = 15000.0,
    val location: String = "Akwa, Douala",
    val reviewsCount: Int = 84
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val workerId: String,
    val workerName: String,
    val workerAvatar: String,
    val serviceTitle: String,
    val category: ServiceCategory,
    val date: String,
    val timeSlot: String,
    val status: JobStatus,
    val address: String,
    val notes: String,
    val priceAmount: Double,
    val escrowStatus: EscrowStatus,
    val paymentMethod: PaymentMethod,
    val customerRating: Float = 0f,
    val customerReviewText: String = "",
    val pointsEarned: Int = 0,
    val workerPhone: String = "+237 671 234 567",
    val customerPhone: String = "+237 677 889 900",
    val customerLat: Double = 4.0511,
    val customerLng: Double = 9.7679,
    val workerLat: Double = 4.0483,
    val workerLng: Double = 9.7043,
    val trackingActive: Boolean = false,
    val etaMinutes: Int = 12,
    val distanceKm: Double = 3.4,
    val workerSpeedKmh: Float = 25.0f,
    val workerHeading: Float = 45.0f,
    val lastLocationUpdate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val beforePhotoUrl: String? = null,
    val beforePhotoTimestamp: Long? = null,
    val beforePhotoLat: Double? = null,
    val beforePhotoLng: Double? = null,
    val afterPhotoUrl: String? = null,
    val afterPhotoTimestamp: Long? = null,
    val afterPhotoLat: Double? = null,
    val afterPhotoLng: Double? = null,
    val workStartedTimestamp: Long? = null,
    val workCompletedTimestamp: Long? = null,
    val packageTier: String = "FLASH"
)

@Entity(tableName = "worker_locations")
data class WorkerLocation(
    @PrimaryKey val bookingId: String,
    val workerId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Float = 0f,
    val heading: Float = 0f,
    val destinationLat: Double = 4.0511,
    val destinationLng: Double = 9.7679,
    val destinationAddress: String = "",
    val isTrackingActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class FixoNotification(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // BOOKING_CREATED, WORKER_ACCEPTED, WORKER_STARTED_TRIP, WORKER_ARRIVED, MESSAGE_RECEIVED, PAYMENT, JOB_COMPLETED, REVIEW_REQUEST, VERIFICATION, DISPUTE
    val bookingId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "worker_reviews")
data class WorkerReview(
    @PrimaryKey val id: String,
    val bookingId: String,
    val workerId: String,
    val customerId: String,
    val customerName: String,
    val rating: Float,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val bookingId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true,
    val attachmentUrl: String? = null,
    val attachmentType: String? = null, // IMAGE, DOCUMENT, VOICE, ANNOTATED_IMAGE
    val deliveryState: String = "DELIVERED", // SENDING, SENT, DELIVERED, READ, FAILED
    val voiceDurationSeconds: Int? = null,
    val annotationPathsJson: String? = null
)

data class ConversationItem(
    val bookingId: String,
    val otherParticipantId: String,
    val otherParticipantName: String,
    val otherParticipantAvatar: String,
    val otherParticipantRole: UserRole,
    val serviceTitle: String,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val unreadCount: Int = 0,
    val jobStatus: JobStatus
)

@Entity(tableName = "wallet_transactions")
data class WalletTransaction(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // DEPOSIT, ESCROW_HOLD, ESCROW_RELEASE, PAYOUT, CASHBACK
    val amount: Double,
    val currency: String = "XAF",
    val description: String,
    val status: String = "COMPLETED", // COMPLETED, PENDING, FAILED
    val paymentProvider: String, // MTN_MOMO, ORANGE_MONEY, FIXO_ESCROW
    val referenceCode: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun formatFixoCurrency(amount: Double): String {
    return String.format(java.util.Locale.FRANCE, "%,.0f FCFA", amount)
}

@Entity(tableName = "rewards")
data class RewardItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val pointsRequired: Int,
    val discountAmount: Double,
    val promoCode: String
)

@Entity(tableName = "enterprise_projects")
data class EnterpriseProject(
    @PrimaryKey val id: String,
    val companyName: String,
    val title: String,
    val requiredWorkersCount: Int,
    val category: ServiceCategory,
    val budget: Double,
    val status: String = "ACTIVE", // OPEN, ACTIVE, COMPLETED
    val location: String,
    val assignedWorkersCount: Int = 0
)

@Entity(tableName = "disputes")
data class DisputeReport(
    @PrimaryKey val id: String,
    val reporterId: String,
    val reporterName: String,
    val reportedType: String, // WORKER, REEL, BOOKING
    val reportedId: String,
    val reason: String,
    val status: String = "PENDING", // PENDING, INVESTIGATING, RESOLVED
    val adminNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "organizations")
data class Organization(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // Construction & Renovation, Facilities Management, Multi-Trade Agency, Electrical Engineering
    val description: String,
    val logoUrl: String = "",
    val phone: String,
    val email: String,
    val address: String,
    val city: String = "Douala",
    val region: String = "Littoral",
    val registrationNumber: String, // RCCM / NIU
    val authorizedRepresentative: String,
    val representativeTitle: String = "Managing Director",
    val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED_PRO,
    val rating: Double = 4.9,
    val completedProjectsCount: Int = 18,
    val activeWorkersCount: Int = 32,
    val website: String = "",
    val servicesOffered: String = "Commercial Plumbing, Industrial Electrical, Architectural Masonry, HVAC Installations",
    val createdAt: Long = System.currentTimeMillis()
) {
    val isVerified: Boolean get() = verificationStatus == VerificationStatus.VERIFIED_PRO || verificationStatus == VerificationStatus.MASTER_CRAFTSMAN
    val businessType: String get() = type
    val representativeName: String get() = authorizedRepresentative
}

@Entity(tableName = "workforce_requests")
data class WorkforceRequest(
    @PrimaryKey val id: String,
    val organizationId: String,
    val organizationName: String,
    val projectTitle: String,
    val category: ServiceCategory,
    val requiredCount: Int,
    val recruitedCount: Int = 0,
    val ratePerDayXaf: Double,
    val location: String,
    val startDate: String,
    val endDate: String,
    val status: String = "OPEN", // OPEN, IN_PROGRESS, COMPLETED
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val title: String get() = projectTitle
    val neededWorkers: Int get() = requiredCount
    val filledWorkers: Int get() = recruitedCount
    val dailyRate: Double get() = ratePerDayXaf
    val applicantIds: List<String> get() = emptyList()
}

// Cameroon Location Data Structure
data class CameroonQuarter(val name: String, val lat: Double, val lng: Double)
data class CameroonCity(val name: String, val quarters: List<CameroonQuarter>)
data class CameroonRegion(val name: String, val cities: List<CameroonCity>)

object CameroonLocationRegistry {
    val regions: List<CameroonRegion> = listOf(
        CameroonRegion(
            name = "Littoral",
            cities = listOf(
                CameroonCity(
                    name = "Douala",
                    quarters = listOf(
                        CameroonQuarter("Akwa", 4.0505, 9.6950),
                        CameroonQuarter("Bonamoussadi", 4.0880, 9.7360),
                        CameroonQuarter("Bonapriso", 4.0150, 9.6920),
                        CameroonQuarter("Makepe", 4.0720, 9.7450),
                        CameroonQuarter("Deido", 4.0620, 9.7100),
                        CameroonQuarter("Bali", 4.0320, 9.6890),
                        CameroonQuarter("Kotto", 4.0950, 9.7550),
                        CameroonQuarter("Denver", 4.0820, 9.7280),
                        CameroonQuarter("Bepanda", 4.0580, 9.7320),
                        CameroonQuarter("New Bell", 4.0280, 9.7210)
                    )
                ),
                CameroonCity(
                    name = "Edea",
                    quarters = listOf(
                        CameroonQuarter("Centre-Ville", 3.8000, 10.1333),
                        CameroonQuarter("Pout", 3.7920, 10.1250)
                    )
                )
            )
        ),
        CameroonRegion(
            name = "Centre",
            cities = listOf(
                CameroonCity(
                    name = "Yaoundé",
                    quarters = listOf(
                        CameroonQuarter("Bastos", 3.8860, 11.5140),
                        CameroonQuarter("Omnisport", 3.8820, 11.5360),
                        CameroonQuarter("Biyem-Assi", 3.8340, 11.4880),
                        CameroonQuarter("Mendong", 3.8200, 11.4720),
                        CameroonQuarter("Tsinga", 3.8750, 11.5020),
                        CameroonQuarter("Essos", 3.8680, 11.5450),
                        CameroonQuarter("Odza", 3.8100, 11.5350),
                        CameroonQuarter("Santa Barbara", 3.8990, 11.5200)
                    )
                )
            )
        ),
        CameroonRegion(
            name = "West",
            cities = listOf(
                CameroonCity(
                    name = "Bafoussam",
                    quarters = listOf(
                        CameroonQuarter("Djeleng", 5.4770, 10.4180),
                        CameroonQuarter("Famla", 5.4850, 10.4280),
                        CameroonQuarter("Toukouop", 5.4690, 10.4050)
                    )
                )
            )
        ),
        CameroonRegion(
            name = "South-West",
            cities = listOf(
                CameroonCity(
                    name = "Buea",
                    quarters = listOf(
                        CameroonQuarter("Molyko", 4.1520, 9.2890),
                        CameroonQuarter("Clerks Quarters", 4.1620, 9.2780),
                        CameroonQuarter("Bokwango", 4.1450, 9.2550)
                    )
                ),
                CameroonCity(
                    name = "Limbe",
                    quarters = listOf(
                        CameroonQuarter("Down Beach", 4.0150, 9.2150),
                        CameroonQuarter("Bota", 4.0080, 9.1850)
                    )
                )
            )
        )
    )

    fun getDefaultQuarter(): CameroonQuarter = regions.first().cities.first().quarters.first()

    fun findQuarter(quarterName: String): CameroonQuarter? {
        for (region in regions) {
            for (city in region.cities) {
                val found = city.quarters.find { it.name.equals(quarterName, ignoreCase = true) }
                if (found != null) return found
            }
        }
        return null
    }
}


