package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * CHANTIER 3 - Radar Vectoriel de Proximité (120 dp)
 * - Fond dégradé radial sombre #0D131F avec bordure 1.5 px rgba(255, 255, 255, 0.12)
 * - Ondes concentriques dorées pulsantes (RadarPulseAnimation, cycle 2.5s)
 * - Télémétrie locale : 🟢 14 techniciens certifiés disponibles à Akwa
 * - Interaction : Un tap sur le radar bascule vers l'écran cartographique plein écran
 */
@Composable
fun ProximityRadarCard(
    activeCount: Int = 14,
    currentQuarterName: String = "Akwa",
    language: AppLanguage = AppLanguage.FR,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarWaveTransition")

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, delayMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave2"
    )

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Sweep"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, FixoBorderSubtle, RoundedCornerShape(16.dp))
            .clickable { onOpenMap() }
            .testTag("proximity_radar_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D131F))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF162136),
                            Color(0xFF0D131F),
                            FixoBgCanvas
                        ),
                        radius = 450f
                    )
                )
        ) {
            // Radar Vector Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("radar_vector_canvas")
            ) {
                val centerOffset = Offset(size.width * 0.82f, size.height * 0.5f)
                val maxRadius = size.height * 0.85f

                // Concentric guide circles
                drawCircle(
                    color = Color(0x1AFFFFFF),
                    radius = maxRadius * 0.35f,
                    center = centerOffset,
                    style = Stroke(width = 1f)
                )
                drawCircle(
                    color = Color(0x14FFFFFF),
                    radius = maxRadius * 0.68f,
                    center = centerOffset,
                    style = Stroke(width = 1f)
                )
                drawCircle(
                    color = Color(0x0FFFFFFF),
                    radius = maxRadius,
                    center = centerOffset,
                    style = Stroke(width = 1f)
                )

                // Crosshairs
                drawLine(
                    color = Color(0x1AFFFFFF),
                    start = Offset(centerOffset.x - maxRadius, centerOffset.y),
                    end = Offset(centerOffset.x + maxRadius, centerOffset.y),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color(0x1AFFFFFF),
                    start = Offset(centerOffset.x, centerOffset.y - maxRadius),
                    end = Offset(centerOffset.x, centerOffset.y + maxRadius),
                    strokeWidth = 1f
                )

                // Pulsing golden waves
                if (wave1 > 0f) {
                    val radius1 = maxRadius * wave1
                    val alpha1 = (1f - wave1) * 0.55f
                    drawCircle(
                        color = FixoElectricAmber.copy(alpha = alpha1),
                        radius = radius1,
                        center = centerOffset,
                        style = Stroke(width = 2.5f)
                    )
                }

                if (wave2 > 0f) {
                    val radius2 = maxRadius * wave2
                    val alpha2 = (1f - wave2) * 0.45f
                    drawCircle(
                        color = FixoElectricAmber.copy(alpha = alpha2),
                        radius = radius2,
                        center = centerOffset,
                        style = Stroke(width = 2f)
                    )
                }

                // Central radar pulse core
                drawCircle(
                    color = FixoElectricAmber,
                    radius = 4.5f,
                    center = centerOffset
                )
                drawCircle(
                    color = FixoElectricAmber.copy(alpha = 0.35f),
                    radius = 9f,
                    center = centerOffset
                )

                // Simulated active artisan patrol blips
                val blip1 = Offset(centerOffset.x - 38f, centerOffset.y - 20f)
                val blip2 = Offset(centerOffset.x - 62f, centerOffset.y + 24f)
                val blip3 = Offset(centerOffset.x - 22f, centerOffset.y + 36f)
                drawCircle(color = FixoEmerald500, radius = 3.5f, center = blip1)
                drawCircle(color = FixoEmerald500, radius = 3f, center = blip2)
                drawCircle(color = FixoElectricAmber, radius = 3.5f, center = blip3)
            }

            // Foreground Content Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Golden Telemetry Title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "Radar",
                            tint = FixoElectricAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "RADAR PATROUILLE FIXO" else "FIXO PATROL RADAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FixoElectricAmber,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Dynamic Telemetry Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0x4010B981),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FixoEmerald500.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(FixoEmerald500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "$activeCount certifiés dispo à $currentQuarterName"
                                else
                                    "$activeCount certified available in $currentQuarterName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Secondary Subtext
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "Ouvrir la carte interactive" else "Open live patrol map",
                            fontSize = 11.sp,
                            color = FixoTextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = FixoElectricAmber,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Right side empty space where canvas radar animates cleanly
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFB800)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Map radar",
                        tint = FixoElectricAmber,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
