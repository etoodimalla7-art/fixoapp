package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.FixoGold500

data class SymbolicAvatarDefinition(
    val id: String,
    val titleFr: String,
    val titleEn: String,
    val subtitleFr: String,
    val subtitleEn: String
)

val FIXO_SYMBOLIC_AVATARS = listOf(
    SymbolicAvatarDefinition("symbol:shield", "Bouclier Séquestre", "Escrow Shield", "Protection Or Ambre", "Gold Shield Protection"),
    SymbolicAvatarDefinition("symbol:master_wrench", "Insigne Maître", "Master Insignia", "Artisanat Vérifié", "Verified Craftsmanship"),
    SymbolicAvatarDefinition("symbol:bolt", "Éclair Précision", "Precision Bolt", "Énergie & Rapidité", "Speed & Energy"),
    SymbolicAvatarDefinition("symbol:architect", "Compas d'Architecte", "Architect Compass", "Conformité Technique", "Technical Precision"),
    SymbolicAvatarDefinition("symbol:water_drop", "Flux & Sanitaire", "Sanitary Flow", "Génie Fluides", "Plumbing Fluids"),
    SymbolicAvatarDefinition("symbol:monogram", "Monogramme Initiales", "Initials Monogram", "Signature Dégradé Or", "Gold Gradient Signature")
)

/**
 * Universal Avatar display component:
 * Renders either symbolic vector badges, dynamic monogram, or gallery/camera photos.
 * Zero reliance on random human face stock photos.
 */
@Composable
fun FixoSymbolicAvatar(
    avatarUrl: String?,
    userName: String = "Sarah Jenkins",
    size: Dp = 76.dp,
    modifier: Modifier = Modifier
) {
    val cleanUrl = avatarUrl?.trim().orEmpty()
    val initials = getInitials(userName)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(2.dp, FixoGold500, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            cleanUrl == "symbol:shield" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0A0E17)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Bouclier Séquestre",
                        tint = FixoGold500,
                        modifier = Modifier.size(size * 0.52f)
                    )
                }
            }
            cleanUrl == "symbol:master_wrench" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Construction,
                        contentDescription = "Insigne Maître Artisan",
                        tint = FixoGold500,
                        modifier = Modifier.size(size * 0.52f)
                    )
                }
            }
            cleanUrl == "symbol:bolt" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF111827)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Éclair d'Énergie",
                        tint = FixoGold500,
                        modifier = Modifier.size(size * 0.55f)
                    )
                }
            }
            cleanUrl == "symbol:architect" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF080C15)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Handyman,
                        contentDescription = "Compas d'Architecte",
                        tint = FixoGold500,
                        modifier = Modifier.size(size * 0.52f)
                    )
                }
            }
            cleanUrl == "symbol:water_drop" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF081326)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Plumbing,
                        contentDescription = "Goutte d'Eau & Sanitaire",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(size * 0.52f)
                    )
                }
            }
            cleanUrl == "symbol:monogram" || cleanUrl.isBlank() || cleanUrl.contains("unsplash.com") -> {
                // Dynamic Gold Gradient Monogram
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFF59E0B),
                                    Color(0xFFD97706),
                                    Color(0xFF78350F)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = (size.value * 0.38f).sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
            else -> {
                // Real photo selected from camera or phone gallery
                AsyncImage(
                    model = cleanUrl,
                    contentDescription = "Photo de profil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

private fun getInitials(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
        parts.isNotEmpty() -> parts[0].take(2).uppercase()
        else -> "FX"
    }
}
