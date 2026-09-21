package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =========================================================================
// Dark Color Scheme: Grounded in FIXO Deep Midnight Navy (#070E1B / #0C1628)
// with vibrant Gold (#F5A623) accents and crisp white typography
// =========================================================================
private val DarkColorScheme = darkColorScheme(
    primary = FixoGold500,
    onPrimary = FixoNavy950,
    primaryContainer = FixoNavy800,
    onPrimaryContainer = FixoGold500,

    secondary = FixoGold500,
    onSecondary = FixoNavy950,
    secondaryContainer = FixoNavy700,
    onSecondaryContainer = FixoGold200,

    tertiary = FixoEmerald500,
    onTertiary = FixoNavy950,

    background = FixoNavy950,
    onBackground = FixoWhite,

    surface = FixoNavy900,
    onSurface = FixoWhite,

    surfaceVariant = FixoNavy800,
    onSurfaceVariant = FixoNeutral300,

    outline = FixoNavy700,
    outlineVariant = FixoNavy800
)

// =========================================================================
// Light Color Scheme: Clean, professional off-white surfaces (#F9FAFB)
// with authoritative FIXO Navy (#0C1628) typography & primary buttons,
// and warm Golden Amber (#E29110 / #F5A623) accents
// =========================================================================
private val LightColorScheme = lightColorScheme(
    primary = FixoNavy900,
    onPrimary = FixoWhite,
    primaryContainer = FixoNavy50,
    onPrimaryContainer = FixoNavy900,

    secondary = FixoGold600,
    onSecondary = FixoWhite,
    secondaryContainer = FixoGold100,
    onSecondaryContainer = FixoGold700,

    tertiary = FixoEmerald600,
    onTertiary = FixoWhite,

    background = FixoNeutral50,
    onBackground = FixoNeutral900,

    surface = FixoWhite,
    onSurface = FixoNeutral900,

    surfaceVariant = FixoNeutral100,
    onSurfaceVariant = FixoNeutral600,

    outline = FixoNeutral300,
    outlineVariant = FixoNeutral200
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
