package com.battuk.app.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity
data class Profile(@PrimaryKey val id:Int=1, val name:String, val dob:String?=null, val photoUri:String?=null)
@Entity
data class FamilyMember(@PrimaryKey(autoGenerate=true) val id:Long=0, val name:String, val relation:String, val dob:String?=null, val photoUri:String?=null, val hasIncome:Boolean=false)
@Entity
data class Category(@PrimaryKey(autoGenerate=true) val id:Long=0, val name:String, val type:String, val icon:String)
@Entity
data class Item(@PrimaryKey(autoGenerate=true) val id:Long=0, val nameEnglish:String, val nameMarathi:String, val subtype:String, val defaultUnit:String)
@Entity
data class Expense(@PrimaryKey(autoGenerate=true) val id:Long=0, val itemId:Long?=null, val description:String?=null, val categoryId:Long, val amount:Double, val quantity:Double, val unit:String, val date:String, val familyMemberId:Long?=null, val storeName:String?=null, val receiptImageUri:String?=null, val tripId:Long?=null)
@Entity
data class Trip(@PrimaryKey(autoGenerate=true) val id:Long=0, val date:String, val storeName:String, val tripReceiptImageUri:String?=null)
@Entity
data class Income(@PrimaryKey(autoGenerate=true) val id:Long=0, val familyMemberId:Long, val amount:Double, val source:String, val date:String, val note:String?=null)
@Entity
data class CalculatorEntry(@PrimaryKey(autoGenerate=true) val id:Long=0, val expression:String, val result:String, val timestamp:Long)
@Entity
data class ShoppingList(@PrimaryKey(autoGenerate=true) val id:Long=0, val name:String, val createdDate:String)
@Entity
data class ShoppingListItem(@PrimaryKey(autoGenerate=true) val id:Long=0, val listId:Long, val itemName:String, val categoryId:Long?=null, val checked:Boolean=false)

@Dao
interface ProfileDao { @Query("SELECT * FROM Profile WHERE id=1") suspend fun get():Profile?; @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun save(p:Profile); @Query("DELETE FROM Profile") suspend fun clear() }
@Dao
interface FamilyDao { @Query("SELECT * FROM FamilyMember ORDER BY name") suspend fun all():List<FamilyMember>; @Insert suspend fun insert(x:FamilyMember):Long; @Update suspend fun update(x:FamilyMember); @Delete suspend fun delete(x:FamilyMember); @Query("DELETE FROM FamilyMember") suspend fun clear() }
@Dao
interface CategoryDao { @Query("SELECT * FROM Category ORDER BY name") suspend fun all():List<Category>; @Insert suspend fun insert(x:Category):Long; @Delete suspend fun delete(x:Category); @Query("DELETE FROM Category") suspend fun clear() }
@Dao
interface ItemDao { @Query("SELECT * FROM Item ORDER BY nameEnglish") suspend fun all():List<Item>; @Query("SELECT * FROM Item WHERE id=:id") suspend fun get(id:Long):Item?; @Insert suspend fun insert(x:Item):Long; @Query("DELETE FROM Item") suspend fun clear() }
@Dao
interface ExpenseDao {
    @Query("SELECT * FROM Expense ORDER BY date DESC, id DESC") suspend fun all():List<Expense>
    @Insert suspend fun insert(x:Expense):Long
    @Query("SELECT * FROM Expense WHERE date=:day ORDER BY id DESC") suspend fun byDay(day:String):List<Expense>; @Query("SELECT COALESCE(SUM(amount),0) FROM Expense WHERE categoryId=:categoryId AND date LIKE :monthKey || '%'") suspend fun sumForCategoryMonth(categoryId:Long,monthKey:String):Double; @Query("DELETE FROM Expense") suspend fun clear()
}
@Dao
interface TripDao { @Insert suspend fun insert(x:Trip):Long; @Query("SELECT * FROM Trip ORDER BY date DESC") suspend fun all():List<Trip>; @Query("DELETE FROM Trip") suspend fun clear() }
@Dao
interface IncomeDao { @Query("SELECT * FROM Income ORDER BY date DESC") suspend fun all():List<Income>; @Insert suspend fun insert(x:Income):Long; @Query("DELETE FROM Income") suspend fun clear() }
@Dao
interface CalcDao { @Query("SELECT * FROM CalculatorEntry ORDER BY timestamp DESC") suspend fun all():List<CalculatorEntry>; @Insert suspend fun insert(x:CalculatorEntry):Long; @Query("DELETE FROM CalculatorEntry") suspend fun clear() }
@Dao
interface ShoppingDao {
    @Query("SELECT * FROM ShoppingList ORDER BY createdDate DESC") suspend fun lists():List<ShoppingList>
    @Query("SELECT * FROM ShoppingListItem WHERE listId=:listId ORDER BY id") suspend fun items(listId:Long):List<ShoppingListItem>
    @Insert suspend fun insertList(x:ShoppingList):Long
    @Insert suspend fun insertItem(x:ShoppingListItem):Long
    @Update suspend fun updateItem(x:ShoppingListItem)
    @Query("DELETE FROM ShoppingListItem") suspend fun clearItems()
    @Query("DELETE FROM ShoppingList") suspend fun clearLists()
}


