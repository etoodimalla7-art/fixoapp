package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate300
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700
import kotlinx.coroutines.launch

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String,
    val highlightColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: (UserRole) -> Unit,
    onSkipToLogin: () -> Unit
) {
    val steps = listOf(
        OnboardingStep(
            title = "Find Verified Artisans Across Cameroon",
            subtitle = "Access certified technicians in Douala, Yaoundé, and all major cities. Guaranteed background vetting and CNI identity checks.",
            icon = Icons.Default.Security,
            badge = "TRUST & CERTIFICATION",
            highlightColor = FixoBlue600
        ),
        OnboardingStep(
            title = "Watch Real Craftsmanship on FIXO Reels",
            subtitle = "Inspect authentic work video portfolios, diagnostic walk-throughs, and previous project results before you book.",
            icon = Icons.Default.PlayCircle,
            badge = "TRANSPARENT REELS",
            highlightColor = FixoAmber500
        ),
        OnboardingStep(
            title = "Guaranteed Escrow & Mobile Money",
            subtitle = "Pay seamlessly with MTN MoMo or Orange Money. Funds are locked securely in escrow until you approve completed work.",
            icon = Icons.Default.CheckCircle,
            badge = "SECURE ESCROW",
            highlightColor = FixoEmerald600
        ),
        OnboardingStep(
            title = "Choose How You Will Use FIXO",
            subtitle = "Select your primary account profile to customize your experience and registration process.",
            icon = Icons.Default.Person,
            badge = "PERSONALIZED WORKSPACE",
            highlightColor = FixoNavy900
        )
    )

    val pagerState = rememberPagerState(pageCount = { steps.size })
    val coroutineScope = rememberCoroutineScope()
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Brand Logo & Skip Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.fixo_logo),
                        contentDescription = "FIXO",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FIXO",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (pagerState.currentPage < steps.size - 1) {
                    TextButton(onClick = onSkipToLogin) {
                        Text(
                            text = "Skip to Sign In",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = FixoSlate500
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Pager with Onboarding Steps
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val step = steps[page]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (page < 3) {
                        // Illustrated Icon Badge
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(step.highlightColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = step.highlightColor,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(step.highlightColor.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = step.badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = step.highlightColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = step.subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = FixoSlate500,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    } else {
                        // Final Page: Role Selection Cards
                        Text(
                            text = "How will you use FIXO?",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Choose your role to start registration",
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Customer Card
                        RoleSelectCard(
                            title = "I want to hire Artisans",
                            subtitle = "For home, office repairs, verified quotes & escrow security",
                            icon = Icons.Default.Person,
                            role = UserRole.CUSTOMER,
                            isSelected = selectedRole == UserRole.CUSTOMER,
                            onClick = { selectedRole = UserRole.CUSTOMER }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Worker Card
                        RoleSelectCard(
                            title = "I am an Artisan / Technician",
                            subtitle = "Offer services, showcase videos, receive direct payouts",
                            icon = Icons.Default.Build,
                            role = UserRole.WORKER,
                            isSelected = selectedRole == UserRole.WORKER,
                            onClick = { selectedRole = UserRole.WORKER }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Enterprise Card
                        RoleSelectCard(
                            title = "Enterprise / Contractor",
                            subtitle = "Manage large projects and recruit skilled workforce",
                            icon = Icons.Default.Apartment,
                            role = UserRole.ENTERPRISE,
                            isSelected = selectedRole == UserRole.ENTERPRISE,
                            onClick = { selectedRole = UserRole.ENTERPRISE }
                        )
                    }
                }
            }

            // Pager Indicator Dots
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(steps.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) FixoBlue600 else FixoSlate300)
                    )
                }
            }

            // Bottom Action Button
            if (pagerState.currentPage < steps.size - 1) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Continue", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Button(
                    onClick = { onComplete(selectedRole) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_complete_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600)
                ) {
                    Text(
                        text = "Get Started as ${when (selectedRole) {
                            UserRole.CUSTOMER -> "Client"
                            UserRole.WORKER -> "Artisan"
                            UserRole.ENTERPRISE -> "Enterprise"
                            UserRole.ADMIN -> "Admin"
                        }}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleSelectCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    role: UserRole,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FixoBlue50 else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) FixoBlue600 else FixoSlate200
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) FixoBlue600 else FixoSlate100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else FixoNavy900,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 12.sp)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = FixoBlue600,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
