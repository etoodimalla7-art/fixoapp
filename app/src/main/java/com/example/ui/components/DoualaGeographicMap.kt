package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint as AndroidPaint
import android.graphics.Rect as AndroidRect
import android.graphics.Typeface as AndroidTypeface
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.math.abs

data class LatLng(
    val latitude: Double,
    val longitude: Double
)

data class MapArtisanPoint(
    val worker: WorkerProfile,
    val lat: Double,
    val lng: Double,
    val quarter: String
)

/**
 * OSMDROID MAP ENGINE — PLAN EXACT DE DOUALA (ZÉRO CLÉ API)
 *
 * Spécifications :
 * 1. Zéro clé API requise : OpenStreetMap officiel (TileSourceFactory.MAPNIK).
 * 2. Filtre Sombre Natif par Matrice de Couleur (Dark Mode) et tuiles claires nettes en Light Mode.
 * 3. Affichage immédiat des rues réelles de Douala (Boulevard de la Liberté, République, Rue Drouot, Joss, Wouri).
 * 4. Marqueurs interactifs d'artisans (Marc Dubois, Paul Essomba...) avec pastille dorée Or Ambre (#F59E0B)
 *    et icône de métier (🔧, ⚡, ❄️, 🛠️).
 * 5. Support de la Polyligne d'Itinéraire Réel (ex: de Deïdo vers Rue Drouot, Akwa) pour le Live Tracking.
 * 6. Sélecteur d'adresse « Pin Drop » interactif avec épingle centrale fixe.
 */
