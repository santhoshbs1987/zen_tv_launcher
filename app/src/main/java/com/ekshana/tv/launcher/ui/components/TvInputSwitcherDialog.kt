package com.ekshana.tv.launcher.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ekshana.tv.launcher.data.TvInputItem
import com.ekshana.tv.launcher.data.TvInputManagerHelper
import com.ekshana.tv.launcher.ui.theme.AccentBlue
import com.ekshana.tv.launcher.ui.theme.CardBg
import com.ekshana.tv.launcher.ui.theme.CardFocusedBg
import com.ekshana.tv.launcher.ui.theme.DialogBg
import com.ekshana.tv.launcher.ui.theme.TextPrimary
import com.ekshana.tv.launcher.ui.theme.TextSecondary
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
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* block outside clicks */ },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(420.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(DialogBg)
                .padding(24.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TV Inputs & Sources",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Select hardware input port",
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(16.dp))

                TvInputManagerHelper.inputs.forEachIndexed { index, input ->
                    val btnModifier = if (index == 0) {
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .focusRequester(firstButtonRequester)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    }

                    Button(
                        onClick = {
                            onSelectInput(input)
                            onDismiss()
                        },
                        modifier = btnModifier,
                        shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.colors(
                            containerColor = CardBg,
                            focusedContainerColor = CardFocusedBg
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(text = input.icon, fontSize = 16.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = input.label,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = input.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = CardBg,
                        focusedContainerColor = CardFocusedBg
                    ),
                ) {
                    Text("Close", color = AccentBlue)
                }
            }
        }
    }
}
