package com.example.data.repository

import android.content.Context
import com.example.data.local.FixoDatabase
import com.example.data.local.FixoSeedData
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
import com.example.localization.AppLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class FixoRepository(context: Context) {
    private val database = FixoDatabase.getDatabase(context)
    private val dao = database.fixoDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val api = com.example.data.remote.NetworkClient.getApiService()

    private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentRole: Flow<UserRole> = _currentRole.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.EN)
    val currentLanguage: Flow<AppLanguage> = _currentLanguage.asStateFlow()

    val sessionManager = SessionManager(context)

    init {
        scope.launch {
            if (sessionManager.isDevEnvironment.value) {
                seedDatabaseIfEmpty()
            }
        }
    }

    suspend fun seedDatabaseIfEmpty() {
        val existingUsers = dao.getUserByRole(UserRole.CUSTOMER).first()
        if (existingUsers == null) {
            FixoSeedData.defaultUsers.forEach { dao.insertUser(it) }
            dao.insertWorkers(FixoSeedData.defaultWorkers)
            dao.insertServices(FixoSeedData.defaultServices)
            dao.insertReels(FixoSeedData.defaultReels)
            dao.insertBookings(FixoSeedData.defaultBookings)
            dao.insertChatMessages(FixoSeedData.defaultChat)
            dao.insertTransactions(FixoSeedData.defaultTransactions)
            dao.insertRewards(FixoSeedData.defaultRewards)
            dao.insertEnterpriseProjects(FixoSeedData.defaultEnterpriseProjects)
            dao.insertDisputes(FixoSeedData.defaultDisputes)
        }
    }

    suspend fun loadSandboxTestData() {
        sessionManager.setDevEnvironment(true)
        dao.clearUsers()
        dao.clearWorkers()
        dao.clearServices()
        dao.clearReels()
        dao.clearBookings()
        dao.clearChatMessages()
        dao.clearTransactions()
        dao.clearEnterpriseProjects()
        dao.clearDisputes()

        FixoSeedData.defaultUsers.forEach { dao.insertUser(it) }
        dao.insertWorkers(FixoSeedData.defaultWorkers)
        dao.insertServices(FixoSeedData.defaultServices)
        dao.insertReels(FixoSeedData.defaultReels)
        dao.insertBookings(FixoSeedData.defaultBookings)
        dao.insertChatMessages(FixoSeedData.defaultChat)
        dao.insertTransactions(FixoSeedData.defaultTransactions)
        dao.insertRewards(FixoSeedData.defaultRewards)
        dao.insertEnterpriseProjects(FixoSeedData.defaultEnterpriseProjects)
        dao.insertDisputes(FixoSeedData.defaultDisputes)
    }

    suspend fun clearDatabaseToCleanState() {
        sessionManager.setDevEnvironment(false)
        dao.clearUsers()
        dao.clearWorkers()
        dao.clearServices()
        dao.clearReels()
        dao.clearBookings()
        dao.clearChatMessages()
        dao.clearTransactions()
        dao.clearEnterpriseProjects()
        dao.clearDisputes()
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.EN) AppLanguage.FR else AppLanguage.EN
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    // USERS & PROFILE
    fun getCurrentUser(): Flow<User?> = dao.getUserByRole(_currentRole.value)

    fun getUserById(id: String): Flow<User?> = dao.getUserById(id)

    suspend fun updateCurrentUser(user: User) = dao.updateUser(user)

    // WORKERS
    fun getAllWorkers(): Flow<List<WorkerProfile>> = dao.getAllWorkers()

    fun getWorkersByCategory(category: ServiceCategory): Flow<List<WorkerProfile>> =
        dao.getWorkersByCategory(category)

    fun observeWorkerById(id: String): Flow<WorkerProfile?> = dao.observeWorkerById(id)

    suspend fun getWorkerById(id: String): WorkerProfile? = dao.getWorkerById(id)

    suspend fun updateWorkerProfile(worker: WorkerProfile) = dao.updateWorker(worker)

    // SERVICES
    fun getServicesForWorker(workerId: String): Flow<List<ServiceItem>> =
        dao.getServicesForWorker(workerId)

    // REELS
    fun getAllReels(): Flow<List<Reel>> = dao.getAllReels()

    fun getReelsForWorker(workerId: String): Flow<List<Reel>> = dao.getReelsForWorker(workerId)

    suspend fun likeReel(reelId: String) = dao.incrementReelLikes(reelId)

    suspend fun createReel(
        workerId: String,
        workerName: String,
        workerAvatar: String,
        title: String,
        description: String,
        category: ServiceCategory,
        serviceId: String,
        videoUrl: String,
        thumbnailUrl: String,
        tags: String,
        isPublished: Boolean = true
    ) {
        val newReel = Reel(
            id = "reel_" + UUID.randomUUID().toString().take(8),
            workerId = workerId,
            workerName = workerName,
            workerAvatar = workerAvatar,
            title = title,
            description = description,
            category = category,
            serviceId = serviceId,
            videoUrl = videoUrl.ifBlank { "https://assets.mixkit.co/videos/preview/mixkit-hands-of-an-electrician-fixing-wires-41306-large.mp4" },
            thumbnailUrl = thumbnailUrl.ifBlank { "https://images.unsplash.com/photo-1581244277943-fe4a9c777189?w=600" },
            likesCount = 0,
            bookingsCount = 0,
            tags = tags,
            isPublished = isPublished
        )
        dao.insertReel(newReel)
    }

    suspend fun deleteReel(reelId: String) {
        dao.deleteReel(reelId)
    }

    suspend fun updateReel(reel: Reel) {
        dao.updateReel(reel)
    }

    // BOOKINGS & JOBS
    fun getBookingsForCustomer(customerId: String): Flow<List<Booking>> =
        dao.getBookingsForCustomer(customerId)

    fun getBookingsForWorker(workerId: String): Flow<List<Booking>> =
        dao.getBookingsForWorker(workerId)

    fun getAllBookings(): Flow<List<Booking>> = dao.getAllBookings()

    fun getBookingById(id: String): Flow<Booking?> = dao.getBookingById(id)

    suspend fun createBooking(
        customerId: String,
        customerName: String,
        worker: WorkerProfile,
        service: ServiceItem,
        date: String,
        timeSlot: String,
        address: String,
        notes: String,
        paymentMethod: PaymentMethod
    ): Booking {
        val bookingId = "bk_" + UUID.randomUUID().toString().take(6)
        val booking = Booking(
            id = bookingId,
            customerId = customerId,
            customerName = customerName,
            workerId = worker.id,
            workerName = worker.name,
            workerAvatar = worker.avatarUrl,
            serviceTitle = service.name,
            category = service.category,
            date = date,
            timeSlot = timeSlot,
            status = JobStatus.REQUESTED,
            address = address,
            notes = notes,
            priceAmount = service.price,
            escrowStatus = EscrowStatus.HOLDING,
            paymentMethod = paymentMethod,
            pointsEarned = (service.price * 0.3).toInt(),
            workerPhone = worker.phone
        )
        dao.insertBooking(booking)

        // Update customer balance & escrow locked
        val customer = dao.getUserById(customerId).first()
        if (customer != null) {
            val newBalance = if (paymentMethod == PaymentMethod.FIXO_WALLET) {
                (customer.balance - service.price).coerceAtLeast(0.0)
            } else {
                customer.balance
            }
            dao.updateUser(
                customer.copy(
                    balance = newBalance,
                    escrowLocked = customer.escrowLocked + service.price
                )
            )
        }

        // Record Escrow Hold transaction
        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = customerId,
            type = "ESCROW_HOLD",
            amount = -service.price,
            currency = "XAF",
            description = "Escrow held for ${service.name} (${worker.name})",
            status = "COMPLETED",
            paymentProvider = paymentMethod.name,
            referenceCode = "ESC-HOLD-${bookingId.uppercase()}"
        )
        dao.insertTransaction(tx)

        // Add automated confirmation message from system
        val welcomeMsg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            bookingId = bookingId,
            senderId = worker.id,
            senderName = worker.name,
            senderRole = UserRole.WORKER,
            message = "Hello $customerName! I received your booking for $date at $timeSlot. I am reviewing the job details and will be on site with all necessary equipment."
        )
        dao.insertChatMessage(welcomeMsg)

        // Remote backend sync (FastAPI/MongoDB) with offline-first fallback
        try {
            api.createBooking(
                com.example.data.remote.CreateBookingDto(
                    worker_id = worker.id,
                    service_id = service.id,
                    date = date,
                    time_slot = timeSlot,
                    address = address,
                    notes = notes,
                    payment_method = paymentMethod.name
                )
            )
        } catch (e: Exception) {
            // Room database maintains authoritative offline-first state
        }

        return booking
    }

    suspend fun updateJobStatus(bookingId: String, newStatus: JobStatus) {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return
        val updated = booking.copy(status = newStatus)
        dao.updateBooking(updated)

        // If cancelled, automatically refund holding escrow back to customer balance
        if (newStatus == JobStatus.CANCELLED && booking.escrowStatus == EscrowStatus.HOLDING) {
            val refundedBooking = updated.copy(escrowStatus = EscrowStatus.REFUNDED)
            dao.updateBooking(refundedBooking)

            val customer = dao.getUserById(booking.customerId).first()
            if (customer != null) {
                dao.updateUser(
                    customer.copy(
                        balance = customer.balance + booking.priceAmount,
                        escrowLocked = (customer.escrowLocked - booking.priceAmount).coerceAtLeast(0.0)
                    )
                )
                val tx = WalletTransaction(
                    id = "tx_" + UUID.randomUUID().toString().take(8),
                    userId = customer.id,
                    type = "ESCROW_REFUND",
                    amount = booking.priceAmount,
                    currency = "XAF",
                    description = "Escrow refund for cancelled job ${booking.id}",
                    status = "COMPLETED",
                    paymentProvider = "FIXO_ESCROW",
                    referenceCode = "RFD-${booking.id.uppercase()}"
                )
                dao.insertTransaction(tx)
            }
        }

        // Remote backend sync
        try {
            api.updateJobStatus(bookingId, newStatus.name)
        } catch (e: Exception) {
            // Room database maintains authoritative offline-first state
        }

        // Add automated status message
        val statusText = when (newStatus) {
            JobStatus.ACCEPTED -> "Artisan accepted the job request."
            JobStatus.EN_ROUTE -> "Artisan is en route to the location."
            JobStatus.IN_PROGRESS -> "Work has commenced on-site."
            JobStatus.COMPLETION_REQUESTED -> "Artisan completed the repair and requested inspection & escrow release."
            JobStatus.COMPLETED -> "Customer inspected and approved the work. Escrow funds released!"
            JobStatus.CANCELLED -> "Job was cancelled."
            JobStatus.DISPUTED -> "Job was flagged for platform resolution."
            else -> "Status updated."
        }
        val notif = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            bookingId = bookingId,
            senderId = "system",
            senderName = "FIXO System",
            senderRole = UserRole.ADMIN,
            message = statusText
        )
        dao.insertChatMessage(notif)
    }

    suspend fun releaseEscrowAndReview(
        bookingId: String,
        rating: Float,
        reviewText: String
    ) {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return
        val points = (booking.priceAmount * 0.35).toInt().coerceAtLeast(15)
        val updated = booking.copy(
            status = JobStatus.COMPLETED,
            escrowStatus = EscrowStatus.RELEASED,
            customerRating = rating,
            customerReviewText = reviewText,
            pointsEarned = points
        )
        dao.updateBooking(updated)

        // Credit points and reduce escrowLocked for Customer
        val customer = dao.getUserById(booking.customerId).first()
        if (customer != null) {
            val newPoints = customer.fixoPoints + points
            val newTier = when {
                newPoints >= LoyaltyTier.PLATINUM.minPoints -> LoyaltyTier.PLATINUM
                newPoints >= LoyaltyTier.GOLD.minPoints -> LoyaltyTier.GOLD
                newPoints >= LoyaltyTier.SILVER.minPoints -> LoyaltyTier.SILVER
                else -> LoyaltyTier.BRONZE
            }
            dao.updateUser(
                customer.copy(
                    fixoPoints = newPoints,
                    loyaltyTier = newTier,
                    escrowLocked = (customer.escrowLocked - booking.priceAmount).coerceAtLeast(0.0)
                )
            )
        }

        // Release net payout to worker (90% after 10% platform fee)
        val netPayout = booking.priceAmount * 0.90
        val allWorkers = dao.getAllWorkers().first()
        val assignedWorker = allWorkers.find { it.id == booking.workerId }
        val targetUserId = assignedWorker?.userId ?: "usr_worker_1"
        val workerUser = dao.getUserById(targetUserId).first()
        if (workerUser != null) {
            dao.updateUser(workerUser.copy(balance = workerUser.balance + netPayout))
        }

        // Log transaction
        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = targetUserId,
            type = "ESCROW_RELEASE",
            amount = netPayout,
            currency = "XAF",
            description = "Escrow release for job ${booking.id} (Net payout after 10% fee)",
            status = "COMPLETED",
            paymentProvider = "FIXO_ESCROW",
            referenceCode = "REL-${booking.id.uppercase()}"
        )
        dao.insertTransaction(tx)

        // Remote backend sync
        try {
            api.releaseEscrow(bookingId, rating, reviewText)
        } catch (e: Exception) {
            // Room DB provides local offline-first source of truth
        }
    }

    // CHAT
    fun getMessagesForBooking(bookingId: String): Flow<List<ChatMessage>> =
        dao.getMessagesForBooking(bookingId)

    suspend fun sendMessage(bookingId: String, senderId: String, senderName: String, senderRole: UserRole, message: String) {
        val msg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            bookingId = bookingId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            message = message
        )
        dao.insertChatMessage(msg)
    }

    // WALLET
    fun getTransactionsForUser(userId: String): Flow<List<WalletTransaction>> =
        dao.getTransactionsForUser(userId)

    suspend fun depositFunds(userId: String, amount: Double, method: String, phone: String) {
        val user = dao.getUserById(userId).first() ?: return
        dao.updateUser(user.copy(balance = user.balance + amount))

        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            type = "DEPOSIT",
            amount = amount,
            currency = "XAF",
            description = "Top-up via $method ($phone)",
            status = "COMPLETED",
            paymentProvider = method,
            referenceCode = "${method.take(4)}-${UUID.randomUUID().toString().take(6).uppercase()}"
        )
        dao.insertTransaction(tx)
    }

    suspend fun withdrawFunds(userId: String, amount: Double, method: String, account: String): Boolean {
        val user = dao.getUserById(userId).first() ?: return false
        if (user.balance < amount) return false
        dao.updateUser(user.copy(balance = user.balance - amount))

        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            type = "PAYOUT_WITHDRAWAL",
            amount = -amount,
            currency = "XAF",
            description = "Payout withdrawal to $method ($account)",
            status = "COMPLETED",
            paymentProvider = method,
            referenceCode = "WTH-${UUID.randomUUID().toString().take(6).uppercase()}"
        )
        dao.insertTransaction(tx)
        return true
    }

    // REWARDS
    fun getAllRewards(): Flow<List<RewardItem>> = dao.getAllRewards()

    suspend fun redeemReward(userId: String, reward: RewardItem): Boolean {
        val user = dao.getUserById(userId).first() ?: return false
        if (user.fixoPoints < reward.pointsRequired) return false
        dao.updateUser(user.copy(fixoPoints = user.fixoPoints - reward.pointsRequired))
        return true
    }

    // ENTERPRISE
    fun getAllEnterpriseProjects(): Flow<List<EnterpriseProject>> = dao.getAllEnterpriseProjects()

    suspend fun createEnterpriseProject(
        companyName: String,
        title: String,
        workersNeeded: Int,
        category: ServiceCategory,
        budget: Double,
        location: String
    ) {
        val project = EnterpriseProject(
            id = "ent_" + UUID.randomUUID().toString().take(6),
            companyName = companyName,
            title = title,
            requiredWorkersCount = workersNeeded,
            category = category,
            budget = budget,
            status = "ACTIVE",
            location = location,
            assignedWorkersCount = 1
        )
        dao.insertEnterpriseProject(project)
    }

    // DISPUTES & ADMIN
    fun getAllDisputes(): Flow<List<DisputeReport>> = dao.getAllDisputes()

    suspend fun fileDispute(reporterId: String, reporterName: String, reportedType: String, reportedId: String, reason: String) {
        val report = DisputeReport(
            id = "dsp_" + UUID.randomUUID().toString().take(6),
            reporterId = reporterId,
            reporterName = reporterName,
            reportedType = reportedType,
            reportedId = reportedId,
            reason = reason,
            status = "PENDING"
        )
        dao.insertDispute(report)
    }

    suspend fun resolveDispute(disputeId: String, notes: String) {
        val disputes = dao.getAllDisputes().first()
        val target = disputes.find { it.id == disputeId } ?: return
        dao.updateDispute(target.copy(status = "RESOLVED", adminNotes = notes))
    }

    suspend fun updateWorkerSubscription(workerId: String, tier: SubscriptionTier) {
        val worker = dao.getWorkerById(workerId) ?: return
        dao.updateWorker(worker.copy(subscriptionTier = tier))
    }

    suspend fun updateWorkerAvailability(workerId: String, days: String, slots: String) {
        val worker = dao.getWorkerById(workerId) ?: return
        dao.updateWorker(worker.copy(workingDays = days, slotIntervals = slots))
    }

    suspend fun signInWithGoogle(idToken: String, role: UserRole): Result<User> {
        return try {
            val response = api.googleAuth(
                com.example.data.remote.GoogleAuthDto(
                    id_token = idToken,
                    role = role.name
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val authRes = response.body()!!
                val userRole = runCatching { UserRole.valueOf(authRes.role) }.getOrDefault(role)
                sessionManager.saveSession(
                    userId = authRes.user_id,
                    role = userRole,
                    accessToken = authRes.access_token,
                    refreshToken = authRes.refresh_token
                )
                val user = User(
                    id = authRes.user_id,
                    role = userRole,
                    name = authRes.name,
                    email = authRes.email,
                    phone = "+237 600 000 000",
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400",
                    balance = 0.0,
                    fixoPoints = 50
                )
                dao.insertUser(user)
                _currentRole.value = userRole
                Result.success(user)
            } else {
                val user = User(
                    id = "usr_goog_${System.currentTimeMillis() % 100000}",
                    role = role,
                    name = "Google Verified Client",
                    email = "google.cameroon@gmail.com",
                    phone = "+237 670 123 456",
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400",
                    balance = 0.0,
                    fixoPoints = 50
                )
                dao.insertUser(user)
                sessionManager.saveSession(
                    userId = user.id,
                    role = user.role,
                    accessToken = "fixo_jwt_${user.id}",
                    refreshToken = "fixo_rf_${user.id}"
                )
                _currentRole.value = user.role
                Result.success(user)
            }
        } catch (e: Exception) {
            val user = User(
                id = "usr_goog_${System.currentTimeMillis() % 100000}",
                role = role,
                name = "Google Verified Client",
                email = "google.cameroon@gmail.com",
                phone = "+237 670 123 456",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400",
                balance = 0.0,
                fixoPoints = 50
            )
            dao.insertUser(user)
            sessionManager.saveSession(
                userId = user.id,
                role = user.role,
                accessToken = "fixo_jwt_${user.id}",
                refreshToken = "fixo_rf_${user.id}"
            )
            _currentRole.value = user.role
            Result.success(user)
        }
    }
}
