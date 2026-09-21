package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FixoBlue600,
    onPrimary = Color.White,
    primaryContainer = FixoNavy700,
    onPrimaryContainer = FixoBlue50,
    secondary = FixoAmber500,
    onSecondary = Color.Black,
    secondaryContainer = FixoAmber600.copy(alpha = 0.2f),
    onSecondaryContainer = FixoAmber100,
    tertiary = FixoEmerald500,
    onTertiary = Color.White,
    background = FixoNavy900,
    onBackground = FixoSlate50,
    surface = FixoNavy800,
    onSurface = FixoSlate50,
    surfaceVariant = FixoNavy700,
    onSurfaceVariant = FixoSlate300,
    outline = FixoSlate700,
    outlineVariant = FixoSlate800
)

private val LightColorScheme = lightColorScheme(
    primary = FixoBlue600,
    onPrimary = Color.White,
    primaryContainer = FixoBlue50,
    onPrimaryContainer = FixoBlue700,
    secondary = FixoAmber600,
    onSecondary = Color.White,
    secondaryContainer = FixoAmber50,
    onSecondaryContainer = FixoAmber600,
    tertiary = FixoEmerald600,
    onTertiary = Color.White,
    background = FixoSlate50,
    onBackground = FixoSlate900,
    surface = Color.White,
    onSurface = FixoSlate900,
    surfaceVariant = FixoSlate100,
    onSurfaceVariant = FixoSlate600,
    outline = FixoSlate200,
    outlineVariant = FixoSlate300
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

