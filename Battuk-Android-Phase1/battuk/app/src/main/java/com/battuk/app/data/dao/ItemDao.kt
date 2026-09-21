package com.battuk.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.battuk.app.data.entity.Item
import com.battuk.app.data.entity.ItemSubtype
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item ORDER BY nameEnglish ASC")
    fun observeAll(): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE subtype = :subtype ORDER BY nameEnglish ASC")
    fun observeBySubtype(subtype: ItemSubtype): Flow<List<Item>>

    @Query(
        "SELECT * FROM item WHERE nameEnglish LIKE '%' || :query || '%' " +
            "OR nameMarathi LIKE '%' || :query || '%' ORDER BY nameEnglish ASC"
    )
    fun search(query: String): Flow<List<Item>>

    @Query("SELECT COUNT(*) FROM item")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Item>)

    @Insert
    suspend fun insert(item: Item): Long
}
