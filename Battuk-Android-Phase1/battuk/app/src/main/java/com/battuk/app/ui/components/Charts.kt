package com.battuk.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ChartSlice(val label: String, val value: Double, val color: Color)

@Composable
fun BattukPieChart(
    slices: List<ChartSlice>,
    modifier: Modifier = Modifier,
    centerLabel: String? = null
) {
    val total = slices.sumOf { it.value }.takeIf { it > 0 } ?: 1.0

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                var startAngle = -90f
                slices.forEach { slice ->
                    val sweep = (slice.value / total * 360f).toFloat()
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweep
                }
            }
            if (centerLabel != null) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(centerLabel, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            slices.forEach { slice ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(slice.color)
                    )
                    Text(
                        text = "  ${slice.label} (${((slice.value / total) * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

data class BarGroup(val label: String, val expense: Double, val income: Double = 0.0)

@Composable
fun BattukBarChart(
    groups: List<BarGroup>,
    modifier: Modifier = Modifier,
    expenseColor: Color = MaterialTheme.colorScheme.error,
    incomeColor: Color = MaterialTheme.colorScheme.primary,
    showIncome: Boolean = true
) {
    val maxValue = groups.maxOfOrNull { maxOf(it.expense, it.income) }?.takeIf { it > 0 } ?: 1.0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        groups.forEach { group ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.height(110.dp)
                ) {
                    val expenseFraction = (group.expense / maxValue).coerceIn(0.0, 1.0).toFloat()
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .fillMaxHeight(expenseFraction)
                            .background(expenseColor)
                    )
                    if (showIncome) {
                        val incomeFraction = (group.income / maxValue).coerceIn(0.0, 1.0).toFloat()
                        Box(
                            modifier = Modifier
                                .width(10.dp)
                                .fillMaxHeight(incomeFraction)
                                .background(incomeColor)
                        )
                    }
                }
                Text(group.label, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