@Composable
fun DoualaGeographicMap(
    workers: List<WorkerProfile> = emptyList(),
    selectedWorker: WorkerProfile? = null,
    onSelectWorker: (WorkerProfile) -> Unit = {},
    modifier: Modifier = Modifier,
    centerLat: Double = 4.0511,
    centerLng: Double = 9.7085,
    initialZoom: Float = 16.5f,
    isPinDropMode: Boolean = false,
    onCameraPositionChanged: ((LatLng) -> Unit)? = null,
    onLocateMe: (() -> Unit)? = null,
    routePoints: List<LatLng>? = null,
    trackerLat: Double? = null,
    trackerLng: Double? = null,
    trackerName: String = "Marc Dubois",
    destinationPoint: LatLng? = null,
    destinationLabel: String = "Client (Rue Drouot, Akwa)"
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Initialize osmdroid configuration with application User-Agent
    remember {
        try {
            Configuration.getInstance().userAgentValue = context.packageName
            Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        } catch (_: Throwable) {}
        true
    }

    // Dynamic Day/Night Theme detection
    val isDark = MaterialTheme.colorScheme.surface.let {
        (0.299 * it.red + 0.587 * it.green + 0.114 * it.blue) < 0.5
    }

    // Real Douala patrol points (Akwa, Deïdo, Bonanjo, Bonapriso)
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

    var isMapInitialized by remember { mutableStateOf(false) }

    // MapView instance configured with official OpenStreetMap (TileSourceFactory.MAPNIK)
    val mapView = remember {
        try {
            MapView(context).apply {
                setBuiltInZoomControls(false)
                setMultiTouchControls(true)
                setTileSource(TileSourceFactory.MAPNIK)
                controller.setZoom(initialZoom.toDouble())
                controller.setCenter(GeoPoint(centerLat, centerLng))
                if (isDark) {
                    val inverseMatrix = ColorMatrix(floatArrayOf(
                        -0.85f, 0f, 0f, 0f, 240f,
                        0f, -0.85f, 0f, 0f, 240f,
                        0f, 0f, -0.85f, 0f, 240f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                    overlayManager.tilesOverlay.setColorFilter(ColorMatrixColorFilter(inverseMatrix))
                }
                isMapInitialized = true
            }
        } catch (_: Throwable) {
            null
        }
    }

    // Lifecycle observer for MapView
    DisposableEffect(lifecycleOwner, mapView) {
        val targetMapView = mapView
        if (targetMapView != null) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> targetMapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> targetMapView.onPause()
                    Lifecycle.Event.ON_DESTROY -> targetMapView.onDetach()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                try {
                    targetMapView.onDetach()
                } catch (_: Throwable) {}
            }
        } else {
            onDispose {}
        }
    }

    // Tile filter update when Day/Night theme changes
    LaunchedEffect(mapView, isDark) {
        mapView?.let { map ->
            map.setTileSource(TileSourceFactory.MAPNIK)
            if (isDark) {
                val inverseMatrix = ColorMatrix(floatArrayOf(
                    -0.85f, 0f, 0f, 0f, 240f,
                    0f, -0.85f, 0f, 0f, 240f,
                    0f, 0f, -0.85f, 0f, 240f,
                    0f, 0f, 0f, 1f, 0f
                ))
                map.overlayManager.tilesOverlay.setColorFilter(ColorMatrixColorFilter(inverseMatrix))
            } else {
                map.overlayManager.tilesOverlay.setColorFilter(null)
            }
            map.invalidate()
        }
    }

    // Camera animation when centerLat/centerLng change externally
    LaunchedEffect(mapView, centerLat, centerLng) {
        mapView?.let { map ->
            val current = map.mapCenter as? GeoPoint
            if (current != null) {
                val latDiff = abs(current.latitude - centerLat)
                val lngDiff = abs(current.longitude - centerLng)
                if (latDiff > 0.0003 || lngDiff > 0.0003) {
                    map.controller.animateTo(GeoPoint(centerLat, centerLng))
                }
            }
        }
    }

    // Dynamic Pin Drop Map Listener
    DisposableEffect(mapView, isPinDropMode, onCameraPositionChanged) {
        val map = mapView
        if (map != null && isPinDropMode && onCameraPositionChanged != null) {
            val listener = object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {
                    val center = map.mapCenter as? GeoPoint
                    if (center != null) {
                        onCameraPositionChanged(LatLng(center.latitude, center.longitude))
                    }
                    return false
                }

                override fun onZoom(event: ZoomEvent?): Boolean {
                    val center = map.mapCenter as? GeoPoint
                    if (center != null) {
                        onCameraPositionChanged(LatLng(center.latitude, center.longitude))
                    }
                    return false
                }
            }
            map.addMapListener(listener)
            onDispose {
                map.removeMapListener(listener)
            }
        } else {
            onDispose {}
        }
    }

    // Markers & Polylines Overlay
    LaunchedEffect(mapView, artisanPoints, selectedWorker, isPinDropMode, routePoints, trackerLat, trackerLng, destinationPoint) {
        mapView?.let { map ->
            // Clear existing dynamic markers and polylines
            map.overlays.removeAll { it is Marker || it is Polyline }

            // 1. LIVE ROUTE POLYLINES (Live Tracking Mode)
            if (!routePoints.isNullOrEmpty()) {
                val polyline = Polyline(map).apply {
                    outlinePaint.color = AndroidColor.parseColor("#F59E0B")
                    outlinePaint.strokeWidth = 14f
                    outlinePaint.strokeCap = AndroidPaint.Cap.ROUND
                    outlinePaint.isAntiAlias = true
                    setPoints(routePoints.map { GeoPoint(it.latitude, it.longitude) })
                }
                map.overlays.add(polyline)
            }

            // 2. DESTINATION MARKER
            if (destinationPoint != null) {
                val destMarker = Marker(map).apply {
                    position = GeoPoint(destinationPoint.latitude, destinationPoint.longitude)
                    title = destinationLabel
                    icon = BitmapDrawable(context.resources, createDestinationMarkerBitmap(context, destinationLabel))
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                map.overlays.add(destMarker)
            }

            // 3. TRACKER ARTISAN MARKER (Active vehicle/artisan)
            if (trackerLat != null && trackerLng != null) {
                val trackerMarker = Marker(map).apply {
                    position = GeoPoint(trackerLat, trackerLng)
                    title = trackerName
                    snippet = "En route • 26 km/h"
                    icon = BitmapDrawable(context.resources, createTrackerMarkerBitmap(context, trackerName))
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                }
                map.overlays.add(trackerMarker)
            }

            // 4. DISCOVERY ARTISAN PATROL MARKERS (If not in tracking or pin-drop mode)
            if (!isPinDropMode && routePoints.isNullOrEmpty() && trackerLat == null) {
                artisanPoints.forEach { point ->
                    val isSelected = selectedWorker?.id == point.worker.id
                    val marker = Marker(map).apply {
                        position = GeoPoint(point.lat, point.lng)
                        title = point.worker.name
                        subDescription = "${point.worker.category.displayName} • 15 000 FCFA"
                        icon = BitmapDrawable(
                            context.resources,
                            createArtisanMarkerBitmap(context, point.worker, isSelected)
                        )
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        setOnMarkerClickListener { _, _ ->
                            onSelectWorker(point.worker)
                            true
                        }
                    }
                    map.overlays.add(marker)
                }
            }
            map.invalidate()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("douala_geographic_map_container")
    ) {
        if (mapView != null) {
            // NATIVE OSMDROID MAP VIEW (MAPNIK TILES + DYNAMIC COLOR MATRIX)
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // JVM / ROBOLECTRIC COMPATIBLE FALLBACK
            DoualaRobolectricPlanPlaceholder(
                centerLat = centerLat,
                centerLng = centerLng,
                isDark = isDark,
                workers = if (!isPinDropMode && routePoints.isNullOrEmpty()) workers else emptyList(),
                selectedWorker = selectedWorker,
                onSelectWorker = onSelectWorker,
                routePoints = routePoints,
                trackerName = trackerName,
                destinationLabel = destinationLabel
            )
        }

        // FIXED CENTER PIN FOR PIN DROP MODE (Zero Recomposition on Parent)
        if (isPinDropMode) {
            PinDropFixedTargetOverlay(modifier = Modifier.align(Alignment.Center))
        }

        // FLOATING CONTROLS: ZOOM IN / OUT & RECENTER
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            // Zoom In
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 4.dp
            ) {
                IconButton(
                    onClick = { mapView?.controller?.zoomIn() },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Zoom Out
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 4.dp
            ) {
                IconButton(
                    onClick = { mapView?.controller?.zoomOut() },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Locate Me / Recenter
            Surface(
                shape = CircleShape,
                color = FixoGold500,
                shadowElevation = 6.dp
            ) {
                IconButton(
                    onClick = {
                        mapView?.controller?.animateTo(GeoPoint(4.0511, 9.7085))
                        mapView?.controller?.setZoom(16.5)
                        onLocateMe?.invoke()
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("btn_recenter_douala_map")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Recentrer Akwa",
                        tint = Color(0xFF080C15)
                    )
                }
            }
        }
    }
}

/**
 * Isolated Pin Drop Overlay with pulsing target ground ring.
 */
@Composable
private fun PinDropFixedTargetOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pinPulse")
    val pinTargetPulse by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "targetPulse"
    )

    Box(
        modifier = modifier.offset(y = (-20).dp),
        contentAlignment = Alignment.Center
    ) {
        // Ground target shadow & pulse circle
        Box(
            modifier = Modifier
                .offset(y = 20.dp)
                .size(pinTargetPulse.dp)
                .clip(CircleShape)
                .background(FixoGold500.copy(alpha = 0.35f))
        )
        Box(
            modifier = Modifier
                .offset(y = 20.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(FixoGold500)
        )

        // Floating Pin Icon (📍)
        Surface(
            shape = CircleShape,
            color = FixoGold500,
            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.White),
            shadowElevation = 8.dp,
            modifier = Modifier.size(46.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Point d'intervention sélectionné",
                    tint = Color(0xFF080C15),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

/**
 * Creates high-contrast Marker Bitmap for artisans.
 */
private fun createArtisanMarkerBitmap(
    context: Context,
    worker: WorkerProfile,
    isSelected: Boolean
): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (46 * density).toInt().coerceAtLeast(56)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)

    // Drop shadow
    val shadowPaint = AndroidPaint().apply {
        isAntiAlias = true
        color = AndroidColor.argb(90, 0, 0, 0)
    }
    canvas.drawCircle(sizePx / 2f, sizePx / 2f + 2f * density, sizePx * 0.42f, shadowPaint)

    // Main Amber Gold Circle (#F59E0B or selected #D97706)
    val circlePaint = AndroidPaint().apply {
        isAntiAlias = true
        color = if (isSelected) AndroidColor.parseColor("#D97706") else AndroidColor.parseColor("#F59E0B")
    }
    val circleRadius = sizePx * 0.38f
    val circleCenterX = sizePx / 2f
    val circleCenterY = sizePx / 2f
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, circlePaint)

    // Crisp white border
    val ringPaint = AndroidPaint().apply {
        isAntiAlias = true
        style = AndroidPaint.Style.STROKE
        strokeWidth = (if (isSelected) 3.5f else 2.5f) * density
        color = AndroidColor.WHITE
    }
    canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, ringPaint)

    // Profession Symbol
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
 * Creates vehicle / tracker marker for live artisan tracking (e.g. Marc Dubois).
 */
private fun createTrackerMarkerBitmap(
    context: Context,
    workerName: String
): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (50 * density).toInt().coerceAtLeast(60)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)

    // Shadow
    val shadowPaint = AndroidPaint().apply {
        isAntiAlias = true
        color = AndroidColor.argb(100, 0, 0, 0)
    }
    canvas.drawCircle(sizePx / 2f, sizePx / 2f + 2f * density, sizePx * 0.42f, shadowPaint)

    // Black & Gold Vehicle Badge
    val bgPaint = AndroidPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#080C15")
    }
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx * 0.38f, bgPaint)

    val borderPaint = AndroidPaint().apply {
        isAntiAlias = true
        style = AndroidPaint.Style.STROKE
        strokeWidth = 3f * density
        color = AndroidColor.parseColor("#F59E0B")
    }
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx * 0.38f, borderPaint)

    // Emoji / Icon
    val textPaint = AndroidPaint().apply {
        isAntiAlias = true
        textSize = 16f * density
        textAlign = AndroidPaint.Align.CENTER
    }
    val bounds = AndroidRect()
    val symbol = "🏍️"
    textPaint.getTextBounds(symbol, 0, symbol.length, bounds)
    canvas.drawText(symbol, sizePx / 2f, sizePx / 2f + (bounds.height() / 2f), textPaint)

    return bitmap
}

