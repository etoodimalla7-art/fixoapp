package com.example.ui.screens.worker

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Booking
import com.example.data.model.JobStatus
import com.example.data.model.isOnSite
import com.example.data.model.isWorking
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoInspectionValidator {
    /**
     * Le chronomètre et le démarrage effectif des travaux sont strictement bloqués
     * tant que l'artisan n'a pas pris et certifié la photo avant travaux.
     */
    fun canStartWork(beforePhotoUrl: String?): Boolean {
        return !beforePhotoUrl.isNullOrBlank()
    }

    /**
     * Le bouton de génération de facture QR / Clôture reste strictement désactivé
     * tant que la photo finale après réparation n'a pas été enregistrée.
     */
    fun canGenerateInvoiceQr(afterPhotoUrl: String?): Boolean {
        return !afterPhotoUrl.isNullOrBlank()
    }
}

@Composable
fun PhotoInspectionProtocolCard(
    booking: Booking,
    isArtisan: Boolean,
    language: AppLanguage,
    onTakeBeforePhoto: (photoUrl: String) -> Unit,
    onTakeAfterPhoto: (photoUrl: String) -> Unit,
    onStartWork: () -> Unit,
    onGeneratePaymentQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasBeforePhoto = PhotoInspectionValidator.canStartWork(booking.beforePhotoUrl)
    val hasAfterPhoto = PhotoInspectionValidator.canGenerateInvoiceQr(booking.afterPhotoUrl)
    val isOnSiteState = booking.status.isOnSite
    val isWorkingState = booking.status.isWorking
    val isPendingClosure = booking.status == JobStatus.COMPLETION_REQUESTED || booking.status == JobStatus.COMPLETED_PENDING_HANDSHAKE

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("photo_inspection_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(FixoEmerald500.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = FixoEmerald600,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Protocole Photo Anti-Litige",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Horodatage + Géolocalisation Fixo Shield",
                            style = MaterialTheme.typography.labelSmall,
                            color = FixoEmerald600
                        )
                    }
                }

                Surface(
                    color = if (hasBeforePhoto && hasAfterPhoto) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (hasBeforePhoto && hasAfterPhoto) "100% Conforme" else if (hasBeforePhoto) "Étape 1/2" else "En attente",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (hasBeforePhoto && hasAfterPhoto) Color(0xFF166534) else Color(0xFF92400E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 1: Photo Avant Travaux
            InspectionPhotoStepRow(
                stepNumber = "1",
                title = "Photo Avant Travaux (Constat d'Arrivée)",
                subtitle = "Obligatoire pour débloquer le chronomètre de travail",
                photoUrl = booking.beforePhotoUrl,
                timestamp = booking.beforePhotoTimestamp,
                lat = booking.beforePhotoLat,
                lng = booking.beforePhotoLng,
                isCompleted = hasBeforePhoto,
                isActive = isOnSiteState && !hasBeforePhoto,
                isArtisan = isArtisan,
                testTag = "before_photo_row",
                onTakePhoto = {
                    val sampleBeforeUrl = "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=600&auto=format&fit=crop"
                    onTakeBeforePhoto(sampleBeforeUrl)
                }
            )

            // Button to start work if before photo is taken and artisan is on site
            if (isArtisan && isOnSiteState && hasBeforePhoto) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onStartWork,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_work_unlocked_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FixoEmerald500,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DÉMARRER LES TRAVAUX & CHRONOMÈTRE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 2: Photo Après Réparation
            InspectionPhotoStepRow(
                stepNumber = "2",
                title = "Photo Après Réparation (État Final)",
                subtitle = "Obligatoire pour générer le Code QR de clôture",
                photoUrl = booking.afterPhotoUrl,
                timestamp = booking.afterPhotoTimestamp,
                lat = booking.afterPhotoLat,
                lng = booking.afterPhotoLng,
                isCompleted = hasAfterPhoto,
                isActive = isWorkingState && !hasAfterPhoto,
                isArtisan = isArtisan,
                testTag = "after_photo_row",
                onTakePhoto = {
                    val sampleAfterUrl = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600&auto=format&fit=crop"
                    onTakeAfterPhoto(sampleAfterUrl)
                }
            )

            // Button to generate payment QR code
            if (isArtisan && (isWorkingState || isPendingClosure)) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onGeneratePaymentQr,
                    enabled = hasAfterPhoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_payment_qr_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FixoGold500,
                        contentColor = FixoNavy900,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = if (hasAfterPhoto) Icons.Default.CheckCircle else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasAfterPhoto) {
                            "✓ TRAVAUX TERMINÉS : GÉNÉRER LE CODE QR DE PAIEMENT"
                        } else {
                            "PHOTO APRÈS TRAVAUX REQUISE POUR LE QR CODE"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun InspectionPhotoStepRow(
    stepNumber: String,
    title: String,
    subtitle: String,
    photoUrl: String?,
    timestamp: Long?,
    lat: Double?,
    lng: Double?,
    isCompleted: Boolean,
    isActive: Boolean,
    isArtisan: Boolean,
    testTag: String,
    onTakePhoto: () -> Unit
) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.FRANCE) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .border(
                width = if (isCompleted) 1.5.dp else if (isActive) 1.dp else 0.5.dp,
                color = if (isCompleted) FixoEmerald500 else if (isActive) FixoGold500 else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) FixoEmerald500.copy(alpha = 0.04f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) FixoEmerald500 else if (isActive) FixoGold500 else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text(
                            text = stepNumber,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isActive) FixoNavy900 else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isArtisan && !isCompleted) {
                    OutlinedButton(
                        onClick = onTakePhoto,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("take_photo_btn_$stepNumber")
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Prendre", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Photo preview if completed
            if (isCompleted && !photoUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = FixoEmerald600)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Certifié à ${timestamp?.let { timeFormatter.format(Date(it)) } ?: "10:14:22"}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = FixoEmerald600
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GPS : ${lat ?: 4.0505}° N, ${lng ?: 9.6950}° E (Akwa)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7))
                            .padding(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Verified", tint = Color(0xFF166534), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
