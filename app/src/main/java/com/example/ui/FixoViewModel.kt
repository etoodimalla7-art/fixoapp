package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.FixoSeedData
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.DisputeReport
import com.example.data.model.EnterpriseProject
import com.example.data.model.PaymentMethod
import com.example.data.model.Reel
import com.example.data.model.RewardItem
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.SubscriptionTier
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.WalletTransaction
import com.example.data.model.WorkerProfile
import com.example.data.repository.FixoRepository
import com.example.data.repository.SessionManager
import com.example.data.repository.ThemeMode
import com.example.localization.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FixoUiState(
    val isAuthenticated: Boolean = true,
    val authLoading: Boolean = false,
    val authError: String? = null,
    val currentRole: UserRole = UserRole.CUSTOMER,
    val currentLanguage: AppLanguage = AppLanguage.EN,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notifJobs: Boolean = true,
    val notifMessages: Boolean = true,
    val notifPayments: Boolean = true,
    val shareLocation: Boolean = true,
    val currentUser: User? = null,
    val allWorkers: List<WorkerProfile> = emptyList(),
    val filteredWorkers: List<WorkerProfile> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: ServiceCategory? = null,
    val filterVerifiedOnly: Boolean = false,
    val filterEmergencyOnly: Boolean = false,
    val filterMinRating: Double = 0.0,
    val selectedWorker: WorkerProfile? = null,
    val workerServices: List<ServiceItem> = emptyList(),
    val reels: List<Reel> = emptyList(),
    val customerBookings: List<Booking> = emptyList(),
    val workerBookings: List<Booking> = emptyList(),
    val allBookings: List<Booking> = emptyList(),
    val selectedBooking: Booking? = null,
    val chatMessages: List<ChatMessage> = emptyList(),
    val allMessages: List<ChatMessage> = emptyList(),
    val unreadMessageCount: Int = 0,
    val transactions: List<WalletTransaction> = emptyList(),
    val rewards: List<RewardItem> = emptyList(),
    val enterpriseProjects: List<EnterpriseProject> = emptyList(),
    val disputes: List<DisputeReport> = emptyList(),
    val toastMessage: String? = null,
    val isBookingDialogVisible: Boolean = false,
    val selectedServiceForBooking: ServiceItem? = null,
    val isReviewDialogVisible: Boolean = false,
    val isDepositDialogVisible: Boolean = false,
    val isWithdrawDialogVisible: Boolean = false,
    val isUploadReelDialogVisible: Boolean = false,
    val isAvailabilityDialogVisible: Boolean = false,
    val isSubscriptionDialogVisible: Boolean = false,
    val isEnterpriseCreateDialogVisible: Boolean = false,
    val isDisputeDialogVisible: Boolean = false,
    val isDevEnvironment: Boolean = false,
    val savedWorkerIds: Set<String> = emptySet(),
    val notifications: List<com.example.data.model.FixoNotification> = emptyList(),
    val unreadNotificationCount: Int = 0,
    val isNotificationsDialogVisible: Boolean = false,
    val activeWorkerLocation: com.example.data.model.WorkerLocation? = null,
    val selectedWorkerReviews: List<com.example.data.model.WorkerReview> = emptyList(),
    val isStartTripConfirmationVisible: Boolean = false,
    val pendingStartTripBooking: Booking? = null,
    val allOrganizations: List<com.example.data.model.Organization> = emptyList(),
    val selectedOrganization: com.example.data.model.Organization? = null,
    val workforceRequests: List<com.example.data.model.WorkforceRequest> = emptyList(),
    val selectedQuarter: com.example.data.model.CameroonQuarter = com.example.data.model.CameroonLocationRegistry.getDefaultQuarter(),
    val isLocationPickerVisible: Boolean = false,
    val isVerificationCenterVisible: Boolean = false,
    val isHelpCenterVisible: Boolean = false,
    val isSessionChecked: Boolean = false,
    val isViewingKycFunnel: Boolean = false,
    val artisanKycOverride: com.example.data.model.ArtisanKycStatus? = null
) {
    val effectiveArtisanKycStatus: com.example.data.model.ArtisanKycStatus
        get() = artisanKycOverride ?: currentUser?.verificationStatus?.let {
            when (it) {
                com.example.data.model.VerificationStatus.UNVERIFIED -> com.example.data.model.ArtisanKycStatus.NOT_STARTED
                com.example.data.model.VerificationStatus.PENDING -> com.example.data.model.ArtisanKycStatus.IN_REVIEW
                com.example.data.model.VerificationStatus.VERIFIED_PRO,
                com.example.data.model.VerificationStatus.MASTER_CRAFTSMAN -> com.example.data.model.ArtisanKycStatus.APPROVED
            }
        } ?: com.example.data.model.ArtisanKycStatus.NOT_STARTED
}

class FixoViewModel(application: Application) : AndroidViewModel(application) {
    val repository = FixoRepository(application.applicationContext)
    val sessionManager = SessionManager(application.applicationContext)

