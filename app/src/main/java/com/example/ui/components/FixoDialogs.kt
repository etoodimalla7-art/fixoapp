package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Booking
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.SubscriptionTier
import com.example.data.model.WorkerProfile
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoBlue700
import com.example.ui.theme.FixoEmerald100
import com.example.ui.theme.FixoEmerald50
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold100
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700

@Composable
fun BookingDialog(
    worker: WorkerProfile,
    service: ServiceItem,
    onDismiss: () -> Unit,
    onConfirm: (date: String, slot: String, address: String, notes: String, payment: PaymentMethod) -> Unit
) {
    var selectedDate by remember { mutableStateOf("Today, Oct 14") }
    val availableSlots = worker.slotIntervals.split(",")
    var selectedSlot by remember { mutableStateOf(availableSlots.firstOrNull() ?: "10:30 - 12:30") }
    var address by remember { mutableStateOf("Bonapriso, Rue des Palmiers, Douala") }
    var notes by remember { mutableStateOf("Main shutoff pipe leaking underneath kitchen counter.") }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.FIXO_WALLET) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reserve Service",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "with ${worker.name}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = FixoSlate500)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Service Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FixoBlue50)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = service.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = FixoNavy900)
                            )
                            Text(
                                text = "Est. duration: ${service.durationEstimateMinutes} mins",
                                style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                            )
                        }
                        Text(
                            text = com.example.data.model.formatFixoCurrency(service.price),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = FixoBlue700
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Selection Chips
                Text("Select Service Date", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Today, Oct 14", "Tomorrow, Oct 15", "Wed, Oct 16").forEach { date ->
                        val isSel = selectedDate == date
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) FixoBlue600 else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedDate = date }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.split(",").first(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Authoritative Available Slots
                Text("Artisan Available Slots (Real-Time)", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableSlots.forEach { slot ->
                        val isSel = selectedSlot == slot.trim()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSel) FixoBlue600 else FixoSlate200,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(if (isSel) FixoBlue50 else MaterialTheme.colorScheme.surface)
                                .clickable { selectedSlot = slot.trim() }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = slot.trim(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) FixoBlue700 else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = FixoBlue600, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Address Field
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Service Location / Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .clickable { address = "Avenue De Gaulle, Bonanjo, Douala" }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍 Use GPS: Avenue De Gaulle, Bonanjo, Douala",
                        fontSize = 11.sp,
                        color = FixoBlue600,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes & Photo Field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Problem Description & Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = FixoAmber500, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attach Site Photo (Optional)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 6. Itemized Pricing Breakdown
                val servicePrice = service.price
                val transportFee = 1500.0
                val platformFee = 500.0
                val totalEscrow = servicePrice + transportFee + platformFee

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Itemized Pricing Breakdown", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Service Rate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(com.example.data.model.formatFixoCurrency(servicePrice), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Artisan Transport & Dispatch", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(com.example.data.model.formatFixoCurrency(transportFee), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Platform & Escrow Insurance", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(com.example.data.model.formatFixoCurrency(platformFee), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Escrow Deposit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(com.example.data.model.formatFixoCurrency(totalEscrow), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FixoNavy900)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Gateway Selector
                Text("Escrow Payment Gateway", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
                PaymentMethod.values().forEach { method ->
                    val isSel = selectedPayment == method
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, if (isSel) FixoBlue600 else FixoSlate200, RoundedCornerShape(8.dp))
                            .background(if (isSel) FixoBlue50 else MaterialTheme.colorScheme.surface)
                            .clickable { selectedPayment = method }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (method) {
                                    PaymentMethod.FIXO_WALLET -> Icons.Outlined.AccountBalanceWallet
                                    PaymentMethod.MTN_MOMO, PaymentMethod.ORANGE_MONEY -> Icons.Default.PhoneAndroid
                                    PaymentMethod.CREDIT_CARD -> Icons.Outlined.CreditCard
                                },
                                contentDescription = method.label,
                                tint = if (isSel) FixoBlue600 else FixoSlate500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = method.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                Text(
                                    text = when (method) {
                                        PaymentMethod.FIXO_WALLET -> "Funds deducted and held in secure escrow"
                                        PaymentMethod.MTN_MOMO -> "Instant USSD authorization via MTN Mobile Money"
                                        PaymentMethod.ORANGE_MONEY -> "Instant checkout via Orange Money wallet"
                                        PaymentMethod.CREDIT_CARD -> "Secured with 256-bit encryption"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = FixoSlate500)
                                )
                            }
                            if (isSel) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = FixoBlue600, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Escrow Guarantee Notice
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FixoEmerald50)
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = "Guarantee", tint = FixoEmerald600, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Escrow Protection: ${com.example.data.model.formatFixoCurrency(totalEscrow)} is locked securely until you inspect and approve the completed repair.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = FixoEmerald600, fontWeight = FontWeight.Medium)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action CTA
                Button(
                    onClick = {
                        onConfirm(selectedDate, selectedSlot, address, notes, selectedPayment)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("confirm_booking_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FixoGold500,
                        contentColor = FixoNavy950
                    )
                ) {
                    Text(
                        text = "Confirm & Hold in Escrow (${com.example.data.model.formatFixoCurrency(totalEscrow)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = FixoNavy950
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewAndReleaseDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onSubmit: (rating: Float, review: String) -> Unit
) {
    var rating by remember { mutableStateOf(5f) }
    var reviewText by remember { mutableStateOf("Outstanding craftsmanship! Marc diagnosed the pipe fracture immediately, replaced it with brass fittings, and left the work area completely dry and spotless. Highly recommended.") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "Release Escrow & Review",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Job: ${booking.serviceTitle} with ${booking.workerName}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = FixoSlate500)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Star Picker
                Text("Artisan Craftsmanship Rating", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { rating = star.toFloat() },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star stars",
                                tint = if (star <= rating) FixoAmber500 else FixoSlate200,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Your Review & Feedback") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Points Notification
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FixoEmerald50)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = "Points", tint = FixoEmerald600, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Releasing $${booking.priceAmount} will credit +${(booking.priceAmount * 0.35).toInt()} FIXO Points to your account!",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoEmerald600, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onSubmit(rating, reviewText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_review_escrow_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoEmerald600)
                ) {
                    Text(
                        text = "Release Escrow & Submit Review",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DepositDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, method: String, phone: String) -> Unit
) {
    var amount by remember { mutableStateOf("100") }
    var selectedMethod by remember { mutableStateOf("MTN Mobile Money") }
    var phone by remember { mutableStateOf("+1 (555) 389-1029") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "Deposit to FIXO Wallet",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Top-up balance for instant escrow booking",
                    style = MaterialTheme.typography.bodyMedium.copy(color = FixoSlate500)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Amount Chips
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("5000", "10000", "25000", "50000").forEach { preset ->
                        val isSel = amount == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) FixoBlue600 else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { amount = preset }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${preset} F",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Deposit Amount (FCFA)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Payment Provider", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
                listOf("MTN Mobile Money", "Orange Money", "Debit / Credit Card").forEach { method ->
                    val isSel = selectedMethod == method
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, if (isSel) FixoBlue600 else FixoSlate200, RoundedCornerShape(8.dp))
                            .background(if (isSel) FixoBlue50 else MaterialTheme.colorScheme.surface)
                            .clickable { selectedMethod = method }
                            .padding(10.dp)
                    ) {
                        Text(
                            text = method,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) FixoBlue700 else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / Account (+237...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val parsed = amount.toDoubleOrNull() ?: 5000.0
                        onConfirm(parsed, selectedMethod, phone)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_deposit_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FixoGold500,
                        contentColor = FixoNavy950
                    )
                ) {
                    Text(
                        text = "Authorize Top-Up ($amount FCFA)",
                        fontWeight = FontWeight.Bold,
                        color = FixoNavy950
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawDialog(
    availableBalance: Double,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, method: String, account: String) -> Unit
) {
    var amount by remember { mutableStateOf("10000") }
    var selectedMethod by remember { mutableStateOf("MTN Mobile Money") }
    var account by remember { mutableStateOf("+237 699 876 543") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "Withdraw Artisan Earnings",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Available Balance: ${String.format(java.util.Locale.FRANCE, "%,.0f FCFA", availableBalance)}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = FixoEmerald600, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Payout Amount (FCFA)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Payout Destination", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
                listOf("MTN Mobile Money", "Orange Money", "Bank Wire Transfer").forEach { method ->
                    val isSel = selectedMethod == method
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, if (isSel) FixoBlue600 else FixoSlate200, RoundedCornerShape(8.dp))
                            .background(if (isSel) FixoBlue50 else MaterialTheme.colorScheme.surface)
                            .clickable { selectedMethod = method }
                            .padding(10.dp)
                    ) {
                        Text(
                            text = method,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) FixoBlue700 else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = account,
                    onValueChange = { account = it },
                    label = { Text("Phone / Account (+237...)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val parsed = amount.toDoubleOrNull() ?: 5000.0
                        onConfirm(parsed, selectedMethod, account)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_withdraw_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoEmerald600)
                ) {
                    Text("Transfer Payout ($amount FCFA)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UploadReelDialog(
    onDismiss: () -> Unit,
    onPublish: (title: String, desc: String, category: ServiceCategory, tags: String, videoUrl: String, thumbUrl: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ServiceCategory.HVAC) }
    var tags by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp)
            ) {
                Text(
                    text = "Publish Craftsmanship Reel",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Showcase your real craftsmanship technique to gain direct customer bookings",
                    style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Trade Category", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ServiceCategory.values().forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, if (isSelected) FixoBlue600 else FixoSlate200, RoundedCornerShape(8.dp))
                                .background(if (isSelected) FixoBlue50 else MaterialTheme.colorScheme.surface)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.displayName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) FixoBlue700 else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reel Title") },
                    placeholder = { Text("e.g. Copper Pipe Brazing with Nitrogen Purge") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Technique Description & Equipment") },
                    placeholder = { Text("Explain your diagnostic procedure, safety protocol, and craftsmanship standard.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags / Skills (comma separated)") },
                    placeholder = { Text("e.g. HVAC, Copper, Brazing, Safety") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FixoBlue50)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Object Storage Ready: Videos are served via high-speed global CDN with native app deep links (https://fixo.app/reels/...).",
                        style = MaterialTheme.typography.bodySmall.copy(color = FixoBlue700, fontSize = 11.sp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val finalTitle = title.ifBlank { "Professional ${selectedCategory.displayName} Technique" }
                        val finalDesc = desc.ifBlank { "Real on-site craftsmanship demonstration by verified Cameroon artisan." }
                        val finalTags = tags.ifBlank { selectedCategory.displayName }
                        onPublish(
                            finalTitle,
                            finalDesc,
                            selectedCategory,
                            finalTags,
                            "https://assets.mixkit.co/videos/preview/mixkit-hands-of-an-electrician-fixing-wires-41306-large.mp4",
                            "https://images.unsplash.com/photo-1581244277943-fe4a9c777189?w=600"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("publish_reel_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FixoGold500,
                        contentColor = FixoNavy950
                    )
                ) {
                    Text(
                        text = "Publish to Public Discovery Feed",
                        fontWeight = FontWeight.Bold,
                        color = FixoNavy950
                    )
                }
            }
        }
    }
}

@Composable
fun DisputeDialog(
    onDismiss: () -> Unit,
    onSubmit: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("Artisan failed to arrive within the scheduled 2-hour window and cannot be reached.") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Text(
                    text = "Report Issue / Dispute",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = FixoRed500)
                )
                Text(
                    text = "All escrow payments remain locked until dispute resolution.",
                    style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Details of the dispute") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = { onSubmit(reason) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_dispute_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoRed500)
                ) {
                    Text("Submit to Compliance Team", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
