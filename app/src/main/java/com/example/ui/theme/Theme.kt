package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * MOTEUR DE THÈME DYNAMIQUE UNIFIÉ « OBSIDIAN & CHAMPAGNE GOLD » (LIGHT & DARK RÉACTIFS)
 *
 * Spécifications de contraste :
 * - background : Dark #080C15 | Light #F8FAFC
 * - surface : Dark #111827 | Light #FFFFFF
 * - surfaceVariant : Dark #1E293B | Light #F1F5F9
 * - textPrimary : Dark #FFFFFF | Light #0F172A
 * - textSecondary : Dark #CBD5E1 | Light #475569 (WCAG AAA garanti sur fond blanc)
 * - border : Dark rgba(255, 255, 255, 0.08) | Light rgba(15, 23, 42, 0.08)
 * - accentGold : #F59E0B -> #D97706
 */

private val DarkColorScheme = darkColorScheme(
    primary = FixoGold500,
    onPrimary = Color(0xFF080C15),
    primaryContainer = FixoDarkSurface,
    onPrimaryContainer = FixoGold500,

    secondary = FixoGold600,
    onSecondary = Color(0xFF080C15),
    secondaryContainer = FixoDarkSurfaceCard,
    onSecondaryContainer = FixoDarkTextPrimary,

    tertiary = FixoStatusGreen,
    onTertiary = Color(0xFF080C15),

    error = FixoStatusRed,
    onError = FixoDarkTextPrimary,
    errorContainer = Color(0xFF451A1A),
    onErrorContainer = Color(0xFFFECACA),

    background = FixoDarkBackground, // #080C15
    onBackground = FixoDarkTextPrimary, // #FFFFFF

    surface = FixoDarkSurface, // #111827
    onSurface = FixoDarkTextPrimary, // #FFFFFF

    surfaceVariant = FixoDarkSurfaceCard, // #1E293B
    onSurfaceVariant = FixoDarkTextSecondary, // #CBD5E1

    outline = FixoDarkBorder, // rgba(255, 255, 255, 0.08)
    outlineVariant = Color(0x24FFFFFF)
)

private val LightColorScheme = lightColorScheme(
    primary = FixoGold600,
    onPrimary = FixoWhite,
    primaryContainer = FixoLightSurfaceCard,
    onPrimaryContainer = FixoLightTextPrimary,

    secondary = FixoGold500,
    onSecondary = FixoWhite,
    secondaryContainer = FixoLightSurfaceCard,
    onSecondaryContainer = FixoLightTextPrimary,

    tertiary = FixoStatusGreen,
    onTertiary = FixoWhite,

    error = FixoStatusRed,
    onError = FixoWhite,
    errorContainer = Color(0xFFFEF2F2),
    onErrorContainer = Color(0xFF991B1B),

    background = FixoLightBackground, // #F8FAFC
    onBackground = FixoLightTextPrimary, // #0F172A

    surface = FixoLightSurface, // #FFFFFF
    onSurface = FixoLightTextPrimary, // #0F172A

    surfaceVariant = FixoLightSurfaceCard, // #F1F5F9
    onSurfaceVariant = Color(0xFF475569), // WCAG AAA garanti sur fond blanc

    outline = FixoLightBorder, // rgba(15, 23, 42, 0.08)
    outlineVariant = Color(0xFFE2E8F0)
)

/**
 * Accessoire sémantique direct pour tous les composants de l'application
 */
object FixoTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val background: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.background

    val surface: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surface

    val surfaceVariant: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceVariant

    val textPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSurface

    val textSecondary: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSurfaceVariant

    val border: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.outline

    val borderSubtle: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.outlineVariant

    val goldAccent: Color
        get() = FixoGold500

    val goldGradient: Brush
        get() = Brush.linearGradient(listOf(FixoGold500, FixoGold600))

    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() {
            val bg = MaterialTheme.colorScheme.background
            return (0.299 * bg.red + 0.587 * bg.green + 0.114 * bg.blue) < 0.5
        }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
