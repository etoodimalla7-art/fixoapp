package com.example.ui.screens.worker

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSuccessGreen

/**
 * Véritable Espace Profil Pro Artisan Homologué FIXO (Marc Dubois)
 * Exclut rigoureusement tout élément client (points rewards, favoris plombiers).
 */
@Composable
fun WorkerProfileScreen(
    user: User?,
    workerProfile: WorkerProfile?,
    language: AppLanguage = AppLanguage.FR,
    onNavigateToWallet: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var interventionRadiusKm by remember { mutableFloatStateOf(8.0f) }

    val isDark = MaterialTheme.colorScheme.surface.let {
        (0.299 * it.red + 0.587 * it.green + 0.114 * it.blue) < 0.5
    }

    val bgColor = if (isDark) Color(0xFF080C15) else Color(0xFFF8FAFC)
    val cardBg = if (isDark) Color(0xFF111827) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("worker_pro_profile_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // En-tête Pro Artisan
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AsyncImage(
                            model = user?.avatarUrl?.ifBlank { "https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=400" }
                                ?: "https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=400",
                            contentDescription = "Photo certifiée Marc Dubois",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .border(3.dp, FixoGold500, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(FixoSuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Certifié",
                                tint = Color(0xFF080C15),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = user?.name ?: "Marc Dubois",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Badge émeraude Maître Artisan Agréé FIXO ✓
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(FixoSuccessGreen.copy(alpha = 0.15f))
                            .border(1.dp, FixoSuccessGreen, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Maître Artisan Agréé FIXO ✓",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoSuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Plomberie sanitaire & Soudure cuivre",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FixoGold500
                    )
                }
            }
        }

        // Métriques d'Excellence
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.FR) "Métriques d'Excellence Opérationnelle" else "Operational Metrics",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(
                            icon = Icons.Default.Star,
                            iconTint = FixoGold500,
                            value = "4.9 ★",
                            label = if (language == AppLanguage.FR) "124 chantiers" else "124 jobs",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                        MetricItem(
                            icon = Icons.Default.Speed,
                            iconTint = FixoSuccessGreen,
                            value = "98%",
                            label = if (language == AppLanguage.FR) "Ponctualité < 30m" else "Punctuality",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                        MetricItem(
                            icon = Icons.Default.Percent,
                            iconTint = Color(0xFF3B82F6),
                            value = "8%",
                            label = if (language == AppLanguage.FR) "Palier Maître" else "Commission",
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Dossier d'Homologation & Sceaux Légaux
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "Dossier d'Homologation & Sceaux Légaux" else "Legal Homologation Seals",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = FixoSuccessGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LegalSealRow(
                        title = if (language == AppLanguage.FR) "Pièce d'Identité (CNI Cameroun)" else "National ID Card (CNI)",
                        status = if (language == AppLanguage.FR) "Vérifiée" else "Verified",
                        textPrimary = textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LegalSealRow(
                        title = if (language == AppLanguage.FR) "Extrait de Casier Judiciaire" else "Criminal Record Certificate",
                        status = if (language == AppLanguage.FR) "Vierge (Bulletin N°3)" else "Clean Record",
                        textPrimary = textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LegalSealRow(
                        title = if (language == AppLanguage.FR) "CQP / Diplôme Technique Métier" else "Vocational Diploma (CQP)",
                        status = if (language == AppLanguage.FR) "Validé par Commission FIXO" else "Validated",
                        textPrimary = textPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Paramètres Opérationnels
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (language == AppLanguage.FR) "Paramètres d'Intervention" else "Operational Parameters",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Curseur interactif de rayon d'action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR)
                                "Rayon d'action : ${interventionRadiusKm.toInt()} km autour d'Akwa"
                            else
                                "Coverage radius: ${interventionRadiusKm.toInt()} km around Akwa",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                    }

                    Slider(
                        value = interventionRadiusKm,
                        onValueChange = { interventionRadiusKm = it },
                        valueRange = 2f..25f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = FixoGold500,
                            activeTrackColor = FixoGold500,
                            inactiveTrackColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.testTag("worker_radius_slider")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Coordonnées de versement bancaire/mobile
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FixoSuccessGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = FixoSuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (language == AppLanguage.FR) "Versement Mobile Money" else "Mobile Money Payout",
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                                Text(
                                    text = "MTN MoMo (+237 699 *** 543) Vérifié",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Portefeuille & Déconnexion
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToWallet() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = FixoGold500,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Accéder à mon Portefeuille & Retraits" else "Wallet & Cash-Out",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("worker_logout_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        contentColor = Color(0xFFEF4444)
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Déconnexion Espace Pro" else "Log Out Pro Account",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = textPrimary
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = textSecondary
        )
    }
}

@Composable
private fun LegalSealRow(
    title: String,
    status: String,
    textPrimary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = FixoSuccessGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(FixoSuccessGreen.copy(alpha = 0.12f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = FixoSuccessGreen
            )
        }
    }
}
