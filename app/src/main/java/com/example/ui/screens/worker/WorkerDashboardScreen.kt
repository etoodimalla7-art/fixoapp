package com.example.ui.screens.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArtisanKycStatus
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.data.model.Reel
import com.example.data.model.SubscriptionTier
import com.example.data.model.User
import com.example.data.model.VerificationStatus
import com.example.data.model.WorkerProfile
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.components.ArtisanReelsStudioModal
import com.example.ui.components.CashOutModal
import com.example.ui.components.HandshakeRole
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.KycHomologationModal
import com.example.ui.components.PhysicalHandshakeQrPinDialog
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * CHANTIER 5 : COCKPIT ARTISAN PRO (MODE CHAUFFEUR UBER & SLIDE TO ACCEPT)
 *
 * Réservé exclusivement aux artisans homologués (KYC validé).
 * - Top Pro Bar (56 dp) avec avatar certifié, niveau doré et commutateur En Ligne/Pause
 * - Cadran Financier Transparent : Solde disponible 48 500 FCFA vs Séquestre 15 000 FCFA
 * - Indicateurs d'activité : 4.9⭐, 98% ponctualité, Maître Artisan 8%
 * - Raccourcis Studio Reels & Planning d'intervention
 * - Alerte plein écran prioritaire de mission Flash avec compte à rebours 30s
 *   et glissière tactile de sécurité « Slide to Accept » (seuil >= 85%)
 */
