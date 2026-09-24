package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.R
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoElectricAmberDark
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * CHANTIER 3 - Top App Bar Unique (56 dp) — CustomerTopBar
 * - Hauteur fixe : 56 dp, ancrée sous la barre système avec fond semi-transparent (#080C15 à 85% d'opacité)
 * - Cluster Gauche :
 *   - Logo FIXO stylisé (aile dorée 28 dp).
 *   - Sélecteur de quartier cliquable : 📍 Akwa, Douala ▾ (ouvre le sélecteur / Bottom Sheet d'adresses).
 * - Cluster Droite :
 *   - Bouton sélecteur de langue interactif [ FR | EN ] avec permutation instantanée sans rechargement.
 *   - Icône cloche de notifications NotificationBell avec pastille rouge active.
 */
@Composable
fun CustomerTopBar(
    currentQuarterName: String,
    language: AppLanguage,
    unreadNotificationCount: Int,
    onQuarterSelectorClick: () -> Unit,
    onToggleLanguage: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("customer_top_bar"),
        color = FixoBgCanvas.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, FixoBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cluster Gauche : Logo FIXO (28 dp) + Sélecteur de quartier
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Official Fixo stylized logo
                Image(
                    painter = painterResource(id = R.drawable.fixo_logo),
                    contentDescription = "FIXO Logo",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Sélecteur de quartier cliquable : 📍 Akwa, Douala ▾
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = FixoSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle),
                    modifier = Modifier
                        .clickable { onQuarterSelectorClick() }
                        .testTag("topbar_quarter_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = FixoElectricAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$currentQuarterName, Douala",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoTextPrimary
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = FixoElectricAmber,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Cluster Droite : [ FR | EN ] + Cloche de notifications
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bouton sélecteur de langue interactif [ FR | EN ]
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E2838),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle),
                    modifier = Modifier
                        .clickable { onToggleLanguage() }
                        .testTag("topbar_language_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FR",
                            fontSize = 11.sp,
                            fontWeight = if (language == AppLanguage.FR) FontWeight.Black else FontWeight.Normal,
                            color = if (language == AppLanguage.FR) FixoElectricAmber else FixoTextMuted
                        )
                        Text(
                            text = " | ",
                            fontSize = 10.sp,
                            color = FixoBorderSubtle
                        )
                        Text(
                            text = "EN",
                            fontSize = 11.sp,
                            fontWeight = if (language == AppLanguage.EN) FontWeight.Black else FontWeight.Normal,
                            color = if (language == AppLanguage.EN) FixoElectricAmber else FixoTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Cloche de notifications avec pastille rouge active
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("notification_bell_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = FixoRed500,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$unreadNotificationCount",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                // Default active red dot for live alerts
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(FixoRed500)
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = FixoTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
