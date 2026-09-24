package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoElectricAmberDark
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoSurfaceElevated
import com.example.ui.theme.FixoTextMuted
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary

/**
 * CHANTIER 3 - MODALE DÉTAILLÉE : LE PASSEPORT DE CONFIANCE ARTISAN (WorkerPassportModal.kt)
 * S'ouvre sous forme de feuille basse quasi plein écran (hauteur 85%) dès qu'on clique sur la fiche d'un technicien.
 *
 * 1. En-tête Institutionnel
 * 2. Grille des 4 Piliers d'Homologation Officielle
 * 3. Matrice Tarifaire Transparente & Forfaitaire
 * 4. Bouton d'Engagement Sécurisé "Verrouiller les Fonds sous Séquestre"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerPassportModal(
    worker: WorkerProfile,
    language: AppLanguage,
    initialIsFlash: Boolean = false,
    customServiceTitle: String? = null,
    customPrice: Double? = null,
    onDismiss: () -> Unit,
    onBookConfirmed: (isFlash: Boolean, totalXaf: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPackageIsFlash by remember { mutableStateOf(initialIsFlash) }

    val standardPrice = customPrice ?: 12000.0
    val flashPrice = if (customPrice != null) customPrice else 15000.0
    val activePrice = if (customPrice != null) customPrice else if (selectedPackageIsFlash) flashPrice else standardPrice

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FixoBgCanvas,
        contentColor = FixoTextPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(FixoBorderSubtle)
            )
        },
        modifier = modifier.testTag("worker_passport_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar: Back / Close & Master Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onDismiss() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = FixoElectricAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.FR) "PASSEPORT OFFICIEL FIXO" else "OFFICIAL FIXO PASSPORT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FixoElectricAmber,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x33FFB800),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoElectricAmber)
                ) {
                    Text(
                        text = "MASTER ARTISAN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoElectricAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. En-tête Institutionnel
            Card(
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FixoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile photo with golden ring
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(3.dp, FixoElectricAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = worker.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300" },
                            contentDescription = worker.name,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = worker.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = FixoTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = FixoEmerald500,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "Maître ${worker.category.displayName} Certifié • Akwa, Douala",
                            fontSize = 12.sp,
                            color = FixoTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = FixoElectricAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "%.1f".format(worker.rating),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoTextPrimary
                                )
                                Text(
                                    text = " (${worker.completedJobs} avis)",
                                    fontSize = 11.sp,
                                    color = FixoTextMuted
                                )
                            }

                            Text("•", fontSize = 12.sp, color = FixoBorderSubtle)

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x3310B981)
                            ) {
                                Text(
                                    text = "Arrivée ~15 min",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FixoEmerald500,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Grille des 4 Piliers d'Homologation Officielle
            Text(
                text = if (language == AppLanguage.FR) "AUDIT DE CONFORMITÉ FIXO (100% HOMOLOGUÉ)" else "FIXO COMPLIANCE AUDIT (100% VETTED)",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FixoElectricAmber,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PillarAuditCard(
                    title = if (language == AppLanguage.FR) "Identité Biométrique Validée" else "Biometric ID Verified",
                    subtitle = if (language == AppLanguage.FR) "CNI camerounaise scannée & contrôlée" else "Cameroon National ID scanned & audited",
                    icon = Icons.Default.Verified
                )
                PillarAuditCard(
                    title = if (language == AppLanguage.FR) "Casier Judiciaire Vierge" else "Clean Criminal Record",
                    subtitle = if (language == AppLanguage.FR) "Bulletin N°3 vérifié il y a moins de 3 mois" else "Bulletin No. 3 checked within 3 months",
                    icon = Icons.Default.Shield
                )
                PillarAuditCard(
                    title = if (language == AppLanguage.FR) "Compétences Techniques Certifiées" else "Certified Trade Credentials",
                    subtitle = if (language == AppLanguage.FR) "Diplôme d'État & test pratique d'atelier réussi" else "State trade diploma & practical workshop test",
                    icon = Icons.Default.CheckCircle
                )
                PillarAuditCard(
                    title = if (language == AppLanguage.FR) "Garantie Fixo Shield" else "Fixo Shield Guarantee",
                    subtitle = if (language == AppLanguage.FR) "Couverture dégradations matérielles incluse" else "Property damage warranty included",
                    icon = Icons.Default.Security
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Matrice Tarifaire Transparente & Forfaitaire
            Text(
                text = if (language == AppLanguage.FR) "TARIF FORFAITAIRE TOUT INCLUS (ZÉRO SURPRISE)" else "ALL-INCLUSIVE FIXED RATES (NO HIDDEN FEES)",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FixoElectricAmber,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (customPrice != null) {
                RateChoiceCard(
                    title = customServiceTitle ?: (if (language == AppLanguage.FR) "Prestation Exacte du Reel" else "Exact Reel Service"),
                    price = "${customPrice.toInt()} FCFA",
                    isSelected = true,
                    onClick = { },
                    badge = "COMMANDÉ VIA REEL"
                )
            } else {
                // Choice 1: Standard
                RateChoiceCard(
                    title = if (language == AppLanguage.FR) "Fixo Standard (Intervention sous 1-2h)" else "Fixo Standard (Arrival in 1-2 hours)",
                    price = "12 000 FCFA",
                    isSelected = !selectedPackageIsFlash,
                    onClick = { selectedPackageIsFlash = false },
                    badge = null
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Choice 2: Flash
                RateChoiceCard(
                    title = if (language == AppLanguage.FR) "⚡ Fixo Flash (Arrivée garantie < 30 min)" else "⚡ Fixo Flash (Guaranteed arrival < 30 min)",
                    price = "15 000 FCFA",
                    isSelected = selectedPackageIsFlash,
                    onClick = { selectedPackageIsFlash = true },
                    badge = "PRIORITAIRE"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Escrow protection banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2838)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoElectricAmber.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = FixoElectricAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == AppLanguage.FR)
                            "La somme est conservée sous séquestre sécurisé. Aucun paiement en espèces n'est autorisé sur le chantier."
                        else
                            "Funds are held in secure escrow. No cash payments are allowed on the job site.",
                        fontSize = 11.sp,
                        color = FixoTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Bouton d'Engagement Sécurisé "Verrouiller les Fonds sous Séquestre"
            val buttonPrice = if (customPrice != null) "${customPrice.toInt()}" else if (selectedPackageIsFlash) "15 000" else "12 000"
            Button(
                onClick = {
                    onBookConfirmed(selectedPackageIsFlash, activePrice)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("lock_escrow_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FixoElectricAmber,
                    contentColor = FixoBgCanvas
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = FixoBgCanvas,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Verrouiller les Fonds sous Séquestre ($buttonPrice FCFA)"
                        else
                            "Lock Funds in Escrow ($buttonPrice FCFA)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = FixoBgCanvas
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PillarAuditCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = FixoSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0x3310B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FixoEmerald500,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = FixoTextMuted
                )
            }

            Text(
                text = "AUDITÉ ✓",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FixoEmerald500
            )
        }
    }
}

@Composable
fun RateChoiceCard(
    title: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String?
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF261D07) else FixoSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) FixoElectricAmber else FixoBorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = FixoElectricAmber,
                        unselectedColor = FixoTextMuted
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) FixoElectricAmber else FixoTextPrimary
                    )
                    if (badge != null) {
                        Text(
                            text = "⚡ $badge",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FixoElectricAmber
                        )
                    }
                }
            }

            Text(
                text = price,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) FixoElectricAmber else FixoTextPrimary
            )
        }
    }
}