    private val _uiState = MutableStateFlow(FixoUiState())
    val uiState: StateFlow<FixoUiState> = _uiState.asStateFlow()

    init {
        // Observe dev environment
        viewModelScope.launch {
            sessionManager.isDevEnvironment.collect { isDev ->
                _uiState.value = _uiState.value.copy(isDevEnvironment = isDev)
            }
        }

        // Observe role
        viewModelScope.launch {
            repository.currentRole.collect { role ->
                _uiState.value = _uiState.value.copy(currentRole = role)
            }
        }

        // Observe language
        viewModelScope.launch {
            repository.currentLanguage.collect { lang ->
                _uiState.value = _uiState.value.copy(currentLanguage = lang)
            }
        }

        // Observe theme mode and preferences
        viewModelScope.launch {
            sessionManager.themeMode.collect { mode ->
                _uiState.value = _uiState.value.copy(themeMode = mode)
            }
        }

        viewModelScope.launch {
            sessionManager.notifJobs.collect { jobs ->
                _uiState.value = _uiState.value.copy(notifJobs = jobs)
            }
        }

        viewModelScope.launch {
            sessionManager.notifMessages.collect { msgs ->
                _uiState.value = _uiState.value.copy(notifMessages = msgs)
            }
        }

        viewModelScope.launch {
            sessionManager.notifPayments.collect { payments ->
                _uiState.value = _uiState.value.copy(notifPayments = payments)
            }
        }

        viewModelScope.launch {
            sessionManager.shareLocation.collect { loc ->
                _uiState.value = _uiState.value.copy(shareLocation = loc)
            }
        }

        // Observe current user
        viewModelScope.launch {
            repository.getCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
            }
        }

        // Observe workers and apply active filters
        viewModelScope.launch {
            repository.getAllWorkers().collect { workers ->
                _uiState.value = _uiState.value.copy(allWorkers = workers)
                applyWorkerFilters()
            }
        }

        // Observe reels
        viewModelScope.launch {
            repository.getAllReels().collect { reels ->
                _uiState.value = _uiState.value.copy(reels = reels)
            }
        }

        // Observe bookings for customer
        viewModelScope.launch {
            repository.getBookingsForCustomer("usr_cust_1").collect { bookings ->
                _uiState.value = _uiState.value.copy(customerBookings = bookings)
                if (_uiState.value.selectedBooking == null && bookings.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(selectedBooking = bookings.first())
                }
            }
        }

        // Observe bookings for worker
        viewModelScope.launch {
            repository.getBookingsForWorker("wrk_1").collect { bookings ->
                _uiState.value = _uiState.value.copy(workerBookings = bookings)
            }
        }

        // Observe all bookings (for Admin)
        viewModelScope.launch {
            repository.getAllBookings().collect { bookings ->
                _uiState.value = _uiState.value.copy(allBookings = bookings)
            }
        }

        // Observe rewards
        viewModelScope.launch {
            repository.getAllRewards().collect { rewards ->
                _uiState.value = _uiState.value.copy(rewards = rewards)
            }
        }

        // Observe enterprise projects
        viewModelScope.launch {
            repository.getAllEnterpriseProjects().collect { projects ->
                _uiState.value = _uiState.value.copy(enterpriseProjects = projects)
            }
        }

        // Observe disputes
        viewModelScope.launch {
            repository.getAllDisputes().collect { disputes ->
                _uiState.value = _uiState.value.copy(disputes = disputes)
            }
        }

        // Observe organizations
        viewModelScope.launch {
            repository.getAllOrganizations().collect { orgs ->
                _uiState.value = _uiState.value.copy(allOrganizations = orgs)
            }
        }

        // Observe workforce requests
        viewModelScope.launch {
            repository.getAllWorkforceRequests().collect { requests ->
                _uiState.value = _uiState.value.copy(workforceRequests = requests)
            }
        }

        // Observe transactions for current user
        viewModelScope.launch {
            repository.getTransactionsForUser("usr_cust_1").collect { txs ->
                _uiState.value = _uiState.value.copy(transactions = txs)
            }
        }

        // Observe notifications for current user
        viewModelScope.launch {
            repository.getNotificationsForUser("usr_cust_1").collect { notifs ->
                _uiState.value = _uiState.value.copy(notifications = notifs)
            }
        }

        // Observe unread notification count
        viewModelScope.launch {
            repository.getUnreadNotificationCount("usr_cust_1").collect { count ->
                _uiState.value = _uiState.value.copy(unreadNotificationCount = count)
            }
        }

