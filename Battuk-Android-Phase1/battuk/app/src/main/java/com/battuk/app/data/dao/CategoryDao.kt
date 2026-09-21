package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.battuk.app.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category ORDER BY id ASC")
    fun observeAll(): Flow<List<Category>>

    @Query("SELECT * FROM category ORDER BY id ASC")
    suspend fun getAllOnce(): List<Category>

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getById(id: Long): Category?

    @Query("SELECT COUNT(*) FROM category")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<Category>)

    @Insert
    suspend fun insert(category: Category): Long

    @Delete
    suspend fun delete(category: Category)
}
