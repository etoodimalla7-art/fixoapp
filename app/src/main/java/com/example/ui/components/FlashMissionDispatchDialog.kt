package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoDangerRed
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGoldGradient
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Uber Driver-style Priority Incoming Flash Mission Dispatch Alert (Écran A2)
 * Features 30s audio-visual countdown, urgent beacon pulsation, guaranteed net payout,
 * and tactile "Slide to Accept" gesture to prevent accidental confirmation.
 */
@Composable
fun FlashMissionDispatchDialog(
    tradeTitle: String = "Plomberie sanitaire (Fuite d'eau)",
    quarterLocation: String = "Akwa, Rue Drouot (à 1.2 km de votre position)",
    netPayout: String = "13 500 FCFA",
    clientName: String = "Sarah Jenkins",
    language: AppLanguage = AppLanguage.FR,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    var secondsLeft by remember { mutableIntStateOf(30) }
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        pulseScale.animateTo(
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        if (secondsLeft == 0) {
            onDecline()
        }
    }

    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val maxDragWidthPx = 540f // approx width threshold for slide to accept

    Dialog(
        onDismissRequest = onDecline,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FixoBgCanvas.copy(alpha = 0.94f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, FixoGold500, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Alert Beacon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .scale(pulseScale.value)
                                    .clip(CircleShape)
                                    .background(FixoGold500),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = FixoBgCanvas,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "⚡ ALERTE MISSION FLASH" else "⚡ FLASH MISSION DISPATCH",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = FixoGold500
                            )
                        }

                        IconButton(onClick = onDecline) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Decline",
                                tint = FixoTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 30s Countdown timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (secondsLeft <= 10) FixoDangerRed else FixoGold500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Temps restant : 00:${secondsLeft.toString().padStart(2, '0')}s" else "Accept in : 00:${secondsLeft.toString().padStart(2, '0')}s",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (secondsLeft <= 10) FixoDangerRed else FixoTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { secondsLeft / 30f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (secondsLeft <= 10) FixoDangerRed else FixoGold500,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Mission Details Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, FixoBorderSubtle, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Plumbing, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = tradeTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoTextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = quarterLocation,
                                    fontSize = 13.sp,
                                    color = FixoTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FixoSuccessGreen.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == AppLanguage.FR) "Délai d'arrivée requis :" else "Required Arrival :",
                                    fontSize = 11.sp,
                                    color = FixoTextSecondary
                                )
                                Text(
                                    text = if (language == AppLanguage.FR) "< 30 minutes" else "< 30 min guaranteed",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoSuccessGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Guaranteed Net Payout Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E293B))
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "GAIN NET GARANTI ARTISAN" else "GUARANTEED NET ARTISAN PAYOUT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FixoTextSecondary,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = netPayout,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = FixoGold500
                        )
                        Text(
                            text = if (language == AppLanguage.FR) "Commission plateforme (1 500 FCFA) déjà déduite" else "Platform commission already deducted",
                            fontSize = 11.sp,
                            color = FixoTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Horizontal "Slide to Accept" Gesture Rail
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, FixoGold500.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
                            .testTag("slide_to_accept_rail"),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        // Rail Background Text
                        Text(
                            text = if (language == AppLanguage.FR) ">>> Glisser pour accepter >>>" else ">>> Slide to accept >>>",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )

                        // Draggable Thumb Button
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(dragOffsetX.roundToInt(), 0) }
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(FixoGoldGradient)
                                .draggable(
                                    orientation = Orientation.Horizontal,
                                    state = rememberDraggableState { delta ->
                                        val newOffset = (dragOffsetX + delta).coerceIn(0f, maxDragWidthPx)
                                        dragOffsetX = newOffset
                                        if (newOffset >= maxDragWidthPx * 0.85f) {
                                            onAccept()
                                        }
                                    },
                                    onDragStopped = {
                                        if (dragOffsetX < maxDragWidthPx * 0.85f) {
                                            dragOffsetX = 0f
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Accept",
                                tint = FixoBgCanvas,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Decline Button
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("decline_mission_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, FixoBorderSubtle),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoTextSecondary)
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "Refuser la mission" else "Decline Mission",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
