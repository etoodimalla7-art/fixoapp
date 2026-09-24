package com.example.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.data.repository.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Chantier Ambient Light Sensor Manager
 * Permet l'adaptation automatique Double Thème Tout-Terrain :
 * - Plein soleil sur un chantier (lux >= 2500 lx) -> Thème Clair haute visibilité (#F8FAFC, #FFFFFF, #E2E8F0, #0F172A)
 * - Pièce sombre / délestage (lux < 2500 lx) -> Mode Sombre Haute Visibilité WCAG AAA (#080C15, #1A2232, #222E42, #FFFFFF, #E2E8F0, #FFB800)
 */
object AmbientLightManager {
    const val SUNLIGHT_LUX_THRESHOLD = 2500f // Seuil bascule plein soleil / chantier
    const val DARK_ROOM_LUX_THRESHOLD = 50f // Seuil pénombre / délestage

    private val _currentLux = MutableStateFlow(300f) // Valeur nominale d'intérieur
    val currentLux: StateFlow<Float> = _currentLux.asStateFlow()

    fun updateLux(lux: Float) {
        _currentLux.value = lux
    }

    /**
     * Calcule si l'application doit basculer en mode sombre selon le mode sélectionné
     * et le niveau de luminosité ambiante mesuré par le capteur matériel.
     */
    fun resolveIsDark(
        mode: ThemeMode,
        systemIsDark: Boolean,
        lux: Float
    ): Boolean {
        return when (mode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.AUTO_LUX -> {
                // Si plein soleil sur chantier (lux élevé), forcer le thème clair haute visibilité
                // En pièce sombre ou délestage (lux bas), forcer le mode sombre WCAG AAA
                lux < SUNLIGHT_LUX_THRESHOLD
            }
            ThemeMode.SYSTEM -> systemIsDark
        }
    }
}

/**
 * Composable remember hook observant le capteur de luminosité ambiante de l'appareil (Sensor.TYPE_LIGHT).
 */
@Composable
fun rememberAmbientLightSensorLux(): State<Float> {
    val context = LocalContext.current
    val luxState = remember { mutableFloatStateOf(AmbientLightManager.currentLux.value) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.values.isNotEmpty()) {
                    val lux = event.values[0]
                    luxState.floatValue = lux
                    AmbientLightManager.updateLux(lux)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensorManager != null && lightSensor != null) {
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    return luxState
}
