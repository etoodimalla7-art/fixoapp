package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint
import android.graphics.Rect as AndroidRect
import android.graphics.Typeface as AndroidTypeface
import android.os.Bundle
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.ServiceCategory
import com.example.data.model.WorkerProfile
import com.example.ui.theme.FixoEmerald500
import com.example.ui.theme.FixoGold500
import com.example.ui.theme.FixoNavy900
import com.example.ui.theme.FixoWhite
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class MapArtisanPoint(
    val worker: WorkerProfile,
    val lat: Double,
    val lng: Double,
    val quarter: String
)

data class ExtrudedBuilding(
    val lat: Double,
    val lng: Double,
    val widthMeters: Float,
    val heightMeters: Float,
    val elevationHeight: Float // Extrusion height in 3D
)

/**
 * GOOGLE MAPS SDK FOR ANDROID INTEGRATION
 * Features:
 * - Real Google Maps SDK (MapView / GoogleMap)
 * - 3D Perspective View centered on Akwa, Douala (Lat: 4.0511° N, Lng: 9.7085° E)
 *   with tilt = 45.0f, bearing = 30.0f, zoom = 16.5f
 * - 3D extruded buildings rendering enabled (googleMap.isBuildingsEnabled = true)
 * - Dynamic Day / Night Theme:
 *   - Dark Mode: "Obsidian & Gold" custom styling JSON (anthracite roads, deep Wouri water #0A0F1D)
 *   - Light Mode: "Silver Minimalist" styling JSON (crisp silver roads, soft river blue)
 * - High-Definition 3D Artisan Markers:
 *   - Custom vector bitmap: warm amber gold (#F59E0B) circular badge with 2 dp white border,
 *     trade icon, and drop shadow simulating floating over the pavement
 *   - Pulse radar wave animation
 * - Marker click interaction triggering technician selection
 * - Interactive 3D camera controls: 3D perspective toggle, zoom in/out, recenter on Akwa
 * - Graceful fallback to 3D vector canvas when Google Play Services is unavailable (e.g. JVM Robolectric unit tests)
 */
