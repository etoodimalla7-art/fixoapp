package com.example.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.data.model.User
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold100
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoNeutral100
import com.example.ui.theme.FixoNeutral200
import com.example.ui.theme.FixoNeutral400
import com.example.ui.theme.FixoNeutral500
import com.example.ui.theme.FixoNeutral600
import com.example.ui.theme.FixoNeutral700
import com.example.ui.theme.FixoNeutral900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoWhite

@Composable
fun ProfileScreen(
    user: User?,
    customerBookings: List<Booking>,
    savedWorkers: List<WorkerProfile>,
    language: AppLanguage,
    onUpdateProfile: (name: String, email: String, phone: String, city: String, avatarUrl: String) -> Unit,
    onToggleLanguage: () -> Unit,
    onNavigateToJobs: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onWorkerClicked: (WorkerProfile) -> Unit,
    onOpenDispute: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPointsExplanationDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteAccountConfirm by remember { mutableStateOf(false) }

    // User preferences state
    var pushNotificationsEnabled by remember { mutableStateOf(true) }
    var smsUpdatesEnabled by remember { mutableStateOf(true) }

    val activeCount = customerBookings.count { it.status != JobStatus.COMPLETED && it.status != JobStatus.CANCELLED }
    val completedCount = customerBookings.count { it.status == JobStatus.COMPLETED }
    val cancelledCount = customerBookings.count { it.status == JobStatus.CANCELLED }

    val pointsBalance = (user?.balance?.times(0.3)?.toInt() ?: 1250).coerceAtLeast(350)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. PROFILE HEADER CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AsyncImage(
                                model = user?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400",
                                contentDescription = "Profile Avatar",
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, FixoGold500, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(FixoGold500)
                                    .clickable { showEditProfileDialog = true }
                                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit photo",
                                    tint = FixoNavy900,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user?.name ?: "Valued Customer",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Profile",
                                    tint = FixoEmerald500,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = user?.email ?: "customer@fixo.cm",
                                fontSize = 13.sp,
                                color = FixoNeutral500
                            )
                            Text(
                                text = user?.phone ?: "+237 670 112 233",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = FixoNeutral600
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = FixoGold500,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Bonapriso, Douala • Cameroon",
                                    fontSize = 12.sp,
                                    color = FixoNeutral500
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("edit_profile_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FixoNavy900,
                            contentColor = FixoWhite
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Profile & Personal Details", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // 2. MY ACTIVITY METRICS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "Mon Activité" else "My Activity",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = onNavigateToJobs) {
                            Text(
                                text = if (language == AppLanguage.FR) "Voir les réservations" else "View All Bookings",
                                fontSize = 12.sp,
                                color = FixoGold600,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActivityStatBox(
                            title = if (language == AppLanguage.FR) "En cours" else "Active Jobs",
                            count = activeCount.toString(),
                            accentColor = FixoGold600,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToJobs() }
                        )
                        ActivityStatBox(
                            title = if (language == AppLanguage.FR) "Terminés" else "Completed",
                            count = completedCount.toString(),
                            accentColor = FixoEmerald500,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToJobs() }
                        )
                        ActivityStatBox(
                            title = if (language == AppLanguage.FR) "Annulés" else "Cancelled",
                            count = cancelledCount.toString(),
                            accentColor = FixoRed500,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. FIXO POINTS & LOYALTY CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fixo_points_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FixoNavy900)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                    .background(FixoGold500),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = FixoNavy900,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "FIXO Rewards & Points",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                                Text(
                                    text = "Tier: Gold Client • 30% back on jobs",
                                    fontSize = 11.sp,
                                    color = FixoGold100
                                )
                            }
                        }

                        TextButton(onClick = { showPointsExplanationDialog = true }) {
                            Text(
                                text = "How it works",
                                fontSize = 12.sp,
                                color = FixoGold500,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Current Points Balance",
                                fontSize = 12.sp,
                                color = FixoNeutral400
                            )
                            Text(
                                text = "$pointsBalance PTS",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FixoGold500
                            )
                            Text(
                                text = "Equivalent to $pointsBalance XAF discount",
                                fontSize = 11.sp,
                                color = FixoNeutral400
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToWallet,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoGold500)
                        ) {
                            Text("Redeem in Wallet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. SAVED PROFESSIONALS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = FixoGold600,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Artisans Favoris" else "Saved Professionals",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${savedWorkers.size} saved",
                            fontSize = 12.sp,
                            color = FixoNeutral500
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (savedWorkers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "Aucun artisan enregistré. Explorez pour enregistrer vos professionnels favoris."
                                else
                                    "No saved artisans yet. Save top plumbers, electricians and mechanics for quick booking.",
                                fontSize = 13.sp,
                                color = FixoNeutral500,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(savedWorkers) { worker ->
                                Card(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .clickable { onWorkerClicked(worker) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AsyncImage(
                                            model = worker.avatarUrl,
                                            contentDescription = worker.name,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = worker.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = worker.category.name,
                                            fontSize = 11.sp,
                                            color = FixoNeutral500
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${worker.hourlyRate.toInt()} XAF/hr",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FixoGold600
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. WALLET & ESCROW QUICK ACCESS
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToWallet() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FixoNavy900),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalanceWallet,
                                contentDescription = null,
                                tint = FixoGold500,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.FR) "Portefeuille FIXO & Dépôt Tiers" else "FIXO Wallet & Escrow",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Balance: ${(user?.balance ?: 45000.0).toInt()} XAF • Escrow: ${(user?.escrowLocked ?: 18000.0).toInt()} XAF",
                                fontSize = 12.sp,
                                color = FixoNeutral500
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Open Wallet",
                        tint = FixoNeutral400,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 6. PREFERENCES & SETTINGS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.FR) "Paramètres & Préférences" else "Settings & Preferences",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Language Switcher
                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = if (language == AppLanguage.FR) "Langue (Language)" else "Language (Langue)",
                        subtitle = if (language == AppLanguage.FR) "Français (Actuel)" else "English (Current)",
                        trailing = {
                            Button(
                                onClick = onToggleLanguage,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FixoNavy900,
                                    contentColor = FixoWhite
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (language == AppLanguage.FR) "Switch to EN" else "Passer en FR",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = FixoNeutral200)

                    // Push Notifications
                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        title = if (language == AppLanguage.FR) "Notifications instantanées" else "Push Notifications",
                        subtitle = if (language == AppLanguage.FR) "Statut du travail et alertes d'artisan" else "Job updates, messages & arrival alerts",
                        trailing = {
                            Switch(
                                checked = pushNotificationsEnabled,
                                onCheckedChange = { pushNotificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = FixoWhite,
                                    checkedTrackColor = FixoNavy900
                                )
                            )
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = FixoNeutral200)

                    // SMS Updates (Cameroon specific)
                    SettingsRow(
                        icon = Icons.Default.Phone,
                        title = "SMS Offline Alerts",
                        subtitle = "Receive booking confirmation via MTN/Orange SMS",
                        trailing = {
                            Switch(
                                checked = smsUpdatesEnabled,
                                onCheckedChange = { smsUpdatesEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = FixoWhite,
                                    checkedTrackColor = FixoNavy900
                                )
                            )
                        }
                    )
                }
            }
        }

        // 7. SUPPORT & SAFETY
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.FR) "Support & Sécurité" else "Support & Safety",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ActionRow(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = if (language == AppLanguage.FR) "Centre d'Aide & FAQ" else "Help Center & FAQ",
                        subtitle = "Escrow protection, pricing, artisan guarantees",
                        onClick = { showFaqDialog = true }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = FixoNeutral200)

                    ActionRow(
                        icon = Icons.Default.SupportAgent,
                        title = if (language == AppLanguage.FR) "Contacter le Service Client" else "Contact FIXO Support",
                        subtitle = "Direct WhatsApp, Phone (+237) & Email support",
                        onClick = { showSupportDialog = true }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = FixoNeutral200)

                    ActionRow(
                        icon = Icons.Default.ReportProblem,
                        title = if (language == AppLanguage.FR) "Signaler un Problème / Litige" else "Report a Problem / Dispute",
                        subtitle = "Escrow freeze & fast human dispute arbitration",
                        onClick = onOpenDispute
                    )
                }
            }
        }

        // 8. ABOUT FIXO & LEGAL
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == AppLanguage.FR) "À Propos de FIXO" else "About FIXO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ActionRow(
                        icon = Icons.Default.Info,
                        title = "About FIXO Platform",
                        subtitle = "Version 2.0.0 Production • Cameroon Skilled Trades",
                        onClick = { showAboutDialog = true }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = FixoNeutral200)

                    ActionRow(
                        icon = Icons.Default.Policy,
                        title = if (language == AppLanguage.FR) "Conditions d'Utilisation" else "Terms of Service",
                        subtitle = "Escrow terms, artisan code of conduct",
                        onClick = { showTermsDialog = true }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = FixoNeutral200)

                    ActionRow(
                        icon = Icons.Default.Security,
                        title = if (language == AppLanguage.FR) "Politique de Confidentialité" else "Privacy Policy",
                        subtitle = "Data protection & payment confidentiality",
                        onClick = { showPrivacyDialog = true }
                    )
                }
            }
        }

        // 9. ACCOUNT ACTIONS (LOGOUT & DELETE)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("profile_logout_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FixoNeutral400),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Se Déconnecter" else "Log Out",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = { showDeleteAccountConfirm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_delete_account_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = FixoRed500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Supprimer mon compte FIXO" else "Delete FIXO Account",
                            color = FixoRed500,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // DIALOGS & MODALS
    // =========================================================================

    // 1. EDIT PROFILE DIALOG
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(user?.name ?: "") }
        var editEmail by remember { mutableStateOf(user?.email ?: "") }
        var editPhone by remember { mutableStateOf(user?.phone ?: "+237 670 112 233") }
        var editCity by remember { mutableStateOf("Douala, Littoral") }
        var editAvatarUrl by remember { mutableStateOf(user?.avatarUrl ?: "") }

        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Edit Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Keep your contact info up-to-date for smooth artisan visits.",
                        fontSize = 12.sp,
                        color = FixoNeutral500
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone (MTN / Orange)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editCity,
                        onValueChange = { editCity = it },
                        label = { Text("City & Neighborhood") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editAvatarUrl,
                        onValueChange = { editAvatarUrl = it },
                        label = { Text("Avatar Photo URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showEditProfileDialog = false }) {
                            Text("Cancel", color = FixoNeutral600)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onUpdateProfile(editName, editEmail, editPhone, editCity, editAvatarUrl)
                                showEditProfileDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FixoNavy900,
                                contentColor = FixoWhite
                            )
                        ) {
                            Text("Save Changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // 2. HOW POINTS WORK DIALOG
    if (showPointsExplanationDialog) {
        Dialog(onDismissRequest = { showPointsExplanationDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FixoGold500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = FixoNavy900)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "How FIXO Points Work",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "1. Earn 30% Points Back\nEvery completed and confirmed job credits 30% of the service value as reward points to your account.",
                        fontSize = 13.sp,
                        color = FixoNeutral700,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "2. Review Bonus\nLeave a genuine rating and photos of the completed work to earn +100 bonus points.",
                        fontSize = 13.sp,
                        color = FixoNeutral700,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "3. 1 Point = 1 XAF\nRedeem your points anytime as a discount on your next booking or convert them to balance in your FIXO Wallet.",
                        fontSize = 13.sp,
                        color = FixoNeutral700,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { showPointsExplanationDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoNavy900)
                    ) {
                        Text("Understood", color = FixoWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 3. ABOUT FIXO DIALOG (WITH OFFICIAL LOGO)
    if (showAboutDialog) {
        Dialog(onDismissRequest = { showAboutDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Surface(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        color = Color.White,
                        shadowElevation = 6.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.fixo_logo),
                                contentDescription = "Official FIXO Logo",
                                modifier = Modifier
                                    .size(70.dp)
                                    .padding(2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FIXO",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(FixoGold500, shape = CircleShape)
                        )
                    }
                    Text(
                        text = "Trusted Skilled Trades Marketplace",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = FixoGold600
                    )
                    Text(
                        text = "Version 2.0.0 Production Release",
                        fontSize = 11.sp,
                        color = FixoNeutral500
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "FIXO connects homeowners and businesses in Cameroon with verified, background-checked plumbers, electricians, carpenters, AC technicians, and mechanics. Powered by guaranteed Escrow payments and video reels.",
                        fontSize = 13.sp,
                        color = FixoNeutral600,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "HQ: Douala, Littoral Region, Cameroon\nWeb: https://fixo.cm\nSupport: support@fixo.cm",
                        fontSize = 12.sp,
                        color = FixoNeutral500,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showAboutDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoNavy900)
                    ) {
                        Text("Close", color = FixoWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 4. SUPPORT DIALOG
    if (showSupportDialog) {
        Dialog(onDismissRequest = { showSupportDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Contact Support",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Our support team in Douala & Yaoundé is available 24/7.",
                        fontSize = 12.sp,
                        color = FixoNeutral500
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ContactOptionRow(
                        title = "WhatsApp Support",
                        detail = "+237 670 000 001",
                        badge = "Fastest response (< 5 min)"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ContactOptionRow(
                        title = "Direct Hotline (Cameroon)",
                        detail = "+237 233 42 00 00",
                        badge = "Emergency calls"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ContactOptionRow(
                        title = "Email Helpdesk",
                        detail = "support@fixo.cm",
                        badge = "Replies in 2 hours"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showSupportDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoNavy900)
                    ) {
                        Text("Close", color = FixoWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 5. FAQ DIALOG
    if (showFaqDialog) {
        Dialog(onDismissRequest = { showFaqDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(androidx.compose.foundation.rememberScrollState())
                ) {
                    Text(
                        text = "Frequently Asked Questions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FaqItem(
                        question = "How does FIXO Escrow protect my money?",
                        answer = "When you book a service, your payment is held safely in FIXO Escrow. The artisan only gets paid after the job is completed and you approve the release in the app."
                    )

                    FaqItem(
                        question = "Which payment methods are accepted?",
                        answer = "We support MTN Mobile Money (MoMo), Orange Money, FIXO In-App Wallet, and Cash on Completion."
                    )

                    FaqItem(
                        question = "What if the work is unsatisfactory?",
                        answer = "You can tap 'Report Problem' or open a dispute. Escrow funds remain frozen while a FIXO mediator inspects photos and coordinates resolution or a refund."
                    )

                    FaqItem(
                        question = "Are artisans background checked?",
                        answer = "Yes. Every verified artisan undergoes national CNI identity verification, trade certificate authentication, and criminal record check before receiving the Verified Pro badge."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showFaqDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoNavy900)
                    ) {
                        Text("Done", color = FixoWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 6. TERMS OF SERVICE DIALOG
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "1. Acceptance of Terms: By accessing FIXO services, you agree to comply with platform standards.\n2. Escrow Protection: All non-cash payments are held in escrow until satisfactory work completion.\n3. Dispute Resolution: Users agree to binding mediation through FIXO compliance team prior to third-party arbitration.\n4. Artisan Responsibility: Independent contractors maintain responsibility for workmanship standards.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("I Understand", fontWeight = FontWeight.Bold, color = FixoNavy900)
                }
            }
        )
    }

    // 7. PRIVACY POLICY DIALOG
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "FIXO respects user privacy in accordance with CEMAC telecommunications regulations. We collect phone numbers, email, and service coordinates solely to connect clients with licensed professionals. We never sell personal data to external advertisers. Payment transactions are encrypted end-to-end.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", fontWeight = FontWeight.Bold, color = FixoNavy900)
                }
            }
        )
    }

    // 8. LOGOUT CONFIRMATION DIALOG
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log Out of FIXO?") },
            text = { Text("Are you sure you want to log out? Your bookings and wallet balance will remain securely stored.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoRed500)
                ) {
                    Text("Log Out", color = FixoWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 9. DELETE ACCOUNT CONFIRMATION DIALOG
    if (showDeleteAccountConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirm = false },
            title = { Text("Delete FIXO Account?", color = FixoRed500, fontWeight = FontWeight.Bold) },
            text = {
                Text("This action is permanent and cannot be undone. All active bookings, saved artisans, and loyalty points will be cleared.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountConfirm = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoRed500)
                ) {
                    Text("Permanently Delete", color = FixoWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// =============================================================================
// SUB-COMPONENTS
// =============================================================================

@Composable
private fun ActivityStatBox(
    title: String,
    count: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = FixoNeutral600,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FixoNavy900,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = FixoNeutral500
                )
            }
        }
        trailing()
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FixoNavy900,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = FixoNeutral500
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = FixoNeutral400,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun ContactOptionRow(
    title: String,
    detail: String,
    badge: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = detail, fontSize = 12.sp, color = FixoGold600, fontWeight = FontWeight.SemiBold)
            }
            Text(
                text = badge,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = FixoEmerald500
            )
        }
    }
}

@Composable
private fun FaqItem(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { expanded = !expanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = FixoNavy900,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = answer,
                    fontSize = 12.sp,
                    color = FixoNeutral600,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
