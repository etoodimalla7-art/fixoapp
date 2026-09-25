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
import com.example.data.model.isEnRoute
import com.example.data.model.isOnSite
import com.example.data.model.isWorking
import com.example.data.model.isCompletionPending
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
import com.example.data.model.WorkerLocation
import com.example.data.model.FixoNotification
import com.example.data.model.WorkerReview
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
import kotlin.math.roundToInt

class FixoRepository(context: Context) {
    private val database = FixoDatabase.getDatabase(context)
    val dao = database.fixoDao()
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
            FixoSeedData.defaultLocations.forEach { dao.insertWorkerLocation(it) }
            dao.insertNotifications(FixoSeedData.defaultNotifications)
            dao.insertWorkerReviews(FixoSeedData.defaultReviews)
            dao.insertOrganizations(FixoSeedData.defaultOrganizations)
            dao.insertWorkforceRequests(FixoSeedData.defaultWorkforceRequests)
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
        dao.clearWorkerLocations()
        dao.clearNotifications()
        dao.clearWorkerReviews()
        dao.clearOrganizations()
        dao.clearWorkforceRequests()

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
        FixoSeedData.defaultLocations.forEach { dao.insertWorkerLocation(it) }
        dao.insertNotifications(FixoSeedData.defaultNotifications)
        dao.insertWorkerReviews(FixoSeedData.defaultReviews)
        dao.insertOrganizations(FixoSeedData.defaultOrganizations)
        dao.insertWorkforceRequests(FixoSeedData.defaultWorkforceRequests)
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
        dao.clearWorkerLocations()
        dao.clearNotifications()
        dao.clearWorkerReviews()
        dao.clearOrganizations()
        dao.clearWorkforceRequests()
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

    suspend fun updateBooking(booking: Booking) {
        dao.updateBooking(booking)
    }

    suspend fun getBookingByIdDirect(bookingId: String): Booking? {
        return dao.getBookingByIdDirect(bookingId)
    }

