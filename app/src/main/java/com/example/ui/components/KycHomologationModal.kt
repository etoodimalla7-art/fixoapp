package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ServiceCategory
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * Funnel d'Homologation & Dossier KYC Artisan (Écran A0)
 * Corps de métiers, upload CNI recto/verso, bulletin n°3 casier judiciaire,
 * liaison compte Mobile Money au même nom que la CNI.
 */
@Composable
fun KycHomologationModal(
    language: AppLanguage = AppLanguage.FR,
    onDismiss: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var selectedTrades = remember { mutableStateListOf(ServiceCategory.PLUMBING) }
    var cniRectoUploaded by remember { mutableStateOf(true) }
    var cniVersoUploaded by remember { mutableStateOf(true) }
    var casierJudiciaireUploaded by remember { mutableStateOf(true) }
    var attestationUploaded by remember { mutableStateOf(false) }

    var momoOperator by remember { mutableStateOf("MTN MoMo") }
    var momoPhone by remember { mutableStateOf("+237 699 876 543") }
    var accountHolderName by remember { mutableStateOf("Marc Dubois") }

    var isSubmitted by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FixoBgCanvas.copy(alpha = 0.95f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, FixoGold500, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FixoGold500.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Homologation Réseau Pro" else "Pro Network KYC Application",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextPrimary
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = FixoTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSubmitted) {
                        // Submitted Status View
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(FixoGold500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (language == AppLanguage.FR) "Dossier en cours d'examen" else "Application Under Review",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = FixoGold500
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "Votre dossier est actuellement audité par les experts FIXO. Vous recevrez une notification push dès que votre profil Pro sera activé (délai moyen : < 4h)."
                                else
                                    "Your credentials are being verified by FIXO compliance experts. You will receive a push notification as soon as approved (< 4h average).",
                                fontSize = 13.sp,
                                color = FixoTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    onSubmitSuccess()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = FixoBgCanvas)
                            ) {
                                Text(if (language == AppLanguage.FR) "Compris" else "Got It", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // STEP 1: Trades selection
                        Text(
                            text = if (language == AppLanguage.FR) "1. Corps de métiers exercés" else "1. Trades & Expertise",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val availableTrades = listOf(
                            ServiceCategory.PLUMBING to "Plomberie sanitaire",
                            ServiceCategory.ELECTRICAL to "Électricité bâtiment",
                            ServiceCategory.AC_COOLING to "Climatisation & Froid",
                            ServiceCategory.CONSTRUCTION to "Menuiserie & Serrurerie"
                        )

                        availableTrades.forEach { (cat, name) ->
                            val isChecked = selectedTrades.contains(cat)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isChecked) Color(0xFF1E293B) else Color(0xFF0F172A))
                                    .border(1.dp, if (isChecked) FixoGold500 else FixoBorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (isChecked) selectedTrades.remove(cat) else selectedTrades.add(cat)
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isChecked) FixoGold500 else Color.Transparent)
                                        .border(1.dp, if (isChecked) FixoGold500 else FixoTextSecondary, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = FixoBgCanvas, modifier = Modifier.size(14.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = name, fontSize = 13.sp, color = FixoTextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // STEP 2: Secure Document Uploads
                        Text(
                            text = if (language == AppLanguage.FR) "2. Pièces justificatives obligatoires" else "2. Mandatory Verification Documents",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Upload item helper
                        listOf(
                            Triple("CNI Recto (Photo nette)", cniRectoUploaded) { cniRectoUploaded = !cniRectoUploaded },
                            Triple("CNI Verso (Photo nette)", cniVersoUploaded) { cniVersoUploaded = !cniVersoUploaded },
                            Triple("Extrait de Casier Judiciaire (Bulletin n°3)", casierJudiciaireUploaded) { casierJudiciaireUploaded = !casierJudiciaireUploaded },
                            Triple("Attestations de formation / Diplôme", attestationUploaded) { attestationUploaded = !attestationUploaded }
                        ).forEach { (docTitle, isUploaded, onToggle) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, if (isUploaded) FixoSuccessGreen.copy(alpha = 0.6f) else FixoBorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable { onToggle() }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                        contentDescription = null,
                                        tint = if (isUploaded) FixoSuccessGreen else FixoTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = docTitle, fontSize = 12.sp, color = FixoTextPrimary)
                                }
                                Text(
                                    text = if (isUploaded) (if (language == AppLanguage.FR) "Téléversé ✓" else "Uploaded ✓") else (if (language == AppLanguage.FR) "+ Joindre" else "+ Attach"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUploaded) FixoSuccessGreen else FixoGold500
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // STEP 3: Mobile Money Account Linking (Must match CNI)
                        Text(
                            text = if (language == AppLanguage.FR) "3. Compte Mobile Money de Retrait" else "3. Mobile Money Payout Account",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoGold500
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Le compte doit obligatoirement être au même nom que votre CNI." else "Account holder name must match your National ID exactly.",
                            fontSize = 11.sp,
                            color = FixoTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("MTN MoMo", "Orange Money").forEach { op ->
                                val isSel = momoOperator == op
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF1E293B) else Color(0xFF0F172A))
                                        .border(1.dp, if (isSel) FixoGold500 else FixoBorderSubtle, RoundedCornerShape(8.dp))
                                        .clickable { momoOperator = op }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = op,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) FixoGold500 else FixoTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = momoPhone,
                            onValueChange = { momoPhone = it },
                            label = { Text("Numéro Mobile Money (+237)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoGold500,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = accountHolderName,
                            onValueChange = { accountHolderName = it },
                            label = { Text("Nom du Titulaire (Identique CNI)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoGold500,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                isSubmitted = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("submit_kyc_application_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FixoGold500,
                                contentColor = FixoBgCanvas
                            )
                        ) {
                            Text(
                                text = if (language == AppLanguage.FR) "Soumettre Mon Dossier d'Homologation" else "Submit KYC Credentials",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
