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
import com.example.data.model.Organization
import com.example.data.model.UserRole
import com.example.data.model.WorkerProfile
import com.example.data.model.WorkforceRequest
import com.example.data.repository.ThemeMode
import com.example.ui.components.BookingDialog
import com.example.ui.components.CameroonLocationPickerModal
import com.example.ui.components.DepositDialog
import com.example.ui.components.DisputeDialog
import com.example.ui.components.FixoBottomNav
import com.example.ui.components.FixoTopBar
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.ReviewAndReleaseDialog
import com.example.ui.components.StartTripConfirmationDialog
import com.example.ui.components.UploadReelDialog
import com.example.ui.components.WithdrawDialog
import com.example.ui.screens.admin.AdminPortalScreen
import com.example.ui.screens.chat.ChatDetailScreen
import com.example.ui.screens.chat.ConversationsScreen
import com.example.ui.screens.chat.LiveWorkroomChat
import com.example.ui.screens.customer.BookingCheckoutModal
import com.example.ui.screens.customer.CustomerActivityScreen
import com.example.ui.screens.customer.CustomerHomeScreen
import com.example.ui.screens.customer.CustomerServicesScreen
import com.example.ui.screens.customer.ExplorerMapScreen
import com.example.ui.screens.customer.JobTrackingScreen
import com.example.ui.screens.customer.WorkerProfileScreen
import com.example.ui.screens.enterprise.EnterpriseScreen
import com.example.ui.screens.enterprise.OrganizationDetailScreen
import com.example.ui.screens.enterprise.WorkforceRecruitmentScreen
import com.example.ui.screens.help.HelpCenterScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.VerificationCenterScreen
import com.example.ui.screens.reels.ReelsFeedScreen
import com.example.ui.screens.splash.FixoSplashScreen
import com.example.ui.screens.wallet.WalletRewardsScreen
import com.example.ui.screens.worker.JobNavigationScreen
import com.example.ui.screens.worker.WorkerDashboardScreen
import com.example.ui.theme.FixoNavy900

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
    var isViewingConversations by remember { mutableStateOf(false) }
    var activeChatBookingId by remember { mutableStateOf<String?>(null) }
    var showSplashScreen by remember { mutableStateOf(true) }
    var isViewingOnboarding by remember { mutableStateOf(false) }
    var isViewingVerificationCenter by remember { mutableStateOf(false) }
    var isViewingHelpCenter by remember { mutableStateOf(false) }
    var isViewingLocationPicker by remember { mutableStateOf(false) }
    var isViewingWorkforceRecruitment by remember { mutableStateOf(false) }
    var isViewingPatrolMap by remember { mutableStateOf(false) }
    var selectedOrganization by remember { mutableStateOf<Organization?>(null) }
    var activeReelId by remember { mutableStateOf<String?>(null) }

    // When role changes, reset tab to 0
    LaunchedEffect(uiState.currentRole) {
        selectedTab = 0
        viewingJobId = null
        isViewingWallet = false
        isViewingConversations = false
        activeChatBookingId = null
        isViewingVerificationCenter = false
        isViewingHelpCenter = false
        isViewingWorkforceRecruitment = false
        selectedOrganization = null
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
            onSplashComplete = {
                showSplashScreen = false
                viewModel.checkSessionOnSplashComplete()
            },
            modifier = modifier
        )
    }

    if (!showSplashScreen) {
        if (!uiState.isAuthenticated) {
            com.example.ui.screens.AuthScreen(
                currentLanguage = uiState.currentLanguage,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onPhoneOtpValidated = { phone, isCustomer ->
                    viewModel.loginWithPhoneOtp(phone, isCustomer)
                },
                onGoogleSignIn = {
                    viewModel.signInWithGoogle()
                },
                onQuickLoginCustomer = {
                    viewModel.quickLoginCustomer()
                },
                onQuickLoginArtisan = { status ->
                    viewModel.quickLoginArtisan(status)
                },
                onOpenArtisanKycFunnel = {
                    viewModel.quickLoginArtisan(com.example.data.model.ArtisanKycStatus.NOT_STARTED)
                },
                modifier = modifier
            )
        } else if (uiState.currentRole == UserRole.WORKER && (uiState.isViewingKycFunnel || uiState.effectiveArtisanKycStatus == com.example.data.model.ArtisanKycStatus.NOT_STARTED)) {
            com.example.ui.screens.worker.ArtisanKycFunnelScreen(
                user = uiState.currentUser,
                language = uiState.currentLanguage,
                onKycSubmitted = {
                    viewModel.submitArtisanKycDossier()
                },
                onBack = {
                    viewModel.closeArtisanKycFunnel()
                    if (uiState.effectiveArtisanKycStatus == com.example.data.model.ArtisanKycStatus.NOT_STARTED) {
                        viewModel.logout()
                    }
                },
                modifier = modifier
            )
        } else if (uiState.currentRole == UserRole.WORKER && uiState.effectiveArtisanKycStatus == com.example.data.model.ArtisanKycStatus.IN_REVIEW) {
            com.example.ui.screens.worker.KycPendingScreen(
                user = uiState.currentUser,
                language = uiState.currentLanguage,
                onUpdateDocuments = {
                    viewModel.openArtisanKycFunnel()
                },
                onContactSupportWhatsapp = {
                    viewModel.showToast("Ouverture de l'assistance FIXO via WhatsApp (+237 670 000 000)...")
                },
                onSimulateInstantApproval = {
                    viewModel.simulateApproveWorkerKyc()
                },
                onLogout = {
                    viewModel.logout()
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
                        },
                        unreadNotificationCount = uiState.unreadNotificationCount,
                        onNotificationsClick = {
                            viewModel.openNotificationsDialog()
                        },
                        unreadMessageCount = uiState.unreadMessageCount,
                        onMessagesClick = {
                            isViewingConversations = true
                            isViewingWallet = false
                            activeChatBookingId = null
                        },
                        selectedQuarter = uiState.selectedQuarter.name,
                        onLocationClick = {
                            isViewingLocationPicker = true
                        },
                        onHelpClick = {
                            isViewingHelpCenter = true
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
                    isViewingConversations = false
                    activeChatBookingId = null
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
            when {
                isViewingOnboarding -> {
                    OnboardingScreen(
                        onComplete = { role ->
                            isViewingOnboarding = false
                            viewModel.switchRole(role)
                        },
                        onSkipToLogin = {
                            isViewingOnboarding = false
                        }
                    )
                }
                isViewingHelpCenter -> {
                    HelpCenterScreen(
                        onBack = { isViewingHelpCenter = false },
                        onContactSupport = {
                            viewModel.showToast("Connecting to WhatsApp Support (+237 670 000 000)...")
                        }
                    )
                }
                isViewingVerificationCenter -> {
                    VerificationCenterScreen(
                        currentUser = uiState.currentUser,
                        onBack = { isViewingVerificationCenter = false },
                        onSubmitWorkerVerification = { cni, tradeReg ->
                            viewModel.submitProfessionalVerification(cni, tradeReg)
                        },
                        onSubmitOrgVerification = { rccm, niu ->
                            viewModel.submitOrganizationVerification(rccm, niu)
                        }
                    )
                }
                isViewingWorkforceRecruitment -> {
                    WorkforceRecruitmentScreen(
                        currentUser = uiState.currentUser,
                        workforceRequests = uiState.workforceRequests,
                        onBack = { isViewingWorkforceRecruitment = false },
                        onApplyToRequest = { reqId, _ ->
                            viewModel.applyForWorkforceRequest(reqId)
                        },
                        onCreateRequest = { title, cat, needed, rate, loc, _, desc ->
                            viewModel.createWorkforceRequest(title, cat, needed, rate, loc, "Immediate", "Ongoing", desc)
                        }
                    )
                }
                selectedOrganization != null -> {
                    OrganizationDetailScreen(
                        organization = selectedOrganization!!,
                        workforceRequests = uiState.workforceRequests.filter { it.organizationId == selectedOrganization!!.id },
                        onBack = { selectedOrganization = null },
                        onContact = {
                            viewModel.showToast("Connecting to ${selectedOrganization!!.representativeName}...")
                        },
                        onOpenRequest = { _ ->
                            selectedOrganization = null
                            isViewingWorkforceRecruitment = true
                        }
                    )
                }
                activeChatBookingId != null -> {
                    val chatBooking = uiState.allBookings.find { it.id == activeChatBookingId }
                        ?: uiState.customerBookings.find { it.id == activeChatBookingId }
                        ?: uiState.workerBookings.find { it.id == activeChatBookingId }
                        ?: uiState.selectedBooking
                    if (chatBooking != null) {
                        LiveWorkroomChat(
                            booking = chatBooking,
                            messages = uiState.chatMessages.ifEmpty { uiState.allMessages.filter { it.bookingId == chatBooking.id } },
                            currentUserRole = uiState.currentRole,
                            currentUserId = uiState.currentUser?.id ?: "usr_cust_1",
                            language = uiState.currentLanguage,
                            onBack = {
                                activeChatBookingId = null
                            },
                            onSendMessage = { text, attachmentUrl, attachmentType, durationSec ->
                                viewModel.sendWorkroomChatMessage(chatBooking.id, text, attachmentUrl, attachmentType, durationSec)
                            }
                        )
                    } else {
                        activeChatBookingId = null
                    }
                }
                isViewingConversations -> {
                    ConversationsScreen(
                        currentUserId = uiState.currentUser?.id ?: "usr_cust_1",
                        currentUserRole = uiState.currentRole,
                        bookings = if (uiState.currentRole == UserRole.WORKER) uiState.workerBookings else uiState.customerBookings,
                        messages = uiState.allMessages,
                        onConversationSelected = { bookingId ->
                            val b = uiState.allBookings.find { it.id == bookingId }
                                ?: uiState.customerBookings.find { it.id == bookingId }
                                ?: uiState.workerBookings.find { it.id == bookingId }
                            if (b != null) {
                                viewModel.selectBooking(b)
                            }
                            activeChatBookingId = bookingId
                        },
                        onBackClicked = {
                            isViewingConversations = false
                        }
                    )
                }
                isViewingWallet -> {
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
                }
                else -> {
                    when (uiState.currentRole) {
                    UserRole.CUSTOMER -> {
                        // Check if viewing worker profile or job detail
                        val selectedWorker = uiState.selectedWorker
                        val activeBooking = uiState.selectedBooking

                        when {
                            // Sub-screen: Fullscreen Explorer Patrol Map
                            isViewingPatrolMap -> {
                                ExplorerMapScreen(
                                    workers = uiState.allWorkers,
                                    currentQuarterName = uiState.selectedQuarter.name,
                                    language = uiState.currentLanguage,
                                    onBack = { isViewingPatrolMap = false },
                                    onWorkerSelected = { worker ->
                                        viewModel.selectWorker(worker)
                                        isViewingPatrolMap = false
                                    },
                                    onBookWorker = { worker, isFlash, price ->
                                        isViewingPatrolMap = false
                                        val service = com.example.data.model.ServiceItem(
                                            id = "srv_fixo_${if (isFlash) "flash" else "std"}_${worker.id}",
                                            workerId = worker.id,
                                            name = if (isFlash) "Fixo Flash ⚡ Dépannage Immédiat (< 30 min)" else "Fixo Standard Intervention",
                                            category = worker.category,
                                            description = "Intervention professionnelle forfaitaire avec garantie Fixo Shield.",
                                            price = price,
                                            durationEstimateMinutes = if (isFlash) 30 else 90
                                        )
                                        viewModel.openBookingDialog(service, worker)
                                    }
                                )
                            }

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
                                activeLocation = uiState.activeWorkerLocation,
                                currentRole = uiState.currentRole,
                                onBack = { viewingJobId = null },
                                onStartTrip = {
                                    viewModel.openStartTripConfirmation(activeBooking)
                                },
                                onMarkArrived = {
                                    viewModel.markWorkerArrived(activeBooking.id)
                                },
                                onStartWork = {
                                    viewModel.startWork(activeBooking.id)
                                },
                                onRequestCompletion = {
                                    viewModel.requestJobCompletion(activeBooking.id)
                                },
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
                                },
                                onCancelBooking = {
                                    viewModel.cancelBooking(activeBooking.id)
                                },
                                onOpenFullChat = {
                                    activeChatBookingId = activeBooking.id
                                },
                                onTakeBeforePhoto = { photoUrl ->
                                    viewModel.submitInitialPhoto(activeBooking.id, photoUrl)
                                },
                                onTakeAfterPhoto = { photoUrl ->
                                    viewModel.submitFinalPhoto(activeBooking.id, photoUrl)
                                },
                                onSimulateArrivalAndPhotos = {
                                    viewModel.simulateArrivalAndPhotos(activeBooking.id)
                                },
                                language = uiState.currentLanguage
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
                                    onWatchReelsClicked = {
                                        activeReelId = null
                                        selectedTab = 3
                                    },
                                    onWatchSpecificReel = { reel ->
                                        activeReelId = reel.id
                                        selectedTab = 3
                                    },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onOpenQuarterPicker = { isViewingLocationPicker = true },
                                    onOpenNotifications = { viewModel.openNotificationsDialog() },
                                    onOpenPatrolMap = { isViewingPatrolMap = true },
                                    onConfirmPassportBooking = { worker, isFlash, price ->
                                        val service = com.example.data.model.ServiceItem(
                                            id = "srv_fixo_${if (isFlash) "flash" else "std"}_${worker.id}",
                                            workerId = worker.id,
                                            name = if (isFlash) "Fixo Flash ⚡ Dépannage Immédiat (< 30 min)" else "Fixo Standard Intervention",
                                            category = worker.category,
                                            description = "Intervention professionnelle forfaitaire avec garantie Fixo Shield.",
                                            price = price,
                                            durationEstimateMinutes = if (isFlash) 30 else 90
                                        )
                                        viewModel.openBookingDialog(service, worker)
                                    },
                                    currentQuarterName = uiState.selectedQuarter.name,
                                    unreadNotificationCount = uiState.unreadNotificationCount
                                )

                                1 -> CustomerServicesScreen(
                                    allWorkers = uiState.allWorkers,
                                    language = uiState.currentLanguage,
                                    onBookService = { service, worker ->
                                        viewModel.openBookingDialog(service, worker)
                                    },
                                    onWorkerClicked = { worker ->
                                        viewModel.selectWorker(worker)
                                    }
                                )

                                2 -> CustomerActivityScreen(
                                    bookings = uiState.customerBookings,
                                    language = uiState.currentLanguage,
                                    onSelectBooking = { booking ->
                                        viewModel.selectBooking(booking)
                                        viewingJobId = booking.id
                                    },
                                    onOpenReview = { booking ->
                                        viewModel.openReviewDialog(booking)
                                    },
                                    onOpenDispute = {
                                        viewModel.openDisputeDialog()
                                    }
                                )

                                3 -> ReelsFeedScreen(
                                    reels = uiState.reels,
                                    allWorkers = uiState.allWorkers,
                                    initialReelId = activeReelId,
                                    language = uiState.currentLanguage,
                                    savedWorkerIds = uiState.savedWorkerIds,
                                    onToggleSaveWorker = { workerId ->
                                        viewModel.toggleSaveWorker(workerId)
                                    },
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

                                4 -> ProfileScreen(
                                    user = uiState.currentUser,
                                    customerBookings = uiState.customerBookings,
                                    savedWorkers = uiState.allWorkers.filter { uiState.savedWorkerIds.contains(it.id) },
                                    language = uiState.currentLanguage,
                                    onUpdateProfile = { name, email, phone, city, avatarUrl ->
                                        viewModel.updateUserProfile(name, email, phone, city, avatarUrl)
                                    },
                                    onToggleLanguage = { viewModel.toggleLanguage() },
                                    onNavigateToJobs = { selectedTab = 2 },
                                    onNavigateToWallet = { isViewingWallet = true },
                                    onWorkerClicked = { worker -> viewModel.selectWorker(worker) },
                                    onOpenDispute = { viewModel.openDisputeDialog() },
                                    onLogout = { viewModel.logout() },
                                    onDeleteAccount = { viewModel.deleteAccount() },
                                    themeMode = uiState.themeMode,
                                    onThemeModeChanged = { viewModel.setThemeMode(it) },
                                    notifJobs = uiState.notifJobs,
                                    notifMessages = uiState.notifMessages,
                                    notifPayments = uiState.notifPayments,
                                    shareLocation = uiState.shareLocation,
                                    onNotificationPrefChanged = { j, m, p -> viewModel.setNotificationPref(j, m, p) },
                                    onShareLocationChanged = { viewModel.setShareLocation(it) },
                                    onOpenVerificationCenter = { isViewingVerificationCenter = true },
                                    onOpenHelpCenter = { isViewingHelpCenter = true }
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
                            language = uiState.currentLanguage,
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
                            language = uiState.currentLanguage,
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
                            val activeJob = uiState.selectedBooking ?: uiState.workerBookings.firstOrNull() ?: uiState.customerBookings.firstOrNull()
                            if (activeJob != null) {
                                JobNavigationScreen(
                                    booking = activeJob,
                                    language = uiState.currentLanguage,
                                    onBack = { selectedTab = 0 },
                                    onMarkArrived = {
                                        viewModel.markWorkerArrived(activeJob.id)
                                    },
                                    onTakeBeforePhoto = { photoUrl ->
                                        viewModel.submitInitialPhoto(activeJob.id, photoUrl)
                                    },
                                    onStartWork = {
                                        viewModel.startWork(activeJob.id)
                                    },
                                    onTakeAfterPhoto = { photoUrl ->
                                        viewModel.submitFinalPhoto(activeJob.id, photoUrl)
                                    },
                                    onGeneratePaymentQr = {
                                        viewModel.requestJobCompletion(activeJob.id)
                                    },
                                    onOpenWorkroomChat = {
                                        activeChatBookingId = activeJob.id
                                    },
                                    onSimulateArrivalAndPhotos = {
                                        viewModel.simulateArrivalAndPhotos(activeJob.id)
                                    }
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
                            onDeleteAccount = { viewModel.deleteAccount() },
                            onOpenVerificationCenter = { isViewingVerificationCenter = true },
                            onOpenHelpCenter = { isViewingHelpCenter = true }
                        )
                    }
                }

                UserRole.ENTERPRISE -> {
                    EnterpriseScreen(
                        user = uiState.currentUser,
                        projects = uiState.enterpriseProjects,
                        workforceRequests = uiState.workforceRequests,
                        onOpenCreateProject = { viewModel.openEnterpriseCreateDialog() },
                        onOpenWorkforceRecruitment = { isViewingWorkforceRecruitment = true },
                        onOpenOrgProfile = {
                            selectedOrganization = uiState.allOrganizations.firstOrNull()
                        }
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
}

    // Interactive Dialogs & Modals
    if (uiState.isBookingDialogVisible && uiState.selectedWorker != null) {
        val worker = uiState.selectedWorker!!
        val service = uiState.selectedServiceForBooking ?: com.example.data.model.ServiceItem(
            id = "srv_flash_plumb",
            workerId = worker.id,
            name = "Plomberie sanitaire (Fuite d'eau standard)",
            category = com.example.data.model.ServiceCategory.PLUMBING,
            description = "Réparation immédiate de fuite sous évier, tuyauterie cuivre / PVC",
            price = 15000.0
        )
        BookingCheckoutModal(
            worker = worker,
            service = service,
            language = uiState.currentLanguage,
            onDismiss = { viewModel.closeBookingDialog() },
            onConfirmEscrowLock = { packageOption, operator, phone, address, notes ->
                viewModel.confirmBookingEscrow(packageOption, operator, phone, address, notes)
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

    if (uiState.isNotificationsDialogVisible) {
        NotificationsDialog(
            notifications = uiState.notifications,
            onDismiss = { viewModel.closeNotificationsDialog() },
            onNotificationClick = { notif ->
                viewModel.markNotificationRead(notif.id)
                viewModel.closeNotificationsDialog()
                when {
                    notif.type == "MESSAGE_RECEIVED" && notif.bookingId != null -> {
                        activeChatBookingId = notif.bookingId
                        isViewingConversations = false
                        isViewingWallet = false
                    }
                    notif.type == "PAYMENT" -> {
                        isViewingWallet = true
                        isViewingConversations = false
                        activeChatBookingId = null
                    }
                    notif.bookingId != null -> {
                        val b = uiState.allBookings.find { it.id == notif.bookingId }
                            ?: uiState.customerBookings.find { it.id == notif.bookingId }
                            ?: uiState.workerBookings.find { it.id == notif.bookingId }
                        if (b != null) {
                            viewModel.selectBooking(b)
                            viewingJobId = b.id
                        }
                    }
                }
            },
            onMarkAllRead = { viewModel.markAllNotificationsRead() }
        )
    }

    if (uiState.isStartTripConfirmationVisible && uiState.pendingStartTripBooking != null) {
        StartTripConfirmationDialog(
            booking = uiState.pendingStartTripBooking!!,
            onDismiss = { viewModel.closeStartTripConfirmation() },
            onConfirm = { viewModel.confirmStartTrip() }
        )
    }

    if (isViewingLocationPicker) {
        CameroonLocationPickerModal(
            selectedQuarter = uiState.selectedQuarter,
            onQuarterSelected = { quarter ->
                viewModel.setQuarter(quarter)
                isViewingLocationPicker = false
            },
            onDismiss = { isViewingLocationPicker = false }
        )
    }
    }
}
}
