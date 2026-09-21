package com.example.ui.screens.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.UserRole
import com.example.data.model.WorkerLocation
import com.example.data.model.formatFixoCurrency
import com.example.ui.components.EscrowBadge
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.LiveTrackingMap
import com.example.ui.theme.FixoAmber100
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoBlue700
import com.example.ui.theme.FixoEmerald100
import com.example.ui.theme.FixoEmerald50
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoRed50
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate300
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700
import com.example.ui.theme.FixoSlate800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JobTrackingScreen(
    booking: Booking,
    chatMessages: List<ChatMessage>,
    activeLocation: WorkerLocation?,
    currentRole: UserRole,
    onBack: () -> Unit,
    onStartTrip: () -> Unit,
    onMarkArrived: () -> Unit,
    onStartWork: () -> Unit,
    onRequestCompletion: () -> Unit,
    onAdvanceStatus: (JobStatus) -> Unit,
    onOpenReview: () -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenDispute: () -> Unit,
    onCancelBooking: () -> Unit,
    onOpenFullChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var messageInput by remember { mutableStateOf("") }
    var showCancelDialog by remember { mutableStateOf(false) }

    // Dynamic coordinates resolution
    val workerLat = activeLocation?.latitude ?: (if (booking.workerLat != 0.0) booking.workerLat else 4.0380)
    val workerLng = activeLocation?.longitude ?: (if (booking.workerLng != 0.0) booking.workerLng else 9.6990)
    val customerLat = if (booking.customerLat != 0.0) booking.customerLat else 4.0511
    val customerLng = if (booking.customerLng != 0.0) booking.customerLng else 9.7679
    val isTrackingLive = booking.status == JobStatus.ON_THE_WAY && (booking.trackingActive || activeLocation?.isTrackingActive == true)

    val etaMinutes = if (isTrackingLive) booking.etaMinutes.coerceAtLeast(1) else 0
    val distanceKm = if (isTrackingLive) booking.distanceKm else 0.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. TOP APP BAR
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("job_track_back")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Job #${booking.id}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                if (isTrackingLive) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(FixoEmerald500)
                                    )
                                }
                            }
                            Text(
                                text = booking.serviceTitle,
                                style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = onOpenDispute,
                            modifier = Modifier.testTag("job_dispute_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = FixoRed500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispute", color = FixoRed500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. LIVE WORKER TRACKING MAP SECTION (Phase 7 & 8)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                // Tracking Status Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (booking.status) {
                        JobStatus.ON_THE_WAY -> FixoBlue50
                        JobStatus.ARRIVED -> FixoEmerald50
                        JobStatus.IN_PROGRESS -> FixoAmber100
                        JobStatus.COMPLETED -> FixoEmerald50
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (booking.status) {
                                    JobStatus.ON_THE_WAY -> Icons.Default.Navigation
                                    JobStatus.ARRIVED -> Icons.Default.CheckCircle
                                    JobStatus.IN_PROGRESS -> Icons.Default.Build
                                    JobStatus.COMPLETED -> Icons.Default.ThumbUp
                                    else -> Icons.Default.AccessTime
                                },
                                contentDescription = null,
                                tint = when (booking.status) {
                                    JobStatus.ON_THE_WAY -> FixoBlue700
                                    JobStatus.ARRIVED -> FixoEmerald600
                                    JobStatus.IN_PROGRESS -> FixoAmber600
                                    JobStatus.COMPLETED -> FixoEmerald600
                                    else -> FixoSlate700
                                },
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = when (booking.status) {
                                        JobStatus.ON_THE_WAY -> "Artisan is en route"
                                        JobStatus.ARRIVED -> "Artisan arrived on site"
                                        JobStatus.IN_PROGRESS -> "Work in progress"
                                        JobStatus.COMPLETION_REQUESTED -> "Inspection requested"
                                        JobStatus.COMPLETED -> "Service completed & paid"
                                        JobStatus.CANCELLED -> "Job cancelled & refunded"
                                        JobStatus.SCHEDULED -> "Scheduled for ${booking.date}"
                                        else -> "Booking confirmed"
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = when (booking.status) {
                                        JobStatus.ON_THE_WAY -> "ETA: $etaMinutes min ($distanceKm km away)"
                                        JobStatus.ARRIVED -> "Location tracking closed. On-site inspection begun."
                                        JobStatus.IN_PROGRESS -> "Diagnosing and repairing service items."
                                        JobStatus.COMPLETION_REQUESTED -> "Please inspect work and release escrow."
                                        JobStatus.COMPLETED -> "Escrow released to artisan."
                                        else -> "Destination: ${booking.address}"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                                )
                            }
                        }

                        if (isTrackingLive) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FixoEmerald500
                            ) {
                                Text(
                                    text = "LIVE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // High-performance Live Vector Tracking Canvas
                LiveTrackingMap(
                    workerLat = workerLat,
                    workerLng = workerLng,
                    customerLat = customerLat,
                    customerLng = customerLng,
                    workerName = booking.workerName,
                    destinationAddress = booking.address,
                    isTrackingActive = isTrackingLive,
                    workerSpeedKmh = booking.workerSpeedKmh,
                    workerHeading = booking.workerHeading,
                    etaMinutes = etaMinutes,
                    distanceKm = distanceKm
                )

                // Location privacy notice (Phase 8 Requirement)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = FixoSlate500,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Job-scoped GPS privacy: Location tracking is restricted to this appointment and terminates upon arrival.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = FixoSlate500,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // 3. ARTISAN PROFILE CARD (Rating, Badges, Direct Call)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = booking.workerAvatar,
                            contentDescription = booking.workerName,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = booking.workerName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Background Verified",
                                    tint = FixoBlue600,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${booking.category.displayName} • Verified Professional",
                                style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = FixoGold500,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "4.9 (124 jobs completed)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = FixoSlate700,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        // Direct Call Action
                        Surface(
                            shape = CircleShape,
                            color = FixoEmerald50,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${booking.workerPhone}")
                                    }
                                    try {
                                        context.startActivity(dialIntent)
                                    } catch (_: Exception) {}
                                }
                                .testTag("call_worker_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call Artisan",
                                    tint = FixoEmerald600,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. SERVICE & APPOINTMENT DETAILS CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        JobStatusBadge(status = booking.status)
                        EscrowBadge(status = booking.escrowStatus, amount = booking.priceAmount)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = FixoSlate100)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Service",
                                style = MaterialTheme.typography.labelSmall.copy(color = FixoSlate500)
                            )
                            Text(
                                text = booking.serviceTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Service Price",
                                style = MaterialTheme.typography.labelSmall.copy(color = FixoSlate500)
                            )
                            Text(
                                text = formatFixoCurrency(booking.priceAmount),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoEmerald600
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Appointment Date & Time",
                                style = MaterialTheme.typography.labelSmall.copy(color = FixoSlate500)
                            )
                            Text(
                                text = "${booking.date} (${booking.timeSlot})",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Payment Method",
                                style = MaterialTheme.typography.labelSmall.copy(color = FixoSlate500)
                            )
                            Text(
                                text = booking.paymentMethod.label,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column {
                        Text(
                            text = "Site Address",
                            style = MaterialTheme.typography.labelSmall.copy(color = FixoSlate500)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = FixoEmerald600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = booking.address,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    if (booking.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = FixoSlate100)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Customer Instructions & Site Notes",
                            style = MaterialTheme.typography.labelSmall.copy(color = FixoSlate500)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = booking.notes,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // 5. JOB LIFECYCLE PROGRESS & ACTIONS (Phase 10 & 11)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Service Lifecycle Progress",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val lifecycleSteps = listOf(
                        JobStatus.REQUESTED to "Service Requested",
                        JobStatus.ACCEPTED to "Artisan Accepted",
                        JobStatus.ON_THE_WAY to "Artisan En Route (Live Tracking)",
                        JobStatus.ARRIVED to "Artisan Arrived on Site",
                        JobStatus.IN_PROGRESS to "Work in Progress",
                        JobStatus.COMPLETION_REQUESTED to "Inspection Ready",
                        JobStatus.COMPLETED to "Approved & Escrow Released"
                    )

                    val currentIdx = lifecycleSteps.indexOfFirst {
                        it.first == booking.status || (booking.status == JobStatus.SCHEDULED && it.first == JobStatus.ACCEPTED)
                    }.coerceAtLeast(0)

                    lifecycleSteps.forEachIndexed { index, (status, label) ->
                        val isDone = index <= currentIdx
                        val isCurrent = index == currentIdx

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> FixoBlue600
                                            isDone -> FixoEmerald600
                                            else -> FixoSlate200
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = FixoSlate500,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) FixoBlue700 else if (isDone) MaterialTheme.colorScheme.onSurface else FixoSlate500
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION CONTROLS ACCORDING TO USER ROLE & STATE MACHINE
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Worker Actions
                        if (currentRole == UserRole.WORKER || currentRole == UserRole.ADMIN) {
                            when (booking.status) {
                                JobStatus.REQUESTED -> {
                                    Button(
                                        onClick = { onAdvanceStatus(JobStatus.ACCEPTED) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Accept Service Request", fontWeight = FontWeight.Bold)
                                    }
                                }
                                JobStatus.ACCEPTED, JobStatus.SCHEDULED -> {
                                    Button(
                                        onClick = onStartTrip,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("worker_start_trip_button")
                                    ) {
                                        Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Start Trip to Site (Activate GPS)", fontWeight = FontWeight.Bold)
                                    }
                                }
                                JobStatus.ON_THE_WAY -> {
                                    Button(
                                        onClick = onMarkArrived,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoEmerald600),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("worker_mark_arrived_button")
                                    ) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("I Have Arrived (Stop Tracking)", fontWeight = FontWeight.Bold)
                                    }
                                }
                                JobStatus.ARRIVED -> {
                                    Button(
                                        onClick = onStartWork,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("worker_start_work_button")
                                    ) {
                                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Start Diagnostic & Work", fontWeight = FontWeight.Bold)
                                    }
                                }
                                JobStatus.IN_PROGRESS -> {
                                    Button(
                                        onClick = onRequestCompletion,
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FixoGold500),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("worker_request_completion_button")
                                    ) {
                                        Text("Work Finished — Request Inspection & Release", fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                                else -> {}
                            }
                        }

                        // Customer Actions: Cancellation before transit
                        if (booking.status == JobStatus.REQUESTED || booking.status == JobStatus.ACCEPTED || booking.status == JobStatus.SCHEDULED) {
                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoRed500),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cancel_booking_button")
                            ) {
                                Text("Cancel Booking & Refund Escrow", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // 6. ESCROW RELEASE CARD (For Customer when work is ready)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FixoEmerald50),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoEmerald600.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = "Escrow", tint = FixoEmerald600, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FIXO Escrow: ${formatFixoCurrency(booking.priceAmount)} Secured",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = FixoEmerald600)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (booking.escrowStatus == EscrowStatus.RELEASED)
                            "Payment has been released to ${booking.workerName}. Thank you for using FIXO Escrow Protection!"
                        else
                            "Funds are safely locked in platform escrow. Release payment only after the technician has completed the work to your full satisfaction.",
                        style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate700)
                    )

                    if (booking.escrowStatus == EscrowStatus.HOLDING) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onOpenReview,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("release_escrow_cta"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoEmerald600)
                        ) {
                            Text(
                                text = "Inspect, Release Escrow & Review (+50 Points)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 7. COMPLETED REVIEW CARD (If completed)
        if (booking.customerRating > 0f) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Verified Review",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { i ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (i < booking.customerRating.toInt()) FixoGold500 else FixoSlate200,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        if (booking.customerReviewText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "\"${booking.customerReviewText}\"",
                                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Reward: +${booking.pointsEarned.coerceAtLeast(50)} FIXO Loyalty Points Credited",
                            style = MaterialTheme.typography.labelSmall.copy(color = FixoEmerald600, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // 8. DIRECT MESSAGES WITH ARTISAN (Phase 12)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Direct Messages (${booking.workerName})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${chatMessages.size} messages • Encrypted & Job-Scoped",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 11.sp)
                        )
                    }
                    TextButton(
                        onClick = onOpenFullChat,
                        modifier = Modifier.testTag("open_full_chat_button")
                    ) {
                        Text("Open Full Chat", color = FixoBlue600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Chat message bubbles
        items(chatMessages) { msg ->
            val isMe = (currentRole == UserRole.CUSTOMER && msg.senderRole == UserRole.CUSTOMER) ||
                    (currentRole == UserRole.WORKER && msg.senderRole == UserRole.WORKER)
            val isSystem = msg.senderRole == UserRole.ADMIN || msg.senderId == "system"

            if (isSystem) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "🔔 ${msg.message}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = FixoSlate700,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Text(
                        text = if (isMe) "You" else msg.senderName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = FixoSlate500,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isMe) 14.dp else 2.dp,
                                    bottomEnd = if (isMe) 2.dp else 14.dp
                                )
                            )
                            .background(if (isMe) FixoBlue600 else MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = msg.message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // 9. CHAT INPUT BAR WITH QUICK CHIPS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Quick reply chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("I'm at the location", "Water main shut off", "Please call when near gate").forEach { quick ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onSendMessage(quick) }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = quick,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input"),
                        placeholder = { Text("Message artisan or customer...") },
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FixoBlue600,
                            unfocusedBorderColor = FixoSlate200
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                onSendMessage(messageInput)
                                messageInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(FixoBlue600)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Cancellation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Appointment?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to cancel this booking? Since the artisan has not yet started transit, 100% of your escrow funds (${formatFixoCurrency(booking.priceAmount)}) will be immediately refunded back to your FIXO wallet.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancelBooking()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoRed500)
                ) {
                    Text("Confirm Cancellation & Refund", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Appointment")
                }
            }
        )
    }
}
