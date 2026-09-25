package com.example.ui.screens.worker

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.components.SlideToAcceptButton
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoDangerRed
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import kotlinx.coroutines.delay

/**
 * CHANTIER 5 : L'ALERTE PLEIN ÉCRAN DE MISSION FLASH (< 30 MIN)
 *
 * Modale prioritaire bloquante (#080C15 opaque)
 * - Déclenchement son d'alerte & vibration cadencée (avec gestion mode silencieux)
 * - Compte à rebours visuel de 30 secondes (cercle animé qui s'épuise en temps réel)
 * - Badge clignotant rouge/or : 🚨 NOUVELLE MISSION FLASH EXPRESS
 * - Rémunération Nette Garantie : 13 500 FCFA NET
 * - Détails complets de l'intervention (Métier, Adresse, Client, Exigence < 30 min)
 * - Composant SlideToAcceptButton (seuil >= 85%)
 * - Bouton de refus sous le rail
 */
@Composable
fun IncomingJobAlertOverlay(
    mission: FlashMissionAlert = FlashMissionAlert(),
    language: AppLanguage = AppLanguage.FR,
    onAccept: (FlashMissionAlert) -> Unit,
    onDecline: () -> Unit,
    onTimeout: () -> Unit = onDecline,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var remainingSeconds by remember { mutableIntStateOf(mission.timeoutSeconds) }

    // Sound & Cadenced Vibration
    DisposableEffect(Unit) {
        var toneGenerator: ToneGenerator? = null
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
        } catch (_: Throwable) {}

        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                val pattern = longArrayOf(0, 300, 200, 300)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            }
        } catch (_: Throwable) {}

        onDispose {
            try {
                toneGenerator?.release()
            } catch (_: Throwable) {}
        }
    }

    // 30-Second Countdown timer
    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
        }
        onTimeout()
    }

    // Blinking effect for header badge
    val infiniteTransition = rememberInfiniteTransition(label = "badge_blink")
    val badgeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    Dialog(
        onDismissRequest = { /* Modal bloquante, action requise */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF080C15)) // #080C15 Opaque
                .padding(20.dp)
                .testTag("incoming_job_alert_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ==========================================
                // 1. EN-TÊTE : BADGE CLIGNOTANT 🚨 NOUVELLE MISSION FLASH EXPRESS
                // ==========================================
                val alertTitle = FixoStrings.getString("worker.alert.title", language)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    FixoDangerRed.copy(alpha = 0.25f),
                                    FixoElectricAmber.copy(alpha = 0.25f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            color = FixoElectricAmber.copy(alpha = badgeAlpha),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = alertTitle,
                        color = FixoElectricAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.alpha(badgeAlpha)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // 2. COMPTE À REBOURS VISUEL DE 30 SECONDES
                // ==========================================
                val progressFraction = (remainingSeconds.toFloat() / mission.timeoutSeconds.toFloat()).coerceIn(0f, 1f)
                val formattedTime = String.format("00:%02d", remainingSeconds)

                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Cercle d'arrière-plan gris
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF1E293B),
                        strokeWidth = 6.dp
                    )
                    // Cercle de décompte animé jaune ambre
                    CircularProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.fillMaxSize(),
                        color = if (remainingSeconds <= 5) FixoDangerRed else FixoElectricAmber,
                        strokeWidth = 6.dp
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.testTag("countdown_timer_text")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // 3. RÉMUNÉRATION NETTE GARANTIE (13 500 FCFA NET)
                // ==========================================
                val netEarningLabel = FixoStrings.getString("worker.alert.net_earning", language)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = netEarningLabel.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${formatFixoCurrency(mission.netAmount)} NET",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = FixoElectricAmber
                    )
                    Text(
                        text = if (language == AppLanguage.FR) "Commission plateforme déjà déduite" else "Fixo commission already deducted",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 4. DÉTAILS DE L'INTERVENTION
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                    border = BorderStroke(1.dp, Color(0x2EFFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Métier & Panne
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FixoElectricAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = FixoElectricAmber, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = mission.tradeName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = mission.title,
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Localisation & Temps de trajet estimé
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FixoEmerald500.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoEmerald500, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = mission.address,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "à ${mission.distanceKm} km • ~${mission.etaMinutes} min en moto",
                                    fontSize = 12.sp,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }

                        // Client & Séquestre provisionné
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${mission.customerName} ✓",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                val securedText = FixoStrings.getString("worker.alert.secured_escrow", language)
                                Text(
                                    text = securedText,
                                    fontSize = 11.sp,
                                    color = FixoEmerald500
                                )
                            }
                        }

                        // Exigence de Ponctualité (< 30 minutes)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FixoDangerRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = FixoDangerRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            val reqSub = FixoStrings.getString("worker.alert.requirement_sub", language)
                            Text(
                                text = reqSub,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCA5A5)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 5. SLIDE TO ACCEPT GESTURE (SEUIL >= 85%)
                // ==========================================
                val slideLabelTemplate = FixoStrings.getString("worker.alert.slide_label", language)
                val formattedSlideText = String.format(slideLabelTemplate, formatFixoCurrency(mission.netAmount))

                SlideToAcceptButton(
                    label = formattedSlideText,
                    onAccepted = {
                        onAccept(mission)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ==========================================
                // 6. BOUTON DISCRET DE REFUS
                // ==========================================
                val declineLabel = FixoStrings.getString("worker.alert.decline", language)
                TextButton(
                    onClick = onDecline,
                    modifier = Modifier.testTag("alert_decline_button")
                ) {
                    Text(
                        text = declineLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}
