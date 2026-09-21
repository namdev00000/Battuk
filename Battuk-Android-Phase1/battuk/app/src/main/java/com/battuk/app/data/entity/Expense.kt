package com.battuk.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemName: String,
    val itemNameMarathi: String? = null,
    val categoryId: Long,
    val amount: Double,
    val quantity: Double,
    val unit: String,
    val dateMillis: Long,
    val familyMemberId: Long? = null,
    val storeName: String? = null,
    val receiptUri: String? = null,
    val tripId: Long? = null,
    val note: String? = null
)
