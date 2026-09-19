package com.battuk.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import coil.compose.AsyncImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.RoomDatabase
import com.battuk.app.data.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

private val Context.dataStore by preferencesDataStore("battuk_settings")
val Green = Color(0xFF16A85A)
private val DarkGreen = Color(0xFF0F5736)
val SoftGreen = Color(0xFFE9F8EF)
private val Orange = Color(0xFFFFA726)
private val Ink = Color(0xFF17324D)

class MainActivity : ComponentActivity() {
    private val vm by viewModels<BattukViewModel> { BattukViewModel.Factory(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); if (Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 4001); ensureNotificationChannels(this); setContent { BattukApp(vm) } }
}

class BattukViewModel(private val ctx: Context) : ViewModel() {
    val db = BattukDatabase.get(ctx)
    var profile by mutableStateOf<Profile?>(null); private set
    var family by mutableStateOf(listOf<FamilyMember>()); private set
    var categories by mutableStateOf(listOf<Category>()); private set
    var items by mutableStateOf(listOf<Item>()); private set
    var expenses by mutableStateOf(listOf<Expense>()); private set
    var incomes by mutableStateOf(listOf<Income>()); private set
    var calcHistory by mutableStateOf(listOf<CalculatorEntry>()); private set
    var shoppingLists by mutableStateOf(listOf<ShoppingList>()); private set
    var theme by mutableStateOf("system"); private set
    var soundOn by mutableStateOf(true); private set
    var initialized by mutableStateOf(false); private set
    var archivedYears by mutableStateOf(setOf<String>()); private set
    var language by mutableStateOf("en"); private set

    init { viewModelScope.launch { load() } }

    private suspend fun load() {
        profile = db.profileDao().get()
        family = db.familyDao().all(); categories = db.categoryDao().all(); items = db.itemDao().all(); expenses = db.expenseDao().all(); incomes = db.incomeDao().all(); calcHistory = db.calcDao().all(); shoppingLists = db.shoppingDao().lists()
        seedIfNeeded(); val p = ctx.dataStore.data.first(); theme = p[stringPreferencesKey("theme")] ?: "system"; soundOn = p[booleanPreferencesKey("sound")] ?: true; archivedYears = p[stringSetPreferencesKey("archivedYears")] ?: emptySet(); language = p[stringPreferencesKey("language")] ?: "en"; BattukLocale.language = language; db.loanEmiDao().all().filter { it.active }.forEach { scheduleLoanReminder(ctx,it) }; initialized=true
    }
    private suspend fun seedIfNeeded() {
        if (categories.isEmpty()) listOf(
            "Weekly Market" to "WeeklyMarket","Kirana Store" to "KiranaStore","Clothes" to "Clothes","Shoes" to "Shoes","Toiletries" to "Toiletries","Milk Products" to "MilkProducts","Fast Food" to "FastFood","Entertainment" to "Entertainment","Loan/EMI" to "LoanEMI","Petrol/Diesel" to "Petrol","Other" to "Other"
        ).forEach { (n,t) -> db.categoryDao().insert(Category(name=n,type=t,icon=iconFor(t))) }
        if (items.isEmpty()) seedItems()
        categories = db.categoryDao().all(); items=db.itemDao().all()
    }
    private suspend fun seedItems() {
        fun row(e:String,m:String,s:String,u:String)=Item(nameEnglish=e,nameMarathi=m,subtype=s,defaultUnit=u)
        val rows = listOf(
            row("Onion","कांदा","Vegetable","kg"),row("Potato","बटाटा","Vegetable","kg"),row("Tomato","टोमॅटो","Vegetable","kg"),row("Brinjal","वांगी","Vegetable","kg"),row("Okra","भेंडी","Vegetable","kg"),row("Bitter Gourd","कारले","Vegetable","kg"),row("Bottle Gourd","दूधी भोपळा","Vegetable","pc"),row("Cabbage","कोबी","Vegetable","pc"),row("Cauliflower","फ्लॉवर","Vegetable","pc"),row("Coriander Leaves","कोथिंबीर","Vegetable","bunch"),row("Cucumber","काकडी","Vegetable","kg"),row("Capsicum","ढोबळी मिरची","Vegetable","kg"),
            row("Fenugreek","मेथी","LeafyGreen","bunch"),row("Spinach","पालक","LeafyGreen","bunch"),row("Amaranth","राजगिरा/तांदुळजा","LeafyGreen","bunch"),row("Sorrel","अंबाडी","LeafyGreen","bunch"),row("Dill","शेपू","LeafyGreen","bunch"),row("Colocasia Leaves","अळूची पाने","LeafyGreen","bunch"),row("Radish Greens","मुळ्याचा पाला","LeafyGreen","bunch"),row("Mint","पुदिना","LeafyGreen","bunch"),row("Curry Leaves","कढीपत्ता","LeafyGreen","bunch"),row("Spring Onion","कांद्याची पात","LeafyGreen","bunch"),
            row("Mango","आंबा","Fruit","kg"),row("Banana","केळी","Fruit","dozen"),row("Guava","पेरू","Fruit","kg"),row("Jackfruit","फणस","Fruit","kg"),row("Pomegranate","डाळिंब","Fruit","kg"),row("Custard Apple","सीताफळ","Fruit","kg"),row("Watermelon","कलिंगड","Fruit","pc"),row("Papaya","पपई","Fruit","pc"),row("Grapes","द्राक्षे","Fruit","kg"),row("Orange","संत्री","Fruit","kg"),row("Chikoo","चिकू","Fruit","kg"),row("Jambhul","जांभूळ","Fruit","kg")
        ); rows.forEach { db.itemDao().insert(it) }
    }

