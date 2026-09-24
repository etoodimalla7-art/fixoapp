package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// CHANTIER 1 : MASTER PROMPT DESIGN TOKENS (WCAG AAA & HAUTE VISIBILITÉ)
// L'interface doit rester lisible en plein soleil sur chantier ou dans une
// pièce sombre en cas de délestage.
// =========================================================================

// --- Mode Sombre Haute Visibilité (WCAG AAA) ---
// Fond d'écran : Noir absolu #000000 ou noir d'encre #080C15
val FixoBgCanvas = Color(0xFF080C15) // Noir d'encre ultra-profond
val FixoBlackAbsolute = Color(0xFF000000) // Noir absolu anti-délestage

// Surfaces de cartes : Élévation par gris anthracite #1A2232 et #222E42
val FixoSurfaceCard = Color(0xFF1A2232) // Élévation 1 : gris anthracite pour cartes
val FixoSurfaceElevated = Color(0xFF222E42) // Élévation 2 : conteneurs actifs & sous-plans
val FixoSurfaceGlass = Color(0xEB1A2232) // Surface semi-transparente haute opacité

// Bords et contours : Liseré de délimitation renforcé en rgba(255, 255, 255, 0.16) de 1.5 px
val FixoBorderSubtle = Color(0x29FFFFFF) // rgba(255, 255, 255, 0.16)
val FixoBorderHighContrast = Color(0x29FFFFFF)

// Typographie : Blanc pur #FFFFFF pour 100% des titres et libellés majeurs ;
// blanc bleuté #E2E8F0 pour le texte secondaire (suppression intégrale des gris sombres illisibles)
val FixoTextPrimary = Color(0xFFFFFFFF) // Blanc pur
val FixoTextSecondary = Color(0xFFE2E8F0) // Blanc bleuté haute visibilité
val FixoTextMuted = Color(0xFFCBD5E1) // Blanc bleuté doux (jamais de gris terne)

// Or Ambre Électrique : Ambre saturé (#FFB800 vers #FF9100) pour boutons primaires,
// garantissant un contraste supérieur à 7:1 face au fond sombre
val FixoElectricAmber = Color(0xFFFFB800)
val FixoElectricAmberDark = Color(0xFFFF9100)
val FixoGoldGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFB800), Color(0xFFFF9100))
)

// --- Architecture Double Thème : Thème Clair Adaptatif (Plein Soleil Chantier) ---
// Fond blanc cassé #F8FAFC, conteneurs #FFFFFF, bordures #E2E8F0, texte noir d'encre #0F172A
val FixoLightBg = Color(0xFFF8FAFC)
val FixoLightSurface = Color(0xFFFFFFFF)
val FixoLightBorder = Color(0xFFE2E8F0)
val FixoLightTextPrimary = Color(0xFF0F172A)
val FixoLightTextSecondary = Color(0xFF334155)

// =========================================================================
// FIXO Brand Palette Derived Tokens
// =========================================================================

// Navy Brand Foundation
val FixoNavy950 = Color(0xFF080C15)
val FixoNavy900 = Color(0xFF0F172A)
val FixoNavy850 = Color(0xFF141E33)
val FixoNavy800 = Color(0xFF1A2232)
val FixoNavy700 = Color(0xFF222E42)
val FixoNavy600 = Color(0xFF334A6E)
val FixoNavy500 = Color(0xFF47608A)
val FixoNavy200 = Color(0xFFCBD5E1)
val FixoNavy100 = Color(0xFFE2E8F0)
val FixoNavy50  = Color(0xFFF1F5F9)

// Gold / Electric Amber
val FixoGold400 = Color(0xFFFFC72C)
val FixoGold500 = Color(0xFFFFB800) // Ambre saturé électrique
val FixoGold600 = Color(0xFFFF9100) // Ambre profond saturé
val FixoGold700 = Color(0xFFD97706)
val FixoGold200 = Color(0xFFFDE68A)
val FixoGold100 = Color(0xFFFEF3C7)
val FixoGold50  = Color(0xFFFFFBEB)

// Neutrals
val FixoNeutral900 = Color(0xFF0F172A)
val FixoNeutral800 = Color(0xFF1A2232)
val FixoNeutral700 = Color(0xFF222E42)
val FixoNeutral600 = Color(0xFF475569)
val FixoNeutral500 = Color(0xFF64748B)
val FixoNeutral400 = Color(0xFF94A3B8)
val FixoNeutral300 = Color(0xFFCBD5E1)
val FixoNeutral200 = Color(0xFFE2E8F0)
val FixoNeutral100 = Color(0xFFF1F5F9)
val FixoNeutral50  = Color(0xFFF8FAFC)
val FixoWhite      = Color(0xFFFFFFFF)

// Semantic Accents (Trust, Verification & Status)
val FixoEmerald500 = Color(0xFF10B981) // Statut En Ligne, CNI validée, encaissements
val FixoEmerald600 = Color(0xFF059669)
val FixoEmerald100 = Color(0xFFD1FAE5)
val FixoEmerald50  = Color(0xFFECFDF5)

val FixoRed500     = Color(0xFFEF4444) // Litige, annulation, urgence
val FixoRed50      = Color(0xFFFEF2F2)

val FixoSuccessGreen = FixoEmerald500
val FixoDangerRed = FixoRed500

// Backward-Compatibility Aliases
val FixoBlue600 = FixoNavy900
val FixoBlue700 = FixoNavy950
val FixoBlue50  = FixoNavy50
val FixoBlue100 = FixoNavy100

val FixoAmber500 = FixoGold500
val FixoAmber600 = FixoGold600
val FixoAmber100 = FixoGold100
val FixoAmber50  = FixoGold50

val FixoSlate900 = FixoNeutral900
val FixoSlate800 = FixoNeutral800
val FixoSlate700 = FixoNeutral700
val FixoSlate600 = FixoNeutral600
val FixoSlate500 = FixoNeutral500
val FixoSlate400 = FixoNeutral400
val FixoSlate300 = FixoNeutral300
val FixoSlate200 = FixoNeutral200
val FixoSlate100 = FixoNeutral100
val FixoSlate50  = FixoNeutral50
