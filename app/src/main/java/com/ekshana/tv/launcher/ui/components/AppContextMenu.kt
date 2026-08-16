package com.ekshana.tv.launcher.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.data.AppInfo
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppContextMenu(
    app: AppInfo,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onMoveFavoriteLeft: () -> Unit,
    onMoveFavoriteRight: () -> Unit,
    onToggleHide: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onDismiss: () -> Unit,
) {
    val firstButtonRequester = remember { FocusRequester() }

    BackHandler { onDismiss() }

    LaunchedEffect(Unit) {
        delay(120)
        try {
            firstButtonRequester.requestFocus()
        } catch (_: Exception) { /* ignore */ }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(360.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1E2530))
                .padding(horizontal = 22.dp, vertical = 18.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Header
                Text(
                    text = app.label,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = app.packageName,
                    fontSize = 10.5.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                )
                Spacer(Modifier.height(14.dp))

                // 1. Favorite Toggle Button
                MenuActionButton(
                    text = if (isFavorite) "★  Remove from Favorites" else "☆  Add to Favorites",
                    focusRequester = firstButtonRequester,
                    onClick = {
                        onToggleFavorite()
                        onDismiss()
                    }
                )

                // 2. Reorder controls if already favorite
                if (isFavorite) {
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MenuActionButton(
                            text = "◀ Move Left",
                            modifier = Modifier.weight(1f),
                            onClick = { onMoveFavoriteLeft() }
                        )
                        MenuActionButton(
                            text = "Move Right ▶",
                            modifier = Modifier.weight(1f),
                            onClick = { onMoveFavoriteRight() }
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // 3. App Info & Cache
                MenuActionButton(
                    text = "ℹ️  App Info & Settings",
                    onClick = {
                        onAppInfo()
                        onDismiss()
                    }
                )

                Spacer(Modifier.height(6.dp))

                // 4. Hide App from Grid
                MenuActionButton(
                    text = "👁️  Hide App from Grid",
                    onClick = {
                        onToggleHide()
                        onDismiss()
                    }
                )

                Spacer(Modifier.height(6.dp))

                // 5. Uninstall App
                MenuActionButton(
                    text = "🗑️  Uninstall App",
                    onClick = {
                        onUninstall()
                        onDismiss()
                    }
                )

                Spacer(Modifier.height(10.dp))

                // 6. Close Menu
                MenuActionButton(
                    text = "✕  Close Menu",
                    textColor = Color(0xFF38BDF8),
                    onClick = onDismiss
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun MenuActionButton(
    text: String,
    onClick: () -> Unit,
    focusRequester: FocusRequester? = null,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    val btnModifier = if (focusRequester != null) {
        modifier.focusRequester(focusRequester)
    } else {
        modifier
    }

    Button(
        onClick = onClick,
        modifier = btnModifier,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(12.dp)),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, Color(0x20FFFFFF)),
                shape = RoundedCornerShape(12.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(12.dp)
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = Color(0xFF28303E),
            focusedContainerColor = Color(0xFF3B465A)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
