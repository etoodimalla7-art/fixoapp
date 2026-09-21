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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PersonPinCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoBlue700
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance, self-contained Vector Map Canvas for live worker tracking.
 * Provides street grid, arterial boulevards, animated pulse radar, real-time route polyline,
 * vehicle marker with heading angle, destination pin, and map controls.
 */
@Composable
fun LiveTrackingMap(
    workerLat: Double,
    workerLng: Double,
    customerLat: Double,
    customerLng: Double,
    workerName: String,
    destinationAddress: String,
    isTrackingActive: Boolean,
    workerSpeedKmh: Float,
    workerHeading: Float,
    etaMinutes: Int,
    distanceKm: Double,
    modifier: Modifier = Modifier
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var centerMode by remember { mutableFloatStateOf(0.5f) } // 0.0f = focus destination, 1.0f = focus worker, 0.5f = balanced view

    // Pulsing radar animation for worker's live location
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 44f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E2638))
            .border(1.dp, Color(0xFF2C384E), RoundedCornerShape(16.dp))
            .testTag("live_tracking_map_container")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw city background blocks
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF182030), Color(0xFF131A27))
                )
            )

            // 2. Draw city street grid
            val gridColor = Color(0xFF243044)
            val majorAvenueColor = Color(0xFF2D3C55)

            // Horizontal secondary streets
            val streetSpacingY = 32f * zoomScale
            var y = (height * 0.1f) % streetSpacingY
            while (y < height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2.dp.toPx()
                )
                y += streetSpacingY
            }

            // Vertical secondary streets
            val streetSpacingX = 38f * zoomScale
            var x = (width * 0.1f) % streetSpacingX
            while (x < width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2.dp.toPx()
                )
                x += streetSpacingX
            }

            // Diagonal Primary Arterial Boulevards (representing Douala Boulevard de la Liberté / Akwa)
            drawLine(
                color = majorAvenueColor,
                start = Offset(0f, height * 0.85f),
                end = Offset(width, height * 0.15f),
                strokeWidth = 7.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFF384A68),
                start = Offset(0f, height * 0.85f),
                end = Offset(width, height * 0.15f),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawLine(
                color = majorAvenueColor,
                start = Offset(width * 0.2f, height),
                end = Offset(width * 0.85f, 0f),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )

            // 3. Normalized Anchor points based on user viewport mode
            val destOffset = Offset(
                x = width * (0.75f - (centerMode - 0.5f) * 0.2f),
                y = height * (0.28f + (centerMode - 0.5f) * 0.1f)
            )

            val workerOffset = Offset(
                x = width * (0.25f - (centerMode - 0.5f) * 0.2f),
                y = height * (0.75f + (centerMode - 0.5f) * 0.1f)
            )

            // 4. Realistic Navigation Route with waypoints
            val routeMid1 = Offset(width * 0.35f, height * 0.50f)
            val routeMid2 = Offset(width * 0.55f, height * 0.42f)

            val routePath = Path().apply {
                moveTo(workerOffset.x, workerOffset.y)
                lineTo(routeMid1.x, routeMid1.y)
                lineTo(routeMid2.x, routeMid2.y)
                lineTo(destOffset.x, destOffset.y)
            }

            // Outer route casing (dark navy border)
            drawPath(
                path = routePath,
                color = Color(0xFF0F172A),
                style = Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Dynamic Route Stroke (Fixo Gold / Emerald gradient)
            drawPath(
                path = routePath,
                brush = Brush.linearGradient(
                    colors = if (isTrackingActive) listOf(FixoGold500, FixoEmerald500) else listOf(Color(0xFF64748B), Color(0xFF94A3B8)),
                    start = workerOffset,
                    end = destOffset
                ),
                style = Stroke(
                    width = 4.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Directional route dash dots
            drawPath(
                path = routePath,
                color = Color.White.copy(alpha = 0.55f),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 18f), 0f),
                    cap = StrokeCap.Round
                )
            )

            // 5. Draw Customer Destination Pin
            // Soft base glow
            drawCircle(
                color = FixoEmerald500.copy(alpha = 0.25f),
                radius = 18.dp.toPx(),
                center = destOffset
            )
            // Inner base anchor
            drawCircle(
                color = FixoEmerald600,
                radius = 10.dp.toPx(),
                center = destOffset
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = destOffset
            )

            // 6. Draw Worker Live Marker
            if (isTrackingActive) {
                // Pulsating Radar Wave
                drawCircle(
                    color = FixoGold500.copy(alpha = pulseAlpha),
                    radius = (pulseRadius * zoomScale).dp.toPx(),
                    center = workerOffset
                )
            }

            // Worker Outer Ring
            drawCircle(
                color = Color(0xFF0F172A),
                radius = 14.dp.toPx(),
                center = workerOffset
            )
            // Worker Inner Core
            drawCircle(
                color = if (isTrackingActive) FixoGold500 else FixoBlue600,
                radius = 10.dp.toPx(),
                center = workerOffset
            )

            // Heading indicator arrow
            rotate(degrees = workerHeading, pivot = workerOffset) {
                val arrowTip = Offset(workerOffset.x, workerOffset.y - 12.dp.toPx())
                val arrowLeft = Offset(workerOffset.x - 5.dp.toPx(), workerOffset.y - 4.dp.toPx())
                val arrowRight = Offset(workerOffset.x + 5.dp.toPx(), workerOffset.y - 4.dp.toPx())

                val arrowPath = Path().apply {
                    moveTo(arrowTip.x, arrowTip.y)
                    lineTo(arrowLeft.x, arrowLeft.y)
                    lineTo(workerOffset.x, workerOffset.y - 2.dp.toPx())
                    lineTo(arrowRight.x, arrowRight.y)
                    close()
                }
                drawPath(path = arrowPath, color = Color.White)
            }
        }

        // Overlay 1: Live Status Chip & Speed (Top Left)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.90f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isTrackingActive) FixoEmerald500 else Color(0xFF94A3B8))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isTrackingActive) "LIVE GPS • ${(workerSpeedKmh).toInt()} km/h" else "TRIP CONCLUDED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // Overlay 2: Destination Label Callout (Top Right)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.90f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopEnd)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = FixoEmerald500,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = destinationAddress.take(22) + if (destinationAddress.length > 22) "..." else "",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFE2E8F0),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Overlay 3: Interactive Map Controls (+ / - / Re-center) (Bottom Right)
        Column(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF0F172A).copy(alpha = 0.88f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.size(34.dp)
            ) {
                IconButton(
                    onClick = { zoomScale = (zoomScale + 0.2f).coerceAtMost(1.8f) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = Color(0xFF0F172A).copy(alpha = 0.88f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.size(34.dp)
            ) {
                IconButton(
                    onClick = { zoomScale = (zoomScale - 0.2f).coerceAtLeast(0.7f) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = if (centerMode > 0.6f) FixoGold500 else Color(0xFF0F172A).copy(alpha = 0.88f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.size(34.dp)
            ) {
                IconButton(
                    onClick = { centerMode = if (centerMode == 0.5f) 0.9f else 0.5f },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Focus Artisan",
                        tint = if (centerMode > 0.6f) Color.Black else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Overlay 4: Worker Name & Distance Floating Banner (Bottom Left)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.BottomStart)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = FixoGold500,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "$workerName • en route",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "$distanceKm km remaining",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
