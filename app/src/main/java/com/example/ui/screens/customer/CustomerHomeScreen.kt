package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.data.model.Reel
import com.example.data.model.ServiceCategory
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.components.JobStatusBadge
import com.example.ui.components.StarRatingRow
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.FixoAmber100
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue100
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoBlue700
import com.example.ui.theme.FixoEmerald100
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate300
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700

@Composable
fun CustomerHomeScreen(
    workers: List<WorkerProfile>,
    reels: List<Reel>,
    activeBookings: List<Booking>,
    searchQuery: String,
    selectedCategory: ServiceCategory?,
    filterVerifiedOnly: Boolean,
    filterEmergencyOnly: Boolean,
    language: AppLanguage,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (ServiceCategory?) -> Unit,
    onToggleVerified: () -> Unit,
    onToggleEmergency: () -> Unit,
    onWorkerClicked: (WorkerProfile) -> Unit,
    onBookingClicked: (Booking) -> Unit,
    onWatchReelsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeJob = activeBookings.firstOrNull { it.status != JobStatus.COMPLETED && it.status != JobStatus.CANCELLED }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Active Job Banner (High Priority Attention)
        if (activeJob != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(FixoNavy900, FixoBlue700)
                            )
                        )
                        .clickable { onBookingClicked(activeJob) }
                        .padding(16.dp)
                        .testTag("active_job_banner")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(FixoEmerald500)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = FixoStrings.get("active_job", language),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            JobStatusBadge(status = activeJob.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = activeJob.serviceTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Artisan: ${activeJob.workerName} • Escrow Held: $${activeJob.priceAmount}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tap to track live progress & release funds →",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = FixoAmber500,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Hero Search & Emergency Callout
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Emergency Quick Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FixoAmber500.copy(alpha = 0.12f))
                        .border(1.dp, FixoAmber500.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { onToggleEmergency() }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag("emergency_callout_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FixoAmber500),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Emergency",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = FixoStrings.get("emergency_callout", language),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Burst pipes, blackout panels, HVAC failures",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = FixoSlate500
                                    )
                                )
                            }
                        }
                        Text(
                            text = if (filterEmergencyOnly) "Active" else "Dispatch",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (filterEmergencyOnly) FixoEmerald600 else FixoAmber600
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    placeholder = {
                        Text(
                            text = FixoStrings.get("search_hint", language),
                            style = MaterialTheme.typography.bodyMedium.copy(color = FixoSlate500)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = FixoSlate500
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChanged("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoBlue600,
                        unfocusedBorderColor = FixoSlate200
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Quick Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Verified Only Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (filterVerifiedOnly) FixoEmerald100 else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onToggleVerified() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("filter_verified_chip")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = if (filterVerifiedOnly) FixoEmerald600 else FixoSlate500,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = FixoStrings.get("filter_verified", language),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (filterVerifiedOnly) FixoEmerald600 else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    // Escrow Protected Badge Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FixoBlue50)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Escrow",
                                tint = FixoBlue600,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% Escrow Guarantee",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoBlue600
                                )
                            )
                        }
                    }
                }
            }
        }

        // Skilled Trades Category Grid / Row
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FixoStrings.get("top_categories", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (selectedCategory != null) {
                        TextButton(onClick = { onCategorySelected(null) }) {
                            Text("Clear filter", color = FixoBlue600, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(ServiceCategory.values()) { category ->
                        val isSel = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) FixoBlue600 else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    if (isSel) FixoBlue700 else FixoSlate200,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onCategorySelected(if (isSel) null else category)
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .testTag("category_${category.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = when (category) {
                                        ServiceCategory.PLUMBING -> Icons.Default.Plumbing
                                        ServiceCategory.ELECTRICAL -> Icons.Default.ElectricBolt
                                        ServiceCategory.HVAC -> Icons.Default.FlashOn
                                        ServiceCategory.CARPENTRY -> Icons.Default.Handyman
                                        ServiceCategory.MASONRY -> Icons.Default.Build
                                        ServiceCategory.APPLIANCES -> Icons.Outlined.Engineering
                                        ServiceCategory.AUTO_TECH -> Icons.Default.Build
                                        ServiceCategory.IT_NETWORKING -> Icons.Default.Shield
                                    },
                                    contentDescription = category.displayName,
                                    tint = if (isSel) Color.White else FixoBlue600,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = category.displayName.split(" & ").first(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Craftsmanship Reels Strip (Core feature visual hook!)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = FixoStrings.get("watch_reels", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Watch real craftsmanship before booking",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                        )
                    }
                    TextButton(onClick = onWatchReelsClicked) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Open Feed", color = FixoBlue600, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = FixoBlue600, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (reels.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = FixoStrings.get("empty_reels", language),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = FixoSlate500,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(reels) { reel ->
                            Box(
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(230.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onWatchReelsClicked() }
                                    .testTag("reel_card_${reel.id}")
                            ) {
                                AsyncImage(
                                    model = reel.thumbnailUrl,
                                    contentDescription = reel.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                // Gradient Overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                            )
                                        )
                                    )
                                // Play icon badge
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                // Bottom Info
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = reel.workerName,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = reel.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 11.sp
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Top-Rated Artisans List
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = FixoStrings.get("featured_artisans", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${workers.size} Available",
                        style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                    )
                }
            }
        }

        if (workers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("empty_artisans_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = FixoSlate500
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = FixoStrings.get("empty_artisans", language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = FixoSlate700,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                }
            }
        } else {
            items(workers) { worker ->
                WorkerCard(
                    worker = worker,
                    language = language,
                    onWorkerClicked = { onWorkerClicked(worker) }
                )
            }
        }
    }
}

@Composable
fun WorkerCard(
    worker: WorkerProfile,
    language: AppLanguage,
    onWorkerClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onWorkerClicked() }
            .testTag("worker_card_${worker.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Worker Avatar
                AsyncImage(
                    model = worker.avatarUrl,
                    contentDescription = worker.name,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = worker.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${com.example.data.model.formatFixoCurrency(worker.hourlyRate)} ${FixoStrings.get("per_hour", language)}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = FixoBlue700
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        VerificationBadge(status = if (worker.rating >= 4.95) com.example.data.model.VerificationStatus.MASTER_CRAFTSMAN else com.example.data.model.VerificationStatus.VERIFIED_PRO)
                        Spacer(modifier = Modifier.width(6.dp))
                        StarRatingRow(rating = worker.rating, reviewCount = worker.reviewCount)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoSlate500, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = worker.locationCity,
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 11.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = worker.bio,
                style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate700),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Skills Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                worker.skills.split(",").take(2).forEach { skill ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = skill.trim(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                Text(
                    text = "${worker.completedJobs} ${FixoStrings.get("completed_jobs", language)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        color = FixoEmerald600,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}
