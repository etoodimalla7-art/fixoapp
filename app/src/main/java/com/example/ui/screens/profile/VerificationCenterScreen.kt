package com.example.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.theme.FixoAmber500
import com.example.ui.theme.FixoAmber600
import com.example.ui.theme.FixoBlue50
import com.example.ui.theme.FixoBlue600
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoEmerald600
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoRed500
import com.example.ui.theme.FixoSlate100
import com.example.ui.theme.FixoSlate200
import com.example.ui.theme.FixoSlate400
import com.example.ui.theme.FixoSlate500
import com.example.ui.theme.FixoSlate700

@Composable
fun VerificationCenterScreen(
    currentUser: User?,
    onBack: () -> Unit,
    onSubmitWorkerVerification: (cniNumber: String, tradeReg: String) -> Unit,
    onSubmitOrgVerification: (rccm: String, niu: String) -> Unit
) {
    val isWorker = currentUser?.role == UserRole.WORKER
    val isOrg = currentUser?.role == UserRole.ENTERPRISE
    val status = currentUser?.verificationStatus ?: VerificationStatus.UNVERIFIED

    var cniNumber by remember { mutableStateOf("") }
    var tradeRegNumber by remember { mutableStateOf("") }
    var rccmNumber by remember { mutableStateOf("") }
    var niuNumber by remember { mutableStateOf("") }

    var cniFrontUploaded by remember { mutableStateOf(status == VerificationStatus.VERIFIED_PRO) }
    var cniBackUploaded by remember { mutableStateOf(status == VerificationStatus.VERIFIED_PRO) }
    var selfieUploaded by remember { mutableStateOf(status == VerificationStatus.VERIFIED_PRO) }
    var certUploaded by remember { mutableStateOf(status == VerificationStatus.VERIFIED_PRO) }
    var isSubmitting by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Trust & Verification Center",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Cameroon Ministry of Employment & FIXO Standards",
                        style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 11.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Current Verification Status Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (status) {
                        VerificationStatus.MASTER_CRAFTSMAN, VerificationStatus.VERIFIED_PRO -> FixoEmerald500.copy(alpha = 0.12f)
                        VerificationStatus.PENDING -> FixoAmber500.copy(alpha = 0.12f)
                        VerificationStatus.UNVERIFIED -> FixoBlue50
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = when (status) {
                        VerificationStatus.MASTER_CRAFTSMAN, VerificationStatus.VERIFIED_PRO -> FixoEmerald600
                        VerificationStatus.PENDING -> FixoAmber600
                        VerificationStatus.UNVERIFIED -> FixoBlue600
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                when (status) {
                                    VerificationStatus.MASTER_CRAFTSMAN, VerificationStatus.VERIFIED_PRO -> FixoEmerald600
                                    VerificationStatus.PENDING -> FixoAmber600
                                    VerificationStatus.UNVERIFIED -> FixoNavy900
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (status) {
                                VerificationStatus.MASTER_CRAFTSMAN, VerificationStatus.VERIFIED_PRO -> Icons.Default.VerifiedUser
                                VerificationStatus.PENDING -> Icons.Default.Security
                                VerificationStatus.UNVERIFIED -> Icons.Default.Lock
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = when (status) {
                                VerificationStatus.MASTER_CRAFTSMAN -> "Master Craftsman Elite"
                                VerificationStatus.VERIFIED_PRO -> "Verified Certified Pro"
                                VerificationStatus.PENDING -> "Dossier Under Administrative Review"
                                VerificationStatus.UNVERIFIED -> "Account Unverified"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = when (status) {
                                VerificationStatus.MASTER_CRAFTSMAN -> "Highest trust tier unlocked. Priority job dispatch and VIP badge displayed on your profile."
                                VerificationStatus.VERIFIED_PRO -> "Full platform privileges unlocked. Official badge displayed on your public profile and quotes."
                                VerificationStatus.PENDING -> "Our compliance team in Douala is reviewing your national ID card and trade credentials. Typically takes 2-4 hours."
                                VerificationStatus.UNVERIFIED -> "Submit your Cameroonian National ID (CNI) and trade credentials to receive high-value client bookings."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 12.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 1: Phone & Contact Security
            VerificationStepItem(
                title = "Phone & Identity Confirmation",
                subtitle = "Cameroon cellular number verified via SMS OTP (+237)",
                icon = Icons.Default.Phone,
                isCompleted = true,
                badgeText = "Verified (+237)"
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isWorker || (!isWorker && !isOrg)) {
                // Artisan Verification Steps
                Text(
                    text = "Professional Artisan Credentials",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = cniNumber,
                    onValueChange = { cniNumber = it },
                    label = { Text("National ID Card (CNI) Number") },
                    placeholder = { Text("e.g. 118472901 or Passport Number") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = FixoBlue600)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tradeRegNumber,
                    onValueChange = { tradeRegNumber = it },
                    label = { Text("Trade Guild or Vocational Certification Number") },
                    placeholder = { Text("e.g. CAP / CQP Plumbing or GIC / Chambre des Métiers") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = FixoBlue600)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Upload Cards Grid
                Text(
                    text = "Document Photos & Liveness",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = FixoSlate700)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UploadTile(
                        title = "CNI Front",
                        isUploaded = cniFrontUploaded,
                        icon = Icons.Default.CameraAlt,
                        modifier = Modifier.weight(1f),
                        onClick = { cniFrontUploaded = !cniFrontUploaded }
                    )
                    UploadTile(
                        title = "CNI Back",
                        isUploaded = cniBackUploaded,
                        icon = Icons.Default.CameraAlt,
                        modifier = Modifier.weight(1f),
                        onClick = { cniBackUploaded = !cniBackUploaded }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UploadTile(
                        title = "Selfie Portrait",
                        isUploaded = selfieUploaded,
                        icon = Icons.Default.Face,
                        modifier = Modifier.weight(1f),
                        onClick = { selfieUploaded = !selfieUploaded }
                    )
                    UploadTile(
                        title = "Trade Diploma",
                        isUploaded = certUploaded,
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f),
                        onClick = { certUploaded = !certUploaded }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isSubmitting = true
                        onSubmitWorkerVerification(cniNumber, tradeRegNumber)
                        isSubmitting = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_artisan_verification_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text(
                            text = if (status == VerificationStatus.VERIFIED_PRO) "Update Verification Dossier" else "Submit Dossier for Review",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Organization Verification
                Text(
                    text = "Corporate Legal Entity Compliance",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = rccmNumber,
                    onValueChange = { rccmNumber = it },
                    label = { Text("Trade Register Number (RCCM)") },
                    placeholder = { Text("e.g. RC/DLA/2021/B/1892") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = FixoBlue600)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = niuNumber,
                    onValueChange = { niuNumber = it },
                    label = { Text("Taxpayer Identification Number (NIU)") },
                    placeholder = { Text("e.g. M01210003412P") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = FixoBlue600)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UploadTile(
                        title = "RCCM Certificate",
                        isUploaded = cniFrontUploaded,
                        icon = Icons.Default.Upload,
                        modifier = Modifier.weight(1f),
                        onClick = { cniFrontUploaded = !cniFrontUploaded }
                    )
                    UploadTile(
                        title = "Tax Attestation",
                        isUploaded = cniBackUploaded,
                        icon = Icons.Default.Upload,
                        modifier = Modifier.weight(1f),
                        onClick = { cniBackUploaded = !cniBackUploaded }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isSubmitting = true
                        onSubmitOrgVerification(rccmNumber, niuNumber)
                        isSubmitting = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_org_verification_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                    enabled = !isSubmitting
                ) {
                    Text(
                        text = "Submit Corporate Dossier for Verification",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificationStepItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isCompleted: Boolean,
    badgeText: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, FixoSlate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) FixoEmerald500.copy(alpha = 0.15f) else FixoSlate100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isCompleted) FixoEmerald600 else FixoSlate500,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = FixoSlate500)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isCompleted) FixoEmerald500.copy(alpha = 0.15f) else FixoSlate200)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) FixoEmerald600 else FixoSlate700
                    )
                )
            }
        }
    }
}

@Composable
private fun UploadTile(
    title: String,
    isUploaded: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUploaded) FixoEmerald500.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isUploaded) FixoEmerald600 else FixoSlate200
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isUploaded) FixoEmerald600 else FixoBlue50),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUploaded) Icons.Default.CheckCircle else icon,
                    contentDescription = null,
                    tint = if (isUploaded) Color.White else FixoBlue600,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (isUploaded) "Document Loaded" else "Tap to Upload",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = if (isUploaded) FixoEmerald600 else FixoSlate400
                )
            )
        }
    }
}
