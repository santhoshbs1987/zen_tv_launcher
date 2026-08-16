package com.ekshana.tv.launcher.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.ekshana.tv.launcher.data.AppInfo

/**
 * 6-Column Landscape Squircle Grid matching Apple TV home grid layout.
 *
 * Each card is sized identically to the top row (74.dp height, proportional width).
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AllAppsGrid(
    apps: List<AppInfo>,
    focusRequesters: Map<String, FocusRequester> = emptyMap(),
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onAppFocused: (AppInfo) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 44.dp, end = 44.dp, top = 8.dp, bottom = 32.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        items(items = apps, key = { it.packageName }) { app ->
            AppCard(
                label = app.label,
                packageName = app.packageName,
                iconBitmap = app.iconBitmap,
                onClick = { onAppClick(app) },
                onLongClick = { onAppLongClick(app) },
                onFocused = { onAppFocused(app) },
                focusRequester = focusRequesters[app.packageName],
                cardHeight = 74.dp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
