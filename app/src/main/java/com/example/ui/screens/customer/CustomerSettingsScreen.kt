package com.example.ui.screens.customer

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite

data class FavoriteAddress(
    val title: String,
    val quarter: String,
    val landmark: String
)

data class InvoiceItem(
    val invoiceNumber: String,
    val serviceTitle: String,
    val date: String,
    val amountTtc: String,
    val vatAmount: String,
    val status: String
)

data class FaqItem(
    val question: String,
    val answer: String
)

enum class LegalSheetType { ABOUT_PLATFORM, TERMS_ESCROW, DATA_PRIVACY, SHIELD }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSettingsScreen(
    user: User?,
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    onUpdateProfile: (name: String, email: String, phone: String, altPhone: String) -> Unit,
    onToggleLanguage: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(user?.name ?: "Sarah Jenkins") }
    var email by remember { mutableStateOf(user?.email ?: "sarah.jenkins@gmail.com") }
    var phone by remember { mutableStateOf(user?.phone ?: "+237 677 889 900") }
    var altPhone by remember { mutableStateOf(user?.secondaryPhone ?: "+237 699 112 233") }

    // 1. Carnet d'Adresses Réel Douala Cameroun
    val favoriteAddresses = remember {
        mutableStateListOf(
            FavoriteAddress("Domicile", "Akwa, Rue Drouot", "Portail noir en face Boulangerie Zepol"),
            FavoriteAddress("Bureau", "Bonanjo", "Immeuble Crédit Foncier, 3e étage"),
            FavoriteAddress("Famille", "Bonamoussadi", "Carrefour Denver")
        )
    }

    var newAddressTitle by remember { mutableStateOf("") }
    var newAddressQuarter by remember { mutableStateOf("") }
    var newAddressLandmark by remember { mutableStateOf("") }
    var showAddAddress by remember { mutableStateOf(false) }

    // Biometrics security
    var biometricUnlockEnabled by remember { mutableStateOf(true) }
    var twoFactorSmsEnabled by remember { mutableStateOf(true) }

    // Legal & Regulatory sheet state
    var activeLegalSheet by remember { mutableStateOf<LegalSheetType?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Invoices list
    val invoices = listOf(
        InvoiceItem("FX-2026-0891", "Remplacement Vanne d'Arrêt PEX", "28/09/2026", "15 000 FCFA", "2 421 FCFA (19.25%)", "Payé sous Séquestre"),
        InvoiceItem("FX-2026-0742", "Câblage Disjoncteur Divisionnaire", "14/09/2026", "20 000 FCFA", "3 228 FCFA (19.25%)", "Payé sous Séquestre"),
        InvoiceItem("FX-2026-0610", "Entretien & Désembouage Split AC", "02/09/2026", "18 000 FCFA", "2 905 FCFA (19.25%)", "Payé sous Séquestre")
    )

    var expandedFaqIndex by remember { mutableStateOf<Int?>(-1) }

    val faqs = listOf(
        FaqItem(
            if (language == AppLanguage.FR) "Comment fonctionne le séquestre FIXO ?" else "How does FIXO Escrow work?",
            if (language == AppLanguage.FR)
                "Vos fonds sont conservés dans un coffre numérique sécurisé. L'artisan ne reçoit son paiement qu'une fois les travaux terminés et validés par votre scan du QR code ou saisie du code PIN."
            else
                "Your funds are held in a secure digital vault. The artisan only receives payment once work is fully inspected and approved via your QR scan or PIN authorization."
        ),
        FaqItem(
            if (language == AppLanguage.FR) "Pourquoi ne faut-il jamais payer en espèces ?" else "Why must you never pay cash?",
            if (language == AppLanguage.FR)
                "Tout versement d'argent liquide sur place est strictement interdit par la charte FIXO et annule automatiquement la garantie Fixo Shield 14 jours ainsi que tout droit d'arbitrage."
            else
                "Paying cash on site is strictly prohibited by FIXO terms. Cash payments automatically void your 14-day Fixo Shield warranty and dispute protection."
        ),
        FaqItem(
            if (language == AppLanguage.FR) "Comment faire valoir la garantie 14 jours ?" else "How to claim the 14-day warranty?",
            if (language == AppLanguage.FR)
                "Dans votre historique d'interventions terminées, cliquez sur 'Signaler une récidive sous garantie'. La réintervention de l'artisan est 100% gratuite."
            else
                "In your completed jobs history, tap 'Report issue under warranty'. The artisan's follow-up intervention is 100% free of charge."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == AppLanguage.FR) "Paramètres & Gestion de Compte" else "Settings & Account Management",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("customer_settings_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FixoWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FixoBgCanvas)
            )
        },
        containerColor = FixoBgCanvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // 1. Profil & Coordonnées
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Informations Personnelles" else "Personal Information",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nom complet") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoGold500,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedTextColor = FixoWhite,
                                unfocusedTextColor = FixoWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email de facturation") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoGold500,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedTextColor = FixoWhite,
                                unfocusedTextColor = FixoWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Numéro Mobile Money principal (+237)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoGold500,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedTextColor = FixoWhite,
                                unfocusedTextColor = FixoWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = altPhone,
                            onValueChange = { altPhone = it },
                            label = { Text("Numéro d'urgence alternatif (+237)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoGold500,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedTextColor = FixoWhite,
                                unfocusedTextColor = FixoWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onUpdateProfile(name, email, phone, altPhone)
                                Toast.makeText(context, if (language == AppLanguage.FR) "Profil mis à jour" else "Profile updated", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = Color(0xFF080C15)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_profile_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enregistrer les modifications", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Carnet d'Adresses Cameroun Réel
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = FixoGold500)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == AppLanguage.FR) "Carnet d'Adresses (Douala)" else "Address Book (Douala)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FixoWhite
                                    )
                                )
                            }

                            IconButton(onClick = { showAddAddress = !showAddAddress }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Address", tint = FixoGold500)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        favoriteAddresses.forEachIndexed { index, addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF141920))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = addr.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = FixoGold500
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "• ${addr.quarter}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = FixoWhite)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = addr.landmark,
                                        style = MaterialTheme.typography.bodySmall.copy(color = FixoTextSecondary)
                                    )
                                }

                                if (favoriteAddresses.size > 1) {
                                    IconButton(
                                        onClick = { favoriteAddresses.removeAt(index) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = FixoRed500, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = showAddAddress) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                HorizontalDivider(color = FixoBorderSubtle)
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = newAddressTitle,
                                    onValueChange = { newAddressTitle = it },
                                    label = { Text("Titre (ex: Résidence, Chantier)") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = newAddressQuarter,
                                    onValueChange = { newAddressQuarter = it },
                                    label = { Text("Quartier (ex: Bonamoussadi, Deïdo)") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = newAddressLandmark,
                                    onValueChange = { newAddressLandmark = it },
                                    label = { Text("Repère visuel (ex: Face Total Énergie)") },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (newAddressTitle.isNotBlank() && newAddressQuarter.isNotBlank()) {
                                            favoriteAddresses.add(FavoriteAddress(newAddressTitle, newAddressQuarter, newAddressLandmark))
                                            newAddressTitle = ""
                                            newAddressQuarter = ""
                                            newAddressLandmark = ""
                                            showAddAddress = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = Color(0xFF080C15)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ajouter cette adresse", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Moyens de Paiement Liés & Facturation Certifiée FIXO
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Moyens de Paiement & Factures" else "Payment & Invoicing",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Comptes Liés MoMo & Orange
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF141920))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("MTN Mobile Money", fontWeight = FontWeight.Bold, color = FixoWhite, fontSize = 13.sp)
                                Text("+237 677 *** 900", color = FixoTextSecondary, fontSize = 11.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FixoSuccessGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FixoSuccessGreen)
                            ) {
                                Text("✓ Validé", fontSize = 11.sp, color = FixoSuccessGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF141920))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Orange Money Cameroun", fontWeight = FontWeight.Bold, color = FixoWhite, fontSize = 13.sp)
                                Text("+237 699 *** 233", color = FixoTextSecondary, fontSize = 11.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FixoSuccessGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FixoSuccessGreen)
                            ) {
                                Text("✓ Validé", fontSize = 11.sp, color = FixoSuccessGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Factures Récentes (Mention Séquestre & TVA 19.25%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        invoices.forEach { inv ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F141C))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FixoWhite)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("• ${inv.date}", fontSize = 10.sp, color = FixoTextSecondary)
                                    }
                                    Text(inv.serviceTitle, fontSize = 11.sp, color = FixoTextSecondary)
                                    Text("${inv.amountTtc} TTC (dont ${inv.vatAmount})", fontSize = 10.sp, color = FixoGold500, fontWeight = FontWeight.SemiBold)
                                }

                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "Téléchargement PDF de la facture ${inv.invoiceNumber}...", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download PDF", tint = FixoGold500, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 4. Sécurité & Biométrie pour Libération Séquestre
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Sécurité & Biométrie" else "Security & Biometrics",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Empreinte / Face Unlock Séquestre", fontWeight = FontWeight.SemiBold, color = FixoWhite, fontSize = 13.sp)
                                Text("Exiger la validation biométrique pour libérer les fonds à l'artisan", color = FixoTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = biometricUnlockEnabled,
                                onCheckedChange = { biometricUnlockEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = FixoGold500, checkedTrackColor = FixoGold500.copy(alpha = 0.5f))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = FixoBorderSubtle)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Authentification 2FA SMS", fontWeight = FontWeight.SemiBold, color = FixoWhite, fontSize = 13.sp)
                                Text("Code OTP requis pour toute modification de numéro ou retrait", color = FixoTextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = twoFactorSmsEnabled,
                                onCheckedChange = { twoFactorSmsEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = FixoGold500, checkedTrackColor = FixoGold500.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }

            // 5. Section "About FIXO" & Réglementations
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("about_fixo_section_card")
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "À Propos de FIXO (About FIXO)" else "About FIXO Platform",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Ligne 1 : About FIXO Platform
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF141920),
                            onClick = {
                                Log.d("FIXO_CLICK", "Clic About FIXO Platform")
                                activeLegalSheet = LegalSheetType.ABOUT_PLATFORM
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("about_fixo_platform_row")
                                .clickable {
                                    Log.d("FIXO_CLICK", "Clic About FIXO Platform (modifier)")
                                    activeLegalSheet = LegalSheetType.ABOUT_PLATFORM
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("About FIXO Platform", fontWeight = FontWeight.Bold, color = FixoWhite, fontSize = 13.sp)
                                    Text("Plateforme Agréée République du Cameroun • Tiers de confiance", color = FixoGold500, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null, tint = FixoGold500)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Ligne 2 : Terms of Service & Escrow
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF141920),
                            onClick = {
                                Log.d("FIXO_CLICK", "Clic Terms of Service & Escrow")
                                activeLegalSheet = LegalSheetType.TERMS_ESCROW
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("about_fixo_terms_row")
                                .clickable {
                                    Log.d("FIXO_CLICK", "Clic Terms of Service & Escrow (modifier)")
                                    activeLegalSheet = LegalSheetType.TERMS_ESCROW
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Terms of Service & Escrow", fontWeight = FontWeight.Bold, color = FixoWhite, fontSize = 13.sp)
                                    Text("Articles 1 à 4 : Mandat zéro cash, séquestre & QR de clôture", color = FixoTextSecondary, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null, tint = FixoGold500)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Ligne 3 : Data Privacy (CEMAC)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF141920),
                            onClick = {
                                Log.d("FIXO_CLICK", "Clic Data Privacy (CEMAC)")
                                activeLegalSheet = LegalSheetType.DATA_PRIVACY
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("about_fixo_privacy_row")
                                .clickable {
                                    Log.d("FIXO_CLICK", "Clic Data Privacy (CEMAC) (modifier)")
                                    activeLegalSheet = LegalSheetType.DATA_PRIVACY
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Data Privacy (CEMAC)", fontWeight = FontWeight.Bold, color = FixoWhite, fontSize = 13.sp)
                                    Text("Directives CEMAC / MINPOSTEL, chiffrement AES-256 & géoloc", color = FixoTextSecondary, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null, tint = FixoGold500)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Ligne 4 : Garantie Fixo Shield 14 Jours
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF141920),
                            onClick = {
                                Log.d("FIXO_CLICK", "Clic Garantie Fixo Shield 14 Jours")
                                activeLegalSheet = LegalSheetType.SHIELD
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("about_fixo_shield_row")
                                .clickable {
                                    Log.d("FIXO_CLICK", "Clic Garantie Fixo Shield 14 Jours (modifier)")
                                    activeLegalSheet = LegalSheetType.SHIELD
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Garantie Fixo Shield 14 Jours", fontWeight = FontWeight.Bold, color = FixoSuccessGreen, fontSize = 13.sp)
                                    Text("Prise en charge malfaçon 300 000 FCFA & réintervention", color = FixoTextSecondary, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null, tint = FixoSuccessGreen)
                            }
                        }
                    }
                }
            }

            // 6. Langue & Suppression de Compte
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = FixoGold500)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == AppLanguage.FR) "Langue de l'application" else "App Language",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = FixoWhite)
                                )
                            }
                            OutlinedButton(
                                onClick = onToggleLanguage,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("toggle_language_btn")
                            ) {
                                Text(text = if (language == AppLanguage.FR) "EN (Switch)" else "FR (Changer)", color = FixoGold500, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = FixoBorderSubtle)
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = onDeleteAccount,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoRed500),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("delete_account_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = FixoRed500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Supprimer définitivement mon compte", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Bottom Sheet pour Textes Juridiques Complets & Présentation FIXO
    if (activeLegalSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { activeLegalSheet = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        ) {
            when (activeLegalSheet) {
                LegalSheetType.ABOUT_PLATFORM -> AboutPlatformContent(onClose = { activeLegalSheet = null })
                LegalSheetType.TERMS_ESCROW -> TermsAndEscrowContent(onClose = { activeLegalSheet = null })
                LegalSheetType.DATA_PRIVACY -> DataPrivacyCemacContent(onClose = { activeLegalSheet = null })
                LegalSheetType.SHIELD -> ShieldWarrantyContent(onClose = { activeLegalSheet = null })
                null -> {}
            }
        }
    }
}

