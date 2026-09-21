package com.battuk.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.battuk.app.BattukApplication
import com.battuk.app.ui.calendar.MiniCalendarScreen
import com.battuk.app.ui.category.CategoryBrowserScreen
import com.battuk.app.ui.category.CategoryDetailsScreen
import com.battuk.app.ui.category.ItemSearchScreen
import com.battuk.app.ui.components.BattukBottomBar
import com.battuk.app.ui.components.BattukTopBar
import com.battuk.app.ui.components.BottomTab
import com.battuk.app.ui.components.HubItem
import com.battuk.app.ui.components.HubScreen
import com.battuk.app.ui.dashboard.VisualDashboardScreen
import com.battuk.app.ui.expense.BulkEntryScreen
import com.battuk.app.ui.expense.SingleEntryScreen
import com.battuk.app.ui.family.AddFamilyMemberScreen
import com.battuk.app.ui.family.FamilyMembersScreen
import com.battuk.app.ui.home.HomeScreen
import com.battuk.app.ui.inflation.InflationCalculatorScreen
import com.battuk.app.ui.income.IncomeEntryScreen
import com.battuk.app.ui.itemhistory.ItemPriceHistoryScreen
import com.battuk.app.ui.onboarding.OnboardingScreen
import com.battuk.app.ui.onboarding.ProfileSetupScreen
import com.battuk.app.ui.receipts.ReceiptsGalleryScreen
import com.battuk.app.ui.reports.ReportsScreen
import com.battuk.app.ui.settings.SettingsScreen
import com.battuk.app.ui.settings.YearManagementScreen
import com.battuk.app.ui.shoppinglist.ShoppingListDetailScreen
import com.battuk.app.ui.shoppinglist.ShoppingListsScreen
import com.battuk.app.ui.theme.BattukTheme
import com.battuk.app.ui.theme.toThemeSetting
import com.battuk.app.viewmodel.AppViewModelFactory
import com.battuk.app.viewmodel.CalculatorViewModel
import com.battuk.app.viewmodel.CategoryViewModel
import com.battuk.app.viewmodel.ExpenseViewModel
import com.battuk.app.viewmodel.FamilyViewModel
import com.battuk.app.viewmodel.HomeViewModel
import com.battuk.app.viewmodel.InflationCalculatorViewModel
import com.battuk.app.viewmodel.IncomeViewModel
import com.battuk.app.viewmodel.ItemHistoryViewModel
import com.battuk.app.viewmodel.ProfileViewModel
import com.battuk.app.viewmodel.ReportsViewModel
import com.battuk.app.viewmodel.SettingsViewModel
import com.battuk.app.viewmodel.ShoppingListViewModel
import com.battuk.app.viewmodel.YearManagementViewModel

private val TAB_ROUTES = setOf(Screen.Home.route, "expenses_hub", Screen.Reports.route, Screen.MiniCalendar.route, "more_hub")

