package com.battuk.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row table holding the app owner's own profile.
 * We always read/write the row with id = 1.
 */
@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey val id: Int = 1,
    val firstName: String = "",
    val lastName: String = "",
    val dobMillis: Long? = null,
    val photoUri: String? = null
)
