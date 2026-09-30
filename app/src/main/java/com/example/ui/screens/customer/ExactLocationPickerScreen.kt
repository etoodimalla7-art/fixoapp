package com.example.ui.screens.customer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.CameroonLocationRegistry
import com.example.data.model.CameroonQuarter
import com.example.localization.AppLanguage
import com.example.ui.components.DoualaGeographicMap
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoNavy950
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import com.example.ui.theme.FixoWhite
import com.google.android.gms.location.LocationServices
import com.example.ui.components.LatLng
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * CHANTIER 2 : LE SÉLECTEUR D'ADRESSE EXACTE DU CLIENT (« PIN DROP »)
 *
 * Spécifications :
 * 1. Mode Sélecteur Interactif :
 *    - Épingle centrale fixe (📍) au milieu de l'écran avec cible au sol animée.
 *    - Déplacement fluide de la carte sous le repère (pan & pinch-to-zoom 2D zénithal).
 *    - Bouton rapide en bas à droite : [ 🎯 Me localiser ] via FusedLocationProviderClient.
 * 2. Champ d'Adresse & Repère Local :
 *    - Bandeau flottant au bas affichant le quartier détecté (ex: Akwa, Douala).
 *    - Champ de précision indispensable au Cameroun : "Repère / Précision (ex: Portail noir face Boulangerie Zepol)".
 *    - Bouton d'action proéminent : [ Confirmer cette adresse exacte d'intervention ].
 * 3. Visibilité Maximale :
 *    - Aucun Scaffold parasite ni en-tête rognant la carte.
 */
