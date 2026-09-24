package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.Reel
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * CHANTIER 3 - Carrousel "En Direct des Chantiers" (Aperçu Reels)
 * - Ruban horizontal de capsules vidéo 9:16 (largeur 90 dp, hauteur 135 dp, coins 14 dp)
 * - Miniature vidéo avec dégradé sombre affichant :
 *   - Icône Play ▶
 *   - Prénom de l'artisan et corps de métier (ex: Marc (Plombier))
 *   - Quartier d'intervention en doré (ex: Akwa)
 * - Tap sur une capsule ➔ Ouvre immédiatement le lecteur plein écran Reels sur la vidéo exacte
 */
@Composable
fun LiveStoriesRow(
    reels: List<Reel>,
    language: AppLanguage,
    onWatchReel: (Reel) -> Unit,
    onViewAllReels: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (reels.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("live_stories_section")
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = FixoElectricAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (language == AppLanguage.FR) "En Direct des Chantiers" else "Live from Job Sites",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoTextPrimary
                    )
                    Text(
                        text = if (language == AppLanguage.FR) "Preuves de savoir-faire en vidéo" else "Video proof of master craftsmanship",
                        fontSize = 11.sp,
                        color = FixoTextMuted
                    )
                }
            }

            TextButton(
                onClick = onViewAllReels,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.FR) "Tout voir" else "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoElectricAmber
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Ribbon of 9:16 video pills (width: 90dp, height: 135dp, corner: 14dp)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(reels) { reel ->
                LiveStoryCapsule(
                    reel = reel,
                    onClick = { onWatchReel(reel) }
                )
            }
        }
    }
}

@Composable
fun LiveStoryCapsule(
    reel: Reel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val workerFirstName = reel.workerName.split(" ").firstOrNull() ?: reel.workerName
    val categoryLabel = reel.category.displayName.split(" ").firstOrNull() ?: reel.category.displayName
    val district = "Akwa" // Standard Cameroon district context

    Surface(
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .width(96.dp)
            .height(144.dp)
            .border(1.2.dp, FixoElectricAmber.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("reel_capsule_${reel.id}"),
        color = Color(0xFF162136)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Video Thumbnail
            AsyncImage(
                model = reel.thumbnailUrl.ifBlank { "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=300" },
                contentDescription = reel.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Scrim overlay for legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.2f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            // Center Play Icon Badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, FixoElectricAmber.copy(alpha = 0.8f), CircleShape)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = FixoElectricAmber,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Bottom Caption Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "$workerFirstName ($categoryLabel)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "📍 $district",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FixoElectricAmber,
                    maxLines = 1
                )
            }
        }
    }
}
