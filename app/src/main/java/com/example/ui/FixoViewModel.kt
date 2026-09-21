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
    val isDevEnvironment: Boolean = false
)

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

        // Observe transactions for current user
        viewModelScope.launch {
            repository.getTransactionsForUser("usr_cust_1").collect { txs ->
                _uiState.value = _uiState.value.copy(transactions = txs)
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
            repository.getMessagesForBooking(booking.id).collect { msgs ->
                _uiState.value = _uiState.value.copy(chatMessages = msgs)
            }
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
            showToast("Booking Confirmed! $${service.price} held safely in Escrow.")
        }
    }

    fun advanceJobStatus(bookingId: String, newStatus: com.example.data.model.JobStatus) {
        viewModelScope.launch {
            repository.updateJobStatus(bookingId, newStatus)
            showToast("Job status updated to ${newStatus.name}")
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
        val user = _uiState.value.currentUser ?: return
        if (message.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(
                bookingId = booking.id,
                senderId = user.id,
                senderName = user.name,
                senderRole = _uiState.value.currentRole,
                message = message
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
            showToast("Deposited $$amount via $method! Balance updated.")
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
                showToast("Payout of $$amount transferred to $method ($account)!")
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
                role = role,
                accessToken = "fixo_jwt_${matchedUser.id}",
                refreshToken = "fixo_rf_${matchedUser.id}"
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

    fun signInWithGoogle(role: UserRole = UserRole.CUSTOMER) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(authLoading = true)
            val fakeIdToken = "google_id_token_${System.currentTimeMillis()}"
            val result = repository.signInWithGoogle(fakeIdToken, role)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    currentUser = user,
                    currentRole = user.role,
                    authLoading = false
                )
                showToast("Signed in with Google as ${user.name}")
            }.onFailure {
                _uiState.value = _uiState.value.copy(authLoading = false)
                showToast("Google authentication failed.")
            }
        }
    }

    fun quickLoginAs(user: User) {
        sessionManager.saveSession(
            userId = user.id,
            role = user.role,
            accessToken = "fixo_jwt_${user.id}",
            refreshToken = "fixo_rf_${user.id}"
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

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
