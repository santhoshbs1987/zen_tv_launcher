package com.ekshana.tv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.ui.theme.AccentBlue
import com.ekshana.tv.launcher.ui.theme.TextMuted
import com.ekshana.tv.launcher.ui.theme.TextPrimary

/**
 * All Apps Grid styled identically to Favorites with uniform 112dp item sizing,
 * adaptive columns, and aligned horizontal margins.
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
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 44.dp, bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF38BDF8).copy(alpha = 0.15f))
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⊞",
                    fontSize = 10.5.sp,
                    color = AccentBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "ALL APPS",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 1.1.sp,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${apps.size}",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 112.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(start = 44.dp, end = 44.dp, top = 2.dp, bottom = 28.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(items = apps, key = { it.packageName }) { app ->
                AppCard(
                    label = app.label,
                    iconBitmap = app.iconBitmap,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) },
                    onFocused = { onAppFocused(app) },
                    focusRequester = focusRequesters[app.packageName],
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
