package com.example.ui.screens.worker

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
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
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.FixoAmber100
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoBlue700
import com.example.ui.theme.FixoEmerald100
import com.example.ui.theme.FixoEmerald50
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700
import com.example.ui.theme.FixoWhite

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
    modifier: Modifier = Modifier
) {
    var isOnline by remember { mutableStateOf(true) }
    val workerId = workerProfile?.id ?: user?.id ?: ""
    val myReels = if (workerId.isBlank()) reels else reels.filter { it.workerId == workerId }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Online / Ready for Emergency Dispatch Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) FixoEmerald600 else FixoSlate500)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isOnline) "Online • Receiving Jobs" else "Offline • Not Visible",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "Emergency 30-min callouts and scheduled slots enabled",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 11.sp)
                        )
                    }

                    Switch(
                        checked = isOnline,
                        onCheckedChange = { isOnline = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = FixoEmerald600
                        ),
                        modifier = Modifier.testTag("worker_online_toggle")
                    )
                }
            }
        }

        // Earnings & Payout Summary
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FixoNavy900)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Available Earnings",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.8f))
                            )
                            Text(
                                text = com.example.data.model.formatFixoCurrency(user?.balance ?: 0.0),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        Button(
                            onClick = onOpenWithdraw,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoEmerald600),
                            modifier = Modifier.testTag("artisan_withdraw_button")
                        ) {
                            Text("Withdraw Payout", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pending in Escrow: ${com.example.data.model.formatFixoCurrency(user?.escrowLocked ?: 0.0)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoAmber500, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Tier: ${workerProfile?.subscriptionTier?.title ?: "Pro"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                }
            }
        }

        // Quick Management Shortcuts: Availability & Subscription
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenAvailability() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = FixoBlue600)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Schedule & Slots", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        Text("Edit working days & hours", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = FixoSlate500))
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenSubscription() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = FixoAmber500)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Subscription Tier", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        Text("Pro & Master badges", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = FixoSlate500))
                    }
                }
            }
        }

        // Active Orders & Service Requests
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Current Job Queue (${bookings.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        if (bookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("worker_empty_bookings"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = FixoSlate500, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active job requests right now.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Keep your status Online to receive instant 30-min callouts and scheduled bookings.",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        )
                    }
                }
            }
        } else {
            items(bookings) { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onSelectBooking(booking) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = booking.serviceTitle,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            JobStatusBadge(status = booking.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Customer: ${booking.customerName} • ${booking.address}",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                        )
                        Text(
                            text = "Scheduled: ${booking.date} (${booking.timeSlot})",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Advance Status Quick Buttons for Worker
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${com.example.data.model.formatFixoCurrency(booking.priceAmount)} in Escrow",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = FixoEmerald600)
                            )

                            val nextStatus = when (booking.status) {
                                JobStatus.REQUESTED -> JobStatus.ACCEPTED
                                JobStatus.ACCEPTED, JobStatus.SCHEDULED -> JobStatus.ON_THE_WAY
                                JobStatus.ON_THE_WAY -> JobStatus.ARRIVED
                                JobStatus.ARRIVED -> JobStatus.IN_PROGRESS
                                JobStatus.IN_PROGRESS -> JobStatus.COMPLETION_REQUESTED
                                else -> null
                            }

                            if (nextStatus != null) {
                                Button(
                                    onClick = { onAdvanceJobStatus(booking.id, nextStatus) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (nextStatus == JobStatus.ON_THE_WAY) FixoGold500 else FixoBlue600,
                                        contentColor = if (nextStatus == JobStatus.ON_THE_WAY) FixoNavy900 else FixoWhite
                                    )
                                ) {
                                    Text(
                                        text = when (nextStatus) {
                                            JobStatus.ACCEPTED -> "Accept Job"
                                            JobStatus.ON_THE_WAY -> "Start Trip"
                                            JobStatus.ARRIVED -> "Arrived at Site"
                                            JobStatus.IN_PROGRESS -> "Start Work"
                                            JobStatus.COMPLETION_REQUESTED -> "Request Inspection"
                                            else -> "Update"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Craftsmanship Reels Studio
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Craftsmanship Reels Studio",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Show your technique to generate direct bookings",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                        )
                    }

                    Button(
                        onClick = onOpenUploadReel,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                        modifier = Modifier.testTag("worker_upload_reel_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Reel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (myReels.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No craftsmanship reels published yet. Tap '+ New Reel' to publish video demonstrations of your trade skills.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = FixoSlate500,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(myReels) { reel ->
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(190.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = reel.thumbnailUrl,
                                    contentDescription = reel.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                                )

                                // Delete icon at top-right
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .clickable { onDeleteReel(reel.id) }
                                        .testTag("delete_reel_${reel.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete Reel",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = reel.title,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                        maxLines = 2
                                    )
                                    Text(
                                        text = "❤️ ${reel.likesCount} • 💼 ${reel.bookingsCount} booked",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
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
