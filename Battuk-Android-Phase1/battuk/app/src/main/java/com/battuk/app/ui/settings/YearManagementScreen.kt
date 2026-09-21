package com.battuk.app.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.battuk.app.viewmodel.YearManagementViewModel
import androidx.compose.ui.unit.dp

@Composable
fun YearManagementScreen(viewModel: YearManagementViewModel) {
    val context = LocalContext.current
    val activeYear by viewModel.activeYear.collectAsState()
    val years = viewModel.availableYears()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Year Management", style = MaterialTheme.typography.headlineSmall)
        Text("View, export, or set the active year for your records", style = MaterialTheme.typography.bodyMedium)

        LazyColumn(contentPadding = PaddingValues(vertical = 16.dp)) {
            items(years) { year ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            year.toString(),
                            style = if (year == activeYear) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                            color = if (year == activeYear) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Row {
                            TextButton(onClick = {
                                viewModel.exportYear(context, year) { uri ->
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/csv"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Export $year data"))
                                }
                            }) { Text("Export") }
                            if (year != activeYear) {
                                TextButton(onClick = { viewModel.setActiveYear(year) }) { Text("Set Active") }
                            } else {
                                Text("Active", modifier = Modifier.padding(horizontal = 12.dp), style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
