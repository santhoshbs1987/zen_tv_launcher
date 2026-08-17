package com.ekshana.tv.launcher.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.ekshana.tv.launcher.R
import com.ekshana.tv.launcher.data.TvInputItem
import com.ekshana.tv.launcher.ui.theme.CardGlassBorder

private val CardShape = RoundedCornerShape(14.dp)

/**
 * Top Inputs Row matching exactly the layout, dimensions and visual style of App Cards:
 * - 5 equal cards aligned with the apps grid below.
 * - Hardware port artwork (HDMI connector, 3-color RCA jacks, Coax RF F-connector).
 * - Full parity with AppCard dimensions and focus highlights.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun InputsRow(
    inputs: List<TvInputItem>,
    onSelectInput: (TvInputItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (inputs.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 44.dp, end = 44.dp, top = 4.dp, bottom = 16.dp)
            .focusRestorer(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        inputs.forEach { input ->
            InputCard(
                input = input,
                onClick = { onSelectInput(input) },
                cardHeight = 78.dp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun InputCard(
    input: TvInputItem,
    onClick: () -> Unit,
    cardHeight: Dp,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }

    val iconRes = remember(input.id, input.label) {
        when {
            input.label.contains("AV", ignoreCase = true) -> R.drawable.ic_input_av_port
            input.label.contains("Antenna", ignoreCase = true) || input.id.contains("ADTV", ignoreCase = true) -> R.drawable.ic_input_antenna_port
            else -> R.drawable.ic_input_hdmi_port
        }
    }

    val displayLabel = remember(input.label) {
        when {
            input.label.contains("HDMI 1", ignoreCase = true) -> "HDMI 1"
            input.label.contains("HDMI 2", ignoreCase = true) -> "HDMI 2"
            input.label.contains("HDMI 3", ignoreCase = true) -> "HDMI 3"
            input.label.contains("AV", ignoreCase = true) -> "AV"
            input.label.contains("Antenna", ignoreCase = true) || input.label.contains("Cable", ignoreCase = true) -> "Antenna"
            else -> input.label
        }
    }

    val style = remember(input.label) {
        when {
            input.label.contains("HDMI 1", ignoreCase = true) -> AppCardStyle(
                bgColor = Color(0xFF0F172A),
                textColor = Color(0xFF38BDF8)
            )
            input.label.contains("HDMI 2", ignoreCase = true) -> AppCardStyle(
                bgColor = Color(0xFF0F172A),
                textColor = Color(0xFF38BDF8)
            )
            input.label.contains("HDMI 3", ignoreCase = true) -> AppCardStyle(
                bgColor = Color(0xFF0F172A),
                textColor = Color(0xFF38BDF8)
            )
            input.label.contains("AV", ignoreCase = true) -> AppCardStyle(
                bgColor = Color(0xFF1E1B2E),
                textColor = Color(0xFFFACC15)
            )
            else -> AppCardStyle(
                bgColor = Color(0xFF18222E),
                textColor = Color(0xFF34D399)
            )
        }
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .height(cardHeight)
            .onFocusChanged { isFocused = it.isFocused },
        shape = CardDefaults.shape(CardShape),
        border = CardDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, CardGlassBorder),
                shape = CardShape
            ),
            focusedBorder = Border(
                border = BorderStroke(2.5.dp, Color.White),
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
                .background(if (isFocused) style.bgColor.copy(alpha = 0.95f) else style.bgColor)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = displayLabel,
                    modifier = Modifier.size(38.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = displayLabel,
                    fontSize = 13.sp,
                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isFocused) Color.White else style.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
