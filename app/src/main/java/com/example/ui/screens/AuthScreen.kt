package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FixoSeedData
import com.example.data.model.ArtisanKycStatus
import com.example.data.model.CameroonMobileOperator
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.model.detectCameroonOperator
import com.example.localization.AppLanguage
import com.example.ui.components.FixoOtpBottomSheet
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold100
import com.example.ui.theme.FixoGold400
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGoldGradient
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite

/**
 * 2. L'Écran d'Authentification Unifié (Client & Inscription Artisan)
 * L'interface d'entrée reste d'une simplicité totale pour ne créer aucun blocage chez l'utilisateur camerounais.
 *
 * Détection Opérateur Automatique :
 * - Préfixes MTN (67x, 650-654, 68x) : pastille jaune MoMo affichée [ MTN MoMo 🟡 ].
 * - Préfixes Orange (69x, 655-659) : pastille orange affichée [ Orange Money 🟠 ].
 *
 * Sandbox de test strictly bipolaire :
 * Ce volet de débogage sert uniquement à tester les deux rôles de l'application :
 * - Sarah Jenkins (Cliente - Bonapriso)
 * - Marc Dubois (Plombier Pro - Akwa) avec bascule des 3 statuts KYC (Non Commencé, En Revue, Approuvé).
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onPhoneOtpValidated: (phone: String, isCustomer: Boolean) -> Unit,
    onGoogleSignIn: () -> Unit = {},
    onQuickLoginCustomer: () -> Unit,
    onQuickLoginArtisan: (ArtisanKycStatus) -> Unit,
    onOpenArtisanKycFunnel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Se Connecter, 1 = Créer un Compte
    var mainTab by remember { mutableIntStateOf(0) }

    // Phone Input (defaults to Cameroon phone)
    var phoneInput by remember { mutableStateOf("671234567") }
    var showOtpBottomSheet by remember { mutableStateOf(false) }

    // Operator Detection
    val detectedOperator = detectCameroonOperator(phoneInput)

    // Artisan sandbox status selection (for Marc Dubois)
    var testArtisanKycStatus by remember { mutableStateOf(ArtisanKycStatus.IN_REVIEW) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("unified_auth_screen"),
        color = FixoBgCanvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar: FIXO Logo on left/center + Language switch [FR | EN] on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = FixoWhite
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.fixo_logo),
                                contentDescription = "FIXO",
                                modifier = Modifier.size(36.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FIXO",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = FixoWhite
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(FixoGold500, shape = CircleShape)
                            )
                        }
                        Text(
                            text = "Cameroon",
                            fontSize = 11.sp,
                            color = FixoGold400,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Bilingual Toggle [FR | EN]
                Surface(
                    modifier = Modifier
                        .clickable { onToggleLanguage() }
                        .testTag("btn_language_toggle"),
                    shape = RoundedCornerShape(20.dp),
                    color = FixoNavy800,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Langue",
                            tint = FixoGold500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.FR) "FR | EN" else "EN | FR",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    }
                }
            }

            // Slogan Subtitle
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentLanguage == AppLanguage.FR)
                    "Services certifiés & interventions à domicile"
                else
                    "Certified Services & On-Demand Home Interventions",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = FixoGold100.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Tabs: [ Se Connecter ] | [ Créer un Compte ]
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = FixoNavy900,
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                TabRow(
                    selectedTabIndex = mainTab,
                    containerColor = Color.Transparent,
                    contentColor = FixoGold500,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[mainTab]),
                            color = FixoGold500,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = mainTab == 0,
                        onClick = { mainTab = 0 },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("tab_login"),
                        text = {
                            Text(
                                text = if (currentLanguage == AppLanguage.FR) "Se Connecter" else "Sign In",
                                fontWeight = if (mainTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (mainTab == 0) FixoWhite else FixoTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = mainTab == 1,
                        onClick = { mainTab = 1 },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("tab_register"),
                        text = {
                            Text(
                                text = if (currentLanguage == AppLanguage.FR) "Créer un Compte" else "Create Account",
                                fontWeight = if (mainTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (mainTab == 1) FixoWhite else FixoTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Phone Input Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phone_auth_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.FR)
                            "Numéro de téléphone (+237)"
                        else
                            "Phone number (+237)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phone input field with 🇨🇲 +237 prefix and [Valider] action
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { input ->
                            // Keep digits only, max 9
                            phoneInput = input.filter { it.isDigit() }.take(9)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_phone_number"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                            ) {
                                Text(
                                    text = "🇨🇲 +237",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = FixoWhite
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .height(20.dp)
                                        .width(1.dp)
                                        .background(FixoBorderSubtle)
                                )
                            }
                        },
                        trailingIcon = {
                            if (phoneInput.length >= 8) {
                                Surface(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable { showOtpBottomSheet = true }
                                        .testTag("btn_validate_phone_inline"),
                                    color = FixoGold500,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Valider",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = FixoNavy950,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FixoGold500,
                            unfocusedBorderColor = FixoBorderSubtle,
                            focusedTextColor = FixoWhite,
                            unfocusedTextColor = FixoWhite,
                            focusedContainerColor = FixoNavy950,
                            unfocusedContainerColor = FixoNavy950
                        ),
                        shape = RoundedCornerShape(14.dp),
                        placeholder = {
                            Text("6 71 23 45 67", color = FixoTextSecondary)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Détection Opérateur Automatique Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("operator_detection_pill_row")
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.FR) "Opérateur détecté : " else "Detected operator: ",
                            fontSize = 12.sp,
                            color = FixoTextSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when (detectedOperator) {
                                CameroonMobileOperator.MTN_MOMO -> Color(0xFFFFCC00)
                                CameroonMobileOperator.ORANGE_MONEY -> Color(0xFFFF6600)
                                CameroonMobileOperator.UNKNOWN -> FixoNavy800
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (detectedOperator == CameroonMobileOperator.UNKNOWN) FixoBorderSubtle else Color.Transparent
                            ),
                            modifier = Modifier.testTag("detected_operator_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${detectedOperator.label} ${detectedOperator.badgeEmoji}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (detectedOperator == CameroonMobileOperator.MTN_MOMO) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Button: [ RECEVOIR MON CODE DE SÉCURITÉ SMS ]
                    Button(
                        onClick = { showOtpBottomSheet = true },
                        enabled = phoneInput.length >= 8,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_receive_otp"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FixoGold500,
                            disabledContainerColor = FixoNavy800
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = if (phoneInput.length >= 8) FixoNavy950 else FixoTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.FR)
                                "RECEVOIR MON CODE DE SÉCURITÉ SMS"
                            else
                                "RECEIVE SMS SECURITY CODE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (phoneInput.length >= 8) FixoNavy950 else FixoTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Separator: ─────────────── ou continuer avec ───────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = FixoBorderSubtle
                )
                Text(
                    text = if (currentLanguage == AppLanguage.FR) " ou continuer avec " else " or continue with ",
                    fontSize = 12.sp,
                    color = FixoTextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = FixoBorderSubtle
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Button
            OutlinedButton(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_google_signin"),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = FixoWhite
                )
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4285F4),
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.FR) "Continuer avec Google" else "Continue with Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Professional Artisan Application Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenArtisanKycFunnel() }
                    .testTag("banner_postuler_artisan"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FixoNavy900),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FixoGold500.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = FixoGold500,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.FR)
                                "Vous êtes ouvrier ou artisan qualifié ?"
                            else
                                "Are you a skilled worker or craftsman?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.FR)
                                "👉 Postuler comme Prestataire Professionnel"
                            else
                                "👉 Apply as Professional Provider",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = FixoGold500,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ============================================================
            // 🧪 ACCÈS TEST INSTANTANÉ (Sandbox de Développement)
            // Strictement Bipolaire : Sarah la cliente vs Marc l'artisan
            // ============================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sandbox_auth_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1626)),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoAmber500.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = FixoAmber500.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🧪 SANDBOX DE DÉVELOPPEMENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoAmber500,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (currentLanguage == AppLanguage.FR)
                            "Accès direct bipolaire pour tester les 2 rôles étanches de l'application :"
                        else
                            "Strictly bipolar instant access to test both roles:",
                        fontSize = 11.sp,
                        color = FixoTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Bouton Sarah Jenkins (Cliente - Bonapriso)
                    Button(
                        onClick = onQuickLoginCustomer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_sandbox_sarah_client"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "👤", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Connexion : Sarah Jenkins (Cliente - Bonapriso)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                                Text(
                                    text = "Portefeuille Séquestre initialisé ➔ Home C1",
                                    fontSize = 10.sp,
                                    color = FixoSuccessGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Bouton Marc Dubois (Plombier Pro - Akwa)
                    Button(
                        onClick = { onQuickLoginArtisan(testArtisanKycStatus) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_sandbox_marc_artisan"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = "👨🏾‍🔧", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Connexion : Marc Dubois (Plombier Pro - Akwa)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoGold500
                                )
                                Text(
                                    text = when (testArtisanKycStatus) {
                                        ArtisanKycStatus.NOT_STARTED -> "Statut : NON COMMENCÉ ➔ Formulaire KYC"
                                        ArtisanKycStatus.IN_REVIEW -> "Statut : EN REVUE ➔ Écran d'attente"
                                        ArtisanKycStatus.APPROVED -> "Statut : APPROUVÉ ➔ Cockpit Pro A1"
                                    },
                                    fontSize = 10.sp,
                                    color = FixoWhite.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Micro Selector for Marc's KYC Status (to test the 3 states easily)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Statut KYC :",
                            fontSize = 10.sp,
                            color = FixoTextSecondary
                        )

                        listOf(
                            ArtisanKycStatus.NOT_STARTED to "Non commencé",
                            ArtisanKycStatus.IN_REVIEW to "En revue",
                            ArtisanKycStatus.APPROVED to "Approuvé"
                        ).forEach { (status, label) ->
                            val isSelected = testArtisanKycStatus == status
                            Surface(
                                modifier = Modifier
                                    .clickable { testArtisanKycStatus = status }
                                    .testTag("sandbox_kyc_toggle_${status.name.lowercase()}"),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) FixoGold500 else FixoNavy800,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) FixoGold500 else FixoBorderSubtle
                                )
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) FixoNavy950 else FixoWhite,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // 3. Validation par Code OTP (ModalBottomSheet 4 chiffres)
        if (showOtpBottomSheet) {
            FixoOtpBottomSheet(
                phoneNumber = "+237 $phoneInput",
                language = currentLanguage,
                onDismiss = { showOtpBottomSheet = false },
                onOtpVerified = { _ ->
                    showOtpBottomSheet = false
                    // Main tab 0 = login as customer by default unless artisan link clicked
                    onPhoneOtpValidated("+237 $phoneInput", true)
                }
            )
        }
    }
}
