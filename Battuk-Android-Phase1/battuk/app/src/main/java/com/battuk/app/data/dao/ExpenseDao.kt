package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.battuk.app.data.entity.Expense
import kotlinx.coroutines.flow.Flow

data class CategoryTotal(val categoryId: Long, val total: Double)
data class FamilyMemberTotal(val familyMemberId: Long?, val total: Double)
data class ItemTotal(val itemName: String, val total: Double)

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense ORDER BY dateMillis DESC")
    fun observeAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE dateMillis BETWEEN :start AND :end ORDER BY dateMillis DESC")
    fun observeBetween(start: Long, end: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE tripId = :tripId ORDER BY id ASC")
    fun observeByTrip(tripId: Long): Flow<List<Expense>>

    @Query(
        "SELECT * FROM expense WHERE itemName = :itemName ORDER BY dateMillis ASC"
    )
    fun observePriceHistory(itemName: String): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense WHERE dateMillis BETWEEN :start AND :end")
    fun observeTotalBetween(start: Long, end: Long): Flow<Double>

    @Query(
        "SELECT categoryId, COALESCE(SUM(amount), 0.0) as total FROM expense " +
            "WHERE dateMillis BETWEEN :start AND :end GROUP BY categoryId ORDER BY total DESC"
    )
    fun observeCategoryTotalsBetween(start: Long, end: Long): Flow<List<CategoryTotal>>

    @Query(
        "SELECT familyMemberId, COALESCE(SUM(amount), 0.0) as total FROM expense " +
            "WHERE dateMillis BETWEEN :start AND :end GROUP BY familyMemberId ORDER BY total DESC"
    )
    fun observeFamilyMemberTotalsBetween(start: Long, end: Long): Flow<List<FamilyMemberTotal>>

    @Query(
        "SELECT itemName, COALESCE(SUM(amount), 0.0) as total FROM expense " +
            "WHERE dateMillis BETWEEN :start AND :end GROUP BY itemName ORDER BY total DESC LIMIT :limit"
    )
    fun observeTopItemsBetween(start: Long, end: Long, limit: Int): Flow<List<ItemTotal>>

    @Query("SELECT DISTINCT itemName FROM expense ORDER BY itemName ASC")
    fun observeDistinctItemNames(): Flow<List<String>>

    @Insert
    suspend fun insert(expense: Expense): Long

    @Insert
    suspend fun insertAll(expenses: List<Expense>): List<Long>

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)
}
