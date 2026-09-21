package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.battuk.app.data.entity.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trip ORDER BY dateMillis DESC")
    fun observeAll(): Flow<List<Trip>>

    @Query("SELECT * FROM trip WHERE id = :id")
    suspend fun getById(id: Long): Trip?

    @Insert
    suspend fun insert(trip: Trip): Long

    @Update
    suspend fun update(trip: Trip)
}
