package com.example.ui.screens.worker

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.data.model.isEnRoute
import com.example.data.model.isOnSite
import com.example.data.model.isWorking
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.LiveTrackingMap
import com.example.ui.screens.chat.MaskedVoipCallDialog
import com.example.ui.screens.chat.ZeroCashOmnipresentBanner
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate800
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobNavigationScreen(
    booking: Booking,
    language: AppLanguage,
    onBack: () -> Unit,
    onMarkArrived: () -> Unit,
    onTakeBeforePhoto: (String) -> Unit,
    onStartWork: () -> Unit,
    onTakeAfterPhoto: (String) -> Unit,
    onGeneratePaymentQr: () -> Unit,
    onOpenWorkroomChat: () -> Unit,
    onSimulateArrivalAndPhotos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showMaskedCall by remember { mutableStateOf(false) }

    val isEnRoute = booking.status.isEnRoute || booking.status == JobStatus.ACCEPTED
    val isOnSite = booking.status.isOnSite
    val isWorking = booking.status.isWorking
    val isPendingClosure = booking.status == JobStatus.COMPLETION_REQUESTED || booking.status == JobStatus.COMPLETED_PENDING_HANDSHAKE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Guidage Artisan & Chantier",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Client : ${booking.customerName} • Akwa",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Chat Workroom shortcut
                    IconButton(
                        onClick = onOpenWorkroomChat,
                        modifier = Modifier.testTag("artisan_open_workroom_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FixoGold500.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Workroom",
                                tint = FixoNavy900,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Masked Call
                    IconButton(onClick = { showMaskedCall = true }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Masked Call",
                            tint = FixoEmerald600
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Zéro Cash Omnipresent Warning Banner
            item {
                ZeroCashOmnipresentBanner(language = language)
            }

            // 2. Sandbox Simulation Button
            item {
                OutlinedButton(
                    onClick = onSimulateArrivalAndPhotos,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("simulate_arrival_photos_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FixoGold600
                    )
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = FixoGold600, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚡ Simuler Arrivée & Photos (Test Sandbox)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // 3. Navigation Map Card with route towards Akwa (Rue Drouot)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("artisan_navigation_map_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            LiveTrackingMap(
                                workerLat = if (booking.workerLat != 0.0) booking.workerLat else 4.0483,
                                workerLng = if (booking.workerLng != 0.0) booking.workerLng else 9.7043,
                                customerLat = if (booking.customerLat != 0.0) booking.customerLat else 4.0511,
                                customerLng = if (booking.customerLng != 0.0) booking.customerLng else 9.7679,
                                workerName = booking.workerName,
                                destinationAddress = "Rue Drouot, Akwa",
                                isTrackingActive = isEnRoute,
                                workerSpeedKmh = 26f,
                                workerHeading = 35f,
                                etaMinutes = 8,
                                distanceKm = 2.1,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Telemetry badge overlay
                            Surface(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.TopStart),
                                shape = RoundedCornerShape(20.dp),
                                color = FixoNavy900.copy(alpha = 0.85f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isEnRoute) "En moto • 26 km/h • ~8 min (2.1 km)" else "Arrivé sur site",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Address & External GPS App button
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = FixoRed500, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Destination Client : Rue Drouot, Akwa, Douala",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Accès portail noir • Code sonnette 24B",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // [ 🗺️ Ouvrir dans Google Maps / Waze ]
                            OutlinedButton(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:${booking.customerLat},${booking.customerLng}?q=${Uri.encode("Rue Drouot, Akwa, Douala")}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(Intent.createChooser(mapIntent, "Ouvrir dans Google Maps / Waze"))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("open_external_map_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = FixoStrings.get("workroom.nav.open_external_map", language),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Prominent Arrival Button (52 dp, Emerald green #10B981)
            if (isEnRoute) {
                item {
                    Button(
                        onClick = onMarkArrived,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("mark_arrived_on_site_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "📍 JE SUIS ARRIVÉ SUR PLACE",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                }
            }

            // 5. Photo Inspection Protocol Card
            if (isOnSite || isWorking || isPendingClosure) {
                item {
                    PhotoInspectionProtocolCard(
                        booking = booking,
                        isArtisan = true,
                        language = language,
                        onTakeBeforePhoto = onTakeBeforePhoto,
                        onTakeAfterPhoto = onTakeAfterPhoto,
                        onStartWork = onStartWork,
                        onGeneratePaymentQr = onGeneratePaymentQr
                    )
                }
            }

            // 6. Direct Access to Live Workroom Chat
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenWorkroomChat() }
                        .testTag("open_workroom_chat_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(FixoGold500.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = FixoNavy900)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Salon de Chantier en Direct",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Notes vocales PTT, annotations sur photos & VoIP",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = FixoEmerald500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "En direct 🟢",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = FixoEmerald600,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMaskedCall) {
        MaskedVoipCallDialog(
            otherPartyName = booking.customerName,
            otherPartyAvatar = "",
            onDismiss = { showMaskedCall = false }
        )
    }
}
