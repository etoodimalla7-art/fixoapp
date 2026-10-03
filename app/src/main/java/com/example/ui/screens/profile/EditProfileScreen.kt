package com.example.ui.screens.profile

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

val DOUALA_QUARTERS = listOf(
    "Akwa",
    "Bonanjo",
    "Deido",
    "Bonamoussadi",
    "Makepe",
    "Bali",
    "Kotto",
    "Bonapriso",
    "Denver",
    "Bassa"
)

/**
 * CHANTIER IDENTITÉ : Écran Complet de Modification de Profil
 * Remplaçant le dialogue flottant étriqué par un écran grand format à 100% de hauteur.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: User?,
    language: AppLanguage = AppLanguage.FR,
    onSaveProfile: (name: String, email: String, phone: String, city: String, landmark: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(user?.name ?: "Sarah Jenkins") }
    var phoneInput by remember { mutableStateOf(formatCameroonPhone(user?.phone ?: "670112233")) }
    var email by remember { mutableStateOf(user?.email ?: "sarah.jenkins@gmail.com") }
    var selectedQuarter by remember { mutableStateOf(user?.quarter?.ifEmpty { "Akwa" } ?: "Akwa") }
    var landmark by remember { mutableStateOf("Portail noir face Boulangerie Zepol, Rue Drouot") }
    var isQuarterDropdownExpanded by remember { mutableStateOf(false) }

    // Validation
    val isNameValid = name.trim().length >= 3
    val isPhoneValid = phoneInput.replace(" ", "").length >= 9
    val isLandmarkValid = landmark.trim().length >= 5
    val canSubmit = isNameValid && isPhoneValid && isLandmarkValid

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(FixoBgCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("edit_profile_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == AppLanguage.FR) "Modifier mon Profil" else "Edit Profile",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoTextPrimary
                        )
                        Text(
                            text = if (language == AppLanguage.FR) "Coordonnées d'intervention à Douala" else "Douala service contact details",
                            fontSize = 11.sp,
                            color = FixoTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_edit_profile")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = FixoGold500
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FixoBgCanvas)
            )
        },
        containerColor = FixoBgCanvas
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Intro Callout Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = FixoSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(FixoGold500.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = FixoGold500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Vos coordonnées et repères précis permettent aux artisans d'arriver sous 15 minutes sans errer dans le quartier."
                        else
                            "Accurate contact and local landmarks allow technicians to reach your location within 15 minutes.",
                        fontSize = 12.sp,
                        color = FixoTextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            // 1. Full Name
            Column {
                Text(
                    text = if (language == AppLanguage.FR) "Nom & Prénom *" else "Full Name *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FixoGold500) },
                    placeholder = { Text("ex: Sarah Jenkins") },
                    singleLine = true,
                    isError = !isNameValid && name.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = FixoBorderSubtle,
                        focusedContainerColor = FixoSurfaceCard,
                        unfocusedContainerColor = FixoSurfaceCard
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_name")
                )
                if (!isNameValid && name.isNotBlank()) {
                    Text("Le nom doit contenir au moins 3 caractères", color = FixoRed500, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
                }
            }

            // 2. Phone (with Cameroon format)
            Column {
                Text(
                    text = if (language == AppLanguage.FR) "Numéro de Téléphone (MTN / Orange) *" else "Phone Number (Cameroon) *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { raw ->
                        phoneInput = formatCameroonPhone(raw)
                    },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FixoGold500) },
                    placeholder = { Text("+237 6XX XX XX XX") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = FixoBorderSubtle,
                        focusedContainerColor = FixoSurfaceCard,
                        unfocusedContainerColor = FixoSurfaceCard
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_phone")
                )
                Text(
                    text = if (language == AppLanguage.FR) "Format national : +237 6XX XX XX XX" else "National format: +237 6XX XX XX XX",
                    fontSize = 11.sp,
                    color = FixoTextSecondary,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            // 3. Quarter Dropdown Selector
            Column {
                Text(
                    text = if (language == AppLanguage.FR) "Quartier de Résidence (Douala) *" else "Neighborhood (Douala) *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FixoSurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isQuarterDropdownExpanded = true }
                            .testTag("dropdown_edit_quarter")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "$selectedQuarter, Douala",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FixoTextPrimary
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = FixoGold500)
                        }
                    }

                    DropdownMenu(
                        expanded = isQuarterDropdownExpanded,
                        onDismissRequest = { isQuarterDropdownExpanded = false },
                        modifier = Modifier
                            .background(FixoSurfaceCard)
                            .border(1.dp, FixoBorderSubtle, RoundedCornerShape(8.dp))
                    ) {
                        DOUALA_QUARTERS.forEach { quarter ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "$quarter, Douala",
                                        fontWeight = if (selectedQuarter == quarter) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedQuarter == quarter) FixoGold500 else FixoTextPrimary
                                    )
                                },
                                onClick = {
                                    selectedQuarter = quarter
                                    isQuarterDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 4. Local Landmark (Repère d'accès camerounais obligatoire)
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (language == AppLanguage.FR) "Repère d'accès au domicile *" else "Access Landmark (Cameroon) *",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = FixoGold500.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "INDISPENSABLE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = FixoGold500,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = landmark,
                    onValueChange = { landmark = it },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = FixoGold500) },
                    placeholder = { Text("ex: Portail noir face Boulangerie Zepol") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = FixoBorderSubtle,
                        focusedContainerColor = FixoSurfaceCard,
                        unfocusedContainerColor = FixoSurfaceCard
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_landmark")
                )
                Text(
                    text = if (language == AppLanguage.FR)
                        "Précisez carrefour, commerce ou couleur du portail."
                    else
                        "Specify nearest bakery, junction or gate color.",
                    fontSize = 11.sp,
                    color = FixoTextSecondary,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Save Action Button
            Button(
                onClick = {
                    if (canSubmit) {
                        val fullCity = "$selectedQuarter, Douala"
                        onSaveProfile(name.trim(), email.trim(), phoneInput.trim(), fullCity, landmark.trim())
                    }
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_profile_changes"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FixoGold500,
                    contentColor = Color(0xFF080C15),
                    disabledContainerColor = Color(0xFF334155),
                    disabledContentColor = Color(0xFF94A3B8)
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.FR) "Enregistrer les modifications" else "Save Changes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Formats a Cameroon phone number into "+237 6XX XX XX XX"
 */
private fun formatCameroonPhone(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    val clean = when {
        digits.startsWith("237") -> digits.removePrefix("237")
        else -> digits
    }
    return when {
        clean.isEmpty() -> "+237 "
        clean.length <= 3 -> "+237 $clean"
        clean.length <= 5 -> "+237 ${clean.substring(0, 3)} ${clean.substring(3)}"
        clean.length <= 7 -> "+237 ${clean.substring(0, 3)} ${clean.substring(3, 5)} ${clean.substring(5)}"
        else -> {
            val p1 = clean.substring(0, 3)
            val p2 = clean.substring(3, 5.coerceAtMost(clean.length))
            val p3 = clean.substring(5, 7.coerceAtMost(clean.length))
            val p4 = clean.substring(7, 9.coerceAtMost(clean.length))
            "+237 $p1 $p2 $p3 $p4"
        }
    }
}
