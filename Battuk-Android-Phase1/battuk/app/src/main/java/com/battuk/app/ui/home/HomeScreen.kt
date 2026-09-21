package com.battuk.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.util.DateUtils
import com.battuk.app.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onShoppingList: () -> Unit,
    onViewReports: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val monthExpense by viewModel.monthExpense.collectAsState()
    val monthIncome by viewModel.monthIncome.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val recentExpenses by viewModel.recentExpenses.collectAsState()

    var now by remember { mutableStateOf(java.time.LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = java.time.LocalDateTime.now()
            delay(30_000)
        }
    }

    val greeting = when (now.hour) {
        in 0..11 -> "Good Morning!"
        in 12..16 -> "Good Afternoon!"
        else -> "Good Evening!"
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Battuk", style = MaterialTheme.typography.headlineSmall)
        Text(now.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy")), style = MaterialTheme.typography.bodyMedium)
        Text(now.format(DateTimeFormatter.ofPattern("h:mm a")), style = MaterialTheme.typography.headlineMedium)

        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(greeting, style = MaterialTheme.typography.titleMedium)
                Text("Small steps make a bigger tomorrow", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickAction("Add Expense", Icons.Filled.Add, onAddExpense, Modifier.weight(1f))
            QuickAction("Add Income", Icons.Filled.AttachMoney, onAddIncome, Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickAction("Shopping List", Icons.Filled.Checklist, onShoppingList, Modifier.weight(1f))
            QuickAction("View Reports", Icons.Filled.QueryStats, onViewReports, Modifier.weight(1f))
        }

        Text("This Month", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SnapshotCard("Expenses", CurrencyUtils.format(monthExpense), Modifier.weight(1f))
            SnapshotCard("Income", CurrencyUtils.format(monthIncome), Modifier.weight(1f))
            SnapshotCard("Savings", CurrencyUtils.format(monthIncome - monthExpense), Modifier.weight(1f))
        }

        Text("Top Categories", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        val total = categoryTotals.sumOf { it.total }.takeIf { it > 0 } ?: 1.0
        LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
            items(categoryTotals.take(4)) { ct ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(viewModel.categoryName(ct.categoryId), modifier = Modifier.weight(1f))
                    Text("${((ct.total / total) * 100).toInt()}% \u2022 ${CurrencyUtils.format(ct.total)}")
                }
            }
        }
    }
}

@Composable
private fun QuickAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(64.dp)) {
        Column {
            Icon(icon, contentDescription = null)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SnapshotCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.titleMedium)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
