package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// FIXO Official Logo-Derived Design System Palette
// Derived precisely from the official FIXO brand asset:
// - Deep Midnight Navy: Authoritative background & F-stem
// - Golden Amber Accent: Radiant wing & dot
// - Crisp White & Restrained Neutrals
// =========================================================================

// --- FIXO Navy (Primary Brand Foundation) ---
val FixoNavy950 = Color(0xFF070E1B) // Ultra midnight base for dark mode canvas
val FixoNavy900 = Color(0xFF0C1628) // Authoritative FIXO Navy (logo background & primary dark)
val FixoNavy850 = Color(0xFF101D36)
val FixoNavy800 = Color(0xFF152544) // Dark mode card / elevated surface
val FixoNavy700 = Color(0xFF1E3258) // Dark mode container & subtle border
val FixoNavy600 = Color(0xFF2B4474) // Medium navy accent
val FixoNavy500 = Color(0xFF3C5E9E) // Muted navy
val FixoNavy200 = Color(0xFFBDD0EB)
val FixoNavy100 = Color(0xFFDCE6F5) // Soft navy badge container
val FixoNavy50  = Color(0xFFF0F4FA) // Light navy tint for selected containers

// --- FIXO Gold (Authoritative Brand Accent) ---
val FixoGold500 = Color(0xFFF5A623) // Radiant golden-amber accent from logo wing & dot
val FixoGold600 = Color(0xFFE29110) // Pressed/darker gold for high-contrast on light
val FixoGold700 = Color(0xFFC07604)
val FixoGold200 = Color(0xFFFDE68A)
val FixoGold100 = Color(0xFFFEF3D6) // Soft gold container / pending badge background
val FixoGold50  = Color(0xFFFFF9ED) // Warm light tint

// --- Restrained Neutral Hierarchy (No Purple, No Pink, No Random Blue Gradients) ---
val FixoNeutral900 = Color(0xFF111827) // High contrast text on light
val FixoNeutral800 = Color(0xFF1F2937)
val FixoNeutral700 = Color(0xFF374151)
val FixoNeutral600 = Color(0xFF4B5563) // Secondary text on light
val FixoNeutral500 = Color(0xFF6B7280) // Muted text / tertiary
val FixoNeutral400 = Color(0xFF9CA3AF) // Placeholder text / subtle icons
val FixoNeutral300 = Color(0xFFD1D5DB) // Subtle card & field borders
val FixoNeutral200 = Color(0xFFE5E7EB) // Dividers
val FixoNeutral100 = Color(0xFFF3F4F6) // Subtle card surface in light
val FixoNeutral50  = Color(0xFFF9FAFB) // Clean light canvas background
val FixoWhite      = Color(0xFFFFFFFF)

// --- Semantic Accents (Trust, Verification & Status) ---
val FixoEmerald500 = Color(0xFF10B981) // Verified badge, positive escrow
val FixoEmerald600 = Color(0xFF059669)
val FixoEmerald100 = Color(0xFFD1FAE5)
val FixoEmerald50  = Color(0xFFECFDF5)

val FixoRed500     = Color(0xFFEF4444) // Error, dispute, cancellation
val FixoRed50      = Color(0xFFFEF2F2)

// =========================================================================
// Backward-Compatibility Aliases
// Replaces prior generic cobalt blue & raw amber with official FIXO Navy & Gold
// =========================================================================
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
