package com.battuk.app.data

import androidx.room.TypeConverter
import com.battuk.app.data.entity.CategoryType
import com.battuk.app.data.entity.ItemSubtype

class Converters {
    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType =
        runCatching { CategoryType.valueOf(value) }.getOrDefault(CategoryType.OTHER)

    @TypeConverter
    fun fromItemSubtype(value: ItemSubtype): String = value.name

    @TypeConverter
    fun toItemSubtype(value: String): ItemSubtype =
        runCatching { ItemSubtype.valueOf(value) }.getOrDefault(ItemSubtype.OTHER)
}
