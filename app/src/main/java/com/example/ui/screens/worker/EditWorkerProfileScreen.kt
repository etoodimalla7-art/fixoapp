package com.example.ui.screens.worker

import android.util.Log
import com.example.data.repository.WorkerProfileRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkerProfile
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoWhite

/**
 * Écran d'édition du profil artisan (EditWorkerProfileScreen).
 * Permet à Marc Dubois de modifier sa biographie professionnelle, ses années d'expérience
 * et ses compétences certifiées visibles immédiatement par les clients.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkerProfileScreen(
    worker: WorkerProfile,
    onBack: () -> Unit,
    onSaveBio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var bioText by remember {
        mutableStateOf(
            worker.bio.ifEmpty {
                "Maître Artisan certifié FIXO. 14 ans d'expérience en plomberie sanitaire, soudures de précision cuivre et dépannage garanti sous 30 minutes à Douala."
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Modifier mon Profil Pro",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FixoWhite
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("edit_profile_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = FixoWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FixoBgCanvas)
            )
        },
        containerColor = FixoBgCanvas,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Carte de présentation & biographie pro
            Card(
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = FixoGold500, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Présentation professionnelle & Biographie (visible par les clients)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = FixoWhite,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Rédigez votre biographie détaillée (expérience, spécialités, garanties). Ce texte sera visible par tous les clients consultant votre vitrine officielle FIXO.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = bioText,
                        onValueChange = { bioText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_worker_bio_input"),
                        minLines = 5,
                        maxLines = 10,
                        placeholder = {
                            Text(
                                "Décrivez votre savoir-faire, vos 14 ans d'expérience...",
                                color = Color(0xFF64748B)
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FixoGold500,
                            unfocusedBorderColor = FixoBorderSubtle,
                            focusedTextColor = FixoWhite,
                            unfocusedTextColor = FixoWhite,
                            focusedContainerColor = Color(0xFF141920),
                            unfocusedContainerColor = Color(0xFF141920)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            Log.d("FIXO_CLICK", "Clic Enregistrer biographie pro EditScreen: $bioText")
                            WorkerProfileRepository.updateBio(bioText)
                            onSaveBio(bioText)
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FixoGold500, contentColor = Color(0xFF080C15)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_save_worker_bio_pro")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF080C15),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enregistrer ma biographie pro",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF080C15)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Récapitulatif métier & certifications
            Card(
                colors = CardDefaults.cardColors(containerColor = FixoSurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Éléments Réglementaires Associés",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Métier homologué : ${worker.category.displayName}\n" +
                                "• Titre : Maître Artisan Référent (14 ans d'expérience)\n" +
                                "• Ville d'attache : Douala (Akwa, Boulevard de la Liberté)\n" +
                                "• Certifications : CQP Plomberie-Soudure, CNI & Casier Judiciaire vérifiés",
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}