@Composable
fun DoualaGeographicMap(
    workers: List<WorkerProfile>,
    selectedWorker: WorkerProfile?,
    onSelectWorker: (WorkerProfile) -> Unit,
    modifier: Modifier = Modifier,
    centerLat: Double = 4.0511,
    centerLng: Double = 9.7085
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Check Google Play Services availability
    val isGooglePlayServicesAvailable = remember {
        try {
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        } catch (_: Throwable) {
            false
        }
    }

    // Dynamic Day/Night Theme detection
    val isDark = MaterialTheme.colorScheme.surface.let {
        (0.299 * it.red + 0.587 * it.green + 0.114 * it.blue) < 0.5
    }

    // 3D Perspective Camera Parameters
    var tiltDeg by remember { mutableFloatStateOf(45.0f) }
    var bearingDeg by remember { mutableFloatStateOf(30.0f) }
    var zoomLevel by remember { mutableFloatStateOf(16.5f) }
    var is3DMode by remember { mutableStateOf(true) }

    // Map artisans to real geographic coordinates in Douala
    val artisanPoints = remember(workers) {
        workers.mapIndexed { index, worker ->
            val (lat, lng, quarter) = when (index % 4) {
                0 -> Triple(4.0520, 9.7095, "Akwa (Bd de la Liberté)")
                1 -> Triple(4.0610, 9.7160, "Deïdo (Rond-Point)")
                2 -> Triple(4.0415, 9.6880, "Bonanjo (Bd Leclerc)")
                else -> Triple(4.0325, 9.7030, "Bonapriso (Av. de Gaulle)")
            }
            MapArtisanPoint(worker = worker, lat = lat, lng = lng, quarter = quarter)
        }
    }

    // Hold reference to active GoogleMap instance for camera controls
    var activeGoogleMap by remember { mutableStateOf<GoogleMap?>(null) }
    val markerList = remember { mutableListOf<Marker>() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("douala_geographic_map_container")
    ) {
        if (isGooglePlayServicesAvailable) {
            // NATIVE GOOGLE MAPS SDK VIEW
            val mapView = remember {
                MapView(context).apply {
                    onCreate(Bundle())
                }
            }

            DisposableEffect(lifecycleOwner, mapView) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> mapView.onStart()
                        Lifecycle.Event.ON_RESUME -> mapView.onResume()
                        Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                        Lifecycle.Event.ON_STOP -> mapView.onStop()
                        Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    mapView.onDestroy()
                }
            }

            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    view.getMapAsync { googleMap ->
                        activeGoogleMap = googleMap

                        // 1. Enable 3D Buildings Rendering
                        googleMap.isBuildingsEnabled = true

                        // 2. Configure UI Controls
                        googleMap.uiSettings.isCompassEnabled = true
                        googleMap.uiSettings.isTiltGesturesEnabled = true
                        googleMap.uiSettings.isRotateGesturesEnabled = true
                        googleMap.uiSettings.isZoomControlsEnabled = false
                        googleMap.uiSettings.isMapToolbarEnabled = false
                        googleMap.uiSettings.isMyLocationButtonEnabled = false

                        // 3. Apply Dynamic Day / Night JSON Map Style
                        val mapStyleJson = if (isDark) OBSIDIAN_GOLD_MAP_STYLE else SILVER_MINIMALIST_MAP_STYLE
                        try {
                            googleMap.setMapStyle(MapStyleOptions(mapStyleJson))
                        } catch (_: Exception) {}

                        // 4. Set 3D Perspective Camera
                        val cameraPosition = CameraPosition.builder()
                            .target(LatLng(centerLat, centerLng))
                            .zoom(zoomLevel)
                            .tilt(if (is3DMode) tiltDeg else 0f)
                            .bearing(bearingDeg)
                            .build()
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                        // 5. Populate High-Definition 3D Artisan Markers
                        markerList.forEach { it.remove() }
                        markerList.clear()

                        artisanPoints.forEach { point ->
                            val isSelected = selectedWorker?.id == point.worker.id
                            val markerBitmap = createArtisanMarkerBitmap(context, point.worker, isSelected)
                            val markerOptions = MarkerOptions()
                                .position(LatLng(point.lat, point.lng))
                                .title(point.worker.name)
                                .snippet("${point.worker.category.displayName} • 15 000 FCFA")
                                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap))
                                .anchor(0.5f, 0.85f)

                            val marker = googleMap.addMarker(markerOptions)
                            marker?.let {
                                it.tag = point.worker
                                markerList.add(it)
                            }
                        }

                        // 6. Marker Click Handler
                        googleMap.setOnMarkerClickListener { marker ->
                            val worker = marker.tag as? WorkerProfile
                            if (worker != null) {
                                onSelectWorker(worker)
                                true
                            } else {
                                false
                            }
                        }
                    }
                }
            )
        } else {
            // GRACEFUL VECTOR 3D FALLBACK CANVAS (Ensures JVM Robolectric tests and previews never crash)
            Douala3DVectorFallbackCanvas(
                workers = workers,
                selectedWorker = selectedWorker,
                onSelectWorker = onSelectWorker,
                isDark = isDark,
                is3DMode = is3DMode,
                tiltDeg = tiltDeg,
                bearingDeg = bearingDeg,
                centerLat = centerLat,
                centerLng = centerLng
            )
        }

        // FLOATING 3D MAP CONTROLS OVERLAY
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            // Toggle 3D Perspective (tilt 45° vs 0°)
            Surface(
                shape = CircleShape,
                color = if (is3DMode) FixoGold500 else FixoNavy900.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (is3DMode) FixoWhite else Color(0x33FFFFFF)),
                shadowElevation = 6.dp
            ) {
                IconButton(
                    onClick = {
                        is3DMode = !is3DMode
                        tiltDeg = if (is3DMode) 45.0f else 0.0f
                        activeGoogleMap?.let { gMap ->
                            val currentPos = gMap.cameraPosition
                            val newPos = CameraPosition.builder(currentPos)
                                .tilt(tiltDeg)
                                .build()
                            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPos))
                        }
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = "Toggle 3D Buildings",
                        tint = if (is3DMode) Color(0xFF080C15) else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Zoom In
            Surface(
                shape = CircleShape,
                color = FixoNavy900.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                shadowElevation = 4.dp
            ) {
                IconButton(
                    onClick = {
                        zoomLevel = (zoomLevel + 1.0f).coerceAtMost(20.0f)
                        activeGoogleMap?.animateCamera(CameraUpdateFactory.zoomIn())
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = FixoWhite)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Zoom Out
            Surface(
                shape = CircleShape,
                color = FixoNavy900.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                shadowElevation = 4.dp
            ) {
                IconButton(
                    onClick = {
                        zoomLevel = (zoomLevel - 1.0f).coerceAtLeast(10.0f)
                        activeGoogleMap?.animateCamera(CameraUpdateFactory.zoomOut())
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = FixoWhite)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recenter on Akwa, Douala
            Surface(
                shape = CircleShape,
                color = FixoNavy900.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500),
                shadowElevation = 4.dp
            ) {
                IconButton(
                    onClick = {
                        tiltDeg = 45.0f
                        bearingDeg = 30.0f
                        zoomLevel = 16.5f
                        is3DMode = true
                        activeGoogleMap?.let { gMap ->
                            val cameraPosition = CameraPosition.builder()
                                .target(LatLng(centerLat, centerLng))
                                .zoom(16.5f)
                                .tilt(45.0f)
                                .bearing(30.0f)
                                .build()
                            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        }
                    },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Recenter Akwa", tint = FixoGold500)
                }
            }
        }
    }
}

/**
 * Creates high-definition 3D artisan marker bitmap with amber gold circle (#F59E0B),
 * 2 dp white border, trade symbol, and ground drop shadow simulating floating over pavement.
 */
private fun createArtisanMarkerBitmap(
    context: Context,
    worker: WorkerProfile,
    isSelected: Boolean
): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (54 * density).toInt().coerceAtLeast(64)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)

    // 1. Realistic ground drop shadow (oval below the pin)
    val shadowPaint = AndroidPaint().apply {
        isAntiAlias = true
        color = AndroidColor.argb(85, 0, 0, 0)
    }
    canvas.drawOval(
        sizePx * 0.16f,
        sizePx * 0.78f,
        sizePx * 0.84f,
        sizePx * 0.94f,
        shadowPaint
    )

    // 2. Main Amber Gold Circle (#F59E0B)
    val circlePaint = AndroidPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#F59E0B")
    }
    val circleRadius = sizePx * 0.35f
    val circleCenterX = sizePx / 2f
    val circleCenterY = sizePx * 0.40f
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, circlePaint)

    // 3. Crisp 2 dp White Ring
    val ringPaint = AndroidPaint().apply {
        isAntiAlias = true
        style = AndroidPaint.Style.STROKE
        strokeWidth = 2.5f * density
        color = if (isSelected) AndroidColor.WHITE else AndroidColor.parseColor("#FFFFFF")
    }
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, ringPaint)

    // 4. Trade Symbol
    val symbol = when (worker.category) {
        ServiceCategory.PLUMBING -> "🔧"
        ServiceCategory.ELECTRICAL -> "⚡"
        ServiceCategory.AC_COOLING -> "❄️"
        else -> "🛠️"
    }
    val textPaint = AndroidPaint().apply {
        isAntiAlias = true
        textSize = 15f * density
        color = AndroidColor.parseColor("#080C15")
        typeface = AndroidTypeface.DEFAULT_BOLD
        textAlign = AndroidPaint.Align.CENTER
    }
    val bounds = AndroidRect()
    textPaint.getTextBounds(symbol, 0, symbol.length, bounds)
    canvas.drawText(symbol, circleCenterX, circleCenterY + (bounds.height() / 2f), textPaint)

    return bitmap
}

