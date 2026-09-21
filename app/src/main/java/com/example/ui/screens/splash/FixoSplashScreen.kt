package com.example.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoGold100
import com.example.ui.theme.FixoGold400
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoWhite
import kotlinx.coroutines.delay

/**
 * High-fidelity animated Splash Screen featuring the official FIXO brand logo
 * and color palette (Midnight Obsidian & Golden Amber).
 */
@Composable
fun FixoSplashScreen(
    language: AppLanguage = AppLanguage.EN,
    onSplashComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }
    val glowPulse = remember { Animatable(0.85f) }
    val progress = remember { Animatable(0.05f) }

    LaunchedEffect(Unit) {
        // Staggered bounce scale & fade in
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        glowPulse.animateTo(
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        // Animate smooth progress bar to 100% over 1.8s
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
        )
        delay(150)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FixoNavy950,
                        FixoNavy900,
                        Color(0xFF080F1E)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Tap anywhere to skip immediately
                onSplashComplete()
            },
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient gold radial background glow
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(glowPulse.value)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            FixoGold500.copy(alpha = 0.18f),
                            FixoGold600.copy(alpha = 0.08f),
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
                .padding(horizontal = 32.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Elevated White Squircle Logo Card — directly from the official fixo logo design
            Surface(
                modifier = Modifier
                    .size(136.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = FixoGold500.copy(alpha = 0.25f),
                        spotColor = FixoGold500.copy(alpha = 0.4f)
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
                            .size(126.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Typography
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
                // Golden accent dot echoing the logo's gold sphere
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(FixoGold500, shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == AppLanguage.FR)
                    "L'excellence des métiers & réparations au Cameroun"
                else
                    "Master Crafts & Verified Services in Cameroon",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = FixoGold100.copy(alpha = 0.85f),
                letterSpacing = 0.4.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Refined Golden Progress Bar
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxSize()
                        .height(4.dp),
                    color = FixoGold500,
                    trackColor = FixoNavy800
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trust & Escrow Micro-copy
            Text(
                text = if (language == AppLanguage.FR)
                    "Sécurisation Escrow & Réseau d'Artisans..."
                else
                    "Securing Escrow & Verified Artisans...",
                fontSize = 11.sp,
                color = FixoGold400.copy(alpha = 0.7f),
                letterSpacing = 0.2.sp
            )
        }

        // Bottom Brand Security Footnote
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alpha.value)
        ) {
            Text(
                text = "Douala • Yaoundé • Bafoussam • Garoua",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.sp
            )
        }
    }
}
