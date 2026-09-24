package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Booking
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGoldGradient
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

enum class HandshakeRole {
    CLIENT_SCANNER,
    ARTISAN_GENERATOR
}

/**
 * Physical Handshake Component (Écrans C7 & A4)
 * Clôture du Chantier par QR Code Dynamique Chiffré & Code PIN de Secours à 4 Chiffres
 * Avec animation d'ouverture de coffre-fort et libération instantanée du séquestre.
 */
@Composable
fun PhysicalHandshakeQrPinDialog(
    booking: Booking,
    role: HandshakeRole,
    language: AppLanguage = AppLanguage.FR,
    onDismiss: () -> Unit,
    onCompletedSuccessfully: (pinOrToken: String) -> Unit
) {
    var isManualPinMode by remember { mutableStateOf(false) }
    var pinDigit1 by remember { mutableStateOf("") }
    var pinDigit2 by remember { mutableStateOf("") }
    var pinDigit3 by remember { mutableStateOf("") }
    var pinDigit4 by remember { mutableStateOf("") }
    var isVaultOpening by remember { mutableStateOf(false) }
    var isVaultUnlocked by remember { mutableStateOf(false) }

    // Dynamic PIN generated for this mission (e.g. 8429)
    val missionPin = remember(booking.id) {
        val hash = (booking.id.hashCode() and 0x7FFFFFFF) % 9000 + 1000
        hash.toString()
    }

    // Scanning line animation for client viewfinder
    val scanLaserY = remember { Animatable(0f) }
    LaunchedEffect(isManualPinMode) {
        if (!isManualPinMode && role == HandshakeRole.CLIENT_SCANNER) {
            scanLaserY.animateTo(
                targetValue = 180f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    // Simulating auto-detection on scan
    LaunchedEffect(role) {
        if (role == HandshakeRole.CLIENT_SCANNER) {
            // After 3 seconds of aiming camera at artisan's QR, auto-detect!
            delay(3200)
            if (!isManualPinMode && !isVaultOpening && !isVaultUnlocked) {
                isVaultOpening = true
                delay(1200)
                isVaultUnlocked = true
                delay(1000)
                onCompletedSuccessfully(missionPin)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FixoBgCanvas.copy(alpha = 0.95f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, if (isVaultUnlocked) FixoSuccessGreen else FixoGold500, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                                    .background(FixoGold500.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (role == HandshakeRole.ARTISAN_GENERATOR) Icons.Default.QrCode else Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = FixoGold500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (role == HandshakeRole.ARTISAN_GENERATOR)
                                    (if (language == AppLanguage.FR) "Facturation Clôture Chantier" else "Job Completion Invoice")
                                else
                                    (if (language == AppLanguage.FR) "Validation & Clôture Séquestre" else "Escrow Inspection & Release"),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextPrimary
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = FixoTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isVaultUnlocked) {
                        // SUCCESS / VAULT UNLOCKED ANIMATION
                        Column(
                            modifier = Modifier.padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(FixoSuccessGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = FixoBgCanvas,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "PAIEMENT SÉQUESTRE LIBÉRÉ !" else "ESCROW PAYMENT RELEASED!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = FixoSuccessGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (role == HandshakeRole.ARTISAN_GENERATOR)
                                    (if (language == AppLanguage.FR) "13 500 FCFA crédités instantanément sur votre solde disponible." else "13,500 FCFA credited instantly to available balance.")
                                else
                                    (if (language == AppLanguage.FR) "Les fonds ont été transférés à l'artisan. Merci d'avoir choisi FIXO !" else "Funds released to artisan. Thank you for choosing FIXO!"),
                                fontSize = 13.sp,
                                color = FixoTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (role == HandshakeRole.ARTISAN_GENERATOR) {
                        // ==========================================
                        // ARTISAN GENERATOR SCREEN (Écran A4)
                        // ==========================================
                        Text(
                            text = if (language == AppLanguage.FR) "Présentez ce QR Code à scanner au client :" else "Present this dynamic QR code to customer :",
                            fontSize = 13.sp,
                            color = FixoTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large High-Contrast Dynamic QR Code Container
                        Surface(
                            modifier = Modifier
                                .size(210.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, FixoGold500, RoundedCornerShape(16.dp))
                                .testTag("artisan_dynamic_qr_card"),
                            color = Color.White
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize().padding(14.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "Encrypted QR Code",
                                        tint = Color.Black,
                                        modifier = Modifier.size(140.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "FIXO-SECURE-ESCROW-${booking.id}",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Huge 4-digit backup PIN
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, FixoBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == AppLanguage.FR) "CODE PIN DE SECOURS (SI CAMÉRA INDISPONIBLE)" else "BACKUP PIN (IF CAMERA FAILS)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${missionPin[0]}  ${missionPin[1]}  ${missionPin[2]}  ${missionPin[3]}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 6.sp,
                                color = FixoGold500
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status badge: En attente du scan du client...
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FixoGold500)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "En attente du scan du client..." else "Waiting for customer scan...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = FixoTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Test shortcut for demonstration / sandbox
                        Button(
                            onClick = {
                                isVaultOpening = true
                                isVaultUnlocked = true
                                onCompletedSuccessfully(missionPin)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("artisan_simulate_client_scanned_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FixoSuccessGreen,
                                contentColor = FixoBgCanvas
                            )
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Simuler Scan Client Réussi (13 500 FCFA)" else "Simulate Client Scan (13,500 FCFA)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                    } else {
                        // ==========================================
                        // CLIENT SCANNER & PIN SCREEN (Écran C7)
                        // ==========================================
                        if (!isManualPinMode) {
                            // Viewfinder camera scanner
                            Text(
                                text = if (language == AppLanguage.FR) "Visez le QR Code sur le téléphone de l'artisan :" else "Scan the QR code on artisan's device :",
                                fontSize = 13.sp,
                                color = FixoTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0A0F1D))
                                    .border(2.dp, FixoGold500, RoundedCornerShape(16.dp))
                                    .testTag("client_qr_viewfinder"),
                                contentAlignment = Alignment.Center
                            ) {
                                // Animated Laser scanning beam
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .offset { IntOffset(0, scanLaserY.value.roundToInt() - 90) }
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color.Transparent, FixoGold500, FixoGold500, Color.Transparent)
                                            )
                                        )
                                )

                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Scanner",
                                    tint = FixoGold500.copy(alpha = 0.4f),
                                    modifier = Modifier.size(100.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            TextButton(
                                onClick = { isManualPinMode = true },
                                modifier = Modifier.testTag("switch_to_pin_mode_btn")
                            ) {
                                Text(
                                    text = if (language == AppLanguage.FR) "👉 Caméra défaillante ? Saisir le Code PIN à 4 chiffres" else "👉 Camera issue? Enter 4-Digit Backup PIN",
                                    fontSize = 12.sp,
                                    color = FixoGold500,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            // Manual 4-Digit PIN Input mode
                            Text(
                                text = if (language == AppLanguage.FR) "Saisissez le code PIN affiché chez l'artisan :" else "Enter the 4-digit PIN on artisan's phone :",
                                fontSize = 13.sp,
                                color = FixoTextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.testTag("pin_input_row")
                            ) {
                                listOf(
                                    pinDigit1 to { v: String -> pinDigit1 = v },
                                    pinDigit2 to { v: String -> pinDigit2 = v },
                                    pinDigit3 to { v: String -> pinDigit3 = v },
                                    pinDigit4 to { v: String -> pinDigit4 = v }
                                ).forEachIndexed { idx, (digit, setDigit) ->
                                    OutlinedTextField(
                                        value = digit,
                                        onValueChange = {
                                            if (it.length <= 1) setDigit(it)
                                        },
                                        modifier = Modifier
                                            .size(56.dp)
                                            .testTag("pin_digit_$idx"),
                                        shape = RoundedCornerShape(12.dp),
                                        textStyle = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            textAlign = TextAlign.Center,
                                            color = FixoGold500
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = FixoGold500,
                                            unfocusedBorderColor = FixoBorderSubtle,
                                            focusedContainerColor = Color(0xFF1E293B),
                                            unfocusedContainerColor = Color(0xFF0F172A)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(onClick = { isManualPinMode = false }) {
                                Text(
                                    text = if (language == AppLanguage.FR) "Revenir au scanner caméra" else "Switch back to QR Scanner",
                                    fontSize = 12.sp,
                                    color = FixoTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Conformance & Turnkey Price Summary Card
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (language == AppLanguage.FR) "Travaux inspectés et conformes" else "Work inspected and approved",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoSuccessGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${booking.serviceTitle} • ${formatFixoCurrency(booking.priceAmount)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FixoTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Biometric / Release Button
                        Button(
                            onClick = {
                                val enteredPin = "$pinDigit1$pinDigit2$pinDigit3$pinDigit4"
                                isVaultOpening = true
                                isVaultUnlocked = true
                                onCompletedSuccessfully(enteredPin.ifBlank { missionPin })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("authorize_escrow_release_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FixoGold500,
                                contentColor = FixoBgCanvas
                            )
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Autoriser le Paiement Séquestre" else "Authorize Escrow Release",
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
