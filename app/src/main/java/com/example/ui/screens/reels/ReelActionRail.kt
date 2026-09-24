package com.example.ui.screens.reels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Reel
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoRed500
import kotlinx.coroutines.launch

/**
 * Rail Latéral d'Interactions (Côté Droit) — CHANTIER 4
 * 1. Avatar Artisan (48 dp) avec liseré ambre et bouton [ + ] superposé
 * 2. Bouton Favoris / Like avec micro-rebond et compteur
 * 3. Bouton Avis & Preuves Techniques avec compteur vérifié
 * 4. Bouton Partage WhatsApp via Android Share Intent
 * 5. Contrôle Audio Mute / Unmute
 */
@Composable
fun ReelActionRail(
    reel: Reel,
    worker: WorkerProfile,
    isLiked: Boolean,
    likesCount: Int,
    isFollowed: Boolean,
    isMuted: Boolean,
    language: AppLanguage,
    onLikeClick: () -> Unit,
    onFollowClick: () -> Unit,
    onReviewsClick: () -> Unit,
    onMuteToggle: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val heartScale = remember { Animatable(1f) }

    Column(
        modifier = modifier.testTag("reel_action_rail_${reel.id}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Avatar de l'Artisan (48 dp) + Bouton [ + ] doré
        Box(
            modifier = Modifier
                .size(54.dp)
                .testTag("reel_avatar_container_${reel.id}"),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, FixoElectricAmber, CircleShape)
                    .clickable { onOpenProfile() }
                    .testTag("reel_artisan_avatar_${reel.id}")
            ) {
                AsyncImage(
                    model = reel.workerAvatar,
                    contentDescription = reel.workerName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Bouton [ + ] ou checkmark superposé
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isFollowed) FixoEmerald500 else FixoElectricAmber)
                    .clickable { onFollowClick() }
                    .testTag("reel_follow_button_${reel.id}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFollowed) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (isFollowed) "Abonné" else "S'abonner",
                    tint = FixoNavy950,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 2. Bouton Favoris / Like avec micro-rebond
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.testTag("reel_like_section_${reel.id}")
        ) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        heartScale.animateTo(
                            targetValue = 1.35f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                        heartScale.animateTo(1f)
                    }
                    onLikeClick()
                },
                modifier = Modifier
                    .size(44.dp)
                    .scale(heartScale.value)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("reel_like_${reel.id}")
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) FixoRed500 else Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$likesCount",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 3. Bouton Avis & Preuves Techniques
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.testTag("reel_reviews_section_${reel.id}")
        ) {
            IconButton(
                onClick = onReviewsClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("reel_reviews_button_${reel.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Avis & Preuves Techniques",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${reel.reviewsCount}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 4. Bouton Partage WhatsApp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.testTag("reel_share_section_${reel.id}")
        ) {
            val formattedPrice = "${reel.priceAmount.toInt()} FCFA"
            val canonicalUrl = "https://fixo.cm/reels/${reel.id}"

            IconButton(
                onClick = {
                    try {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clip = ClipData.newPlainText("FIXO Reel Link", canonicalUrl)
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(context, "Lien copié : $canonicalUrl", Toast.LENGTH_SHORT).show()
                    } catch (_: Exception) {}

                    val shareTemplate = FixoStrings.getString("reels.share_text_template", language)
                    val shareText = shareTemplate
                        .replace("{workerName}", reel.workerName)
                        .replace("{price}", formattedPrice)
                        .replace("{url}", canonicalUrl)

                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Réparation FIXO - ${reel.workerName}")
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Partager le Reel"))
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .testTag("reel_share_${reel.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Partager",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Partager",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // 5. Contrôle Audio (Mute / Unmute)
        IconButton(
            onClick = onMuteToggle,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .testTag("reel_mute_${reel.id}")
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = if (isMuted) "Activer le son" else "Couper le son",
                tint = if (isMuted) Color.White.copy(alpha = 0.7f) else FixoElectricAmber,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