    fun saveProfile(name:String,dob:String?,photo:String?) = viewModelScope.launch { val x=Profile(name=name,dob=dob,photoUri=photo?.let(::persistUri)); db.profileDao().save(x); profile=x }
    fun addFamily(name:String,relation:String,dob:String?,photo:String?,income:Boolean) = viewModelScope.launch { db.familyDao().insert(FamilyMember(name=name,relation=relation,dob=dob,photoUri=photo,hasIncome=income)); family=db.familyDao().all() }
    fun addCategory(name:String) = viewModelScope.launch { db.categoryDao().insert(Category(name=name,type="Other",icon="📦")); categories=db.categoryDao().all() }
    fun addExpense(x:Expense) = viewModelScope.launch {
        val prepared = x.copy(receiptImageUri=x.receiptImageUri?.let(::persistUri))
        val budget = db.budgetDao().get(prepared.categoryId, prepared.date.take(7))
        val before = if (budget != null) db.expenseDao().sumForCategoryMonth(prepared.categoryId, prepared.date.take(7)) else 0.0
        db.expenseDao().insert(prepared)
        val after = before + prepared.amount
        if (budget != null && budget.alertEnabled && before <= budget.limitAmount && after > budget.limitAmount) {
            notifyBudgetExceeded(ctx, categories.firstOrNull{it.id==prepared.categoryId}?.name ?: "Category", after, budget.limitAmount)
        }
        expenses=db.expenseDao().all()
    }
    fun addIncome(x:Income) = viewModelScope.launch { db.incomeDao().insert(x); incomes=db.incomeDao().all() }
    fun addTrip(x:Trip, rows:List<Expense>) = viewModelScope.launch {
        val saved=x.copy(tripReceiptImageUri=x.tripReceiptImageUri?.let(::persistUri))
        val id=db.tripDao().insert(saved)
        rows.forEach { row ->
            val prepared=row.copy(tripId=id,receiptImageUri=saved.tripReceiptImageUri?.let(::persistUri))
            val budget=db.budgetDao().get(prepared.categoryId,prepared.date.take(7))
            val before=if(budget!=null) db.expenseDao().sumForCategoryMonth(prepared.categoryId,prepared.date.take(7)) else 0.0
            db.expenseDao().insert(prepared)
            val after=before+prepared.amount
            if(budget!=null && budget.alertEnabled && before<=budget.limitAmount && after>budget.limitAmount) notifyBudgetExceeded(ctx,categories.firstOrNull{it.id==prepared.categoryId}?.name ?: "Category",after,budget.limitAmount)
        }
        expenses=db.expenseDao().all()
    }
    fun addCalc(expr:String,res:String) = viewModelScope.launch { db.calcDao().insert(CalculatorEntry(expression=expr,result=res,timestamp=System.currentTimeMillis())); calcHistory=db.calcDao().all() }
    fun createShoppingList(name:String, itemNames:List<String>)=viewModelScope.launch { val id=db.shoppingDao().insertList(ShoppingList(name=name,createdDate=today())); itemNames.forEach{db.shoppingDao().insertItem(ShoppingListItem(listId=id,itemName=it))}; shoppingLists=db.shoppingDao().lists() }
    suspend fun shoppingItems(id:Long)=db.shoppingDao().items(id)
    fun addShoppingItem(listId:Long,itemName:String)=viewModelScope.launch { db.shoppingDao().insertItem(ShoppingListItem(listId=listId,itemName=itemName)); shoppingLists=db.shoppingDao().lists() }
    fun toggleShopping(i:ShoppingListItem)=viewModelScope.launch{db.shoppingDao().updateItem(i.copy(checked=!i.checked))}
    fun setTheme(v:String)=viewModelScope.launch { theme=v; ctx.dataStore.edit{it[stringPreferencesKey("theme")]=v} }
    fun setSound(v:Boolean)=viewModelScope.launch {soundOn=v;ctx.dataStore.edit{it[booleanPreferencesKey("sound")]=v}}
    fun setLanguage(v:String)=viewModelScope.launch { language=v; BattukLocale.language=v; ctx.dataStore.edit{it[stringPreferencesKey("language")]=v} }
    private fun persistUri(uri:String):String {
        return try {
            val source=Uri.parse(uri); val input=ctx.contentResolver.openInputStream(source) ?: return uri
            val dir=java.io.File(ctx.filesDir, "battuk_media").apply{mkdirs()}
            val file=java.io.File(dir, "${System.currentTimeMillis()}_${dir.listFiles()?.size ?: 0}.img")
            input.use { a -> file.outputStream().use { b -> a.copyTo(b) } }; file.toURI().toString()
        } catch (_:Exception) { uri }
    }
    fun archiveYear(year:String) = viewModelScope.launch {
        archivedYears = archivedYears + year
        ctx.dataStore.edit { it[stringSetPreferencesKey("archivedYears")] = archivedYears }
    }
    fun refresh()=viewModelScope.launch{expenses=db.expenseDao().all();family=db.familyDao().all();categories=db.categoryDao().all();incomes=db.incomeDao().all();calcHistory=db.calcDao().all();shoppingLists=db.shoppingDao().lists()}

    companion object { fun Factory(ctx:Context)=object:ViewModelProvider.Factory{override fun <T:ViewModel> create(c:Class<T>):T{ @Suppress("UNCHECKED_CAST") return BattukViewModel(ctx) as T }} }
}

private fun iconFor(t:String)=when(t){"WeeklyMarket"->"🥬";"KiranaStore"->"🛒";"Clothes"->"👕";"Shoes"->"👟";"Toiletries"->"🧴";"MilkProducts"->"🥛";"FastFood"->"🍴";"Entertainment"->"🎮";"LoanEMI"->"🏦";"Petrol"->"⛽";else->"📦"}
fun today()=SimpleDateFormat("yyyy-MM-dd",Locale.US).format(Date())
fun daysAgo(n:Int):String=SimpleDateFormat("yyyy-MM-dd",Locale.US).format(Date(System.currentTimeMillis()-n*86_400_000L))
private fun inPeriod(date:String,filter:String):Boolean { val start=when(filter){"Day"->today();"Week"->daysAgo(6);"Month"->daysAgo(30);"6 Months"->daysAgo(183);else->today().substring(0,4)+"-01-01"}; return date>=start && date<=today() }
private fun csvSafe(v:String)="\""+v.replace("\"","\"\"")+"\""
fun money(v:Double)="₹"+String.format(Locale.US,"%,.0f",v)

