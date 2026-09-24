package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoElectricAmberDark
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

data class ProblemPill(
    val id: String,
    val labelFr: String,
    val labelEn: String,
    val emoji: String,
    val queryKeywords: List<String>
)

val standardProblemPills = listOf(
    ProblemPill("water_leak", "Fuite d'eau", "Water Leak", "🚰", listOf("fuite", "eau", "robinet", "lavabo", "tuyau", "plomberie", "leak")),
    ProblemPill("circuit_short", "Court-circuit", "Short Circuit", "⚡", listOf("court-circuit", "disjoncteur", "compteur", "prise", "electricite", "power", "breaker")),
    ProblemPill("ac_broken", "Panne de climatiseur", "AC Breakdown", "❄️", listOf("clim", "froid", "climatiseur", "gaz", "hvac", "ac")),
    ProblemPill("lock_stuck", "Serrure bloquée", "Stuck Lock", "🚪", listOf("serrure", "porte", "verrou", "clef", "cle", "lock")),
    ProblemPill("crack_masonry", "Fissure / Maçonnerie", "Crack / Masonry", "🧱", listOf("fissure", "maconnerie", "beton", "mur", "ciment", "masonry")),
    ProblemPill("carpentry_wood", "Meuble / Menuiserie", "Furniture / Wood", "🛋️", listOf("meuble", "menuiserie", "bois", "porte", "placard", "carpenter"))
)

/**
 * CHANTIER 3 - Recherche Sémantique & Diagnostic Express
 * 1. Barre de Recherche Universelle (50 dp) avec mapping sémantique local
 * 2. Bannière FIXO Flash ⚡ (Urgence < 30 Min) avec contour lumineux ambre (#FFB800)
 * 3. Sélecteur de Pannes par Pilules Tactiles (Horizontal Scroll, 40 dp, coins 20 dp, fond #1A2232)
 */
@Composable
fun ProblemDiagnosticSection(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    isFlashUrgentActive: Boolean,
    onToggleFlashUrgent: () -> Unit,
    selectedPillId: String?,
    onPillSelected: (ProblemPill?) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("problem_diagnostic_section")
    ) {
        // 1. Barre de Recherche Universelle (50 dp)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("semantic_search_bar"),
            placeholder = {
                Text(
                    text = if (language == AppLanguage.FR)
                        "Décrivez votre problème (ex: disjoncteur saute, fuite lavabo)..."
                    else
                        "Describe your issue (e.g. breaker trips, sink leak)...",
                    fontSize = 12.sp,
                    color = FixoTextMuted,
                    maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = FixoElectricAmber,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = FixoTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FixoElectricAmber,
                unfocusedBorderColor = FixoBorderSubtle,
                focusedContainerColor = FixoSurfaceCard,
                unfocusedContainerColor = FixoSurfaceCard,
                focusedTextColor = FixoTextPrimary,
                unfocusedTextColor = FixoTextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Bannière FIXO Flash ⚡ (Urgence < 30 Min)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isFlashUrgentActive) 2.dp else 1.2.dp,
                    color = if (isFlashUrgentActive) FixoElectricAmber else FixoElectricAmber.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
                .clickable { onToggleFlashUrgent() }
                .testTag("fixo_flash_urgent_banner"),
            colors = CardDefaults.cardColors(
                containerColor = if (isFlashUrgentActive) Color(0xFF261D07) else FixoSurfaceCard
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FixoElectricAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Flash",
                            tint = FixoBgCanvas,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (language == AppLanguage.FR)
                                    "⚡ FIXO FLASH — Intervention sous 30 min"
                                else
                                    "⚡ FIXO FLASH — Immediate arrival in < 30 min",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FixoElectricAmber
                            )
                        }
                        Text(
                            text = if (language == AppLanguage.FR)
                                "Patrouille motorisée prioritaire • Devis forfaitaire garanti"
                            else
                                "Priority motorized dispatch • Guaranteed fixed price",
                            fontSize = 11.sp,
                            color = FixoTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onToggleFlashUrgent,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFlashUrgentActive) FixoElectricAmber else Color(0x33FFB800),
                        contentColor = if (isFlashUrgentActive) FixoBgCanvas else FixoElectricAmber
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (isFlashUrgentActive)
                            (if (language == AppLanguage.FR) "Mode Activé ✓" else "Active ✓")
                        else
                            (if (language == AppLanguage.FR) "Activer" else "Activate"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Sélecteur de Pannes par Pilules Tactiles (Horizontal Scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .testTag("diagnostic_pills_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            standardProblemPills.forEach { pill ->
                val isSelected = selectedPillId == pill.id
                val label = if (language == AppLanguage.FR) pill.labelFr else pill.labelEn

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF2E2410) else FixoSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) FixoElectricAmber else FixoBorderSubtle
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .clickable {
                            if (isSelected) {
                                onPillSelected(null)
                            } else {
                                onPillSelected(pill)
                            }
                        }
                        .testTag("pill_${pill.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${pill.emoji} $label",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) FixoElectricAmber else FixoTextPrimary
                        )
                    }
                }
            }
        }
    }
}
