package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.battuk.app.data.entity.CalculatorEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM calculator_entry ORDER BY timestampMillis DESC")
    fun observeHistory(): Flow<List<CalculatorEntry>>

    @Insert
    suspend fun insert(entry: CalculatorEntry): Long

    @Query("DELETE FROM calculator_entry")
    suspend fun clearHistory()
}