@Composable
fun BattukApp() {
    val context = LocalContext.current
    val app = context.applicationContext as BattukApplication
    val factory = remember {
        AppViewModelFactory(app.repository, app.preferencesManager, app.soundManager)
    }

    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val theme by settingsViewModel.themeMode.collectAsState()

    BattukTheme(themeSetting = theme.toThemeSetting()) {
        val navController = rememberNavController()
        val profileViewModel: ProfileViewModel = viewModel(factory = factory)
        val profile by profileViewModel.profile.collectAsState()

        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        val showChrome = currentRoute in TAB_ROUTES

        Scaffold(
            topBar = {
                if (currentRoute != Screen.Onboarding.route && currentRoute != Screen.ProfileSetup.route) {
                    BattukTopBar(
                        title = titleFor(currentRoute),
                        onBack = if (!showChrome) ({ navController.popBackStack() }) else null,
                        currentTheme = theme,
                        onToggleTheme = { settingsViewModel.cycleTheme() }
                    )
                }
            },
            bottomBar = {
                if (showChrome) {
                    BattukBottomBar(
                        selected = tabFor(currentRoute),
                        onSelect = { tab ->
                            val route = when (tab) {
                                BottomTab.HOME -> Screen.Home.route
                                BottomTab.EXPENSES -> "expenses_hub"
                                BottomTab.REPORTS -> Screen.Reports.route
                                BottomTab.CALENDAR -> Screen.MiniCalendar.route
                                BottomTab.MORE -> "more_hub"
                            }
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = if (profile == null) Screen.Onboarding.route else Screen.Home.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(onGetStarted = { navController.navigate(Screen.ProfileSetup.route) })
                }
                composable(Screen.ProfileSetup.route) {
                    ProfileSetupScreen(
                        viewModel = profileViewModel,
                        onContinue = {
                            navController.navigate(Screen.FamilyMembers.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.FamilyMembers.route) {
                    val familyViewModel: FamilyViewModel = viewModel(factory = factory)
                    FamilyMembersScreen(
                        viewModel = familyViewModel,
                        onAddMember = { navController.navigate(Screen.AddFamilyMember.route) },
                        onContinue = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0)
                            }
                        }
                    )
                }
                composable(Screen.AddFamilyMember.route) {
                    val familyViewModel: FamilyViewModel = viewModel(factory = factory)
                    AddFamilyMemberScreen(viewModel = familyViewModel, onSaved = { navController.popBackStack() })
                }

                composable(Screen.Home.route) {
                    val homeViewModel: HomeViewModel = viewModel(factory = factory)
                    HomeScreen(
                        viewModel = homeViewModel,
                        onAddExpense = { navController.navigate(Screen.SingleEntry.route) },
                        onAddIncome = { navController.navigate(Screen.IncomeEntry.route) },
                        onShoppingList = { navController.navigate(Screen.ShoppingLists.route) },
                        onViewReports = { navController.navigate(Screen.Reports.route) }
                    )
                }

                composable("expenses_hub") {
                    HubScreen(
                        title = "Expenses",
                        items = listOf(
                            HubItem("Single Entry", "Add one item quickly", Icons.Filled.AttachMoney) { navController.navigate(Screen.SingleEntry.route) },
                            HubItem("Bulk Entry", "Log a full market trip", Icons.Filled.ShoppingCart) { navController.navigate(Screen.BulkEntryItems.route) },
                            HubItem("Remember the Things", "Plan before you shop", Icons.Filled.Checklist) { navController.navigate(Screen.ShoppingLists.route) },
                            HubItem("Categories", "Browse all categories", Icons.Filled.Category) { navController.navigate(Screen.CategoryBrowser.route) },
                            HubItem("Income", "Record family income", Icons.Filled.AttachMoney) { navController.navigate(Screen.IncomeEntry.route) }
                        )
                    )
                }

                composable(Screen.SingleEntry.route) {
                    val expenseViewModel: ExpenseViewModel = viewModel(factory = factory)
                    val categoryViewModel: CategoryViewModel = viewModel(factory = factory)
                    val calculatorViewModel: CalculatorViewModel = viewModel(factory = factory)
                    SingleEntryScreen(
                        expenseViewModel = expenseViewModel,
                        categoryViewModel = categoryViewModel,
                        calculatorViewModel = calculatorViewModel,
                        onPickItem = {
                            val catId = expenseViewModel.singleCategoryId
                            if (catId != null) navController.navigate(Screen.ItemSearch.createRoute(catId))
                        },
                        onSaved = { navController.popBackStack() }
                    )
                }

                composable(Screen.BulkEntryItems.route) {
                    val expenseViewModel: ExpenseViewModel = viewModel(factory = factory)
                    val categoryViewModel: CategoryViewModel = viewModel(factory = factory)
                    val calculatorViewModel: CalculatorViewModel = viewModel(factory = factory)
                    BulkEntryScreen(
                        expenseViewModel = expenseViewModel,
                        categoryViewModel = categoryViewModel,
                        calculatorViewModel = calculatorViewModel,
                        onSaved = { navController.popBackStack() }
                    )
                }

                composable(Screen.ShoppingLists.route) {
                    val shoppingListViewModel: ShoppingListViewModel = viewModel(factory = factory)
                    ShoppingListsScreen(viewModel = shoppingListViewModel, onOpenList = { id ->
                        navController.navigate(Screen.ShoppingListDetail.createRoute(id))
                    })
                }
                composable(Screen.ShoppingListDetail.route) { backStackEntry ->
                    val listId = backStackEntry.arguments?.getString("listId")?.toLongOrNull() ?: return@composable
                    val shoppingListViewModel: ShoppingListViewModel = viewModel(factory = factory)
                    val expenseViewModel: ExpenseViewModel = viewModel(factory = factory)
                    ShoppingListDetailScreen(
                        viewModel = shoppingListViewModel,
                        listId = listId,
                        onConvertToBulkEntry = { checkedItems ->
                            expenseViewModel.draftItems.clear()
                            navController.navigate(Screen.BulkEntryItems.route)
                        }
                    )
                }

                composable(Screen.CategoryBrowser.route) {
                    val categoryViewModel: CategoryViewModel = viewModel(factory = factory)
                    CategoryBrowserScreen(viewModel = categoryViewModel, onCategoryClick = { id ->
                        navController.navigate(Screen.CategoryDetails.createRoute(id))
                    })
                }
                composable(Screen.ItemSearch.route) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId")?.toLongOrNull() ?: return@composable
                    val categoryViewModel: CategoryViewModel = viewModel(factory = factory)
                    val expenseViewModel: ExpenseViewModel = viewModel(factory = factory)
                    ItemSearchScreen(
                        viewModel = categoryViewModel,
                        categoryId = categoryId,
                        onItemSelected = { item ->
                            expenseViewModel.singleItemName = item.nameEnglish
                            expenseViewModel.singleItemNameMarathi = item.nameMarathi
                            expenseViewModel.singleCategoryId = item.categoryId
                            expenseViewModel.singleUnit = item.defaultUnit
                            navController.popBackStack()
                        },
                        onCustomEntry = { name ->
                            expenseViewModel.singleItemName = name
                            expenseViewModel.singleCategoryId = categoryId
                            navController.popBackStack()
                        }
                    )
                }
                composable(Screen.CategoryDetails.route) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId")?.toLongOrNull() ?: return@composable
                    val reportsViewModel: ReportsViewModel = viewModel(factory = factory)
                    CategoryDetailsScreen(viewModel = reportsViewModel, categoryId = categoryId)
                }

                composable(Screen.ItemPriceHistory.route) { backStackEntry ->
                    val itemName = backStackEntry.arguments?.getString("itemName") ?: return@composable
                    val itemHistoryViewModel: ItemHistoryViewModel = viewModel(factory = factory)
                    ItemPriceHistoryScreen(viewModel = itemHistoryViewModel, itemName = itemName)
                }

                composable(Screen.MiniCalendar.route) {
                    val calendarViewModel: com.battuk.app.viewmodel.CalendarViewModel = viewModel(factory = factory)
                    MiniCalendarScreen(viewModel = calendarViewModel)
                }

                composable(Screen.Reports.route) {
                    val reportsViewModel: ReportsViewModel = viewModel(factory = factory)
                    ReportsScreen(viewModel = reportsViewModel)
                }

                composable("more_hub") {
                    HubScreen(
                        title = "More",
                        items = listOf(
                            HubItem("Visual Dashboard", "Key insights at a glance", Icons.Filled.QueryStats) { navController.navigate(Screen.VisualDashboard.route) },
                            HubItem("Inflation Calculator", "Track price changes", Icons.Filled.Calculate) { navController.navigate(Screen.InflationCalculator.route) },
                            HubItem("Receipts", "View all saved receipts", Icons.Filled.Receipt) { navController.navigate(Screen.ReceiptsGallery.route) },
                            HubItem("Family Members", "Manage your family", Icons.Filled.Checklist) { navController.navigate(Screen.FamilyMembers.route) },
                            HubItem("Settings", "Theme, sound, and more", Icons.Filled.Settings) { navController.navigate(Screen.Settings.route) }
                        )
                    )
                }

                composable(Screen.VisualDashboard.route) {
                    val reportsViewModel: ReportsViewModel = viewModel(factory = factory)
                    VisualDashboardScreen(viewModel = reportsViewModel, onItemClick = { name ->
                        navController.navigate(Screen.ItemPriceHistory.createRoute(name))
                    })
                }
                composable(Screen.InflationCalculator.route) {
                    val inflationViewModel: InflationCalculatorViewModel = viewModel(factory = factory)
                    InflationCalculatorScreen(viewModel = inflationViewModel)
                }
                composable(Screen.IncomeEntry.route) {
                    val incomeViewModel: IncomeViewModel = viewModel(factory = factory)
                    IncomeEntryScreen(viewModel = incomeViewModel, onSaved = { navController.popBackStack() })
                }
                composable(Screen.ReceiptsGallery.route) {
                    val reportsViewModel: ReportsViewModel = viewModel(factory = factory)
                    ReceiptsGalleryScreen(viewModel = reportsViewModel)
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onManageFamily = { navController.navigate(Screen.FamilyMembers.route) },
                        onManageCategories = { navController.navigate(Screen.CategoryBrowser.route) },
                        onManageYears = { navController.navigate(Screen.YearManagement.route) }
                    )
                }
                composable(Screen.YearManagement.route) {
                    val yearViewModel: YearManagementViewModel = viewModel(factory = factory)
                    YearManagementScreen(viewModel = yearViewModel)
                }
            }
        }
    }
}

