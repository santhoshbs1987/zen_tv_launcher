package com.ekshana.tv.launcher.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Zen Launcher Refined Obsidian Palette ─────────────────────────────────────
val ZenBgTop                = Color(0xFF141923)
val ZenBgMid                = Color(0xFF0D1117)
val ZenBgBot                = Color(0xFF080B10)

val BackgroundGradient      = Brush.verticalGradient(
    colors = listOf(ZenBgTop, ZenBgMid, ZenBgBot)
)

// ── Card & Surface Glass Tokens ───────────────────────────────────────────────
val CardGlassBorder         = Color(0x24FFFFFF)

// ── In-Hierarchy Modal Overlay Tokens ─────────────────────────────────────────
val ModalScrim              = Color(0xB3000000)
val ModalGlassBg            = Color(0xF2151A24)
val ModalGlassBorder        = Color(0x33FFFFFF)
val ModalSheenGradient      = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.16f),
        Color.Transparent
    )
)

// ── Modal Buttons ─────────────────────────────────────────────────────────────
val ButtonGlassBg           = Color(0x1AFFFFFF)
val ButtonGlassFocusedBg    = Color(0x38FFFFFF)
val ButtonGlassBorder       = Color(0x24FFFFFF)
val ButtonGlassFocusedBorder = Color(0xFFFFFFFF)

val ButtonDangerBg          = Color(0x26EF4444)
val ButtonDangerFocusedBg   = Color(0x66EF4444)

// ── Typography & Top Bar Status Tokens ────────────────────────────────────────
val StatusTextPrimary       = Color(0xFFF8FAFC)
val StatusTextSecondary     = Color(0xFF94A3B8)
val StatusIconColor         = Color(0xFF94A3B8)
val StatusIconActive        = Color(0xFFFFFFFF)