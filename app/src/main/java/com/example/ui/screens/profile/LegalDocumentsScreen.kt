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
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * DOCUMENTS LÉGAUX ET RÉGLEMENTAIRES DÉROULANTS (CONFORMITÉ CEMAC & MINPOSTEL)
 * 1. Conditions Générales d'Utilisation & Mandat de Séquestre (5 Sections)
 * 2. Confidentialité & Protection des Données CEMAC / MINPOSTEL (4 Sections)
 * 3. Garantie Fixo Shield & Récidive 14 Jours (4 Sections)
 */
@OptIn(ExperimentalMaterial3Api::class)
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
                        text = if (language == AppLanguage.FR) "Textes Juridiques & Séquestre" else "Legal & Escrow Terms",
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
            // Navigation tabs
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

            // Scrollable full text
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> TermsOfServiceSection(language)
                    1 -> PrivacyPolicySection(language)
                    2 -> ShieldArbitrationSection(language)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TermsOfServiceSection(language: AppLanguage) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LegalHeaderCard(
            title = if (language == AppLanguage.FR)
                "Conditions Générales d'Utilisation & Mandat de Séquestre"
            else
                "General Terms of Service & Escrow Mandate",
            version = "Version 3.1 • En vigueur en République du Cameroun • Réf: FIXO-CM-2026",
            icon = Icons.Default.Gavel,
            accentColor = FixoGold500
        )

        LegalArticleCard(
            articleNumber = "Section 1",
            title = if (language == AppLanguage.FR)
                "Statut d'Opérateur Technique & Intermédiation Neutre"
            else
                "Technical Operator Status & Neutral Intermediation",
            content = if (language == AppLanguage.FR)
                "La plateforme FIXO est exploitée par la société FIXO SARL, enregistrée au Registre du Commerce et du Crédit Mobilier de Douala. FIXO agit strictement et exclusivement en qualité d'opérateur d'infrastructure technologique et de tiers de séquestre financier agréé. FIXO n'est pas un employeur, ni un entrepreneur de travaux publics, ni le maître d'œuvre direct des artisans répertoriés. Chaque artisan inscrit exerce son métier en tant que travailleur indépendant agréé et immatriculé. La mise en relation s'effectue selon des critères objectifs de géolocalisation, de qualification vérifiée et de disponibilité instantanée."
            else
                "The FIXO platform is operated by FIXO SARL, registered in Douala. FIXO acts strictly as a neutral technology infrastructure operator and approved escrow agent. Each listed craftsman operates as an independent verified contractor."
        )

        LegalArticleCard(
            articleNumber = "Section 2",
            title = if (language == AppLanguage.FR)
                "Mandat de Séquestre Financier (Escrow Vault) & Zéro Cash"
            else
                "Financial Escrow Vault Mandate & Zero-Cash Policy",
            content = if (language == AppLanguage.FR)
                "En confirmant une demande d'intervention, le client confère à FIXO un mandat irrévocable de séquestre temporaire des fonds convenus. Tout règlement en espèces (Cash) sur le lieu de l'intervention est formellement interdit sur tout le territoire camerounais. Les fonds sont prélevés via Mobile Money (MTN MoMo, Orange Money) ou carte bancaire et cantonnés dans un compte de séquestre étanche. Les fonds ne peuvent en aucun cas être débloqués unilatéralement : la libération s'effectue exclusivement par validation physique sur site par scan du QR Code cryptographique généré sur le terminal de l'artisan ou, à titre subsidiaire, par composition du code PIN sécurisé."
            else
                "Cash payments on site are strictly forbidden in Cameroon. All job fees are escrowed via Mobile Money (MTN MoMo, Orange Money) and released only upon cryptographic QR Code scanning or secure PIN entry."
        )

        LegalArticleCard(
            articleNumber = "Section 3",
            title = if (language == AppLanguage.FR)
                "Charte d'Homologation & Vérification des Artisans"
            else
                "Craftsman Homologation & Verification Charter",
            content = if (language == AppLanguage.FR)
                "L'admission au réseau d'artisans FIXO impose une vérification rigoureuse de conformité en trois étapes : (a) Présentation d'une Carte Nationale d'Identité (CNI) camerounaise ou d'un titre de séjour en cours de validité ; (b) Extrait de casier judiciaire (Bulletin n°3) vierge datant de moins de 3 mois ; (c) Diplôme technique d'État (CAP, CQP, BTS) ou attestation officielle de fin d'apprentissage validée par la Chambre de Métiers. Tout artisan faisant l'objet d'un signalement de fraude ou de harcèlement est radié sur-le-champ sans préavis."
            else
                "Every artisan must submit a valid National ID card (CNI), clean criminal record extract (Bulletin #3), and certified vocational diploma verified by regulatory trade authorities."
        )

        LegalArticleCard(
            articleNumber = "Section 4",
            title = if (language == AppLanguage.FR)
                "Protocole de Litige & Arbitrage Contradictoire sous 48h"
            else
                "Dispute Protocol & 48h Adversarial Arbitration",
            content = if (language == AppLanguage.FR)
                "En cas de désaccord sur la conformité de l'intervention ou sur l'achèvement des travaux, le client active l'option 'Signaler un Litige' dans l'application avant validation du QR Code. Dès ce signalement, le séquestre financier est instantanément verrouillé. Les deux parties sont tenues de soumettre leurs preuves photographiques horodatées ('Photo Avant' et 'Photo Après'). Une commission d'arbitrage technique FIXO statue sous 48 heures ouvrées pour ordonner soit la reprise sans frais par l'artisan, soit le remboursement intégral au client, soit la réassignation à un Maître Artisan."
            else
                "In case of disagreement, funds are instantly frozen. The platform technical arbitration committee examines before/after photo evidence and issues a binding ruling within 48 business hours."
        )

        LegalArticleCard(
            articleNumber = "Section 5",
            title = if (language == AppLanguage.FR)
                "Responsabilité, Forfait Garanti & Assurances Professionnelles"
            else
                "Liability, Fixed Pricing & Professional Insurance",
            content = if (language == AppLanguage.FR)
                "Les interventions d'urgence de niveau standard font l'objet d'un tarif forfaitaire ferme de 15 000 FCFA garanti sous séquestre couvrant le déplacement, le diagnostic initial et la première heure de main-d'œuvre qualifiée. Tout remplacement de pièces ou intervention lourde excédant le forfait requiert l'émission d'un devis numérique validé dans l'application. Les artisans répondent directement de leurs fautes professionnelles, couvertes en premier ressort par la garantie intégrée Fixo Shield dans la limite des plafonds contractuels."
            else
                "Standard emergency jobs are strictly fixed at 15,000 FCFA escrowed. Any additional parts require an in-app digital estimate. Workmanship is covered under the Fixo Shield policy."
        )
    }
}

