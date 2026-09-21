package com.battuk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.Expense
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ItemHistoryViewModel(private val repository: BattukRepository) : ViewModel() {
    fun priceHistory(itemName: String): StateFlow<List<Expense>> =
        repository.observePriceHistory(itemName)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
