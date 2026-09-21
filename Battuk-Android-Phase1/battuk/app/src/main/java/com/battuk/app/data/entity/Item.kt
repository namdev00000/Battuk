package com.battuk.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ItemSubtype { VEGETABLE, LEAFY_GREEN, FRUIT, OTHER }

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameEnglish: String,
    val nameMarathi: String,
    val subtype: ItemSubtype,
    val defaultUnit: String,
    val categoryId: Long,
    val isCustom: Boolean = false
)
