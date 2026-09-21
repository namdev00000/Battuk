package com.battuk.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_member")
data class FamilyMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dobMillis: Long? = null,
    val relation: String,
    val photoUri: String? = null,
    val isEarningMember: Boolean = false,
    val isSelf: Boolean = false
)
