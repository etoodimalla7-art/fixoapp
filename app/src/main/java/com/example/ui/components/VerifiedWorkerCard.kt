package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoElectricAmberDark
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * CHANTIER 3 - Cartes Artisans Recommandés (VerifiedWorkerCard)
 * - Conteneur : Fond gris ardoise contrasté #1A2232, bordure 1.5 px rgba(255, 255, 255, 0.12), coins 16 dp
 * - En-tête : Avatar rond avec anneau ambre de Maître Artisan, nom complet, badge officiel "Certifié Fixo ✓" en vert émeraude (#10B981)
 * - Ligne de confiance : ⭐ 4.9 (124 chantiers) • À 1.2 km • Répond en 3 min
 * - Spécialité : Plomberie sanitaire • Détection de fuites & soudure PEX
 * - Règle Ergonomique Stricte : 1 Seul Bouton d'Action Primaire :
 *   Cliquer sur le corps ou l'avatar ouvre la modale Passeport Artisan.
 *   Le bas de la carte contient un unique grand bouton pleine largeur (52 dp, fond ambre saturé #FFB800 vers #FF9100) :
 *   [ Réserver avec Marc • 15 000 FCFA Tout Inclus ]
 */
@Composable
fun VerifiedWorkerCard(
    worker: WorkerProfile,
    isFlashMode: Boolean = false,
    language: AppLanguage = AppLanguage.FR,
    onCardClick: () -> Unit,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstName = worker.name.split(" ").firstOrNull() ?: worker.name
    val priceRate = if (isFlashMode) 15000 else 12000
    val priceFormatted = "15 000" // Standard Chantier 3 package price display

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .border(1.5.dp, FixoBorderSubtle, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .testTag("verified_worker_card_${worker.id}"),
        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Avatar with Golden Ring + Names & Trust Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Round Avatar with Amber Ring of Master Craftsman
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, FixoElectricAmber, CircleShape)
                        .clickable { onCardClick() },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = worker.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300" },
                        contentDescription = worker.name,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Full Name + Official Fixo Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = worker.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Official Fixo Badge in Emerald Green (#10B981)
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0x3310B981),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FixoEmerald500)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = FixoEmerald500,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Certifié Fixo ✓",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FixoEmerald500
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Ligne de confiance : ⭐ 4.9 (124 chantiers) • À 1.2 km • Répond en 3 min
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = FixoElectricAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "%.1f".format(worker.rating),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoTextPrimary
                        )
                        Text(
                            text = "(${worker.completedJobs} chantiers)",
                            fontSize = 11.sp,
                            color = FixoTextSecondary
                        )
                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            color = FixoBorderSubtle
                        )
                        Text(
                            text = "À 1.2 km",
                            fontSize = 11.sp,
                            color = FixoTextSecondary
                        )
                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            color = FixoBorderSubtle
                        )
                        Text(
                            text = "Répond en 3 min",
                            fontSize = 11.sp,
                            color = FixoEmerald500,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Spécialité technique
                    val specialtyText = if (worker.skills.isNotBlank()) {
                        worker.skills
                    } else {
                        "${worker.category.displayName} • Détection de fuites & soudure PEX"
                    }
                    Text(
                        text = specialtyText,
                        fontSize = 11.sp,
                        color = FixoTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Règle Ergonomique Stricte : 1 SEUL Grand Bouton d'Action Primaire (Hauteur: 52 dp)
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("book_worker_primary_button_${worker.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FixoElectricAmber,
                    contentColor = FixoBgCanvas
                )
            ) {
                Text(
                    text = if (language == AppLanguage.FR)
                        "Réserver avec $firstName • $priceFormatted FCFA Tout Inclus"
                    else
                        "Book with $firstName • $priceFormatted FCFA All-Inclusive",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FixoBgCanvas
                )
            }
        }
    }
}
