package com.ekshana.tv.launcher.ui.components

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.data.TvInputItem
import com.ekshana.tv.launcher.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Native Android TV side panel for TV Inputs.
 * Replicates the authentic right-docked Google TV / Android TV native Inputs Side Sheet:
 * - Docks flush against the right edge of the screen (width ~340dp, height 100vh).
 * - Dark semi-translucent scrim on the left.
 * - Native TV Material 3 pill rows with active focus borders and high contrast typography.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvInputsModal(
    inputs: List<TvInputItem>,
    onSelectInput: (TvInputItem) -> Unit,
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
            .background(Color(0x99000000)),
        contentAlignment = Alignment.CenterEnd
    ) {
        // Native Right-Docked Drawer Sheet
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(350.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xF0181A20),
                            Color(0xF812141A)
                        )
                    )
                )
                .border(
                    BorderStroke(1.dp, Color(0x2BFFFFFF)),
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                )
                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                .padding(top = 28.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Side Panel Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp, start = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0x22FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📺",
                            fontSize = 18.sp
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Inputs",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                        Text(
                            text = "TV Source",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }

                // Inputs Item List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(inputs, key = { it.id }) { input ->
                        val isFirst = inputs.firstOrNull()?.id == input.id
                        NativeTvInputRowItem(
                            input = input,
                            focusRequester = if (isFirst) firstItemRequester else null,
                            onClick = {
                                onSelectInput(input)
                                onDismiss()
                            }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Footer / Back button
                Button(
                    onClick = onDismiss,
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(12.dp)),
                    border = ButtonDefaults.border(
                        border = Border(BorderStroke(1.dp, Color(0x1FFFFFFF)), shape = RoundedCornerShape(12.dp)),
                        focusedBorder = Border(BorderStroke(2.dp, Color.White), shape = RoundedCornerShape(12.dp))
                    ),
                    colors = ButtonDefaults.colors(
                        containerColor = Color(0x14FFFFFF),
                        focusedContainerColor = Color(0x33FFFFFF)
                    ),
                    contentPadding = PaddingValues(vertical = 11.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Close",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFCCCCCC),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun NativeTvInputRowItem(
    input: TvInputItem,
    focusRequester: FocusRequester?,
    onClick: () -> Unit,
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
        onClick = onClick,
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(14.dp)),
        scale = ButtonDefaults.scale(scale = 1.0f, focusedScale = 1.0f),
        border = ButtonDefaults.border(
            border = Border(BorderStroke(1.dp, Color(0x14FFFFFF)), shape = RoundedCornerShape(14.dp)),
            focusedBorder = Border(BorderStroke(2.dp, Color.White), shape = RoundedCornerShape(14.dp))
        ),
        colors = ButtonDefaults.colors(
            containerColor = Color(0x1E222A38),
            focusedContainerColor = Color(0xFF384358)
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        modifier = baseModifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        if (isFocused) Color(0x44FFFFFF) else Color(0x18FFFFFF),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = input.icon,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = input.label,
                    fontSize = 14.sp,
                    fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Medium,
                    color = Color.White,
                )
                Text(
                    text = input.description,
                    fontSize = 10.5.sp,
                    color = if (isFocused) Color(0xFFE0E0E0) else Color(0xFF8E95A5),
                    maxLines = 1,
                )
            }
        }
    }
}
