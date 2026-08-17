package com.ekshana.tv.launcher.ui.components

import android.view.KeyEvent
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.ui.theme.CardGlassBorder

private val CardShape = RoundedCornerShape(14.dp)

/**
 * Ultra-lightweight TV App Card optimized for 60fps DPAD traversal on 1GB RAM TVs.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppCard(
    label: String,
    packageName: String,
    iconBitmap: ImageBitmap?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFocused: () -> Unit = {},
    isReordering: Boolean = false,
    onMoveDirection: (delta: Int) -> Unit = {},
    onFinishReordering: () -> Unit = {},
    focusRequester: FocusRequester? = null,
    cardHeight: Dp = 74.dp,
    modifier: Modifier = Modifier,
) {
    var longPressArmed by remember { mutableStateOf(false) }

    val keyInterceptModifier = modifier.onPreviewKeyEvent { keyEvent ->
        val native = keyEvent.nativeKeyEvent
        val code = native.keyCode

        if (isReordering) {
            if (native.action == KeyEvent.ACTION_DOWN) {
                when (code) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> { onMoveDirection(-1); return@onPreviewKeyEvent true }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> { onMoveDirection(1); return@onPreviewKeyEvent true }
                    KeyEvent.KEYCODE_DPAD_UP -> { onMoveDirection(-6); return@onPreviewKeyEvent true }
                    KeyEvent.KEYCODE_DPAD_DOWN -> { onMoveDirection(6); return@onPreviewKeyEvent true }
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.KEYCODE_NUMPAD_ENTER,
                    KeyEvent.KEYCODE_BACK -> { onFinishReordering(); return@onPreviewKeyEvent true }
                }
            }
            return@onPreviewKeyEvent false
        }

        if (code == KeyEvent.KEYCODE_DPAD_CENTER ||
            code == KeyEvent.KEYCODE_ENTER ||
            code == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            when (native.action) {
                KeyEvent.ACTION_DOWN -> {
                    if (native.repeatCount >= 1 && !longPressArmed) {
                        longPressArmed = true
                        return@onPreviewKeyEvent true
                    }
                    if (longPressArmed) return@onPreviewKeyEvent true
                }
                KeyEvent.ACTION_UP -> {
                    if (longPressArmed) {
                        longPressArmed = false
                        onLongClick()
                        return@onPreviewKeyEvent true
                    }
                }
            }
        }
        false
    }

    val baseModifier = if (focusRequester != null) {
        keyInterceptModifier
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) onFocused() }
    } else {
        keyInterceptModifier.onFocusChanged { if (it.isFocused) onFocused() }
    }

    val isBanner = iconBitmap != null && iconBitmap.width > iconBitmap.height * 1.3f
    val style = remember(packageName, label) { getAppStyle(packageName, label) }

    Card(
        onClick = if (isReordering) onFinishReordering else onClick,
        onLongClick = { /* handled via key intercept */ },
        modifier = baseModifier.height(cardHeight),
        shape = CardDefaults.shape(CardShape),
        border = CardDefaults.border(
            border = Border(
                border = BorderStroke(
                    width = if (isReordering) 2.dp else 1.dp,
                    color = if (isReordering) Color(0xFF00E5FF) else CardGlassBorder
                ),
                shape = CardShape
            ),
            focusedBorder = Border(
                border = BorderStroke(
                    width = if (isReordering) 3.dp else 2.5.dp,
                    color = if (isReordering) Color(0xFF00E5FF) else Color.White
                ),
                shape = CardShape
            )
        ),
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CardShape)
                .background(style.bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (iconBitmap != null) {
                if (isBanner) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            bitmap = iconBitmap,
                            contentDescription = label,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp)
                ) {
                    Text(
                        text = if (label.isNotEmpty()) label.take(1).uppercase() else "•",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = style.textColor
                    )
                    if (label.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = style.textColor.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            if (longPressArmed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CardShape)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Lightweight Solid Palette
// -----------------------------------------------------------------------------

data class AppCardStyle(
    val bgColor: Color,
    val textColor: Color = Color.White,
    val borderColor: Color = Color.Transparent,
)

fun getAppStyle(packageName: String, label: String): AppCardStyle {
    val pkg = packageName.lowercase()
    val name = label.lowercase()

    return when {
        pkg.contains("netflix") || name.contains("netflix") -> AppCardStyle(
            bgColor = Color(0xFFFFFFFF),
            textColor = Color(0xFFE50914),
            borderColor = Color(0x20000000)
        )
        pkg.contains("youtube") || name.contains("youtube") || pkg.contains("smarttube") -> AppCardStyle(
            bgColor = Color(0xFFF4F4F6),
            textColor = Color(0xFF282828),
            borderColor = Color(0x20000000)
        )
        pkg.contains("amazon") && pkg.contains("video") || name.contains("prime") -> AppCardStyle(
            bgColor = Color(0xFF0072A0),
            textColor = Color.White
        )
        pkg.contains("appletv") -> AppCardStyle(
            bgColor = Color(0xFF1B1D22),
            textColor = Color.White
        )
        pkg.contains("jellyfin") || name.contains("jellyfin") -> AppCardStyle(
            bgColor = Color(0xFF00A4DC),
            textColor = Color.White
        )
        pkg.contains("mxtech") || name.contains("mx player") -> AppCardStyle(
            bgColor = Color(0xFF0C78E4),
            textColor = Color.White
        )
        pkg.contains("hotstar") || name.contains("hotstar") || name.contains("disney") -> AppCardStyle(
            bgColor = Color(0xFF0C5FE8),
            textColor = Color.White
        )
        pkg.contains("sonyliv") || name.contains("sonyliv") || name.contains("sony liv") -> AppCardStyle(
            bgColor = Color(0xFF12141A),
            textColor = Color.White
        )
        pkg.contains("stremio") || name.contains("stremio") -> AppCardStyle(
            bgColor = Color(0xFF14172C),
            textColor = Color.White
        )
        pkg.contains("browsehere") || name.contains("browsehere") || name.contains("browser") -> AppCardStyle(
            bgColor = Color(0xFF2563EB),
            textColor = Color.White
        )
        pkg.contains("settings") || name.contains("setting") -> AppCardStyle(
            bgColor = Color(0xFF475569),
            textColor = Color.White
        )
        pkg.contains("downloader") || name.contains("downloader") -> AppCardStyle(
            bgColor = Color(0xFFEA580C),
            textColor = Color.White
        )
        pkg.contains("vending") || pkg.contains("play") || name.contains("play") -> AppCardStyle(
            bgColor = Color(0xFFFFFFFF),
            textColor = Color(0xFF1E293B)
        )
        name == "tv" || pkg.contains("android.tv") || name.contains("channels") -> AppCardStyle(
            bgColor = Color(0xFFFFFFFF),
            textColor = Color(0xFF991B1B)
        )
        name.contains("media") || name.contains("gallery") || name.contains("photo") -> AppCardStyle(
            bgColor = Color(0xFF283244),
            textColor = Color.White
        )
        else -> {
            val hash = (pkg.hashCode() and 0x7FFFFFFF) % 4
            when (hash) {
                0 -> AppCardStyle(bgColor = Color(0xFF1E293B))
                1 -> AppCardStyle(bgColor = Color(0xFF263238))
                2 -> AppCardStyle(bgColor = Color(0xFF334155))
                else -> AppCardStyle(bgColor = Color(0xFF1F2937))
            }
        }
    }
}
