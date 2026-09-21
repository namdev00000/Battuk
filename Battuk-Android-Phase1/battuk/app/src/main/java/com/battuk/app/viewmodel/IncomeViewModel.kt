package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.FamilyMember
import com.battuk.app.data.entity.Income
import com.battuk.app.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IncomeViewModel(private val repository: BattukRepository) : ViewModel() {

    val familyMembers: StateFlow<List<FamilyMember>> = repository.observeFamilyMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentIncome: StateFlow<List<Income>> = repository.observeAllIncome()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var familyMemberId by mutableStateOf<Long?>(null)
    var source by mutableStateOf("Salary")
    var amount by mutableStateOf("")
    var dateMillis by mutableStateOf(DateUtils.nowMillis())
    var note by mutableStateOf("")

    fun save(onDone: () -> Unit) {
        val memberId = familyMemberId ?: return
        val amountValue = amount.toDoubleOrNull() ?: return
        viewModelScope.launch {
            repository.addIncome(
                Income(
                    familyMemberId = memberId,
                    amount = amountValue,
                    source = source.trim().ifBlank { "Other" },
                    dateMillis = dateMillis,
                    note = note.ifBlank { null }
                )
            )
            amount = ""
            note = ""
            onDone()
        }
    }
}
