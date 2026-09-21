package com.example.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.model.JobStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class LocationTrackingState {
    IDLE,
    ACQUIRING_FIX,
    STREAMING_LIVE,
    ARRIVED_JOB_STOPPED,
    PERMISSION_DENIED,
    GPS_HARDWARE_DISABLED,
    NETWORK_OFFLINE
}

data class LiveGeoPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float = 0f,
    val speedKmh: Float = 0f,
    val bearingDegrees: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

interface FixoLocationTracker {
    fun checkPermissions(): Boolean
    fun startJobScopedTracking(
        bookingId: String,
        currentJobStatus: JobStatus,
        onLocationUpdate: (LiveGeoPoint) -> Unit,
        onStateChange: (LocationTrackingState) -> Unit
    )
    fun stopJobScopedTracking(bookingId: String)
    fun getLastKnownLocation(onResult: (LiveGeoPoint?) -> Unit)
}

/**
 * Production Android Location Provider backed by Google Play Services FusedLocationProviderClient.
 * Enforces strict job-level privacy: location is ONLY transmitted when job status is ON_THE_WAY.
 */
class AndroidFixoLocationTracker(
    private val context: Context
) : FixoLocationTracker {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private var activeCallback: LocationCallback? = null
    private var activeBookingId: String? = null

    override fun checkPermissions(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    override fun startJobScopedTracking(
        bookingId: String,
        currentJobStatus: JobStatus,
        onLocationUpdate: (LiveGeoPoint) -> Unit,
        onStateChange: (LocationTrackingState) -> Unit
    ) {
        // Enforce job-scoped lifecycle security
        if (currentJobStatus != JobStatus.ON_THE_WAY) {
            onStateChange(LocationTrackingState.ARRIVED_JOB_STOPPED)
            stopJobScopedTracking(bookingId)
            return
        }

        if (!checkPermissions()) {
            onStateChange(LocationTrackingState.PERMISSION_DENIED)
            return
        }

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isGpsEnabled = lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                lm?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
        if (!isGpsEnabled) {
            onStateChange(LocationTrackingState.GPS_HARDWARE_DISABLED)
            return
        }

        // Cancel existing if different
        if (activeBookingId != null && activeBookingId != bookingId) {
            stopJobScopedTracking(activeBookingId!!)
        }
        activeBookingId = bookingId

        onStateChange(LocationTrackingState.ACQUIRING_FIX)

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000L)
            .setMinUpdateIntervalMillis(2000L)
            .setMinUpdateDistanceMeters(5f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val speedKmh = if (loc.hasSpeed()) loc.speed * 3.6f else 0f
                val bearing = if (loc.hasBearing()) loc.bearing else 0f
                val point = LiveGeoPoint(
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    accuracyMeters = if (loc.hasAccuracy()) loc.accuracy else 10f,
                    speedKmh = speedKmh,
                    bearingDegrees = bearing,
                    timestamp = loc.time
                )
                onStateChange(LocationTrackingState.STREAMING_LIVE)
                onLocationUpdate(point)
            }
        }

        activeCallback = callback
        try {
            fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            onStateChange(LocationTrackingState.PERMISSION_DENIED)
        }
    }

    override fun stopJobScopedTracking(bookingId: String) {
        if (activeBookingId == bookingId || bookingId.isEmpty()) {
            activeCallback?.let { fusedClient.removeLocationUpdates(it) }
            activeCallback = null
            activeBookingId = null
        }
    }

    override fun getLastKnownLocation(onResult: (LiveGeoPoint?) -> Unit) {
        if (!checkPermissions()) {
            onResult(null)
            return
        }
        try {
            fusedClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    onResult(
                        LiveGeoPoint(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            accuracyMeters = loc.accuracy,
                            speedKmh = if (loc.hasSpeed()) loc.speed * 3.6f else 0f,
                            bearingDegrees = if (loc.hasBearing()) loc.bearing else 0f,
                            timestamp = loc.time
                        )
                    )
                } else {
                    onResult(null)
                }
            }.addOnFailureListener {
                onResult(null)
            }
        } catch (e: SecurityException) {
            onResult(null)
        }
    }
}

/**
 * Geodesic math utilities for real distance (Haversine) and ETA calculations.
 */
object GeoCalculations {
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun calculateEtaMinutes(distanceKm: Double, currentSpeedKmh: Float): Int {
        val effectiveSpeed = when {
            currentSpeedKmh > 10f -> currentSpeedKmh
            distanceKm < 1.0 -> 15f // Walking/traffic urban speed in Cameroon
            distanceKm < 5.0 -> 25f // Motorcycle/city taxi speed
            else -> 35f // Arterial transit speed
        }
        val hours = distanceKm / effectiveSpeed
        val minutes = (hours * 60).toInt() + 2 // +2 min arrival buffer
        return minutes.coerceAtLeast(1)
    }

    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val dLon = Math.toRadians(lon2 - lon1)
        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(dLon)
        val bearing = Math.toDegrees(atan2(y, x))
        return ((bearing + 360) % 360).toFloat()
    }
}
