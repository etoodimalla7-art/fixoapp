package com.example

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.data.repository.ThemeMode
import com.example.ui.theme.AmbientLightManager
import com.example.ui.theme.FixoBgCanvas
import com.example.ui.theme.FixoBorderSubtle
import com.example.ui.theme.FixoElectricAmber
import com.example.ui.theme.FixoElectricAmberDark
import com.example.ui.theme.FixoLightBg
import com.example.ui.theme.FixoLightBorder
import com.example.ui.theme.FixoLightSurface
import com.example.ui.theme.FixoLightTextPrimary
import com.example.ui.theme.FixoSurfaceCard
import com.example.ui.theme.FixoSurfaceElevated
import com.example.ui.theme.FixoTextPrimary
import com.example.ui.theme.FixoTextSecondary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ChantierDesignSystemTest {

    @Test
    fun testAmbientLightManagerAutomaticSwitch() {
        // High glare sunlight on construction site (lux >= 2500) -> Light Mode (isDark = false)
        val sunlightLux = 12000f
        val isDarkSunlight = AmbientLightManager.resolveIsDark(
            mode = ThemeMode.AUTO_LUX,
            systemIsDark = true,
            lux = sunlightLux
        )
        assertFalse("Under bright chantier sunlight, auto mode must activate Light Mode", isDarkSunlight)

        // Dark room or load shedding / délestage (lux < 2500) -> High Visibility Dark Mode (isDark = true)
        val darkRoomLux = 15f
        val isDarkRoom = AmbientLightManager.resolveIsDark(
            mode = ThemeMode.AUTO_LUX,
            systemIsDark = false,
            lux = darkRoomLux
        )
        assertTrue("In dark room or délestage, auto mode must activate WCAG AAA Dark Mode", isDarkRoom)

        // Explicit user preference overrides
        assertTrue(AmbientLightManager.resolveIsDark(ThemeMode.DARK, false, sunlightLux))
        assertFalse(AmbientLightManager.resolveIsDark(ThemeMode.LIGHT, true, darkRoomLux))
    }

    @Test
    fun testWcagAaaElectricAmberContrastOverDarkObsidianBackground() {
        // Calculate relative luminance for WCAG contrast
        fun getLuminance(color: Color): Double {
            fun channelLum(c: Float): Double {
                return if (c <= 0.03928f) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
            }
            return 0.2126 * channelLum(color.red) +
                   0.7152 * channelLum(color.green) +
                   0.0722 * channelLum(color.blue)
        }

        fun getContrastRatio(foreground: Color, background: Color): Double {
            val l1 = getLuminance(foreground)
            val l2 = getLuminance(background)
            val lighter = max(l1, l2)
            val darker = min(l1, l2)
            return (lighter + 0.05) / (darker + 0.05)
        }

        // Electric Amber (#FFB800) against Noir d'encre (#080C15)
        val amberContrastAgainstDark = getContrastRatio(FixoElectricAmber, FixoBgCanvas)
        assertTrue(
            "Electric Amber against dark obsidian must exceed 7:1 WCAG AAA standard (actual: $amberContrastAgainstDark)",
            amberContrastAgainstDark >= 7.0
        )

        // Pure white (#FFFFFF) against Dark canvas
        val whiteContrast = getContrastRatio(FixoTextPrimary, FixoBgCanvas)
        assertTrue(
            "Pure white against dark canvas must exceed 15:1 (actual: $whiteContrast)",
            whiteContrast >= 15.0
        )
    }

    @Test
    fun testChantierColorTokensAndSurfaces() {
        // High visibility dark mode tokens
        assertEquals(Color(0xFF080C15), FixoBgCanvas)
        assertEquals(Color(0xFF1A2232), FixoSurfaceCard)
        assertEquals(Color(0xFF222E42), FixoSurfaceElevated)
        assertEquals(Color(0xFFFFFFFF), FixoTextPrimary)
        assertEquals(Color(0xFFE2E8F0), FixoTextSecondary)
        assertEquals(Color(0xFFFFB800), FixoElectricAmber)
        assertEquals(Color(0xFFFF9100), FixoElectricAmberDark)

        // Adaptive light mode tokens (plein soleil)
        assertEquals(Color(0xFFF8FAFC), FixoLightBg)
        assertEquals(Color(0xFFFFFFFF), FixoLightSurface)
        assertEquals(Color(0xFFE2E8F0), FixoLightBorder)
        assertEquals(Color(0xFF0F172A), FixoLightTextPrimary)
    }
}
