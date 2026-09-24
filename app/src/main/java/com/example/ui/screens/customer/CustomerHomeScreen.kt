package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.data.model.Reel
import com.example.data.model.ServiceCategory
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.ui.components.CustomerTopBar
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.LiveStoriesRow
import com.example.ui.components.ProblemDiagnosticSection
import com.example.ui.components.ProblemPill
import com.example.ui.components.ProximityRadarCard
import com.example.ui.components.VerifiedWorkerCard
import com.example.ui.components.WorkerPassportModal
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoElectricAmberDark
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * CHANTIER 3 : INTERFACE CLIENT, DIAGNOSTIC EXPRESS & PASSEPORT CONFIANCE (PRODUCTION-READY)
 *
 * Développé en Dark Obsidian & Gold (#080C15, #1A2232, #FFB800) avec contraste maximal (WCAG AAA)
 * et bilinguisme intégral FR/EN.
 */
@Composable
fun CustomerHomeScreen(
    workers: List<WorkerProfile>,
    reels: List<Reel>,
    activeBookings: List<Booking>,
    searchQuery: String,
    selectedCategory: ServiceCategory?,
    filterVerifiedOnly: Boolean,
    filterEmergencyOnly: Boolean,
    language: AppLanguage,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (ServiceCategory?) -> Unit,
    onToggleVerified: () -> Unit,
    onToggleEmergency: () -> Unit,
    onWorkerClicked: (WorkerProfile) -> Unit,
    onBookingClicked: (Booking) -> Unit,
    onWatchReelsClicked: () -> Unit,
    onWatchSpecificReel: (Reel) -> Unit = {},
    onToggleLanguage: () -> Unit = {},
    onOpenQuarterPicker: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenPatrolMap: () -> Unit = {},
    onConfirmPassportBooking: (WorkerProfile, Boolean, Double) -> Unit = { _, _, _ -> },
    currentQuarterName: String = "Akwa",
    unreadNotificationCount: Int = 1,
    modifier: Modifier = Modifier
) {
    var passportWorker by remember { mutableStateOf<WorkerProfile?>(null) }
    var passportInitialIsFlash by remember { mutableStateOf(false) }
    var selectedProblemPillId by remember { mutableStateOf<String?>(null) }

    val activeJob = activeBookings.firstOrNull {
        it.status != JobStatus.COMPLETED && it.status != JobStatus.CANCELLED
    }

    // Dynamic filtering based on semantic query, selected pill, and emergency flash mode
    val displayWorkers = remember(workers, searchQuery, selectedCategory, filterEmergencyOnly, filterVerifiedOnly) {
        workers.filter { worker ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.lowercase()
                worker.name.lowercase().contains(q) ||
                        worker.skills.lowercase().contains(q) ||
                        worker.category.displayName.lowercase().contains(q) ||
                        // Semantic mapping:
                        (q.contains("eau") || q.contains("robinet") || q.contains("lavabo") || q.contains("fuite")) && worker.category == ServiceCategory.PLUMBING ||
                        (q.contains("compteur") || q.contains("prise") || q.contains("disjoncteur") || q.contains("court-circuit")) && worker.category == ServiceCategory.ELECTRICAL ||
                        (q.contains("froid") || q.contains("clim") || q.contains("climatiseur")) && worker.category == ServiceCategory.HVAC
            }

            val matchesCategory = selectedCategory == null || worker.category == selectedCategory
            val matchesEmergency = !filterEmergencyOnly || worker.emergencyCalloutAvailable
            val matchesVerified = !filterVerifiedOnly || worker.backgroundVerified

            matchesSearch && matchesCategory && matchesEmergency && matchesVerified
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FixoBgCanvas)
            .testTag("customer_home_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // A. Top App Bar Unique (56 dp) — CustomerTopBar
            CustomerTopBar(
                currentQuarterName = currentQuarterName,
                language = language,
                unreadNotificationCount = unreadNotificationCount,
                onQuarterSelectorClick = onOpenQuarterPicker,
                onToggleLanguage = onToggleLanguage,
                onNotificationClick = onOpenNotifications
            )

            // Scrollable Home Feed
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // ACTIVE JOB PRIORITY BANNER
                if (activeJob != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(FixoNavy950, FixoNavy900)
                                    )
                                )
                                .border(1.2.dp, FixoElectricAmber.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .clickable { onBookingClicked(activeJob) }
                                .padding(14.dp)
                                .testTag("active_job_banner")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(FixoEmerald500)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (language == AppLanguage.FR) "TRAVAIL EN COURS" else "ACTIVE JOB IN PROGRESS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = FixoEmerald500,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    JobStatusBadge(status = activeJob.status)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = activeJob.workerAvatar.ifBlank { "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300" },
                                        contentDescription = activeJob.workerName,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, FixoElectricAmber, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = activeJob.serviceTitle,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FixoTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Artisan: ${activeJob.workerName} • ${activeJob.category.displayName}",
                                            fontSize = 11.sp,
                                            color = FixoTextSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = FixoBorderSubtle)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Escrow",
                                            tint = FixoElectricAmber,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Séquestre: ${activeJob.priceAmount.toInt()} FCFA",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FixoElectricAmber
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { onBookingClicked(activeJob) }
                                    ) {
                                        Text(
                                            text = if (language == AppLanguage.FR) "Suivre & Discuter" else "Track & Chat",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FixoTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = FixoElectricAmber,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // B. Radar Vectoriel de Proximité (120 dp) — ProximityRadarCard
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        ProximityRadarCard(
                            activeCount = 14,
                            currentQuarterName = currentQuarterName,
                            language = language,
                            onOpenMap = onOpenPatrolMap
                        )
                    }
                }

                // C. Recherche Sémantique & Diagnostic Express — ProblemDiagnosticSection
                item {
                    ProblemDiagnosticSection(
                        searchQuery = searchQuery,
                        onSearchChanged = { query ->
                            onSearchChanged(query)
                            if (query.isBlank()) {
                                selectedProblemPillId = null
                            }
                        },
                        isFlashUrgentActive = filterEmergencyOnly,
                        onToggleFlashUrgent = onToggleEmergency,
                        selectedPillId = selectedProblemPillId,
                        onPillSelected = { pill ->
                            if (pill == null) {
                                selectedProblemPillId = null
                                onSearchChanged("")
                                onCategorySelected(null)
                            } else {
                                selectedProblemPillId = pill.id
                                onSearchChanged(pill.queryKeywords.first())
                                // Map pill to appropriate ServiceCategory
                                when (pill.id) {
                                    "water_leak" -> onCategorySelected(ServiceCategory.PLUMBING)
                                    "circuit_short" -> onCategorySelected(ServiceCategory.ELECTRICAL)
                                    "ac_broken" -> onCategorySelected(ServiceCategory.AC_COOLING)
                                    "lock_stuck" -> onCategorySelected(ServiceCategory.OTHER)
                                    "crack_masonry" -> onCategorySelected(ServiceCategory.CONSTRUCTION)
                                    "carpentry_wood" -> onCategorySelected(ServiceCategory.CONSTRUCTION)
                                    else -> onCategorySelected(null)
                                }
                            }
                        },
                        language = language
                    )
                }

                // D. Carrousel "En Direct des Chantiers" (Aperçu Reels) — LiveStoriesRow
                if (reels.isNotEmpty()) {
                    item {
                        LiveStoriesRow(
                            reels = reels,
                            language = language,
                            onWatchReel = { reel ->
                                onWatchSpecificReel(reel)
                                onWatchReelsClicked()
                            },
                            onViewAllReels = onWatchReelsClicked
                        )
                    }
                }

                // E. Section Titre : Cartes Artisans Recommandés
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (language == AppLanguage.FR) "Artisans Certifiés Disponibles" else "Available Certified Craftsmen",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "Homologation KYC & Séquestre Garanti • Akwa, Douala"
                                else
                                    "Audited KYC & Escrow Protected • Akwa, Douala",
                                fontSize = 11.sp,
                                color = FixoTextMuted
                            )
                        }
                    }
                }

                // E. Cartes Artisans Recommandés — VerifiedWorkerCard
                if (displayWorkers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = FixoTextMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (language == AppLanguage.FR)
                                        "Aucun artisan trouvé pour cette recherche"
                                    else
                                        "No artisans found matching current criteria",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FixoTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(onClick = {
                                    onSearchChanged("")
                                    selectedProblemPillId = null
                                    onCategorySelected(null)
                                }) {
                                    Text(if (language == AppLanguage.FR) "Réinitialiser les filtres" else "Reset Filters")
                                }
                            }
                        }
                    }
                } else {
                    items(displayWorkers) { worker ->
                        VerifiedWorkerCard(
                            worker = worker,
                            isFlashMode = filterEmergencyOnly,
                            language = language,
                            onCardClick = {
                                passportWorker = worker
                                passportInitialIsFlash = filterEmergencyOnly
                            },
                            onBookClick = {
                                passportWorker = worker
                                passportInitialIsFlash = filterEmergencyOnly
                            }
                        )
                    }
                }

                // Escrow Financial Seal at the bottom
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x3310B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = FixoEmerald500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.FR)
                                        "Protection Séquestre FIXO Intégrale"
                                    else
                                        "100% Escrow Vault Protection",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoTextPrimary
                                )
                                Text(
                                    text = if (language == AppLanguage.FR)
                                        "Fonds débloqués uniquement après votre validation par QR / PIN."
                                    else
                                        "Funds released only upon your QR / PIN inspection approval.",
                                    fontSize = 11.sp,
                                    color = FixoTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. MODALE DÉTAILLÉE : LE PASSEPORT DE CONFIANCE ARTISAN (WorkerPassportModal)
        passportWorker?.let { worker ->
            WorkerPassportModal(
                worker = worker,
                language = language,
                initialIsFlash = passportInitialIsFlash,
                onDismiss = { passportWorker = null },
                onBookConfirmed = { isFlash, price ->
                    val bookedWorker = worker
                    passportWorker = null
                    onConfirmPassportBooking(bookedWorker, isFlash, price)
                }
            )
        }
    }
}
