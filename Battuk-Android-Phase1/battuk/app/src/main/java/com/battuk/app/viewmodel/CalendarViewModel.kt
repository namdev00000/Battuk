package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.Expense
import com.battuk.app.util.DateUtils
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

class CalendarViewModel(private val repository: BattukRepository) : ViewModel() {

    var visibleMonth by mutableStateOf(YearMonth.now())
    var selectedDateMillis by mutableStateOf<Long?>(null)

    fun expensesForMonth(month: YearMonth): Flow<List<Expense>> =
        repository.observeExpensesBetween(
            DateUtils.toMillis(month.atDay(1)),
            DateUtils.endOfDayMillis(month.atEndOfMonth())
        )

    fun expensesForDay(dateMillis: Long): Flow<List<Expense>> {
        val date = DateUtils.toLocalDate(dateMillis)
        return repository.observeExpensesBetween(DateUtils.toMillis(date), DateUtils.endOfDayMillis(date))
    }
}
