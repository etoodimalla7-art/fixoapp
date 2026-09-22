package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FixoSeedData
import com.example.data.model.CameroonLocationRegistry
import com.example.data.model.ServiceCategory
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.localization.AppLanguage
import com.example.localization.FixoStrings
import com.example.ui.theme.FixoAmber500
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
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    currentLanguage: AppLanguage,
    onLogin: (email: String, role: UserRole) -> Unit,
    onQuickLogin: (User) -> Unit,
    onGoogleSignIn: (UserRole) -> Unit = {},
    isDevEnvironment: Boolean = false,
    onToggleEnvironment: () -> Unit = {},
    onRegisterCustomer: (name: String, username: String, phone: String, email: String, region: String, city: String, quarter: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onRegisterWorker: (name: String, username: String, phone: String, email: String, category: ServiceCategory, hourlyRate: Double, bio: String, serviceArea: String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onRegisterOrg: (name: String, type: String, description: String, phone: String, email: String, address: String, city: String, region: String, regNumber: String, repName: String, repTitle: String) -> Unit = { _, _, _, _, _, _, _, _, _, _, _ -> },
    initialRole: UserRole = UserRole.CUSTOMER,
    modifier: Modifier = Modifier
) {
    // Top Tabs: 0 = Sign In, 1 = Create Account
    var mainTab by remember { mutableIntStateOf(0) }

    // Sign In Mode: 0 = Phone OTP, 1 = Email / Password
    var signInMode by remember { mutableIntStateOf(0) }

    // Role for registration or sign in
    var activeRole by remember { mutableStateOf(initialRole) }

    // Phone OTP state
    var phoneInput by remember { mutableStateOf("+237 671 234 567") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var resendCooldown by remember { mutableIntStateOf(60) }

    // Timer effect for OTP
    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            resendCooldown = 60
            while (resendCooldown > 0) {
                delay(1000)
                resendCooldown--
            }
        }
    }

    // Email / Password state
    var emailInput by remember { mutableStateOf("sarah.j@gmail.com") }
    var passwordInput by remember { mutableStateOf("password123") }

    // Registration shared state
    var regFullName by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("+237 6") }
    var regEmail by remember { mutableStateOf("") }
    var regRegion by remember { mutableStateOf("Littoral") }
    var regCity by remember { mutableStateOf("Douala") }
    var regQuarter by remember { mutableStateOf("Akwa") }

    // Artisan specific
    var workerCategory by remember { mutableStateOf(ServiceCategory.PLUMBING) }
    var workerHourlyRate by remember { mutableStateOf("15000") }
    var workerBio by remember { mutableStateOf("") }
    var workerServiceArea by remember { mutableStateOf("Akwa, Bonamoussadi, Bonapriso") }

    // Enterprise specific
    var orgName by remember { mutableStateOf("") }
    var orgType by remember { mutableStateOf("Construction & Renovation") }
    var orgRegNumber by remember { mutableStateOf("") }
    var orgAddress by remember { mutableStateOf("") }
    var orgRepName by remember { mutableStateOf("") }
    var orgRepTitle by remember { mutableStateOf("Managing Director") }
    var orgDescription by remember { mutableStateOf("") }

    val isUsernameValid = regUsername.trim().length >= 3

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // FIXO Brand Logo & Slogan
            Surface(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(18.dp)),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.fixo_logo),
                        contentDescription = "FIXO Logo",
                        modifier = Modifier.size(54.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "FIXO",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Certified Artisans & Escrow Protection in Cameroon",
                style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Main Tab Switcher: Sign In vs Register
            TabRow(
                selectedTabIndex = mainTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = mainTab == 0,
                    onClick = { mainTab = 0 },
                    text = {
                        Text(
                            text = "Sign In",
                            fontWeight = if (mainTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (mainTab == 0) FixoBlue600 else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                Tab(
                    selected = mainTab == 1,
                    onClick = { mainTab = 1 },
                    text = {
                        Text(
                            text = "Create Account",
                            fontWeight = if (mainTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (mainTab == 1) FixoBlue600 else MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (mainTab == 0) {
                // ==================== SIGN IN WORKFLOW ====================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (signInMode == 0) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { signInMode = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Phone (SMS OTP)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (signInMode == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (signInMode == 0) FixoBlue600 else FixoSlate500
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (signInMode == 1) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { signInMode = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Email & Password",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (signInMode == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (signInMode == 1) FixoBlue600 else FixoSlate500
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (signInMode == 0) {
                    // Phone OTP Form
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Cameroon Mobile (+237)") },
                        placeholder = { Text("+237 6XX XXX XXX") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = FixoBlue600)
                        },
                        trailingIcon = {
                            if (!isOtpSent) {
                                TextButton(
                                    onClick = { isOtpSent = true },
                                    enabled = phoneInput.length >= 9
                                ) {
                                    Text("Send Code", fontWeight = FontWeight.Bold, color = FixoBlue600, fontSize = 12.sp)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedVisibility(visible = isOtpSent) {
                        Column {
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { if (it.length <= 6) otpCode = it },
                                label = { Text("6-Digit SMS Verification Code") },
                                placeholder = { Text("123456") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = FixoEmerald600)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Code sent to $phoneInput",
                                    style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 11.sp)
                                )
                                TextButton(
                                    onClick = { resendCooldown = 60 },
                                    enabled = resendCooldown == 0
                                ) {
                                    Text(
                                        text = if (resendCooldown > 0) "Resend in ${resendCooldown}s" else "Resend Code",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val email = if (phoneInput.contains("@")) phoneInput else "$phoneInput@fixo.cm"
                            onLogin(email, activeRole)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_phone_otp_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600)
                    ) {
                        Text(
                            text = if (isOtpSent) "Verify Code & Sign In" else "Request SMS OTP Code",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    // Email / Password Form
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = FixoBlue600)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = FixoBlue600)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onLogin(emailInput, activeRole) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_email_login_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600)
                    ) {
                        Text("Sign In with Email", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Google Sign In
                Button(
                    onClick = { onGoogleSignIn(activeRole) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("google_signin_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FixoSlate200)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("G", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FixoBlue600)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continue with Google", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            } else {
                // ==================== REGISTRATION WORKFLOW ====================
                Text(
                    text = "Select Account Type",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = FixoSlate700),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 3-way Role Selector Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoleSelectChip(
                        title = "Client",
                        role = UserRole.CUSTOMER,
                        isSelected = activeRole == UserRole.CUSTOMER,
                        modifier = Modifier.weight(1f),
                        onClick = { activeRole = UserRole.CUSTOMER }
                    )
                    RoleSelectChip(
                        title = "Artisan",
                        role = UserRole.WORKER,
                        isSelected = activeRole == UserRole.WORKER,
                        modifier = Modifier.weight(1f),
                        onClick = { activeRole = UserRole.WORKER }
                    )
                    RoleSelectChip(
                        title = "Enterprise",
                        role = UserRole.ENTERPRISE,
                        isSelected = activeRole == UserRole.ENTERPRISE,
                        modifier = Modifier.weight(1f),
                        onClick = { activeRole = UserRole.ENTERPRISE }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (activeRole) {
                    UserRole.CUSTOMER -> {
                        // Customer Registration Fields
                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Sarah Jenkins") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = FixoBlue600)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regUsername,
                            onValueChange = { regUsername = it.lowercase().filter { c -> c.isLetterOrDigit() || c == '_' } },
                            label = { Text("Username Handle (@handle)") },
                            placeholder = { Text("sarah_j") },
                            prefix = { Text("@", fontWeight = FontWeight.Bold, color = FixoBlue600) },
                            trailingIcon = {
                                if (isUsernameValid) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Available", tint = FixoEmerald600)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Cameroon Phone (+237)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = FixoBlue600)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regQuarter,
                            onValueChange = { regQuarter = it },
                            label = { Text("Quarter / Neighborhood (Douala / Yaoundé)") },
                            placeholder = { Text("e.g. Akwa, Bonamoussadi, Bastos") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = FixoBlue600)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                onRegisterCustomer(
                                    regFullName,
                                    regUsername,
                                    regPhone,
                                    regEmail,
                                    regRegion,
                                    regCity,
                                    regQuarter
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_customer_registration_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                            enabled = regFullName.isNotBlank() && isUsernameValid
                        ) {
                            Text("Create Client Account", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    UserRole.WORKER -> {
                        // Artisan Registration Fields
                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text("Artisan / Professional Name") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = FixoBlue600)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regUsername,
                            onValueChange = { regUsername = it.lowercase().filter { c -> c.isLetterOrDigit() || c == '_' } },
                            label = { Text("Artisan Handle (@handle)") },
                            prefix = { Text("@", fontWeight = FontWeight.Bold, color = FixoBlue600) },
                            trailingIcon = {
                                if (isUsernameValid) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Available", tint = FixoEmerald600)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Trade Specialty",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = FixoSlate700),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(ServiceCategory.values()) { cat ->
                                val isSelected = cat == workerCategory
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) FixoBlue600 else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { workerCategory = cat }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = workerHourlyRate,
                            onValueChange = { workerHourlyRate = it },
                            label = { Text("Base Hourly / Diagnostic Rate (FCFA)") },
                            leadingIcon = {
                                Text("XAF", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FixoEmerald600, modifier = Modifier.padding(start = 12.dp, end = 4.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = workerBio,
                            onValueChange = { workerBio = it },
                            label = { Text("Trade Background & Certifications") },
                            placeholder = { Text("e.g. 10 years experience in commercial plumbing and PEX installations.") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val rate = workerHourlyRate.toDoubleOrNull() ?: 15000.0
                                onRegisterWorker(
                                    regFullName,
                                    regUsername,
                                    regPhone,
                                    regEmail,
                                    workerCategory,
                                    rate,
                                    workerBio,
                                    workerServiceArea
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_worker_registration_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                            enabled = regFullName.isNotBlank() && isUsernameValid
                        ) {
                            Text("Register as Certified Artisan", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    UserRole.ENTERPRISE -> {
                        // Enterprise Contractor Registration
                        OutlinedTextField(
                            value = orgName,
                            onValueChange = { orgName = it },
                            label = { Text("Company Legal Name") },
                            placeholder = { Text("e.g. Bâtir Cameroon SARL") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = FixoBlue600)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = orgRegNumber,
                            onValueChange = { orgRegNumber = it },
                            label = { Text("RCCM / NIU Registration Number") },
                            placeholder = { Text("RC/DLA/2022/B/1420 - NIU M02...") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = FixoBlue600)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = orgRepName,
                            onValueChange = { orgRepName = it },
                            label = { Text("Authorized Representative Full Name") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = FixoBlue600)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = orgAddress,
                            onValueChange = { orgAddress = it },
                            label = { Text("Headquarters Address (City, Quarter)") },
                            placeholder = { Text("Boulevard de la Liberté, Akwa, Douala") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Apartment, contentDescription = null, tint = FixoBlue600)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                onRegisterOrg(
                                    orgName,
                                    orgType,
                                    orgDescription,
                                    regPhone,
                                    regEmail,
                                    orgAddress,
                                    "Douala",
                                    "Littoral",
                                    orgRegNumber,
                                    orgRepName,
                                    orgRepTitle
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_org_registration_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FixoBlue600),
                            enabled = orgName.isNotBlank() && orgRepName.isNotBlank()
                        ) {
                            Text("Create Enterprise Workspace", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Sandbox Profiles for Reviewers / QA
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoSlate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick Demo Access",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(FixoAmber500.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("SANDBOX", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FixoAmber500)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Instant 1-tap sign in to verified user accounts:",
                        style = MaterialTheme.typography.bodySmall.copy(color = FixoSlate500, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FixoSeedData.defaultUsers.forEach { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                .clickable { onQuickLogin(user) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        modifier = Modifier.size(13.dp),
                                        tint = FixoEmerald600
                                    )
                                }
                                Text(
                                    text = "${user.email} • ${user.phone}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = FixoSlate500)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(FixoBlue600.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = user.role.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FixoBlue600
                                    )
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

@Composable
private fun RoleSelectChip(
    title: String,
    role: UserRole,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) FixoNavy900 else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
