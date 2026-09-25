package com.example.ui.screens.worker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoElectricAmber

/**
 * CHANTIER 5 : LE CADRAN FINANCIER TRANSPARENT
 *
 * Grand bloc horizontal contrasté (#1A2232, bordure 1.5 px rgba(255, 255, 255, 0.12), coins 18 dp) :
 * 1. Solde disponible retirable immédiatement (typographie bold 30px blanc pur)
 * 2. Fonds sécurisés sous séquestre (18px ambre #FFB800 avec cadenas)
 * 3. Bouton d'action retrait immédiat émeraude 44 dp
 */
@Composable
fun EarningsCard(
    availableBalance: Double,
    escrowBalance: Double,
    onCashOutClick: () -> Unit,
    language: AppLanguage = AppLanguage.FR,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("earnings_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2232)),
        border = BorderStroke(1.5.dp, Color(0x1FFFFFFF)) // rgba(255, 255, 255, 0.12)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // ==========================================
            // 1. SOLDE DISPONIBLE RETIRABLE
            // ==========================================
            val availableTitle = FixoStrings.getString("worker.earnings.available_title", language)
            val availableSubtitle = FixoStrings.getString("worker.earnings.available_subtitle", language)

            Text(
                text = availableTitle.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatFixoCurrency(availableBalance),
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.testTag("available_balance_text")
            )

            Text(
                text = availableSubtitle,
                fontSize = 12.sp,
                color = Color(0xFF38BDF8),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 2. FONDS SOUS SÉQUESTRE (CHANTIERS EN COURS)
            // ==========================================
            val escrowSubtitle = FixoStrings.getString("worker.earnings.escrow_subtitle", language)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = FixoElectricAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${formatFixoCurrency(escrowBalance)} ${if (language == AppLanguage.FR) "sous séquestre" else "in escrow"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FixoElectricAmber,
                    modifier = Modifier.testTag("escrow_balance_text")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "« $escrowSubtitle »",
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1).copy(alpha = 0.8f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 3. BOUTON ACTION RETRAIT IMMÉDIAT (Émeraude 44 dp)
            // ==========================================
            val cashoutLabel = FixoStrings.getString("worker.earnings.cashout_button", language)

            Button(
                onClick = onCashOutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("artisan_cashout_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color(0xFF080C15)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF080C15)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = cashoutLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
