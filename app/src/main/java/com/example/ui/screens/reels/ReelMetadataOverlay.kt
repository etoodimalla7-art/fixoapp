package com.example.ui.screens.reels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Reel
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoEmerald500

/**
 * Métadonnées du Chantier (Zone Inférieure Gauche) — CHANTIER 4
 * - Identité Artisan : @Marc_Dubois_Plomberie + badge officiel émeraude Certifié Fixo ✓
 * - Localisation du Chantier : Pastille sombre translucide avec broche dorée 📍 Akwa, Douala
 * - Titre Technique de la Réalisation : Blanc pur 14px Semi-Bold (2 lignes max)
 * - Hashtags Métier : #Plomberie #SoudurePEX #UrgenceDouala en ambre clair #FFA726
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReelMetadataOverlay(
    reel: Reel,
    worker: WorkerProfile,
    language: AppLanguage,
    onArtisanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val handleCategory = when (reel.category) {
        com.example.data.model.ServiceCategory.PLUMBING -> "Plomberie"
        com.example.data.model.ServiceCategory.ELECTRICAL -> "Electricite"
        com.example.data.model.ServiceCategory.AC_COOLING -> "Climatisation"
        else -> reel.category.name.lowercase().replaceFirstChar { it.uppercase() }
    }
    val artisanHandle = "@${reel.workerName.replace(" ", "_")}_$handleCategory"
    val verifiedBadgeText = FixoStrings.getString("reels.verified_badge", language)

    Column(
        modifier = modifier
            .testTag("reel_metadata_overlay_${reel.id}")
            .padding(bottom = 8.dp)
    ) {
        // Localisation du Chantier : Pastille sombre translucide avec broche dorée
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.55f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.testTag("reel_location_badge_${reel.id}")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = FixoElectricAmber,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = reel.location,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Identité Artisan (@Marc_Dubois_Plomberie) + Badge Émeraude Certifié Fixo ✓
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onArtisanClick() }
                .testTag("reel_artisan_identity_${reel.id}")
        ) {
            Text(
                text = artisanHandle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = FixoEmerald500.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoEmerald500.copy(alpha = 0.6f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Certifié",
                        tint = FixoEmerald500,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = verifiedBadgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FixoEmerald500
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Titre Technique de la Réalisation : Texte blanc pur 14px Semi-Bold (2 lignes max)
        Text(
            text = reel.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp,
            modifier = Modifier.testTag("reel_technical_title_${reel.id}")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Hashtags Métier en ambre clair #FFA726
        val tagList = reel.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.testTag("reel_hashtags_${reel.id}")
        ) {
            tagList.forEach { tag ->
                val displayTag = if (tag.startsWith("#")) tag else "#$tag"
                Text(
                    text = displayTag,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoElectricAmber
                )
            }
        }
    }
}
