package com.example.ui.screens.reels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Reel
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoElectricAmberDark

/**
 * Le Déclencheur Commercial : Bouton de Commande Directe — CHANTIER 4
 * - Hauteur : 52 dp, Coins : 14 dp
 * - Fond dégradé Ambre Solaire #FFB800 vers #FF9100
 * - Ombre portée dorée lumineuse (0 4px 16px rgba(245, 158, 11, 0.35))
 * - Icône outil : 🛠️ + Libellé "Commander cette prestation exacte • 15 000 FCFA"
 */
@Composable
fun ReelBookingCTA(
    reel: Reel,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedPrice = "${reel.priceAmount.toInt()} FCFA"
    val template = FixoStrings.getString("reels.book_exact_service", language)
    val ctaText = if (template.contains("%s")) {
        template.format(formattedPrice)
    } else {
        if (language == AppLanguage.FR)
            "Commander cette prestation exacte • $formattedPrice"
        else
            "Book this exact service • $formattedPrice"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = Color(0x59F59E0B),
                spotColor = Color(0x59F59E0B)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(FixoElectricAmber, FixoElectricAmberDark)
                )
            )
            .clickable { onClick() }
            .height(52.dp)
            .testTag("reel_booking_cta_${reel.id}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "🛠️",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = ctaText,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FixoBgCanvas,
                maxLines = 1
            )
        }
    }
}
