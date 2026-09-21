package com.battuk.app.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.util.CsvExporter
import com.battuk.app.util.DateUtils
import com.battuk.app.util.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Year

class YearManagementViewModel(
    private val repository: BattukRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val activeYear: StateFlow<Int> = preferencesManager.activeYear
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Year.now().value)

    fun availableYears(): List<Int> {
        val current = Year.now().value
        return (current downTo current - 4).toList()
    }

    fun setActiveYear(year: Int) {
        viewModelScope.launch { preferencesManager.setActiveYear(year) }
    }

    fun exportYear(context: Context, year: Int, onReady: (Uri) -> Unit) {
        viewModelScope.launch {
            val start = DateUtils.toMillis(java.time.LocalDate.of(year, 1, 1))
            val end = DateUtils.endOfDayMillis(java.time.LocalDate.of(year, 12, 31))
            val expenses = repository.observeExpensesBetween(start, end).first()
            val uri = CsvExporter.exportExpenses(context, year, expenses)
            onReady(uri)
        }
    }
}
