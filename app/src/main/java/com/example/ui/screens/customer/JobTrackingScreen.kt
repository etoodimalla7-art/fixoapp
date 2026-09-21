package com.example.ui.screens.customer

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.UserRole
import com.example.ui.components.EscrowBadge
import com.example.ui.components.JobStatusBadge
import com.example.ui.theme.FixoAmber100
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoBlue700
import com.example.ui.theme.FixoEmerald100
import com.example.ui.theme.FixoEmerald50
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700

@Composable
fun JobTrackingScreen(
    booking: Booking,
    chatMessages: List<ChatMessage>,
    onBack: () -> Unit,
    onAdvanceStatus: (JobStatus) -> Unit,
    onOpenReview: () -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenDispute: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messageInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // App Bar
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
                            Text(
                                text = "Job #${booking.id}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = booking.serviceTitle,
                                style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                            )
                        }
                    }

                    TextButton(onClick = onOpenDispute) {
                        Text("Dispute", color = FixoRed500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Job Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = booking.workerAvatar,
                            contentDescription = booking.workerName,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = booking.workerName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${booking.category.displayName} • ${booking.workerPhone}",
                                style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = FixoSlate100)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Scheduled Time", style = MaterialTheme.typography.labelSmall.copy(color = FixoSlate500))
                            Text(text = "${booking.date} (${booking.timeSlot})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Location", style = MaterialTheme.typography.labelSmall.copy(color = FixoSlate500))
                            Text(text = booking.address, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        // Interactive Lifecycle Stepper
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Real-Time Service Progress",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val steps = listOf(
                        JobStatus.REQUESTED to "Service Requested",
                        JobStatus.ACCEPTED to "Artisan Accepted",
                        JobStatus.EN_ROUTE to "En Route to Site",
                        JobStatus.IN_PROGRESS to "Work In Progress",
                        JobStatus.COMPLETION_REQUESTED to "Inspection Ready",
                        JobStatus.COMPLETED to "Escrow Released & Done"
                    )

                    val currentIdx = steps.indexOfFirst { it.first == booking.status }.coerceAtLeast(0)

                    steps.forEachIndexed { index, (status, label) ->
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

                    Spacer(modifier = Modifier.height(14.dp))

                    // Advance Lifecycle Action (for instant testing/demonstration)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (booking.status != JobStatus.COMPLETED) {
                            val nextStatus = when (booking.status) {
                                JobStatus.REQUESTED -> JobStatus.ACCEPTED
                                JobStatus.ACCEPTED -> JobStatus.EN_ROUTE
                                JobStatus.EN_ROUTE -> JobStatus.IN_PROGRESS
                                JobStatus.IN_PROGRESS -> JobStatus.COMPLETION_REQUESTED
                                JobStatus.COMPLETION_REQUESTED -> JobStatus.COMPLETED
                                else -> JobStatus.COMPLETED
                            }
                            Button(
                                onClick = { onAdvanceStatus(nextStatus) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Simulate Next Step",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Escrow Release Card (When finished or ready)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FixoEmerald50),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoEmerald600.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = "Escrow", tint = FixoEmerald600, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Escrow Protection: $${booking.priceAmount} Secured",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = FixoEmerald600)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (booking.escrowStatus == EscrowStatus.RELEASED)
                            "Payment has been released to ${booking.workerName}. Thank you for using FIXO Escrow Guarantee!"
                        else
                            "Once the artisan has completed the work to your satisfaction, release the funds and submit your verified review.",
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
                                text = "Release Escrow & Review (+${booking.pointsEarned.coerceAtLeast(25)} Points)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Live Chat with Artisan
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Direct Messages with ${booking.workerName}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Chat message bubbles
        items(chatMessages) { msg ->
            val isMe = msg.senderRole == UserRole.CUSTOMER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isMe) 12.dp else 2.dp,
                                bottomEnd = if (isMe) 2.dp else 12.dp
                            )
                        )
                        .background(if (isMe) FixoBlue600 else MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
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

        // Chat Input Row
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Quick chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("I'm at the location", "Water main shut off", "Please call my phone").forEach { quick ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onSendMessage(quick) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = quick, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp))
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
                        placeholder = { Text("Type message to artisan...") },
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
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
