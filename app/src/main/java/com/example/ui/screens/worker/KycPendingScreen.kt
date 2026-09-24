package com.example.ui.screens.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoGoldGradient
import com.example.ui.theme.FixoNavy800
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoSuccessGreen
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite

/**
 * 5. L'Écran d'Attente Autonome de l'Artisan (KycPendingScreen)
 * Tant que le profil n'a pas été audité dans la base de données, l'artisan n'a pas accès
 * aux missions client ni au commutateur "En Ligne". Lorsqu'il ouvre l'application, il voit cet écran informatif.
 *
 * Synchronisation en tâche de fond : Dès que le compte passe à l'état APPROUVÉ dans la base de données,
 * l'application reçoit une notification push silencieuse. L'écran d'attente se déverrouille instantanément
 * pour laisser place au Cockpit Pro avec le commutateur vert 🟢 EN LIGNE.
 */
@Composable
fun KycPendingScreen(
    user: User?,
    language: AppLanguage = AppLanguage.FR,
    onUpdateDocuments: () -> Unit,
    onContactSupportWhatsapp: () -> Unit,
    onSimulateInstantApproval: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("kyc_pending_screen"),
        color = FixoBgCanvas
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: FIXO PRO | Statut de votre dossier
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "FIXO",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = FixoWhite,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = FixoGold500,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "PRO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoNavy950,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = FixoNavy800,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Statut de votre dossier" else "Dossier Status",
                        fontSize = 11.sp,
                        color = FixoGold500,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pulsing hourglass icon
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .scale(pulseScale)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                FixoAmber500.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = FixoNavy900,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FixoAmber500)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "En attente",
                            tint = FixoAmber500,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Status Title
            Text(
                text = "⏳ DOSSIER EN REVUE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = FixoWhite,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Official Description
            Text(
                text = if (language == AppLanguage.FR)
                    "Votre demande d'homologation a bien été transmise.\nNos équipes techniques vérifient l'authenticité de vos documents sous 24 à 48 heures ouvrées."
                else
                    "Your accreditation application has been submitted.\nOur compliance team is reviewing your documents within 24 to 48 business hours.",
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = FixoTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Checklist Card: Contrôles en cours
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kyc_checks_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Contrôles en cours :" else "Pending verifications:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Phone validated [✓]
                    KycChecklistItem(
                        isCompleted = true,
                        label = if (language == AppLanguage.FR) "Numéro de téléphone validé" else "Phone number verified (+237)"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. CNI [⏳]
                    KycChecklistItem(
                        isCompleted = false,
                        label = if (language == AppLanguage.FR) "Pièce d'identité officielle (CNI Recto/Verso)" else "Official National ID (CNI)"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Criminal Record [⏳]
                    KycChecklistItem(
                        isCompleted = false,
                        label = if (language == AppLanguage.FR) "Vérification des antécédents judiciaires (Bulletin n°3)" else "Criminal record background check"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Skills Certifications [⏳]
                    KycChecklistItem(
                        isCompleted = false,
                        label = if (language == AppLanguage.FR) "Certifications de compétences techniques" else "Technical skill certification / apprenticeship"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action: Mettre à jour mes pièces justificatives
            Text(
                text = if (language == AppLanguage.FR) "Besoin de modifier un document ?" else "Need to update any file?",
                fontSize = 12.sp,
                color = FixoTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onUpdateDocuments,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_update_kyc_docs"),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FixoGold500.copy(alpha = 0.8f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = FixoGold500
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.FR)
                        "Mettre à jour mes pièces justificatives"
                    else
                        "Update my submitted documents",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Contacter assistance WhatsApp
            Text(
                text = if (language == AppLanguage.FR) "Une question ?" else "Any questions?",
                fontSize = 12.sp,
                color = FixoTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onContactSupportWhatsapp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_contact_support_whatsapp"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366) // Official WhatsApp Green
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.FR)
                        "💬 Contacter l'assistance FIXO via WhatsApp"
                    else
                        "💬 Contact FIXO Support on WhatsApp",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sandbox Real-Time Unlock Test Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("kyc_sandbox_unlock_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FixoNavy900),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoSuccessGreen.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🧪 DÉBOGAGE & VÉRIFICATION SANDBOX",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoSuccessGreen,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (language == AppLanguage.FR)
                            "Simule l'audit validé par le backend FIXO et la réception du push silencieux pour déverrouiller instantanément le Cockpit Pro."
                        else
                            "Simulates backend approval & silent push to immediately unlock Pro Cockpit with green ONLINE switch.",
                        fontSize = 11.sp,
                        color = FixoTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onSimulateInstantApproval,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_simulate_kyc_approval"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoSuccessGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.FR)
                                "⚡ Simuler Approbation Immédiate (APPROUVÉ)"
                            else
                                "⚡ Simulate Instant Approval (APPROVED)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout link
            TextButton(
                onClick = onLogout,
                modifier = Modifier.testTag("btn_kyc_logout")
            ) {
                Text(
                    text = if (language == AppLanguage.FR) "Se Déconnecter" else "Sign Out",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun KycChecklistItem(
    isCompleted: Boolean,
    label: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(22.dp),
            shape = CircleShape,
            color = if (isCompleted) FixoSuccessGreen.copy(alpha = 0.2f) else FixoAmber500.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isCompleted) FixoSuccessGreen else FixoAmber500
            )
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Validé",
                        tint = FixoSuccessGreen,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = "En attente",
                        tint = FixoAmber500,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isCompleted) FixoWhite else FixoTextPrimary,
            fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
