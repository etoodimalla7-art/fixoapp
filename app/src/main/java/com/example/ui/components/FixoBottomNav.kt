package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoSlate500

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
            NavItem("Explore", Icons.Outlined.Search, Icons.Filled.Search, "nav_customer_explore"),
            NavItem("Reels", Icons.Outlined.PlayCircle, Icons.Filled.PlayCircle, "nav_customer_reels"),
            NavItem("Jobs", Icons.Outlined.Work, Icons.Filled.Work, "nav_customer_jobs"),
            NavItem("Wallet", Icons.Outlined.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_customer_wallet")
        )
        UserRole.WORKER -> listOf(
            NavItem("Dashboard", Icons.Outlined.Home, Icons.Filled.Home, "nav_worker_dash"),
            NavItem("Schedule", Icons.Outlined.DateRange, Icons.Filled.DateRange, "nav_worker_schedule"),
            NavItem("Reels Studio", Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary, "nav_worker_reels"),
            NavItem("Active Jobs", Icons.Outlined.Work, Icons.Filled.Work, "nav_worker_jobs"),
            NavItem("Earnings", Icons.Outlined.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_worker_earnings")
        )
        UserRole.ENTERPRISE -> listOf(
            NavItem("Workforce", Icons.Outlined.Business, Icons.Filled.Business, "nav_enterprise_projects"),
            NavItem("Invoicing", Icons.Outlined.Assignment, Icons.Filled.Assignment, "nav_enterprise_invoices")
        )
        UserRole.ADMIN -> listOf(
            NavItem("Overview", Icons.Outlined.Security, Icons.Filled.Security, "nav_admin_overview"),
            NavItem("Verification", Icons.Outlined.VerifiedUser, Icons.Filled.VerifiedUser, "nav_admin_verify"),
            NavItem("Disputes", Icons.Outlined.Assignment, Icons.Filled.Assignment, "nav_admin_disputes")
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
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FixoBlue600,
                    selectedTextColor = FixoBlue600,
                    indicatorColor = FixoBlue50,
                    unselectedIconColor = FixoSlate500,
                    unselectedTextColor = FixoSlate500
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}
