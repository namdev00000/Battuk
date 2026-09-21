package com.battuk.app.ui.itemhistory

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.util.DateUtils
import com.battuk.app.viewmodel.ItemHistoryViewModel

@Composable
fun ItemPriceHistoryScreen(
    viewModel: ItemHistoryViewModel,
    itemName: String
) {
    val history by remember(itemName) { viewModel.priceHistory(itemName) }.collectAsState()
    val perUnitPrices = history.map { it.amount / it.quantity.coerceAtLeast(0.0001) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(itemName, style = MaterialTheme.typography.headlineSmall)

        if (history.size >= 2) {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                Canvas(modifier = Modifier.fillMaxWidth().height(160.dp).padding(12.dp)) {
                    val maxPrice = perUnitPrices.maxOrNull() ?: 1.0
                    val minPrice = perUnitPrices.minOrNull() ?: 0.0
                    val range = (maxPrice - minPrice).takeIf { it > 0 } ?: 1.0
                    val stepX = size.width / (perUnitPrices.size - 1).coerceAtLeast(1)
                    val points = perUnitPrices.mapIndexed { index, price ->
                        val x = index * stepX
                        val y = size.height - ((price - minPrice) / range * size.height).toFloat()
                        Offset(x, y)
                    }
                    for (i in 0 until points.size - 1) {
                        drawLine(
                            color = androidx.compose.ui.graphics.Color(0xFF2E7D32),
                            start = points[i],
                            end = points[i + 1],
                            strokeWidth = 6f
                        )
                    }
                    points.forEach { point ->
                        drawCircle(color = androidx.compose.ui.graphics.Color(0xFF2E7D32), radius = 8f, center = point)
                    }
                }
            }

            val first = perUnitPrices.first()
            val last = perUnitPrices.last()
            val changePercent = if (first != 0.0) ((last - first) / first * 100) else 0.0
            val direction = if (changePercent >= 0) "\u2191" else "\u2193"

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Current Price", style = MaterialTheme.typography.labelMedium)
                    Text(CurrencyUtils.format(last), style = MaterialTheme.typography.titleMedium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Last Purchase", style = MaterialTheme.typography.labelMedium)
                    Text(DateUtils.formatDate(history.last().dateMillis), style = MaterialTheme.typography.titleMedium)
                }
            }
            Text(
                "$direction ${String.format("%.1f", kotlin.math.abs(changePercent))}% since first recorded purchase",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text("Not enough purchases yet to show a trend.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 12.dp))
        }

        Text("Purchase History", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
            items(history.reversed()) { expense ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(DateUtils.formatDate(expense.dateMillis), modifier = Modifier.weight(1f))
                    Text("${expense.quantity} ${expense.unit}", modifier = Modifier.weight(1f))
                    Text(CurrencyUtils.format(expense.amount), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
