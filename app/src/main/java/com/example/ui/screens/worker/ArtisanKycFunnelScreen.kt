package com.example.ui.screens.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CameroonMobileOperator
import com.example.data.model.ServiceCategory
import com.example.data.model.User
import com.example.data.model.detectCameroonOperator
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 4. Le Funnel d'Homologation KYC & L'Écran d'Attente Artisan
 * L'artisan gère l'ensemble de sa demande directement depuis son téléphone, de manière totalement autonome :
 * - Étape 1 : Spécialités Techniques
 * - Étape 2 : Numérisation des Justificatifs (avec gabarit de cadrage caméra)
 * - Étape 3 : Liaison du Compte Financier de Retrait (MTN MoMo / Orange Money)
 */
@Composable
fun ArtisanKycFunnelScreen(
    user: User?,
    language: AppLanguage = AppLanguage.FR,
    onKycSubmitted: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableIntStateOf(1) } // 1, 2, 3

    // Étape 1: Spécialités Techniques
    val selectedTrades = remember { mutableStateListOf(ServiceCategory.PLUMBING) }
    var hourlyRateInput by remember { mutableStateOf("15000") }
    var experienceYears by remember { mutableStateOf("5") }
    var artisanBio by remember {
        mutableStateOf("Artisan qualifié en plomberie sanitaire et réseaux d'eau avec 5 ans d'expérience sur Douala.")
    }

    // Étape 2: Numérisation des Justificatifs
    var cniRectoUploaded by remember { mutableStateOf(false) }
    var cniVersoUploaded by remember { mutableStateOf(false) }
    var casierJudiciaireUploaded by remember { mutableStateOf(false) }
    var diplomeCertifUploaded by remember { mutableStateOf(false) }

    // Camera Framing Overlay Modal
    var activeCameraDocType by remember { mutableStateOf<String?>(null) }
    var isSimulatingUpload by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0f) }

    // Étape 3: Liaison Compte Financier
    var momoPhone by remember { mutableStateOf("+237 671 234 567") }
    var accountHolderName by remember { mutableStateOf(user?.name ?: "Marc Dubois") }
    val detectedOperator = detectCameroonOperator(momoPhone)

    val isStep1Valid = selectedTrades.isNotEmpty() && hourlyRateInput.isNotBlank()
    val isStep2Valid = cniRectoUploaded && cniVersoUploaded && casierJudiciaireUploaded && diplomeCertifUploaded
    val isStep3Valid = momoPhone.length >= 9 && accountHolderName.isNotBlank()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("artisan_kyc_funnel_screen"),
        color = FixoBgCanvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar with Back and Step indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.testTag("btn_kyc_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = FixoWhite
                    )
                }

                Text(
                    text = if (language == AppLanguage.FR) "Homologation Artisan PRO" else "Artisan PRO Accreditation",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoWhite
                )

                Surface(
                    color = FixoNavy800,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Étape $currentStep / 3",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Step Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (step in 1..3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (step <= currentStep) FixoGold500 else FixoNavy800
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // STEP 1 : Spécialités Techniques
            // ==========================================
            if (currentStep == 1) {
                Text(
                    text = if (language == AppLanguage.FR) "1. Spécialités Techniques" else "1. Technical Specialties",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = FixoWhite
                )
                Text(
                    text = if (language == AppLanguage.FR)
                        "Sélectionnez les métiers pour lesquels vous êtes qualifié et souhaitez recevoir des missions."
                    else
                        "Select the trades you are qualified for and wish to receive on-demand missions.",
                    fontSize = 13.sp,
                    color = FixoTextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Trade Categories Selection Grid / Chips
                Text(
                    text = if (language == AppLanguage.FR) "Vos métiers (sélection multiple) :" else "Your trades (multi-select):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoGold500
                )
                Spacer(modifier = Modifier.height(8.dp))

                val availableTrades = listOf(
                    ServiceCategory.PLUMBING,
                    ServiceCategory.ELECTRICAL,
                    ServiceCategory.AC_COOLING,
                    ServiceCategory.CONSTRUCTION,
                    ServiceCategory.PAINTING,
                    ServiceCategory.APPLIANCE_REPAIR,
                    ServiceCategory.TECH_SUPPORT,
                    ServiceCategory.OTHER
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableTrades.chunked(2).forEach { rowTrades ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTrades.forEach { trade ->
                                val isSelected = selectedTrades.contains(trade)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clickable {
                                            if (isSelected) {
                                                if (selectedTrades.size > 1) selectedTrades.remove(trade)
                                            } else {
                                                selectedTrades.add(trade)
                                            }
                                        }
                                        .testTag("trade_chip_${trade.name.lowercase()}"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) FixoNavy800 else FixoSurfaceCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isSelected) FixoGold500 else FixoBorderSubtle
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Description,
                                            contentDescription = null,
                                            tint = if (isSelected) FixoGold500 else FixoTextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = trade.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) FixoWhite else FixoTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Hourly Rate in FCFA
                Text(
                    text = if (language == AppLanguage.FR) "Taux horaire indicatif (FCFA) :" else "Hourly rate estimate (FCFA):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoGold500
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = hourlyRateInput,
                    onValueChange = { hourlyRateInput = it.filter { ch -> ch.isDigit() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kyc_hourly_rate"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Text(
                            text = "FCFA/h",
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = FixoBorderSubtle,
                        focusedTextColor = FixoWhite,
                        unfocusedTextColor = FixoWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Années d'expérience
                Text(
                    text = if (language == AppLanguage.FR) "Années d'expérience professionnelle :" else "Years of professional experience:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoGold500
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = experienceYears,
                    onValueChange = { experienceYears = it.filter { ch -> ch.isDigit() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kyc_experience"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Text(
                            text = "ans",
                            color = FixoTextSecondary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = FixoBorderSubtle,
                        focusedTextColor = FixoWhite,
                        unfocusedTextColor = FixoWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bio / Description
                Text(
                    text = if (language == AppLanguage.FR) "Présentation de vos compétences :" else "Description of your skills:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoGold500
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = artisanBio,
                    onValueChange = { artisanBio = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("input_kyc_bio"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = FixoBorderSubtle,
                        focusedTextColor = FixoWhite,
                        unfocusedTextColor = FixoWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Next Step Button
                Button(
                    onClick = { currentStep = 2 },
                    enabled = isStep1Valid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_kyc_step1_next"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoGold500)
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Suivant : Numérisation des Pièces ➔" else "Next: Scan Documents ➔",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoNavy950
                    )
                }
            }

            // ==========================================
            // STEP 2 : Numérisation des Justificatifs
            // ==========================================
            if (currentStep == 2) {
                Text(
                    text = if (language == AppLanguage.FR) "2. Numérisation des Justificatifs" else "2. Document Digitization",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = FixoWhite
                )
                Text(
                    text = if (language == AppLanguage.FR)
                        "L'appareil photo s'ouvre avec un gabarit de cadrage pour garantir la lisibilité optimale de vos pièces."
                    else
                        "The camera will open with a framing overlay to ensure optimal document readability.",
                    fontSize = 13.sp,
                    color = FixoTextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Doc 1: CNI Recto
                KycDocUploadCard(
                    title = if (language == AppLanguage.FR) "CNI ou Passeport (Recto)" else "National ID or Passport (Front)",
                    subtitle = if (language == AppLanguage.FR) "Photo nette, bords visibles, sans reflets" else "Sharp photo, visible edges",
                    isUploaded = cniRectoUploaded,
                    onScanClick = {
                        activeCameraDocType = "cni_recto"
                    },
                    testTagPrefix = "cni_recto"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Doc 2: CNI Verso
                KycDocUploadCard(
                    title = if (language == AppLanguage.FR) "CNI ou Passeport (Verso)" else "National ID (Back)",
                    subtitle = if (language == AppLanguage.FR) "Puce et bande de lecture MRZ lisibles" else "Readable MRZ band",
                    isUploaded = cniVersoUploaded,
                    onScanClick = {
                        activeCameraDocType = "cni_verso"
                    },
                    testTagPrefix = "cni_verso"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Doc 3: Extrait de casier judiciaire (Bulletin n°3)
                KycDocUploadCard(
                    title = if (language == AppLanguage.FR) "Extrait de Casier Judiciaire (Bulletin n°3)" else "Criminal Record Excerpt (Bulletin #3)",
                    subtitle = if (language == AppLanguage.FR) "Délivré par le tribunal de première instance (< 3 mois)" else "Issued by judicial authority (< 3 months)",
                    isUploaded = casierJudiciaireUploaded,
                    onScanClick = {
                        activeCameraDocType = "casier"
                    },
                    testTagPrefix = "casier"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Doc 4: Diplôme, certificat ou attestation
                KycDocUploadCard(
                    title = if (language == AppLanguage.FR) "Diplôme, Certificat ou Attestation" else "Diploma, Certificate or Trade Attestation",
                    subtitle = if (language == AppLanguage.FR) "CAP, CQP, certificat d'apprentissage ou attestation d'employeur" else "Vocational diploma or apprenticeship certificate",
                    isUploaded = diplomeCertifUploaded,
                    onScanClick = {
                        activeCameraDocType = "diplome"
                    },
                    testTagPrefix = "diplome"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick pre-fill all documents button for testing
                OutlinedButton(
                    onClick = {
                        cniRectoUploaded = true
                        cniVersoUploaded = true
                        casierJudiciaireUploaded = true
                        diplomeCertifUploaded = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_kyc_autofill_all_docs"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "⚡ Numériser & Valider toutes les pièces (Mode Rapide)",
                        fontSize = 12.sp,
                        color = FixoGold500,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Next Step Button
                Button(
                    onClick = { currentStep = 3 },
                    enabled = isStep2Valid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_kyc_step2_next"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoGold500)
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Suivant : Compte Financier de Retrait ➔" else "Next: Payout Account ➔",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoNavy950
                    )
                }
            }

            // ==========================================
            // STEP 3 : Liaison du Compte Financier de Retrait
            // ==========================================
            if (currentStep == 3) {
                Text(
                    text = if (language == AppLanguage.FR) "3. Liaison du Compte Financier" else "3. Payout Account Linking",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = FixoWhite
                )
                Text(
                    text = if (language == AppLanguage.FR)
                        "Saisissez le compte Mobile Money destiné à recevoir vos gains futurs. La réglementation exige une concordance d'identité stricte."
                    else
                        "Enter the Mobile Money account to receive your payouts. Strict identity matching is required by regulations.",
                    fontSize = 13.sp,
                    color = FixoTextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Mobile Money Phone
                Text(
                    text = if (language == AppLanguage.FR) "Numéro Mobile Money (+237) :" else "Mobile Money Number (+237):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoGold500
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = momoPhone,
                    onValueChange = { momoPhone = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kyc_momo_phone"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = FixoBorderSubtle,
                        focusedTextColor = FixoWhite,
                        unfocusedTextColor = FixoWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Detected Operator Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Opérateur détecté : " else "Detected operator: ",
                        fontSize = 12.sp,
                        color = FixoTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = when (detectedOperator) {
                            CameroonMobileOperator.MTN_MOMO -> Color(0xFFFFCC00)
                            CameroonMobileOperator.ORANGE_MONEY -> Color(0xFFFF6600)
                            else -> FixoNavy800
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("kyc_momo_operator_pill")
                    ) {
                        Text(
                            text = "${detectedOperator.label} ${detectedOperator.badgeEmoji}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (detectedOperator == CameroonMobileOperator.MTN_MOMO) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Account Holder Name (Must match CNI)
                Text(
                    text = if (language == AppLanguage.FR) "Nom du titulaire du compte Mobile Money :" else "Account holder full name:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoGold500
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = accountHolderName,
                    onValueChange = { accountHolderName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_kyc_account_holder"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = FixoBorderSubtle,
                        focusedTextColor = FixoWhite,
                        unfocusedTextColor = FixoWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Identity Matching Guarantee Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_kyc_identity_match"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = FixoNavy900),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoSuccessGreen.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = FixoSuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (language == AppLanguage.FR) "Concordance d'Identité Validée" else "Identity Match Verified",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoSuccessGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "Le nom \"$accountHolderName\" correspond parfaitement à la pièce d'identité officielle numérisée."
                                else
                                    "Name \"$accountHolderName\" matches the scanned national identity document.",
                                fontSize = 11.sp,
                                color = FixoTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Final Submission Button
                Button(
                    onClick = {
                        onKycSubmitted()
                    },
                    enabled = isStep3Valid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_submit_kyc_dossier"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoGold500)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = FixoNavy950,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Soumettre mon dossier d'homologation"
                        else
                            "Submit Accreditation Dossier",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoNavy950
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ==========================================
    // Camera Framing Overlay Dialog
    // ==========================================
    if (activeCameraDocType != null) {
        val docName = when (activeCameraDocType) {
            "cni_recto" -> "CNI (Recto)"
            "cni_verso" -> "CNI (Verso)"
            "casier" -> "Extrait de Casier Judiciaire"
            else -> "Diplôme / Attestation"
        }

        Dialog(
            onDismissRequest = {
                if (!isSimulatingUpload) activeCameraDocType = null
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.96f))
                    .testTag("camera_framing_dialog"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Bar in camera
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { activeCameraDocType = null },
                            enabled = !isSimulatingUpload
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "Gabarit de Cadrage : $docName",
                            color = FixoGold500,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Box(modifier = Modifier.size(40.dp))
                    }

                    // Framing Rect with Corner Guides
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF141A28))
                            .border(1.5.dp, FixoGold500.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Corner Guides Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cornerLen = 36f
                            val stroke = 6f
                            val color = Color(0xFFFFB300)

                            // Top-Left
                            drawLine(color, Offset(16f, 16f), Offset(16f + cornerLen, 16f), stroke)
                            drawLine(color, Offset(16f, 16f), Offset(16f, 16f + cornerLen), stroke)

                            // Top-Right
                            drawLine(color, Offset(size.width - 16f, 16f), Offset(size.width - 16f - cornerLen, 16f), stroke)
                            drawLine(color, Offset(size.width - 16f, 16f), Offset(size.width - 16f, 16f + cornerLen), stroke)

                            // Bottom-Left
                            drawLine(color, Offset(16f, size.height - 16f), Offset(16f + cornerLen, size.height - 16f), stroke)
                            drawLine(color, Offset(16f, size.height - 16f), Offset(16f, size.height - 16f - cornerLen), stroke)

                            // Bottom-Right
                            drawLine(color, Offset(size.width - 16f, size.height - 16f), Offset(size.width - 16f - cornerLen, size.height - 16f), stroke)
                            drawLine(color, Offset(size.width - 16f, size.height - 16f), Offset(size.width - 16f, size.height - 16f - cornerLen), stroke)
                        }

                        if (!isSimulatingUpload) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = FixoGold500.copy(alpha = 0.8f),
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Centrez le document dans le cadre",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Éclairage suffisant • Aucun reflet",
                                    color = FixoTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(
                                    text = "Compression & Téléversement sécurisé...",
                                    color = FixoGold500,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = { uploadProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = FixoGold500,
                                    trackColor = FixoNavy800
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${(uploadProgress * 100).toInt()}% • Optimisation JPEG 2000",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Bottom Shutter Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(72.dp)
                                .clickable(enabled = !isSimulatingUpload) {
                                    isSimulatingUpload = true
                                    uploadProgress = 0.1f
                                    coroutineScope.launch {
                                        delay(250)
                                        uploadProgress = 0.45f
                                        delay(250)
                                        uploadProgress = 0.85f
                                        delay(200)
                                        uploadProgress = 1.0f
                                        delay(150)
                                        when (activeCameraDocType) {
                                            "cni_recto" -> cniRectoUploaded = true
                                            "cni_verso" -> cniVersoUploaded = true
                                            "casier" -> casierJudiciaireUploaded = true
                                            "diplome" -> diplomeCertifUploaded = true
                                        }
                                        isSimulatingUpload = false
                                        activeCameraDocType = null
                                    }
                                }
                                .testTag("btn_camera_shutter"),
                            shape = CircleShape,
                            color = FixoGold500,
                            border = androidx.compose.foundation.BorderStroke(4.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Prendre photo",
                                    tint = FixoNavy950,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Toucher pour numériser la pièce",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KycDocUploadCard(
    title: String,
    subtitle: String,
    isUploaded: Boolean,
    onScanClick: () -> Unit,
    testTagPrefix: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onScanClick() }
            .testTag("card_upload_$testTagPrefix"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isUploaded) FixoSuccessGreen else FixoBorderSubtle
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = if (isUploaded) FixoSuccessGreen.copy(alpha = 0.2f) else FixoNavy800
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = if (isUploaded) FixoSuccessGreen else FixoGold500,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = FixoTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                color = if (isUploaded) FixoSuccessGreen.copy(alpha = 0.2f) else FixoGold500.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUploaded) FixoSuccessGreen else FixoGold500
                )
            ) {
                Text(
                    text = if (isUploaded) "✓ Validé" else "Numériser",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUploaded) FixoSuccessGreen else FixoGold500,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}
