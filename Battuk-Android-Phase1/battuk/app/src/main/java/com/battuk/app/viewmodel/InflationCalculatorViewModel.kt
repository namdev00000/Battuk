package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class InflationResult(
    val previousPrice: Double,
    val currentPrice: Double,
    val percentChange: Double
)

class InflationCalculatorViewModel(private val repository: BattukRepository) : ViewModel() {

    val itemNames: StateFlow<List<String>> = repository.observeDistinctItemNames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var selectedItem by mutableStateOf<String?>(null)
    var period by mutableStateOf(ReportPeriod.MONTH)
    var result by mutableStateOf<InflationResult?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun selectItem(itemName: String) {
        selectedItem = itemName
        errorMessage = null
        result = null
    }

    fun setPeriod(newPeriod: ReportPeriod) {
        period = newPeriod
        result = null
    }

    fun priceHistoryFlow(itemName: String) = repository.observePriceHistory(itemName)

    fun calculateFrom(history: List<com.battuk.app.data.entity.Expense>) {
        if (history.size < 2) {
            errorMessage = "Not enough purchase history for this product yet."
            result = null
            return
        }
        val range = period.rangeEnding()
        val inRange = history.filter { it.dateMillis in range.start..range.end }
        val relevant = inRange.ifEmpty { history }
        if (relevant.size < 2) {
            errorMessage = "Not enough purchases in this period."
            result = null
            return
        }
        val previous = relevant.first().amount / relevant.first().quantity.coerceAtLeast(0.0001)
        val current = relevant.last().amount / relevant.last().quantity.coerceAtLeast(0.0001)
        val change = if (previous != 0.0) ((current - previous) / previous) * 100 else 0.0
        result = InflationResult(previous, current, change)
        errorMessage = null
    }
}
