package com.battuk.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.battuk.app.data.dao.CalculatorDao
import com.battuk.app.data.dao.CategoryDao
import com.battuk.app.data.dao.ExpenseDao
import com.battuk.app.data.dao.FamilyMemberDao
import com.battuk.app.data.dao.IncomeDao
import com.battuk.app.data.dao.ItemDao
import com.battuk.app.data.dao.ProfileDao
import com.battuk.app.data.dao.ShoppingListDao
import com.battuk.app.data.dao.TripDao
import com.battuk.app.data.entity.CalculatorEntry
import com.battuk.app.data.entity.Category
import com.battuk.app.data.entity.Expense
import com.battuk.app.data.entity.FamilyMember
import com.battuk.app.data.entity.Income
import com.battuk.app.data.entity.Item
import com.battuk.app.data.entity.Profile
import com.battuk.app.data.entity.ShoppingList
import com.battuk.app.data.entity.ShoppingListItem
import com.battuk.app.data.entity.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Profile::class,
        FamilyMember::class,
        Category::class,
        Item::class,
        Trip::class,
        Expense::class,
        Income::class,
        CalculatorEntry::class,
        ShoppingList::class,
        ShoppingListItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun categoryDao(): CategoryDao
    abstract fun itemDao(): ItemDao
    abstract fun tripDao(): TripDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun calculatorDao(): CalculatorDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "battuk.db"
                ).addCallback(SeedCallback(context)).build()
                INSTANCE = instance
                instance
            }
        }
    }

    /** Seeds default categories + the bilingual Weekly Market item list on first creation. */
    private class SeedCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                val categoryDao = database.categoryDao()
                val itemDao = database.itemDao()

                if (categoryDao.count() == 0) {
                    categoryDao.insertAll(SeedData.defaultCategories())
                }
                val weeklyMarket = categoryDao.getAllOnce()
                    .firstOrNull { it.type == com.battuk.app.data.entity.CategoryType.WEEKLY_MARKET }
                if (weeklyMarket != null && itemDao.count() == 0) {
                    itemDao.insertAll(SeedData.weeklyMarketItems(weeklyMarket.id))
                }
            }
        }
    }
}
