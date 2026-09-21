package com.battuk.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingList(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdDateMillis: Long,
    val isArchived: Boolean = false
)

@Entity(tableName = "shopping_list_item")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listId: Long,
    val itemName: String,
    val itemNameMarathi: String? = null,
    val categoryId: Long? = null,
    val quantityLabel: String? = null,
    val checked: Boolean = false
)