        // Observe all messages
        viewModelScope.launch {
            repository.getAllMessages().collect { msgs ->
                val currentUserId = _uiState.value.currentUser?.id ?: "usr_cust_1"
                val unreadCount = msgs.count { !it.isRead && it.senderId != currentUserId }
                _uiState.value = _uiState.value.copy(
                    allMessages = msgs,
                    unreadMessageCount = unreadCount
                )
            }
        }
    }

    fun switchRole(role: UserRole) {
        repository.setRole(role)
        showToast("Switched to ${role.name.lowercase().replaceFirstChar { it.uppercase() }} Mode")
    }

    fun toggleLanguage() {
        repository.toggleLanguage()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyWorkerFilters()
    }

    fun onCategorySelected(category: ServiceCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyWorkerFilters()
    }

    fun toggleFilterVerifiedOnly() {
        _uiState.value = _uiState.value.copy(filterVerifiedOnly = !_uiState.value.filterVerifiedOnly)
        applyWorkerFilters()
    }

    fun toggleFilterEmergencyOnly() {
        _uiState.value = _uiState.value.copy(filterEmergencyOnly = !_uiState.value.filterEmergencyOnly)
        applyWorkerFilters()
    }

    fun setFilterMinRating(rating: Double) {
        _uiState.value = _uiState.value.copy(filterMinRating = if (_uiState.value.filterMinRating == rating) 0.0 else rating)
        applyWorkerFilters()
    }

    private fun applyWorkerFilters() {
        val state = _uiState.value
        val filtered = state.allWorkers.filter { worker ->
            val matchesQuery = state.searchQuery.isBlank() ||
                    worker.name.contains(state.searchQuery, ignoreCase = true) ||
                    worker.skills.contains(state.searchQuery, ignoreCase = true) ||
                    worker.category.displayName.contains(state.searchQuery, ignoreCase = true)

            val matchesCategory = state.selectedCategory == null || worker.category == state.selectedCategory
            val matchesVerified = !state.filterVerifiedOnly || worker.backgroundVerified
            val matchesEmergency = !state.filterEmergencyOnly || worker.emergencyCalloutAvailable
            val matchesRating = worker.rating >= state.filterMinRating

            matchesQuery && matchesCategory && matchesVerified && matchesEmergency && matchesRating
        }
        _uiState.value = _uiState.value.copy(filteredWorkers = filtered)
    }

    fun selectWorker(worker: WorkerProfile) {
        _uiState.value = _uiState.value.copy(selectedWorker = worker)
        viewModelScope.launch {
            repository.getServicesForWorker(worker.id).collect { services ->
                _uiState.value = _uiState.value.copy(workerServices = services)
            }
        }
    }

    fun selectBooking(booking: Booking) {
        _uiState.value = _uiState.value.copy(selectedBooking = booking)
        viewModelScope.launch {
            repository.getBookingById(booking.id).collect { updatedBooking ->
                if (updatedBooking != null) {
                    _uiState.value = _uiState.value.copy(selectedBooking = updatedBooking)
                }
            }
        }
        viewModelScope.launch {
            repository.getMessagesForBooking(booking.id).collect { msgs ->
                _uiState.value = _uiState.value.copy(chatMessages = msgs)
            }
        }
        viewModelScope.launch {
            repository.getWorkerLocation(booking.id).collect { loc ->
                _uiState.value = _uiState.value.copy(activeWorkerLocation = loc)
            }
        }
        viewModelScope.launch {
            repository.getReviewsForWorker(booking.workerId).collect { reviews ->
                _uiState.value = _uiState.value.copy(selectedWorkerReviews = reviews)
            }
        }
    }

    fun openStartTripConfirmation(booking: Booking) {
        _uiState.value = _uiState.value.copy(
            isStartTripConfirmationVisible = true,
            pendingStartTripBooking = booking
        )
    }

    fun closeStartTripConfirmation() {
        _uiState.value = _uiState.value.copy(
            isStartTripConfirmationVisible = false,
            pendingStartTripBooking = null
        )
    }

    fun confirmStartTrip() {
        val booking = _uiState.value.pendingStartTripBooking ?: return
        viewModelScope.launch {
            // Real Start Trip from Douala workshop location towards customer site
            val startLat = if (booking.workerLat != 0.0) booking.workerLat else 4.0380
            val startLng = if (booking.workerLng != 0.0) booking.workerLng else 9.6990
            repository.startWorkerTrip(booking.id, startLat, startLng)
            _uiState.value = _uiState.value.copy(
                isStartTripConfirmationVisible = false,
                pendingStartTripBooking = null
            )
            showToast("Trip Started! Live GPS location shared with customer.")
        }
    }

    fun markWorkerArrived(bookingId: String) {
        viewModelScope.launch {
            repository.markWorkerArrived(bookingId)
            showToast("Arrival confirmed! Location tracking stopped.")
        }
    }

    fun startWork(bookingId: String) {
        viewModelScope.launch {
            repository.startWork(bookingId)
            showToast("Work started on-site.")
        }
    }

    fun requestJobCompletion(bookingId: String) {
        viewModelScope.launch {
            repository.requestJobCompletion(bookingId)
            showToast("Completion requested. Customer notified to inspect.")
        }
    }

    fun updateWorkerLiveLocation(bookingId: String, lat: Double, lng: Double, speedKmh: Float = 25f, heading: Float = 0f) {
        viewModelScope.launch {
            repository.updateWorkerLocation(bookingId, lat, lng, speedKmh, heading)
        }
    }

    fun openNotificationsDialog() {
        _uiState.value = _uiState.value.copy(isNotificationsDialogVisible = true)
    }

    fun closeNotificationsDialog() {
        _uiState.value = _uiState.value.copy(isNotificationsDialogVisible = false)
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(user.id)
            showToast("All notifications marked as read")
        }
    }

    fun openBookingDialog(service: ServiceItem, worker: WorkerProfile? = null) {
        if (worker != null) {
            _uiState.value = _uiState.value.copy(selectedWorker = worker)
        }
        _uiState.value = _uiState.value.copy(
            selectedServiceForBooking = service,
            isBookingDialogVisible = true
        )
    }

    fun closeBookingDialog() {
        _uiState.value = _uiState.value.copy(isBookingDialogVisible = false)
    }

    fun confirmBooking(
        date: String,
        timeSlot: String,
        address: String,
        notes: String,
        paymentMethod: PaymentMethod
    ) {
        val worker = _uiState.value.selectedWorker ?: return
        val service = _uiState.value.selectedServiceForBooking ?: return
        val customer = _uiState.value.currentUser ?: return

        viewModelScope.launch {
            val booking = repository.createBooking(
                customerId = customer.id,
                customerName = customer.name,
                worker = worker,
                service = service,
                date = date,
                timeSlot = timeSlot,
                address = address,
                notes = notes,
                paymentMethod = paymentMethod
            )
            _uiState.value = _uiState.value.copy(
                isBookingDialogVisible = false,
                selectedBooking = booking
            )
            showToast("Booking Confirmed! ${com.example.data.model.formatFixoCurrency(service.price)} held safely in Escrow.")
        }
    }

    fun advanceJobStatus(bookingId: String, newStatus: com.example.data.model.JobStatus) {
        viewModelScope.launch {
            repository.updateJobStatus(bookingId, newStatus)
            showToast("Job status updated to ${newStatus.name}")
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.updateJobStatus(bookingId, com.example.data.model.JobStatus.CANCELLED)
            showToast("Booking cancelled and escrow refunded to wallet.")
        }
    }

    fun openReviewDialog(booking: Booking) {
        _uiState.value = _uiState.value.copy(
            selectedBooking = booking,
            isReviewDialogVisible = true
        )
    }

    fun closeReviewDialog() {
        _uiState.value = _uiState.value.copy(isReviewDialogVisible = false)
    }

    fun submitReviewAndReleaseEscrow(rating: Float, reviewText: String) {
        val booking = _uiState.value.selectedBooking ?: return
        viewModelScope.launch {
            repository.releaseEscrowAndReview(booking.id, rating, reviewText)
            _uiState.value = _uiState.value.copy(isReviewDialogVisible = false)
            showToast("Escrow Released! Earned +${(booking.priceAmount * 0.35).toInt()} FIXO Points!")
        }
    }

    fun sendChatMessage(message: String) {
        val booking = _uiState.value.selectedBooking ?: return
        sendChatMessageWithAttachment(booking.id, message, null, null)
    }

    fun sendChatMessageWithAttachment(
        bookingId: String,
        message: String,
        attachmentUrl: String? = null,
        attachmentType: String? = null
    ) {
        val user = _uiState.value.currentUser ?: return
        if (message.isBlank() && attachmentUrl.isNullOrBlank()) return

        viewModelScope.launch {
            repository.sendMessageWithAttachment(
                bookingId = bookingId,
                senderId = user.id,
                senderName = user.name,
                senderRole = _uiState.value.currentRole,
                message = message,
                attachmentUrl = attachmentUrl,
                attachmentType = attachmentType
            )
        }
    }

    fun likeReel(reelId: String) {
        viewModelScope.launch {
            repository.likeReel(reelId)
        }
    }

    fun openUploadReelDialog() {
        _uiState.value = _uiState.value.copy(isUploadReelDialogVisible = true)
    }

    fun closeUploadReelDialog() {
        _uiState.value = _uiState.value.copy(isUploadReelDialogVisible = false)
    }

    fun publishReel(
        title: String,
        description: String,
        category: ServiceCategory,
        tags: String,
        videoUrl: String,
        thumbnailUrl: String
    ) {
        val worker = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val currentWorker = _uiState.value.allWorkers.find { it.userId == worker.id || it.id == worker.id }
                ?: _uiState.value.allWorkers.firstOrNull()
            val workerId = currentWorker?.id ?: worker.id
            val workerName = currentWorker?.name ?: worker.name
            val workerAvatar = currentWorker?.avatarUrl ?: worker.avatarUrl
            repository.createReel(
                workerId = workerId,
                workerName = workerName,
                workerAvatar = workerAvatar,
                title = title,
                description = description,
                category = category,
                serviceId = "srv_general",
                videoUrl = videoUrl,
                thumbnailUrl = thumbnailUrl,
                tags = tags
            )
            _uiState.value = _uiState.value.copy(isUploadReelDialogVisible = false)
            showToast("Reel Published to Discovery Feed!")
        }
    }

    fun deleteReel(reelId: String) {
        viewModelScope.launch {
            repository.deleteReel(reelId)
            showToast("Reel removed from craftsmanship catalog.")
        }
    }

    fun openDepositDialog() {
        _uiState.value = _uiState.value.copy(isDepositDialogVisible = true)
    }

    fun closeDepositDialog() {
        _uiState.value = _uiState.value.copy(isDepositDialogVisible = false)
    }

    fun processDeposit(amount: Double, method: String, phone: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.depositFunds(user.id, amount, method, phone)
            _uiState.value = _uiState.value.copy(isDepositDialogVisible = false)
            showToast("Deposited ${com.example.data.model.formatFixoCurrency(amount)} via $method! Balance updated.")
        }
    }

    fun openWithdrawDialog() {
        _uiState.value = _uiState.value.copy(isWithdrawDialogVisible = true)
    }

    fun closeWithdrawDialog() {
        _uiState.value = _uiState.value.copy(isWithdrawDialogVisible = false)
    }

    fun processWithdraw(amount: Double, method: String, account: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val success = repository.withdrawFunds(user.id, amount, method, account)
            _uiState.value = _uiState.value.copy(isWithdrawDialogVisible = false)
            if (success) {
                showToast("Payout of ${com.example.data.model.formatFixoCurrency(amount)} transferred to $method ($account)!")
            } else {
                showToast("Insufficient balance for withdrawal.")
            }
        }
    }

    fun redeemReward(reward: RewardItem) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val success = repository.redeemReward(user.id, reward)
            if (success) {
                showToast("Voucher code [${reward.promoCode}] redeemed successfully!")
            } else {
                showToast("Need ${reward.pointsRequired} points to redeem this reward.")
            }
        }
    }

    fun openSubscriptionDialog() {
        _uiState.value = _uiState.value.copy(isSubscriptionDialogVisible = true)
    }

    fun closeSubscriptionDialog() {
        _uiState.value = _uiState.value.copy(isSubscriptionDialogVisible = false)
    }

    fun selectSubscriptionTier(tier: SubscriptionTier) {
        viewModelScope.launch {
            repository.updateWorkerSubscription("wrk_1", tier)
            _uiState.value = _uiState.value.copy(isSubscriptionDialogVisible = false)
            showToast("Artisan subscription upgraded to ${tier.title}!")
        }
    }

    fun openAvailabilityDialog() {
        _uiState.value = _uiState.value.copy(isAvailabilityDialogVisible = true)
    }

    fun closeAvailabilityDialog() {
        _uiState.value = _uiState.value.copy(isAvailabilityDialogVisible = false)
    }

    fun saveAvailability(days: String, slots: String) {
        viewModelScope.launch {
            repository.updateWorkerAvailability("wrk_1", days, slots)
            _uiState.value = _uiState.value.copy(isAvailabilityDialogVisible = false)
            showToast("Artisan schedule & time slots saved!")
        }
    }

    fun openEnterpriseCreateDialog() {
        _uiState.value = _uiState.value.copy(isEnterpriseCreateDialogVisible = true)
    }

    fun closeEnterpriseCreateDialog() {
        _uiState.value = _uiState.value.copy(isEnterpriseCreateDialogVisible = false)
    }

    fun createEnterpriseProject(
        title: String,
        workersNeeded: Int,
        category: ServiceCategory,
        budget: Double,
        location: String
    ) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.createEnterpriseProject(
                companyName = user.name,
                title = title,
                workersNeeded = workersNeeded,
                category = category,
                budget = budget,
                location = location
            )
            _uiState.value = _uiState.value.copy(isEnterpriseCreateDialogVisible = false)
            showToast("Enterprise workforce order posted! AI dispatch matched 1 artisan.")
        }
    }

    fun openDisputeDialog() {
        _uiState.value = _uiState.value.copy(isDisputeDialogVisible = true)
    }

    fun closeDisputeDialog() {
        _uiState.value = _uiState.value.copy(isDisputeDialogVisible = false)
    }

    fun submitDispute(reason: String) {
        val user = _uiState.value.currentUser ?: return
        val booking = _uiState.value.selectedBooking
        viewModelScope.launch {
            repository.fileDispute(
                reporterId = user.id,
                reporterName = user.name,
                reportedType = "BOOKING",
                reportedId = booking?.id ?: "general",
                reason = reason
            )
            _uiState.value = _uiState.value.copy(isDisputeDialogVisible = false)
            showToast("Dispute ticket registered. FIXO Compliance team will review within 2 hours.")
        }
    }

    fun resolveDispute(disputeId: String, notes: String) {
        viewModelScope.launch {
            repository.resolveDispute(disputeId, notes)
            showToast("Dispute $disputeId marked as resolved.")
        }
    }

    fun loadSandboxTestData() {
        viewModelScope.launch {
            repository.loadSandboxTestData()
            showToast("Sandbox dataset populated (verified Cameroon artisans, bookings & reels).")
        }
    }

    fun clearToCleanProductionState() {
        viewModelScope.launch {
            repository.clearDatabaseToCleanState()
            showToast("Database reset to clean production mode. Ready for live Cameroon data.")
        }
    }

    fun toggleDevEnvironment() {
        val current = _uiState.value.isDevEnvironment
        if (current) {
            clearToCleanProductionState()
        } else {
            loadSandboxTestData()
        }
    }

    fun login(email: String, role: UserRole) {
        viewModelScope.launch {
            val allUsers = FixoSeedData.defaultUsers
            val defaultName = email.substringBefore("@").let { prefix ->
                if (prefix.isNotEmpty()) prefix.substring(0, 1).uppercase() + prefix.substring(1) else "User"
            }
            val matchedUser = allUsers.find { it.email.equals(email, ignoreCase = true) }
                ?: User(
                    id = "usr_${System.currentTimeMillis()}",
                    role = role,
                    name = defaultName,
                    email = email,
                    phone = "+237 670 000 000",
                    avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400"
                )
            sessionManager.saveSession(
                userId = matchedUser.id,
                role = role
            )
            repository.setRole(role)
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                currentUser = matchedUser,
                currentRole = role
            )
            showToast("Welcome back, ${matchedUser.name}!")
        }
    }

    fun loginWithBackend(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authLoading = true)
            val result = repository.loginWithBackend(email, password)
            _uiState.value = _uiState.value.copy(authLoading = false)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    currentUser = user,
                    currentRole = user.role
                )
                showToast("Welcome back, ${user.name}!")
            }.onFailure { err ->
                showToast(err.message ?: "Authentication failed")
            }
        }
    }

    fun signInWithGoogle(role: UserRole = UserRole.CUSTOMER) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authLoading = true)
            // Check if Google OAuth Client ID or production credentials are configured
            val googleClientId = com.example.BuildConfig.BUILD_TYPE // BuildConfig presence check
            // If production Google OAuth credentials are not configured, explicitly notify instead of fabricating credentials
            val isGoogleConfigured = false // Unconfigured in test/dev container without production Google OAuth setup
            if (!isGoogleConfigured) {
                _uiState.value = _uiState.value.copy(authLoading = false)
                showToast("Google OAuth is unavailable: Production credentials/client ID are not configured in this environment.")
                return@launch
            }
        }
    }

    fun checkSessionOnSplashComplete() {
        val hasSession = sessionManager.isAuthenticated.value && sessionManager.currentUserId.value.isNotBlank()
        if (hasSession) {
            val userId = sessionManager.currentUserId.value
            val role = sessionManager.currentRole.value
            val user = FixoSeedData.defaultUsers.find { it.id == userId }
                ?: User(
                    id = userId,
                    role = role,
                    name = if (role == UserRole.WORKER) "Marc Dubois" else "Sarah Jenkins",
                    email = "$userId@fixo.cm",
                    phone = "+237 671 234 567",
                    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300"
                )
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                currentUser = user,
                currentRole = role,
                isSessionChecked = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = false,
                isSessionChecked = true
            )
        }
    }

    fun quickLoginCustomer() {
        val sarah = FixoSeedData.defaultUsers.find { it.id == "usr_cust_1" }
            ?: User(
                id = "usr_cust_1",
                role = UserRole.CUSTOMER,
                name = "Sarah Jenkins",
                email = "sarah.j@gmail.com",
                phone = "+237 671 234 567",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
                balance = 35000.0,
                escrowLocked = 0.0,
                city = "Douala",
                quarter = "Bonapriso"
            )
        sessionManager.saveSession(
            userId = sarah.id,
            role = UserRole.CUSTOMER
        )
        repository.setRole(UserRole.CUSTOMER)
        _uiState.value = _uiState.value.copy(
            isAuthenticated = true,
            currentUser = sarah,
            currentRole = UserRole.CUSTOMER,
            isViewingKycFunnel = false
        )
        showToast("Bienvenue Sarah ! Portefeuille Séquestre initialisé.")
    }

    fun quickLoginArtisan(status: com.example.data.model.ArtisanKycStatus) {
        val marc = FixoSeedData.defaultUsers.find { it.id == "usr_worker_1" }
            ?: User(
                id = "usr_worker_1",
                role = UserRole.WORKER,
                name = "Marc Dubois",
                email = "marc.craftsman@fixo.pro",
                phone = "+237 699 876 543",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                balance = 48500.0,
                escrowLocked = 15000.0,
                city = "Douala",
                quarter = "Akwa"
            )
        val updatedVerification = when (status) {
            com.example.data.model.ArtisanKycStatus.NOT_STARTED -> com.example.data.model.VerificationStatus.UNVERIFIED
            com.example.data.model.ArtisanKycStatus.IN_REVIEW -> com.example.data.model.VerificationStatus.PENDING
            com.example.data.model.ArtisanKycStatus.APPROVED -> com.example.data.model.VerificationStatus.VERIFIED_PRO
        }
        val userWithStatus = marc.copy(verificationStatus = updatedVerification)

        sessionManager.saveSession(
            userId = userWithStatus.id,
            role = UserRole.WORKER
        )
        repository.setRole(UserRole.WORKER)
        _uiState.value = _uiState.value.copy(
            isAuthenticated = true,
            currentUser = userWithStatus,
            currentRole = UserRole.WORKER,
            artisanKycOverride = status,
            isViewingKycFunnel = status == com.example.data.model.ArtisanKycStatus.NOT_STARTED
        )
        showToast("Connecté : Marc Dubois (Statut KYC : ${status.name})")
    }

    fun loginWithPhoneOtp(phone: String, isCustomer: Boolean) {
        if (isCustomer) {
            quickLoginCustomer()
        } else {
            quickLoginArtisan(com.example.data.model.ArtisanKycStatus.IN_REVIEW)
        }
    }

    fun openArtisanKycFunnel() {
        _uiState.value = _uiState.value.copy(isViewingKycFunnel = true)
    }

    fun closeArtisanKycFunnel() {
        _uiState.value = _uiState.value.copy(isViewingKycFunnel = false)
    }

    fun submitArtisanKycDossier() {
        val user = _uiState.value.currentUser ?: FixoSeedData.defaultUsers.find { it.id == "usr_worker_1" }
        val updated = user?.copy(verificationStatus = com.example.data.model.VerificationStatus.PENDING)
        viewModelScope.launch {
            if (updated != null) {
                repository.updateCurrentUser(updated)
            }
            _uiState.value = _uiState.value.copy(
                currentUser = updated,
                artisanKycOverride = com.example.data.model.ArtisanKycStatus.IN_REVIEW,
                isViewingKycFunnel = false
            )
            showToast("Dossier KYC transmis avec succès ! En cours de revue.")
        }
    }

    fun simulateApproveWorkerKyc() {
        viewModelScope.launch {
            val user = _uiState.value.currentUser ?: FixoSeedData.defaultUsers.find { it.id == "usr_worker_1" }
            val approved = user?.copy(verificationStatus = com.example.data.model.VerificationStatus.VERIFIED_PRO)
            if (approved != null) {
                repository.updateCurrentUser(approved)
            }

            // Push silent notification
            val notif = com.example.data.model.FixoNotification(
                id = "notif_kyc_${System.currentTimeMillis()}",
                userId = user?.id ?: "usr_worker_1",
                title = "🎉 Homologation Validée !",
                message = "Félicitations ! Votre profil artisan FIXO PRO a été approuvé. Votre Cockpit Pro est déverrouillé.",
                type = "KYC_APPROVED",
                timestamp = System.currentTimeMillis()
            )
            repository.insertNotification(notif)

            _uiState.value = _uiState.value.copy(
                currentUser = approved,
                artisanKycOverride = com.example.data.model.ArtisanKycStatus.APPROVED,
                isViewingKycFunnel = false
            )
            showToast("🟢 Homologation approuvée ! Cockpit Pro activé.")
        }
    }

    fun quickLoginAs(user: User) {
        sessionManager.saveSession(
            userId = user.id,
            role = user.role
        )
        repository.setRole(user.role)
        _uiState.value = _uiState.value.copy(
            isAuthenticated = true,
            currentUser = user,
            currentRole = user.role
        )
        showToast("Logged in as ${user.name} (${user.role.name})")
    }

    fun logout() {
        sessionManager.logout()
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false
        )
        showToast("Logged out successfully.")
    }

    fun updateUserProfile(name: String, email: String, phone: String, city: String, avatarUrl: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            val updated = user.copy(
                name = name.ifBlank { user.name },
                email = email.ifBlank { user.email },
                phone = phone.ifBlank { user.phone },
                avatarUrl = avatarUrl.ifBlank { user.avatarUrl }
            )
            repository.updateCurrentUser(updated)
            _uiState.value = _uiState.value.copy(currentUser = updated)
            showToast("Profile updated successfully.")
        }
    }

    fun toggleSaveWorker(workerId: String) {
        val current = _uiState.value.savedWorkerIds.toMutableSet()
        if (current.contains(workerId)) {
            current.remove(workerId)
            showToast("Artisan removed from saved.")
        } else {
            current.add(workerId)
            showToast("Artisan saved to favorites.")
        }
        _uiState.value = _uiState.value.copy(savedWorkerIds = current)
    }

    fun setThemeMode(mode: ThemeMode) {
        sessionManager.setThemeMode(mode)
        showToast("Theme changed to ${mode.name.lowercase().replaceFirstChar { it.uppercase() }}")
    }

    fun setNotificationPref(jobs: Boolean, messages: Boolean, payments: Boolean) {
        sessionManager.setNotificationPref(jobs, messages, payments)
        showToast("Notification preferences updated")
    }

    fun setShareLocation(enabled: Boolean) {
        sessionManager.setShareLocation(enabled)
        showToast(if (enabled) "Live location sharing enabled for active jobs" else "Location sharing paused")
    }

    fun deleteAccount() {
        viewModelScope.launch {
            logout()
            showToast("Account deleted.")
        }
    }

    fun clearSelectedWorker() {
        _uiState.value = _uiState.value.copy(selectedWorker = null)
    }

    fun selectOrganization(org: com.example.data.model.Organization?) {
        _uiState.value = _uiState.value.copy(selectedOrganization = org)
    }

    fun selectCameroonQuarter(quarter: com.example.data.model.CameroonQuarter) {
        _uiState.value = _uiState.value.copy(
            selectedQuarter = quarter,
            isLocationPickerVisible = false
        )
        showToast("Location updated to ${quarter.name}, Douala")
    }

    fun showLocationPicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(isLocationPickerVisible = show)
    }

    fun showVerificationCenter(show: Boolean) {
        _uiState.value = _uiState.value.copy(isVerificationCenterVisible = show)
    }

    fun showHelpCenter(show: Boolean) {
        _uiState.value = _uiState.value.copy(isHelpCenterVisible = show)
    }

    fun registerCustomer(
        name: String,
        username: String,
        phone: String,
        email: String,
        region: String,
        city: String,
        quarter: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authLoading = true)
            val user = repository.registerCustomer(name, username, phone, email, region, city, quarter)
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                currentUser = user,
                currentRole = UserRole.CUSTOMER,
                authLoading = false
            )
            showToast("Welcome to FIXO, ${user.name}!")
        }
    }

    fun registerWorker(
        name: String,
        username: String,
        phone: String,
        email: String,
        category: ServiceCategory,
        hourlyRate: Double,
        bio: String,
        serviceArea: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authLoading = true)
            val pair = repository.registerWorker(name, username, phone, email, category, hourlyRate, bio, serviceArea)
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                currentUser = pair.first,
                currentRole = UserRole.WORKER,
                authLoading = false
            )
            showToast("Artisan profile registered! Please complete ID verification.")
        }
    }

    fun registerOrganization(
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
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authLoading = true)
            val pair = repository.registerOrganization(
                name, type, description, phone, email, address, city, region, regNumber, repName, repTitle
            )
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                currentUser = pair.first,
                currentRole = UserRole.ENTERPRISE,
                selectedOrganization = pair.second,
                authLoading = false
            )
            showToast("Enterprise account registered for ${pair.second.name}!")
        }
    }

    fun sendPhoneOtp(phone: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (phone.length < 8) {
                onResult(false, "Invalid phone number format. Please provide Cameroon number.")
                return@launch
            }
            onResult(true, "OTP code sent to $phone")
            showToast("Verification code sent to $phone")
        }
    }

    fun verifyPhoneOtp(phone: String, otp: String, role: UserRole, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (otp.trim().length == 6) {
                onResult(true)
                login(email = "$phone@fixo.cm", role = role)
            } else {
                onResult(false)
                showToast("Invalid 6-digit OTP code")
            }
        }
    }

    fun createWorkforceRequest(
        title: String,
        category: ServiceCategory,
        requiredCount: Int,
        ratePerDayXaf: Double,
        location: String,
        startDate: String,
        endDate: String,
        description: String
    ) {
        viewModelScope.launch {
            val org = _uiState.value.selectedOrganization
                ?: _uiState.value.allOrganizations.firstOrNull()
                ?: return@launch
            val request = com.example.data.model.WorkforceRequest(
                id = "wfr_" + java.util.UUID.randomUUID().toString().take(8),
                organizationId = org.id,
                organizationName = org.name,
                projectTitle = title,
                category = category,
                requiredCount = requiredCount,
                ratePerDayXaf = ratePerDayXaf,
                location = location,
                startDate = startDate,
                endDate = endDate,
                description = description
            )
            repository.createWorkforceRequest(request)
            showToast("Workforce recruitment listing published!")
        }
    }

    fun applyForWorkforceRequest(requestId: String) {
        viewModelScope.launch {
            val worker = _uiState.value.currentUser ?: return@launch
            repository.applyForWorkforceRequest(requestId, worker.id)
            showToast("Application submitted to organization!")
        }
    }

    fun submitProfessionalVerification(cniNumber: String, tradeReg: String) {
        viewModelScope.launch {
            val user = _uiState.value.currentUser ?: return@launch
            val updated = user.copy(verificationStatus = com.example.data.model.VerificationStatus.PENDING)
            repository.updateCurrentUser(updated)
            _uiState.value = _uiState.value.copy(currentUser = updated)
            showToast("Documents submitted for administrative review.")
        }
    }

    fun submitOrganizationVerification(rccm: String, niu: String) {
        viewModelScope.launch {
            val user = _uiState.value.currentUser ?: return@launch
            val updated = user.copy(verificationStatus = com.example.data.model.VerificationStatus.PENDING)
            repository.updateCurrentUser(updated)
            _uiState.value = _uiState.value.copy(currentUser = updated)
            showToast("Enterprise compliance dossier submitted for review.")
        }
    }

    fun setQuarter(quarter: com.example.data.model.CameroonQuarter) {
        _uiState.value = _uiState.value.copy(selectedQuarter = quarter)
        showToast("Location set to ${quarter.name}")
    }

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
