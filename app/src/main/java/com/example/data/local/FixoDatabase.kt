package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.DisputeReport
import com.example.data.model.EnterpriseProject
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.LoyaltyTier
import com.example.data.model.PaymentMethod
import com.example.data.model.Reel
import com.example.data.model.RewardItem
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.SubscriptionTier
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.model.WalletTransaction
import com.example.data.model.WorkerProfile

class FixoTypeConverters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.CUSTOMER)

    @TypeConverter
    fun fromVerificationStatus(status: VerificationStatus): String = status.name

    @TypeConverter
    fun toVerificationStatus(value: String): VerificationStatus =
        runCatching { VerificationStatus.valueOf(value) }.getOrDefault(VerificationStatus.VERIFIED_PRO)

    @TypeConverter
    fun fromServiceCategory(cat: ServiceCategory): String = cat.name

    @TypeConverter
    fun toServiceCategory(value: String): ServiceCategory =
        runCatching { ServiceCategory.valueOf(value) }.getOrDefault(ServiceCategory.PLUMBING)

    @TypeConverter
    fun fromJobStatus(status: JobStatus): String = status.name

    @TypeConverter
    fun toJobStatus(value: String): JobStatus = runCatching { JobStatus.valueOf(value) }.getOrDefault(JobStatus.REQUESTED)

    @TypeConverter
    fun fromEscrowStatus(status: EscrowStatus): String = status.name

    @TypeConverter
    fun toEscrowStatus(value: String): EscrowStatus =
        runCatching { EscrowStatus.valueOf(value) }.getOrDefault(EscrowStatus.HOLDING)

    @TypeConverter
    fun fromPaymentMethod(method: PaymentMethod): String = method.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod =
        runCatching { PaymentMethod.valueOf(value) }.getOrDefault(PaymentMethod.FIXO_WALLET)

    @TypeConverter
    fun fromSubscriptionTier(tier: SubscriptionTier): String = tier.name

    @TypeConverter
    fun toSubscriptionTier(value: String): SubscriptionTier =
        runCatching { SubscriptionTier.valueOf(value) }.getOrDefault(SubscriptionTier.PRO)

    @TypeConverter
    fun fromLoyaltyTier(tier: LoyaltyTier): String = tier.name

    @TypeConverter
    fun toLoyaltyTier(value: String): LoyaltyTier =
        runCatching { LoyaltyTier.valueOf(value) }.getOrDefault(LoyaltyTier.SILVER)
}

@Database(
    entities = [
        User::class,
        WorkerProfile::class,
        ServiceItem::class,
        Reel::class,
        Booking::class,
        ChatMessage::class,
        WalletTransaction::class,
        RewardItem::class,
        EnterpriseProject::class,
        DisputeReport::class,
        com.example.data.model.WorkerLocation::class,
        com.example.data.model.FixoNotification::class,
        com.example.data.model.WorkerReview::class,
        com.example.data.model.Organization::class,
        com.example.data.model.WorkforceRequest::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(FixoTypeConverters::class)
abstract class FixoDatabase : RoomDatabase() {
    abstract fun fixoDao(): FixoDao

    companion object {
        @Volatile
        private var INSTANCE: FixoDatabase? = null

        fun getDatabase(context: Context): FixoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FixoDatabase::class.java,
                    "fixo_production.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
