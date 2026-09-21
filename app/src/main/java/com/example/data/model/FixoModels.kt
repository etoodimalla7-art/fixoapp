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

enum class ServiceCategory(val displayName: String, val iconKey: String) {
    PLUMBING("Plumbing & Pipefitting", "plumbing"),
    ELECTRICAL("Electrical & Solar", "electrical"),
    HVAC("HVAC & Air Conditioning", "hvac"),
    CARPENTRY("Cabinetry & Carpentry", "carpentry"),
    MASONRY("Masonry & Tiling", "masonry"),
    APPLIANCES("Appliance Diagnostics", "appliances"),
    AUTO_TECH("Mobile Auto Mechanic", "auto_tech"),
    IT_NETWORKING("CCTV & Smart Systems", "it_networking")
}

enum class JobStatus {
    REQUESTED,
    ACCEPTED,
    EN_ROUTE,
    IN_PROGRESS,
    COMPLETION_REQUESTED,
    COMPLETED,
    CANCELLED,
    DISPUTED
}

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
    val loyaltyTier: LoyaltyTier = LoyaltyTier.BRONZE
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
    val createdAt: Long = System.currentTimeMillis()
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
    val isRead: Boolean = true
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