    fun calculateHaversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (r * c * 10.0).roundToInt() / 10.0
    }

    fun calculateEtaMinutes(distanceKm: Double, averageSpeedKmh: Double = 25.0): Int {
        if (distanceKm <= 0.05) return 0
        val effectiveSpeed = if (averageSpeedKmh > 5.0) averageSpeedKmh else 25.0
        val hours = distanceKm / effectiveSpeed
        return Math.max(1, (hours * 60.0).roundToInt())
    }

    suspend fun startWorkerTrip(bookingId: String, currentLat: Double, currentLng: Double, statusToSet: JobStatus = JobStatus.ARTISAN_EN_ROUTE) {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return
        // State Machine validation
        if (booking.status != JobStatus.ACCEPTED && booking.status != JobStatus.SCHEDULED && 
            booking.status != JobStatus.REQUESTED && booking.status != JobStatus.DISPATCHED) {
            return
        }

        val distance = calculateHaversineDistanceKm(currentLat, currentLng, booking.customerLat, booking.customerLng)
        val eta = calculateEtaMinutes(distance, 26.5)

        val updated = booking.copy(
            status = statusToSet,
            workerLat = currentLat,
            workerLng = currentLng,
            trackingActive = true,
            distanceKm = distance,
            etaMinutes = eta,
            workerSpeedKmh = 26.5f,
            workerHeading = 35.0f,
            lastLocationUpdate = System.currentTimeMillis()
        )
        dao.updateBooking(updated)

        // Job-scoped worker location
        val loc = WorkerLocation(
            bookingId = booking.id,
            workerId = booking.workerId,
            latitude = currentLat,
            longitude = currentLng,
            speedKmh = 26.5f,
            heading = 35.0f,
            destinationLat = booking.customerLat,
            destinationLng = booking.customerLng,
            destinationAddress = booking.address,
            isTrackingActive = true,
            updatedAt = System.currentTimeMillis()
        )
        dao.insertWorkerLocation(loc)

        // Deliver notification to customer
        val notif = FixoNotification(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            userId = booking.customerId,
            title = "Artisan Started Trip",
            message = "${booking.workerName} has started heading to ${booking.address}. Estimated arrival in $eta mins.",
            type = "WORKER_STARTED_TRIP",
            bookingId = booking.id,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        dao.insertNotification(notif)

        // Add automated chat message
        val chatMsg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            bookingId = booking.id,
            senderId = booking.workerId,
            senderName = booking.workerName,
            senderRole = UserRole.WORKER,
            message = "I have started my trip to your address (${booking.address}). You can track my live GPS location on the tracking screen. ETA: $eta min."
        )
        dao.insertChatMessage(chatMsg)

        try {
            api.updateJobStatus(bookingId, statusToSet.name)
        } catch (_: Exception) {}
    }

    suspend fun updateWorkerLocation(bookingId: String, lat: Double, lng: Double, speedKmh: Float = 25f, heading: Float = 0f) {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return
        if (!booking.trackingActive || booking.status != JobStatus.ON_THE_WAY) return

        val distance = calculateHaversineDistanceKm(lat, lng, booking.customerLat, booking.customerLng)
        val eta = calculateEtaMinutes(distance, speedKmh.toDouble())

        val updated = booking.copy(
            workerLat = lat,
            workerLng = lng,
            distanceKm = distance,
            etaMinutes = eta,
            workerSpeedKmh = speedKmh,
            workerHeading = heading,
            lastLocationUpdate = System.currentTimeMillis()
        )
        dao.updateBooking(updated)

        val loc = WorkerLocation(
            bookingId = booking.id,
            workerId = booking.workerId,
            latitude = lat,
            longitude = lng,
            speedKmh = speedKmh,
            heading = heading,
            destinationLat = booking.customerLat,
            destinationLng = booking.customerLng,
            destinationAddress = booking.address,
            isTrackingActive = true,
            updatedAt = System.currentTimeMillis()
        )
        dao.insertWorkerLocation(loc)
    }

    suspend fun markWorkerArrived(bookingId: String, statusToSet: JobStatus = JobStatus.ON_SITE) {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return
        if (!booking.status.isEnRoute && booking.status != JobStatus.ACCEPTED && booking.status != JobStatus.REQUESTED && booking.status != JobStatus.DISPATCHED) return

        val targetStatus = if (statusToSet == JobStatus.ARRIVED) JobStatus.ARRIVED else JobStatus.ON_SITE
        val updated = booking.copy(
            status = targetStatus,
            trackingActive = false,
            distanceKm = 0.0,
            etaMinutes = 0,
            lastLocationUpdate = System.currentTimeMillis()
        )
        dao.updateBooking(updated)

        // Stop job-scoped location tracking
        dao.deleteWorkerLocation(booking.id)

        // Customer notification
        val notif = FixoNotification(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            userId = booking.customerId,
            title = "Artisan Has Arrived!",
            message = "${booking.workerName} has arrived at ${booking.address}.",
            type = "WORKER_ARRIVED",
            bookingId = booking.id,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        dao.insertNotification(notif)

        val chatMsg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            bookingId = booking.id,
            senderId = booking.workerId,
            senderName = booking.workerName,
            senderRole = UserRole.WORKER,
            message = "I have arrived at your destination address. Ready to inspect and begin work!"
        )
        dao.insertChatMessage(chatMsg)

        try {
            api.updateJobStatus(bookingId, targetStatus.name)
        } catch (_: Exception) {}
    }

    suspend fun submitInitialPhoto(bookingId: String, photoUrl: String, lat: Double = 4.0505, lng: Double = 9.6950): Boolean {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return false
        val updated = booking.copy(
            beforePhotoUrl = photoUrl,
            beforePhotoTimestamp = System.currentTimeMillis(),
            beforePhotoLat = lat,
            beforePhotoLng = lng
        )
        dao.updateBooking(updated)

        val chatMsg = ChatMessage(
            id = "msg_photo_before_" + UUID.randomUUID().toString().take(8),
            bookingId = booking.id,
            senderId = booking.workerId,
            senderName = booking.workerName,
            senderRole = UserRole.WORKER,
            message = "📷 Photo d'état initial (avant travaux) certifiée Fixo Shield.",
            attachmentUrl = photoUrl,
            attachmentType = "IMAGE"
        )
        dao.insertChatMessage(chatMsg)
        return true
    }

    suspend fun submitFinalPhoto(bookingId: String, photoUrl: String, lat: Double = 4.0505, lng: Double = 9.6950): Boolean {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return false
        val updated = booking.copy(
            afterPhotoUrl = photoUrl,
            afterPhotoTimestamp = System.currentTimeMillis(),
            afterPhotoLat = lat,
            afterPhotoLng = lng,
            workCompletedTimestamp = System.currentTimeMillis()
        )
        dao.updateBooking(updated)

        val chatMsg = ChatMessage(
            id = "msg_photo_after_" + UUID.randomUUID().toString().take(8),
            bookingId = booking.id,
            senderId = booking.workerId,
            senderName = booking.workerName,
            senderRole = UserRole.WORKER,
            message = "📷 Photo d'état final (après réparation) certifiée Fixo Shield.",
            attachmentUrl = photoUrl,
            attachmentType = "IMAGE"
        )
        dao.insertChatMessage(chatMsg)
        return true
    }

    suspend fun startWork(bookingId: String, requireBeforePhoto: Boolean = false, statusToSet: JobStatus = JobStatus.WORK_IN_PROGRESS): Boolean {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return false
        if (!booking.status.isOnSite) return false
        if (requireBeforePhoto && booking.beforePhotoUrl.isNullOrBlank()) {
            return false // Chronomètre bloqué tant que photo avant non fournie
        }

        val targetStatus = if (statusToSet == JobStatus.IN_PROGRESS) JobStatus.IN_PROGRESS else JobStatus.WORK_IN_PROGRESS
        val updated = booking.copy(
            status = targetStatus,
            workStartedTimestamp = System.currentTimeMillis()
        )
        dao.updateBooking(updated)

        val chatMsg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            bookingId = booking.id,
            senderId = booking.workerId,
            senderName = booking.workerName,
            senderRole = UserRole.WORKER,
            message = "Diagnostic completed. Repair work has officially begun."
        )
        dao.insertChatMessage(chatMsg)

        try {
            api.updateJobStatus(bookingId, targetStatus.name)
        } catch (_: Exception) {}
        return true
    }

    suspend fun requestJobCompletion(bookingId: String, requireAfterPhoto: Boolean = false, statusToSet: JobStatus = JobStatus.COMPLETED_PENDING_HANDSHAKE): Boolean {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return false
        if (!booking.status.isWorking) return false
        if (requireAfterPhoto && booking.afterPhotoUrl.isNullOrBlank()) {
            return false // Clôture bloquée sans photo après réparation
        }

        val targetStatus = if (statusToSet == JobStatus.COMPLETION_REQUESTED) JobStatus.COMPLETION_REQUESTED else JobStatus.COMPLETED_PENDING_HANDSHAKE
        val updated = booking.copy(
            status = targetStatus,
            workCompletedTimestamp = System.currentTimeMillis()
        )
        dao.updateBooking(updated)

        val notif = FixoNotification(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            userId = booking.customerId,
            title = "Work Complete — Inspection Ready",
            message = "${booking.workerName} has finished the work. Please inspect and approve escrow release.",
            type = "REVIEW_REQUEST",
            bookingId = booking.id,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        dao.insertNotification(notif)

        val chatMsg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            bookingId = booking.id,
            senderId = booking.workerId,
            senderName = booking.workerName,
            senderRole = UserRole.WORKER,
            message = "Work has been finished! Please inspect the repair and tap 'Review & Release Escrow' on the tracking screen."
        )
        dao.insertChatMessage(chatMsg)

        try {
            api.updateJobStatus(bookingId, targetStatus.name)
        } catch (_: Exception) {}
        return true
    }

    suspend fun simulateArrivalAndPhotos(bookingId: String) {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return

        // 1. Mark arrived / ON_SITE
        val onSiteBooking = booking.copy(
            status = JobStatus.ON_SITE,
            trackingActive = false,
            distanceKm = 0.0,
            etaMinutes = 0
        )
        dao.updateBooking(onSiteBooking)

        // 2. Submit initial photo
        val beforePhotoUrl = "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=600&auto=format&fit=crop"
        submitInitialPhoto(bookingId, beforePhotoUrl)

        // 3. Start work / WORK_IN_PROGRESS
        startWork(bookingId, requireBeforePhoto = true, statusToSet = JobStatus.WORK_IN_PROGRESS)

        // 4. Submit final photo
        val afterPhotoUrl = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600&auto=format&fit=crop"
        submitFinalPhoto(bookingId, afterPhotoUrl)
    }

    suspend fun sendWorkroomChatMessage(
        bookingId: String,
        senderId: String,
        senderName: String,
        senderRole: UserRole,
        text: String,
        attachmentUrl: String? = null,
        attachmentType: String? = null,
        voiceDurationSeconds: Int? = null
    ) {
        val chatMsg = ChatMessage(
            id = "msg_wr_" + UUID.randomUUID().toString().take(8),
            bookingId = bookingId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            message = text,
            attachmentUrl = attachmentUrl,
            attachmentType = attachmentType,
            voiceDurationSeconds = voiceDurationSeconds
        )
        dao.insertChatMessage(chatMsg)
    }

    suspend fun updateJobStatus(bookingId: String, newStatus: JobStatus) {
        val booking = dao.getBookingByIdDirect(bookingId) ?: return

        // Route to specialized lifecycle transitions
        when (newStatus) {
            JobStatus.ON_THE_WAY -> {
                startWorkerTrip(bookingId, booking.workerLat, booking.workerLng, JobStatus.ON_THE_WAY)
                return
            }
            JobStatus.ARTISAN_EN_ROUTE -> {
                startWorkerTrip(bookingId, booking.workerLat, booking.workerLng, JobStatus.ARTISAN_EN_ROUTE)
                return
            }
            JobStatus.ARRIVED -> {
                markWorkerArrived(bookingId, JobStatus.ARRIVED)
                return
            }
            JobStatus.ON_SITE -> {
                markWorkerArrived(bookingId, JobStatus.ON_SITE)
                return
            }
            JobStatus.IN_PROGRESS -> {
                startWork(bookingId, false, JobStatus.IN_PROGRESS)
                return
            }
            JobStatus.WORK_IN_PROGRESS -> {
                startWork(bookingId, false, JobStatus.WORK_IN_PROGRESS)
                return
            }
            JobStatus.COMPLETION_REQUESTED -> {
                requestJobCompletion(bookingId, false, JobStatus.COMPLETION_REQUESTED)
                return
            }
            JobStatus.COMPLETED_PENDING_HANDSHAKE -> {
                requestJobCompletion(bookingId, false, JobStatus.COMPLETED_PENDING_HANDSHAKE)
                return
            }
            else -> {}
        }

        val updated = booking.copy(status = newStatus)
        dao.updateBooking(updated)

        // If cancelled, automatically refund holding escrow back to customer balance
        if (newStatus == JobStatus.CANCELLED && booking.escrowStatus == EscrowStatus.HOLDING) {
            val refundedBooking = updated.copy(escrowStatus = EscrowStatus.REFUNDED, trackingActive = false)
            dao.updateBooking(refundedBooking)
            dao.deleteWorkerLocation(booking.id)

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
            JobStatus.SCHEDULED -> "Service appointment scheduled."
            JobStatus.ON_THE_WAY -> "Artisan is en route to site."
            JobStatus.ARRIVED -> "Artisan arrived on site."
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
            trackingActive = false,
            customerRating = rating,
            customerReviewText = reviewText,
            pointsEarned = points
        )
        dao.updateBooking(updated)
        dao.deleteWorkerLocation(booking.id)

        // Insert worker review
        val review = WorkerReview(
            id = "rev_" + UUID.randomUUID().toString().take(8),
            bookingId = booking.id,
            workerId = booking.workerId,
            customerId = booking.customerId,
            customerName = booking.customerName,
            rating = rating,
            comment = reviewText
        )
        dao.insertWorkerReview(review)

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

        // Notifications
        dao.insertNotification(
            FixoNotification(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                userId = targetUserId,
                title = "Payment Released!",
                message = "${com.example.data.model.formatFixoCurrency(netPayout)} credited to your wallet for job #${booking.id}.",
                type = "PAYMENT",
                bookingId = booking.id,
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            FixoNotification(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                userId = booking.customerId,
                title = "Job Completed",
                message = "Job #${booking.id} completed. +$points FIXO Points added to your account!",
                type = "JOB_COMPLETED",
                bookingId = booking.id,
                timestamp = System.currentTimeMillis()
            )
        )

        // Remote backend sync
        try {
            api.releaseEscrow(bookingId, rating, reviewText)
        } catch (e: Exception) {
            // Room DB provides local offline-first source of truth
        }
    }

    // JOB TRACKING & LOCATIONS
    fun getWorkerLocation(bookingId: String): Flow<WorkerLocation?> =
        dao.getWorkerLocation(bookingId)

    // NOTIFICATIONS
    fun getNotificationsForUser(userId: String): Flow<List<FixoNotification>> =
        dao.getNotificationsForUser(userId)

    fun getUnreadNotificationCount(userId: String): Flow<Int> =
        dao.getUnreadNotificationCount(userId)

    suspend fun markNotificationRead(id: String) = dao.markNotificationRead(id)

    suspend fun markAllNotificationsRead(userId: String) = dao.markAllNotificationsRead(userId)

    // REVIEWS
    fun getReviewsForWorker(workerId: String): Flow<List<WorkerReview>> =
        dao.getReviewsForWorker(workerId)

    // CHAT
    fun getMessagesForBooking(bookingId: String): Flow<List<ChatMessage>> =
        dao.getMessagesForBooking(bookingId)

    fun getAllMessages(): Flow<List<ChatMessage>> = dao.getAllMessages()

    suspend fun sendMessage(bookingId: String, senderId: String, senderName: String, senderRole: UserRole, message: String) {
        sendMessageWithAttachment(bookingId, senderId, senderName, senderRole, message, null, null)
    }

    suspend fun sendMessageWithAttachment(
        bookingId: String,
        senderId: String,
        senderName: String,
        senderRole: UserRole,
        message: String,
        attachmentUrl: String? = null,
        attachmentType: String? = null
    ) {
        val msg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            bookingId = bookingId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            attachmentUrl = attachmentUrl,
            attachmentType = attachmentType,
            deliveryState = "DELIVERED"
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
                val errBody = response.errorBody()?.string()
                val errorMsg = if (!errBody.isNullOrBlank()) {
                    errBody
                } else {
                    "Google authentication failed (HTTP ${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithBackend(email: String, password: String): Result<User> {
        return try {
            val response = api.login(com.example.data.remote.LoginDto(email = email, password = password))
            if (response.isSuccessful && response.body() != null) {
                val authRes = response.body()!!
                val userRole = runCatching { UserRole.valueOf(authRes.role) }.getOrDefault(UserRole.CUSTOMER)
                sessionManager.saveSession(
                    userId = authRes.user_id,
                    role = userRole,
                    accessToken = authRes.access_token,
                    refreshToken = authRes.refresh_token
                )
                val existingUser = dao.getUserById(authRes.user_id).first()
                val user = existingUser ?: User(
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
                val errBody = response.errorBody()?.string()
                val errorMsg = if (!errBody.isNullOrBlank()) errBody else "Authentication failed (HTTP ${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithBackend(
        name: String,
        email: String,
        password: String,
        phone: String,
        role: UserRole
    ): Result<User> {
        return try {
            val response = api.register(
                com.example.data.remote.RegisterDto(
                    name = name,
                    email = email,
                    password = password,
                    phone = phone,
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
                    phone = phone,
                    avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400",
                    balance = 0.0,
                    fixoPoints = 50
                )
                dao.insertUser(user)
                _currentRole.value = userRole
                Result.success(user)
            } else {
                val errBody = response.errorBody()?.string()
                val errorMsg = if (!errBody.isNullOrBlank()) errBody else "Registration failed (HTTP ${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ORGANIZATIONS & WORKFORCE RECRUITMENT
    fun getAllOrganizations(): Flow<List<com.example.data.model.Organization>> = dao.getAllOrganizations()

    fun getOrganizationById(id: String): Flow<com.example.data.model.Organization?> = dao.getOrganizationById(id)

    suspend fun insertOrganization(org: com.example.data.model.Organization) = dao.insertOrganization(org)

    fun getAllWorkforceRequests(): Flow<List<com.example.data.model.WorkforceRequest>> = dao.getAllWorkforceRequests()

    fun getWorkforceRequestsForOrg(orgId: String): Flow<List<com.example.data.model.WorkforceRequest>> =
        dao.getWorkforceRequestsForOrg(orgId)

    suspend fun createWorkforceRequest(request: com.example.data.model.WorkforceRequest) =
        dao.insertWorkforceRequest(request)

    suspend fun applyForWorkforceRequest(requestId: String, workerId: String) {
        val requests = dao.getAllWorkforceRequests().first()
        val target = requests.find { it.id == requestId } ?: return
        dao.updateWorkforceRequest(target.copy(recruitedCount = target.recruitedCount + 1))
        dao.insertNotification(
            FixoNotification(
                id = "notif_" + UUID.randomUUID().toString().take(8),
                userId = target.organizationId,
                title = "Artisan Applied for Workforce Request",
                message = "A verified artisan has applied to your workforce listing: ${target.projectTitle}.",
                type = "WORKFORCE_APPLICATION",
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        )
    }

    suspend fun insertNotification(notification: FixoNotification) = dao.insertNotification(notification)

    // USERNAME & REGISTRATION ARCHITECTURE
    suspend fun isUsernameAvailable(username: String): Boolean {
        val clean = username.trim().lowercase().removePrefix("@")
        if (clean.length < 3) return false
        // Reserved handles
        val reserved = listOf("admin", "fixo", "support", "help", "root", "system", "moderator")
        if (clean in reserved) return false
        val allUsers = dao.getUserByRole(UserRole.CUSTOMER).first() // Quick check
        // Check in seed and db
        val inSeed = FixoSeedData.defaultUsers.any { it.username.equals(clean, ignoreCase = true) }
        return !inSeed
    }

    suspend fun registerCustomer(
        name: String,
        username: String,
        phone: String,
        email: String,
        region: String,
        city: String,
        quarter: String
    ): User {
        val userId = "usr_cust_" + UUID.randomUUID().toString().take(8)
        val cleanUsername = username.trim().removePrefix("@").ifBlank { "client_${System.currentTimeMillis() % 10000}" }
        val newUser = User(
            id = userId,
            role = UserRole.CUSTOMER,
            name = name.ifBlank { "Client FIXO" },
            email = email.ifBlank { "$cleanUsername@client.fixo.cm" },
            phone = phone.ifBlank { "+237 670 000 000" },
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400",
            rating = 5.0,
            balance = 0.0,
            escrowLocked = 0.0,
            fixoPoints = 50, // Welcome reward
            verificationStatus = VerificationStatus.VERIFIED_PRO,
            loyaltyTier = LoyaltyTier.BRONZE,
            username = cleanUsername,
            region = region,
            city = city,
            quarter = quarter,
            isPhoneVerified = true,
            isEmailVerified = false
        )
        dao.insertUser(newUser)
        sessionManager.saveSession(
            userId = newUser.id,
            role = newUser.role
        )
        _currentRole.value = UserRole.CUSTOMER
        return newUser
    }

    suspend fun registerWorker(
        name: String,
        username: String,
        phone: String,
        email: String,
        category: ServiceCategory,
        hourlyRate: Double,
        bio: String,
        serviceArea: String
    ): Pair<User, WorkerProfile> {
        val userId = "usr_wrk_" + UUID.randomUUID().toString().take(8)
        val workerId = "wrk_" + UUID.randomUUID().toString().take(8)
        val cleanUsername = username.trim().removePrefix("@").ifBlank { "artisan_${System.currentTimeMillis() % 10000}" }
        val newUser = User(
            id = userId,
            role = UserRole.WORKER,
            name = name.ifBlank { "Artisan FIXO" },
            email = email.ifBlank { "$cleanUsername@pro.fixo.cm" },
            phone = phone.ifBlank { "+237 690 000 000" },
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            rating = 5.0,
            balance = 0.0,
            escrowLocked = 0.0,
            fixoPoints = 100,
            verificationStatus = VerificationStatus.PENDING, // Strictly pending verification
            loyaltyTier = LoyaltyTier.SILVER,
            username = cleanUsername,
            region = "Littoral",
            city = "Douala",
            quarter = serviceArea.ifBlank { "Akwa" },
            isPhoneVerified = true,
            isEmailVerified = false
        )
        val newWorker = WorkerProfile(
            id = workerId,
            userId = userId,
            name = newUser.name,
            category = category,
            hourlyRate = if (hourlyRate > 0) hourlyRate else 15000.0,
            emergencyCalloutAvailable = true,
            bio = bio.ifBlank { "Professional craftsman in Cameroon offering certified diagnostic and repair services." },
            skills = "${category.displayName}, Diagnostic, Troubleshooting, Installation",
            certifications = "Vocational Trade Diploma, FIXO ID Verified",
            completedJobs = 0,
            rating = 5.0,
            reviewCount = 0,
            subscriptionTier = SubscriptionTier.PRO,
            avatarUrl = newUser.avatarUrl,
            locationCity = "Douala",
            locationDistanceKm = 1.2,
            backgroundVerified = false,
            phone = newUser.phone
        )
        dao.insertUser(newUser)
        dao.insertWorkers(listOf(newWorker))
        // Create initial default service for this worker
        dao.insertService(
            ServiceItem(
                id = "srv_" + UUID.randomUUID().toString().take(6),
                workerId = workerId,
                name = "${category.displayName} Inspection & Diagnostic",
                category = category,
                description = "On-site comprehensive inspection, diagnostic report, and initial estimate.",
                price = newWorker.hourlyRate,
                durationEstimateMinutes = 60
            )
        )
        sessionManager.saveSession(
            userId = newUser.id,
            role = newUser.role
        )
        _currentRole.value = UserRole.WORKER
        return Pair(newUser, newWorker)
    }

    suspend fun registerOrganization(
        name: String,
        type: String,
        description: String,
        phone: String,
        email: String,
        address: String,
        city: String,
        region: String,
        regNumber: String,
        repName: String,
        repTitle: String
    ): Pair<User, com.example.data.model.Organization> {
        val userId = "usr_org_" + UUID.randomUUID().toString().take(8)
        val orgId = "org_" + UUID.randomUUID().toString().take(8)
        val cleanName = name.ifBlank { "Entreprise FIXO" }
        val newUser = User(
            id = userId,
            role = UserRole.ENTERPRISE,
            name = repName.ifBlank { "Directeur Général" },
            email = email.ifBlank { "contact@enterprise.cm" },
            phone = phone.ifBlank { "+237 670 112 233" },
            avatarUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb186156f?w=400",
            rating = 5.0,
            balance = 0.0,
            escrowLocked = 0.0,
            fixoPoints = 250,
            verificationStatus = VerificationStatus.PENDING,
            username = cleanName.lowercase().replace(" ", "_").take(15),
            region = region,
            city = city,
            quarter = address,
            isPhoneVerified = true,
            isEmailVerified = false,
            organizationId = orgId
        )
        val newOrg = com.example.data.model.Organization(
            id = orgId,
            name = cleanName,
            type = type.ifBlank { "Construction & Multi-Trade" },
            description = description.ifBlank { "Registered enterprise providing certified multi-trade engineering, construction, and facility maintenance in Cameroon." },
            logoUrl = newUser.avatarUrl,
            phone = phone,
            email = email,
            address = address,
            city = city,
            region = region,
            registrationNumber = regNumber.ifBlank { "RC/DLA/2024/B/1000 - NIU M01240001000P" },
            authorizedRepresentative = repName,
            representativeTitle = repTitle.ifBlank { "Directeur Général" },
            verificationStatus = VerificationStatus.PENDING,
            rating = 5.0,
            completedProjectsCount = 0,
            activeWorkersCount = 5,
            servicesOffered = "General Contracting, Structural Works, Industrial Engineering"
        )
        dao.insertUser(newUser)
        dao.insertOrganization(newOrg)
        sessionManager.saveSession(
            userId = newUser.id,
            role = newUser.role
        )
        _currentRole.value = UserRole.ENTERPRISE
        return Pair(newUser, newOrg)
    }
}
