package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * PALETTE SÉMANTIQUE IMMUABLE « OBSIDIAN & CHAMPAGNE GOLD »
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

    background = FixoDarkBackground, // #080C15
    onBackground = FixoDarkTextPrimary, // #FFFFFF

    surface = FixoDarkSurface, // #111827
    onSurface = FixoDarkTextPrimary, // #FFFFFF

    surfaceVariant = FixoDarkSurfaceCard, // #1E293B
    onSurfaceVariant = FixoDarkTextSecondary, // #CBD5E1

    outline = FixoDarkBorder, // rgba(255, 255, 255, 0.08)
    outlineVariant = FixoDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = FixoGold600,
    onPrimary = FixoWhite,
    primaryContainer = FixoLightSurfaceCard,
    onPrimaryContainer = FixoLightTextPrimary,

    secondary = FixoLightTextPrimary,
    onSecondary = FixoWhite,
    secondaryContainer = FixoLightSurfaceCard,
    onSecondaryContainer = FixoLightTextPrimary,

    tertiary = FixoStatusGreen,
    onTertiary = FixoWhite,

    error = FixoStatusRed,
    onError = FixoWhite,

    background = FixoLightBackground, // #F8FAFC
    onBackground = FixoLightTextPrimary, // #0F172A

    surface = FixoLightSurface, // #FFFFFF
    onSurface = FixoLightTextPrimary, // #0F172A

    surfaceVariant = FixoLightSurfaceCard, // #F1F5F9
    onSurfaceVariant = FixoLightTextSecondary, // #64748B

    outline = FixoLightBorder, // rgba(15, 23, 42, 0.08)
    outlineVariant = FixoLightBorder
)

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
