package com.ekshana.tv.launcher.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.ekshana.tv.launcher.data.TvRecommendation
import com.ekshana.tv.launcher.ui.theme.CardGlassBorder

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun RecommendationsRow(
    recommendations: List<TvRecommendation>,
    onRecommendationClick: (TvRecommendation) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (recommendations.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(
            text = "Watch Next",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.7f),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 44.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 44.dp)
                .focusRestorer(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            recommendations.forEach { item ->
                Card(
                    onClick = { onRecommendationClick(item) },
                    shape = CardDefaults.shape(shape = RoundedCornerShape(14.dp)),
                    border = CardDefaults.border(
                        border = Border(BorderStroke(1.dp, CardGlassBorder)),
                        focusedBorder = Border(BorderStroke(2.dp, Color.White))
                    ),
                    modifier = Modifier.size(width = 180.dp, height = 90.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF1E2230),
                                        Color(0xFF11141C)
                                    )
                                )
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = item.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            item.description?.let { desc ->
                                if (desc.isNotBlank()) {
                                    Text(
                                        text = desc,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
