package com.battuk.app.ui.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.battuk.app.data.entity.Item
import com.battuk.app.viewmodel.CategoryViewModel
import androidx.compose.ui.unit.dp

@Composable
fun ItemSearchScreen(
    viewModel: CategoryViewModel,
    categoryId: Long,
    onItemSelected: (Item) -> Unit,
    onCustomEntry: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val allItemsForCategory = viewModel.itemsFor(categoryId)
    val filtered = remember(query, allItemsForCategory) {
        if (query.isBlank()) allItemsForCategory
        else allItemsForCategory.filter {
            it.nameEnglish.contains(query, ignoreCase = true) || it.nameMarathi.contains(query)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select Item", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search vegetables / भाज्या...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        )

        LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
            items(filtered, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onItemSelected(item) }
                ) {
                    Text(
                        "${item.nameEnglish} (${item.nameMarathi})",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            if (query.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onCustomEntry(query) }
                    ) {
                        Text(
                            "Use \"$query\" as a custom item",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
