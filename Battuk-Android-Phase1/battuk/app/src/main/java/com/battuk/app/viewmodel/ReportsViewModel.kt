@file:OptIn(ExperimentalCoroutinesApi::class)

package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.dao.CategoryTotal
import com.battuk.app.data.dao.FamilyMemberTotal
import com.battuk.app.data.dao.ItemTotal
import com.battuk.app.data.entity.Category
import com.battuk.app.data.entity.Expense
import com.battuk.app.data.entity.FamilyMember
import com.battuk.app.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate


enum class ReportPeriod(val label: String) {
    DAY("Day"), WEEK("Week"), MONTH("Month"), SIX_MONTH("6 Months"), YEAR("Year")
}

data class ReportRange(val start: Long, val end: Long)

fun ReportPeriod.rangeEnding(referenceDate: LocalDate = LocalDate.now()): ReportRange {
    val start = when (this) {
        ReportPeriod.DAY -> referenceDate
        ReportPeriod.WEEK -> DateUtils.startOfWeek(referenceDate)
        ReportPeriod.MONTH -> DateUtils.startOfMonth(referenceDate)
        ReportPeriod.SIX_MONTH -> referenceDate.minusMonths(6).withDayOfMonth(1)
        ReportPeriod.YEAR -> DateUtils.startOfYear(referenceDate)
    }
    return ReportRange(DateUtils.toMillis(start), DateUtils.endOfDayMillis(referenceDate))
}

class ReportsViewModel(private val repository: BattukRepository) : ViewModel() {

    private val periodState = mutableStateOf(ReportPeriod.MONTH)
    val period: ReportPeriod
        get() = periodState.value

    private val rangeFlow = kotlinx.coroutines.flow.MutableStateFlow(periodState.value.rangeEnding())

    fun setPeriod(newPeriod: ReportPeriod) {
        periodState.value = newPeriod
        rangeFlow.value = newPeriod.rangeEnding()
    }

    val categories: StateFlow<List<Category>> = repository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val familyMembers: StateFlow<List<FamilyMember>> = repository.observeFamilyMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpense: StateFlow<Double> = rangeFlow.flatMapLatest { range ->
        repository.observeTotalBetween(range.start, range.end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncome: StateFlow<Double> = rangeFlow.flatMapLatest { range ->
        repository.observeIncomeTotalBetween(range.start, range.end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val categoryTotals: StateFlow<List<CategoryTotal>> = rangeFlow.flatMapLatest { range ->
        repository.observeCategoryTotalsBetween(range.start, range.end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val familyMemberTotals: StateFlow<List<FamilyMemberTotal>> = rangeFlow.flatMapLatest { range ->
        repository.observeFamilyMemberTotalsBetween(range.start, range.end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topItems: StateFlow<List<ItemTotal>> = rangeFlow.flatMapLatest { range ->
        repository.observeTopItemsBetween(range.start, range.end, 5)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expensesInRange: StateFlow<List<Expense>> = rangeFlow.flatMapLatest { range ->
        repository.observeExpensesBetween(range.start, range.end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Weekly buckets (last 4 weeks) for the spend-trend bar chart on Reports/Dashboard. */
    val weeklyTrend: StateFlow<List<Pair<String, Double>>> = expensesInRange.map { list ->
        val weeks = 4
        val today = LocalDate.now()
        (weeks - 1 downTo 0).map { weekOffset ->
            val weekStart = DateUtils.startOfWeek(today.minusWeeks(weekOffset.toLong()))
            val weekEnd = weekStart.plusDays(6)
            val startMillis = DateUtils.toMillis(weekStart)
            val endMillis = DateUtils.endOfDayMillis(weekEnd)
            val sum = list.filter { it.dateMillis in startMillis..endMillis }.sumOf { it.amount }
            "Wk ${weeks - weekOffset}" to sum
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun categoryName(categoryId: Long): String = categories.value.firstOrNull { it.id == categoryId }?.name ?: "Other"
    fun familyMemberName(id: Long?): String =
        if (id == null) "Unassigned" else familyMembers.value.firstOrNull { it.id == id }?.name ?: "Unknown"
}