/**
 * Dynamic "Obsidian & Gold" Map Style JSON for Dark Mode:
 * - Wouri River deep night blue #0A0F1D
 * - Urban ground dark obsidian #080C15
 * - Roads anthracite #1E293B
 * - Minimal POI clutter
 */
private const val OBSIDIAN_GOLD_MAP_STYLE = """
[
  {
    "elementType": "geometry",
    "stylers": [{"color": "#080c15"}]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#94a3b8"}]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [{"color": "#080c15"}]
  },
  {
    "featureType": "administrative.locality",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#f59e0b"}]
  },
  {
    "featureType": "poi",
    "stylers": [{"visibility": "off"}]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [{"color": "#1e293b"}]
  },
  {
    "featureType": "road",
    "elementType": "geometry.stroke",
    "stylers": [{"color": "#141c2a"}]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [{"color": "#253347"}]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry.stroke",
    "stylers": [{"color": "#1e293b"}]
  },
  {
    "featureType": "transit",
    "elementType": "geometry",
    "stylers": [{"color": "#1e293b"}]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [{"color": "#0a0f1d"}]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#38bdf8"}]
  }
]
"""

/**
 * Dynamic "Silver Minimalist" Map Style JSON for Light Mode:
 * - Clean pearl ground #F8FAFC
 * - Roads crisp white with subtle gray borders
 * - Soft clean river blue #93C5FD
 */
