package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.ShoppingList
import com.battuk.app.data.entity.ShoppingListItem
import com.battuk.app.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val repository: BattukRepository) : ViewModel() {

    val lists: StateFlow<List<ShoppingList>> = repository.observeShoppingLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var newListName by mutableStateOf("This Week's Market")

    fun createList(onCreated: (Long) -> Unit) {
        if (newListName.isBlank()) return
        viewModelScope.launch {
            val id = repository.addShoppingList(ShoppingList(name = newListName.trim(), createdDateMillis = DateUtils.nowMillis()))
            newListName = "This Week's Market"
            onCreated(id)
        }
    }

    fun itemsFor(listId: Long): StateFlow<List<ShoppingListItem>> =
        repository.observeShoppingListItems(listId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addItem(listId: Long, name: String, quantityLabel: String?, categoryId: Long?) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addShoppingListItem(
                ShoppingListItem(listId = listId, itemName = name.trim(), quantityLabel = quantityLabel, categoryId = categoryId)
            )
        }
    }

    fun toggleChecked(item: ShoppingListItem) {
        viewModelScope.launch { repository.updateShoppingListItem(item.copy(checked = !item.checked)) }
    }

    fun deleteItem(item: ShoppingListItem) {
        viewModelScope.launch { repository.deleteShoppingListItem(item) }
    }

    fun archiveList(list: ShoppingList) {
        viewModelScope.launch { repository.updateShoppingList(list.copy(isArchived = true)) }
    }
}
