package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// FIXO Official Logo-Derived Design System Palette
// Derived precisely from the official FIXO brand asset:
// - Deep Midnight Navy: Authoritative background & F-stem
// - Golden Amber Accent: Radiant wing & dot
// - Crisp White & Restrained Neutrals
// =========================================================================

// --- FIXO Navy (Primary Brand Foundation - Logo F-Stem & Midnight Base) ---
val FixoNavy950 = Color(0xFF0B132B) // Ultra midnight obsidian base for dark canvas & splash
val FixoNavy900 = Color(0xFF0F172A) // Authoritative FIXO Navy (logo "F" stem & primary dark)
val FixoNavy850 = Color(0xFF141E33)
val FixoNavy800 = Color(0xFF1E293B) // Dark mode card / elevated surface
val FixoNavy700 = Color(0xFF283853) // Dark mode container & subtle border
val FixoNavy600 = Color(0xFF334A6E) // Medium navy accent
val FixoNavy500 = Color(0xFF47608A) // Muted navy
val FixoNavy200 = Color(0xFFCBD5E1)
val FixoNavy100 = Color(0xFFE2E8F0) // Soft navy badge container
val FixoNavy50  = Color(0xFFF1F5F9) // Light navy tint for selected containers

// --- FIXO Gold (Authoritative Brand Accent - Logo Wing & Circular Dot) ---
val FixoGold400 = Color(0xFFFBBF24) // Bright gold highlight on upper wing
val FixoGold500 = Color(0xFFF59E0B) // Radiant golden-amber accent from logo wing & dot
val FixoGold600 = Color(0xFFD97706) // Deep amber shadow & high-contrast button
val FixoGold700 = Color(0xFFB45309) // Warm gold text on light surface
val FixoGold200 = Color(0xFFFDE68A)
val FixoGold100 = Color(0xFFFEF3C7) // Soft gold container / pending badge background
val FixoGold50  = Color(0xFFFFFBEB) // Warm light tint

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
