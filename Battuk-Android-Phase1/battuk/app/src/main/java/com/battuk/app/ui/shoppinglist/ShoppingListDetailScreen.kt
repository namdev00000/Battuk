package com.battuk.app.ui.shoppinglist

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.battuk.app.data.entity.ShoppingListItem
import com.battuk.app.viewmodel.ShoppingListViewModel

@Composable
fun ShoppingListDetailScreen(
    viewModel: ShoppingListViewModel,
    listId: Long,
    onConvertToBulkEntry: (List<Pair<String, String?>>) -> Unit
) {
    val items by viewModel.itemsFor(listId).collectAsState()
    var newItemName by remember { mutableStateOf("") }

    val checkedCount = items.count { it.checked }
    val progress = if (items.isEmpty()) 0f else checkedCount / items.size.toFloat()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("This Week's Market", style = MaterialTheme.typography.headlineSmall)
        Text("$checkedCount of ${items.size} items", style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            OutlinedTextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                label = { Text("Add item (e.g. Tomato / टोमॅटो)") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                viewModel.addItem(listId, newItemName, null, null)
                newItemName = ""
            }) { Icon(Icons.Filled.Add, contentDescription = "Add") }
        }

        LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 4.dp)) {
            items(items, key = { it.id }) { item ->
                ShoppingItemRow(item, onToggle = { viewModel.toggleChecked(item) }, onDelete = { viewModel.deleteItem(item) })
            }
        }

        Button(
            onClick = {
                val checked = items.filter { it.checked }.map { it.itemName to it.itemNameMarathi }
                onConvertToBulkEntry(checked.ifEmpty { items.map { it.itemName to it.itemNameMarathi } })
            },
            enabled = items.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Convert to Bulk Entry") }
    }
}

@Composable
private fun ShoppingItemRow(item: ShoppingListItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = item.checked, onCheckedChange = { onToggle() })
        Text(
            "${item.itemName}${item.quantityLabel?.let { " ($it)" } ?: ""}",
            style = if (item.checked) {
                MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough)
            } else MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Remove") }
    }
}
