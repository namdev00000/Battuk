package com.battuk.app.ui.family

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.battuk.app.ui.components.AvatarPicker
import com.battuk.app.util.DateUtils
import com.battuk.app.viewmodel.FamilyViewModel
import com.battuk.app.viewmodel.RELATIONS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyMemberScreen(
    viewModel: FamilyViewModel,
    onSaved: () -> Unit
) {
    var relationExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Family Member", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        AvatarPicker(photoUri = viewModel.photoUri, onPhotoChanged = { viewModel.photoUri = it })

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("Name *") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(expanded = relationExpanded, onExpandedChange = { relationExpanded = it }) {
            OutlinedTextField(
                value = viewModel.relation,
                onValueChange = {},
                readOnly = true,
                label = { Text("Relation") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relationExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            DropdownMenu(expanded = relationExpanded, onDismissRequest = { relationExpanded = false }) {
                RELATIONS.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        viewModel.relation = option
                        relationExpanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
            Text(viewModel.dobMillis?.let { "DOB: ${DateUtils.formatDate(it)}" } ?: "Date of Birth (Optional)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Set as Earning Member", style = MaterialTheme.typography.titleMedium)
                Text("This member has an income", style = MaterialTheme.typography.bodyMedium)
            }
            Switch(checked = viewModel.isEarningMember, onCheckedChange = { viewModel.isEarningMember = it })
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.addMember(onDone = onSaved) },
            enabled = viewModel.name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("+ Add Family Member") }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = viewModel.dobMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dobMillis = state.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }
}
