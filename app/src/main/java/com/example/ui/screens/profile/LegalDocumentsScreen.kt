package com.example.ui.screens.profile

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
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoStatusGreen
import com.example.ui.theme.FixoStatusRed
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * DOCUMENTS LÉGAUX ET RÉGLEMENTAIRES DÉROULANTS (CONFORMITÉ CEMAC & MINPOSTEL)
 * 1. Conditions Générales d'Utilisation (CGU)
 * 2. Politique de Protection des Données (CEMAC & ISO 27001)
 * 3. Protocole de Garantie & Arbitrage Fixo Shield
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentsScreen(
    initialTab: Int = 0,
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(initialTab.coerceIn(0, 2)) }

    val tabs = listOf(
        if (language == AppLanguage.FR) "CGU & Séquestre" else "Terms & Escrow",
        if (language == AppLanguage.FR) "Données (CEMAC)" else "Privacy (CEMAC)",
        if (language == AppLanguage.FR) "Garantie Fixo Shield" else "Fixo Shield"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("legal_documents_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.FR) "Textes Réglementaires" else "Regulatory Documents",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("legal_docs_back_btn")
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
        ) {
            // Onglets de navigation
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = FixoBgCanvas,
                contentColor = FixoGold500,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = FixoGold500
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) FixoGold500 else FixoTextSecondary
                            )
                        }
                    )
                }
            }

            HorizontalDivider(color = FixoBorderSubtle)

            // Contenu défilant complet
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                when (selectedTab) {
                    0 -> TermsOfServiceSection()
                    1 -> PrivacyPolicySection()
                    2 -> ShieldArbitrationSection()
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun TermsOfServiceSection() {
    Column {
        LegalHeaderCard(
            title = "Conditions Générales d'Utilisation (CGU)",
            version = "Version 2.4 • En vigueur au Cameroun depuis le 01/01/2026",
            icon = Icons.Default.Gavel,
            accentColor = FixoGold500
        )

        Spacer(modifier = Modifier.height(18.dp))

        LegalArticleCard(
            articleNumber = "Article 1",
            title = "Statut de Courtier Technologique & Tiers de Séquestre",
            content = "La société FIXO SARL agit exclusivement en qualité d'opérateur de plateforme technique et de tiers de séquestre financier agréé. FIXO n'est ni employeur ni maître d'œuvre direct des artisans inscrits. La plateforme met en relation des particuliers ou professionnels maîtres d'ouvrage avec des artisans indépendants homologués selon les lois en vigueur en République du Cameroun."
        )

        Spacer(modifier = Modifier.height(14.dp))

        LegalArticleCard(
            articleNumber = "Article 2",
            title = "Protocole de Contractualisation & Acceptation",
            content = "Toute réservation effectuée sur l'application emporte mandat exprès confié à FIXO pour séquestrer les fonds convenus auprès des établissements de paiement agréés (Orange Money, MTN MoMo, Cartes bancaires). Le contrat de prestation de dépannage est conclu directement entre le client et l'artisan lors de la confirmation d'arrivée sur le lieu d'intervention."
        )

        Spacer(modifier = Modifier.height(14.dp))

        LegalArticleCard(
            articleNumber = "Article 3",
            title = "Barème Forfaitaire Strict & Interdiction Formelle des Espèces",
            content = "Afin d'éradiquer la spéculation sur les chantiers d'urgence, chaque intervention d'urgence standard est soumise à un forfait ferme fixé à 15 000 FCFA comprenant le déplacement, le diagnostic et 1 heure de main-d'œuvre qualifiée (hors coût éventuel des pièces de rechange sur devis validé dans l'app). Tout paiement direct en espèces (Cash) de la main-d'œuvre sur le chantier est formellement interdit sous peine de déchéance immédiate de la garantie Fixo Shield et d'exclusion définitive du réseau."
        )

        Spacer(modifier = Modifier.height(14.dp))

        LegalArticleCard(
            articleNumber = "Article 4",
            title = "Procédure de Clôture Obligatoire par Double Clé (QR Code + PIN)",
            content = "La libération du séquestre au profit de l'artisan est soumise à l'exécution d'une double vérification cryptographique : le client doit scanner le QR code vectoriel généré sur le terminal de l'artisan ou composer le code PIN de secours transmis par SMS. Cette signature électronique certifie la réception conforme des travaux."
        )
    }
}

@Composable
private fun PrivacyPolicySection() {
    Column {
        LegalHeaderCard(
            title = "Politique de Protection des Données (CEMAC)",
            version = "Conformité Règlement CEMAC / ART Cameroun • Norme ISO 27001",
            icon = Icons.Default.Lock,
            accentColor = FixoStatusGreen
        )

        Spacer(modifier = Modifier.height(18.dp))

        LegalArticleCard(
            articleNumber = "Section 1",
            title = "Chiffrement de Bout en Bout des Données Télémétriques & GPS",
            content = "Les coordonnées géographiques des clients et artisans sont collectées exclusivement pendant la phase de navigation active d'intervention. Elles sont chiffrées en transit (TLS 1.3) et au repos (AES-256). Dès la clôture du chantier certifiée par QR code, la télémétrie en temps réel est immédiatement purgée et anonymisée sous forme de statistiques kilométriques globales."
        )

        Spacer(modifier = Modifier.height(14.dp))

        LegalArticleCard(
            articleNumber = "Section 2",
            title = "Conservation Sécurisée des Pièces d'Identité (CNI & Casier)",
            content = "Les documents officiels transmis lors de l'audit d'homologation (Cartes Nationales d'Identité, Bulletins N°3 de Casier Judiciaire, Certificats de Qualification Professionnelle) sont stockés dans un coffre-fort numérique étanche certifié ISO/IEC 27001. Aucun employé tiers ou utilisateur n'a accès direct aux copies intégrales non masquées."
        )

        Spacer(modifier = Modifier.height(14.dp))

        LegalArticleCard(
            articleNumber = "Section 3",
            title = "Passerelle Téléphonique d'Anonymisation (Numéros Masqués)",
            content = "Les communications vocales et SMS initiées depuis l'application FIXO transitent par une passerelle VoIP dédiée qui masque réciproquement les numéros GSM réels des clients et des artisans, prévenant tout démarchage ultérieur non sollicité hors plateforme."
        )
    }
}

@Composable
private fun ShieldArbitrationSection() {
    Column {
        LegalHeaderCard(
            title = "Protocole de Garantie & Arbitrage Fixo Shield",
            version = "Protection Intégrale Maître d'Ouvrage • Plafond 300 000 FCFA",
            icon = Icons.Default.Shield,
            accentColor = Color(0xFF38BDF8)
        )

        Spacer(modifier = Modifier.height(18.dp))

        LegalArticleCard(
            articleNumber = "Protocole 1",
            title = "Modalités de Gel Immédiat du Séquestre",
            content = "En cas de contestation de conformité émise par le client avant la validation du QR code ou dans les 48 heures suivant l'intervention, les fonds séquestrés sont automatiquement gelés. L'artisan ne peut prétendre à aucun versement tant que la procédure contradictoire de médiation n'a pas rendu ses conclusions."
        )

        Spacer(modifier = Modifier.height(14.dp))

        LegalArticleCard(
            articleNumber = "Protocole 2",
            title = "Obligation de la Preuve Contradictoire (Photo Avant / Photo Après)",
            content = "L'arbitrage FIXO s'appuie obligatoirement sur l'inspection visuelle certifiée par l'application : la photo horodatée de l'état initial (« Photo Avant ») et la photo de l'installation terminée (« Photo Après »). Tout écart manifeste entre les engagements et le résultat constaté ouvre droit à l'activation de la garantie sans pénalité pour le client."
        )

        Spacer(modifier = Modifier.height(14.dp))

        LegalArticleCard(
            articleNumber = "Protocole 3",
            title = "Prise en Charge des Malfaçons & Réintervention Sans Frais",
            content = "La garantie Fixo Shield couvre les malfaçons, fuites récidivantes et défauts d'installation jusqu'à un montant forfaitaire de 300 000 FCFA pendant 14 jours calendaires. FIXO mandate en urgence un second Maître Artisan Référent pour corriger les non-conformités sans aucun coût additionnel pour le maître d'ouvrage."
        )
    }
}

@Composable
private fun LegalHeaderCard(
    title: String,
    version: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = version,
                    fontSize = 11.sp,
                    color = FixoTextSecondary
                )
            }
        }
    }
}

@Composable
private fun LegalArticleCard(
    articleNumber: String,
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FixoGold500.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = articleNumber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                fontSize = 13.sp,
                color = FixoTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}
