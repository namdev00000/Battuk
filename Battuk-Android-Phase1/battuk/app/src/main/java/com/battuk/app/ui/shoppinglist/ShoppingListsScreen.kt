package com.battuk.app.ui.shoppinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.battuk.app.util.DateUtils
import com.battuk.app.viewmodel.ShoppingListViewModel
import androidx.compose.ui.unit.dp

@Composable
fun ShoppingListsScreen(
    viewModel: ShoppingListViewModel,
    onOpenList: (Long) -> Unit
) {
    val lists by viewModel.lists.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Remember the Things", style = MaterialTheme.typography.headlineSmall)
        Text("Plan before you shop", style = MaterialTheme.typography.bodyMedium)

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            OutlinedTextField(
                value = viewModel.newListName,
                onValueChange = { viewModel.newListName = it },
                label = { Text("New list name") },
                modifier = Modifier.weight(1f)
            )
        }
        Button(onClick = { viewModel.createList(onOpenList) }, modifier = Modifier.fillMaxWidth()) {
            Text("+ Create List")
        }

        LazyColumn(contentPadding = PaddingValues(vertical = 12.dp)) {
            items(lists, key = { it.id }) { list ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onOpenList(list.id) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(list.name, style = MaterialTheme.typography.titleMedium)
                        Text(DateUtils.formatDate(list.createdDateMillis), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
