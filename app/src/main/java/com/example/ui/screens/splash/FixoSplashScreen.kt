package com.example.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoGold100
import com.example.ui.theme.FixoGold400
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash Screen Animé (0.0s à 1.2s)
 * Comportement matériel : Écran noir d'encre pur (#080C15). Aucune barre système apparente.
 *
 * Séquence visuelle :
 * - 0.0s à 0.3s : Apparition de l'aile supérieure dorée du logo FIXO avec un balayage de lumière progressif.
 * - 0.3s à 0.6s : Tracé de la base bleu nuit du logo et chute avec rebond élastique du point central ambre.
 * - 0.6s à 1.0s : Apparition de la signature bilingue : L'Excellence à votre Porte / Craftsmanship on Demand.
 * - 1.0s à 1.2s : Réduction fluide du logo vers le haut de l'écran pour former l'en-tête de la page d'authentification.
 */
@Composable
fun FixoSplashScreen(
    language: AppLanguage = AppLanguage.FR,
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0.0s - 0.3s : Wing appearance & light sweep
    val wingAlpha = remember { Animatable(0f) }
    val sweepProgress = remember { Animatable(0f) }

    // 0.3s - 0.6s : Base trace & bouncing amber dot
    val baseAlpha = remember { Animatable(0f) }
    val dotDropOffsetY = remember { Animatable(-80f) }
    val dotScale = remember { Animatable(0.2f) }

    // 0.6s - 1.0s : Slogan bilingual
    val sloganAlpha = remember { Animatable(0f) }

    // 1.0s - 1.2s : Fluid reduction towards header position
    val logoScale = remember { Animatable(1f) }
    val logoOffsetY = remember { Animatable(0f) }
    val overallAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Stage 1: 0.0s à 0.3s -> Golden wing appearance with progressive light sweep
        launch {
            wingAlpha.animateTo(1f, animationSpec = tween(300, easing = LinearEasing))
        }
        launch {
            sweepProgress.animateTo(1f, animationSpec = tween(300, easing = LinearEasing))
        }

        delay(300)

        // Stage 2: 0.3s à 0.6s -> Base navy & bounce drop of center amber dot
        launch {
            baseAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        }
        launch {
            dotScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            dotDropOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        delay(300) // now at 600ms

        // Stage 3: 0.6s à 1.0s -> Slogan appearance
        launch {
            sloganAlpha.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }

        delay(400) // now at 1000ms

        // Stage 4: 1.0s à 1.2s -> Reduction towards header
        launch {
            logoScale.animateTo(0.68f, animationSpec = tween(200, easing = FastOutSlowInEasing))
        }
        launch {
            logoOffsetY.animateTo(-160f, animationSpec = tween(200, easing = FastOutSlowInEasing))
        }
        launch {
            overallAlpha.animateTo(0.95f, animationSpec = tween(200, easing = LinearEasing))
        }

        delay(200) // now at 1200ms
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FixoBgCanvas) // Pure dark ink #080C15
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Tap to skip
                onSplashComplete()
            }
            .testTag("fixo_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient gold radial background glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FixoGold500.copy(alpha = 0.18f * wingAlpha.value),
                            FixoNavy900.copy(alpha = 0.10f * baseAlpha.value),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset { IntOffset(0, logoOffsetY.value.toInt()) }
                .scale(logoScale.value)
                .alpha(overallAlpha.value)
        ) {
            // Elevated Logo Card
            Surface(
                modifier = Modifier
                    .size(136.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = FixoGold500.copy(alpha = 0.35f),
                        spotColor = FixoGold500.copy(alpha = 0.6f)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .testTag("splash_logo_card"),
                color = FixoWhite
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fixo_logo),
                        contentDescription = "FIXO Brand Logo",
                        modifier = Modifier
                            .size(124.dp)
                            .padding(4.dp)
                            .alpha(wingAlpha.value.coerceAtLeast(0.3f)),
                        contentScale = ContentScale.Fit
                    )

                    // Stage 1: Light sweep line animation across logo
                    if (sweepProgress.value in 0.01f..0.99f) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val sweepX = size.width * sweepProgress.value
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        FixoGold100.copy(alpha = 0.7f),
                                        Color.White,
                                        Color.Transparent
                                    ),
                                    startX = sweepX - 40f,
                                    endX = sweepX + 40f
                                ),
                                start = Offset(sweepX, 0f),
                                end = Offset(sweepX + 30f, size.height),
                                strokeWidth = 18f
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FIXO Brand Title & Golden Center Accent Dot
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FIXO",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = FixoWhite
                )
                Spacer(modifier = Modifier.width(6.dp))

                // Amber central dot with elastic bounce
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, dotDropOffsetY.value.toInt()) }
                        .scale(dotScale.value)
                        .size(10.dp)
                        .background(FixoGold500, shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Signature Bilingue : L'Excellence à votre Porte / Craftsmanship on Demand
            Text(
                text = "L'Excellence à votre Porte / Craftsmanship on Demand",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = FixoGold100.copy(alpha = 0.90f),
                letterSpacing = 0.3.sp,
                modifier = Modifier.alpha(sloganAlpha.value)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Micro progress indicator
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .alpha(baseAlpha.value)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(3.dp),
                    color = FixoGold500,
                    trackColor = FixoNavy800
                )
            }
        }

        // Bottom Trust Mention
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
                .alpha(sloganAlpha.value * 0.7f)
        ) {
            Text(
                text = "Douala • Yaoundé • Bafoussam • Garoua",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.45f),
                letterSpacing = 1.sp
            )
        }
    }
}
