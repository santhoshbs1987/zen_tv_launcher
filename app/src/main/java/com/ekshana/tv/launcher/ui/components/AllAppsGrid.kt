package com.ekshana.tv.launcher.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.ekshana.tv.launcher.data.AppInfo

/**
 * 5-Column Landscape Squircle Grid.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AllAppsGrid(
    apps: List<AppInfo>,
    focusRequesters: Map<String, FocusRequester> = emptyMap(),
    reorderingPackage: String? = null,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onAppFocused: (AppInfo) -> Unit = {},
    onMoveApp: (packageName: String, delta: Int) -> Unit = { _, _ -> },
    onFinishReordering: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 44.dp, end = 44.dp, top = 8.dp, bottom = 32.dp),
        modifier = modifier
            .fillMaxSize()
            .focusRestorer(),
    ) {
        items(items = apps, key = { it.packageName }) { app ->
            val isReordering = app.packageName == reorderingPackage
            AppCard(
                label = app.label,
                packageName = app.packageName,
                iconBitmap = app.iconBitmap,
                onClick = { onAppClick(app) },
                onLongClick = { onAppLongClick(app) },
                onFocused = { onAppFocused(app) },
                isReordering = isReordering,
                onMoveDirection = { delta -> onMoveApp(app.packageName, delta) },
                onFinishReordering = onFinishReordering,
                focusRequester = focusRequesters[app.packageName],
                cardHeight = 78.dp,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