private const val SILVER_MINIMALIST_MAP_STYLE = """
[
  {
    "elementType": "geometry",
    "stylers": [{"color": "#f8fafc"}]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [{"color": "#0f172a"}]
  },
  {
    "featureType": "poi",
    "stylers": [{"visibility": "off"}]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [{"color": "#ffffff"}]
  },
  {
    "featureType": "road",
    "elementType": "geometry.stroke",
    "stylers": [{"color": "#cbd5e1"}]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [{"color": "#f1f5f9"}]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry.stroke",
    "stylers": [{"color": "#cbd5e1"}]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [{"color": "#93c5fd"}]
  }
]
"""

/**
 * Graceful 3D Perspective Vector Canvas Fallback:
 * Provides seamless rendering for Robolectric JVM unit tests and environments
 * without Google Play Services.
 */
@Composable
private fun Douala3DVectorFallbackCanvas(
    workers: List<WorkerProfile>,
    selectedWorker: WorkerProfile?,
    onSelectWorker: (WorkerProfile) -> Unit,
    isDark: Boolean,
    is3DMode: Boolean,
    tiltDeg: Float,
    bearingDeg: Float,
    centerLat: Double,
    centerLng: Double
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "FloatPulse")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatBob"
    )
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    val mapBgColor = if (isDark) Color(0xFF080C15) else Color(0xFFF1F5F9)
    val riverWaterColor = if (isDark) Color(0xFF0A0F1D) else Color(0xFF93C5FD)
    val roadMainColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val roadSecondaryColor = if (isDark) Color(0xFF141C2A) else Color(0xFFE2E8F0)
    val roadCurbColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
    val buildingBaseColor = if (isDark) Color(0xFF111827) else Color(0xFFE2E8F0)
    val buildingRoofColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFFFFFF)
    val buildingShadowColor = if (isDark) Color(0xFF060A10) else Color(0xFFCBD5E1)

    val artisanPoints = remember(workers) {
        workers.mapIndexed { index, worker ->
            val (lat, lng, quarter) = when (index % 4) {
                0 -> Triple(4.0520, 9.7095, "Akwa (Bd de la Liberté)")
                1 -> Triple(4.0610, 9.7160, "Deïdo (Rond-Point)")
                2 -> Triple(4.0415, 9.6880, "Bonanjo (Bd Leclerc)")
                else -> Triple(4.0325, 9.7030, "Bonapriso (Av. de Gaulle)")
            }
            MapArtisanPoint(worker = worker, lat = lat, lng = lng, quarter = quarter)
        }
    }

    val buildings = remember {
        listOf(
            ExtrudedBuilding(4.0535, 9.7070, 45f, 60f, 32f),
            ExtrudedBuilding(4.0500, 9.7060, 50f, 40f, 26f),
            ExtrudedBuilding(4.0490, 9.7115, 40f, 55f, 28f),
            ExtrudedBuilding(4.0440, 9.6910, 60f, 50f, 38f),
            ExtrudedBuilding(4.0460, 9.6950, 45f, 45f, 24f),
            ExtrudedBuilding(4.0570, 9.7050, 35f, 40f, 20f),
            ExtrudedBuilding(4.0350, 9.7010, 50f, 65f, 25f)
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(mapBgColor)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(0.7f, 2.8f)
                    panOffsetX += pan.x
                    panOffsetY += pan.y
                }
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        fun project3D(lat: Double, lng: Double, altitudePx: Float = 0f): Offset {
            val scaleX = widthPx * 14.5f * zoomScale
            val scaleY = heightPx * 14.5f * zoomScale
            val dx = ((lng - centerLng) * scaleX).toFloat()
            val dy = -((lat - centerLat) * scaleY).toFloat()

            val rad = Math.toRadians(bearingDeg.toDouble())
            val cosB = cos(rad).toFloat()
            val sinB = sin(rad).toFloat()
            val rx = dx * cosB - dy * sinB
            val ry = dx * sinB + dy * cosB

            val tiltRad = Math.toRadians((if (is3DMode) tiltDeg else 0f).toDouble())
            val cosT = cos(tiltRad).toFloat()
            val ty = ry * cosT - altitudePx

            return Offset(widthPx / 2f + rx + panOffsetX, heightPx / 2f + ty + panOffsetY)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = if (isDark)
                        listOf(Color(0xFF060A12), Color(0xFF0D1424))
                    else
                        listOf(Color(0xFFE2E8F0), Color(0xFFF8FAFC))
                )
            )

            // Fleuve Wouri 3D
            val riverPath = Path().apply {
                val p1 = project3D(4.0720, 9.6820)
                val p2 = project3D(4.0560, 9.6760)
                val p3 = project3D(4.0410, 9.6710)
                val p4 = project3D(4.0240, 9.6640)
                moveTo(p1.x, p1.y)
                quadraticTo(p2.x, p2.y, p3.x, p3.y)
                lineTo(p4.x, p4.y)
            }
            drawPath(
                path = riverPath,
                color = riverWaterColor,
                style = Stroke(width = 62.dp.toPx() * zoomScale, cap = StrokeCap.Round)
            )

            // Pont sur le Wouri
            val bridgeStart = project3D(4.0620, 9.6860, 10f)
            val bridgeEnd = project3D(4.0655, 9.6680, 10f)
            drawLine(
                color = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                start = bridgeStart,
                end = bridgeEnd,
                strokeWidth = 7.dp.toPx() * zoomScale,
                cap = StrokeCap.Square
            )

            // Réseau Routier 3D
            val roadBoulevards = listOf(
                Pair(Pair(4.0610, 9.7030), Pair(4.0440, 9.7130)),
                Pair(Pair(4.0640, 9.7070), Pair(4.0470, 9.7190)),
                Pair(Pair(4.0540, 9.6970), Pair(4.0400, 9.7160)),
                Pair(Pair(4.0410, 9.6980), Pair(4.0240, 9.7060)),
                Pair(Pair(4.0470, 9.6880), Pair(4.0390, 9.7010))
            )
            roadBoulevards.forEach { (start, end) ->
                drawLine(
                    color = roadCurbColor,
                    start = project3D(start.first, start.second),
                    end = project3D(end.first, end.second),
                    strokeWidth = 14.dp.toPx() * zoomScale,
                    cap = StrokeCap.Round
                )
            }
            roadBoulevards.forEach { (start, end) ->
                drawLine(
                    color = roadMainColor,
                    start = project3D(start.first, start.second),
                    end = project3D(end.first, end.second),
                    strokeWidth = 10.dp.toPx() * zoomScale,
                    cap = StrokeCap.Round
                )
            }

            // Bâtiments 3D Extrudés
            if (is3DMode) {
                buildings.forEach { bldg ->
                    val groundBase = project3D(bldg.lat, bldg.lng, 0f)
                    val roofTop = project3D(bldg.lat, bldg.lng, bldg.elevationHeight * zoomScale)
                    val bWidth = (bldg.widthMeters * 0.45f * zoomScale)
                    val bHeight = (bldg.heightMeters * 0.35f * zoomScale)

                    drawRoundRect(
                        color = buildingShadowColor,
                        topLeft = Offset(groundBase.x - bWidth / 2, groundBase.y - bHeight / 2),
                        size = Size(bWidth, bHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    val wallPath = Path().apply {
                        moveTo(groundBase.x - bWidth / 2, groundBase.y + bHeight / 2)
                        lineTo(groundBase.x + bWidth / 2, groundBase.y + bHeight / 2)
                        lineTo(roofTop.x + bWidth / 2, roofTop.y + bHeight / 2)
                        lineTo(roofTop.x - bWidth / 2, roofTop.y + bHeight / 2)
                        close()
                    }
                    drawPath(wallPath, color = buildingBaseColor)

                    drawRoundRect(
                        color = buildingRoofColor,
                        topLeft = Offset(roofTop.x - bWidth / 2, roofTop.y - bHeight / 2),
                        size = Size(bWidth, bHeight),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                }
            }

            // Pulse wave around center
            val clientGround = project3D(centerLat, centerLng, 0f)
            drawCircle(
                color = FixoEmerald500.copy(alpha = pulseAlpha),
                radius = pulseRadius * zoomScale,
                center = clientGround
            )
            drawCircle(
                color = FixoEmerald500,
                radius = 8.dp.toPx() * zoomScale,
                center = clientGround
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx() * zoomScale,
                center = clientGround
            )
        }

        // Marqueurs 3D d'Artisans Flottants
        artisanPoints.forEach { point ->
            val groundPos = project3D(point.lat, point.lng, 0f)
            val isSelected = selectedWorker?.id == point.worker.id
            val altitudeHover = if (is3DMode) (22f + floatOffset) else 0f

            Box(
                modifier = Modifier
                    .offset { IntOffset(groundPos.x.roundToInt() - 26, groundPos.y.roundToInt() - 44) }
            ) {
                if (is3DMode) {
                    Box(
                        modifier = Modifier
                            .offset(y = 36.dp)
                            .size(36.dp, 14.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .align(Alignment.BottomCenter)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = FixoGold500,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isSelected) Color.White else Color.White.copy(alpha = 0.95f)
                    ),
                    shadowElevation = if (is3DMode) 10.dp else 4.dp,
                    modifier = Modifier
                        .offset(y = (-altitudeHover).dp)
                        .size(46.dp)
                        .clickable { onSelectWorker(point.worker) }
                        .testTag("map_marker_artisan_${point.worker.id}")
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val iconVector: ImageVector = when (point.worker.category) {
                            ServiceCategory.PLUMBING -> Icons.Default.Build
                            ServiceCategory.ELECTRICAL -> Icons.Default.Bolt
                            ServiceCategory.AC_COOLING -> Icons.Default.FlashOn
                            else -> Icons.Default.Build
                        }
                        Icon(
                            imageVector = iconVector,
                            contentDescription = point.worker.category.displayName,
                            tint = Color(0xFF080C15),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