@Composable fun BattukApp(vm:BattukViewModel){
    if(!vm.initialized){Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){CircularProgressIndicator(color=Green)};return}
    val nav=rememberNavController(); val dark=when(vm.theme){"dark"->true;"light"->false;else->isSystemInDarkTheme()}
    MaterialTheme(colorScheme=if(dark)darkColorScheme(primary=Green,secondary=Orange) else lightColorScheme(primary=Green,secondary=Orange,surface=Color(0xFFF9FBF8))){
        NavHost(navController=nav,startDestination=if(vm.profile==null)"onboarding" else "home"){ 
            composable("onboarding"){Onboarding(vm){nav.navigate("family"){popUpTo("onboarding"){inclusive=true}}}}
            composable("family"){FamilyScreen(vm){nav.navigate("home"){popUpTo("family"){inclusive=true}}}}
            composable("home"){HomeScreen(vm,nav)}
            composable("single"){SingleEntryScreen(vm,nav)}
            composable("income"){IncomeEntryScreen(vm,nav)}
            composable("bulk"){BulkEntryScreen(vm,nav)}
            composable("shopping"){ShoppingScreen(vm,nav)}
            composable("categories"){CategoryBrowser(vm,nav)}
            composable("history/{id}"){back->{ItemHistory(vm,nav,back.arguments?.getString("id")?.toLongOrNull()?:1)}}
            composable("calendar"){CalendarScreen(vm,nav)}
            composable("calendarDay/{day}"){back->DayExpensesScreen(vm,nav,back.arguments?.getString("day")?:today())}
            composable("calculator"){CalculatorScreen(vm,nav)}
            composable("reports"){ReportsScreen(vm,nav)}
            composable("dashboard"){VisualDashboard(vm,nav)}
            composable("inflation"){InflationCalculator(vm,nav)}
            composable("receipts"){ReceiptsGallery(vm,nav)}
            composable("settings"){SettingsScreen(vm,nav)}
            composable("phase2"){Phase2Hub(vm,nav)}
            composable("budgets"){BudgetScreen(vm,nav)}
            composable("loans"){LoanEmiScreen(vm,nav)}
            composable("streaks"){StreakScreen(vm,nav)}
            composable("inflationModel"){InflationModelScreen(vm,nav)}
            composable("sync"){SyncScreen(vm,nav)}
            composable("language"){LanguageScreen(vm,nav)}
        }
    }
}

@Composable fun AppScaffold(vm:BattukViewModel,nav:NavHostController,title:String,content:@Composable ColumnScope.()->Unit){Scaffold(topBar={TopAppBar(title={BattukText(title,fontWeight=FontWeight.Bold)},navigationIcon={IconButton({nav.popBackStack()}){Icon(Icons.Default.ArrowBack,null)}},actions={ThemeButton(vm)} )},contentWindowInsets=WindowInsets.systemBars){p->Column(Modifier.fillMaxSize().padding(p).padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)} }
@Composable private fun ThemeButton(vm:BattukViewModel){var show by remember{mutableStateOf(false)};Box{IconButton({show=true}){Icon(if(vm.theme=="dark")Icons.Default.DarkMode else Icons.Default.LightMode,null)};DropdownMenu(expanded=show,onDismissRequest={show=false}){listOf("light" to "Light","dark" to "Dark","system" to "System").forEach{(v,l)->DropdownMenuItem({BattukText(l)},onClick={vm.setTheme(v);show=false})}}}}
@Composable private fun NavRow(nav:NavHostController,selected:String){NavigationBar{listOf("home" to Icons.Default.Home,"reports" to Icons.Default.Assessment,"calendar" to Icons.Default.CalendarMonth,"settings" to Icons.Default.MoreHoriz).forEach{(r,i)->NavigationBarItem(selected==r,onClick={nav.navigate(r)},icon={Icon(i,null)},label={BattukText(r.replaceFirstChar{it.uppercase()})})}}}

@Composable private fun Onboarding(vm:BattukViewModel,onDone:()->Unit){var name by remember{mutableStateOf("")};var dob by remember{mutableStateOf("")};var photo by remember{mutableStateOf<String?>(null)};val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){photo=it?.toString()};Column(Modifier.fillMaxSize().padding(22.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.SpaceEvenly){
    Column(horizontalAlignment=Alignment.CenterHorizontally){BattukText("🏠",fontSize=64.sp);BattukText("Welcome to Battuk",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold,color=DarkGreen);BattukText("Track your home expenses. Understand your spending patterns.",textAlign=TextAlign.Center,color=Ink)}
    Card(shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){OutlinedTextField(name,{name=it},label={BattukText("Name")},singleLine=true);OutlinedTextField(dob,{dob=it},label={BattukText("DOB (optional)")},singleLine=true);OutlinedButton({picker.launch("image/*")}){Icon(Icons.Default.PhotoCamera,null);Spacer(Modifier.width(6.dp));BattukText(if(photo==null)"Add profile photo" else "Photo selected")};Button(enabled=name.isNotBlank(),onClick={vm.saveProfile(name,dob.ifBlank{null},photo);onDone()},modifier=Modifier.fillMaxWidth()){BattukText("Get Started →")}}}
} }

@Composable private fun FamilyScreen(vm: BattukViewModel, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("Father") }
    var dob by remember { mutableStateOf("") }
    var income by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BattukText("Your Family", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        BattukText("Add family members who spend or earn.")
        vm.family.forEach {
            Card(shape = RoundedCornerShape(18.dp)) {
                ListItem(
                    headlineContent = { BattukText(it.name) },
                    supportingContent = { BattukText(it.relation) },
                    leadingContent = { BattukText("👤", fontSize = 22.sp) },
                    trailingContent = { if (it.hasIncome) BattukText("Income", color = Green, fontWeight = FontWeight.Bold) }
                )
            }
        }
        OutlinedTextField(name, { name = it }, label = { BattukText("Name") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            listOf("Father", "Mother", "Son", "Daughter").forEach { r -> FilterChip(selected = relation == r, onClick = { relation = r }, label = { BattukText(r) }) }
        }
        OutlinedTextField(dob, { dob = it }, label = { BattukText("DOB / age (optional)") }, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(income, { income = it })
            BattukText("Set as earning member")
        }
        Button(
            onClick = { if (name.isNotBlank()) { vm.addFamily(name, relation, dob.ifBlank { null }, null, income); name = "" } },
            modifier = Modifier.fillMaxWidth()
        ) { BattukText("Add Family Member") }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
        ) { BattukText("Continue to Battuk") }
    }
}

