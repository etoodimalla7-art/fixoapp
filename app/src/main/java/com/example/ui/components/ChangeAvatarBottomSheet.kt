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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoTheme

/**
 * MODAL BOTTOM SHEET RÉACTIVE : SÉLECTEUR D'INSIGNES & PHOTOS DE PROFIL
 *
 * Spécifications :
 * - Mode Sombre : Fond ardoise #111827, liseré or.
 * - Mode Clair : Fond blanc pur #FFFFFF, liseré gris clair #E2E8F0, textes ardoise #0F172A.
 * - Boutons larges : [ 📷 Prendre une Photo ] et [ 🖼️ Choisir dans la Galerie ].
 * - Grille des 6 insignes vectoriels métalliques avec légende nette sous chaque icône.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeAvatarBottomSheet(
    onDismissRequest: () -> Unit,
    onSelectAvatar: (String) -> Unit,
    onTakePhoto: () -> Unit,
    onPickGallery: () -> Unit,
    currentAvatarUrl: String? = null,
    userName: String = "Sarah Jenkins",
    language: AppLanguage = AppLanguage.FR,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val isDark = FixoTheme.isDark
    val containerBg = if (isDark) Color(0xFF111827) else Color(0xFFFFFFFF)
    val sheetBorder = if (isDark) FixoGold500.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = containerBg,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1))
            )
        },
        modifier = Modifier.testTag("change_avatar_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (language == AppLanguage.FR) "Identité Visuelle Fixo" else "Fixo Visual Identity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Choisissez un insigne officiel ou importez votre cliché"
                        else
                            "Select an official insignia or import your photo",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FixoGold500.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "6 Insignes" else "6 Badges",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action 1: [ 📷 Prendre une Photo ]
            Button(
                onClick = {
                    onTakePhoto()
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_take_avatar_photo"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FixoGold500,
                    contentColor = Color(0xFF080C15)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF080C15)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.FR) "📷 Prendre une Photo" else "📷 Take a Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF080C15)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action 2: [ 🖼️ Choisir dans la Galerie ]
            OutlinedButton(
                onClick = {
                    onPickGallery()
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_pick_avatar_gallery"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) FixoGold500 else Color(0xFFCBD5E1)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                    contentColor = textPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isDark) FixoGold500 else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.FR) "🖼️ Choisir dans la Galerie" else "🖼️ Choose from Gallery",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6 Official Metallic Vector Badges Grid
            Text(
                text = if (language == AppLanguage.FR)
                    "Insignes Métalliques & Monogramme Dynamique :"
                else
                    "Metallic Insignia & Dynamic Monogram:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FIXO_SYMBOLIC_AVATARS.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowItems.forEach { def ->
                            val isSelected = currentAvatarUrl == def.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        onSelectAvatar(def.id)
                                        onDismissRequest()
                                    }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) FixoGold500 else sheetBorder,
                                            shape = CircleShape
                                        )
                                ) {
                                    FixoSymbolicAvatar(
                                        avatarUrl = def.id,
                                        userName = userName,
                                        size = 56.dp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (language == AppLanguage.FR) def.titleFr else def.titleEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) FixoGold500 else textSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
