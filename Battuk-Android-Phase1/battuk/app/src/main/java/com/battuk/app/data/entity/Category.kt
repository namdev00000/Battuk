package com.battuk.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Built-in category types. "Other" allows fully custom categories too. */
enum class CategoryType {
    WEEKLY_MARKET,
    KIRANA_STORE,
    CLOTHES,
    SHOES,
    TOILETRIES,
    MILK_PRODUCTS,
    FAST_FOOD,
    ENTERTAINMENT,
    LOAN_EMI,
    PETROL_DIESEL,
    OTHER
}

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val iconKey: String,
    val isCustom: Boolean = false
)