private fun titleFor(route: String?): String = when (route) {
    Screen.Home.route -> "Home"
    "expenses_hub" -> "Expenses"
    Screen.Reports.route -> "Reports"
    Screen.MiniCalendar.route -> "Calendar"
    "more_hub" -> "More"
    Screen.SingleEntry.route -> "Add Expense"
    Screen.BulkEntryItems.route -> "Bulk Entry"
    Screen.ShoppingLists.route -> "Remember the Things"
    Screen.CategoryBrowser.route -> "Categories"
    Screen.VisualDashboard.route -> "Visual Dashboard"
    Screen.InflationCalculator.route -> "Inflation Calculator"
    Screen.IncomeEntry.route -> "Income Entry"
    Screen.ReceiptsGallery.route -> "Receipts"
    Screen.Settings.route -> "Settings"
    Screen.YearManagement.route -> "Year Management"
    Screen.FamilyMembers.route -> "Family Members"
    Screen.AddFamilyMember.route -> "Add Family Member"
    else -> "Battuk"
}

private fun tabFor(route: String?): BottomTab = when (route) {
    Screen.Home.route -> BottomTab.HOME
    "expenses_hub" -> BottomTab.EXPENSES
    Screen.Reports.route -> BottomTab.REPORTS
    Screen.MiniCalendar.route -> BottomTab.CALENDAR
    else -> BottomTab.MORE
}
