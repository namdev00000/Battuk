package com.battuk.app.ui.dashboard

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
import com.battuk.app.ui.components.ChartSlice
import com.battuk.app.ui.components.BattukPieChart
import com.battuk.app.ui.theme.ChartFood
import com.battuk.app.ui.theme.ChartKirana
import com.battuk.app.ui.theme.ChartMilk
import com.battuk.app.ui.theme.ChartOthers
import com.battuk.app.ui.theme.ChartTransport
import com.battuk.app.ui.theme.ChartVegetable
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.viewmodel.ReportPeriod
import com.battuk.app.viewmodel.ReportsViewModel

private val palette = listOf(ChartVegetable, ChartKirana, ChartMilk, ChartFood, ChartTransport, ChartOthers)

@Composable
fun VisualDashboardScreen(
    viewModel: ReportsViewModel,
    onItemClick: (String) -> Unit
) {
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val familyTotals by viewModel.familyMemberTotals.collectAsState()
    val topItems by viewModel.topItems.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Visual Dashboard", style = MaterialTheme.typography.headlineSmall)

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

        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            item {
                Text("Top Categories", style = MaterialTheme.typography.titleMedium)
                if (categoryTotals.isNotEmpty()) {
                    val slices = categoryTotals.mapIndexed { i, ct ->
                        ChartSlice(viewModel.categoryName(ct.categoryId), ct.total, palette[i % palette.size])
                    }
                    BattukPieChart(slices = slices, centerLabel = CurrencyUtils.format(totalExpense), modifier = Modifier.padding(vertical = 12.dp))
                } else {
                    Text("No data yet.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            item {
                Text("Spend Per Family Member", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            }
            items(familyTotals) { ft ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(viewModel.familyMemberName(ft.familyMemberId), modifier = Modifier.weight(1f))
                        Text(CurrencyUtils.format(ft.total))
                    }
                }
            }

            item {
                Text("Top Items (tap for price trend)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            }
            items(topItems) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onClick = { onItemClick(item.itemName) }
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Text(item.itemName, modifier = Modifier.weight(1f))
                        Text(CurrencyUtils.format(item.total))
                    }
                }
            }
        }
    }
}
