package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextSecondary

data class NavItem(
    val labelKey: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun FixoBottomNav(
    currentRole: UserRole,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    language: AppLanguage = AppLanguage.FR,
    modifier: Modifier = Modifier
) {
    val items = when (currentRole) {
        UserRole.CUSTOMER -> listOf(
            NavItem("home", Icons.Outlined.Home, Icons.Filled.Home, "nav_customer_home"),
            NavItem("nav_explore", Icons.Outlined.Explore, Icons.Filled.Explore, "nav_customer_explore"),
            NavItem("nav_reels", Icons.Outlined.PlayCircle, Icons.Filled.PlayCircle, "nav_customer_reels"),
            NavItem("nav_jobs", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Filled.Assignment, "nav_customer_jobs"),
            NavItem("nav_account", Icons.Outlined.Person, Icons.Filled.Person, "nav_customer_account")
        )
        UserRole.WORKER -> listOf(
            NavItem("pro_nav_cockpit", Icons.Outlined.Speed, Icons.Filled.Speed, "nav_worker_cockpit"),
            NavItem("pro_nav_schedule", Icons.Outlined.DateRange, Icons.Filled.DateRange, "nav_worker_schedule"),
            NavItem("pro_nav_reels", Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary, "nav_worker_reels"),
            NavItem("pro_nav_jobs", Icons.Outlined.Work, Icons.Filled.Work, "nav_worker_jobs"),
            NavItem("pro_nav_profile", Icons.Outlined.Person, Icons.Filled.Person, "nav_worker_profile")
        )
        UserRole.ENTERPRISE -> listOf(
            NavItem("enterprise_portal", Icons.Outlined.Business, Icons.Filled.Business, "nav_enterprise_projects"),
            NavItem("jobs", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Filled.Assignment, "nav_enterprise_invoices")
        )
        UserRole.ADMIN -> listOf(
            NavItem("admin_portal", Icons.Outlined.Security, Icons.Filled.Security, "nav_admin_overview"),
            NavItem("verified_pro", Icons.Outlined.VerifiedUser, Icons.Filled.VerifiedUser, "nav_admin_verify"),
            NavItem("disputes", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Filled.Assignment, "nav_admin_disputes")
        )
    }

    NavigationBar(
        modifier = modifier
            .height(64.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = FixoSurfaceCard,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedTabIndex == index
            val labelText = FixoStrings.get(item.labelKey, language)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.icon,
                            contentDescription = labelText,
                            tint = if (isSelected) FixoGold500 else FixoTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(3.dp))
                            // 3px glowing amber gold dot
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(FixoGold500)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(3.dp))
                        }
                    }
                },
                label = {
                    Text(
                        text = labelText,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) FixoGold500 else FixoTextSecondary,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FixoGold500,
                    selectedTextColor = FixoGold500,
                    indicatorColor = Color.Transparent, // No bulky pill indicator
                    unselectedIconColor = FixoTextSecondary,
                    unselectedTextColor = FixoTextSecondary
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

