package com.example.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoStatusGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * ÉCRAN DÉDIÉ "À PROPOS DE FIXO" (UBER-GRADE STATUTAIRE)
 * Présentation officielle de la plateforme technologique de référence pour les corps de métiers certifiés au Cameroun.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AboutFixoScreen(
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("about_fixo_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.FR) "À Propos de FIXO" else "About FIXO",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("about_fixo_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FixoGold500
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FixoBgCanvas
                )
            )
        },
        containerColor = FixoBgCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // 1. BRAND HEADER AVEC GRAND LOGO OFFICIEL FIXO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(22.dp)),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = R.drawable.fixo_logo),
                                contentDescription = "Logo Officiel FIXO",
                                modifier = Modifier
                                    .size(76.dp)
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FIXO",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = FixoTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(FixoGold500)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "« La plateforme technologique de référence pour les corps de métiers certifiés au Cameroun »",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FixoGold500,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Version 2.0.0 Commercial Release • Douala, Cameroun",
                        fontSize = 11.sp,
                        color = FixoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. CHIFFRES CLÉS STATUTAIRES
            Text(
                text = if (language == AppLanguage.FR) "Indicateurs d'Excellence" else "Key Operational Metrics",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = FixoTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    number = "+450",
                    label = if (language == AppLanguage.FR) "Artisans Audités" else "Audited Artisans",
                    sub = "CNI & Casier Vierge",
                    color = FixoGold500,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    number = "99.2%",
                    label = if (language == AppLanguage.FR) "Chantiers Zéro Litige" else "Zero Dispute Rate",
                    sub = "Validation par QR",
                    color = FixoStatusGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    number = "14 Jours",
                    label = if (language == AppLanguage.FR) "Garantie Shield" else "Shield Warranty",
                    sub = "Jusqu'à 300 000 F",
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. CHARTE D'EXCELLENCE & PROTOCOLE QUALITÉ
            Text(
                text = if (language == AppLanguage.FR) "Charte d'Excellence FIXO" else "FIXO Excellence Charter",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = FixoTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    CharterItem(
                        icon = Icons.Default.VerifiedUser,
                        title = "1. Sélectivité Rigoureuse des Prestataires",
                        description = "Audit physique de la CNI camerounaise, contrôle d'extrait de casier judiciaire (Bulletin N°3) vierge et évaluation technique sur maquette pratique avant toute homologation."
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = FixoBorderSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    CharterItem(
                        icon = Icons.Default.Shield,
                        title = "2. Séquestre Financier Inviolable",
                        description = "Zéro paiement en espèces sur le chantier. Les fonds sont consignés sur compte séquestre bancaire sécurisé et débloqués à l'artisan uniquement après validation du QR code de fin de travaux par le client."
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = FixoBorderSubtle)
                    Spacer(modifier = Modifier.height(14.dp))

                    CharterItem(
                        icon = Icons.Default.Security,
                        title = "3. Garantie Décennale & Protection Fixo Shield",
                        description = "Chaque intervention bénéficie d'une garantie de 14 jours calendaires contre les vices cachés et malfaçons, incluant réintervention prioritaire sans frais ou remboursement sous séquestre jusqu'à 300 000 FCFA."
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. MENTIONS OFFICIELLES & CONFORMITÉ CAMEROUN
            Text(
                text = if (language == AppLanguage.FR) "Mentions Légales & Réglementation" else "Official Notices & Compliance",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = FixoTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    NoticeRow(
                        icon = Icons.Default.Business,
                        title = "Siège Social",
                        value = "Boulevard de la Liberté / Rue Drouot, Akwa, Douala, Région du Littoral, Cameroun"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NoticeRow(
                        icon = Icons.Default.Gavel,
                        title = "Immatriculation & Registre",
                        value = "RC/DLA/2026/B/1429 • N° Contribuable M02261499238L"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NoticeRow(
                        icon = Icons.Default.Shield,
                        title = "Cadre Réglementaire",
                        value = "Conforme aux directives MINPOSTEL & ART Cameroun sur le courtage technologique et la protection des données personnelles (CEMAC)"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NoticeRow(
                        icon = Icons.Default.Email,
                        title = "Contact Institutionnel",
                        value = "contact@fixo.cm • presse@fixo.cm • support@fixo.cm"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NoticeRow(
                        icon = Icons.Default.Phone,
                        title = "Standard Téléphonique",
                        value = "+237 233 42 00 00 / WhatsApp : +237 670 000 001"
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun StatCard(
    number: String,
    label: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = FixoTextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                fontSize = 9.sp,
                color = FixoTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CharterItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(FixoGold500.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FixoGold500,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = FixoTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = FixoTextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun NoticeRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FixoGold500,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = FixoGold500
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                color = FixoTextPrimary,
                lineHeight = 17.sp
            )
        }
    }
}
