package com.autosync.main.ui.theme

import androidx.compose.ui.graphics.Color

// Premium "Cyberpunk/Tesla" Palette

// Accent Colors
val NeonLime = Color(0xFFC0FF02) // The signature "Premium" accent
val NeonCyan = Color(0xFF00E5FF) // Secondary accent

// Dark Mode (Primary Mode)
val CharcoalBackground = Color(0xFF0F1014) // Deep, rich dark
val GunmetalSurface = Color(0xFF1E2025) // Card background
val GunmetalSurfaceVariant = Color(0xFF2A2D35) // Lighter card/sections

// Text Colors
val WhiteText = Color(0xFFFFFFFF)
val GrayText = Color(0xFF8E9099)

// Light Mode (Clean/Executive) - Optional fallback
val LightBackground = Color(0xFFF5F5F7)
val LightSurface = Color(0xFFFFFFFF)

// Mapping to Material Colors (Dark Mode Focus)
val PrimaryDark = NeonLime
val OnPrimaryDark = Color(0xFF000000) // Black text on Lime
val PrimaryContainerDark = GunmetalSurfaceVariant
val OnPrimaryContainerDark = NeonLime

val SecondaryDark = NeonCyan
val OnSecondaryDark = Color(0xFF000000)
val SecondaryContainerDark = Color(0xFF133640)
val OnSecondaryContainerDark = NeonCyan

val BackgroundDark = CharcoalBackground
val SurfaceDark = GunmetalSurface
val OnBackgroundDark = WhiteText
val OnSurfaceDark = WhiteText

val ErrorColor = Color(0xFFFF453A)
val SuccessColor = NeonLime // Reuse lime for success aesthetics