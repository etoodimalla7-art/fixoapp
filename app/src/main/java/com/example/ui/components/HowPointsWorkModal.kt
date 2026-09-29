package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoGold500

/**
 * Modale d'Explication du Programme de Points FIXO (WCAG AAA compliant)
 */
@Composable
fun HowPointsWorkModal(
    language: AppLanguage = AppLanguage.FR,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF111827))
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FixoGold500.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = FixoGold500,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (language == AppLanguage.FR) "Programme Fidélité FIXO" else "FIXO Rewards Program",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF8FAFC)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_how_points_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Point Steps with pure white #F8FAFC text and 22sp line height
                PointsStepCard(
                    icon = Icons.Default.Stars,
                    stepNumber = "1",
                    title = if (language == AppLanguage.FR) "Gagnez 5% de CashBack Points" else "Earn 5% Points Back",
                    description = if (language == AppLanguage.FR)
                        "1. Earn 5% Points Back : Chaque intervention clôturée vous rapporte 5% du montant payé en points FIXO (ex: 15 000 FCFA = 750 Points)."
                    else
                        "1. Earn 5% Points Back: Every completed repair earns 5% of the invoice amount in FIXO Points (e.g. 15,000 FCFA = 750 Points)."
                )

                Spacer(modifier = Modifier.height(14.dp))

                PointsStepCard(
                    icon = Icons.Default.Redeem,
                    stepNumber = "2",
                    title = if (language == AppLanguage.FR) "Convertissez en Bons & Réductions" else "Redeem for Service Vouchers",
                    description = if (language == AppLanguage.FR)
                        "2. Bons instantanés : Débloquez des réductions de 2 000 à 10 000 FCFA déductibles immédiatement lors de votre prochain dépannage."
                    else
                        "2. Instant Vouchers: Unlock discounts from 2,000 to 10,000 FCFA directly applicable to your next emergency or scheduled intervention."
                )

                Spacer(modifier = Modifier.height(14.dp))

                PointsStepCard(
                    icon = Icons.Default.Diamond,
                    stepNumber = "3",
                    title = if (language == AppLanguage.FR) "Accédez au Statut Or Privilège" else "Reach Gold Privilege Tier",
                    description = if (language == AppLanguage.FR)
                        "3. Statut Prestige : Dès 5 000 points cumulés, profitez du support prioritaire 24/7 et d'une assurance Fixo Shield étendue à 60 jours."
                    else
                        "3. Prestige Tier: Starting at 5,000 lifetime points, benefit from 24/7 VIP concierge and 60-day extended Fixo Shield guarantee."
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Bouton de fermeture plein Or Ambre avec texte sombre #080C15
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("close_how_points_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FixoGold500,
                        contentColor = Color(0xFF080C15)
                    )
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Compris, retour à mon portefeuille" else "Got it, back to wallet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PointsStepCard(
    icon: ImageVector,
    stepNumber: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(FixoGold500),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        color = Color(0xFF080C15),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFF8FAFC)
            )
        }
    }
}