@Composable
fun AboutPlatformContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_fixo_logo),
                contentDescription = "Logo FIXO",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("FIXO Cameroun", fontSize = 20.sp, fontWeight = FontWeight.Black, color = FixoWhite)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FixoGold500.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500)
                ) {
                    Text(
                        text = "Plateforme Agréée République du Cameroun",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Présentation de la Mission :\n" +
                    "FIXO est le premier tiers de confiance numérique du Cameroun dédié au dépannage et à la maintenance technique à domicile. Notre protocole exclut rigoureusement tout paiement en espèces et garantit la parfaite exécution de chaque chantier grâce à notre coffre de séquestre numérique et notre audit contradictoire systématique.\n\n" +
                    "Chiffres Clés de la Plateforme :\n" +
                    "• +150 Maîtres Artisans rigoureusement audités (CNI, Casier Judiciaire, Diplôme CQP).\n" +
                    "• Temps moyen d'intervention sur site < 25 minutes sur Douala et Yaoundé.\n" +
                    "• 100% des transactions sous compte séquestre certifié CEMAC / BEAC.\n" +
                    "• Garantie décennale et couverture malfaçon Fixo Shield jusqu'à 300 000 FCFA.\n\n" +
                    "Coordonnées Officielles & Siège :\n" +
                    "Direction des Opérations FIXO Technologies SARL\n" +
                    "Immeuble Le Quartz, Boulevard de la Liberté, Akwa, Douala, République du Cameroun.\n" +
                    "Assistance & Ligne d'Urgence : +237 670 000 000 / contact@fixo.cm",
            color = Color(0xFFCBD5E1),
            fontSize = 13.sp,
            lineHeight = 21.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = Color(0xFF080C15)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("J'ai compris et j'accepte", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TermsAndEscrowContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text("Terms of Service & Escrow Vault", fontSize = 18.sp, fontWeight = FontWeight.Black, color = FixoWhite)
        Text("Contrat-Cadre de Prestation & Séquestre Numérique BEAC / CEMAC", fontSize = 12.sp, color = FixoGold500, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Article 1 : Rôle d'intermédiaire technique et neutralité d'arbitrage\n" +
                    "FIXO opère exclusivement en qualité de tiers de confiance technique et séquestre financier neutre. FIXO n'est pas l'employeur des artisans indépendants homologués mais garantit le cadre contradictoire et impartial de la réalisation des prestations.\n\n" +
                    "Article 2 : Fonctionnement du compte séquestre (Escrow Vault) et mandat zéro cash\n" +
                    "Lors de la commande, le montant forfaitaire convenu est consigné et gelé dans le coffre séquestre. Aucun versement en espèces direct n'est autorisé. Tout paiement en liquide sur place entraîne la déchéance immédiate de la garantie et la résiliation des comptes.\n\n" +
                    "Article 3 : Conditions d'homologation des ouvriers (CNI, Casier Judiciaire, Diplôme)\n" +
                    "Chaque artisan partenaire doit justifier d'une Carte Nationale d'Identité camerounaise valide, d'un extrait de casier judiciaire (Bulletin n°3) vierge datant de moins de 3 mois et d'un titre de qualification professionnelle (CQP, CAP, BT).\n\n" +
                    "Article 4 : Protocole de libération par QR Code optique et code PIN de secours\n" +
                    "Les fonds consignés sous séquestre ne peuvent être transférés sur le portefeuille de l'artisan que suite au scan physique du QR Code de clôture présenté sur le smartphone du client ou la validation du code PIN à 4 chiffres.",
            color = Color(0xFFCBD5E1),
            fontSize = 13.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = Color(0xFF080C15)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("J'ai compris et j'accepte", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DataPrivacyCemacContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text("Data Privacy & Souveraineté des Données (CEMAC)", fontSize = 18.sp, fontWeight = FontWeight.Black, color = FixoWhite)
        Text("Conformité Règlementale MINPOSTEL, ANTIC & Directives CEMAC", fontSize = 12.sp, color = FixoGold500, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "1. Conformité CEMAC & MINPOSTEL :\n" +
                    "Le stockage et le traitement des données des utilisateurs respectent strictement les directives CEMAC relatives aux transferts de fonds électroniques et la loi camerounaise sur la cybersécurité. Les données bancaires et numéros de compte Mobile Money sont chiffrés selon le protocole AES-256.\n\n" +
                    "2. Géolocalisation Restreinte à la Fenêtre de Mission :\n" +
                    "Le partage de la position GPS en temps réel de l'artisan et du client est activé uniquement entre le déclenchement du trajet et la validation finale des travaux. Aucun historique de trajectoire n'est conservé au-delà de 48 heures.\n\n" +
                    "3. Chiffrement de Bout en Bout & Anonymisation VoIP :\n" +
                    "Toutes les communications téléphoniques transitent par le standard masqué FIXO afin de protéger le numéro personnel des deux parties. Les échanges écrits et photos d'inspection sont chiffrés de bout en bout et conservés uniquement pour les besoins d'arbitrage éventuel.",
            color = Color(0xFFCBD5E1),
            fontSize = 13.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = Color(0xFF080C15)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("J'ai compris et j'accepte", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ShieldWarrantyContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text("Garantie Fixo Shield 14 Jours", fontSize = 18.sp, fontWeight = FontWeight.Black, color = FixoSuccessGreen)
        Text("Protection Contractuelle Intégrale des Chantiers", fontSize = 12.sp, color = FixoGold500, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "1. Périmètre de Couverture (Plafond 300 000 FCFA) :\n" +
                    "Toute intervention validée via QR code FIXO bénéficie d'une garantie automatique de 14 jours calendaires contre les vices d'exécution, fuites résiduelles, défauts de soudure ou pannes consécutives à la prestation.\n\n" +
                    "2. Obligation de Réintervention Gratuite :\n" +
                    "En cas de signalement de récidive dans l'application, l'artisan intervenant s'engage contractuellement à réintervenir sous 48 heures sans aucun frais supplémentaire pour le client.\n\n" +
                    "3. Médiation & Remboursement Intégral :\n" +
                    "Si la malfaçon persiste après seconde intervention, une expertise contradictoire est dépêchée sous l'égide de FIXO HQ et le montant des travaux est remboursé intégralement depuis le fonds de réserve Fixo Shield.",
            color = Color(0xFFCBD5E1),
            fontSize = 13.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = Color(0xFF080C15)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("J'ai compris et j'accepte", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
