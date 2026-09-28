package com.example.ui.screens.worker

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.WorkerProfile
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerSettingsScreen(
    user: User?,
    workerProfile: WorkerProfile?,
    language: AppLanguage = AppLanguage.FR,
    onBack: () -> Unit,
    onSaveRadius: (radiusKm: Int) -> Unit
) {
    val context = LocalContext.current
    var radiusKm by remember { mutableFloatStateOf(user?.interventionRadiusKm?.toFloat() ?: 15f) }
    var selectedSlots by remember { mutableStateOf("08:00 - 18:00 (Du Lundi au Samedi)") }
    var kycRenewed by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paramètres Artisan Pro",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("worker_settings_back")) {
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
                // 1. Rayon d'intervention géographique (Slider 1 à 25 km)
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rayon d'Intervention Géographique",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rayon de dispatching autour de votre position :",
                                style = MaterialTheme.typography.bodySmall.copy(color = FixoTextSecondary),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${radiusKm.roundToInt()} km",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FixoGold500
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = radiusKm,
                            onValueChange = { radiusKm = it },
                            valueRange = 1f..25f,
                            steps = 23,
                            colors = SliderDefaults.colors(
                                thumbColor = FixoGold500,
                                activeTrackColor = FixoGold500,
                                inactiveTrackColor = FixoBorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("radius_slider")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onSaveRadius(radiusKm.roundToInt()) },
                            colors = ButtonDefaults.buttonColors(containerColor = FixoGold500),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("save_radius_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enregistrer le rayon (${radiusKm.roundToInt()} km)", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Créneaux de Disponibilité
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Créneaux & Horaires d'Intervention",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Plage horaire actuelle : $selectedSlots",
                            style = MaterialTheme.typography.bodyMedium.copy(color = FixoWhite)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚡ Missions d'urgence Fixo Flash activées 24/7 sur option",
                            style = MaterialTheme.typography.labelSmall.copy(color = FixoGold500)
                        )
                    }
                }
            }

            // 3. Consultation et Renouvellement des Documents KYC
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dossier d'Homologation & CNI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF141920))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Carte Nationale d'Identité (CNI)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FixoWhite
                                    )
                                )
                                Text(
                                    text = "Expire le : ${user?.kycCniExpiry ?: "31/12/2028"}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = FixoTextSecondary)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = FixoSuccessGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Valide", style = MaterialTheme.typography.labelSmall.copy(color = FixoSuccessGreen))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { kycRenewed = true },
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, tint = FixoGold500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Renouveler CNI ou Diplôme Professionnel", color = FixoWhite)
                        }

                        if (kycRenewed) {
                            Text(
                                text = "✓ Nouveau document transmis pour vérification par l'officier KYC.",
                                style = MaterialTheme.typography.bodySmall.copy(color = FixoSuccessGreen),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // 4. Support Technique Prioritaire WhatsApp Pro
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(FixoBorderSubtle, FixoBorderSubtle))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = FixoSuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ligne Directe Support Artisans",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FixoWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Assistance technique dédiée aux prestataires partenaires : dépannage de l'application, questions de facturation ou assistance sur chantier en direct.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = FixoTextSecondary,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/237671234567?text=Artisan%20Support%20FIXO"))
                                context.startActivity(intent)
                            },
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(FixoSuccessGreen, FixoSuccessGreen))),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("worker_whatsapp_support_btn")
                        ) {
                            Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = FixoSuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Contacter le Support WhatsApp Pro", color = FixoSuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
