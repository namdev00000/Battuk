package com.battuk.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class BottomTab(val label: String) {
    HOME("Home"),
    EXPENSES("Expenses"),
    REPORTS("Reports"),
    CALENDAR("Calendar"),
    MORE("More")
}

@Composable
fun BattukBottomBar(
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selected == BottomTab.HOME,
            onClick = { onSelect(BottomTab.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text(BottomTab.HOME.label) }
        )
        NavigationBarItem(
            selected = selected == BottomTab.EXPENSES,
            onClick = { onSelect(BottomTab.EXPENSES) },
            icon = { Icon(Icons.Filled.List, contentDescription = null) },
            label = { Text(BottomTab.EXPENSES.label) }
        )
        NavigationBarItem(
            selected = selected == BottomTab.REPORTS,
            onClick = { onSelect(BottomTab.REPORTS) },
            icon = { Icon(Icons.Filled.PieChart, contentDescription = null) },
            label = { Text(BottomTab.REPORTS.label) }
        )
        NavigationBarItem(
            selected = selected == BottomTab.CALENDAR,
            onClick = { onSelect(BottomTab.CALENDAR) },
            icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
            label = { Text(BottomTab.CALENDAR.label) }
        )
        NavigationBarItem(
            selected = selected == BottomTab.MORE,
            onClick = { onSelect(BottomTab.MORE) },
            icon = { Icon(Icons.Filled.Menu, contentDescription = null) },
            label = { Text(BottomTab.MORE.label) }
        )
    }
}
