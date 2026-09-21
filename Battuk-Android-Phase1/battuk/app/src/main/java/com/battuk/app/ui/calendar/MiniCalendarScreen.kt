package com.battuk.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.battuk.app.util.CurrencyUtils
import com.battuk.app.util.DateUtils
import com.battuk.app.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun MiniCalendarScreen(viewModel: CalendarViewModel) {
    val month = viewModel.visibleMonth
    val monthExpenses by remember(month) { viewModel.expensesForMonth(month) }.collectAsState(initial = emptyList())
    val expenseDays = remember(monthExpenses) { monthExpenses.map { DateUtils.toLocalDate(it.dateMillis).dayOfMonth }.toSet() }

    val selectedDate = viewModel.selectedDateMillis?.let { DateUtils.toLocalDate(it) }
    val dayExpenses by remember(viewModel.selectedDateMillis) {
        viewModel.selectedDateMillis?.let { viewModel.expensesForDay(it) } ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.visibleMonth = month.minusMonths(1) }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
            }
            Text(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")), style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { viewModel.visibleMonth = month.plusMonths(1) }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(it, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelMedium)
            }
        }

        val firstDayOfMonth = month.atDay(1)
        val leadingBlanks = firstDayOfMonth.dayOfWeek.value % 7 // Sunday-first grid
        val totalDays = month.lengthOfMonth()
        val cells = (0 until leadingBlanks).map { null } + (1..totalDays).map { it }

        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.padding(top = 8.dp)) {
            items(cells) { day ->
                if (day == null) {
                    Box(modifier = Modifier.aspectRatio(1f))
                } else {
                    val date = month.atDay(day)
                    val isSelected = selectedDate == date
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { viewModel.selectedDateMillis = DateUtils.toMillis(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                day.toString(),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            if (day in expenseDays) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.secondary)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedDate != null) {
            Text(
                "Expenses on ${DateUtils.formatDate(DateUtils.toMillis(selectedDate))}: ${CurrencyUtils.format(dayExpenses.sumOf { it.amount })}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
                items(dayExpenses) { expense ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(expense.itemName)
                        Text(CurrencyUtils.format(expense.amount))
                    }
                }
            }
        }
    }
}
