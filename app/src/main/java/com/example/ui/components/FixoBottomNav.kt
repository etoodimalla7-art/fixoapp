package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Security
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoNeutral400
import com.example.ui.theme.FixoNeutral500

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun FixoBottomNav(
    currentRole: UserRole,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = when (currentRole) {
        UserRole.CUSTOMER -> listOf(
            NavItem("Home", Icons.Outlined.Home, Icons.Filled.Home, "nav_customer_home"),
            NavItem("Services", Icons.Outlined.Build, Icons.Filled.Build, "nav_customer_services"),
            NavItem("Activity", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Filled.Assignment, "nav_customer_activity"),
            NavItem("Reels", Icons.Outlined.PlayCircle, Icons.Filled.PlayCircle, "nav_customer_reels"),
            NavItem("Account", Icons.Outlined.Person, Icons.Filled.Person, "nav_customer_account")
        )
        UserRole.WORKER -> listOf(
            NavItem("Dashboard", Icons.Outlined.Home, Icons.Filled.Home, "nav_worker_dash"),
            NavItem("Schedule", Icons.Outlined.DateRange, Icons.Filled.DateRange, "nav_worker_schedule"),
            NavItem("Reels Studio", Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary, "nav_worker_reels"),
            NavItem("Active Jobs", Icons.Outlined.Work, Icons.Filled.Work, "nav_worker_jobs"),
            NavItem("Profile", Icons.Outlined.Person, Icons.Filled.Person, "nav_worker_profile")
        )
        UserRole.ENTERPRISE -> listOf(
            NavItem("Workforce", Icons.Outlined.Business, Icons.Filled.Business, "nav_enterprise_projects"),
            NavItem("Invoicing", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Filled.Assignment, "nav_enterprise_invoices")
        )
        UserRole.ADMIN -> listOf(
            NavItem("Overview", Icons.Outlined.Security, Icons.Filled.Security, "nav_admin_overview"),
            NavItem("Verification", Icons.Outlined.VerifiedUser, Icons.Filled.VerifiedUser, "nav_admin_verify"),
            NavItem("Disputes", Icons.AutoMirrored.Outlined.Assignment, Icons.AutoMirrored.Filled.Assignment, "nav_admin_disputes")
        )
    }

    NavigationBar(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedTabIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
