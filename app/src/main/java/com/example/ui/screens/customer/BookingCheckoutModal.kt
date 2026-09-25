package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.CameroonMobileOperator
import com.example.data.model.PaymentMethod
import com.example.data.model.ServiceItem
import com.example.data.model.WorkerProfile
import com.example.data.model.formatFixoCurrency
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGold600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate800
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class BookingPackageOption(
    val title: String,
    val subtitle: String,
    val price: Double,
    val etaDescription: String
) {
    FLASH(
        title = "Fixo Flash ⚡",
        subtitle = "Arrivée < 30 min",
        price = 15000.0,
        etaDescription = "Intervention d'urgence prioritaire garantie en moins de 30 minutes"
    ),
    STANDARD(
        title = "Fixo Standard",
        subtitle = "Arrivée d'ici 1-2h",
        price = 12000.0,
        etaDescription = "Arrivée d'ici 1 à 2 heures pour interventions programmées"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingCheckoutModal(
    worker: WorkerProfile,
    service: ServiceItem,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirmEscrowLock: (
        packageOption: BookingPackageOption,
        operator: CameroonMobileOperator,
        phone: String,
        address: String,
        notes: String
    ) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var selectedPackage by remember { mutableStateOf(BookingPackageOption.FLASH) }
    var selectedOperator by remember { mutableStateOf(CameroonMobileOperator.MTN_MOMO) }
    var mtnPhone by remember { mutableStateOf("+237 671 234 567") }
    var orangePhone by remember { mutableStateOf("+237 699 887 766") }
    var clientAddress by remember { mutableStateOf("Rue Drouot, Akwa, Douala") }
    var clientNotes by remember { mutableStateOf("Fuite importante sous évier cuisine, accès par portail noir") }
    var isProcessingUssd by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("booking_checkout_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp)
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
                            .background(FixoGold500.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Escrow Vault",
                            tint = FixoGold500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = FixoStrings.get("checkout.title", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Garantie 100% Anti-Cash Fixo Shield",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoGold600
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Artisan and Service recap
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = worker.avatarUrl,
                        contentDescription = worker.name,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(2.dp, FixoGold500, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = worker.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Plomberie sanitaire (Fuite d'eau standard)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "⭐ ${worker.rating} • ${worker.locationCity}",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoGold600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Package Selector: Fixo Flash vs Standard
            Text(
                text = "Formule d'Intervention :",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Flash Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPackage = BookingPackageOption.FLASH }
                        .border(
                            width = if (selectedPackage == BookingPackageOption.FLASH) 2.dp else 1.dp,
                            color = if (selectedPackage == BookingPackageOption.FLASH) FixoGold500 else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPackage == BookingPackageOption.FLASH) {
                            FixoGold500.copy(alpha = 0.12f)
                        } else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = FixoGold500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Fixo Flash ⚡",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = FixoGold600
                            )
                        }
                        Text(
                            text = "< 30 min",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "15 000 FCFA",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Standard Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedPackage = BookingPackageOption.STANDARD }
                        .border(
                            width = if (selectedPackage == BookingPackageOption.STANDARD) 2.dp else 1.dp,
                            color = if (selectedPackage == BookingPackageOption.STANDARD) FixoGold500 else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPackage == BookingPackageOption.STANDARD) {
                            FixoGold500.copy(alpha = 0.12f)
                        } else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Standard",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "1 à 2 heures",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "12 000 FCFA",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // What is included
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Inclus : Déplacement + Main-d'œuvre + Consommables de base (joints, téflon, vis, colle)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. ZERO-CASH MANDATORY WARNING BANNER (Crucial UX requirement)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF87171)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("zero_cash_warning_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDC2626)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Fixo Shield",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "CONTRAT SÉCURISÉ ZÉRO CASH",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF991B1B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prestation 100% payée via FIXO. Vous ne devez donner AUCUN argent liquide à l'artisan sur place. Tout paiement en espèces annule la garantie Fixo Shield.",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                            color = Color(0xFF7F1D1D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Mobile Money Operator Selector
            Text(
                text = "Compte Mobile Money pour la Séquestration :",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // MTN MoMo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedOperator = CameroonMobileOperator.MTN_MOMO }
                    .border(
                        width = if (selectedOperator == CameroonMobileOperator.MTN_MOMO) 2.dp else 1.dp,
                        color = if (selectedOperator == CameroonMobileOperator.MTN_MOMO) FixoGold500 else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(10.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOperator == CameroonMobileOperator.MTN_MOMO,
                        onClick = { selectedOperator = CameroonMobileOperator.MTN_MOMO },
                        colors = RadioButtonDefaults.colors(selectedColor = FixoGold500)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🟡 MTN MoMo (+237 671 *** ***)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Orange Money
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedOperator = CameroonMobileOperator.ORANGE_MONEY }
                    .border(
                        width = if (selectedOperator == CameroonMobileOperator.ORANGE_MONEY) 2.dp else 1.dp,
                        color = if (selectedOperator == CameroonMobileOperator.ORANGE_MONEY) FixoGold500 else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(10.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedOperator == CameroonMobileOperator.ORANGE_MONEY,
                        onClick = { selectedOperator = CameroonMobileOperator.ORANGE_MONEY },
                        colors = RadioButtonDefaults.colors(selectedColor = FixoGold500)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🟠 Orange Money (+237 699 *** ***)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Locking Action Button (52 dp height, Gold)
            val lockedAmount = formatFixoCurrency(selectedPackage.price)
            Button(
                onClick = {
                    isProcessingUssd = true
                    coroutineScope.launch {
                        delay(1200) // Simulate USSD push prompt
                        isProcessingUssd = false
                        onConfirmEscrowLock(
                            selectedPackage,
                            selectedOperator,
                            if (selectedOperator == CameroonMobileOperator.MTN_MOMO) mtnPhone else orangePhone,
                            clientAddress,
                            clientNotes
                        )
                    }
                },
                enabled = !isProcessingUssd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("lock_escrow_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FixoGold500,
                    contentColor = FixoNavy900
                )
            ) {
                if (isProcessingUssd) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = FixoNavy900,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Push USSD ${selectedOperator.label} en cours...",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bloquer $lockedAmount dans le Coffre Séquestre",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
