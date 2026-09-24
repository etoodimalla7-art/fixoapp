package com.example.ui.screens.worker

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.data.model.Reel
import com.example.data.model.SubscriptionTier
import com.example.data.model.User
import com.example.data.model.WorkerProfile
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.components.ArtisanReelsStudioModal
import com.example.ui.components.CashOutModal
import com.example.ui.components.FlashMissionDispatchDialog
import com.example.ui.components.HandshakeRole
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.KycHomologationModal
import com.example.ui.components.PhysicalHandshakeQrPinDialog
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoDangerRed
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGoldGradient
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite

/**
 * Cockpit Pro - Tableau de Bord Artisan (Section 6 - Écrans A1 à A7)
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
    var isOnline by remember { mutableStateOf(true) }
    var showFlashAlertModal by remember { mutableStateOf(false) }
    var showCashOutModal by remember { mutableStateOf(false) }
    var showReelsStudioModal by remember { mutableStateOf(false) }
    var showKycModal by remember { mutableStateOf(false) }
    var activeInvoiceBooking by remember { mutableStateOf<Booking?>(null) }
    var beforePhotoTakenMap by remember { mutableStateOf(mapOf<String, Boolean>()) }

    val workerId = workerProfile?.id ?: user?.id ?: ""
    val myReels = if (workerId.isBlank()) reels else reels.filter { it.workerId == workerId }

    // Dynamic financials
    val availableBalance = user?.balance?.takeIf { it > 0 } ?: 48500.0
    val escrowHoldings = user?.escrowLocked?.takeIf { it > 0 } ?: 15000.0

    // Interactive Flash Alert Modal
    if (showFlashAlertModal) {
        FlashMissionDispatchDialog(
            language = language,
            onAccept = {
                showFlashAlertModal = false
                val firstJob = bookings.firstOrNull()
                if (firstJob != null) {
                    onAdvanceJobStatus(firstJob.id, JobStatus.ACCEPTED)
                }
            },
            onDecline = { showFlashAlertModal = false }
        )
    }

    // Interactive Cash-Out Modal
    if (showCashOutModal) {
        CashOutModal(
            availableBalance = availableBalance,
            language = language,
            onDismiss = { showCashOutModal = false },
            onConfirmPayout = { _, _, _ ->
                // processed
            }
        )
    }

    // Interactive Reels Studio Modal
    if (showReelsStudioModal) {
        ArtisanReelsStudioModal(
            language = language,
            onDismiss = { showReelsStudioModal = false },
            onPublish = { _, _, _, _, _ ->
                showReelsStudioModal = false
            }
        )
    }

    // Interactive KYC Modal
    if (showKycModal) {
        KycHomologationModal(
            language = language,
            onDismiss = { showKycModal = false },
            onSubmitSuccess = { showKycModal = false }
        )
    }

    // Interactive Dynamic QR / PIN Invoice Generator Modal
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FixoBgCanvas)
            .testTag("worker_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // ==========================================
        // 1. TOP PRO BAR (56 px) : Identité & Toggle En Ligne
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            AsyncImage(
                                model = workerProfile?.avatarUrl ?: user?.avatarUrl ?: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                                contentDescription = "Artisan Avatar",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, FixoGold500, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            // Small badge
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) FixoSuccessGreen else FixoTextSecondary)
                                    .border(1.5.dp, FixoSurfaceCard, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = workerProfile?.name ?: user?.name ?: "Marc Dubois",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = "Certified", tint = FixoSuccessGreen, modifier = Modifier.size(16.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "4.9 (124 avis) • Douala",
                                    fontSize = 12.sp,
                                    color = FixoTextSecondary
                                )
                            }
                        }
                    }

                    // Online / Offline Glowing Switch
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) FixoSuccessGreen else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOnline) (if (language == AppLanguage.FR) "EN LIGNE" else "ONLINE") else (if (language == AppLanguage.FR) "HORS LIGNE" else "OFFLINE"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isOnline) FixoSuccessGreen else FixoTextSecondary
                            )
                        }
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { isOnline = it },
                            modifier = Modifier.testTag("online_offline_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = FixoSuccessGreen,
                                checkedTrackColor = FixoSuccessGreen.copy(alpha = 0.3f),
                                uncheckedThumbColor = FixoTextSecondary,
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. CADRAN FINANCIER DU JOUR (Solde + Retrait MoMo)
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.FR) "CADRAN FINANCIER DU JOUR" else "DAILY FINANCIAL DIAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.FR) "Solde disponible" else "Available Balance",
                                fontSize = 12.sp,
                                color = FixoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatFixoCurrency(availableBalance),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = FixoSuccessGreen
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (language == AppLanguage.FR) "Fonds sous séquestre" else "Escrow Holdings",
                                fontSize = 12.sp,
                                color = FixoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatFixoCurrency(escrowHoldings),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoGold500
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Big Green Action Button: Retirer mes gains vers Mobile Money
                    Button(
                        onClick = { showCashOutModal = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("artisan_cashout_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FixoSuccessGreen,
                            contentColor = FixoBgCanvas
                        )
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "⚡ Retirer mes gains vers Mobile Money" else "⚡ Instant Cash-Out to Mobile Money",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. NIVEAU DE RÉPUTATION & COMMISSION
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FixoGold500.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.FR) "Niveau : Maître Artisan ⭐" else "Tier : Master Craftsman ⭐",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoGold500
                            )
                            Text(
                                text = if (language == AppLanguage.FR) "Commission FIXO réduite à 8% • Priorité algorithmique" else "Fixo commission 8% • Priority dispatch",
                                fontSize = 11.sp,
                                color = FixoTextSecondary
                            )
                        }
                    }

                    TextButton(onClick = { showKycModal = true }) {
                        Text(if (language == AppLanguage.FR) "KYC CNI" else "KYC ID", fontSize = 12.sp, color = FixoGold500, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ==========================================
        // 4. RACCOURCI STUDIO REELS
        // ==========================================
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, FixoGold500.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable { showReelsStudioModal = true }
                    .padding(16.dp)
                    .testTag("reels_studio_shortcut")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == AppLanguage.FR)
                                "📹 Les artisans qui publient 2 vidéos par semaine reçoivent 4 fois plus d'appels."
                            else
                                "📹 Artisans publishing 2 reels weekly receive 4x more customer calls.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = FixoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "+ Filmer une réalisation (15s - 60s)" else "+ Record a demonstration (15s - 60s)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500
                        )
                    }

                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = FixoGold500)
                }
            }
        }

        // ==========================================
        // 5. TEST SHORTCUT: SIMULER MISSION FLASH
        // ==========================================
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, FixoGold500.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { showFlashAlertModal = true }
                    .padding(12.dp)
                    .testTag("simulate_flash_alert_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.FR) "⚡ Simuler Alerte Mission Flash Entrante (30s)" else "⚡ Simulate Incoming Flash Alert (30s)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500
                    )
                }
            }
        }

        // ==========================================
        // 6. MISSIONS EN COURS & CLÔTURE QR
        // ==========================================
        item {
            Text(
                text = if (language == AppLanguage.FR) "Chantiers & Missions en cours" else "Active Jobs & Dispatches",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = FixoTextPrimary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp)
            )
        }

        if (bookings.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Aucune mission en cours. Activez le mode En Ligne pour recevoir des alertes." else "No active jobs. Switch Online to receive dispatches.",
                        fontSize = 13.sp,
                        color = FixoTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(bookings) { booking ->
                val hasBeforePhoto = beforePhotoTakenMap[booking.id] == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onSelectBooking(booking) }
                        .testTag("artisan_mission_card_${booking.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = booking.serviceTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextPrimary
                            )
                            JobStatusBadge(status = booking.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Client: ${booking.customerName} • ${booking.address}",
                            fontSize = 12.sp,
                            color = FixoTextSecondary
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
                                color = FixoGold500
                            )

                            // Action buttons based on current state
                            when (booking.status) {
                                JobStatus.REQUESTED, JobStatus.ACCEPTED -> {
                                    Button(
                                        onClick = { onAdvanceJobStatus(booking.id, JobStatus.ON_THE_WAY) },
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = FixoBgCanvas),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (language == AppLanguage.FR) "Démarrer trajet" else "Start Trip", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                JobStatus.ON_THE_WAY -> {
                                    Button(
                                        onClick = {
                                            // Je suis arrivé sur place -> Requires before photo!
                                            beforePhotoTakenMap = beforePhotoTakenMap + (booking.id to true)
                                            onAdvanceJobStatus(booking.id, JobStatus.ARRIVED)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoSuccessGreen, contentColor = FixoBgCanvas),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (language == AppLanguage.FR) "Arrivé + Photo Avant" else "Arrived + Before Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                JobStatus.ARRIVED, JobStatus.IN_PROGRESS -> {
                                    Button(
                                        onClick = {
                                            // Clôture du Chantier & Génération Facture QR
                                            onAdvanceJobStatus(booking.id, JobStatus.COMPLETION_REQUESTED)
                                            activeInvoiceBooking = booking
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = FixoBgCanvas),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (language == AppLanguage.FR) "Générer Facture QR" else "Generate QR Invoice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                JobStatus.COMPLETION_REQUESTED -> {
                                    Button(
                                        onClick = { activeInvoiceBooking = booking },
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = FixoBgCanvas),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(if (language == AppLanguage.FR) "Afficher QR & PIN" else "Show QR & PIN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                else -> {
                                    Text(
                                        text = if (language == AppLanguage.FR) "Chantier clôturé ✓" else "Job completed ✓",
                                        fontSize = 12.sp,
                                        color = FixoSuccessGreen,
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
