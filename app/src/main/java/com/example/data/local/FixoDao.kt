package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.DisputeReport
import com.example.data.model.EnterpriseProject
import com.example.data.model.Reel
import com.example.data.model.RewardItem
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.WalletTransaction
import com.example.data.model.WorkerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface FixoDao {

    // USERS
    @Query("SELECT * FROM users WHERE role = :role LIMIT 1")
    fun getUserByRole(role: UserRole): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    // WORKERS
    @Query("SELECT * FROM workers ORDER BY rating DESC, completedJobs DESC")
    fun getAllWorkers(): Flow<List<WorkerProfile>>

    @Query("SELECT * FROM workers WHERE category = :category ORDER BY rating DESC")
    fun getWorkersByCategory(category: ServiceCategory): Flow<List<WorkerProfile>>

    @Query("SELECT * FROM workers WHERE id = :id")
    suspend fun getWorkerById(id: String): WorkerProfile?

    @Query("SELECT * FROM workers WHERE id = :id")
    fun observeWorkerById(id: String): Flow<WorkerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkers(workers: List<WorkerProfile>)

    @Update
    suspend fun updateWorker(worker: WorkerProfile)

    // SERVICES
    @Query("SELECT * FROM services WHERE workerId = :workerId")
    fun getServicesForWorker(workerId: String): Flow<List<ServiceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceItem>)

    // REELS
    @Query("SELECT * FROM reels WHERE isPublished = 1 ORDER BY createdAt DESC")
    fun getAllReels(): Flow<List<Reel>>

    @Query("SELECT * FROM reels WHERE workerId = :workerId ORDER BY createdAt DESC")
    fun getReelsForWorker(workerId: String): Flow<List<Reel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: Reel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<Reel>)

    @Update
    suspend fun updateReel(reel: Reel)

    @Query("DELETE FROM reels WHERE id = :reelId")
    suspend fun deleteReel(reelId: String)

    @Query("UPDATE reels SET likesCount = likesCount + 1 WHERE id = :reelId")
    suspend fun incrementReelLikes(reelId: String)

    // BOOKINGS
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getBookingsForCustomer(customerId: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE workerId = :workerId ORDER BY createdAt DESC")
    fun getBookingsForWorker(workerId: String): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    fun getBookingById(id: String): Flow<Booking?>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingByIdDirect(id: String): Booking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<Booking>)

    @Update
    suspend fun updateBooking(booking: Booking)

    // CHAT MESSAGES
    @Query("SELECT * FROM chat_messages WHERE bookingId = :bookingId ORDER BY timestamp ASC")
    fun getMessagesForBooking(bookingId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessage>)

    // WALLET TRANSACTIONS
    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUser(userId: String): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<WalletTransaction>)

    // REWARDS
    @Query("SELECT * FROM rewards")
    fun getAllRewards(): Flow<List<RewardItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewards(rewards: List<RewardItem>)

    // ENTERPRISE PROJECTS
    @Query("SELECT * FROM enterprise_projects ORDER BY id DESC")
    fun getAllEnterpriseProjects(): Flow<List<EnterpriseProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnterpriseProject(project: EnterpriseProject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnterpriseProjects(projects: List<EnterpriseProject>)

    // DISPUTES
    @Query("SELECT * FROM disputes ORDER BY timestamp DESC")
    fun getAllDisputes(): Flow<List<DisputeReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: DisputeReport)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisputes(disputes: List<DisputeReport>)

    @Update
    suspend fun updateDispute(dispute: DisputeReport)

    // WORKER LOCATIONS (JOB-SCOPED REAL TRACKING)
    @Query("SELECT * FROM worker_locations WHERE bookingId = :bookingId")
    fun getWorkerLocation(bookingId: String): Flow<com.example.data.model.WorkerLocation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkerLocation(location: com.example.data.model.WorkerLocation)

    @Query("DELETE FROM worker_locations WHERE bookingId = :bookingId")
    suspend fun deleteWorkerLocation(bookingId: String)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<com.example.data.model.FixoNotification>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: com.example.data.model.FixoNotification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<com.example.data.model.FixoNotification>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: String)

    // WORKER REVIEWS
    @Query("SELECT * FROM worker_reviews WHERE workerId = :workerId ORDER BY createdAt DESC")
    fun getReviewsForWorker(workerId: String): Flow<List<com.example.data.model.WorkerReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkerReview(review: com.example.data.model.WorkerReview)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkerReviews(reviews: List<com.example.data.model.WorkerReview>)

    // SERVICE MANAGEMENT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceItem)

    @Query("DELETE FROM services WHERE id = :serviceId")
    suspend fun deleteService(serviceId: String)

    // CLEAR DATABASE TABLES FOR CLEAN PRODUCTION MODE
    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Query("DELETE FROM workers")
    suspend fun clearWorkers()

    @Query("DELETE FROM services")
    suspend fun clearServices()

    @Query("DELETE FROM reels")
    suspend fun clearReels()

    @Query("DELETE FROM bookings")
    suspend fun clearBookings()

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    @Query("DELETE FROM wallet_transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM enterprise_projects")
    suspend fun clearEnterpriseProjects()

    @Query("DELETE FROM disputes")
    suspend fun clearDisputes()

    @Query("DELETE FROM worker_locations")
    suspend fun clearWorkerLocations()

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()

    @Query("DELETE FROM worker_reviews")
    suspend fun clearWorkerReviews()
}
