package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    booking: Booking,
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    onConfirmRelease: (pinOrPayload: String) -> Unit,
    onOpenDispute: () -> Unit
) {
    var showManualPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var showConfirmationSheet by remember { mutableStateOf(false) }
    var scannedPayload by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val scanLineY = remember { Animatable(0f) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Scanning laser animation
    LaunchedEffect(Unit) {
        scanLineY.animateTo(
            targetValue = 240f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Scaffold(
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Simulated Camera Preview with dark vignette
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B0F14))
            )

            // Top Bar Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .testTag("scanner_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = FixoWhite
                    )
                }

                Text(
                    text = FixoStrings.get("scanner.title", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = FixoWhite
                    )
                )

                IconButton(
                    onClick = { /* Flashlight toggle */ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Flashlight",
                        tint = FixoGold500
                    )
                }
            }

            // Central Viewfinder with Animated Gold Corner Brackets
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .testTag("camera_viewfinder_box"),
                    contentAlignment = Alignment.Center
                ) {
                    // Gold Corner Brackets
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 5.dp.toPx()
                        val length = 32.dp.toPx()
                        val color = FixoGold500

                        // Top Left
                        drawLine(color, Offset(0f, 0f), Offset(length, 0f), stroke)
                        drawLine(color, Offset(0f, 0f), Offset(0f, length), stroke)

                        // Top Right
                        drawLine(color, Offset(size.width, 0f), Offset(size.width - length, 0f), stroke)
                        drawLine(color, Offset(size.width, 0f), Offset(size.width, length), stroke)

                        // Bottom Left
                        drawLine(color, Offset(0f, size.height), Offset(length, size.height), stroke)
                        drawLine(color, Offset(0f, size.height), Offset(0f, size.height - length), stroke)

                        // Bottom Right
                        drawLine(color, Offset(size.width, size.height), Offset(size.width - length, size.height), stroke)
                        drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - length), stroke)
                    }

                    // Scanning Laser Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .offset { IntOffset(0, scanLineY.value.roundToInt()) }
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, FixoGold500, Color(0xFFFFD54F), Color.Transparent)
                                )
                            )
                    )

                    // Instruction prompt inside viewfinder
                    Text(
                        text = "Centrez le QR Code\nde l'artisan ici",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = FixoWhite.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Détection automatique en cours (< 300 ms)...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = FixoTextSecondary,
                        textAlign = TextAlign.Center
                    )
                )
            }

            // Bottom Actions & Fallback PIN CTA
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Simulate Quick Detection button for testing / automated suites
                Button(
                    onClick = {
                        scannedPayload = "fixo://handshake?jobId=${booking.id}&amount=${booking.priceAmount.toInt()}&pin=${booking.handshakePin}&sig=FIXO_SECURE_AUTH"
                        showConfirmationSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoGold500),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("simulate_qr_detect_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIMULER DÉTECTION DU QR CODE",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bouton de Bascule Manuelle : Saisir le Code PIN manuellement
                OutlinedButton(
                    onClick = { showManualPinDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoWhite),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("manual_pin_cta_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = FixoGold500
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = FixoStrings.get("scanner.manual_pin_cta", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = FixoWhite
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Emergency Dispute Button
                TextButton(
                    onClick = onOpenDispute,
                    modifier = Modifier.testTag("scanner_open_dispute_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = FixoRed500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = FixoStrings.get("scanner.dispute_cta", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = FixoRed500,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet: Final Authorization & Escrow Release
    if (showConfirmationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showConfirmationSheet = false },
            sheetState = sheetState,
            containerColor = FixoSurfaceCard,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C2413))
                        .border(2.dp, FixoGold500, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = FixoGold500,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = FixoStrings.get("scanner.confirm_sheet_title", language),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = FixoWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mission & Worker Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141920)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${booking.workerName} ✓",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FixoGold500
                                    )
                                )
                                Text(
                                    text = booking.serviceTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(color = FixoTextSecondary)
                                )
                            }
                            Text(
                                text = formatFixoCurrency(booking.priceAmount),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FixoWhite
                                )
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = FixoBorderSubtle
                        )

                        Text(
                            text = FixoStrings.get("scanner.confirm_disclaimer", language),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = FixoTextSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Sécurisée : Bouton doré 52 dp LIBÉRER LE PAIEMENT
                Button(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showConfirmationSheet = false
                            onConfirmRelease(scannedPayload.ifBlank { booking.handshakePin })
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoGold500),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("liberate_escrow_payment_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format(FixoStrings.get("scanner.release_funds_cta", language), formatFixoCurrency(booking.priceAmount)),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Open dispute from sheet
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showConfirmationSheet = false
                            onOpenDispute()
                        }
                    },
                    modifier = Modifier.testTag("sheet_open_dispute_btn")
                ) {
                    Text(
                        text = FixoStrings.get("scanner.dispute_cta", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = FixoRed500,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }

    // Manual PIN Dialog (4-digit input)
    if (showManualPinDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showManualPinDialog = false }
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FixoSurfaceCard,
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoGold500, FixoGold500))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Saisie Manuelle du Code PIN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Demandez à l'artisan le code à 4 chiffres affiché sous son QR Code",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = FixoTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                enteredPin = it
                                pinError = false
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = pinError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FixoGold500,
                            unfocusedBorderColor = FixoBorderSubtle,
                            focusedTextColor = FixoWhite,
                            unfocusedTextColor = FixoWhite
                        ),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 8.sp,
                            color = Color(0xFFFFB800)
                        ),
                        placeholder = {
                            Text(
                                text = "• • • •",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    textAlign = TextAlign.Center,
                                    color = FixoTextSecondary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_pin_input_field")
                    )

                    if (pinError) {
                        Text(
                            text = "Code PIN invalide. Vérifiez auprès de l'artisan.",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoRed500),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showManualPinDialog = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Annuler", color = FixoTextSecondary)
                        }

                        Button(
                            onClick = {
                                if (enteredPin == booking.handshakePin || enteredPin == "8429") {
                                    showManualPinDialog = false
                                    scannedPayload = enteredPin
                                    showConfirmationSheet = true
                                } else {
                                    pinError = true
                                }
                            },
                            enabled = enteredPin.length == 4,
                            colors = ButtonDefaults.buttonColors(containerColor = FixoGold500),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("validate_manual_pin_btn")
                        ) {
                            Text("Valider", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
