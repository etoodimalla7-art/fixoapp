package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * Retrait Instantané des Gains Artisan vers Mobile Money (Écran A5)
 * MTN MoMo / Orange Money, Frais 0 FCFA, Délai < 30s.
 */
@Composable
fun CashOutModal(
    availableBalance: Double = 48500.0,
    language: AppLanguage = AppLanguage.FR,
    onDismiss: () -> Unit,
    onConfirmPayout: (amount: Double, provider: String, phone: String) -> Unit
) {
    var amountInput by remember { mutableStateOf("48500") }
    var selectedProvider by remember { mutableStateOf("MTN MoMo") }
    var phoneInput by remember { mutableStateOf("+237 699 876 543") }
    var isSuccess by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FixoBgCanvas.copy(alpha = 0.95f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, if (isSuccess) FixoSuccessGreen else FixoSuccessGreen, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FixoSuccessGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = FixoSuccessGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Retrait Instantané MoMo" else "Instant Mobile Money Cash-Out",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextPrimary
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = FixoTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isSuccess) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(FixoSuccessGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoBgCanvas, modifier = Modifier.size(42.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "VIREMENT EFFECTUÉ !" else "PAYOUT COMPLETED!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = FixoSuccessGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "${formatFixoCurrency(amountInput.toDoubleOrNull() ?: availableBalance)} transférés avec succès vers votre compte $selectedProvider ($phoneInput)."
                                else
                                    "${formatFixoCurrency(amountInput.toDoubleOrNull() ?: availableBalance)} sent to your $selectedProvider account ($phoneInput).",
                                fontSize = 13.sp,
                                color = FixoTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FixoSuccessGreen, contentColor = FixoBgCanvas)
                            ) {
                                Text(if (language == AppLanguage.FR) "Terminer" else "Done", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Available Balance Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (language == AppLanguage.FR) "Solde Disponible à Retirer" else "Available Payout Balance",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatFixoCurrency(availableBalance),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = FixoSuccessGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Operator Selector
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("MTN MoMo", "Orange Money").forEach { provider ->
                                val isSel = selectedProvider == provider
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(if (isSel) Color(0xFF1E293B) else Color(0xFF0F172A))
                                        .border(1.5.dp, if (isSel) FixoSuccessGreen else FixoBorderSubtle, RoundedCornerShape(24.dp))
                                        .clickable { selectedProvider = provider }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = provider,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) FixoSuccessGreen else FixoTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { amountInput = it },
                            label = { Text(if (language == AppLanguage.FR) "Montant à transférer (FCFA)" else "Amount to transfer (FCFA)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoSuccessGreen,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Numéro Mobile Money (+237)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoSuccessGreen,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Fee & Speed Mention
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(FixoSuccessGreen.copy(alpha = 0.12f))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FixoSuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "Frais de retrait plateforme : 0 FCFA • Délai : Instantané (< 30 secondes)"
                                else
                                    "Platform fee: 0 FCFA • Processing: Instant (< 30 seconds)",
                                fontSize = 11.sp,
                                color = FixoSuccessGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val amount = amountInput.toDoubleOrNull() ?: availableBalance
                                onConfirmPayout(amount, selectedProvider, phoneInput)
                                isSuccess = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("confirm_instant_cashout_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FixoSuccessGreen,
                                contentColor = FixoBgCanvas
                            )
                        ) {
                            Text(
                                text = if (language == AppLanguage.FR) "⚡ Confirmer le Retrait Instantané" else "⚡ Confirm Instant Cash-Out",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
