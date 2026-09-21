package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.battuk.app.data.entity.FamilyMember
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMemberDao {
    @Query("SELECT * FROM family_member ORDER BY isSelf DESC, id ASC")
    fun observeAll(): Flow<List<FamilyMember>>

    @Query("SELECT * FROM family_member ORDER BY isSelf DESC, id ASC")
    suspend fun getAllOnce(): List<FamilyMember>

    @Query("SELECT * FROM family_member WHERE id = :id")
    suspend fun getById(id: Long): FamilyMember?

    @Insert
    suspend fun insert(member: FamilyMember): Long

    @Update
    suspend fun update(member: FamilyMember)

    @Delete
    suspend fun delete(member: FamilyMember)
}
