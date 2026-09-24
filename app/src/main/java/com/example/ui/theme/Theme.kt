package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =========================================================================
// CHANTIER 1 : DOUBLE THÈME ADAPTATIF TOUT-TERRAIN
// Mode Sombre Haute Visibilité (WCAG AAA) & Mode Clair Plein Soleil
// =========================================================================

/**
 * Mode Sombre Haute Visibilité (WCAG AAA) :
 * - Fond d'écran : Noir d'encre #080C15 (ou #000000)
 * - Surfaces de cartes : Élévation par gris anthracite #1A2232 et #222E42
 * - Bords et contours : Liseré de délimitation en rgba(255, 255, 255, 0.16) de 1.5 px
 * - Typographie : Blanc pur #FFFFFF pour 100% des titres, blanc bleuté #E2E8F0 pour le secondaire
 * - Or Ambre Électrique : #FFB800 vers #FF9100 pour contraste > 7:1
 */
private val DarkColorScheme = darkColorScheme(
    primary = FixoGold500, // #FFB800
    onPrimary = Color(0xFF080C15), // Contraste > 7:1
    primaryContainer = FixoSurfaceElevated, // #222E42
    onPrimaryContainer = FixoGold500,

    secondary = FixoGold600, // #FF9100
    onSecondary = Color(0xFF080C15),
    secondaryContainer = FixoSurfaceCard, // #1A2232
    onSecondaryContainer = FixoGold500,

    tertiary = FixoSuccessGreen,
    onTertiary = Color(0xFF080C15),

    error = FixoDangerRed,
    onError = FixoWhite,

    background = FixoBgCanvas, // #080C15
    onBackground = FixoTextPrimary, // #FFFFFF

    surface = FixoSurfaceCard, // #1A2232
    onSurface = FixoTextPrimary, // #FFFFFF

    surfaceVariant = FixoSurfaceElevated, // #222E42
    onSurfaceVariant = FixoTextSecondary, // #E2E8F0

    outline = FixoBorderSubtle, // rgba(255, 255, 255, 0.16)
    outlineVariant = FixoBorderSubtle
)

/**
 * Thème Clair Plein Soleil (Chantier Adaptatif) :
 * - Fond blanc cassé #F8FAFC
 * - Conteneurs #FFFFFF
 * - Bordures #E2E8F0
 * - Texte noir d'encre #0F172A
 */
private val LightColorScheme = lightColorScheme(
    primary = FixoLightTextPrimary, // #0F172A
    onPrimary = FixoWhite,
    primaryContainer = FixoLightSurface, // #FFFFFF
    onPrimaryContainer = FixoLightTextPrimary,

    secondary = FixoGold600, // #FF9100
    onSecondary = FixoWhite,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = FixoLightTextPrimary,

    tertiary = FixoEmerald600,
    onTertiary = FixoWhite,

    background = FixoLightBg, // #F8FAFC
    onBackground = FixoLightTextPrimary, // #0F172A

    surface = FixoLightSurface, // #FFFFFF
    onSurface = FixoLightTextPrimary, // #0F172A

    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = FixoLightTextSecondary, // #334155

    outline = FixoLightBorder, // #E2E8F0
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
