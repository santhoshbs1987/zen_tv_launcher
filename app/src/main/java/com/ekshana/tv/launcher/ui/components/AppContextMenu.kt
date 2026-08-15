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
import com.ekshana.tv.launcher.ui.theme.AccentCyan
import com.ekshana.tv.launcher.ui.theme.CardBg
import com.ekshana.tv.launcher.ui.theme.CardBorderIdle
import com.ekshana.tv.launcher.ui.theme.CardFocusedBg
import com.ekshana.tv.launcher.ui.theme.DialogBg
import com.ekshana.tv.launcher.ui.theme.FocusBorderColor
import com.ekshana.tv.launcher.ui.theme.TextPrimary
import com.ekshana.tv.launcher.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Clean Single-Page TV Context Menu (Zero Scrolling Needed).
 *
 * Designed to fit 100% on-screen simultaneously so all options
 * (Favorite, Reorder, App Info, Hide, Uninstall, Close) are directly
 * navigable and selectable with D-pad without clipping or scroll lock.
 */
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
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(360.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(DialogBg)
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
                    color = TextPrimary,
                    maxLines = 1,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = app.packageName,
                    fontSize = 10.5.sp,
                    color = TextSecondary,
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
                    text = "ℹ️  App Info & Cache",
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
                    textColor = AccentCyan,
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
    textColor: Color = TextPrimary,
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
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, CardBorderIdle),
                shape = RoundedCornerShape(10.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, FocusBorderColor),
                shape = RoundedCornerShape(10.dp)
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = CardBg,
            focusedContainerColor = CardFocusedBg
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