@Dao
interface BudgetDao {
    @Query("SELECT * FROM Budget ORDER BY monthKey DESC, categoryId") suspend fun all():List<Budget>
    @Query("SELECT * FROM Budget WHERE monthKey=:monthKey ORDER BY categoryId") suspend fun forMonth(monthKey:String):List<Budget>
    @Query("SELECT * FROM Budget WHERE categoryId=:categoryId AND monthKey=:monthKey LIMIT 1") suspend fun get(categoryId:Long,monthKey:String):Budget?
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(x:Budget):Long
    @Delete suspend fun delete(x:Budget)
    @Query("DELETE FROM Budget") suspend fun clear()
}

@Dao
interface LoanEmiDao {
    @Query("SELECT * FROM LoanEmi ORDER BY active DESC, nextDueDate ASC") suspend fun all():List<LoanEmi>
    @Insert suspend fun insert(x:LoanEmi):Long
    @Update suspend fun update(x:LoanEmi)
    @Query("DELETE FROM LoanEmi") suspend fun clear()
}

@Database(entities=[Profile::class,FamilyMember::class,Category::class,Item::class,Expense::class,Trip::class,Income::class,CalculatorEntry::class,ShoppingList::class,ShoppingListItem::class,Budget::class,LoanEmi::class],version=2,exportSchema=false)
abstract class BattukDatabase:RoomDatabase(){
    abstract fun profileDao():ProfileDao; abstract fun familyDao():FamilyDao; abstract fun categoryDao():CategoryDao; abstract fun itemDao():ItemDao
    abstract fun expenseDao():ExpenseDao; abstract fun tripDao():TripDao; abstract fun incomeDao():IncomeDao; abstract fun calcDao():CalcDao; abstract fun shoppingDao():ShoppingDao; abstract fun budgetDao():BudgetDao; abstract fun loanEmiDao():LoanEmiDao
    companion object {
        val MIGRATION_1_2 = object : Migration(1,2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS Budget (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, categoryId INTEGER NOT NULL, monthKey TEXT NOT NULL, limitAmount REAL NOT NULL, alertEnabled INTEGER NOT NULL, FOREIGN KEY(categoryId) REFERENCES Category(id) ON UPDATE NO ACTION ON DELETE CASCADE)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_Budget_monthKey_categoryId ON Budget(monthKey, categoryId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_Budget_categoryId ON Budget(categoryId)")
                db.execSQL("CREATE TABLE IF NOT EXISTS LoanEmi (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, lender TEXT NOT NULL, principalAmount REAL NOT NULL, emiAmount REAL NOT NULL, dueDay INTEGER NOT NULL, nextDueDate TEXT NOT NULL, active INTEGER NOT NULL, note TEXT)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_LoanEmi_nextDueDate ON LoanEmi(nextDueDate)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_LoanEmi_active ON LoanEmi(active)")
            }
        }
        @Volatile private var INSTANCE:BattukDatabase?=null
        fun get(context:Context)=INSTANCE?:synchronized(this){INSTANCE?:Room.databaseBuilder(context.applicationContext,BattukDatabase::class.java,"battuk.db").addMigrations(MIGRATION_1_2).build().also{INSTANCE=it}}
    }
}