@Composable private fun HomeScreen(vm:BattukViewModel,nav:NavHostController){val total=vm.expenses.sumOf{it.amount};val inc=vm.incomes.sumOf{it.amount};var clock by remember{mutableStateOf(Date())};LaunchedEffect(Unit){while(true){clock=Date();kotlinx.coroutines.delay(1000)}};Scaffold(topBar={TopAppBar(title={Column{BattukText("Battuk",fontWeight=FontWeight.Bold);BattukText(SimpleDateFormat("EEE, dd MMM • hh:mm a",Locale.US).format(clock),style=MaterialTheme.typography.labelSmall)}},actions={ThemeButton(vm)})},bottomBar={NavRow(nav,"home")},floatingActionButton={FloatingActionButton(onClick={nav.navigate("calculator")}){Icon(Icons.Default.Calculate,null)}}){p->LazyColumn(Modifier.fillMaxSize().padding(p).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Card(shape=RoundedCornerShape(26.dp),colors=CardDefaults.cardColors(containerColor=SoftGreen)){Column(Modifier.padding(18.dp)){BattukText("Good day! 🌿",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);BattukText("Track today • Better tomorrow")}}};item{Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){MetricCard("Expenses",money(total),Color(0xFFFFECEB),Modifier.weight(1f));MetricCard("Income",money(inc),Color(0xFFE8F8EE),Modifier.weight(1f));MetricCard("Savings",money(inc-total),Color(0xFFEAF0FF),Modifier.weight(1f))}};item{BattukText("Quick actions",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)};item{Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(10.dp)){Action("Add Expense","single",Icons.Default.AddCard,nav);Action("Add Income","income",Icons.Default.Payments,nav);Action("Bulk Entry","bulk",Icons.Default.ShoppingCart,nav);Action("Remember","shopping",Icons.Default.ListAlt,nav);Action("Calendar","calendar",Icons.Default.CalendarMonth,nav)}};item{BattukText("Top categories",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)};item{TopCategories(vm)};item{BattukText("Recent activity",fontWeight=FontWeight.Bold,style=MaterialTheme.typography.titleMedium)};items(vm.expenses.take(6)){e->Card(shape=RoundedCornerShape(16.dp)){ListItem(headlineContent={BattukText(e.description ?: vm.items.firstOrNull{it.id==e.itemId}?.let{it.nameEnglish+" ("+it.nameMarathi+")"} ?: "Expense")},supportingContent={BattukText(e.date)},trailingContent={BattukText(money(e.amount),fontWeight=FontWeight.Bold)})}}}}
}
@Composable fun MetricCard(a:String,b:String,c:Color,mod:Modifier){Card(mod,shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=c)){Column(Modifier.padding(12.dp)){BattukText(a,style=MaterialTheme.typography.labelMedium);BattukText(b,fontWeight=FontWeight.Bold)}}}
@Composable private fun Action(label:String,route:String,icon:androidx.compose.ui.graphics.vector.ImageVector,nav:NavHostController){ElevatedButton({nav.navigate(route)},shape=RoundedCornerShape(18.dp)){Column(horizontalAlignment=Alignment.CenterHorizontally){Icon(icon,null);BattukText(label)}}}
@Composable private fun TopCategories(vm:BattukViewModel){val grouped=vm.expenses.groupBy{e->vm.categories.firstOrNull{it.id==e.categoryId}?.name ?: "Other"}.mapValues{it.value.sumOf{e->e.amount}}.toList().sortedByDescending{it.second}.take(5);Column(verticalArrangement=Arrangement.spacedBy(8.dp)){grouped.forEach{(n,v)->Row(verticalAlignment=Alignment.CenterVertically){BattukText(n,Modifier.width(130.dp));LinearProgressIndicator(progress={if(grouped.first().second==0.0)0f else (v/grouped.first().second).toFloat()},modifier=Modifier.weight(1f));Spacer(Modifier.width(8.dp));BattukText(money(v))}}}}

@Composable private fun SingleEntryScreen(vm:BattukViewModel,nav:NavHostController){var desc by remember{mutableStateOf("")};var amount by remember{mutableStateOf("")};var qty by remember{mutableStateOf("1")};var unit by remember{mutableStateOf("kg")};var cat by remember{mutableStateOf(vm.categories.firstOrNull()?.id?:1)};var member by remember{mutableStateOf<Long?>(vm.family.firstOrNull()?.id)};var date by remember{mutableStateOf(today())};var receipt by remember{mutableStateOf<String?>(null)};var showItems by remember{mutableStateOf(false)};val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){receipt=it?.toString()};AppScaffold(vm,nav,"Add Expense"){Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){FilterChip(selected=true,onClick={},label={BattukText("Single Entry")});FilterChip(selected=false,onClick={nav.navigate("bulk")},label={BattukText("Bulk Entry")})};Box{OutlinedTextField(desc,{desc=it},label={BattukText("Item / description")},modifier=Modifier.fillMaxWidth(),trailingIcon={IconButton({showItems=true}){Icon(Icons.Default.Search,null)}});DropdownMenu(showItems,{showItems=false}){vm.items.filter{it.nameEnglish.contains(desc,true)||it.nameMarathi.contains(desc)}.take(8).forEach{i->DropdownMenuItem({BattukText(i.nameEnglish+" ("+i.nameMarathi+")")},{desc=i.nameEnglish;unit=i.defaultUnit;showItems=false})}}};OutlinedTextField(amount,{amount=it.filter(Char::isDigit)},label={BattukText("Price (₹)")},modifier=Modifier.fillMaxWidth());Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(qty,{qty=it},label={BattukText("Quantity")},modifier=Modifier.weight(1f));OutlinedTextField(unit,{unit=it},label={BattukText("Unit")},modifier=Modifier.weight(1f))};DropdownField("Category",vm.categories.firstOrNull{it.id==cat}?.name?:("Other"),vm.categories.map{it.id to it.name}){cat=it};DropdownField("Family member",vm.family.firstOrNull{it.id==member}?.name?:("Me"),vm.family.map{it.id to it.name}){member=it};OutlinedTextField(date,{date=it},label={BattukText("Date")},modifier=Modifier.fillMaxWidth());OutlinedButton({picker.launch("image/*")},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.ReceiptLong,null);Spacer(Modifier.width(8.dp));BattukText(if(receipt==null)"Add Receipt Photo (Optional)" else "Receipt Selected")};OutlinedButton(onClick={nav.navigate("calculator")},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.Calculate,null);Spacer(Modifier.width(8.dp));BattukText("Open Calculator")}
    Button(enabled=amount.toDoubleOrNull()!=null,onClick={vm.addExpense(Expense(description=desc.ifBlank{null},categoryId=cat,amount=amount.toDouble(),quantity=qty.toDoubleOrNull()?:1.0,unit=unit,date=date,familyMemberId=member,receiptImageUri=receipt));nav.popBackStack()},modifier=Modifier.fillMaxWidth()){BattukText("Save Expense")}}
}

