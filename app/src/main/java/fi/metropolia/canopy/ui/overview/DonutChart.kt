package fi.metropolia.canopy.ui.overview


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    size: Dp = 170.dp,
    strokeWidth: Dp = 26.dp,
    centerText: String,
    slices: List<EmissionSlice>
) {
    val total = max(0.000001, slices.sumOf { it.value }) // ettei nollalla jakoa

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx())
            var startAngle = -90f

            slices.forEach { slice ->
                val sweep = (slice.value / total * 360.0).toFloat()
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = stroke,
                    size = Size(size.toPx(), size.toPx())
                )
                startAngle += sweep
            }
        }

        Text(
            text = centerText,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}