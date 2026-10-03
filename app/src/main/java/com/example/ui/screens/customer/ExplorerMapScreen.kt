package com.example.ui.screens.customer

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.ui.components.LiveTrackingMap
import com.example.ui.components.WorkerPassportModal
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * CHANTIER 3 - Écran Cartographique Plein Écran : ExplorerMapScreen
 * Déployé lors du tap sur le Radar Vectoriel de Proximité (ProximityRadarCard).
 * Affiche la télémétrie des techniciens certifiés en patrouille active autour du quartier.
 */
@Composable
fun ExplorerMapScreen(
    workers: List<WorkerProfile>,
    currentQuarterName: String = "Akwa",
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    onWorkerSelected: (WorkerProfile) -> Unit,
    onBookWorker: (WorkerProfile, Boolean, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedWorkerForPassport by remember { mutableStateOf<WorkerProfile?>(null) }
    var hoveredWorker by remember { mutableStateOf(workers.firstOrNull()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FixoBgCanvas)
            .testTag("explorer_map_screen")
    ) {
        // Fullscreen Real Douala Geographic Vector Map
        com.example.ui.components.DoualaGeographicMap(
            workers = workers,
            selectedWorker = hoveredWorker,
            onSelectWorker = { worker ->
                hoveredWorker = worker
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top App Bar Floating Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(16.dp),
            color = FixoBgCanvas.copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FixoElectricAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = FixoElectricAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "PATROUILLES EN DIRECT" else "LIVE PATROL RADAR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FixoElectricAmber,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "📍 $currentQuarterName, Douala",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoTextPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x3310B981),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoEmerald500)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(FixoEmerald500)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "14 Actifs",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoEmerald500
                        )
                    }
                }
            }
        }

        // Bottom Worker Selection Sheet Preview (Inspiration Uber)
        hoveredWorker?.let { worker ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .border(2.dp, FixoGold500, CircleShape)
                        ) {
                            AsyncImage(
                                model = worker.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300" },
                                contentDescription = worker.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = worker.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = FixoEmerald500.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Certifié ✓",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FixoEmerald500,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "4.9 ★",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoGold500
                                )
                                Text(
                                    text = " • 1.2 km • ~15 min d'arrivée",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Fixed Escrow Badge
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "15 000 FCFA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = FixoGold500
                            )
                            Text(
                                text = "Forfait Garanti",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Single Major Action Button
                    Button(
                        onClick = { onBookWorker(worker, true, 15000.0) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_view_passport_book"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FixoGold500,
                            contentColor = Color(0xFF0A0E17)
                        )
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR)
                                "Réserver cette intervention (15 000 FCFA)"
                            else
                                "Book This Intervention (15,000 FCFA)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0A0E17)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedWorkerForPassport = worker }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "Voir le Passeport Technique & Diplômes →" else "View Technical Passport & Credentials →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Detailed Worker Passport Modal
        selectedWorkerForPassport?.let { worker ->
            WorkerPassportModal(
                worker = worker,
                language = language,
                initialIsFlash = true,
                onDismiss = { selectedWorkerForPassport = null },
                onBookConfirmed = { isFlash, price ->
                    val chosen = worker
                    selectedWorkerForPassport = null
                    onBookWorker(chosen, isFlash, price)
                }
            )
        }
    }
}
