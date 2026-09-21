package com.battuk.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyMemberId: Long,
    val amount: Double,
    val source: String,
    val dateMillis: Long,
    val note: String? = null,
    val isRecurring: Boolean = false
)
