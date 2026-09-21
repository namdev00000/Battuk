package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.battuk.app.data.entity.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<Profile?>

    @Query("SELECT * FROM profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileOnce(): Profile?

    @Upsert
    suspend fun upsert(profile: Profile)
}