@Composable fun DropdownField(label:String,selected:String,opts:List<Pair<Long,String>>,onPick:(Long)->Unit){var open by remember{mutableStateOf(false)};Box{OutlinedButton({open=true},modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(14.dp)){Column(Modifier.fillMaxWidth()){BattukText(label,style=MaterialTheme.typography.labelSmall);BattukText(selected,fontWeight=FontWeight.SemiBold)}};DropdownMenu(open,{open=false}){opts.forEach{o->DropdownMenuItem({BattukText(o.second)},onClick={onPick(o.first);open=false})}}}}

@Composable private fun IncomeEntryScreen(vm: BattukViewModel, nav: NavHostController) {
    var member by remember { mutableStateOf<Long?>(vm.family.firstOrNull { it.hasIncome }?.id ?: vm.family.firstOrNull()?.id) }
    var source by remember { mutableStateOf("Salary") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(today()) }
    var note by remember { mutableStateOf("") }
    AppScaffold(vm, nav, "Add Income") {
        BattukText("Record income for a family member.")
        DropdownField("Family member", vm.family.firstOrNull { it.id == member }?.name ?: "Select member", vm.family.map { it.id to it.name }) { member = it }
        OutlinedTextField(source, { source = it }, label = { BattukText("Source / type") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(amount, { amount = it.filter(Char::isDigit) }, label = { BattukText("Amount (₹)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(date, { date = it }, label = { BattukText("Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(note, { note = it }, label = { BattukText("Note (optional)") }, modifier = Modifier.fillMaxWidth())
        OutlinedButton({ nav.navigate("calculator") }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Calculate, null); Spacer(Modifier.width(8.dp)); BattukText("Open Calculator") }
        Button(
            enabled = member != null && amount.toDoubleOrNull() != null,
            onClick = {
                vm.addIncome(Income(familyMemberId = member!!, amount = amount.toDouble(), source = source, date = date, note = note.ifBlank { null }))
                nav.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) { BattukText("Save Income") }
    }
}

@Composable private fun BulkEntryScreen(vm:BattukViewModel,nav:NavHostController){var store by remember{mutableStateOf("Weekly Market")};var receipt by remember{mutableStateOf<String?>(null)};var selected by remember{mutableStateOf(vm.items.take(5).map{it.id})};var amounts by remember{mutableStateOf(vm.items.take(5).associate{it.id to ""})};val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){receipt=it?.toString()};AppScaffold(vm,nav,"Bulk Entry"){BattukText("Add multiple items from one market/store run.");OutlinedTextField(store,{store=it},label={BattukText("Store name")},modifier=Modifier.fillMaxWidth());vm.items.take(10).forEach{i->val checked=selected.contains(i.id);Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable{selected=if(checked)selected-i.id else selected+i.id}.padding(8.dp),verticalAlignment=Alignment.CenterVertically){Checkbox(checked,{selected=if(it)selected+i.id else selected-i.id});Column(Modifier.weight(1f)){BattukText(i.nameEnglish,fontWeight=FontWeight.Medium);BattukText(i.nameMarathi,style=MaterialTheme.typography.labelSmall)};OutlinedTextField(amounts[i.id]?:"",{amounts=amounts.toMutableMap().also{m->m[i.id]=it}},label={BattukText("₹")},modifier=Modifier.width(100.dp))}};OutlinedButton({picker.launch("image/*")},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.ReceiptLong,null);Spacer(Modifier.width(8.dp));BattukText(if(receipt==null)"Attach Trip Receipt" else "Trip Receipt Selected")};val total=selected.sumOf{amounts[it]?.toDoubleOrNull()?:0.0};BattukText("Total: ${money(total)}",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);Button({val rows=selected.mapNotNull{id->amounts[id]?.toDoubleOrNull()?.let{Expense(itemId=id,categoryId=vm.categories.firstOrNull{it.name=="Weekly Market"}?.id?:1,amount=it,quantity=1.0,unit=vm.items.firstOrNull{it.id==id}?.defaultUnit?:"unit",date=today(),familyMemberId=vm.family.firstOrNull()?.id,storeName=store,receiptImageUri=receipt)} };if(rows.isNotEmpty())vm.addTrip(Trip(date=today(),storeName=store,tripReceiptImageUri=receipt),rows);nav.popBackStack()},modifier=Modifier.fillMaxWidth()){BattukText("Save Bulk Entry")}}

@Composable private fun ShoppingScreen(vm: BattukViewModel, nav: NavHostController) {
    var name by remember { mutableStateOf("This week's market list") }
    var newItem by remember { mutableStateOf("") }
    var listId by remember { mutableStateOf<Long?>(vm.shoppingLists.firstOrNull()?.id) }
    var rows by remember { mutableStateOf(listOf<ShoppingListItem>()) }
    LaunchedEffect(listId) { rows = listId?.let { vm.shoppingItems(it) } ?: emptyList() }
    AppScaffold(vm, nav, "Remember the Things") {
        BattukText("Plan before you shop. Tick items at the market, then convert them into Bulk Entry.")
        if (vm.shoppingLists.isNotEmpty()) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vm.shoppingLists.forEach { list ->
                    FilterChip(selected = listId == list.id, onClick = { listId = list.id }, label = { BattukText(list.name) })
                }
            }
        }
        OutlinedTextField(name, { name = it }, label = { BattukText("New list name") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(newItem, { newItem = it }, label = { BattukText("Add item") }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (newItem.isNotBlank()) {
                    val id = listId
                    if (id == null) vm.createShoppingList(name, listOf(newItem)) else vm.addShoppingItem(id, newItem)
                    newItem = ""
                }
            }) { BattukText("+") }
        }
        if (listId == null) BattukText("Create a list with an item to begin.")
        rows.forEach { row ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = row.checked,
                    onCheckedChange = {
                        vm.toggleShopping(row)
                        rows = rows.map { x -> if (x.id == row.id) x.copy(checked = !x.checked) else x }
                    }
                )
                BattukText(row.itemName, Modifier.weight(1f), fontWeight = if (row.checked) FontWeight.Normal else FontWeight.SemiBold)
            }
        }
        Button(onClick = { nav.navigate("bulk") }, modifier = Modifier.fillMaxWidth()) { BattukText("Convert Ticked List → Bulk Entry") }
    }
}

