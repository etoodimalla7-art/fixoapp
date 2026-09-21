package com.example.ui.screens.reels

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Reel
import com.example.data.model.ServiceCategory
import com.example.data.model.WorkerProfile
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate500

@Composable
fun ReelsFeedScreen(
    reels: List<Reel>,
    allWorkers: List<WorkerProfile>,
    onLikeReel: (String) -> Unit,
    onBookFromReel: (Reel, WorkerProfile) -> Unit,
    onOpenWorkerProfile: (WorkerProfile) -> Unit,
    onReportReel: (Reel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf<ServiceCategory?>(null) }

    val filteredReels = if (selectedCategory == null) reels else reels.filter { it.category == selectedCategory }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (filteredReels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = FixoSlate500,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No craftsmanship reels published yet in this category.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredReels) { reel ->
                    val worker = allWorkers.find { it.id == reel.workerId }
                        ?: allWorkers.firstOrNull()
                        ?: WorkerProfile(
                            id = reel.workerId,
                            userId = reel.workerId,
                            name = reel.workerName,
                            category = reel.category,
                            hourlyRate = 7500.0,
                            emergencyCalloutAvailable = true,
                            bio = reel.description,
                            skills = reel.tags,
                            certifications = "Guild Verified Craftsman",
                            completedJobs = 0,
                            rating = 5.0,
                            reviewCount = 0,
                            avatarUrl = reel.workerAvatar,
                            locationCity = "Douala",
                            locationDistanceKm = 1.0,
                            backgroundVerified = true,
                            phone = "+237 670 000 000"
                        )
                    var isLiked by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillParentMaxHeight()
                            .fillMaxWidth()
                            .testTag("reel_viewport_${reel.id}")
                    ) {
                        // Background Video Snapshot
                        AsyncImage(
                            model = reel.thumbnailUrl,
                            contentDescription = reel.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Dark Gradient for legibility
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.4f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        // Top Category Filter Pills
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, start = 12.dp, end = 12.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (selectedCategory == null) FixoBlue600 else Color.Black.copy(alpha = 0.5f))
                                        .clickable { selectedCategory = null }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("All Trades", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            items(ServiceCategory.values()) { cat ->
                                val isSel = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSel) FixoBlue600 else Color.Black.copy(alpha = 0.5f))
                                        .clickable { selectedCategory = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        cat.displayName.split(" & ").first(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Right-side Engagement Bar
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp, bottom = 90.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Artisan Avatar Clickable
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .clickable { onOpenWorkerProfile(worker) }
                            ) {
                                AsyncImage(
                                    model = reel.workerAvatar,
                                    contentDescription = reel.workerName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Like Button
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = {
                                        isLiked = !isLiked
                                        onLikeReel(reel.id)
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .testTag("reel_like_${reel.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (isLiked) FixoRed500 else Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = "${reel.likesCount + if (isLiked) 1 else 0}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Share Button (Native Android Share Intent + Clipboard)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = {
                                        val canonicalUrl = "https://fixo.cm/reels/${reel.id}"
                                        try {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("FIXO Reel Link", canonicalUrl)
                                            clipboard?.setPrimaryClip(clip)
                                            android.widget.Toast.makeText(context, "Canonical Reel Link copied: $canonicalUrl", android.widget.Toast.LENGTH_SHORT).show()
                                        } catch (_: Exception) {}

                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_SUBJECT,
                                                "Master Craftsmanship by ${reel.workerName} on FIXO"
                                            )
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Check out this verified craftsmanship reel: '${reel.title}' on FIXO!\n$canonicalUrl"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Reel"))
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.4f))
                                        .testTag("reel_share_${reel.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text("Share", color = Color.White, fontSize = 11.sp)
                            }

                            // Report / Flag
                            IconButton(
                                onClick = { onReportReel(reel) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.4f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Report",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Bottom Overlay: Title, Description, Artisan, Direct Booking CTA
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp, end = 70.dp, bottom = 85.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onOpenWorkerProfile(worker) }
                            ) {
                                Text(
                                    text = reel.workerName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = FixoAmber500,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = reel.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = reel.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    lineHeight = 16.sp
                                ),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Trade and Tags
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                reel.tags.split(",").take(2).forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "#${tag.trim()}",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Direct Booking CTA Button
                            Button(
                                onClick = { onBookFromReel(reel, worker) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("book_from_reel_${reel.id}")
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Book ${worker.name.split(" ").first()} for this Job",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
