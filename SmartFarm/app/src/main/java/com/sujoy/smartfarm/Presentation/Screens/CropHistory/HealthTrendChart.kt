package com.sujoy.smartfarm.Presentation.Screens.CropHistory

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sujoy.smartfarm.Presentation.Utils.CropRecommend.localizedDigits
import com.sujoy.smartfarm.R
import com.sujoy.smartfarm.ui.theme.GreenPrimary
import com.sujoy.smartfarm.ui.theme.TextPrimary
import com.sujoy.smartfarm.ui.theme.TextSecondary
import com.sujoy.smartfarm.ui.theme.WhitePure

@Composable
fun HealthTrendChart(
    scores: List<Int>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("📈", fontSize = 16.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    stringResource(R.string.health_trend_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            if (scores.isNotEmpty()) {
                Text(
                    localizedDigits(stringResource(R.string.latest_score_label, scores.last())),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (scores.size < 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.not_enough_trend_data),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            return
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 4.dp)
        ) {
            val maxScore = 100f
            val topPad = 16f
            val bottomPad = 16f
            val chartHeight = size.height - topPad - bottomPad
            val spacing = if (scores.size > 1) size.width / (scores.size - 1) else size.width

            fun yFor(score: Int) =
                topPad + chartHeight - (score / maxScore) * chartHeight

            val points = scores.mapIndexed { index, score ->
                Offset(x = spacing * index, y = yFor(score))
            }

            // ── Horizontal grid lines (25 / 50 / 75 / 100)
            val gridLevels = listOf(0, 25, 50, 75, 100)
            gridLevels.forEach { level ->
                val y = yFor(level)
                drawLine(
                    color = Color(0xFFE0EDE0),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
            }

            // ── Smooth line path (simple curve through midpoints)
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.lastIndex) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val midX = (p0.x + p1.x) / 2
                    cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
                }
            }

            // ── Gradient fill under the curve
            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(points.last().x, size.height - bottomPad)
                lineTo(points.first().x, size.height - bottomPad)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GreenPrimary.copy(alpha = 0.28f),
                        GreenPrimary.copy(alpha = 0.02f)
                    ),
                    startY = 0f,
                    endY = size.height
                )
            )

            // ── Line itself
            drawPath(
                path = linePath,
                color = GreenPrimary,
                style = Stroke(width = 7f, cap = StrokeCap.Round)
            )

            // ── Points
            points.forEachIndexed { index, point ->
                val isLast = index == points.lastIndex
                drawCircle(
                    color = WhitePure,
                    radius = if (isLast) 13f else 10f,
                    center = point
                )
                drawCircle(
                    color = GreenPrimary,
                    radius = if (isLast) 9f else 6.5f,
                    center = point
                )
            }
        }
    }
}