@Composable private fun CategoryBrowser(vm: BattukViewModel, nav: NavHostController) {
    var add by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Category?>(null) }
    AppScaffold(vm, nav, "Categories") {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.categories) { c ->
                Card(
                    Modifier.fillMaxWidth().clickable { selected = c },
                    shape = RoundedCornerShape(18.dp)
                ) {
                    ListItem(
                        headlineContent = { BattukText(c.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { BattukText(c.type) },
                        leadingContent = { BattukText(c.icon, fontSize = 28.sp) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) }
                    )
                }
            }
            item { Button(onClick = { add = true }, modifier = Modifier.fillMaxWidth()) { BattukText("+ Add Custom Category") } }
            if (selected != null) {
                item { BattukText("Choose an item", fontWeight = FontWeight.Bold) }
                if (selected?.type == "WeeklyMarket") {
                    items(vm.items) { i ->
                        ListItem(
                            headlineContent = { BattukText(i.nameEnglish) },
                            supportingContent = { BattukText("${i.nameMarathi} • ${i.defaultUnit}") },
                            leadingContent = { BattukText("🥬") },
                            modifier = Modifier.clickable { nav.navigate("history/${i.id}") }
                        )
                    }
                } else {
                    item { BattukText("For this category, add any item/description from Single Entry.") }
                }
            }
        }
    }
    if (add) TextInputDialog("New category", { vm.addCategory(it); add = false }, { add = false })
}

@Composable private fun ItemHistory(vm:BattukViewModel,nav:NavHostController,id:Long){val item=vm.items.firstOrNull{it.id==id};val points=vm.expenses.filter{it.itemId==id}.sortedBy{it.date}.takeLast(8);AppScaffold(vm,nav,"Item Price History"){BattukText(item?.nameEnglish+" ("+item?.nameMarathi+")",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold);BattukText("Simple local price trend from your purchases.");Card(shape=RoundedCornerShape(20.dp)){Column(Modifier.padding(16.dp)){BattukText("Price trend",fontWeight=FontWeight.Bold);LineChart(points.map{it.amount})};val latest=points.lastOrNull()?.amount?:0.0;val first=points.firstOrNull()?.amount?:latest;val change=if(first==0.0)0.0 else (latest-first)/first*100;BattukText("Current: ${money(latest)} / change ${if(change>=0)"+" else ""}${String.format(Locale.US,"%.1f",change)}%",fontWeight=FontWeight.Bold,color=if(change>=0)Color(0xFF0A8F54) else Color(0xFFD84E4E))}}
}
@Composable private fun LineChart(vals:List<Double>){Canvas(Modifier.fillMaxWidth().height(180.dp)){if(vals.isEmpty())return@Canvas;val maxV=max(1.0,vals.max());val minV=vals.min();val span=max(1.0,maxV-minV);val step=size.width/(vals.size-1).coerceAtLeast(1);var prev:androidx.compose.ui.geometry.Offset?=null;vals.forEachIndexed{idx,v->val p=androidx.compose.ui.geometry.Offset(idx*step,(size.height-((v-minV)/span).toFloat()*size.height)); if(prev!=null)drawLine(Green,prev!!,p,strokeWidth=6f,cap=StrokeCap.Round);drawCircle(Green,8f,p);prev=p}}}

@Composable private fun CalendarScreen(vm:BattukViewModel,nav:NavHostController){val cal=Calendar.getInstance();val year=cal.get(Calendar.YEAR);val month=cal.get(Calendar.MONTH);val days=cal.getActualMaximum(Calendar.DAY_OF_MONTH);val monthName=SimpleDateFormat("MMMM yyyy",Locale.US).format(cal.time);AppScaffold(vm,nav,"Mini Calendar"){BattukText(monthName,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold);Column{(1..days).chunked(7).forEach{week->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){week.forEach{d->val date=String.format(Locale.US,"%04d-%02d-%02d",year,month+1,d);val has=vm.expenses.any{it.date==date};Button(onClick={nav.navigate("calendarDay/$date")},shape=CircleShape,colors=ButtonDefaults.buttonColors(containerColor=if(has)SoftGreen else MaterialTheme.colorScheme.surface)){BattukText("$d")}}}}};Spacer(Modifier.height(8.dp));BattukText("Days with expenses are highlighted.")}}

@Composable private fun DayExpensesScreen(vm:BattukViewModel,nav:NavHostController,day:String){ val rows=vm.expenses.filter{it.date==day}; AppScaffold(vm,nav,"Day Expenses"){BattukText(day,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);if(rows.isEmpty())BattukText("No expenses logged for today yet.") else rows.forEach{e->ListItem(headlineContent={BattukText(e.description ?: vm.items.firstOrNull{it.id==e.itemId}?.nameEnglish ?: "Expense")},supportingContent={BattukText(vm.categories.firstOrNull{it.id==e.categoryId}?.name ?: "Other")},trailingContent={BattukText(money(e.amount),fontWeight=FontWeight.Bold)})}}}
}

