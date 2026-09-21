package com.battuk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.Expense
import com.battuk.app.data.entity.Profile
import com.battuk.app.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class HomeViewModel(private val repository: BattukRepository) : ViewModel() {

    val profile: StateFlow<Profile?> = repository.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val today = LocalDate.now()
    private val monthStart = DateUtils.toMillis(DateUtils.startOfMonth(today))
    private val monthEnd = DateUtils.endOfDayMillis(today)

    val monthExpense: StateFlow<Double> = repository.observeTotalBetween(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthIncome: StateFlow<Double> = repository.observeIncomeTotalBetween(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categoryTotals = repository.observeCategoryTotalsBetween(monthStart, monthEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = repository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentExpenses: StateFlow<List<Expense>> = repository.observeAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun categoryName(id: Long) = categories.value.firstOrNull { it.id == id }?.name ?: "Other"
}
