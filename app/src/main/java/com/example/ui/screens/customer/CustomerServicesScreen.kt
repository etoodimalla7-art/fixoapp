package com.example.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.FixoSeedData
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoWhite

enum class ServicesDiscoveryTab(val label: String) {
    ALL("All Services"),
    POPULAR("Popular"),
    NEARBY("Nearby"),
    RECOMMENDED("Recommended")
}

enum class SortMode(val label: String) {
    TOP_RATED("Top Rated"),
    PRICE_LOW_HIGH("Price: Low → High"),
    PRICE_HIGH_LOW("Price: High → Low"),
    NEAREST("Nearest")
}

@Composable
fun CustomerServicesScreen(
    allWorkers: List<WorkerProfile>,
    language: AppLanguage,
    onBookService: (ServiceItem, WorkerProfile) -> Unit,
    onWorkerClicked: (WorkerProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ServiceCategory?>(null) }
    var discoveryTab by remember { mutableStateOf(ServicesDiscoveryTab.ALL) }
    var verifiedOnly by remember { mutableStateOf(false) }
    var emergencyOnly by remember { mutableStateOf(false) }
    var sortMode by remember { mutableStateOf(SortMode.TOP_RATED) }
    var maxDistanceKm by remember { mutableStateOf(0.0) } // 0.0 means any
    var minRating by remember { mutableStateOf(0.0) } // 0.0 means any

    // Base seed and worker services
    val baseServices = remember(allWorkers) {
        if (allWorkers.isEmpty()) {
            FixoSeedData.defaultServices
        } else {
            val generated = allWorkers.flatMap { worker ->
                listOf(
                    ServiceItem(
                        id = "srv_std_${worker.id}",
                        workerId = worker.id,
                        name = "${worker.category.displayName} Inspection & Diagnostic",
                        category = worker.category,
                        description = "Full technical diagnostic, issue assessment, and quote with 30-day workmanship guarantee.",
                        price = worker.hourlyRate,
                        durationEstimateMinutes = 45
                    ),
                    ServiceItem(
                        id = "srv_rep_${worker.id}",
                        workerId = worker.id,
                        name = "${worker.category.displayName} Component Overhaul & Maintenance",
                        category = worker.category,
                        description = "Hands-on master craftsmanship repair, replacement parts installation, pressure/safety testing.",
                        price = worker.hourlyRate * 1.8,
                        durationEstimateMinutes = 90
                    )
                )
            }
            FixoSeedData.defaultServices + generated
        }
    }

    val filteredServices = remember(searchQuery, selectedCategory, discoveryTab, verifiedOnly, emergencyOnly, sortMode, maxDistanceKm, minRating, allWorkers) {
        baseServices.filter { service ->
            val worker = allWorkers.find { it.id == service.workerId }
            val matchesQuery = searchQuery.isBlank() ||
                    service.name.contains(searchQuery, ignoreCase = true) ||
                    service.description.contains(searchQuery, ignoreCase = true) ||
                    service.category.displayName.contains(searchQuery, ignoreCase = true) ||
                    (worker?.name?.contains(searchQuery, ignoreCase = true) == true)

            val matchesCategory = selectedCategory == null || service.category == selectedCategory
            val matchesVerified = !verifiedOnly || (worker?.backgroundVerified == true)
            val matchesEmergency = !emergencyOnly || (worker?.emergencyCalloutAvailable == true)
            val matchesDistance = maxDistanceKm <= 0.0 || (worker != null && worker.locationDistanceKm <= maxDistanceKm)
            val matchesRating = minRating <= 0.0 || (worker != null && worker.rating >= minRating)

            val matchesTab = when (discoveryTab) {
                ServicesDiscoveryTab.ALL -> true
                ServicesDiscoveryTab.POPULAR -> service.price <= 35000.0
                ServicesDiscoveryTab.NEARBY -> (worker?.locationDistanceKm ?: 99.0) <= 3.0
                ServicesDiscoveryTab.RECOMMENDED -> (worker?.rating ?: 0.0) >= 4.95
            }

            matchesQuery && matchesCategory && matchesVerified && matchesEmergency && matchesDistance && matchesRating && matchesTab
        }.let { list ->
            when (sortMode) {
                SortMode.TOP_RATED -> list.sortedByDescending { s -> allWorkers.find { it.id == s.workerId }?.rating ?: 0.0 }
                SortMode.PRICE_LOW_HIGH -> list.sortedBy { it.price }
                SortMode.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
                SortMode.NEAREST -> list.sortedBy { s -> allWorkers.find { it.id == s.workerId }?.locationDistanceKm ?: 99.0 }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_services_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. HEADER
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Catalogue des Services" else "Certified Services Catalog",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Prix transparents, devis certifiés et paiement sous séquestre sécurisé"
                        else
                            "Transparent pricing, vetted artisans, and 100% Escrow-protected checkout",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("services_search_input"),
                        placeholder = {
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "Rechercher un service (fuite, disjoncteur, clim...)"
                                else
                                    "Search services (leak repair, AC recharge, wiring...)",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // 1.5 DISCOVERY SECTIONS TABS (All, Popular, Nearby, Recommended)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServicesDiscoveryTab.values().forEach { tab ->
                    val isSelected = discoveryTab == tab
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { discoveryTab = tab }
                    ) {
                        Text(
                            text = tab.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // 2. CATEGORY CHIPS
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        shape = RoundedCornerShape(24.dp),
                        label = { Text("All Trades", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                items(ServiceCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                        shape = RoundedCornerShape(24.dp),
                        label = { Text(category.displayName.substringBefore(" & "), fontSize = 12.sp) }
                    )
                }
            }
        }

        // 3. SECONDARY MULTI-DIMENSIONAL FILTER ROW
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sorting chip cycle
                FilterChip(
                    selected = true,
                    onClick = {
                        sortMode = when (sortMode) {
                            SortMode.TOP_RATED -> SortMode.PRICE_LOW_HIGH
                            SortMode.PRICE_LOW_HIGH -> SortMode.PRICE_HIGH_LOW
                            SortMode.PRICE_HIGH_LOW -> SortMode.NEAREST
                            SortMode.NEAREST -> SortMode.TOP_RATED
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    label = { Text(sortMode.label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FixoNavy900,
                        selectedLabelColor = FixoGold500
                    )
                )

                FilterChip(
                    selected = verifiedOnly,
                    onClick = { verifiedOnly = !verifiedOnly },
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = if (verifiedOnly) MaterialTheme.colorScheme.primary else FixoEmerald500
                        )
                    },
                    label = { Text("Verified Pros Only", fontSize = 11.sp) }
                )

                FilterChip(
                    selected = emergencyOnly,
                    onClick = { emergencyOnly = !emergencyOnly },
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = FixoGold500
                        )
                    },
                    label = { Text("30-Min Callout", fontSize = 11.sp) }
                )

                FilterChip(
                    selected = maxDistanceKm > 0.0,
                    onClick = {
                        maxDistanceKm = when (maxDistanceKm) {
                            0.0 -> 3.0
                            3.0 -> 5.0
                            else -> 0.0
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(13.dp), tint = FixoGold600)
                    },
                    label = {
                        Text(if (maxDistanceKm > 0.0) "< ${maxDistanceKm.toInt()} km" else "Distance: Any", fontSize = 11.sp)
                    }
                )

                FilterChip(
                    selected = minRating > 0.0,
                    onClick = {
                        minRating = if (minRating == 0.0) 4.8 else 0.0
                    },
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(13.dp), tint = FixoGold500)
                    },
                    label = {
                        Text(if (minRating > 0.0) "4.8★+" else "Rating: Any", fontSize = 11.sp)
                    }
                )
            }
        }

        // 4. RESULTS COUNT
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredServices.size} services available",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Douala & Yaoundé Region",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 5. SERVICES LIST
        if (filteredServices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No services matched your query",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = {
                            searchQuery = ""
                            selectedCategory = null
                            verifiedOnly = false
                            emergencyOnly = false
                        }) {
                            Text("Reset Search")
                        }
                    }
                }
            }
        } else {
            items(filteredServices) { service ->
                val worker = allWorkers.find { it.id == service.workerId }
                    ?: allWorkers.firstOrNull()
                    ?: FixoSeedData.defaultWorkers.first()

                ServiceCatalogCard(
                    service = service,
                    worker = worker,
                    language = language,
                    onBook = { onBookService(service, worker) },
                    onViewArtisan = { onWorkerClicked(worker) }
                )
            }
        }
    }
}

@Composable
fun ServiceCatalogCard(
    service: ServiceItem,
    worker: WorkerProfile,
    language: AppLanguage,
    onBook: () -> Unit,
    onViewArtisan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("service_card_${service.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Category & duration pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = service.category.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "~${service.durationEstimateMinutes} mins",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Service Title & Description
            Text(
                text = service.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = service.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(10.dp))

            // Artisan mini row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewArtisan() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = worker.avatarUrl,
                        contentDescription = worker.name,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.dp, FixoGold500, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = worker.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (worker.backgroundVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = FixoEmerald500,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Text(
                            text = "${worker.locationCity} • ★ %.1f".format(worker.rating),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Escrow Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Escrow",
                        tint = FixoGold600,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Escrow Held",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold600
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing & Book CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Fixed Estimate",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${service.price.toInt()} FCFA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewArtisan,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Artisan", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onBook,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "Réserver Service" else "Book Service",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
