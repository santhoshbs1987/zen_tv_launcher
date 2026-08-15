package com.ekshana.tv.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.ekshana.tv.launcher.ui.theme.AccentCyan
import com.ekshana.tv.launcher.ui.theme.TextMuted
import com.ekshana.tv.launcher.ui.theme.TextPrimary

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

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 44.dp, bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "★",
                    fontSize = 10.5.sp,
                    color = AccentCyan,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "FAVORITES",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 1.1.sp,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${favorites.size}",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 44.dp),
        ) {
            items(items = favorites, key = { it.packageName }) { app ->
                AppCard(
                    label = app.label,
                    iconBitmap = app.iconBitmap,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) },
                    onFocused = { onAppFocused(app) },
                    focusRequester = focusRequesters[app.packageName],
                    modifier = Modifier.width(112.dp),
                )
            }
        }
    }
}
