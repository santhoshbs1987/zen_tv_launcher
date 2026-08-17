@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.ekshana.tv.launcher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val LauncherColorScheme = darkColorScheme(
    primary = StatusTextPrimary,
    secondary = StatusIconColor,
    background = ZenBgTop,
    surface = ZenBgMid,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ZenLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LauncherColorScheme,
        typography = Typography,
        content = content,
    )
}