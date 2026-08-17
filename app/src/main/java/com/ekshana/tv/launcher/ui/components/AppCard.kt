package com.ekshana.tv.launcher.ui.components

import android.view.KeyEvent
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
import androidx.compose.ui.graphics.Brush
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
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.ui.theme.CardGlassBorder

/**
 * Modern Landscape Squircle App Card.
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
    focusRequester: FocusRequester? = null,
    cardHeight: Dp = 74.dp,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    // True once the key has been held long enough (first repeat) — armed state
    // We defer the menu open to ACTION_UP so the key is already released when the menu appears.
    var longPressArmed by remember { mutableStateOf(false) }

    val keyInterceptModifier = modifier.onPreviewKeyEvent { keyEvent ->
        val native = keyEvent.nativeKeyEvent
        val code = native.keyCode
        if (code == KeyEvent.KEYCODE_DPAD_CENTER ||
            code == KeyEvent.KEYCODE_ENTER ||
            code == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            when (native.action) {
                KeyEvent.ACTION_DOWN -> {
                    if (native.repeatCount >= 1 && !longPressArmed) {
                        // Key held past initial threshold → arm the long press
                        longPressArmed = true
                        return@onPreviewKeyEvent true // consume so Card never sees it
                    }
                    if (longPressArmed) {
                        return@onPreviewKeyEvent true // consume all subsequent repeats
                    }
                }
                KeyEvent.ACTION_UP -> {
                    if (longPressArmed) {
                        longPressArmed = false
                        onLongClick() // Safe: key is RELEASED before menu opens
                        return@onPreviewKeyEvent true // consume so Card's onClick doesn't fire
                    }
                }
            }
        }
        false
    }

    val baseModifier = if (focusRequester != null) {
        keyInterceptModifier
            .focusRequester(focusRequester)
            .onFocusChanged { state ->
                isFocused = state.isFocused
                if (state.isFocused) onFocused()
            }
    } else {
        keyInterceptModifier.onFocusChanged { state ->
            isFocused = state.isFocused
            if (state.isFocused) onFocused()
        }
    }

    val isBanner = iconBitmap != null && iconBitmap.width > iconBitmap.height * 1.3f
    val style = remember(packageName, label) { getAppStyle(packageName, label) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.09f else 1.0f,
        animationSpec = tween(durationMillis = 180),
        label = "cardFocusScale"
    )

    Card(
        onClick = onClick,
        onLongClick = { /* handled via onPreviewKeyEvent; fires on ACTION_UP after release */ },
        modifier = baseModifier
            .height(cardHeight)
            .scale(scale)
            .then(
                if (isFocused) {
                    Modifier.shadow(
                        elevation = 22.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color.Black.copy(alpha = 0.8f),
                        spotColor = Color.Black.copy(alpha = 0.9f)
                    )
                } else {
                    Modifier
                }
            ),
        shape = androidx.tv.material3.CardDefaults.shape(RoundedCornerShape(18.dp)),
        border = androidx.tv.material3.CardDefaults.border(
            border = androidx.tv.material3.Border(
                border = BorderStroke(
                    1.dp,
                    if (isFocused) Color.White else style.borderColor.takeIf { it != Color.Transparent } ?: CardGlassBorder
                ),
                shape = RoundedCornerShape(18.dp)
            ),
            focusedBorder = androidx.tv.material3.Border(
                border = BorderStroke(3.dp, Color.White),
                shape = RoundedCornerShape(18.dp)
            )
        ),
        colors = androidx.tv.material3.CardDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .then(
                    if (style.gradient != null) {
                        Modifier.background(style.gradient)
                    } else {
                        Modifier.background(style.bgColor)
                    }
                )
                .then(
                    if (isFocused) {
                        Modifier.border(3.dp, Color.White, RoundedCornerShape(18.dp))
                    } else {
                        Modifier
                    }
                ),
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

            // Specular Top-Light Sheen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isFocused) 0.18f else 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Long-press visual feedback: subtle pulsing border glow when key is held
            if (longPressArmed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Curated Brand / App Styling System
// -----------------------------------------------------------------------------

data class AppCardStyle(
    val bgColor: Color,
    val textColor: Color = Color.White,
    val borderColor: Color = Color.Transparent,
    val gradient: Brush? = null,
)

fun getAppStyle(packageName: String, label: String): AppCardStyle {
    val pkg = packageName.lowercase()
    val name = label.lowercase()

    return when {
        // Netflix
        pkg.contains("netflix") || name.contains("netflix") -> AppCardStyle(
            bgColor = Color(0xFFFFFFFF),
            textColor = Color(0xFFE50914),
            borderColor = Color(0x20000000)
        )

        // YouTube
        pkg.contains("youtube") || name.contains("youtube") || pkg.contains("smarttube") -> AppCardStyle(
            bgColor = Color(0xFFF4F4F6),
            textColor = Color(0xFF282828),
            borderColor = Color(0x20000000)
        )

        // Amazon Prime Video
        pkg.contains("amazon") && pkg.contains("video") || name.contains("prime") -> AppCardStyle(
            bgColor = Color(0xFF00A8E1),
            textColor = Color.White,
            gradient = Brush.linearGradient(listOf(Color(0xFF00A8E1), Color(0xFF0072A0)))
        )

        pkg.contains("appletv") -> AppCardStyle(
            bgColor = Color(0xFF1B1D22),
            textColor = Color.White,
            borderColor = Color(0x33FFFFFF)
        )

        // Zee5 / YuppTV
        pkg.contains("yupp") || name.contains("yupp") -> AppCardStyle(
            bgColor = Color(0xFF1E222B),
            textColor = Color.White,
            borderColor = Color(0x33FFFFFF)
        )

        // Jellyfin
        pkg.contains("jellyfin") || name.contains("jellyfin") -> AppCardStyle(
            bgColor = Color(0xFF16192E),
            textColor = Color.White,
            gradient = Brush.linearGradient(listOf(Color(0xFF00A4DC), Color(0xFFAA5CC3)))
        )

        // MX Player
        pkg.contains("mxtech") || name.contains("mx player") -> AppCardStyle(
            bgColor = Color(0xFF0C78E4),
            textColor = Color.White,
            gradient = Brush.linearGradient(listOf(Color(0xFF1D8CFA), Color(0xFF0B63C2)))
        )

        // Disney+ / Hotstar / JioHotstar
        pkg.contains("hotstar") || name.contains("hotstar") || name.contains("disney") -> AppCardStyle(
            bgColor = Color(0xFF0063E5),
            textColor = Color.White,
            gradient = Brush.horizontalGradient(
                listOf(
                    Color(0xFF0C5FE8),
                    Color(0xFF8820B4),
                    Color(0xFFE40066)
                )
            )
        )

        // SonyLIV / Sony
        pkg.contains("sonyliv") || name.contains("sonyliv") || name.contains("sony liv") -> AppCardStyle(
            bgColor = Color(0xFF12141A),
            textColor = Color.White,
            borderColor = Color(0x33FFFFFF)
        )

        // Stremio
        pkg.contains("stremio") || name.contains("stremio") -> AppCardStyle(
            bgColor = Color(0xFF14172C),
            textColor = Color.White,
            borderColor = Color(0x33FFFFFF)
        )

        // BrowseHere / Browser
        pkg.contains("browsehere") || name.contains("browsehere") || name.contains("browser") -> AppCardStyle(
            bgColor = Color(0xFF2E63E5),
            textColor = Color.White,
            gradient = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)))
        )

        // Settings
        pkg.contains("settings") || name.contains("setting") -> AppCardStyle(
            bgColor = Color(0xFF475569),
            textColor = Color.White
        )

        // Downloader
        pkg.contains("downloader") || name.contains("downloader") -> AppCardStyle(
            bgColor = Color(0xFFF97316),
            textColor = Color.White,
            gradient = Brush.linearGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C)))
        )

        // Google Play Movies / Store
        pkg.contains("vending") || pkg.contains("play") || name.contains("play") -> AppCardStyle(
            bgColor = Color(0xFFFFFFFF),
            textColor = Color(0xFF1E293B),
            borderColor = Color(0x20000000)
        )

        // TV / Live Channels
        name == "tv" || pkg.contains("android.tv") || name.contains("channels") -> AppCardStyle(
            bgColor = Color(0xFFFFFFFF),
            textColor = Color(0xFF991B1B),
            borderColor = Color(0x20000000)
        )

        // Media Player / Gallery
        name.contains("media") || name.contains("gallery") || name.contains("photo") -> AppCardStyle(
            bgColor = Color(0xFF283244),
            textColor = Color.White
        )

        // Default Surface Fallback
        else -> {
            val hash = (pkg.hashCode() and 0x7FFFFFFF) % 5
            when (hash) {
                0 -> AppCardStyle(bgColor = Color(0xFF263238), textColor = Color.White)
                1 -> AppCardStyle(bgColor = Color(0xFF1E293B), textColor = Color.White)
                2 -> AppCardStyle(bgColor = Color(0xFF334155), textColor = Color.White)
                3 -> AppCardStyle(bgColor = Color(0xFF3B4B59), textColor = Color.White)
                else -> AppCardStyle(bgColor = Color(0xFF1F2937), textColor = Color.White)
            }
        }
    }
}
