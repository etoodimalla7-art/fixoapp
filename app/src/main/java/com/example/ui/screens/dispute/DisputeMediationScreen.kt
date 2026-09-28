package com.example.ui.screens.dispute

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.isDisputed
import com.example.data.model.formatFixoCurrency
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisputeMediationScreen(
    booking: Booking,
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    onSubmitDispute: (reason: String, photos: List<String>, notes: String) -> Unit
) {
    val context = LocalContext.current

    val disputeReasons = listOf(
        "Fuite persistante après réparation",
        "Matériel endommagé ou détérioré",
        "Abandon de chantier avant achèvement",
        "Non-conformité manifeste du devis initial"
    )

    var selectedReason by remember { mutableStateOf(disputeReasons[0]) }
    var notesText by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf<Boolean>(booking.status.isDisputed) }

    // Mandatory 3 photos for contradiction claim
    val contestationPhotos = remember {
        mutableStateListOf(
            "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=600&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1621905251918-48416bd8575a?w=600&auto=format&fit=crop"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = FixoStrings.get("dispute.title", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("dispute_back_btn")) {
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
                // Gel Immédiat du Séquestre Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1515)),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoRed500, FixoRed500))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("escrow_frozen_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FixoRed500.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = FixoRed500,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "GEL IMMÉDIAT DU SÉQUESTRE",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FixoRed500,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Les ${formatFixoCurrency(booking.priceAmount)} sont verrouillés. Ni le client ni l'artisan ne peuvent retirer les fonds tant que la médiation n'a pas statué.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = FixoWhite,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            // Dossier de Réclamation Contradictoire
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Motif de la contestation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = FixoWhite
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        disputeReasons.forEach { reason ->
                            val isSelected = selectedReason == reason
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF262015) else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) FixoGold500 else FixoBorderSubtle,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedReason = reason }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, if (isSelected) FixoGold500 else FixoTextSecondary, CircleShape)
                                        .background(if (isSelected) FixoGold500 else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = reason,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) FixoWhite else FixoTextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Explication détaillée de la non-conformité",
                            style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = notesText,
                            onValueChange = { notesText = it },
                            placeholder = { Text("Précisez les anomalies constatées...", color = FixoTextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FixoGold500,
                                unfocusedBorderColor = FixoBorderSubtle,
                                focusedTextColor = FixoWhite,
                                unfocusedTextColor = FixoWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dispute_notes_input")
                        )
                    }
                }
            }

            // Comparaison Automatique Affichée : Photo Avant Travaux vs Photos Réclamation
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = null,
                                tint = FixoGold500,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Analyse Comparative Contradictoire",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Left: Before Work Photo (Taken by Artisan)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AVANT (ARTISAN)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = FixoGold500,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF141920))
                                        .border(1.dp, FixoBorderSubtle, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val beforeUrl = booking.beforePhotoUrl
                                    if (beforeUrl != null) {
                                        AsyncImage(
                                            model = beforeUrl,
                                            contentDescription = "Before Photo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Text(
                                            text = "Non fournie",
                                            style = MaterialTheme.typography.bodySmall.copy(color = FixoTextSecondary)
                                        )
                                    }
                                }
                            }

                            // Right: Customer Claim Photos (3 photos)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "RÉCLAMATION (CLIENT)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = FixoRed500,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF141920))
                                        .border(1.dp, FixoRed500.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = contestationPhotos.firstOrNull(),
                                        contentDescription = "Claim photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${contestationPhotos.size} photos",
                                            style = MaterialTheme.typography.labelSmall.copy(color = FixoWhite)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Engagement FIXO : Arbitrage sous 2h & Support WhatsApp
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = FixoGold500,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = FixoStrings.get("dispute.sla_notice", language),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Un juriste-médiateur FIXO examine les preuves géolocalisées et tranche pour ordonner soit la réintervention gratuite, soit le remboursement intégral du séquestre.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = FixoTextSecondary,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bouton WhatsApp Support direct
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/237671234567?text=Litige%20FIXO%20Chantier%20${booking.id}"))
                                context.startActivity(intent)
                            },
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FixoSuccessGreen, FixoSuccessGreen))),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("whatsapp_arbitrator_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = FixoSuccessGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = FixoStrings.get("dispute.whatsapp_cta", language),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoSuccessGreen
                                )
                            )
                        }
                    }
                }
            }

            // Submit Dispute Action
            item {
                Button(
                    onClick = {
                        onSubmitDispute(selectedReason, contestationPhotos.toList(), notesText)
                        isSubmitted = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FixoRed500),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_dispute_confirm_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = FixoWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSubmitted) "DOSSIER TRANSMIS (GEL ACTIF)" else "TRANSMETTRE ET GELER LE SÉQUESTRE",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
