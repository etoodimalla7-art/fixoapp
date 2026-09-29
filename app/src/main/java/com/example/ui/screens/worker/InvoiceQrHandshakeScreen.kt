package com.example.ui.screens.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.JobStatus
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceQrHandshakeScreen(
    booking: Booking,
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    onSimulateCustomerScan: () -> Unit = {}
) {
    val totalAmount = booking.priceAmount
    val netEarnings = totalAmount * 0.90
    val platformCommission = totalAmount * 0.10
    val pin = booking.handshakePin.ifBlank { "8429" }
    val isAlreadyClosed = booking.status == JobStatus.CLOSED_CONFIRMED || booking.status == JobStatus.COMPLETED

    var isSuccess by remember { mutableStateOf(isAlreadyClosed) }
    val vaultScale = remember { Animatable(0.8f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(booking.status) {
        if (booking.status == JobStatus.CLOSED_CONFIRMED || booking.status == JobStatus.COMPLETED) {
            isSuccess = true
            vaultScale.animateTo(1.1f, tween(300, easing = FastOutSlowInEasing))
            vaultScale.animateTo(1.0f, tween(150))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CLÔTURE DU CHANTIER #${booking.id.takeLast(8).uppercase()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("invoice_qr_back")) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSuccess) {
                // Header Subtitle
                Text(
                    text = FixoStrings.get("handshake.artisan_instruction", language),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = FixoTextSecondary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val qrPayload = "fixo://handshake?jobId=${booking.id}&pin=$pin&amount=${totalAmount.toInt()}&sig=FIXO_AUTH"
                val qrBitmap = remember(qrPayload) {
                    com.example.ui.util.QrCodeGenerator.generateQrBitmap(qrPayload, 512)
                }

                // Matrice QR Code Dynamique ZXing Vectorielle (220 x 220 dp)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(220.dp)
                        .testTag("dynamic_qr_matrix")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp), // Zone de sécurité (Quiet zone)
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = qrBitmap,
                                contentDescription = "Dynamic Handshake QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Dynamic Handshake QR Code",
                                tint = Color.Black,
                                modifier = Modifier.size(175.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Emergency Fallback PIN Code Box (4 digits in Gold #FFB800)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A11)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoGold500, Color(0xFFFFD54F)))),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fallback_pin_box")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CODE PIN DE SECOURS (FALLBACK)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FixoTextSecondary,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            pin.take(4).forEach { digit ->
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF2C2413))
                                        .border(1.5.dp, FixoGold500, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit.toString(),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFFB800),
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Net Financial Breakdown
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Montant total de l'intervention",
                                style = MaterialTheme.typography.bodyMedium.copy(color = FixoTextSecondary)
                            )
                            Text(
                                text = formatFixoCurrency(totalAmount),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Text(
                            text = "🔒 Forfait ferme garanti sans espèces",
                            style = MaterialTheme.typography.labelSmall.copy(color = FixoGold500),
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = FixoBorderSubtle
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Votre gain net à percevoir",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FixoSuccessGreen
                                    )
                                )
                                Text(
                                    text = "Commission Fixo déduite (${formatFixoCurrency(platformCommission)})",
                                    style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                                )
                            }
                            Text(
                                text = formatFixoCurrency(netEarnings),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FixoSuccessGreen
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Real-time Pulsing Waiting Status Indicator
                Row(
                    modifier = Modifier
                        .scale(pulseScale)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF2C2515))
                        .border(1.dp, FixoGold500, RoundedCornerShape(24.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = FixoGold500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = FixoStrings.get("handshake.waiting_scan", language),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = FixoGold500
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fast Sandbox Test Trigger Button
                Button(
                    onClick = {
                        onSimulateCustomerScan()
                        isSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242C35)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulate_customer_scan_btn")
                ) {
                    Text(
                        text = "⚡ Simuler Scan Client Immédiat (Mode Test)",
                        style = MaterialTheme.typography.labelMedium.copy(color = FixoTextSecondary)
                    )
                }
            } else {
                // Success / Vault Unlocked View
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(vaultScale.value)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(FixoSuccessGreen, Color(0xFF0F3E2E))))
                            .border(3.dp, FixoGold500, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = FixoWhite,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "PAIEMENT DÉBLOQUÉ ! 🎉",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = FixoGold500,
                            letterSpacing = 1.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Les fonds ont été virés instantanément sur votre solde disponible.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = FixoTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "CRÉDITÉ SUR VOTRE COMPTE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = FixoTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+${formatFixoCurrency(netEarnings)}",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FixoSuccessGreen
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = FixoSuccessGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Garantie 14 jours Fixo Shield activée",
                                    style = MaterialTheme.typography.bodySmall.copy(color = FixoTextSecondary)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = FixoGold500),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("invoice_qr_done_btn")
                    ) {
                        Text(
                            text = "RETOURNER AU TABLEAU DE BORD",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
