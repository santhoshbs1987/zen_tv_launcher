@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.ekshana.tv.launcher.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

// TV launchers always render dark — no light-mode branch needed.
private val LauncherColorScheme = darkColorScheme(
    primary   = AccentBlue,
    secondary = TextSecondary,
    background = DarkBg,
    surface   = CardBg,
    onPrimary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FastLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LauncherColorScheme,
        typography  = Typography,
        content     = content,
    )
}