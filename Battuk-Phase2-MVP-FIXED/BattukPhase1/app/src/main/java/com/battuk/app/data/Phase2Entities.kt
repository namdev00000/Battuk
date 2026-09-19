package com.battuk.app.data

import androidx.room.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("categoryId"), Index(value = ["monthKey", "categoryId"], unique = true)]
)
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val monthKey: String,
    val limitAmount: Double,
    val alertEnabled: Boolean = true
)

@Entity(
    indices = [Index("nextDueDate"), Index("active")]
)
data class LoanEmi(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val lender: String,
    val principalAmount: Double,
    val emiAmount: Double,
    val dueDay: Int,
    val nextDueDate: String,
    val active: Boolean = true,
    val note: String? = null
)
