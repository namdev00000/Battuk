package com.battuk.app.ui.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.battuk.app.data.entity.CategoryType
import com.battuk.app.ui.components.ScreenWithFloatingCalculator
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.util.rememberImagePickers
import com.battuk.app.viewmodel.CalculatorViewModel
import com.battuk.app.viewmodel.CategoryViewModel
import com.battuk.app.viewmodel.DraftItem
import com.battuk.app.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkEntryScreen(
    expenseViewModel: ExpenseViewModel,
    categoryViewModel: CategoryViewModel,
    calculatorViewModel: CalculatorViewModel,
    prefillItems: List<Pair<String, String?>> = emptyList(),
    onSaved: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    val categories by categoryViewModel.categories.collectAsState()
    val marketCategory = categories.firstOrNull { it.type == CategoryType.WEEKLY_MARKET }

    // pre-fill from a converted shopping list, once
    remember(prefillItems) {
        if (expenseViewModel.draftItems.isEmpty() && prefillItems.isNotEmpty() && marketCategory != null) {
            prefillItems.forEach { (name, marathi) ->
                expenseViewModel.addDraftItem(
                    DraftItem(itemName = name, itemNameMarathi = marathi, categoryId = marketCategory.id, amount = 0.0, quantity = 1.0, unit = "kg")
                )
            }
        }
        true
    }

    val receiptPickers = rememberImagePickers(onImageSaved = { expenseViewModel.tripReceiptUri = it })

    ScreenWithFloatingCalculator(calculatorViewModel = calculatorViewModel) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Bulk Entry", style = MaterialTheme.typography.headlineSmall)
            StepIndicator(step)

            when (step) {
                1 -> {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        OutlinedTextField(
                            value = expenseViewModel.tripStoreName,
                            onValueChange = { expenseViewModel.tripStoreName = it },
                            label = { Text("Store") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Items (${expenseViewModel.draftItems.size})", style = MaterialTheme.typography.titleMedium)

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(expenseViewModel.draftItems, key = { it.id }) { draft ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("${draft.itemName}${draft.itemNameMarathi?.let { " ($it)" } ?: ""}")
                                        Text("${draft.quantity} ${draft.unit}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(CurrencyUtils.format(draft.amount))
                                        IconButton(onClick = { expenseViewModel.removeDraftItem(draft) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Remove")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OutlinedButton(onClick = { showAddItemDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("+ Add Item")
                    }

                    Text(
                        "Total Amount: ${CurrencyUtils.format(expenseViewModel.draftTotal())}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Button(
                        onClick = { step = 2 },
                        enabled = expenseViewModel.draftItems.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) { Text("Next: Attach Receipt \u2192") }
                }

                2 -> {
                    Column(modifier = Modifier.weight(1f)) {
                        expenseViewModel.tripReceiptUri?.let { uri ->
                            AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxWidth().height(220.dp))
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { receiptPickers.takePhoto() }, modifier = Modifier.weight(1f)) { Text("Retake") }
                            Button(onClick = { step = 3 }, modifier = Modifier.weight(1f)) { Text("Use Photo") }
                        }
                        TextButton(onClick = { receiptPickers.pickFromGallery() }) { Text("+ Add Another Photo (Optional)") }
                    }
                    OutlinedButton(onClick = { step = 1 }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
                }

                3 -> {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Review", style = MaterialTheme.typography.titleLarge)
                        LazyColumn {
                            items(expenseViewModel.draftItems, key = { it.id }) { draft ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${draft.itemName} \u00d7 ${draft.quantity}${draft.unit}")
                                    Text(CurrencyUtils.format(draft.amount))
                                }
                            }
                        }
                        Text(
                            "Total Amount: ${CurrencyUtils.format(expenseViewModel.draftTotal())}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                    Button(
                        onClick = { expenseViewModel.saveBulkEntry(onSaved) },
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) { Text("Save All Items") }
                }
            }
        }
    }

    if (showAddItemDialog && marketCategory != null) {
        AddDraftItemDialog(
            defaultCategoryId = marketCategory.id,
            categories = categories,
            onDismiss = { showAddItemDialog = false },
            onAdd = { draft ->
                expenseViewModel.addDraftItem(draft)
                showAddItemDialog = false
            }
        )
    }
}

@Composable
private fun StepIndicator(step: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf("1 Items", "2 Receipt", "3 Review").forEachIndexed { index, label ->
            Text(
                label,
                style = if (index + 1 == step) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                color = if (index + 1 == step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDraftItemDialog(
    defaultCategoryId: Long,
    categories: List<com.battuk.app.data.entity.Category>,
    onDismiss: () -> Unit,
    onAdd: (DraftItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("kg") }
    var categoryId by remember { mutableStateOf(defaultCategoryId) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.size(8.dp))
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                    OutlinedTextField(
                        value = categories.firstOrNull { it.id == categoryId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(category.name) }, onClick = {
                                categoryId = category.id
                                categoryExpanded = false
                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row {
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Qty") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.size(8.dp))
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (\u20B9)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = price.toDoubleOrNull()
                val qty = quantity.toDoubleOrNull() ?: 1.0
                if (name.isNotBlank() && amount != null) {
                    onAdd(DraftItem(itemName = name.trim(), categoryId = categoryId, amount = amount, quantity = qty, unit = unit))
                }
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
