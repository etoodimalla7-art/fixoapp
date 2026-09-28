package com.example.ui.screens.worker

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameroonMobileOperator
import com.example.data.model.User
import com.example.data.model.WalletTransaction
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerWalletScreen(
    user: User?,
    transactions: List<WalletTransaction>,
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    onCashOut: (amount: Double, operator: CameroonMobileOperator, phone: String) -> Unit
) {
    val availableBalance = user?.balance ?: 48500.0
    val escrowLocked = user?.escrowLocked ?: 0.0

    var withdrawAmountText by remember { mutableStateOf("40000") }
    var selectedOperator by remember { mutableStateOf(CameroonMobileOperator.MTN_MOMO) }
    var phoneInput by remember { mutableStateOf(user?.phone?.ifBlank { "+237 671 234 567" } ?: "+237 671 234 567") }
    var showSuccessBanner by remember { mutableStateOf(false) }
    var lastWithdrawnAmount by remember { mutableStateOf(0.0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = FixoStrings.get("wallet.worker_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("worker_wallet_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FixoWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FixoBgCanvas)
            )
        },
        containerColor = FixoBgCanvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Cadran Financier Transparent
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("worker_financial_dial")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = FixoStrings.get("wallet.available_balance", language).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FixoTextSecondary,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = formatFixoCurrency(availableBalance),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = FixoWhite
                            ),
                            modifier = Modifier.testTag("worker_available_balance_text")
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = FixoBorderSubtle)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = FixoGold500,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = FixoStrings.get("wallet.escrow_balance", language),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = FixoTextSecondary)
                                )
                            }
                            Text(
                                text = formatFixoCurrency(escrowLocked),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoGold500
                                )
                            )
                        }
                    }
                }
            }

            // Success SMS Banner
            if (showSuccessBanner) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3E2E)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoSuccessGreen, FixoSuccessGreen))),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FixoSuccessGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Virement Opérateur Confirmé",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FixoWhite
                                    )
                                )
                                Text(
                                    text = "SMS MTN/Orange : Transfert de ${formatFixoCurrency(lastWithdrawnAmount)} envoyé sur votre compte mobile avec succès.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = FixoTextSecondary)
                                )
                            }
                        }
                    }
                }
            }

            // Formulaire de Décaissement Instantané (Cash-Out)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Décaissement Instantané (Cash-Out)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = FixoWhite
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Amount Input
                        Text(
                            text = "Montant à retirer (FCFA)",
                            style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = withdrawAmountText,
                            onValueChange = { withdrawAmountText = it.filter { c -> c.isDigit() } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoSuccessGreen,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedTextColor = FixoWhite,
                                unfocusedTextColor = FixoWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cashout_amount_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Destination selection registered in KYC
                        Text(
                            text = "Destination enregistrée (KYC Certifié)",
                            style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 1: MTN MoMo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedOperator == CameroonMobileOperator.MTN_MOMO) Color(0xFF232B20) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (selectedOperator == CameroonMobileOperator.MTN_MOMO) FixoSuccessGreen else FixoBorderSubtle,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedOperator = CameroonMobileOperator.MTN_MOMO }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOperator == CameroonMobileOperator.MTN_MOMO,
                                onClick = { selectedOperator = CameroonMobileOperator.MTN_MOMO },
                                colors = RadioButtonDefaults.colors(selectedColor = FixoSuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MTN Mobile Money (+237 671 *** ***)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 2: Orange Money
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedOperator == CameroonMobileOperator.ORANGE_MONEY) Color(0xFF232B20) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (selectedOperator == CameroonMobileOperator.ORANGE_MONEY) FixoSuccessGreen else FixoBorderSubtle,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedOperator = CameroonMobileOperator.ORANGE_MONEY }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOperator == CameroonMobileOperator.ORANGE_MONEY,
                                onClick = { selectedOperator = CameroonMobileOperator.ORANGE_MONEY },
                                colors = RadioButtonDefaults.colors(selectedColor = FixoSuccessGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Orange Money Cameroon (+237 699 *** ***)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Mention de transparence
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = FixoGold500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = FixoStrings.get("wallet.cashout_transparency", language),
                                style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        val withdrawAmount = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        val canWithdraw = withdrawAmount > 0 && withdrawAmount <= availableBalance

                        // Bouton vert émeraude 52 dp : RETIRER MES FONDS VERS MOMO
                        Button(
                            onClick = {
                                if (canWithdraw) {
                                    lastWithdrawnAmount = withdrawAmount
                                    onCashOut(withdrawAmount, selectedOperator, phoneInput)
                                    showSuccessBanner = true
                                }
                            },
                            enabled = canWithdraw,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FixoEmerald600,
                                disabledContainerColor = FixoEmerald600.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("cashout_submit_btn")
                        ) {
                            Text(
                                text = FixoStrings.get("wallet.cashout_cta", language),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }
                    }
                }
            }

            // Transaction History Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historique des Transactions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                    Text(
                        text = "${transactions.size} opérations",
                        style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                    )
                }
            }

            // Paged / Listed Transactions
            if (transactions.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aucune transaction enregistrée.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = FixoTextSecondary)
                            )
                        }
                    }
                }
            } else {
                items(transactions) { tx ->
                    TransactionItemRow(tx = tx)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TransactionItemRow(tx: WalletTransaction) {
    val isCredit = tx.amount > 0 && tx.type != "CASH_OUT" && tx.type != "PAYOUT_WITHDRAWAL"
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.FRANCE) }

    Card(
        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isCredit) Color(0xFF0F3E2E) else Color(0xFF381E1E)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (isCredit) FixoSuccessGreen else Color(0xFFFF5252),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = tx.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = FixoWhite
                        )
                    )
                    Text(
                        text = "${dateFormatter.format(Date(tx.timestamp))} • ${tx.referenceCode}",
                        style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                    )
                }
            }

            Text(
                text = "${if (isCredit) "+" else "-"}${formatFixoCurrency(kotlin.math.abs(tx.amount))}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isCredit) FixoSuccessGreen else Color(0xFFFF5252)
                )
            )
        }
    }
}
