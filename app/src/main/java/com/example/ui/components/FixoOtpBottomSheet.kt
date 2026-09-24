package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay

/**
 * 3. Validation par Code OTP (4 Chiffres)
 * Une feuille basse (bottom sheet) sombre s'ouvre avec flou d'arrière-plan dès l'envoi du numéro.
 * Saisie automatique du code SMS via l'API Android native (SmsRetriever),
 * ou saisie manuelle dans 4 cases agrandies adaptées au doigt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixoOtpBottomSheet(
    phoneNumber: String,
    language: AppLanguage = AppLanguage.FR,
    onDismiss: () -> Unit,
    onOtpVerified: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var otpCode by remember { mutableStateOf("") }
    var resendCooldown by remember { mutableIntStateOf(60) }
    var isVerifying by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
        while (resendCooldown > 0) {
            delay(1000)
            resendCooldown--
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = FixoNavy950,
        scrimColor = Color.Black.copy(alpha = 0.85f),
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 44.dp, height = 4.dp),
                shape = RoundedCornerShape(2.dp),
                color = FixoBorderSubtle
            ) {}
        },
        modifier = modifier.testTag("otp_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row with Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = FixoGold500.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Sms,
                                contentDescription = null,
                                tint = FixoGold500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (language == AppLanguage.FR) "Code de Sécurité SMS" else "SMS Security Code",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoWhite
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_otp_modal")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = FixoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle with Phone Number
            Text(
                text = if (language == AppLanguage.FR)
                    "Entrez le code à 4 chiffres envoyé par SMS au :"
                else
                    "Enter the 4-digit code sent via SMS to:",
                fontSize = 13.sp,
                color = FixoTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = phoneNumber,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = FixoGold500,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hidden BasicTextField capturing keyboard input
            Box(contentAlignment = Alignment.Center) {
                BasicTextField(
                    value = otpCode,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }.take(4)
                        otpCode = filtered
                        if (filtered.length == 4) {
                            onOtpVerified(filtered)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (otpCode.length == 4) {
                                onOtpVerified(otpCode)
                            }
                        }
                    ),
                    modifier = Modifier
                        .size(1.dp)
                        .focusRequester(focusRequester)
                        .testTag("hidden_otp_text_field")
                )

                // 4 Large tactile OTP digit boxes adapted for finger touch
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (index in 0 until 4) {
                        val digit = otpCode.getOrNull(index)?.toString() ?: ""
                        val isFocusedBox = otpCode.length == index

                        Surface(
                            modifier = Modifier
                                .size(64.dp)
                                .clickable {
                                    try {
                                        focusRequester.requestFocus()
                                    } catch (_: Exception) {}
                                }
                                .testTag("otp_digit_$index"),
                            shape = RoundedCornerShape(16.dp),
                            color = if (digit.isNotEmpty()) FixoNavy800 else FixoSurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                when {
                                    isFocusedBox -> FixoGold500
                                    digit.isNotEmpty() -> FixoGold500.copy(alpha = 0.8f)
                                    else -> FixoBorderSubtle
                                }
                            )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = digit,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FixoWhite,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Auto-retrieval / Instant Fill Simulation Button (SmsRetriever API Simulation)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        otpCode = "4829"
                        onOtpVerified("4829")
                    }
                    .testTag("btn_simulate_sms_retriever"),
                shape = RoundedCornerShape(12.dp),
                color = FixoNavy900,
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = FixoGold500,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚡ Remplir code SMS reçu (4829) - SmsRetriever",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = FixoGold500
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Resend Timer or Button
            if (resendCooldown > 0) {
                Text(
                    text = if (language == AppLanguage.FR)
                        "Renvoyer un nouveau code dans ${resendCooldown}s"
                    else
                        "Resend code in ${resendCooldown}s",
                    fontSize = 12.sp,
                    color = FixoTextSecondary
                )
            } else {
                TextButton(
                    onClick = {
                        resendCooldown = 60
                    },
                    modifier = Modifier.testTag("btn_resend_otp")
                ) {
                    Text(
                        text = if (language == AppLanguage.FR) "Renvoyer le code SMS" else "Resend SMS code",
                        color = FixoGold500,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Validate Action Button
            Button(
                onClick = {
                    if (otpCode.length == 4) {
                        onOtpVerified(otpCode)
                    }
                },
                enabled = otpCode.length == 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_confirm_otp"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FixoGold500,
                    disabledContainerColor = FixoNavy800
                )
            ) {
                Text(
                    text = if (language == AppLanguage.FR) "Valider & Continuer" else "Verify & Proceed",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (otpCode.length == 4) FixoNavy950 else FixoTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
