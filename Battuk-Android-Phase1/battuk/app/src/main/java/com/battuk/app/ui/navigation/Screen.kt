package com.battuk.app.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object ProfileSetup : Screen("profile_setup")
    data object FamilyMembers : Screen("family_members")
    data object AddFamilyMember : Screen("add_family_member")

    data object Home : Screen("home")

    data object SingleEntry : Screen("single_entry")
    data object BulkEntryItems : Screen("bulk_entry_items")
    data object BulkEntryReceipt : Screen("bulk_entry_receipt")
    data object BulkEntryReview : Screen("bulk_entry_review")

    data object ShoppingLists : Screen("shopping_lists")
    data object ShoppingListDetail : Screen("shopping_list_detail/{listId}") {
        fun createRoute(listId: Long) = "shopping_list_detail/$listId"
    }

    data object CategoryBrowser : Screen("category_browser")
    data object ItemSearch : Screen("item_search/{categoryId}") {
        fun createRoute(categoryId: Long) = "item_search/$categoryId"
    }
    data object CategoryDetails : Screen("category_details/{categoryId}") {
        fun createRoute(categoryId: Long) = "category_details/$categoryId"
    }

    data object ItemPriceHistory : Screen("item_price_history/{itemName}") {
        fun createRoute(itemName: String) = "item_price_history/$itemName"
    }

    data object MiniCalendar : Screen("mini_calendar")
    data object CalculatorHistory : Screen("calculator_history")
    data object IncomeEntry : Screen("income_entry")
    data object Reports : Screen("reports")
    data object VisualDashboard : Screen("visual_dashboard")
    data object InflationCalculator : Screen("inflation_calculator")
    data object ReceiptsGallery : Screen("receipts_gallery")
    data object Settings : Screen("settings")
    data object YearManagement : Screen("year_management")
}
