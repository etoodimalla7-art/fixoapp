package com.example.ui.screens.worker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500

/**
 * CHANTIER 5 : INDICATEURS D'ACTIVITÉ & PROGRESSION
 *
 * Rangée de 3 tuiles compactes mesurant l'efficacité du technicien :
 * - Note Moyenne : ⭐ 4.9 (124 avis vérifiés)
 * - Ponctualité / Flash : 98% à l'heure sur les urgences < 30 min
 * - Palier de Commission : Maître Artisan (8%) avec barre de progression linéaire
 */
@Composable
fun WorkerStatsRow(
    rating: Double = 4.9,
    reviewCount: Int = 124,
    punctualityPercent: Int = 98,
    commissionTierName: String = "Maître Artisan (8%)",
    remainingJobsToKeepPrivilege: Int = 3,
    progressFraction: Float = 0.85f,
    language: AppLanguage = AppLanguage.FR,
    modifier: Modifier = Modifier
) {
    val ratingTitle = FixoStrings.getString("worker.stats.rating", language)
    val punctualityTitle = FixoStrings.getString("worker.stats.punctuality", language)
    val punctualitySub = FixoStrings.getString("worker.stats.punctuality_sub", language)
    val tierProgressText = FixoStrings.getString("worker.stats.tier_progress", language)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("worker_stats_row"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Rangée 1 : Deux tuiles statistiques
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tuile 1 : Note Moyenne
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                border = BorderStroke(1.dp, Color(0x1FFFFFFF))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = FixoGold500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = ratingTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "⭐ $rating",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Text(
                        text = "$reviewCount ${if (language == AppLanguage.FR) "avis vérifiés" else "verified reviews"}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Tuile 2 : Ponctualité Flash
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
                border = BorderStroke(1.dp, Color(0x1FFFFFFF))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = FixoElectricAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = punctualityTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$punctualityPercent%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = FixoEmerald500
                    )

                    Text(
                        text = punctualitySub,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1
                    )
                }
            }
        }

        // Rangée 2 : Tuile Palier de Commission & Barre de Progression
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2D)),
            border = BorderStroke(1.dp, Color(0x1FFFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = FixoGold500,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = commissionTierName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500
                        )
                    }

                    Text(
                        text = "${(progressFraction * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = FixoGold500,
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = tierProgressText,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
