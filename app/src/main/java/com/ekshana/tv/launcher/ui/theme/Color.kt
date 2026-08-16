package com.ekshana.tv.launcher.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Apple TV / tvOS Refined Deep Glassmorphic Palette ────────────────────────
val AppleTvBgTop          = Color(0xFF141923) // Deep rich dark indigo/slate
val AppleTvBgMid          = Color(0xFF0D1117) // Ultra-smooth deep midnight
val AppleTvBgBot          = Color(0xFF080B10) // Pure OLED base

// ── Glass & Translucent Surface Tokens ────────────────────────────────────────
val TopGlassContainerBg   = Color(0x1FFFFFFF) // Apple TV Frosted Glass container
val TopGlassContainerBorder = Color(0x28FFFFFF) // Ultra-fine frosted border

val CardGlassBg           = Color(0x18FFFFFF) // Card glass surface
val CardGlassBorder       = Color(0x24FFFFFF) // Subtle specular rim
val CardGlassFocusedBorder = Color(0xFFFFFFFF) // Crisp white specular border

// Card Surfaces
val CardLightBg           = Color(0xFFFFFFFF)
val CardDarkBg            = Color(0xFF1C222C)
val CardDefaultSurface    = Color(0xFF181D26)
val CardFocusedGlow       = Color(0x80000000)

// ── Typography & Icon Colors ──────────────────────────────────────────────────
val StatusTextPrimary     = Color(0xFFF1F5F9) // Clean Apple SF style high-contrast light
val StatusIconColor       = Color(0xFF94A3B8)
val StatusIconActive      = Color(0xFFFFFFFF)

val CardTextLight         = Color(0xFFFFFFFF)
val CardTextDark          = Color(0xFF0F172A)
val CardSubtextLight      = Color(0xFF94A3B8)
val CardSubtextDark       = Color(0xFF64748B)

// ── Background Gradients ──────────────────────────────────────────────────────
val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        AppleTvBgTop,
        AppleTvBgMid,
        AppleTvBgBot
    )
)