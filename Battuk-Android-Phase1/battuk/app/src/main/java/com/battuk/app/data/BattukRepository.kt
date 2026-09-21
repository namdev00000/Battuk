package com.battuk.app.data

import com.battuk.app.data.dao.CategoryTotal
import com.battuk.app.data.dao.FamilyMemberTotal
import com.battuk.app.data.dao.ItemTotal
import com.battuk.app.data.entity.CalculatorEntry
import com.battuk.app.data.entity.Category
import com.battuk.app.data.entity.Expense
import com.battuk.app.data.entity.FamilyMember
import com.battuk.app.data.entity.Income
import com.battuk.app.data.entity.Item
import com.battuk.app.data.entity.ItemSubtype
import com.battuk.app.data.entity.Profile
import com.battuk.app.data.entity.ShoppingList
import com.battuk.app.data.entity.ShoppingListItem
import com.battuk.app.data.entity.Trip
import kotlinx.coroutines.flow.Flow

class BattukRepository(private val db: AppDatabase) {

    // Profile
    fun observeProfile(): Flow<Profile?> = db.profileDao().observeProfile()
    suspend fun getProfileOnce(): Profile? = db.profileDao().getProfileOnce()
    suspend fun saveProfile(profile: Profile) = db.profileDao().upsert(profile)

    // Family members
    fun observeFamilyMembers(): Flow<List<FamilyMember>> = db.familyMemberDao().observeAll()
    suspend fun getFamilyMembersOnce(): List<FamilyMember> = db.familyMemberDao().getAllOnce()
    suspend fun getFamilyMember(id: Long): FamilyMember? = db.familyMemberDao().getById(id)
    suspend fun addFamilyMember(member: FamilyMember): Long = db.familyMemberDao().insert(member)
    suspend fun updateFamilyMember(member: FamilyMember) = db.familyMemberDao().update(member)
    suspend fun deleteFamilyMember(member: FamilyMember) = db.familyMemberDao().delete(member)

    // Categories
    fun observeCategories(): Flow<List<Category>> = db.categoryDao().observeAll()
    suspend fun getCategoriesOnce(): List<Category> = db.categoryDao().getAllOnce()
    suspend fun getCategory(id: Long): Category? = db.categoryDao().getById(id)
    suspend fun addCategory(category: Category): Long = db.categoryDao().insert(category)

    // Items
    fun observeItems(): Flow<List<Item>> = db.itemDao().observeAll()
    fun observeItemsBySubtype(subtype: ItemSubtype): Flow<List<Item>> = db.itemDao().observeBySubtype(subtype)
    fun searchItems(query: String): Flow<List<Item>> = db.itemDao().search(query)
    suspend fun addItem(item: Item): Long = db.itemDao().insert(item)

    // Trips
    fun observeTrips(): Flow<List<Trip>> = db.tripDao().observeAll()
    suspend fun getTrip(id: Long): Trip? = db.tripDao().getById(id)
    suspend fun addTrip(trip: Trip): Long = db.tripDao().insert(trip)
    suspend fun updateTrip(trip: Trip) = db.tripDao().update(trip)

    // Expenses
    fun observeAllExpenses(): Flow<List<Expense>> = db.expenseDao().observeAll()
    fun observeExpensesBetween(start: Long, end: Long): Flow<List<Expense>> =
        db.expenseDao().observeBetween(start, end)
    fun observeExpensesByTrip(tripId: Long): Flow<List<Expense>> = db.expenseDao().observeByTrip(tripId)
    fun observePriceHistory(itemName: String): Flow<List<Expense>> = db.expenseDao().observePriceHistory(itemName)
    fun observeTotalBetween(start: Long, end: Long): Flow<Double> = db.expenseDao().observeTotalBetween(start, end)
    fun observeCategoryTotalsBetween(start: Long, end: Long): Flow<List<CategoryTotal>> =
        db.expenseDao().observeCategoryTotalsBetween(start, end)
    fun observeFamilyMemberTotalsBetween(start: Long, end: Long): Flow<List<FamilyMemberTotal>> =
        db.expenseDao().observeFamilyMemberTotalsBetween(start, end)
    fun observeTopItemsBetween(start: Long, end: Long, limit: Int = 5): Flow<List<ItemTotal>> =
        db.expenseDao().observeTopItemsBetween(start, end, limit)
    fun observeDistinctItemNames(): Flow<List<String>> = db.expenseDao().observeDistinctItemNames()
    suspend fun addExpense(expense: Expense): Long = db.expenseDao().insert(expense)
    suspend fun addExpenses(expenses: List<Expense>): List<Long> = db.expenseDao().insertAll(expenses)
    suspend fun updateExpense(expense: Expense) = db.expenseDao().update(expense)
    suspend fun deleteExpense(expense: Expense) = db.expenseDao().delete(expense)

    // Income
    fun observeAllIncome(): Flow<List<Income>> = db.incomeDao().observeAll()
    fun observeIncomeBetween(start: Long, end: Long): Flow<List<Income>> = db.incomeDao().observeBetween(start, end)
    fun observeIncomeTotalBetween(start: Long, end: Long): Flow<Double> = db.incomeDao().observeTotalBetween(start, end)
    suspend fun addIncome(income: Income): Long = db.incomeDao().insert(income)
    suspend fun deleteIncome(income: Income) = db.incomeDao().delete(income)

    // Calculator history
    fun observeCalculatorHistory(): Flow<List<CalculatorEntry>> = db.calculatorDao().observeHistory()
    suspend fun addCalculatorEntry(entry: CalculatorEntry): Long = db.calculatorDao().insert(entry)
    suspend fun clearCalculatorHistory() = db.calculatorDao().clearHistory()

    // Shopping lists (Remember the Things)
    fun observeShoppingLists(): Flow<List<ShoppingList>> = db.shoppingListDao().observeActiveLists()
    suspend fun getShoppingList(id: Long): ShoppingList? = db.shoppingListDao().getById(id)
    suspend fun addShoppingList(list: ShoppingList): Long = db.shoppingListDao().insert(list)
    suspend fun updateShoppingList(list: ShoppingList) = db.shoppingListDao().update(list)
    suspend fun deleteShoppingList(list: ShoppingList) = db.shoppingListDao().delete(list)
    fun observeShoppingListItems(listId: Long): Flow<List<ShoppingListItem>> =
        db.shoppingListDao().observeItems(listId)
    suspend fun addShoppingListItem(item: ShoppingListItem): Long = db.shoppingListDao().insertItem(item)
    suspend fun updateShoppingListItem(item: ShoppingListItem) = db.shoppingListDao().updateItem(item)
    suspend fun deleteShoppingListItem(item: ShoppingListItem) = db.shoppingListDao().deleteItem(item)
}
