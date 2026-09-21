package com.battuk.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.battuk.app.util.AppThemeMode
import com.battuk.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onManageFamily: () -> Unit,
    onManageCategories: () -> Unit,
    onManageYears: () -> Unit
) {
    val theme by viewModel.themeMode.collectAsState()
    val soundOn by viewModel.soundOn.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("App Settings", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(contentPadding = PaddingValues(vertical = 16.dp)) {
            item {
                SettingsRow(
                    title = "Theme",
                    subtitle = when (theme) {
                        AppThemeMode.SYSTEM -> "System (Follow device)"
                        AppThemeMode.LIGHT -> "Light"
                        AppThemeMode.DARK -> "Dark"
                    },
                    onClick = { viewModel.cycleTheme() }
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Sound Effects", style = MaterialTheme.typography.titleMedium)
                        Text(if (soundOn) "On" else "Off", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(checked = soundOn, onCheckedChange = { viewModel.setSound(it) })
                }
            }
            item {
                SettingsRow(title = "Currency", subtitle = "Indian Rupees (\u20B9) \u2014 fixed", onClick = null)
            }
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { onManageFamily() }) {
                    Icon(Icons.Filled.People, contentDescription = null)
                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                        Text("Family Members", style = MaterialTheme.typography.titleMedium)
                        Text("Manage your family", style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null)
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { onManageCategories() }) {
                    Icon(Icons.Filled.Category, contentDescription = null)
                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                        Text("Categories", style = MaterialTheme.typography.titleMedium)
                        Text("Add / edit categories", style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null)
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { onManageYears() }) {
                    Icon(Icons.Filled.CalendarViewMonth, contentDescription = null)
                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                        Text("Year Management", style = MaterialTheme.typography.titleMedium)
                        Text("Manage years and data", style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null)
                }
            }
            item {
                SettingsRow(title = "About Battuk", subtitle = "Version 1.0.0", onClick = null)
            }
        }
    }
}

@Composable
private fun SettingsRow(title: String, subtitle: String, onClick: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it }
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        if (onClick != null) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null)
        }
    }
}
