package com.battuk.app.ui.expense

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.battuk.app.ui.components.AvatarPicker
import com.battuk.app.ui.components.ScreenWithFloatingCalculator
import com.battuk.app.util.DateUtils
import com.battuk.app.util.rememberImagePickers
import com.battuk.app.viewmodel.CalculatorViewModel
import com.battuk.app.viewmodel.CategoryViewModel
import com.battuk.app.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleEntryScreen(
    expenseViewModel: ExpenseViewModel,
    categoryViewModel: CategoryViewModel,
    calculatorViewModel: CalculatorViewModel,
    onPickItem: () -> Unit,
    onSaved: () -> Unit
) {
    val categories by categoryViewModel.categories.collectAsState()
    val familyMembers by expenseViewModel.familyMembers.collectAsState()

    var categoryExpanded by remember { mutableStateOf(false) }
    var memberExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val receiptPickers = rememberImagePickers(onImageSaved = { expenseViewModel.singleReceiptUri = it })

    ScreenWithFloatingCalculator(
        calculatorViewModel = calculatorViewModel,
        onPasteToExpense = { value -> expenseViewModel.singlePrice = value.toString() }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Add Expense", style = MaterialTheme.typography.headlineSmall)

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                SegmentedButton(selected = true, onClick = {}, shape = MaterialTheme.shapes.small) { Text("Single Entry") }
            }

            LazyColumn {
                item {
                    OutlinedTextField(
                        value = expenseViewModel.singleItemName,
                        onValueChange = { expenseViewModel.singleItemName = it },
                        label = { Text("Item Name") },
                        trailingIcon = {
                            TextButton(onClick = onPickItem) { Text("Pick") }
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    )

                    ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                        OutlinedTextField(
                            value = categories.firstOrNull { it.id == expenseViewModel.singleCategoryId }?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor().padding(vertical = 6.dp)
                        )
                        DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                            categories.forEach { category ->
                                DropdownMenuItem(text = { Text(category.name) }, onClick = {
                                    expenseViewModel.singleCategoryId = category.id
                                    categoryExpanded = false
                                })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = expenseViewModel.singlePrice,
                        onValueChange = { expenseViewModel.singlePrice = it },
                        label = { Text("Price (\u20B9)") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        OutlinedTextField(
                            value = expenseViewModel.singleQuantity,
                            onValueChange = { expenseViewModel.singleQuantity = it },
                            label = { Text("Quantity") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        OutlinedTextField(
                            value = expenseViewModel.singleUnit,
                            onValueChange = { expenseViewModel.singleUnit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val amount = (expenseViewModel.singlePrice.toDoubleOrNull() ?: 0.0)
                    Text(
                        "Total Amount: \u20B9${amount}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Text("Date: ${DateUtils.formatDate(expenseViewModel.singleDateMillis)}")
                    }

                    ExposedDropdownMenuBox(expanded = memberExpanded, onExpandedChange = { memberExpanded = it }) {
                        OutlinedTextField(
                            value = familyMembers.firstOrNull { it.id == expenseViewModel.singleFamilyMemberId }?.name ?: "Me",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Family Member") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor().padding(vertical = 6.dp)
                        )
                        DropdownMenu(expanded = memberExpanded, onDismissRequest = { memberExpanded = false }) {
                            familyMembers.forEach { member ->
                                DropdownMenuItem(text = { Text(member.name) }, onClick = {
                                    expenseViewModel.singleFamilyMemberId = member.id
                                    memberExpanded = false
                                })
                            }
                        }
                    }

                    Text("Receipt Photo (Optional)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
                    Row(modifier = Modifier.padding(vertical = 6.dp)) {
                        expenseViewModel.singleReceiptUri?.let { uri ->
                            AsyncImage(model = uri, contentDescription = null, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                        }
                        OutlinedButton(onClick = { receiptPickers.takePhoto() }) { Text("+ Add Photo") }
                    }

                    Spacer(modifier = Modifier.size(24.dp))
                    Button(
                        onClick = { expenseViewModel.saveSingleEntry(onSaved) },
                        enabled = expenseViewModel.singleItemName.isNotBlank() &&
                            expenseViewModel.singleCategoryId != null &&
                            expenseViewModel.singlePrice.toDoubleOrNull() != null,
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) { Text("Save Expense") }
                    Spacer(modifier = Modifier.size(80.dp))
                }
            }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = expenseViewModel.singleDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { expenseViewModel.singleDateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }
}
