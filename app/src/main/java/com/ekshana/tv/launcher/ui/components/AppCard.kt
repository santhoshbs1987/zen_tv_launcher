package com.ekshana.tv.launcher.ui.components

import android.view.KeyEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.ui.theme.AccentCyan
import com.ekshana.tv.launcher.ui.theme.CardBg
import com.ekshana.tv.launcher.ui.theme.CardBorderIdle
import com.ekshana.tv.launcher.ui.theme.CardFocusedBg
import com.ekshana.tv.launcher.ui.theme.DialogSurface
import com.ekshana.tv.launcher.ui.theme.FocusBorderColor
import com.ekshana.tv.launcher.ui.theme.TextPrimary
import com.ekshana.tv.launcher.ui.theme.TextSecondary

/**
 * TV App Card with standard TV Card click & long click support
 * plus Remote Menu key shortcut.
 *
 * Optimized for standard 720p / 1080p TV Viewport & Overscan:
 * - 48dp Icon size with 54dp inner pill
 * - Scaled focus growth (1.06x) to avoid clipping adjacent grid items
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppCard(
    label: String,
    iconBitmap: ImageBitmap?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFocused: () -> Unit = {},
    focusRequester: FocusRequester? = null,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }

    val baseModifier = if (focusRequester != null) {
        modifier
            .focusRequester(focusRequester)
            .onFocusChanged { state ->
                isFocused = state.isFocused
                if (state.isFocused) onFocused()
            }
    } else {
        modifier.onFocusChanged { state ->
            isFocused = state.isFocused
            if (state.isFocused) onFocused()
        }
    }

    val cardModifier = baseModifier.onPreviewKeyEvent { keyEvent ->
        val native = keyEvent.nativeKeyEvent
        val code = native.keyCode
        val isActionDown = native.action == KeyEvent.ACTION_DOWN
        val isFirstDown = isActionDown && native.repeatCount == 0

        when (code) {
            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_GUIDE,
            KeyEvent.KEYCODE_INFO,
            KeyEvent.KEYCODE_PROG_BLUE,
            KeyEvent.KEYCODE_BOOKMARK,
            KeyEvent.KEYCODE_BUTTON_Y -> {
                if (isFirstDown) {
                    onLongClick()
                    return@onPreviewKeyEvent true
                }
            }
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_BUTTON_A -> {
                if (isFirstDown) {
                    onClick()
                    return@onPreviewKeyEvent true
                }
            }
        }
        false
    }

    Card(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = cardModifier,
        shape = CardDefaults.shape(
            shape = RoundedCornerShape(12.dp),
            focusedShape = RoundedCornerShape(12.dp)
        ),
        scale = CardDefaults.scale(
            scale = 1.0f,
            focusedScale = 1.06f
        ),
        border = CardDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, CardBorderIdle),
                shape = RoundedCornerShape(12.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, FocusBorderColor),
                shape = RoundedCornerShape(12.dp)
            )
        ),
        colors = CardDefaults.colors(
            containerColor = CardBg,
            focusedContainerColor = CardFocusedBg
        ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isFocused) DialogSurface else Color(0xFF101420)),
                contentAlignment = Alignment.Center
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = label,
                        modifier = Modifier.size(42.dp),
                    )
                } else {
                    Text(
                        text = if (label.isNotEmpty()) label.take(1).uppercase() else "•",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFocused) AccentCyan else TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isFocused) TextPrimary else TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
