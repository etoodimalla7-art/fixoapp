package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoStatusRed
import com.example.ui.theme.FixoTheme

val DISPUTE_MOTIFS_FR = listOf(
    "Retard > 2h",
    "Malfaçon constatée",
    "Non-conformité du devis",
    "Comportement inadapté"
)

val DISPUTE_MOTIFS_EN = listOf(
    "Delay > 2h",
    "Defective workmanship",
    "Quote discrepancy",
    "Inappropriate behavior"
)

/**
 * MODAL BOTTOM SHEET RÉACTIVE : FORMULAIRE DE LITIGE & ARBITRAGE SÉQUESTRE
 *
 * Spécifications :
 * - Feuille coulissante bilingue (Français par défaut).
 * - Titre : « Signalement d'Anomalie & Arbitrage Séquestre ».
 * - Choix du motif (Retard > 2h, Malfaçon constatée, Non-conformité du devis).
 * - Champ de description détaillé et zone d'importation de photo contradictoire.
 * - Bouton d'action rouge carmin : [ Transmettre au Comité d'Arbitrage Fixo Shield ].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisputeResolutionBottomSheet(
    onDismissRequest: () -> Unit,
    onSubmit: (reason: String, motif: String, photoUrl: String?) -> Unit,
    language: AppLanguage = AppLanguage.FR,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val isDark = FixoTheme.isDark
    val containerBg = if (isDark) Color(0xFF111827) else Color(0xFFFFFFFF)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)

    val motifs = if (language == AppLanguage.FR) DISPUTE_MOTIFS_FR else DISPUTE_MOTIFS_EN
    var selectedMotif by remember { mutableStateOf(motifs[1]) } // Malfaçon par défaut
    var description by remember {
        mutableStateOf(
            if (language == AppLanguage.FR)
                "Constat de non-conformité sur les travaux effectués. Fuite résiduelle ou matériel non posé selon les règles de l'art."
            else
                "Observed non-compliance on completed work. Residual leak or materials not installed according to trade standards."
        )
    }
    var attachedPhotoUrl by remember { mutableStateOf<String?>("https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=500") }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = containerBg,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
            )
        },
        modifier = Modifier
            .imePadding()
            .testTag("dispute_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header with Shield & Escrow Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = FixoStatusRed.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = FixoStatusRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Signalement d'Anomalie & Arbitrage Séquestre"
                        else
                            "Report Issue & Escrow Arbitration",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Mandat de protection Fixo Shield • Décision sous 48h"
                        else
                            "Fixo Shield Protection • Ruling within 48h",
                        fontSize = 11.sp,
                        color = textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Escrow Frozen Notice Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF3B1212) else Color(0xFFFEF2F2),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF7F1D1D) else Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = FixoStatusRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Les fonds restent gelés sous séquestre Fixo Vault durant toute la procédure d'arbitrage contradictoire."
                        else
                            "All funds remain locked in Fixo Escrow Vault throughout the contradictory arbitration procedure.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFFECACA) else Color(0xFF991B1B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Motif Selection Chips
            Text(
                text = if (language == AppLanguage.FR) "Motif principal du litige :" else "Primary Dispute Reason:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                motifs.take(2).forEach { motif ->
                    val isSelected = selectedMotif == motif
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMotif = motif },
                        label = { Text(motif, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FixoStatusRed.copy(alpha = 0.2f),
                            selectedLabelColor = FixoStatusRed
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) FixoStatusRed else if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                motifs.drop(2).forEach { motif ->
                    val isSelected = selectedMotif == motif
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMotif = motif },
                        label = { Text(motif, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FixoStatusRed.copy(alpha = 0.2f),
                            selectedLabelColor = FixoStatusRed
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) FixoStatusRed else if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Description Field
            Text(
                text = if (language == AppLanguage.FR) "Description détaillée des faits :" else "Detailed Factual Description:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dispute_reason_input"),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FixoStatusRed,
                    unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary
                ),
                placeholder = {
                    Text(
                        if (language == AppLanguage.FR)
                            "Précisez la nature exacte du problème constaté..."
                        else
                            "Explain exactly what issue occurred on site...",
                        color = textSecondary
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Photo Evidence Upload Area
            Text(
                text = if (language == AppLanguage.FR) "Preuve photographique contradictoire :" else "Photographic Evidence:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Toggle sample photo
                        attachedPhotoUrl = if (attachedPhotoUrl == null)
                            "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=500"
                        else
                            null
                    },
                shape = RoundedCornerShape(12.dp),
                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
            ) {
                if (attachedPhotoUrl != null) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = attachedPhotoUrl,
                            contentDescription = "Photo justificative",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == AppLanguage.FR) "Constat_Chantier_01.jpg" else "Site_Inspection_01.jpg",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = if (language == AppLanguage.FR) "Preuve horodatée attachée ✓" else "Timestamped proof attached ✓",
                                fontSize = 10.sp,
                                color = FixoStatusRed
                            )
                        }
                        Text(
                            text = if (language == AppLanguage.FR) "Modifier" else "Change",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FixoGold500
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = FixoGold500,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.FR)
                                "Ajouter une photo du problème (Recommandé)"
                            else
                                "Attach Photo of Issue (Recommended)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button: Rouge Carmin [ Transmettre au Comité d'Arbitrage Fixo Shield ]
            Button(
                onClick = {
                    val fullReason = "[$selectedMotif] $description"
                    onSubmit(fullReason, selectedMotif, attachedPhotoUrl)
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_dispute_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FixoStatusRed,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.FR)
                        "Transmettre au Comité d'Arbitrage Fixo Shield"
                    else
                        "Submit to Fixo Shield Arbitration Committee",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
