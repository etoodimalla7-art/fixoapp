package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.EscrowStatus
import com.example.data.model.JobStatus
import com.example.data.model.SubscriptionTier
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate300
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700

@Composable
fun FixoTopBar(
    currentRole: UserRole,
    currentLanguage: AppLanguage,
    currentUser: User?,
    onRoleSelected: (UserRole) -> Unit,
    onLanguageToggle: () -> Unit,
    onWalletClick: () -> Unit,
    onLogout: () -> Unit = {},
    isDevEnvironment: Boolean = false,
    onToggleEnvironment: () -> Unit = {},
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { roleMenuExpanded = true }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fixo_logo),
                        contentDescription = "FIXO Brand Mark",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FIXO",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(FixoBlue600.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = when (currentRole) {
                                        UserRole.CUSTOMER -> "CLIENT"
                                        UserRole.WORKER -> "ARTISAN"
                                        UserRole.ENTERPRISE -> "ENTERPRISE"
                                        UserRole.ADMIN -> "ADMIN"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FixoBlue600
                                    )
                                )
                            }
                        }
                        Text(
                            text = FixoStrings.get("app_tagline", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = FixoSlate500
                            ),
                            maxLines = 1
                        )
                    }
                }

                // Top Right Action Buttons: Role Switcher, Language Toggle, Wallet Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onLanguageToggle() }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                modifier = Modifier.size(15.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLanguage.name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Role Switcher Pill
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FixoBlue50)
                                .border(1.dp, FixoBlue600.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable { roleMenuExpanded = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("role_switcher_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Switch Role",
                                    modifier = Modifier.size(16.dp),
                                    tint = FixoBlue600
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (currentRole) {
                                        UserRole.CUSTOMER -> "Client"
                                        UserRole.WORKER -> "Artisan"
                                        UserRole.ENTERPRISE -> "Corporate"
                                        UserRole.ADMIN -> "Admin"
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = FixoBlue600
                                    )
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = roleMenuExpanded,
                            onDismissRequest = { roleMenuExpanded = false }
                        ) {
                            UserRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = when (role) {
                                                    UserRole.CUSTOMER -> "Customer View"
                                                    UserRole.WORKER -> "Artisan / Technician View"
                                                    UserRole.ENTERPRISE -> "Enterprise Bulk Workforce"
                                                    UserRole.ADMIN -> "Admin Operations HQ"
                                                },
                                                fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal,
                                                color = if (role == currentRole) FixoBlue600 else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = when (role) {
                                                    UserRole.CUSTOMER -> "Discover artisans, watch reels, book & track"
                                                    UserRole.WORKER -> "Manage slots, orders, studio reels & payout"
                                                    UserRole.ENTERPRISE -> "Post commercial multi-worker projects"
                                                    UserRole.ADMIN -> "Verify artisans, dispute center, platform metrics"
                                                },
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = FixoSlate500)
                                            )
                                        }
                                    },
                                    onClick = {
                                        onRoleSelected(role)
                                        roleMenuExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = if (isDevEnvironment) "Environment: Sandbox Test" else "Environment: Clean Production",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDevEnvironment) FixoAmber600 else FixoEmerald600
                                        )
                                        Text(
                                            text = if (isDevEnvironment) "Tap to clear and switch to Clean Live Mode" else "Tap to load Cameroon Sandbox Test Data",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = FixoSlate500)
                                        )
                                    }
                                },
                                onClick = {
                                    roleMenuExpanded = false
                                    onToggleEnvironment()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Sign Out",
                                        color = FixoRed500,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                onClick = {
                                    roleMenuExpanded = false
                                    onLogout()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Environment Pill (Live vs Sandbox)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDevEnvironment) FixoAmber500.copy(alpha = 0.18f) else FixoEmerald500.copy(alpha = 0.18f))
                            .clickable { onToggleEnvironment() }
                            .padding(horizontal = 6.dp, vertical = 5.dp)
                            .testTag("env_toggle_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDevEnvironment) "SANDBOX" else "LIVE PROD",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = if (isDevEnvironment) FixoAmber600 else FixoEmerald600
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Notifications Bell
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onNotificationsClick() }
                            .padding(horizontal = 7.dp, vertical = 6.dp)
                            .testTag("notifications_topbar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            if (unreadNotificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(FixoRed500)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Wallet Balance
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onWalletClick() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("wallet_topbar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                modifier = Modifier.size(15.dp),
                                tint = FixoEmerald600
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentUser?.balance?.toInt() ?: 0} FCFA",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