@Composable
private fun PrivacyPolicySection(language: AppLanguage) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LegalHeaderCard(
            title = if (language == AppLanguage.FR)
                "Protection des Données Personnelles & Conformité CEMAC"
            else
                "Personal Data Protection & CEMAC Compliance",
            version = "Conforme Directives CEMAC • Recommandations MINPOSTEL & ART Cameroun",
            icon = Icons.Default.Lock,
            accentColor = FixoStatusGreen
        )

        LegalArticleCard(
            articleNumber = "Article 1",
            title = if (language == AppLanguage.FR)
                "Cadre Réglementaire CEMAC & Autorité de Régulation (ART)"
            else
                "CEMAC Framework & Telecommunications Regulatory Agency",
            content = if (language == AppLanguage.FR)
                "Le traitement des données à caractère personnel collectées sur l'application FIXO est régi par le Règlement CEMAC relatif à la protection des consommateurs de services électroniques et les textes de l'Agence de Régulation des Télécommunications (ART) du Cameroun. FIXO met en œuvre des protocoles techniques garantissant la souveraineté, la confidentialité et l'intégrité absolue des flux de paiement et d'identité."
            else
                "Processing complies strictly with CEMAC electronic commerce regulations and Cameroon Ministry of Posts and Telecommunications (MINPOSTEL) data privacy standards."
        )

        LegalArticleCard(
            articleNumber = "Article 2",
            title = if (language == AppLanguage.FR)
                "Traitement Restreint de la Géolocalisation & Purgatoire Télémétrique"
            else
                "Restricted Telemetry & Geolocation Lifespan",
            content = if (language == AppLanguage.FR)
                "La position GPS précise du client et de l'artisan n'est collectée et partagée que pendant la durée strictement nécessaire à l'acheminement de la mission. Dès que l'artisan valide son arrivée sur site ou que le chantier est clôturé par QR Code, la télémétrie en temps réel est immédiatement suspendue. Les coordonnées exactes ne sont ni revendues, ni archivées à des fins de profilage publicitaire."
            else
                "GPS coordinates are only processed during active navigation towards the job. Telemetry stops immediately upon arrival or job completion and is never sold for advertising."
        )

        LegalArticleCard(
            articleNumber = "Article 3",
            title = if (language == AppLanguage.FR)
                "Passerelle Téléphonique d'Anonymisation (Numéros Masqués)"
            else
                "VoIP Phone Masking & Communication Shielding",
            content = if (language == AppLanguage.FR)
                "Pour prémunir les clients et techniciens contre tout démarchage intrusif, harcèlement ou tentative de contournement hors plateforme, les appels vocaux et échanges de messages transitent par une passerelle sécurisée masquant réciproquement les numéros GSM personnels (Orange, MTN, Camtel). Aucun numéro réel n'est divulgué aux intervenants."
            else
                "All phone calls and messages are masked via a secure telecommunications gateway. Personal phone numbers remain completely hidden to prevent unsolicited off-platform contact."
        )

        LegalArticleCard(
            articleNumber = "Article 4",
            title = if (language == AppLanguage.FR)
                "Droits d'Accès, de Rectification & Droit à l'Oubli"
            else
                "Rights of Access, Rectification & Right to Erasure",
            content = if (language == AppLanguage.FR)
                "Conformément à la législation applicable, tout utilisateur dispose d'un droit permanent d'accès, d'exportation et de rectification de ses données. La suppression définitive du compte peut être requise directement depuis l'écran 'Compte', entraînant l'effacement irréversible des historiques de déplacement et pièces justificatives sous réserve des obligations légales d'archivage comptable."
            else
                "Users may request full export, correction or permanent account deletion directly from the Account screen, purging stored documents within regulatory accounting limits."
        )
    }
}

