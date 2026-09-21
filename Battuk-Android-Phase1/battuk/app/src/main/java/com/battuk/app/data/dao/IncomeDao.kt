package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.battuk.app.data.entity.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income ORDER BY dateMillis DESC")
    fun observeAll(): Flow<List<Income>>

    @Query("SELECT * FROM income WHERE dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    fun observeBetween(start: Long, end: Long): Flow<List<Income>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM income WHERE dateMillis BETWEEN :start AND :end")
    fun observeTotalBetween(start: Long, end: Long): Flow<Double>

    @Insert
    suspend fun insert(income: Income): Long

    @Delete
    suspend fun delete(income: Income)
}
