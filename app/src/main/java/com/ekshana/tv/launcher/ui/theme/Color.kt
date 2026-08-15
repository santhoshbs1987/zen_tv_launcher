package com.ekshana.tv.launcher.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Ultra-Modern OLED & Dark Ambient Palette ─────────────────────────────────
val DeepSpaceBlack = Color(0xFF07090E)
val ObsidianDark   = Color(0xFF0E131F)
val SlateElevated  = Color(0xFF161C2C)
val DarkBg         = Color(0xFF07090E) // Primary base

// ── Card Surface Tokens ──────────────────────────────────────────────────────
val CardBg         = Color(0xFF141926)
val CardFocusedBg  = Color(0xFF222B40)
val CardBorderIdle = Color(0xFF1F283C)

// ── Dialogs & Glassmorphism ──────────────────────────────────────────────────
val DialogBg       = Color(0xFF131826)
val DialogSurface  = Color(0xFF1C2438)

// ── Accents & Focused Highlights (Electric Cyan / Aqua Neon) ─────────────────
val AccentCyan     = Color(0xFF00F0FF)
val AccentBlue     = Color(0xFF38BDF8)
val AccentViolet   = Color(0xFF818CF8)
val FocusBorderColor = Color(0xFF00F0FF)

// ── Typography Colors ────────────────────────────────────────────────────────
val TextPrimary    = Color(0xFFF8FAFC)
val TextSecondary  = Color(0xFF94A3B8)
val TextMuted      = Color(0xFF64748B)

// ── Modern Gradients ─────────────────────────────────────────────────────────
val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        DeepSpaceBlack,
        ObsidianDark,
        DeepSpaceBlack
    )
)

val CardGlowGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF00F0FF),
        Color(0xFF38BDF8),
        Color(0xFF818CF8)
    )
)