package com.ekshana.tv.launcher.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Zen Launcher Refined Deep Glassmorphic Palette ────────────────────────────
val ZenBgTop                = Color(0xFF141923) // Deep rich dark indigo/slate
val ZenBgMid                = Color(0xFF0D1117) // Ultra-smooth deep midnight
val ZenBgBot                = Color(0xFF080B10) // Pure OLED base

// ── Glass & Translucent Surface Tokens ────────────────────────────────────────
val TopGlassContainerBg     = Color(0x1AFFFFFF) // Frosted glass container fill
val TopGlassContainerBorder = Color(0x28FFFFFF) // Ultra-fine frosted border

val CardGlassBg             = Color(0x18FFFFFF) // Card glass surface
val CardGlassBorder         = Color(0x24FFFFFF) // Subtle specular rim
val CardGlassFocusedBorder  = Color(0xFFFFFFFF) // Crisp white specular border

val CardLightBg             = Color(0xFFFFFFFF)
val CardDarkBg              = Color(0xFF1C222C)
val CardDefaultSurface      = Color(0xFF181D26)
val CardFocusedGlow         = Color(0x80000000)

// ── Modal & Dialog Tokens ─────────────────────────────────────────────────────
val ModalScrim              = Color(0xB3000000) // 70% OLED dark backdrop scrim
val ModalGlassBg            = Color(0xF2151A24) // Deep frosted slate-indigo modal surface
val ModalGlassBorder        = Color(0x33FFFFFF) // Specular modal rim
val ModalSectionBg          = Color(0x14FFFFFF) // Inner section card fill

// ── Interactive Button & Tile Tokens ──────────────────────────────────────────
val ButtonGlassBg           = Color(0x1AFFFFFF) // Resting glass button
val ButtonGlassFocusedBg    = Color(0x38FFFFFF) // Focused frosted highlight
val ButtonGlassBorder       = Color(0x24FFFFFF) // Resting button border
val ButtonGlassFocusedBorder = Color(0xFFFFFFFF) // Focused crisp white ring

val ButtonDangerBg          = Color(0x26EF4444) // Danger action resting
val ButtonDangerFocusedBg   = Color(0x66EF4444) // Danger action focused

val InputActiveBadgeBg      = Color(0xFF0284C7) // Connected HDMI / ARC badge
val InputActiveBadgeText    = Color(0xFFFFFFFF)

// ── Typography & Icon Colors ──────────────────────────────────────────────────
val StatusTextPrimary       = Color(0xFFF8FAFC) // High-contrast crisp typography
val StatusTextSecondary     = Color(0xFF94A3B8) // Slate 400 subtitle color
val StatusTextTertiary      = Color(0xFF64748B) // Slate 500 meta text
val StatusIconColor         = Color(0xFF94A3B8)
val StatusIconActive        = Color(0xFFFFFFFF)

val CardTextLight           = Color(0xFFFFFFFF)
val CardTextDark            = Color(0xFF0F172A)
val CardSubtextLight        = Color(0xFF94A3B8)
val CardSubtextDark         = Color(0xFF64748B)

// ── Background Gradients ──────────────────────────────────────────────────────
val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        ZenBgTop,
        ZenBgMid,
        ZenBgBot
    )
)

val ModalSheenGradient = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.16f),
        Color.Transparent
    )
)