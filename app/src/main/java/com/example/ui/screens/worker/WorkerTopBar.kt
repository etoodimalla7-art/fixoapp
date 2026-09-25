package com.example.ui.screens.worker

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.SubscriptionTier
import com.example.data.model.User
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary

/**
 * CHANTIER 5 : TOP PRO BAR (56 dp) & COMMUTATEUR DE STATUT
 *
 * - Hauteur : 56 dp, fond semi-transparent flouté (#080C15 à 85% d'opacité)
 * - Cluster Gauche : Photo d'identité certifiée (avatar 36 dp avec anneau doré) et prénom Marc D. ✓
 *   Badge de niveau : Maître Artisan en micro-label doré
 * - Cluster Droite : Commutateur Haute Visibilité (Toggle En Ligne) 36 dp x 110 dp
 * - Cloche de notifications avec compteur
 * - Bouton développeur de simulation d'Alerte Flash
 */
@Composable
fun WorkerTopBar(
    user: User?,
    workerProfile: WorkerProfile?,
    isOnline: Boolean,
    onToggleOnline: (Boolean) -> Unit,
    notificationCount: Int = 2,
    language: AppLanguage = AppLanguage.FR,
    onSimulateFlashAlert: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_online")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_pulse"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("worker_top_bar"),
        color = Color(0xD9080C15), // #080C15 à 85% opacité
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ==========================================
            // CLUSTER GAUCHE : Photo 36 dp, Nom & Micro-label
            // ==========================================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box {
                    AsyncImage(
                        model = workerProfile?.avatarUrl ?: user?.avatarUrl ?: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                        contentDescription = "Photo d'identité certifiée",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, FixoGold500, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val fullName = workerProfile?.name ?: user?.name ?: "Marc Dubois"
                        val shortName = fullName.split(" ").let { parts ->
                            if (parts.size >= 2) "${parts[0]} ${parts[1].first().uppercase()}." else parts.firstOrNull() ?: "Marc D."
                        }
                        Text(
                            text = "$shortName ✓",
                            color = FixoTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Micro-label doré : Maître Artisan
                    val tierLabel = FixoStrings.getString("worker.topbar.master_craftsman", language)
                    Text(
                        text = tierLabel,
                        color = FixoGold500,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            // ==========================================
            // CLUSTER DROITE : Commutateur + Notifications + Simu
            // ==========================================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bouton développeur : Simuler Alerte Flash
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, FixoElectricAmber.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .clickable { onSimulateFlashAlert() }
                        .padding(horizontal = 6.dp, vertical = 5.dp)
                        .testTag("simulate_flash_alert_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = FixoElectricAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Flash",
                            color = FixoElectricAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Le Commutateur Haute Visibilité (Toggle En Ligne) : 36 dp x 110 dp
                val onlineLabel = FixoStrings.getString("worker.topbar.online", language)
                val offlineLabel = FixoStrings.getString("worker.topbar.offline", language)

                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isOnline) Color(0x3310B981) else Color(0xFF1F2937)
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isOnline) Color(0xFF10B981) else Color(0x1AFFFFFF),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable { onToggleOnline(!isOnline) }
                        .padding(horizontal = 8.dp)
                        .testTag("worker_status_toggle"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .scale(if (isOnline) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(if (isOnline) Color(0xFF10B981) else Color(0xFF94A3B8))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOnline) "🟢 $onlineLabel" else "⚪ $offlineLabel",
                            color = if (isOnline) Color(0xFF10B981) else Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Cloche de notifications avec compteur
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (notificationCount > 0) {
                                Badge(
                                    containerColor = FixoElectricAmber,
                                    contentColor = Color(0xFF080C15)
                                ) {
                                    Text(notificationCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
