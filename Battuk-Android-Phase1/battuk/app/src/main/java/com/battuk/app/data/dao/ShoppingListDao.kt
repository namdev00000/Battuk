package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.battuk.app.data.entity.ShoppingList
import com.battuk.app.data.entity.ShoppingListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_list WHERE isArchived = 0 ORDER BY createdDateMillis DESC")
    fun observeActiveLists(): Flow<List<ShoppingList>>

    @Query("SELECT * FROM shopping_list WHERE id = :id")
    suspend fun getById(id: Long): ShoppingList?

    @Insert
    suspend fun insert(list: ShoppingList): Long

    @Update
    suspend fun update(list: ShoppingList)

    @Delete
    suspend fun delete(list: ShoppingList)

    @Query("SELECT * FROM shopping_list_item WHERE listId = :listId ORDER BY checked ASC, id ASC")
    fun observeItems(listId: Long): Flow<List<ShoppingListItem>>

    @Insert
    suspend fun insertItem(item: ShoppingListItem): Long

    @Update
    suspend fun updateItem(item: ShoppingListItem)

    @Delete
    suspend fun deleteItem(item: ShoppingListItem)
}
