package com.battuk.app.ui.inflation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.viewmodel.InflationCalculatorViewModel
import com.battuk.app.viewmodel.ReportPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InflationCalculatorScreen(viewModel: InflationCalculatorViewModel) {
    val itemNames by viewModel.itemNames.collectAsState()
    var itemExpanded by remember { mutableStateOf(false) }
    var periodExpanded by remember { mutableStateOf(false) }

    val history by remember(viewModel.selectedItem) {
        viewModel.selectedItem?.let { viewModel.priceHistoryFlow(it) } ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Inflation Calculator", style = MaterialTheme.typography.headlineSmall)
        Text("Track price changes", style = MaterialTheme.typography.bodyMedium)

        ExposedDropdownMenuBox(expanded = itemExpanded, onExpandedChange = { itemExpanded = it }, modifier = Modifier.padding(top = 16.dp)) {
            OutlinedTextField(
                value = viewModel.selectedItem ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Product") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            DropdownMenu(expanded = itemExpanded, onDismissRequest = { itemExpanded = false }) {
                itemNames.forEach { name ->
                    DropdownMenuItem(text = { Text(name) }, onClick = {
                        viewModel.selectItem(name)
                        itemExpanded = false
                    })
                }
            }
        }

        ExposedDropdownMenuBox(expanded = periodExpanded, onExpandedChange = { periodExpanded = it }, modifier = Modifier.padding(top = 12.dp)) {
            OutlinedTextField(
                value = viewModel.period.label,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Time Period") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            DropdownMenu(expanded = periodExpanded, onDismissRequest = { periodExpanded = false }) {
                ReportPeriod.values().forEach { p ->
                    DropdownMenuItem(text = { Text(p.label) }, onClick = {
                        viewModel.setPeriod(p)
                        periodExpanded = false
                    })
                }
            }
        }

        Button(
            onClick = { viewModel.calculateFrom(history) },
            enabled = viewModel.selectedItem != null,
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = 16.dp)
        ) { Text("Calculate") }

        viewModel.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
        }

        viewModel.result?.let { result ->
            Card(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val direction = if (result.percentChange >= 0) "+" else ""
                    Text(
                        "$direction${String.format("%.1f", result.percentChange)}%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (result.percentChange >= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Price ${if (result.percentChange >= 0) "increased" else "decreased"} by ${CurrencyUtils.format(kotlin.math.abs(result.currentPrice - result.previousPrice))} over this period",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Previous Price", style = MaterialTheme.typography.labelMedium)
                            Text(CurrencyUtils.format(result.previousPrice), style = MaterialTheme.typography.titleMedium)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Current Price", style = MaterialTheme.typography.labelMedium)
                            Text(CurrencyUtils.format(result.currentPrice), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}
