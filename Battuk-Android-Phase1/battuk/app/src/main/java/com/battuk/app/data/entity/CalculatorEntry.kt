package com.battuk.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculator_entry")
data class CalculatorEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val expression: String,
    val result: Double,
    val timestampMillis: Long
)
