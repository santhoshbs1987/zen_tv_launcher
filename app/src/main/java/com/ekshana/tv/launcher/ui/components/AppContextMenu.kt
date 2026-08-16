package com.ekshana.tv.launcher.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.data.AppInfo
import com.ekshana.tv.launcher.ui.theme.*
import kotlinx.coroutines.delay

/**
 * In-Hierarchy Floating Glass Context Menu Modal.
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
            .background(ModalScrim),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(380.dp)
                .shadow(28.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black, spotColor = Color.Black)
                .clip(RoundedCornerShape(22.dp))
                .background(ModalGlassBg)
                .border(BorderStroke(1.dp, ModalGlassBorder), RoundedCornerShape(22.dp))
                .padding(horizontal = 24.dp, vertical = 20.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // App Icon / Thumbnail & Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (app.iconBitmap != null) {
                        Image(
                            bitmap = app.iconBitmap,
                            contentDescription = app.label,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                    Column {
                        Text(
                            text = app.label,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusTextPrimary,
                            maxLines = 1,
                        )
                        Text(
                            text = app.packageName,
                            fontSize = 10.5.sp,
                            color = StatusTextSecondary,
                            maxLines = 1,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Actions List
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Hide / Unhide
                    MenuActionButton(
                        text = "👁  Hide from Home Grid",
                        focusRequester = firstButtonRequester,
                        onClick = {
                            onToggleHide()
                            onDismiss()
                        }
                    )

                    // 2. App Info (System Details)
                    MenuActionButton(
                        text = "ℹ️  App Info & Permissions",
                        onClick = {
                            onAppInfo()
                            onDismiss()
                        }
                    )

                    // 3. Uninstall App
                    MenuActionButton(
                        text = "🗑  Uninstall App",
                        isDanger = true,
                        onClick = {
                            onUninstall()
                            onDismiss()
                        }
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Close / Cancel Button
                Button(
                    onClick = onDismiss,
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(14.dp)),
                    border = ButtonDefaults.border(
                        border = Border(
                            border = BorderStroke(1.dp, ButtonGlassBorder),
                            shape = RoundedCornerShape(14.dp)
                        ),
                        focusedBorder = Border(
                            border = BorderStroke(2.dp, ButtonGlassFocusedBorder),
                            shape = RoundedCornerShape(14.dp)
                        )
                    ),
                    colors = ButtonDefaults.colors(
                        containerColor = ButtonGlassBg,
                        focusedContainerColor = ButtonGlassFocusedBg
                    ),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Close",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = StatusTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Top Specular Sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .align(Alignment.TopCenter)
                    .background(ModalSheenGradient)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun MenuActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
    focusRequester: FocusRequester? = null,
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.04f else 1.0f,
        animationSpec = tween(150),
        label = "menuButtonScale"
    )

    val baseModifier = if (focusRequester != null) {
        modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
    } else {
        modifier.onFocusChanged { isFocused = it.isFocused }
    }

    Button(
        onClick = onClick,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(14.dp)),
        scale = ButtonDefaults.scale(scale = 1.0f, focusedScale = 1.0f),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDanger) Color(0x30EF4444) else ButtonGlassBorder
                ),
                shape = RoundedCornerShape(14.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isDanger) Color(0xFFFCA5A5) else ButtonGlassFocusedBorder
                ),
                shape = RoundedCornerShape(14.dp)
            )
        ),
        colors = ButtonDefaults.colors(
            containerColor = if (isDanger) ButtonDangerBg else ButtonGlassBg,
            focusedContainerColor = if (isDanger) ButtonDangerFocusedBg else ButtonGlassFocusedBg
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.dp),
        modifier = baseModifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (isFocused) {
                    Modifier.shadow(12.dp, RoundedCornerShape(14.dp), ambientColor = Color.Black)
                } else {
                    Modifier
                }
            )
    ) {
        Text(
            text = text,
            fontSize = 13.5.sp,
            fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isDanger) Color(0xFFFCA5A5) else StatusTextPrimary,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
