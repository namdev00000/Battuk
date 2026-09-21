package com.battuk.app.ui.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.viewmodel.ReportsViewModel

@Composable
fun CategoryDetailsScreen(viewModel: ReportsViewModel, categoryId: Long) {
    val expenses by viewModel.expensesInRange.collectAsState()
    val forCategory = expenses.filter { it.categoryId == categoryId }
    val total = forCategory.sumOf { it.amount }
    val byItem = forCategory.groupBy { it.itemName }
        .map { (name, list) -> name to list.sumOf { it.amount } }
        .sortedByDescending { it.second }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(viewModel.categoryName(categoryId), style = MaterialTheme.typography.headlineSmall)
        Text("Total Spent: ${CurrencyUtils.format(total)}", style = MaterialTheme.typography.titleMedium)

        Text("Items", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
        LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
            items(byItem) { (name, sum) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(name, modifier = Modifier.weight(1f))
                    Text(CurrencyUtils.format(sum))
                }
            }
        }
    }
}
