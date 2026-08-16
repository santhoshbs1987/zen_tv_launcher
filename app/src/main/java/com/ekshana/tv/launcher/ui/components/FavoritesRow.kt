package com.ekshana.tv.launcher.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.ui.theme.TopGlassContainerBg
import com.ekshana.tv.launcher.ui.theme.TopGlassContainerBorder

/**
 * Apple TV Style Top Favorites Bar.
 *
 * Sized 100% identically to the grid items below (74.dp height, identical width columns).
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FavoritesRow(
    favorites: List<AppInfo>,
    focusRequesters: Map<String, FocusRequester> = emptyMap(),
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onAppFocused: (AppInfo) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (favorites.isEmpty()) return

    val displayFavorites = favorites.take(6)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(TopGlassContainerBg)
                .border(BorderStroke(1.dp, TopGlassContainerBorder), RoundedCornerShape(22.dp))
                .padding(horizontal = 10.dp, vertical = 10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                displayFavorites.forEach { app ->
                    AppCard(
                        label = app.label,
                        packageName = app.packageName,
                        iconBitmap = app.iconBitmap,
                        onClick = { onAppClick(app) },
                        onLongClick = { onAppLongClick(app) },
                        onFocused = { onAppFocused(app) },
                        focusRequester = focusRequesters[app.packageName],
                        cardHeight = 74.dp,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
