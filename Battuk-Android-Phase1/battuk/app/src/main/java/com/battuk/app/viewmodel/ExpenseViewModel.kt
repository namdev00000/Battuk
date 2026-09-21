package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.Expense
import com.battuk.app.data.entity.FamilyMember
import com.battuk.app.data.entity.Trip
import com.battuk.app.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DraftItem(
    val id: Long = System.nanoTime(),
    var itemName: String,
    var itemNameMarathi: String? = null,
    var categoryId: Long,
    var amount: Double,
    var quantity: Double,
    var unit: String
)

class ExpenseViewModel(private val repository: BattukRepository) : ViewModel() {

    val familyMembers: StateFlow<List<FamilyMember>> = repository.observeFamilyMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ---------- Single Entry form state ----------
    var singleItemName by mutableStateOf("")
    var singleItemNameMarathi by mutableStateOf<String?>(null)
    var singleCategoryId by mutableStateOf<Long?>(null)
    var singlePrice by mutableStateOf("")
    var singleQuantity by mutableStateOf("1")
    var singleUnit by mutableStateOf("kg")
    var singleDateMillis by mutableStateOf(DateUtils.nowMillis())
    var singleFamilyMemberId by mutableStateOf<Long?>(null)
    var singleReceiptUri by mutableStateOf<String?>(null)

    fun resetSingleEntry() {
        singleItemName = ""
        singleItemNameMarathi = null
        singlePrice = ""
        singleQuantity = "1"
        singleReceiptUri = null
        singleDateMillis = DateUtils.nowMillis()
    }

    fun saveSingleEntry(onDone: () -> Unit) {
        val categoryId = singleCategoryId ?: return
        val amount = singlePrice.toDoubleOrNull() ?: return
        val quantity = singleQuantity.toDoubleOrNull() ?: 1.0
        viewModelScope.launch {
            repository.addExpense(
                Expense(
                    itemName = singleItemName.trim(),
                    itemNameMarathi = singleItemNameMarathi,
                    categoryId = categoryId,
                    amount = amount,
                    quantity = quantity,
                    unit = singleUnit,
                    dateMillis = singleDateMillis,
                    familyMemberId = singleFamilyMemberId,
                    receiptUri = singleReceiptUri
                )
            )
            resetSingleEntry()
            onDone()
        }
    }

    // ---------- Bulk Entry (Trip) state ----------
    var tripStoreName by mutableStateOf("")
    var tripDateMillis by mutableStateOf(DateUtils.nowMillis())
    var tripReceiptUri by mutableStateOf<String?>(null)
    val draftItems = mutableStateListOf<DraftItem>()

    fun addDraftItem(item: DraftItem) {
        draftItems.add(item)
    }

    fun removeDraftItem(item: DraftItem) {
        draftItems.remove(item)
    }

    fun draftTotal(): Double = draftItems.sumOf { it.amount }

    fun resetBulkEntry() {
        tripStoreName = ""
        tripReceiptUri = null
        draftItems.clear()
        tripDateMillis = DateUtils.nowMillis()
    }

    fun saveBulkEntry(onDone: () -> Unit) {
        if (draftItems.isEmpty()) return
        viewModelScope.launch {
            val tripId = repository.addTrip(
                Trip(dateMillis = tripDateMillis, storeName = tripStoreName.ifBlank { "Market" }, receiptUri = tripReceiptUri)
            )
            val expenses = draftItems.map { draft ->
                Expense(
                    itemName = draft.itemName,
                    itemNameMarathi = draft.itemNameMarathi,
                    categoryId = draft.categoryId,
                    amount = draft.amount,
                    quantity = draft.quantity,
                    unit = draft.unit,
                    dateMillis = tripDateMillis,
                    storeName = tripStoreName.ifBlank { null },
                    tripId = tripId
                )
            }
            repository.addExpenses(expenses)
            resetBulkEntry()
            onDone()
        }
    }
}