@Composable private fun CalculatorScreen(vm:BattukViewModel,nav:NavHostController){var expr by remember{mutableStateOf("")};var result by remember{mutableStateOf("")};AppScaffold(vm,nav,"Calculator"){OutlinedTextField(expr,{expr=it},label={BattukText("Expression")},modifier=Modifier.fillMaxWidth(),singleLine=true);BattukText(if(result.isBlank())"0" else result,style=MaterialTheme.typography.displaySmall,fontWeight=FontWeight.Bold,modifier=Modifier.fillMaxWidth(),textAlign=TextAlign.End);Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("7","8","9","÷","4","5","6","×","1","2","3","-","0",".","+","=").forEach{b->Button({if(b=="="){runCatching{result=evalExpression(expr).toString();vm.addCalc(expr,result)}}else{expr+=when(b){"×"->"*";"÷"->"/";else->b}}},Modifier.weight(1f)){BattukText(b)}}};BattukText("History",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);vm.calcHistory.take(10).forEach{ListItem(headlineContent={BattukText(it.result,fontWeight=FontWeight.Bold)},supportingContent={BattukText(it.expression)},trailingContent={IconButton({expr=it.result}){Icon(Icons.Default.ContentCopy,null)}})};Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(
    onClick={(nav.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newPlainText("Battuk result",result))},
    enabled=result.isNotBlank()
){BattukText("Copy Result")};Button(
    onClick={nav.previousBackStackEntry?.savedStateHandle?.set("pastedAmount",result);nav.popBackStack()},
    enabled=result.isNotBlank()
){BattukText("Paste to Previous Amount")}}}
}
private fun evalExpression(s: String): Double {
    val text = s.replace("×", "*").replace("÷", "/").replace(" ", "")
    class Parser(private val str: String) {
        var i = 0
        fun parse(): Double { val v = parseExpr(); require(i == str.length) { "Invalid expression" }; return v }
        fun parseExpr(): Double {
            var v = parseTerm()
            while (i < str.length) {
                when (str[i]) {
                    '+' -> { i++; v += parseTerm() }
                    '-' -> { i++; v -= parseTerm() }
                    else -> return v
                }
            }
            return v
        }
        fun parseTerm(): Double {
            var v = parseFactor()
            while (i < str.length) {
                when (str[i]) {
                    '*' -> { i++; v *= parseFactor() }
                    '/' -> { i++; v /= parseFactor() }
                    '%' -> { i++; v %= parseFactor() }
                    else -> return v
                }
            }
            return v
        }
        fun parseFactor(): Double {
            if (i < str.length && str[i] == '-') { i++; return -parseFactor() }
            if (i < str.length && str[i] == '(') {
                i++; val v = parseExpr(); require(i < str.length && str[i] == ')') { "Missing )" }; i++; return v
            }
            val begin = i
            while (i < str.length && (str[i].isDigit() || str[i] == '.')) i++
            require(begin != i) { "Number expected" }
            return str.substring(begin, i).toDouble()
        }
    }
    return Parser(text).parse()
}

@Composable private fun ReportsScreen(vm: BattukViewModel, nav: NavHostController) {
    var filter by remember { mutableStateOf("Month") }
    val filteredExpenses = vm.expenses.filter { inPeriod(it.date, filter) }
    val filteredIncome = vm.incomes.filter { inPeriod(it.date, filter) }
    val expense = filteredExpenses.sumOf { it.amount }
    val income = filteredIncome.sumOf { it.amount }
    AppScaffold(vm, nav, "Reports") {
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Day", "Week", "Month", "6 Months", "Year").forEach { f -> FilterChip(selected = filter == f, onClick = { filter = f }, label = { BattukText(f) }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard("Expenses", money(expense), Color(0xFFFFECEB), Modifier.weight(1f))
            MetricCard("Income", money(income), SoftGreen, Modifier.weight(1f))
            MetricCard("Savings", money(income - expense), Color(0xFFEAF0FF), Modifier.weight(1f))
        }
        BattukText("Expenses by category", fontWeight = FontWeight.Bold)
        DonutChartValues(filteredExpenses)
        BattukText("Family member spend", fontWeight = FontWeight.Bold)
        vm.family.forEach { f ->
            val spent = filteredExpenses.filter { it.familyMemberId == f.id }.sumOf { it.amount }
            ListItem(headlineContent = { BattukText(f.name) }, trailingContent = { BattukText(money(spent), fontWeight = FontWeight.Bold) })
        }
        Button(onClick = { nav.navigate("dashboard") }, modifier = Modifier.fillMaxWidth()) { BattukText("Open Visual Dashboard") }
    }
}
@Composable private fun DonutChartValues(expenses: List<Expense>){val vals=expenses.groupBy{it.categoryId}.values.map{it.sumOf{e->e.amount}};Canvas(Modifier.fillMaxWidth().height(180.dp)){val total=vals.sum().coerceAtLeast(1.0);var start=-90f;vals.forEachIndexed{idx,v->val sweep=(v/total*360).toFloat();drawArc(listOf(Green,Orange,Color(0xFF6D8CF5),Color(0xFFE05D9A),Color(0xFFF5C04F))[idx%5],start,sweep,false,style=Stroke(32f));start+=sweep}}}

@Composable private fun VisualDashboard(vm: BattukViewModel, nav: NavHostController) {
    val current = vm.expenses.filter { it.date >= daysAgo(6) }.sumOf { it.amount }
    val previous = vm.expenses.filter { it.date in daysAgo(13)..daysAgo(7) }.sumOf { it.amount }
    AppScaffold(vm, nav, "Visual Dashboard") {
        BattukText("Your spending patterns at a glance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        BattukText("Spend per family member", fontWeight = FontWeight.Bold)
        BarChart(vm.family.map { f -> f.name to vm.expenses.filter { it.familyMemberId == f.id }.sumOf { e -> e.amount } })
        BattukText("This week vs previous week", fontWeight = FontWeight.Bold)
        BarChart(listOf("This week" to current, "Previous" to previous))
        BattukText("Top categories", fontWeight = FontWeight.Bold)
        TopCategories(vm)
        BattukText("Inflation insights", fontWeight = FontWeight.Bold)
        vm.items.firstOrNull { item -> vm.expenses.any { it.itemId == item.id } }?.let { item ->
            Card(shape = RoundedCornerShape(18.dp)) {
                ListItem(
                    headlineContent = { BattukText("${item.nameEnglish} (${item.nameMarathi})") },
                    supportingContent = { BattukText("Based on your local purchase history") },
                    trailingContent = { Button({ nav.navigate("inflation") }) { BattukText("Calculate") } }
                )
            }
        }
    }
}
@Composable private fun BarChart(data:List<Pair<String,Double>>){Canvas(Modifier.fillMaxWidth().height(180.dp)){val maxV=max(1.0,data.maxOfOrNull{it.second}?:0.0);val gap=size.width/(data.size.coerceAtLeast(1)*2);data.forEachIndexed{idx,(_,v)->val x=gap+idx*gap*2;val h=(v/maxV).toFloat()*size.height*.75f;drawRoundRect(Green, topLeft=androidx.compose.ui.geometry.Offset(x,size.height-h), size=androidx.compose.ui.geometry.Size(gap.toFloat(),h), cornerRadius=androidx.compose.ui.geometry.CornerRadius(14f));}}}

@Composable private fun InflationCalculator(vm: BattukViewModel, nav: NavHostController) {
    var id by remember { mutableStateOf(vm.items.firstOrNull()?.id ?: 1) }
    var result by remember { mutableStateOf<Double?>(null) }
    AppScaffold(vm, nav, "Inflation Calculator") {
        DropdownField(
            "Product",
            vm.items.firstOrNull { it.id == id }?.nameEnglish ?: "Onion",
            vm.items.map { it.id to "${it.nameEnglish} (${it.nameMarathi})" }
        ) { id = it }
        Button(onClick = {
            val points = vm.expenses.filter { it.itemId == id }.sortedBy { it.date }
            val first = points.firstOrNull()?.amount ?: 0.0
            val last = points.lastOrNull()?.amount ?: first
            result = if (first == 0.0) 0.0 else (last - first) / first * 100.0
        }, modifier = Modifier.fillMaxWidth()) { BattukText("Calculate") }
        result?.let {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SoftGreen)) {
                Column(Modifier.padding(18.dp)) {
                    BattukText("Price change", fontWeight = FontWeight.Bold)
                    BattukText("${if (it >= 0) "+" else ""}${String.format(Locale.US, "%.1f", it)}%", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    BattukText("Calculated from your recorded purchase history.")
                }
            }
        }
    }
}

