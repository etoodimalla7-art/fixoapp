package com.example.ui.screens.customer

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Reel
import com.example.data.model.ServiceItem
import com.example.data.model.VerificationStatus
import com.example.data.model.WorkerProfile
import com.example.ui.components.StarRatingRow
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoBlue700
import com.example.ui.theme.FixoEmerald100
import com.example.ui.theme.FixoEmerald50
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700

import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ThumbUp

@Composable
fun WorkerProfileScreen(
    worker: WorkerProfile,
    services: List<ServiceItem>,
    reels: List<Reel>,
    onBack: () -> Unit,
    onBookService: (ServiceItem) -> Unit,
    onWatchReel: (Reel) -> Unit,
    isSaved: Boolean = false,
    onToggleSave: (() -> Unit)? = null,
    onMessage: () -> Unit = {},
    onViewAllReels: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // App bar
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack, modifier = Modifier.testTag("worker_profile_back")) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Artisan Storefront",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (onToggleSave != null) {
                                IconButton(onClick = onToggleSave, modifier = Modifier.testTag("worker_profile_save")) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = if (isSaved) "Saved" else "Save Artisan",
                                        tint = if (isSaved) FixoAmber600 else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Profile Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = worker.avatarUrl,
                                contentDescription = worker.name,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = worker.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = worker.category.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = FixoBlue600, fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    VerificationBadge(status = if (worker.rating >= 4.95) VerificationStatus.MASTER_CRAFTSMAN else VerificationStatus.VERIFIED_PRO)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    StarRatingRow(rating = worker.rating, reviewCount = worker.reviewCount)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = com.example.data.model.formatFixoCurrency(worker.hourlyRate),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = FixoBlue700)
                                )
                                Text(text = "Hourly Rate", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = FixoSlate500))
                            }
                            Divider(modifier = Modifier.height(30.dp).width(1.dp), color = FixoSlate200)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${worker.completedJobs}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = FixoEmerald600)
                                )
                                Text(text = "Completed Jobs", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = FixoSlate500))
                            }
                            Divider(modifier = Modifier.height(30.dp).width(1.dp), color = FixoSlate200)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${worker.locationDistanceKm} km",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(text = "Distance", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = FixoSlate500))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bio
                        Text(
                            text = "About the Artisan",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = worker.bio,
                            style = MaterialTheme.typography.bodyMedium.copy(color = FixoSlate700, lineHeight = 20.sp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Verified Credentials / Certifications
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(FixoEmerald50)
                                .border(1.dp, FixoEmerald600.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = FixoEmerald600, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Trade Credentials & Background Verified",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = FixoEmerald600)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = worker.certifications,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = FixoSlate700)
                                )
                            }
                        }
                    }
                }
            }

            // Location, Service Area & Availability
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoBlue600, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Location & Service Area", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stationed in ${worker.locationCity} • Service coverage across the metropolitan district (up to 20 km radius).",
                            fontSize = 12.sp,
                            color = FixoSlate700
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = FixoAmber600, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Working Hours & Availability", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Active Days: ${worker.workingDays} • Slots: ${worker.slotIntervals}",
                            fontSize = 12.sp,
                            color = FixoSlate700
                        )
                        if (worker.emergencyCalloutAvailable) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(FixoEmerald600)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Emergency 30-min callout dispatch active", fontSize = 11.sp, color = FixoEmerald600, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Skills Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Specialized Trade Skills",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        worker.skills.split(",").forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = skill.trim(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Portfolio Gallery Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Work Portfolio & Completed Jobs",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val portfolioImages = listOf(
                        "https://images.unsplash.com/photo-1581244277943-fe4a9c777189?w=400",
                        "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=400",
                        "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=400"
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(portfolioImages) { imgUrl ->
                            Box(
                                modifier = Modifier
                                    .size(130.dp, 100.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            ) {
                                AsyncImage(
                                    model = imgUrl,
                                    contentDescription = "Portfolio Work",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            // Services & Escrow Pricing
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Services & Fixed Escrow Pricing",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "All bookings are protected by FIXO Escrow until work completion approval.",
                        style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                    )
                }
            }

            items(services) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = service.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = service.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = com.example.data.model.formatFixoCurrency(service.price),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FixoBlue700
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = FixoSlate500, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "~${service.durationEstimateMinutes} mins",
                                    style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 12.sp)
                                )
                            }

                            Button(
                                onClick = { onBookService(service) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FixoNavy900),
                                modifier = Modifier.testTag("book_service_${service.id}")
                            ) {
                                Text("Book Service", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FixoAmber500)
                            }
                        }
                    }
                }
            }

            // Customer Reviews Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Customer Reviews & Ratings (${worker.reviewCount})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val reviews = listOf(
                        Triple("Jean-Paul N.", "5.0", "Exceptional plumbing craftsmanship. Fixed our master bathroom leak within 45 minutes and clean finishing. Escrow payout was painless!"),
                        Triple("Therese B.", "5.0", "Prompt arrival in Akwa, professional diagnostic and very fair pricing. Highly recommended!")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        reviews.forEach { (reviewer, rating, comment) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(reviewer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = FixoAmber500, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(rating, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(comment, fontSize = 11.sp, color = FixoSlate700, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Craftsmanship Reels by this worker
            val workerReels = reels.filter { it.workerId == worker.id }
            if (workerReels.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "Craftsmanship Reels by ${worker.name.split(" ").first()}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(workerReels) { reel ->
                                Box(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onWatchReel(reel) }
                                ) {
                                    AsyncImage(
                                        model = reel.thumbnailUrl,
                                        contentDescription = reel.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))))
                                    )
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = reel.title,
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sticky Bottom Action Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onMessage,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(0.4f)
                ) {
                    Icon(Icons.Filled.Chat, contentDescription = "Message", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Message", fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val firstService = services.firstOrNull() ?: ServiceItem(
                            id = "srv_std_${worker.id}",
                            workerId = worker.id,
                            name = "${worker.category.displayName} Service",
                            category = worker.category,
                            description = "Professional trade service",
                            price = worker.hourlyRate,
                            durationEstimateMinutes = 60
                        )
                        onBookService(firstService)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoNavy900),
                    modifier = Modifier
                        .weight(0.6f)
                        .testTag("worker_profile_book_button")
                ) {
                    Text("Book Artisan", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FixoAmber500)
                }
            }
        }
    }
}
