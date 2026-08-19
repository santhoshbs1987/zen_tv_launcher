package com.ekshana.tv.launcher.ui.components

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.input.key.onPreviewKeyEvent
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HiddenAppsModal(
    hiddenApps: List<AppInfo>,
    onUnhideApp: (AppInfo) -> Unit,
    onUnhideAll: () -> Unit,
    onDismiss: () -> Unit,
) {
    val firstItemRequester = remember { FocusRequester() }

    BackHandler { onDismiss() }

    LaunchedEffect(Unit) {
        try {
            firstItemRequester.requestFocus()
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
                .width(420.dp)
                .heightIn(max = 480.dp)
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
                // Modal Title
                Text(
                    text = "Hidden Apps",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusTextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Select an app to restore it to the home grid",
                    fontSize = 11.5.sp,
                    color = StatusTextSecondary,
                )

                Spacer(Modifier.height(16.dp))

                // Hidden Apps List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(hiddenApps, key = { it.packageName }) { app ->
                        val isFirst = hiddenApps.firstOrNull()?.packageName == app.packageName
                        HiddenAppRowItem(
                            app = app,
                            focusRequester = if (isFirst) firstItemRequester else null,
                            onUnhide = { onUnhideApp(app) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (hiddenApps.size > 1) {
                        Button(
                            onClick = {
                                onUnhideAll()
                                onDismiss()
                            },
                            shape = ButtonDefaults.shape(shape = RoundedCornerShape(14.dp)),
                            border = ButtonDefaults.border(
                                border = Border(BorderStroke(1.dp, ButtonGlassBorder), shape = RoundedCornerShape(14.dp)),
                                focusedBorder = Border(BorderStroke(2.dp, ButtonGlassFocusedBorder), shape = RoundedCornerShape(14.dp))
                            ),
                            colors = ButtonDefaults.colors(
                                containerColor = ButtonGlassBg,
                                focusedContainerColor = ButtonGlassFocusedBg
                            ),
                            contentPadding = PaddingValues(vertical = 10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Unhide All",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = StatusTextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        shape = ButtonDefaults.shape(shape = RoundedCornerShape(14.dp)),
                        border = ButtonDefaults.border(
                            border = Border(BorderStroke(1.dp, ButtonGlassBorder), shape = RoundedCornerShape(14.dp)),
                            focusedBorder = Border(BorderStroke(2.dp, ButtonGlassFocusedBorder), shape = RoundedCornerShape(14.dp))
                        ),
                        colors = ButtonDefaults.colors(
                            containerColor = ButtonGlassBg,
                            focusedContainerColor = ButtonGlassFocusedBg
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Done",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = StatusTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
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
private fun HiddenAppRowItem(
    app: AppInfo,
    focusRequester: FocusRequester?,
    onUnhide: () -> Unit,
) {
    var isFocused by remember { mutableStateOf(false) }

    val baseModifier = if (focusRequester != null) {
        Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
    } else {
        Modifier.onFocusChanged { isFocused = it.isFocused }
    }

    Button(
        onClick = onUnhide,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(12.dp)),
        scale = ButtonDefaults.scale(scale = 1.0f, focusedScale = 1.0f),
        border = ButtonDefaults.border(
            border = Border(BorderStroke(1.dp, ButtonGlassBorder), shape = RoundedCornerShape(12.dp)),
            focusedBorder = Border(BorderStroke(2.dp, ButtonGlassFocusedBorder), shape = RoundedCornerShape(12.dp))
        ),
        colors = ButtonDefaults.colors(
            containerColor = ButtonGlassBg,
            focusedContainerColor = ButtonGlassFocusedBg
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        modifier = baseModifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (app.iconBitmap != null) {
                Image(
                    bitmap = app.iconBitmap,
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = app.label,
                fontSize = 13.5.sp,
                fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Normal,
                color = StatusTextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Unhide 👁",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = if (isFocused) Color.White else StatusTextSecondary,
            )
        }
    }
}
