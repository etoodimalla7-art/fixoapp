package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

data class FaqItem(
    val question: String,
    val answer: String
)

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
    var name by remember { mutableStateOf(user?.name ?: "Sarah Jenkins") }
    var email by remember { mutableStateOf(user?.email ?: "sarah.jenkins@gmail.com") }
    var phone by remember { mutableStateOf(user?.phone ?: "+237 677 889 900") }
    var altPhone by remember { mutableStateOf(user?.secondaryPhone ?: "+237 699 112 233") }

    val favoriteAddresses = remember {
        mutableStateListOf(
            FavoriteAddress("Domicile", "Akwa (Rue Drouot)", "Face Boulangerie Zepol"),
            FavoriteAddress("Bureau", "Bonanjo", "Immeuble CAA, 3ème étage")
        )
    }

    var newAddressTitle by remember { mutableStateOf("") }
    var newAddressQuarter by remember { mutableStateOf("") }
    var newAddressLandmark by remember { mutableStateOf("") }
    var showAddAddress by remember { mutableStateOf(false) }

    var expandedFaqIndex by remember { mutableStateOf<Int?>(-1) }
    var showCguDialog by remember { mutableStateOf(false) }

    val faqs = listOf(
        FaqItem(
            "Comment fonctionne le séquestre FIXO ?",
            "Vos fonds sont conservés dans un coffre numérique sécurisé. L'artisan ne reçoit son paiement qu'une fois les travaux terminés et validés par votre scan du QR code ou saisie du code PIN."
        ),
        FaqItem(
            "Pourquoi ne faut-il jamais payer en espèces ?",
            "Tout versement d'argent liquide sur place est strictement interdit par la charte FIXO et annule automatiquement la garantie Fixo Shield 14 jours ainsi que l'arbitrage."
        ),
        FaqItem(
            "Comment faire valoir la garantie 14 jours ?",
            "Dans votre historique d'interventions terminées, cliquez sur 'Signaler une récidive sous garantie'. La réintervention de l'artisan est 100% gratuite."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paramètres & Sécurité",
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
                                text = "Informations Personnelles",
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
                            label = { Text("Numéro Mobile Money principal") },
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
                            label = { Text("Numéro d'urgence alternatif") },
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
                            onClick = { onUpdateProfile(name, email, phone, altPhone) },
                            colors = ButtonDefaults.buttonColors(containerColor = FixoGold500),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_profile_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enregistrer les modifications", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Carnet d'Adresses GPS Favorites
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
                                    text = "Carnet d'Adresses GPS",
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

                        Spacer(modifier = Modifier.height(10.dp))

                        favoriteAddresses.forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF141920))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = addr.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = FixoGold500
                                        )
                                    )
                                    Text(
                                        text = addr.quarter,
                                        style = MaterialTheme.typography.bodySmall.copy(color = FixoWhite)
                                    )
                                    Text(
                                        text = "📍 Repère : ${addr.landmark}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                                    )
                                }
                                IconButton(onClick = { favoriteAddresses.remove(addr) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = FixoRed500)
                                }
                            }
                        }

                        if (showAddAddress) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = newAddressTitle,
                                onValueChange = { newAddressTitle = it },
                                placeholder = { Text("Libellé (ex: Atelier, Résidence)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newAddressQuarter,
                                onValueChange = { newAddressQuarter = it },
                                placeholder = { Text("Quartier (ex: Makepe, Bonapriso)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newAddressLandmark,
                                onValueChange = { newAddressLandmark = it },
                                placeholder = { Text("Repère visuel (ex: Face Pharmacie)") },
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
                                colors = ButtonDefaults.buttonColors(containerColor = FixoGold500),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ajouter cette adresse", color = Color.Black)
                            }
                        }
                    }
                }
            }

            // 3. Sélecteur Bilingue Persistant
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Langue de l'application",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FixoWhite
                                    )
                                )
                                Text(
                                    text = if (language == AppLanguage.FR) "Français (Actif)" else "English (Active)",
                                    style = MaterialTheme.typography.bodySmall.copy(color = FixoTextSecondary)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onToggleLanguage,
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FixoGold500, FixoGold500))),
                            modifier = Modifier.testTag("toggle_language_btn")
                        ) {
                            Text(
                                text = if (language == AppLanguage.FR) "EN" else "FR",
                                color = FixoGold500,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 4. FAQ Interactive
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Questions Fréquentes (FAQ)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        faqs.forEachIndexed { index, faq ->
                            val isExpanded = expandedFaqIndex == index
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF141920))
                                    .clickable {
                                        expandedFaqIndex = if (isExpanded) null else index
                                    }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = faq.question,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = FixoWhite
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = FixoGold500
                                    )
                                }
                                AnimatedVisibility(visible = isExpanded) {
                                    Text(
                                        text = faq.answer,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = FixoTextSecondary,
                                            lineHeight = 18.sp
                                        ),
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Mentions Légales & Suppression de Compte
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mentions Légales & Conformité",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Conditions Générales d'Utilisation adaptées au droit camerounais (Loi n°2010/012 relative à la cybersécurité et cybercriminalité au Cameroun). Protection des données à caractère personnel garantie.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = FixoTextSecondary,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = onDeleteAccount,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FixoRed500),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FixoRed500, FixoRed500))),
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
}
