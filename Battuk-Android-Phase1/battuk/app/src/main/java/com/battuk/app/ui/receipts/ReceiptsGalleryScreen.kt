package com.battuk.app.ui.receipts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.battuk.app.data.entity.Expense
import com.battuk.app.util.DateUtils
import com.battuk.app.viewmodel.ReportsViewModel

@Composable
fun ReceiptsGalleryScreen(viewModel: ReportsViewModel) {
    val allExpenses by viewModel.expensesInRange.collectAsState()
    val withReceipts = allExpenses.filter { it.receiptUri != null }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Receipts", style = MaterialTheme.typography.headlineSmall)

        if (withReceipts.isEmpty()) {
            Text(
                "No receipts saved yet. Attach a photo when adding an expense or a bulk-entry trip.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(top = 12.dp)) {
                items(withReceipts, key = { it.id }) { expense ->
                    ReceiptCard(expense)
                }
            }
        }
    }
}

@Composable
private fun ReceiptCard(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
        Column {
            AsyncImage(
                model = expense.receiptUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
            Text(DateUtils.formatDate(expense.dateMillis), modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.labelMedium)
            Text(expense.storeName ?: expense.itemName, modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