@Composable
private fun ShieldArbitrationSection(language: AppLanguage) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        LegalHeaderCard(
            title = if (language == AppLanguage.FR)
                "Garantie Fixo Shield & Récidive 14 Jours"
            else
                "Fixo Shield Guarantee & 14-Day Recurrence Coverage",
            version = "Couverture Contre Malfaçons • Plafond 300 000 FCFA • Réf: SHIELD-2026",
            icon = Icons.Default.Shield,
            accentColor = Color(0xFF38BDF8)
        )

        LegalArticleCard(
            articleNumber = "Règle 1",
            title = if (language == AppLanguage.FR)
                "Plafond d'Indemnisation jusqu'à 300 000 FCFA par Sinistre"
            else
                "Compensation Cap up to 300,000 FCFA per Claim",
            content = if (language == AppLanguage.FR)
                "La garantie Fixo Shield intervient pour protéger le client contre les conséquences matérielles directes résultant d'une malfaçon, fuite récidivante ou défaillance technique imputable à l'intervention d'un artisan homologué, à hauteur d'un plafond garanti de 300 000 FCFA par sinistre constaté."
            else
                "Fixo Shield covers direct property damages and craftsmanship failures resulting from verified interventions up to 300,000 FCFA per incident."
        )

        LegalArticleCard(
            articleNumber = "Règle 2",
            title = if (language == AppLanguage.FR)
                "Période de Garantie Automatique de 14 Jours Calendaires"
            else
                "Automatic 14-Day Calendar Recurrence Coverage",
            content = if (language == AppLanguage.FR)
                "Chaque chantier validé bénéficie d'une période de garantie automatique de 14 jours calendaires à compter de l'horodatage du scan QR Code de fin de travaux. Si le même problème réapparaît (ex: résurgence de fuite sur le raccordement réparé ou disjonction sur le tableau inspecté), le sinistre est pris en charge d'office sans nouveau forfait de déplacement."
            else
                "Every completed job is automatically guaranteed for 14 calendar days. Any recurrence of the exact repair is serviced without additional callout fees."
        )

        LegalArticleCard(
            articleNumber = "Règle 3",
            title = if (language == AppLanguage.FR)
                "Réintervention Prioritaire Sans Frais par Maître Artisan"
            else
                "Priority Free Re-Intervention by Master Craftsman",
            content = if (language == AppLanguage.FR)
                "Dès déclaration d'un sinistre sous garantie, FIXO mandate sous 24 heures un Maître Artisan Référent (niveau de qualification supérieur et audit d'ancienneté d'au moins 3 ans) pour diagnostiquer et remédier sans frais au problème. Le client ne règle aucun supplément pour cette contre-visite technique."
            else
                "Within 24 hours of claim filing, a senior Master Craftsman is dispatched to rectify the problem with zero supplemental costs charged to the client."
        )

        LegalArticleCard(
            articleNumber = "Règle 4",
            title = if (language == AppLanguage.FR)
                "Conditions d'Exclusion & Obligations de Non-Intervention Tiers"
            else
                "Exclusions & Third-Party Non-Intervention Clause",
            content = if (language == AppLanguage.FR)
                "La garantie Fixo Shield est réputée caduque dans les cas suivants : (a) Intervention de bricolage ou modification des installations par le client ou un tiers non mandaté après la clôture du chantier ; (b) Fourniture par le client de matériaux ou pièces de rechange d'occasion manifestement contrefaits ou non conformes ; (c) Catastrophes naturelles, inondations de quartier ou surtensions majeures du réseau électrique public."
            else
                "Warranty is void if installations were modified by third parties, if counterfeit customer-provided parts failed, or in case of force majeure."
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
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
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
                Spacer(modifier = Modifier.height(3.dp))
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
                        fontWeight = FontWeight.Black,
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
