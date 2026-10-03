package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// PALETTE SÉMANTIQUE « OBSIDIAN & CHAMPAGNE GOLD » (STANDARD INTERNATIONAL)
// =========================================================================

// --- Dark Mode (Défaut) ---
val FixoDarkBackground = Color(0xFF080C15)    // Noir d'encre bleuté
val FixoDarkSurface = Color(0xFF111827)       // Ardoise noble
val FixoDarkSurfaceCard = Color(0xFF1E293B)   // Gris acier profond
val FixoDarkBorder = Color(0x14FFFFFF)        // rgba(255, 255, 255, 0.08)
val FixoDarkTextPrimary = Color(0xFFFFFFFF)   // Blanc pur
val FixoDarkTextSecondary = Color(0xFFCBD5E1) // Gris clair ardoise contrasté (WCAG AAA)

// --- Light Mode (Miroir Parfait) ---
val FixoLightBackground = Color(0xFFF8FAFC)   // Blanc cassé
val FixoLightBg = FixoLightBackground
val FixoLightSurface = Color(0xFFFFFFFF)      // Blanc pur
val FixoLightSurfaceCard = Color(0xFFF1F5F9)  // Blanc grisé
val FixoLightBorder = Color(0x140F172A)       // rgba(15, 23, 42, 0.08)
val FixoLightTextPrimary = Color(0xFF0F172A)  // Noir d'encre
val FixoLightTextSecondary = Color(0xFF475569)// Ardoise foncée contrastée (WCAG AAA)

// --- Accents Globaux ---
val FixoGold500 = Color(0xFFF59E0B)           // Or ambre signature
val FixoGold600 = Color(0xFFD97706)           // Or ambre profond
val FixoGoldGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
)
val FixoStatusGreen = Color(0xFF10B981)       // Validations KYC, accusés de virement
val FixoStatusRed = Color(0xFFEF4444)         // Gel séquestre, litiges

// --- Aliases pour rétro-compatibilité stricte ---
val FixoBgCanvas = FixoDarkBackground
val FixoSurfaceCard = FixoDarkSurfaceCard
val FixoSurfaceElevated = Color(0xFF222E42)
val FixoBorderSubtle = FixoDarkBorder
val FixoTextPrimary = FixoDarkTextPrimary
val FixoTextSecondary = FixoDarkTextSecondary
val FixoTextMuted = FixoDarkTextSecondary
val FixoElectricAmber = FixoGold500
val FixoElectricAmberDark = FixoGold600
val FixoSuccessGreen = FixoStatusGreen
val FixoDangerRed = FixoStatusRed
val FixoEmerald500 = FixoStatusGreen
val FixoEmerald600 = Color(0xFF059669)
val FixoRed500 = FixoStatusRed
val FixoWhite = Color(0xFFFFFFFF)
val FixoNavy950 = Color(0xFF080C15)
val FixoNavy900 = Color(0xFF0F172A)
val FixoNavy800 = Color(0xFF1A2232)
val FixoNavy700 = Color(0xFF222E42)
val FixoNavy600 = Color(0xFF334A6E)
val FixoBlue600 = FixoNavy900
val FixoBlue700 = FixoNavy950
val FixoBlue50 = Color(0xFFF1F5F9)
val FixoSlate200 = Color(0xFFE2E8F0)
val FixoSlate400 = Color(0xFF94A3B8)
val FixoSlate500 = Color(0xFF64748B)
val FixoSlate700 = Color(0xFF334155)
val FixoSlate800 = Color(0xFF1E293B)
val FixoSlate100 = Color(0xFFF1F5F9)
val FixoSlate300 = Color(0xFFCBD5E1)
val FixoAmber100 = Color(0xFFFEF3C7)
val FixoAmber500 = FixoGold500
val FixoAmber600 = FixoGold600
val FixoGold100 = Color(0xFFFEF3C7)
val FixoGold400 = Color(0xFFFBBF24)
val FixoGold700 = Color(0xFFB45309)
val FixoEmerald50 = Color(0xFFECFDF5)
val FixoEmerald100 = Color(0xFFD1FAE5)
val FixoBlue100 = Color(0xFFDBEAFE)
val FixoRed50 = Color(0xFFFEF2F2)
val FixoNavy50 = Color(0xFFF8FAFC)
val FixoSurfaceGlass = Color(0x331E293B)

val FixoNeutral100 = Color(0xFFF1F5F9)
val FixoNeutral200 = Color(0xFFE2E8F0)
val FixoNeutral300 = Color(0xFFCBD5E1)
val FixoNeutral400 = Color(0xFF94A3B8)
val FixoNeutral500 = Color(0xFF64748B)
val FixoNeutral600 = Color(0xFF475569)
val FixoNeutral700 = Color(0xFF334155)
val FixoNeutral800 = Color(0xFF1E293B)
val FixoNeutral900 = Color(0xFF0F172A)
