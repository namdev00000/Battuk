package com.battuk.app.ui.reports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.battuk.app.ui.components.BarGroup
import com.battuk.app.ui.components.BattukBarChart
import com.battuk.app.ui.components.BattukPieChart
import com.battuk.app.ui.components.ChartSlice
import com.battuk.app.ui.theme.ChartFood
import com.battuk.app.ui.theme.ChartKirana
import com.battuk.app.ui.theme.ChartMilk
import com.battuk.app.ui.theme.ChartOthers
import com.battuk.app.ui.theme.ChartTransport
import com.battuk.app.ui.theme.ChartVegetable
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.viewmodel.ReportPeriod
import com.battuk.app.viewmodel.ReportsViewModel
import androidx.compose.ui.unit.dp

private val paletteColors = listOf(ChartVegetable, ChartKirana, ChartMilk, ChartFood, ChartTransport, ChartOthers)

@Composable
fun ReportsScreen(viewModel: ReportsViewModel) {
    val totalExpense by viewModel.totalExpense.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val topItems by viewModel.topItems.collectAsState()
    val trend by viewModel.weeklyTrend.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Reports", style = MaterialTheme.typography.headlineSmall)

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            ReportPeriod.values().forEach { p ->
                FilterChip(
                    selected = viewModel.period == p,
                    onClick = { viewModel.setPeriod(p) },
                    label = { Text(p.label) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            SummaryCard("Expenses", CurrencyUtils.format(totalExpense), Modifier.weight(1f))
            SummaryCard("Income", CurrencyUtils.format(totalIncome), Modifier.weight(1f))
            SummaryCard("Savings", CurrencyUtils.format(totalIncome - totalExpense), Modifier.weight(1f))
        }

        LazyColumn(contentPadding = PaddingValues(vertical = 16.dp)) {
            item {
                Text("Expenses by Category", style = MaterialTheme.typography.titleMedium)
                if (categoryTotals.isNotEmpty()) {
                    val slices = categoryTotals.mapIndexed { index, ct ->
                        ChartSlice(viewModel.categoryName(ct.categoryId), ct.total, paletteColors[index % paletteColors.size])
                    }
                    BattukPieChart(slices = slices, centerLabel = CurrencyUtils.format(totalExpense), modifier = Modifier.padding(vertical = 12.dp))
                } else {
                    Text("No expenses recorded for this period.", style = MaterialTheme.typography.bodyMedium)
                }
            }
            item {
                Text("Spending Trend", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                BattukBarChart(
                    groups = trend.map { BarGroup(it.first, it.second) },
                    showIncome = false,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            item {
                Text("Top Items This Period", style = MaterialTheme.typography.titleMedium)
            }
            items(topItems) { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(item.itemName, modifier = Modifier.weight(1f))
                    Text(CurrencyUtils.format(item.total))
                }
            }
        }
    }
}

@Composable
fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.titleMedium)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
