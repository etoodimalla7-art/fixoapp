package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoElectricAmberDark
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy900
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * CHANTIER 5 : LE CONTRÔLE TACTILE SIGNATURE « SLIDE TO ACCEPT »
 *
 * Empêche formellement toute acceptation accidentelle en poche.
 * - Hauteur : 60 dp, Coins : 30 dp
 * - Rail sombre avec texte animé
 * - Poignée circulaire dorée ambre
 * - Déclenchement automatique dès franchissement de 85% de la course
 * - Retour haptique puissant et validation
 */
@Composable
fun SlideToAcceptButton(
    label: String,
    onAccepted: () -> Unit,
    modifier: Modifier = Modifier,
    threshold: Float = 0.85f,
    isEnabled: Boolean = true
) {
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val density = LocalDensity.current

    val thumbSize = 52.dp
    val thumbPadding = 4.dp
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val thumbPaddingPx = with(density) { thumbPadding.toPx() }

    val offsetX = remember { Animatable(0f) }
    var isTriggered by remember { mutableStateOf(false) }

    // Shimmer / pulsing animated text transition
    val infiniteTransition = rememberInfiniteTransition(label = "slide_text_shimmer")
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text_alpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF0F172A))
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        FixoElectricAmber.copy(alpha = 0.5f),
                        Color(0xFF334155),
                        FixoElectricAmber.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            )
            .testTag("slide_to_accept_button")
    ) {
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val maxDragPx = (totalWidthPx - thumbSizePx - (thumbPaddingPx * 2)).coerceAtLeast(0f)

        // Track fill effect behind thumb
        val currentProgress = if (maxDragPx > 0f) (offsetX.value / maxDragPx).coerceIn(0f, 1f) else 0f

        if (currentProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(30.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                FixoElectricAmber.copy(alpha = 0.28f * currentProgress),
                                FixoEmerald500.copy(alpha = 0.35f * currentProgress)
                            )
                        )
                    )
            )
        }

        // Center animated prompt text
        Text(
            text = if (isTriggered) "MISSION VALIDÉE !" else label,
            color = if (isTriggered) FixoEmerald500 else Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 56.dp)
                .alpha(if (isTriggered) 1f else textAlpha.coerceAtLeast(0.3f))
        )

        // Draggable circular handle
        Box(
            modifier = Modifier
                .offset { IntOffset((thumbPaddingPx + offsetX.value).roundToInt(), thumbPaddingPx.roundToInt()) }
                .size(thumbSize)
                .clip(CircleShape)
                .background(
                    if (isTriggered) {
                        Brush.verticalGradient(listOf(FixoEmerald500, Color(0xFF059669)))
                    } else {
                        Brush.verticalGradient(listOf(FixoElectricAmber, FixoElectricAmberDark))
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isTriggered) Color.White else FixoGold500,
                    shape = CircleShape
                )
                .draggable(
                    orientation = Orientation.Horizontal,
                    enabled = isEnabled && !isTriggered,
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            val target = (offsetX.value + delta).coerceIn(0f, maxDragPx)
                            offsetX.snapTo(target)
                        }
                    },
                    onDragStopped = {
                        val finalProgress = if (maxDragPx > 0f) (offsetX.value / maxDragPx) else 0f
                        if (finalProgress >= threshold) {
                            // Completed slide >= 85%
                            isTriggered = true
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            coroutineScope.launch {
                                offsetX.animateTo(maxDragPx, spring())
                                onAccepted()
                            }
                        } else {
                            // Cancelled slide (< 85%) -> snap back smoothly to 0
                            coroutineScope.launch {
                                offsetX.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 400f))
                            }
                        }
                    }
                )
                .testTag("slide_thumb"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isTriggered) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Glisser pour accepter",
                tint = Color(0xFF080C15),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