@Composable private fun ReceiptsGallery(vm: BattukViewModel, nav: NavHostController) {
    val receipts = vm.expenses.filter { !it.receiptImageUri.isNullOrBlank() }
    AppScaffold(vm, nav, "Receipts Gallery") {
        if (receipts.isEmpty()) BattukText("No receipts saved yet. Attach one while adding an expense.")
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(receipts) { e ->
                Card(shape = RoundedCornerShape(18.dp)) {
                    Column {
                        e.receiptImageUri?.let { AsyncImage(model = it, contentDescription = "Receipt", modifier = Modifier.fillMaxWidth().height(180.dp)) }
                        ListItem(
                            headlineContent = { BattukText(vm.items.firstOrNull { it.id == e.itemId }?.nameEnglish ?: e.description ?: "Expense") },
                            supportingContent = { BattukText("${e.date} • ${money(e.amount)}") },
                            leadingContent = { BattukText("🧾", fontSize = 28.sp) }
                        )
                    }
                }
            }
        }
    }
}
@Composable private fun SettingsScreen(vm: BattukViewModel, nav: NavHostController) {
    var manageYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR).toString()) }
    val yearRows = vm.expenses.filter { it.date.startsWith(manageYear) }
    AppScaffold(vm, nav, "Settings") {
        BattukText("Battuk", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        ListItem(headlineContent = { BattukText("Theme") }, supportingContent = { BattukText(vm.theme.replaceFirstChar { it.uppercase() }) }, leadingContent = { Icon(Icons.Default.Palette, null) }, trailingContent = { ThemeButton(vm) })
        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.VolumeUp, null); BattukText("Sound effects", Modifier.weight(1f).padding(start = 12.dp)); Switch(vm.soundOn, { vm.setSound(it) }) }
        BattukText("Manage by Year", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(manageYear, { manageYear = it }, label = { BattukText("Year") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = SoftGreen)) {
            Column(Modifier.padding(16.dp)) {
                BattukText("${yearRows.size} expenses • ${money(yearRows.sumOf { it.amount })}", fontWeight = FontWeight.Bold)
                BattukText(if (vm.archivedYears.contains(manageYear)) "Archived locally" else "Active local records")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Button({ exportCsv(vm, nav.context, manageYear) }, modifier = Modifier.weight(1f)) { BattukText("Export CSV") }
            OutlinedButton({ vm.archiveYear(manageYear) }, modifier = Modifier.weight(1f), enabled = !vm.archivedYears.contains(manageYear)) { BattukText("Archive Year") }
        }
        ListItem(headlineContent = { BattukText("Phase 2") }, supportingContent = { BattukText("Advanced Features") }, leadingContent = { Icon(Icons.Default.AutoAwesome, null, tint = Green) }, modifier = Modifier.clickable { nav.navigate("phase2") })
        ListItem(headlineContent = { BattukText("Currency") }, supportingContent = { BattukText("Indian Rupees (₹) — fixed") }, leadingContent = { Icon(Icons.Default.CurrencyRupee, null) })
        ListItem(headlineContent = { BattukText("Family Members") }, supportingContent = { BattukText("Manage your family") }, leadingContent = { Icon(Icons.Default.Group, null) }, modifier = Modifier.clickable { nav.navigate("family") })
        ListItem(headlineContent = { BattukText("Categories") }, supportingContent = { BattukText("Manage categories") }, leadingContent = { Icon(Icons.Default.Category, null) }, modifier = Modifier.clickable { nav.navigate("categories") })
        ListItem(headlineContent = { BattukText("Receipts Gallery") }, supportingContent = { BattukText("Browse local receipt photos") }, leadingContent = { Icon(Icons.Default.ReceiptLong, null) }, modifier = Modifier.clickable { nav.navigate("receipts") })
    }
}
private fun exportCsv(vm: BattukViewModel, ctx: Context, year: String) {
    val rows = vm.expenses.filter { it.date.startsWith(year) }
    val csv = buildString {
        append("date,item,category,amount,quantity,unit,familyMember,store\n")
        rows.forEach { e ->
            append(listOf(
                csvSafe(e.date),
                csvSafe(e.itemId?.let { id -> vm.items.firstOrNull { it.id == id }?.nameEnglish } ?: e.description ?: ""),
                csvSafe(vm.categories.firstOrNull { it.id == e.categoryId }?.name ?: ""),
                e.amount, e.quantity, csvSafe(e.unit),
                csvSafe(vm.family.firstOrNull { it.id == e.familyMemberId }?.name ?: ""),
                csvSafe(e.storeName ?: "")
            ).joinToString(",") + "\n")
        }
    }
    runCatching {
        val file = java.io.File(ctx.cacheDir, "battuk_${year}_data.csv")
        file.writeText(csv)
        val uri = androidx.core.content.FileProvider.getUriForFile(ctx, ctx.packageName + ".fileprovider", file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Battuk $year data.csv")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(send, "Export Battuk CSV"))
    }
}
