package com.ekshana.tv.launcher.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
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
import com.ekshana.tv.launcher.data.TvInputItem
import com.ekshana.tv.launcher.data.TvInputManagerHelper
import kotlinx.coroutines.delay

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
            .background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(420.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF1E2530))
                .padding(20.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔌 TV Inputs & Sources",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Select hardware input to switch signal",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TvInputManagerHelper.inputs.forEachIndexed { index, input ->
                        val requester = if (index == 0) firstButtonRequester else null
                        InputItemButton(
                            input = input,
                            focusRequester = requester,
                            onClick = {
                                onSelectInput(input)
                                onDismiss()
                            }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
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
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✕  Cancel",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF38BDF8)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun InputItemButton(
    input: TvInputItem,
    onClick: () -> Unit,
    focusRequester: FocusRequester? = null,
) {
    val modifier = if (focusRequester != null) {
        Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    } else {
        Modifier.fillMaxWidth()
    }

    Button(
        onClick = onClick,
        modifier = modifier,
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
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = input.icon, fontSize = 16.sp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = input.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = input.description,
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
            Text(
                text = "Switch ➔",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF38BDF8)
            )
        }
    }
}
