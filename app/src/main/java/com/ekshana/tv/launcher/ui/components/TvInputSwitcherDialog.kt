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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.R
import com.ekshana.tv.launcher.data.TvInputItem
import com.ekshana.tv.launcher.data.TvInputManagerHelper
import com.ekshana.tv.launcher.ui.theme.*
import kotlinx.coroutines.delay

/**
 * In-Hierarchy TV Hardware Inputs Switcher Modal.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvInputSwitcherDialog(
    onSelectInput: (TvInputItem) -> Unit,
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
                .width(420.dp)
                .shadow(28.dp, RoundedCornerShape(22.dp), ambientColor = Color.Black, spotColor = Color.Black)
                .clip(RoundedCornerShape(22.dp))
                .background(ModalGlassBg)
                .border(BorderStroke(1.dp, ModalGlassBorder), RoundedCornerShape(22.dp))
                .padding(horizontal = 24.dp, vertical = 22.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Modal Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_tune),
                        contentDescription = "Inputs",
                        colorFilter = ColorFilter.tint(StatusIconActive),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "TV Inputs & Sources",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusTextPrimary
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Switch hardware input signal",
                    fontSize = 11.5.sp,
                    color = StatusTextSecondary
                )

                Spacer(Modifier.height(16.dp))

                // List of Input Devices
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TvInputManagerHelper.inputs.forEachIndexed { index, input ->
                        val requester = if (index == 0) firstButtonRequester else null
                        InputOptionButton(
                            input = input,
                            focusRequester = requester,
                            onClick = {
                                onSelectInput(input)
                                onDismiss()
                            }
                        )
                    }
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
                        modifier = Modifier.wrapContentWidth()
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
private fun InputOptionButton(
    input: TvInputItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.04f else 1.0f,
        animationSpec = tween(150),
        label = "inputButtonScale"
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = input.icon,
                    fontSize = 15.sp,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = input.label,
                        fontSize = 13.5.sp,
                        fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Medium,
                        color = StatusTextPrimary
                    )
                    Text(
                        text = input.description,
                        fontSize = 10.sp,
                        color = StatusTextSecondary
                    )
                }
            }
        }
    }
}
