package com.battuk.app.ui.income

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
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.util.DateUtils
import com.battuk.app.viewmodel.IncomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeEntryScreen(viewModel: IncomeViewModel, onSaved: () -> Unit) {
    val familyMembers by viewModel.familyMembers.collectAsState()
    val recentIncome by viewModel.recentIncome.collectAsState()
    var memberExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Income Entry", style = MaterialTheme.typography.headlineSmall)

        ExposedDropdownMenuBox(expanded = memberExpanded, onExpandedChange = { memberExpanded = it }, modifier = Modifier.padding(top = 16.dp)) {
            OutlinedTextField(
                value = familyMembers.firstOrNull { it.id == viewModel.familyMemberId }?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Family Member") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            DropdownMenu(expanded = memberExpanded, onDismissRequest = { memberExpanded = false }) {
                familyMembers.forEach { member ->
                    DropdownMenuItem(text = { Text(member.name) }, onClick = {
                        viewModel.familyMemberId = member.id
                        memberExpanded = false
                    })
                }
            }
        }

        OutlinedTextField(
            value = viewModel.source,
            onValueChange = { viewModel.source = it },
            label = { Text("Source (e.g. Salary, Business)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        OutlinedTextField(
            value = viewModel.amount,
            onValueChange = { viewModel.amount = it },
            label = { Text("Amount (\u20B9)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text("Date: ${DateUtils.formatDate(viewModel.dateMillis)}")
        }

        OutlinedTextField(
            value = viewModel.note,
            onValueChange = { viewModel.note = it },
            label = { Text("Note (Optional)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        Button(
            onClick = { viewModel.save(onSaved) },
            enabled = viewModel.familyMemberId != null && viewModel.amount.toDoubleOrNull() != null,
            modifier = Modifier.fillMaxWidth().height(52.dp).padding(top = 20.dp)
        ) { Text("Save Income") }

        Text("Recent Income", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
            items(recentIncome) { income ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(income.source, modifier = Modifier.weight(1f))
                    Text(CurrencyUtils.format(income.amount))
                }
            }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = viewModel.dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { viewModel.dateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }
}