@Composable
fun WorkerDashboardScreen(
    user: User?,
    workerProfile: WorkerProfile?,
    bookings: List<Booking>,
    reels: List<Reel>,
    onSelectBooking: (Booking) -> Unit,
    onAdvanceJobStatus: (String, JobStatus) -> Unit,
    onOpenUploadReel: () -> Unit,
    onOpenAvailability: () -> Unit,
    onOpenSubscription: () -> Unit,
    onOpenWithdraw: () -> Unit,
    onDeleteReel: (String) -> Unit = {},
    language: AppLanguage = AppLanguage.FR,
    modifier: Modifier = Modifier
) {
    // État Machine Cockpit
    var cockpitStatus by remember { mutableStateOf(CockpitStatus.ONLINE_IDLE) }
    var isOnline by remember { mutableStateOf(true) }
    var activeFlashMission by remember { mutableStateOf<FlashMissionAlert?>(null) }

    // Modales secondaires
    var showCashOutModal by remember { mutableStateOf(false) }
    var showReelsStudioModal by remember { mutableStateOf(false) }
    var showKycModal by remember { mutableStateOf(false) }
    var activeInvoiceBooking by remember { mutableStateOf<Booking?>(null) }
    var beforePhotoTakenMap by remember { mutableStateOf(mapOf<String, Boolean>()) }

    // Vérification de sécurité KYC : Profil homologué uniquement
    val isKycApproved = user?.verificationStatus == VerificationStatus.VERIFIED_PRO ||
            user?.verificationStatus == VerificationStatus.MASTER_CRAFTSMAN

    // Données financières dynamiques
    val availableBalance = user?.balance?.takeIf { it > 0 } ?: 48500.0
    val escrowHoldings = user?.escrowLocked?.takeIf { it > 0 } ?: 15000.0

    // Synchronisation de l'état en ligne
    fun handleToggleOnline(online: Boolean) {
        isOnline = online
        cockpitStatus = if (online) CockpitStatus.ONLINE_IDLE else CockpitStatus.OFFLINE
    }

    // Déclenchement d'une alerte Flash de simulation
    fun triggerSimulatedFlashMission() {
        if (!isOnline) {
            isOnline = true
        }
        activeFlashMission = FlashMissionAlert(
            id = "flash_sim_${System.currentTimeMillis()}",
            title = "Fuite d'eau standard — Dépannage Immédiat",
            tradeName = "Plomberie sanitaire (Fuite d'eau standard)",
            address = "Akwa, Rue Drouot",
            distanceKm = 1.2,
            etaMinutes = 6,
            customerName = "Sarah Jenkins",
            grossAmount = 15000.0,
            netAmount = 13500.0,
            timeoutSeconds = 30
        )
        cockpitStatus = CockpitStatus.ALERT_RINGING
    }

    // ==========================================
    // 1. ALERTE PLEIN ÉCRAN PRIORITAIRE FLASH (CHANTIER 5)
    // ==========================================
    if (cockpitStatus == CockpitStatus.ALERT_RINGING && activeFlashMission != null) {
        IncomingJobAlertOverlay(
            mission = activeFlashMission!!,
            language = language,
            onAccept = { acceptedMission ->
                cockpitStatus = CockpitStatus.JOB_ASSIGNED
                activeFlashMission = null
                // Si un chantier est disponible, avancer vers ACCEPTED
                val existing = bookings.find { it.id == acceptedMission.id }
                    ?: bookings.firstOrNull()
                if (existing != null) {
                    onAdvanceJobStatus(existing.id, JobStatus.ARTISAN_EN_ROUTE)
                    onSelectBooking(existing)
                }
            },
            onDecline = {
                cockpitStatus = if (isOnline) CockpitStatus.ONLINE_IDLE else CockpitStatus.OFFLINE
                activeFlashMission = null
            },
            onTimeout = {
                cockpitStatus = if (isOnline) CockpitStatus.ONLINE_IDLE else CockpitStatus.OFFLINE
                activeFlashMission = null
            }
        )
    }

    // Modal de Retrait Cash-Out
    if (showCashOutModal) {
        CashOutModal(
            availableBalance = availableBalance,
            language = language,
            onDismiss = { showCashOutModal = false },
            onConfirmPayout = { _, _, _ ->
                showCashOutModal = false
            }
        )
    }

    // Modal Studio Reels
    if (showReelsStudioModal) {
        ArtisanReelsStudioModal(
            language = language,
            onDismiss = { showReelsStudioModal = false },
            onPublish = { _, _, _, _, _ ->
                showReelsStudioModal = false
                onOpenUploadReel()
            }
        )
    }

    // Modal KYC Homologation
    if (showKycModal) {
        KycHomologationModal(
            language = language,
            onDismiss = { showKycModal = false },
            onSubmitSuccess = { showKycModal = false }
        )
    }

    // Modal Facture QR / PIN
    activeInvoiceBooking?.let { booking ->
        PhysicalHandshakeQrPinDialog(
            booking = booking,
            role = HandshakeRole.ARTISAN_GENERATOR,
            language = language,
            onDismiss = { activeInvoiceBooking = null },
            onCompletedSuccessfully = {
                onAdvanceJobStatus(booking.id, JobStatus.COMPLETED)
                activeInvoiceBooking = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FixoBgCanvas)
            .testTag("worker_dashboard_screen")
    ) {
        // ==========================================
        // 2. TOP PRO BAR (56 dp) : Identité, Micro-label & Toggle
        // ==========================================
        WorkerTopBar(
            user = user,
            workerProfile = workerProfile,
            isOnline = isOnline,
            onToggleOnline = { handleToggleOnline(it) },
            notificationCount = 2,
            language = language,
            onSimulateFlashAlert = { triggerSimulatedFlashMission() },
            onNotificationsClick = { /* Notifications */ }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avertissement si KYC incomplet
            if (!isKycApproved) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.dp, FixoElectricAmber),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = FixoElectricAmber)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == AppLanguage.FR) "Homologation KYC Requise" else "KYC Homologation Required",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (language == AppLanguage.FR) "Déposez votre CNI et casier pour débloquer les dépannages Flash." else "Submit ID & record to unlock Flash dispatches.",
                                    color = FixoTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            TextButton(onClick = { showKycModal = true }) {
                                Text(if (language == AppLanguage.FR) "Soumettre" else "Submit", color = FixoElectricAmber, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 3. LE CADRAN FINANCIER TRANSPARENT (EarningsCard)
            // ==========================================
            item {
                EarningsCard(
                    availableBalance = availableBalance,
                    escrowBalance = escrowHoldings,
                    onCashOutClick = { showCashOutModal = true },
                    language = language
                )
            }

            // ==========================================
            // 4. INDICATEURS D'ACTIVITÉ & PROGRESSION (WorkerStatsRow)
            // ==========================================
            item {
                WorkerStatsRow(
                    rating = workerProfile?.rating ?: 4.9,
                    reviewCount = workerProfile?.reviewCount ?: 124,
                    punctualityPercent = 98,
                    commissionTierName = if (language == AppLanguage.FR) "Maître Artisan (8%)" else "Master Craftsman (8%)",
                    remainingJobsToKeepPrivilege = 3,
                    progressFraction = 0.85f,
                    language = language
                )
            }

            // ==========================================
            // 5. RACCOURCIS OPÉRATIONNELS : REELS STUDIO
            // ==========================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showReelsStudioModal = true }
                        .testTag("reels_studio_shortcut"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                    border = BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(Color(0xFF7C3AED).copy(alpha = 0.5f), FixoElectricAmber.copy(alpha = 0.5f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val reelsTitle = FixoStrings.getString("worker.reels_prompt.title", language)
                            val reelsSub = FixoStrings.getString("worker.reels_prompt.subtitle", language)
                            val reelsCta = FixoStrings.getString("worker.reels_prompt.cta", language)

                            Text(
                                text = reelsTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = reelsSub,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = FixoElectricAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = reelsCta,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FixoElectricAmber
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 6. PLANNING & FILE D'ATTENTE
            // ==========================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                    border = BorderStroke(1.dp, Color(0x1FFFFFFF))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        val nextJobTitle = FixoStrings.getString("worker.planning.next_job_title", language)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = nextJobTitle.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Text(
                                text = "15:30",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = FixoElectricAmber
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Réparation tableau électrique à Deido (M. Jean-Paul K.)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            // ==========================================
            // 7. CHANTIERS EN COURS & CLÔTURE QR
            // ==========================================
            item {
                Text(
                    text = if (language == AppLanguage.FR) "Missions & Interventions du Jour" else "Today's Active Missions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (bookings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isOnline) {
                                if (language == AppLanguage.FR) "🟢 En attente de missions... Restez à proximité des zones denses." else "🟢 Waiting for dispatches... Stay near busy quarters."
                            } else {
                                if (language == AppLanguage.FR) "⚪ Vous êtes en pause. Basculez « EN LIGNE » pour recevoir des alertes." else "⚪ You are paused. Switch « ONLINE » to receive alerts."
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(bookings) { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectBooking(booking) }
                            .testTag("artisan_mission_card_${booking.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                        border = BorderStroke(1.dp, Color(0x1FFFFFFF))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = booking.serviceTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                JobStatusBadge(status = booking.status)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${booking.customerName} • ${booking.address}",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Séquestre : ${formatFixoCurrency(booking.priceAmount)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoElectricAmber
                                )

                                when (booking.status) {
                                    JobStatus.REQUESTED, JobStatus.ACCEPTED -> {
                                        Button(
                                            onClick = { onAdvanceJobStatus(booking.id, JobStatus.ON_THE_WAY) },
                                            colors = ButtonDefaults.buttonColors(containerColor = FixoElectricAmber, contentColor = Color(0xFF080C15)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(if (language == AppLanguage.FR) "Démarrer trajet" else "Start Trip", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    JobStatus.ON_THE_WAY -> {
                                        Button(
                                            onClick = {
                                                beforePhotoTakenMap = beforePhotoTakenMap + (booking.id to true)
                                                onAdvanceJobStatus(booking.id, JobStatus.ARRIVED)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = FixoEmerald500, contentColor = Color(0xFF080C15)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (language == AppLanguage.FR) "Arrivé + Photo" else "Arrived + Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    JobStatus.ARRIVED, JobStatus.IN_PROGRESS -> {
                                        Button(
                                            onClick = {
                                                onAdvanceJobStatus(booking.id, JobStatus.COMPLETION_REQUESTED)
                                                activeInvoiceBooking = booking
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = FixoElectricAmber, contentColor = Color(0xFF080C15)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (language == AppLanguage.FR) "Facture QR" else "QR Invoice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    JobStatus.COMPLETION_REQUESTED -> {
                                        Button(
                                            onClick = { activeInvoiceBooking = booking },
                                            colors = ButtonDefaults.buttonColors(containerColor = FixoElectricAmber, contentColor = Color(0xFF080C15)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(if (language == AppLanguage.FR) "Afficher QR/PIN" else "Show QR/PIN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    else -> {
                                        Text(
                                            text = if (language == AppLanguage.FR) "Terminé ✓" else "Completed ✓",
                                            fontSize = 12.sp,
                                            color = FixoEmerald500,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