@Composable
fun ExactLocationPickerScreen(
    initialQuarter: CameroonQuarter,
    initialLandmark: String = "Portail noir face Boulangerie Zepol, Rue Drouot",
    language: AppLanguage = AppLanguage.FR,
    onLocationConfirmed: (quarter: CameroonQuarter, landmark: String, lat: Double, lng: Double) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedLat by remember { mutableDoubleStateOf(initialQuarter.lat) }
    var selectedLng by remember { mutableDoubleStateOf(initialQuarter.lng) }
    var currentQuarter by remember { mutableStateOf(initialQuarter) }
    var landmarkText by remember { mutableStateOf(initialLandmark) }

    val isDark = MaterialTheme.colorScheme.surface.let {
        (0.299 * it.red + 0.587 * it.green + 0.114 * it.blue) < 0.5
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Geolocation trigger function
    @SuppressLint("MissingPermission")
    fun requestUserLocation() {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        selectedLat = loc.latitude
                        selectedLng = loc.longitude
                        currentQuarter = findClosestDoualaQuarter(loc.latitude, loc.longitude)
                        Toast.makeText(
                            context,
                            if (language == AppLanguage.FR) "🎯 Position GPS détectée : ${currentQuarter.name}"
                            else "🎯 GPS Position detected: ${currentQuarter.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Fallback to Rue Drouot, Akwa
                        selectedLat = 4.0511
                        selectedLng = 9.7085
                        currentQuarter = findClosestDoualaQuarter(4.0511, 9.7085)
                    }
                }
            } catch (_: Throwable) {
                selectedLat = 4.0511
                selectedLng = 9.7085
                currentQuarter = findClosestDoualaQuarter(4.0511, 9.7085)
            }
        } else {
            // Permission not granted yet: center to Akwa
            selectedLat = 4.0511
            selectedLng = 9.7085
            currentQuarter = findClosestDoualaQuarter(4.0511, 9.7085)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            requestUserLocation()
        } else {
            Toast.makeText(
                context,
                if (language == AppLanguage.FR) "Autorisation GPS requise pour la localisation automatique"
                else "Location permission needed for automatic detection",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Douala quick shortcuts for popular intervention hubs
    val quickDoualaQuarters = remember {
        listOf(
            CameroonQuarter("Akwa", 4.0511, 9.7085),
            CameroonQuarter("Bonanjo", 4.0415, 9.6880),
            CameroonQuarter("Deido", 4.0620, 9.7100),
            CameroonQuarter("Bonapriso", 4.0150, 9.6920),
            CameroonQuarter("Bonamoussadi", 4.0880, 9.7360),
            CameroonQuarter("Makepe", 4.0720, 9.7450),
            CameroonQuarter("Bali", 4.0320, 9.6890),
            CameroonQuarter("Denver", 4.0820, 9.7280)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FixoBgCanvas)
            .testTag("exact_location_picker_screen")
    ) {
        // 1. FULLSCREEN 2D GOOGLE MAP WITH FIXED PIN DROP
        DoualaGeographicMap(
            centerLat = selectedLat,
            centerLng = selectedLng,
            initialZoom = 16.0f,
            isPinDropMode = true,
            onCameraPositionChanged = { newTarget ->
                selectedLat = newTarget.latitude
                selectedLng = newTarget.longitude
                currentQuarter = findClosestDoualaQuarter(newTarget.latitude, newTarget.longitude)
            },
            onLocateMe = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (hasPermission) {
                    requestUserLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. FLOATING TOP CONTROLS (Back Button & Clean Title Pill)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = FixoNavy900.copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                shadowElevation = 6.dp,
                modifier = Modifier.size(44.dp)
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_location_picker")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Fermer",
                        tint = FixoWhite
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FixoNavy900.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FixoEmerald500)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.FR) "SÉLECTEUR D'ADRESSE • DOUALA" else "ADDRESS PICKER • DOUALA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FixoGold500,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        // 3. FLOATING ACTION BUTTON: [ 🎯 Me localiser ]
        Button(
            onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (hasPermission) {
                    requestUserLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 270.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .testTag("btn_locate_me_gps"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FixoNavy900,
                contentColor = FixoGold500
            ),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, FixoGold500)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Me localiser",
                tint = FixoGold500,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (language == AppLanguage.FR) "🎯 Me localiser" else "🎯 My Location",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FixoWhite
            )
        }

        // 4. FLOATING BOTTOM CARD (Detected Quarter, Landmark Field & Confirmation Action)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .shadow(16.dp, RoundedCornerShape(22.dp))
                .testTag("location_picker_bottom_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDark) Color(0xFF111827) else Color(0xFFFFFFFF)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header: Detected District with Pin Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = FixoGold500.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = FixoGold500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${currentQuarter.name}, Douala",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                            )
                            Text(
                                text = String.format("GPS : %.4f° N, %.4f° E", selectedLat, selectedLng),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x2610B981)
                    ) {
                        Text(
                            text = if (language == AppLanguage.FR) "Précision Haute" else "High Accuracy",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FixoEmerald500,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Douala Quarter Chips (1-tap jump)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickDoualaQuarters) { q ->
                        val isSelected = q.name.equals(currentQuarter.name, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) FixoGold500 else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) FixoGold500 else Color.Transparent
                            ),
                            modifier = Modifier.clickable {
                                currentQuarter = q
                                selectedLat = q.lat
                                selectedLng = q.lng
                            }
                        ) {
                            Text(
                                text = q.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF080C15) else if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Landmark / Precision input (Essential in Cameroon)
                Text(
                    text = if (language == AppLanguage.FR)
                        "Repère / Précision d'accès (Indispensable pour l'artisan)"
                    else
                        "Landmark / Access Detail (Required for Craftsman)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = landmarkText,
                    onValueChange = { landmarkText = it },
                    placeholder = {
                        Text(
                            text = if (language == AppLanguage.FR)
                                "ex: Portail noir face Boulangerie Zepol, Rue Drouot"
                            else
                                "e.g. Black gate opposite Zepol Bakery, Rue Drouot",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_address_landmark"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                        unfocusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                        focusedBorderColor = FixoGold500,
                        unfocusedBorderColor = if (isDark) Color(0x33FFFFFF) else Color(0xFFCBD5E1),
                        focusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                        unfocusedTextColor = if (isDark) Color.White else Color(0xFF0F172A)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Big Action Button: [ Confirmer cette adresse exacte d'intervention ]
                Button(
                    onClick = {
                        onLocationConfirmed(
                            currentQuarter,
                            landmarkText.ifBlank { "Akwa, Rue Drouot" },
                            selectedLat,
                            selectedLng
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_confirm_intervention_address"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FixoGold500,
                        contentColor = Color(0xFF080C15)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF080C15),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.FR)
                            "Confirmer cette adresse exacte d'intervention"
                        else
                            "Confirm Exact Intervention Address",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF080C15)
                    )
                }
            }
        }
    }
}

/**
 * Finds the nearest registered Douala quarter based on given geographic coordinates.
 */
fun findClosestDoualaQuarter(lat: Double, lng: Double): CameroonQuarter {
    val doualaQuarters = CameroonLocationRegistry.regions
        .firstOrNull { it.name.equals("Littoral", ignoreCase = true) }
        ?.cities?.firstOrNull { it.name.equals("Douala", ignoreCase = true) }
        ?.quarters ?: emptyList()

    return doualaQuarters.minByOrNull { q ->
        val dLat = q.lat - lat
        val dLng = q.lng - lng
        dLat.pow(2) + dLng.pow(2)
    } ?: CameroonQuarter("Akwa", 4.0511, 9.7085)
}