/**
 * Creates destination pin marker bitmap for client delivery address.
 */
private fun createDestinationMarkerBitmap(
    context: Context,
    label: String
): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (44 * density).toInt().coerceAtLeast(50)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)

    // Amber Circle
    val circlePaint = AndroidPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#EF4444")
    }
    canvas.drawCircle(sizePx / 2f, sizePx * 0.4f, sizePx * 0.34f, circlePaint)

    val borderPaint = AndroidPaint().apply {
        isAntiAlias = true
        style = AndroidPaint.Style.STROKE
        strokeWidth = 2.5f * density
        color = AndroidColor.WHITE
    }
    canvas.drawCircle(sizePx / 2f, sizePx * 0.4f, sizePx * 0.34f, borderPaint)

    val textPaint = AndroidPaint().apply {
        isAntiAlias = true
        textSize = 14f * density
        textAlign = AndroidPaint.Align.CENTER
    }
    val bounds = AndroidRect()
    val symbol = "🏠"
    textPaint.getTextBounds(symbol, 0, symbol.length, bounds)
    canvas.drawText(symbol, sizePx / 2f, sizePx * 0.4f + (bounds.height() / 2f), textPaint)

    return bitmap
}

/**
 * Clean Douala placeholder for Robolectric unit tests and offline JVM preview.
 */
