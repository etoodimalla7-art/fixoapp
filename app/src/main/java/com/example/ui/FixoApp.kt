package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.data.model.WorkerProfile
import com.example.ui.components.BookingDialog
import com.example.ui.components.DepositDialog
import com.example.ui.components.DisputeDialog
import com.example.ui.components.FixoBottomNav
import com.example.ui.components.FixoTopBar
import com.example.ui.components.ReviewAndReleaseDialog
import com.example.ui.components.UploadReelDialog
import com.example.ui.components.WithdrawDialog
import com.example.ui.screens.admin.AdminPortalScreen
import com.example.ui.screens.customer.CustomerHomeScreen
import com.example.ui.screens.customer.JobTrackingScreen
import com.example.ui.screens.customer.WorkerProfileScreen
import com.example.ui.screens.enterprise.EnterpriseScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.reels.ReelsFeedScreen
import com.example.ui.screens.splash.FixoSplashScreen
import com.example.ui.screens.wallet.WalletRewardsScreen
import com.example.ui.screens.worker.WorkerDashboardScreen
import com.example.ui.theme.FixoNavy900

@Composable
fun FixoApp(
    viewModel: FixoViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTab by remember { mutableIntStateOf(0) }
    var viewingJobId by remember { mutableStateOf<String?>(null) }
    var isViewingWallet by remember { mutableStateOf(false) }
    var showSplashScreen by remember { mutableStateOf(true) }

    // When role changes, reset tab to 0
    LaunchedEffect(uiState.currentRole) {
        selectedTab = 0
        viewingJobId = null
        isViewingWallet = false
    }

    // Show toast message when triggered
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Splash Screen with official brand logo
    AnimatedVisibility(
        visible = showSplashScreen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        FixoSplashScreen(
            language = uiState.currentLanguage,
            onSplashComplete = { showSplashScreen = false },
            modifier = modifier
        )
    }

    if (!showSplashScreen) {
        if (!uiState.isAuthenticated) {
        com.example.ui.screens.AuthScreen(
            currentLanguage = uiState.currentLanguage,
            onLogin = { email, role ->
                viewModel.login(email, role)
            },
            onQuickLogin = { user ->
                viewModel.quickLoginAs(user)
            },
            onGoogleSignIn = { role ->
                viewModel.signInWithGoogle(role)
            },
            isDevEnvironment = uiState.isDevEnvironment,
            onToggleEnvironment = {
                viewModel.toggleDevEnvironment()
            },
            modifier = modifier
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                FixoTopBar(
                    currentRole = uiState.currentRole,
                    currentLanguage = uiState.currentLanguage,
                    currentUser = uiState.currentUser,
                    onRoleSelected = { role ->
                        viewModel.switchRole(role)
                    },
                    onLanguageToggle = {
                        viewModel.toggleLanguage()
                    },
                    onWalletClick = {
                        isViewingWallet = true
                    },
                    onLogout = {
                        viewModel.logout()
                    },
                    isDevEnvironment = uiState.isDevEnvironment,
                    onToggleEnvironment = {
                        viewModel.toggleDevEnvironment()
                    }
                )
            },
        bottomBar = {
            FixoBottomNav(
                currentRole = uiState.currentRole,
                selectedTabIndex = selectedTab,
                onTabSelected = { tabIndex ->
                    selectedTab = tabIndex
                    isViewingWallet = false
                    // If switching tabs, clear selected worker view
                    if (uiState.selectedWorker != null) {
                        viewModel.clearSelectedWorker()
                    }
                    viewingJobId = null
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isViewingWallet) {
                WalletRewardsScreen(
                    user = uiState.currentUser,
                    transactions = uiState.transactions,
                    rewards = uiState.rewards,
                    userRole = uiState.currentRole,
                    onOpenDeposit = { viewModel.openDepositDialog() },
                    onOpenWithdraw = { viewModel.openWithdrawDialog() },
                    onRedeemReward = { viewModel.redeemReward(it) },
                    onBack = { isViewingWallet = false }
                )
            } else {
                when (uiState.currentRole) {
                    UserRole.CUSTOMER -> {
                        // Check if viewing worker profile or job detail
                        val selectedWorker = uiState.selectedWorker
                        val activeBooking = uiState.selectedBooking

                        when {
                            // Sub-screen: Worker profile
                            selectedWorker != null && viewingJobId == null -> {
                                WorkerProfileScreen(
                                    worker = selectedWorker,
                                    services = uiState.workerServices.ifEmpty {
                                        // Default fallback services if worker just clicked
                                        listOf(
                                            com.example.data.model.ServiceItem(
                                                id = "srv_std_${selectedWorker.id}",
                                                workerId = selectedWorker.id,
                                                name = "Standard Diagnostic & On-Site Repair",
                                                category = selectedWorker.category,
                                                description = "On-site assessment, diagnostic equipment inspection, and standard repair with warranty.",
                                                price = selectedWorker.hourlyRate * 1.5,
                                                durationEstimateMinutes = 60
                                            )
                                        )
                                    },
                                    reels = uiState.reels,
                                    isSaved = uiState.savedWorkerIds.contains(selectedWorker.id),
                                    onToggleSave = {
                                        viewModel.toggleSaveWorker(selectedWorker.id)
                                    },
                                    onBack = {
                                        viewModel.clearSelectedWorker()
                                    },
                                    onBookService = { service ->
                                        viewModel.openBookingDialog(service, selectedWorker)
                                    },
                                    onWatchReel = {
                                        selectedTab = 2
                                    }
                                )
                            }

                        // Sub-screen: Job tracking
                        viewingJobId != null && activeBooking != null -> {
                            JobTrackingScreen(
                                booking = activeBooking,
                                chatMessages = uiState.chatMessages,
                                onBack = { viewingJobId = null },
                                onAdvanceStatus = { status ->
                                    viewModel.advanceJobStatus(activeBooking.id, status)
                                },
                                onOpenReview = {
                                    viewModel.openReviewDialog(activeBooking)
                                },
                                onSendMessage = { msg ->
                                    viewModel.sendChatMessage(msg)
                                },
                                onOpenDispute = {
                                    viewModel.openDisputeDialog()
                                }
                            )
                        }

                        // Primary Customer Tabs
                        else -> {
                            when (selectedTab) {
                                0 -> CustomerHomeScreen(
                                    workers = uiState.filteredWorkers,
                                    reels = uiState.reels,
                                    activeBookings = uiState.customerBookings,
                                    searchQuery = uiState.searchQuery,
                                    selectedCategory = uiState.selectedCategory,
                                    filterVerifiedOnly = uiState.filterVerifiedOnly,
                                    filterEmergencyOnly = uiState.filterEmergencyOnly,
                                    language = uiState.currentLanguage,
                                    onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                                    onCategorySelected = { viewModel.onCategorySelected(it) },
                                    onToggleVerified = { viewModel.toggleFilterVerifiedOnly() },
                                    onToggleEmergency = { viewModel.toggleFilterEmergencyOnly() },
                                    onWorkerClicked = { worker ->
                                        viewModel.selectWorker(worker)
                                    },
                                    onBookingClicked = { booking ->
                                        viewModel.selectBooking(booking)
                                        viewingJobId = booking.id
                                    },
                                    onWatchReelsClicked = { selectedTab = 2 }
                                )

                                1 -> CustomerHomeScreen(
                                    workers = uiState.filteredWorkers.ifEmpty { uiState.allWorkers },
                                    reels = uiState.reels,
                                    activeBookings = uiState.customerBookings,
                                    searchQuery = uiState.searchQuery,
                                    selectedCategory = uiState.selectedCategory,
                                    filterVerifiedOnly = uiState.filterVerifiedOnly,
                                    filterEmergencyOnly = uiState.filterEmergencyOnly,
                                    language = uiState.currentLanguage,
                                    onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                                    onCategorySelected = { viewModel.onCategorySelected(it) },
                                    onToggleVerified = { viewModel.toggleFilterVerifiedOnly() },
                                    onToggleEmergency = { viewModel.toggleFilterEmergencyOnly() },
                                    onWorkerClicked = { worker ->
                                        viewModel.selectWorker(worker)
                                    },
                                    onBookingClicked = { booking ->
                                        viewModel.selectBooking(booking)
                                        viewingJobId = booking.id
                                    },
                                    onWatchReelsClicked = { selectedTab = 2 }
                                )

                                2 -> ReelsFeedScreen(
                                    reels = uiState.reels,
                                    allWorkers = uiState.allWorkers,
                                    onLikeReel = { viewModel.likeReel(it) },
                                    onBookFromReel = { reel, worker ->
                                        val service = uiState.workerServices.firstOrNull() ?: com.example.data.model.ServiceItem(
                                            id = "srv_${reel.serviceId}",
                                            workerId = worker.id,
                                            name = reel.title,
                                            category = reel.category,
                                            description = reel.description,
                                            price = worker.hourlyRate * 1.5,
                                            durationEstimateMinutes = 60
                                        )
                                        viewModel.openBookingDialog(service, worker)
                                    },
                                    onOpenWorkerProfile = { worker ->
                                        viewModel.selectWorker(worker)
                                    },
                                    onReportReel = {
                                        viewModel.openDisputeDialog()
                                    }
                                )

                                3 -> {
                                    // Jobs list / tracker
                                    val currentBooking = uiState.customerBookings.firstOrNull()
                                    if (currentBooking != null) {
                                        JobTrackingScreen(
                                            booking = uiState.selectedBooking ?: currentBooking,
                                            chatMessages = uiState.chatMessages,
                                            onBack = { selectedTab = 0 },
                                            onAdvanceStatus = { status ->
                                                viewModel.advanceJobStatus((uiState.selectedBooking ?: currentBooking).id, status)
                                            },
                                            onOpenReview = {
                                                viewModel.openReviewDialog(uiState.selectedBooking ?: currentBooking)
                                            },
                                            onSendMessage = { msg ->
                                                viewModel.sendChatMessage(msg)
                                            },
                                            onOpenDispute = {
                                                viewModel.openDisputeDialog()
                                            }
                                        )
                                    } else {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Text("No bookings yet. Explore top artisans to book.")
                                        }
                                    }
                                }

                                4 -> ProfileScreen(
                                    user = uiState.currentUser,
                                    customerBookings = uiState.customerBookings,
                                    savedWorkers = uiState.allWorkers.filter { uiState.savedWorkerIds.contains(it.id) },
                                    language = uiState.currentLanguage,
                                    onUpdateProfile = { name, email, phone, city, avatarUrl ->
                                        viewModel.updateUserProfile(name, email, phone, city, avatarUrl)
                                    },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onNavigateToJobs = { selectedTab = 3 },
                                    onNavigateToWallet = { isViewingWallet = true },
                                    onWorkerClicked = { worker -> viewModel.selectWorker(worker) },
                                    onOpenDispute = { viewModel.openDisputeDialog() },
                                    onLogout = { viewModel.logout() },
                                    onDeleteAccount = { viewModel.deleteAccount() }
                                )
                            }
                        }
                    }
                }

                UserRole.WORKER -> {
                    when (selectedTab) {
                        0 -> WorkerDashboardScreen(
                            user = uiState.currentUser,
                            workerProfile = uiState.allWorkers.firstOrNull(),
                            bookings = uiState.workerBookings,
                            reels = uiState.reels,
                            onSelectBooking = { booking ->
                                viewModel.selectBooking(booking)
                                viewingJobId = booking.id
                            },
                            onAdvanceJobStatus = { id, status ->
                                viewModel.advanceJobStatus(id, status)
                            },
                            onOpenUploadReel = { viewModel.openUploadReelDialog() },
                            onOpenAvailability = { viewModel.openAvailabilityDialog() },
                            onOpenSubscription = { viewModel.openSubscriptionDialog() },
                            onOpenWithdraw = { viewModel.openWithdrawDialog() },
                            onDeleteReel = { viewModel.deleteReel(it) }
                        )

                        1 -> WorkerDashboardScreen(
                            user = uiState.currentUser,
                            workerProfile = uiState.allWorkers.firstOrNull(),
                            bookings = uiState.workerBookings,
                            reels = uiState.reels,
                            onSelectBooking = { booking ->
                                viewModel.selectBooking(booking)
                                viewingJobId = booking.id
                            },
                            onAdvanceJobStatus = { id, status ->
                                viewModel.advanceJobStatus(id, status)
                            },
                            onOpenUploadReel = { viewModel.openUploadReelDialog() },
                            onOpenAvailability = { viewModel.openAvailabilityDialog() },
                            onOpenSubscription = { viewModel.openSubscriptionDialog() },
                            onOpenWithdraw = { viewModel.openWithdrawDialog() },
                            onDeleteReel = { viewModel.deleteReel(it) }
                        )

                        2 -> ReelsFeedScreen(
                            reels = uiState.reels,
                            allWorkers = uiState.allWorkers,
                            onLikeReel = { viewModel.likeReel(it) },
                            onBookFromReel = { _, _ -> },
                            onOpenWorkerProfile = {},
                            onReportReel = { viewModel.openDisputeDialog() }
                        )

                        3 -> {
                            val activeJob = uiState.workerBookings.firstOrNull() ?: uiState.customerBookings.firstOrNull()
                            if (activeJob != null) {
                                JobTrackingScreen(
                                    booking = uiState.selectedBooking ?: activeJob,
                                    chatMessages = uiState.chatMessages,
                                    onBack = { selectedTab = 0 },
                                    onAdvanceStatus = { status ->
                                        viewModel.advanceJobStatus((uiState.selectedBooking ?: activeJob).id, status)
                                    },
                                    onOpenReview = { viewModel.openReviewDialog(uiState.selectedBooking ?: activeJob) },
                                    onSendMessage = { msg -> viewModel.sendChatMessage(msg) },
                                    onOpenDispute = { viewModel.openDisputeDialog() }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No active jobs assigned yet.")
                                }
                            }
                        }

                        4 -> ProfileScreen(
                            user = uiState.currentUser,
                            customerBookings = uiState.workerBookings,
                            savedWorkers = emptyList(),
                            language = uiState.currentLanguage,
                            onUpdateProfile = { name, email, phone, city, avatarUrl ->
                                viewModel.updateUserProfile(name, email, phone, city, avatarUrl)
                            },
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onNavigateToJobs = { selectedTab = 3 },
                            onNavigateToWallet = { isViewingWallet = true },
                            onWorkerClicked = {},
                            onOpenDispute = { viewModel.openDisputeDialog() },
                            onLogout = { viewModel.logout() },
                            onDeleteAccount = { viewModel.deleteAccount() }
                        )
                    }
                }

                UserRole.ENTERPRISE -> {
                    EnterpriseScreen(
                        user = uiState.currentUser,
                        projects = uiState.enterpriseProjects,
                        onOpenCreateProject = { viewModel.openEnterpriseCreateDialog() }
                    )
                }

                UserRole.ADMIN -> {
                    AdminPortalScreen(
                        user = uiState.currentUser,
                        workers = uiState.allWorkers,
                        disputes = uiState.disputes,
                        onResolveDispute = { id, notes -> viewModel.resolveDispute(id, notes) }
                    )
                }
            }
        }
    }
}

    // Interactive Dialogs
    if (uiState.isBookingDialogVisible && uiState.selectedServiceForBooking != null && uiState.selectedWorker != null) {
        BookingDialog(
            worker = uiState.selectedWorker!!,
            service = uiState.selectedServiceForBooking!!,
            onDismiss = { viewModel.closeBookingDialog() },
            onConfirm = { date, slot, address, notes, payment ->
                viewModel.confirmBooking(date, slot, address, notes, payment)
            }
        )
    }

    if (uiState.isReviewDialogVisible && uiState.selectedBooking != null) {
        ReviewAndReleaseDialog(
            booking = uiState.selectedBooking!!,
            onDismiss = { viewModel.closeReviewDialog() },
            onSubmit = { rating, review ->
                viewModel.submitReviewAndReleaseEscrow(rating, review)
            }
        )
    }

    if (uiState.isDepositDialogVisible) {
        DepositDialog(
            onDismiss = { viewModel.closeDepositDialog() },
            onConfirm = { amount, method, phone ->
                viewModel.processDeposit(amount, method, phone)
            }
        )
    }

    if (uiState.isWithdrawDialogVisible) {
        WithdrawDialog(
            availableBalance = uiState.currentUser?.balance ?: 0.0,
            onDismiss = { viewModel.closeWithdrawDialog() },
            onConfirm = { amount, method, account ->
                viewModel.processWithdraw(amount, method, account)
            }
        )
    }

    if (uiState.isUploadReelDialogVisible) {
        UploadReelDialog(
            onDismiss = { viewModel.closeUploadReelDialog() },
            onPublish = { title, desc, category, tags, videoUrl, thumbUrl ->
                viewModel.publishReel(title, desc, category, tags, videoUrl, thumbUrl)
            }
        )
    }

    if (uiState.isDisputeDialogVisible) {
        DisputeDialog(
            onDismiss = { viewModel.closeDisputeDialog() },
            onSubmit = { reason ->
                viewModel.submitDispute(reason)
            }
        )
    }
    }
}
}
