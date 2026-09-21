package com.battuk.app.ui.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.battuk.app.util.ClipboardUtils
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.util.DateUtils
import com.battuk.app.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorSheet(
    viewModel: CalculatorViewModel,
    onDismiss: () -> Unit,
    onPasteToExpense: ((Double) -> Unit)? = null
) {
    val context = LocalContext.current
    var tab by remember { mutableIntStateOf(0) }
    val history by viewModel.history.collectAsState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Calculator") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("History") })
            }

            Column(modifier = Modifier.padding(top = 16.dp)) {
                if (tab == 0) {
                    Text(
                        text = viewModel.expression.ifBlank { "0" },
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = viewModel.lastResult?.let { CurrencyUtils.format(it).removePrefix("\u20B9") } ?: "",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            viewModel.lastResult?.let {
                                ClipboardUtils.copy(context, "battuk_calc", it.toString())
                                viewModel.noticeCopied("Result copied: $it")
                            }
                        }, modifier = Modifier.weight(1f)) { Text("Copy Result") }

                        if (onPasteToExpense != null) {
                            Button(onClick = {
                                viewModel.lastResult?.let { onPasteToExpense(it) }
                            }, modifier = Modifier.weight(1f)) { Text("Paste to Expense") }
                        }
                    }

                    viewModel.lastCopiedNotice?.let {
                        Text(it, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 4.dp))
                    }

                    CalculatorKeypad(viewModel)
                } else {
                    if (history.isEmpty()) {
                        Text("No calculations yet.", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                            items(history) { entry ->
                                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("${entry.expression} = ${entry.result}")
                                            Text(
                                                DateUtils.formatDate(entry.timestampMillis, "d MMM, h:mm a"),
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                        IconButton(onClick = {
                                            ClipboardUtils.copy(context, "battuk_calc", entry.result.toString())
                                            viewModel.noticeCopied("Result copied: ${entry.result}")
                                        }) {
                                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorKeypad(viewModel: CalculatorViewModel) {
    val rows = listOf(
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "-"),
        listOf("C", "0", "=", "+")
    )
    Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    val isOperator = key in listOf("÷", "×", "-", "+", "=")
                    Button(
                        onClick = {
                            when (key) {
                                "C" -> viewModel.onClear()
                                "=" -> viewModel.onEquals()
                                else -> if (isOperator) viewModel.onOperator(key) else viewModel.onDigit(key)
                            }
                        },
                        colors = if (isOperator) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors(),
                        modifier = Modifier.aspectRatio(1.4f)
                    ) { Text(key) }
                }
            }
        }
    }
}