@Composable
private fun DoualaRobolectricPlanPlaceholder(
    centerLat: Double,
    centerLng: Double,
    isDark: Boolean,
    workers: List<WorkerProfile>,
    selectedWorker: WorkerProfile?,
    onSelectWorker: (WorkerProfile) -> Unit,
    routePoints: List<LatLng>? = null,
    trackerName: String = "Marc Dubois",
    destinationLabel: String = "Client (Rue Drouot, Akwa)"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF080C15) else Color(0xFFF8FAFC)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Icon(
                imageVector = if (!routePoints.isNullOrEmpty()) Icons.Default.DirectionsCar else Icons.Default.LocationOn,
                contentDescription = null,
                tint = FixoGold500,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (!routePoints.isNullOrEmpty()) "Suivi d'Intervention en Direct" else "OpenStreetMap • Douala, Cameroun",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            )
            Text(
                text = if (!routePoints.isNullOrEmpty())
                    "$trackerName en route vers $destinationLabel"
                else
                    "Akwa (Bd de la Liberté) : Lat ${centerLat.format(4)}°, Lng ${centerLng.format(4)}°",
                fontSize = 12.sp,
                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
            )
            Text(
                text = "Axes: Deïdo (Rond-Point) • Bd de la République • Bd de la Liberté • Rue Drouot",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = FixoGold500,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (workers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    workers.take(3).forEach { worker ->
                        val isSelected = selectedWorker?.id == worker.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) FixoGold500 else if (isDark) Color(0xFF1E293B) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, FixoGold500),
                            modifier = Modifier.clickable { onSelectWorker(worker) }
                        ) {
                            Text(
                                text = "${worker.name} (${worker.category.displayName})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF080C15) else if (isDark) Color.White else Color(0xFF0F172A),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun Double.format(digits: Int): String = String.format("%.${digits}f", this)
