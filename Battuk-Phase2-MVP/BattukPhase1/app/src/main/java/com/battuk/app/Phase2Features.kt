package com.battuk.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.battuk.app.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun phase2Money(v:Double)="₹"+String.format(Locale.US,"%,.0f",v)
private fun monthKey(date:String = today()) = date.take(7)
private fun nextDueForDay(day:Int, base:LocalDate = LocalDate.now()):LocalDate {
    val d = day.coerceIn(1, 31)
    val ym = YearMonth.from(base)
    val candidate = ym.atDay(d.coerceAtMost(ym.lengthOfMonth()))
    return if (candidate >= base) candidate else {
        val next = ym.plusMonths(1)
        next.atDay(d.coerceAtMost(next.lengthOfMonth()))
    }
}

@Composable
fun Phase2Hub(vm:BattukViewModel, nav:NavHostController) {
    AppScaffold(vm, nav, "Phase 2") {
        BattukText("Advanced Features", style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Bold)
        BattukText("Build stronger money habits with budgets, reminders, insights and backup.")
        Phase2NavCard("Budgets", "Set category limits and get alerts", Icons.Default.Savings) { nav.navigate("budgets") }
        Phase2NavCard("Loans / EMI", "Schedule due-date reminders", Icons.Default.AccountBalance) { nav.navigate("loans") }
        Phase2NavCard("Streaks & Badges", "Reward consistent daily logging", Icons.Default.EmojiEvents) { nav.navigate("streaks") }
        Phase2NavCard("Inflation Model", "Weighted prices with seasonal view", Icons.Default.TrendingUp) { nav.navigate("inflationModel") }
        Phase2NavCard("Cloud Backup & Sync", "Save/import a complete sync package", Icons.Default.CloudSync) { nav.navigate("sync") }
        Phase2NavCard("Language", "English / Marathi for the whole UI", Icons.Default.Language) { nav.navigate("language") }
    }
}

@Composable private fun Phase2NavCard(title:String, subtitle:String, icon:androidx.compose.ui.graphics.vector.ImageVector, onClick:()->Unit) {
    Card(onClick=onClick, shape=RoundedCornerShape(20.dp)) {
        ListItem(
            headlineContent={BattukText(title,fontWeight=FontWeight.Bold)},
            supportingContent={BattukText(subtitle)},
            leadingContent={Icon(icon,null,color=Green)},
            trailingContent={Icon(Icons.Default.ChevronRight,null)}
        )
    }
}

@Composable
fun BudgetScreen(vm:BattukViewModel, nav:NavHostController) {
    val scope=rememberCoroutineScope()
    var month by remember { mutableStateOf(monthKey()) }
    var selectedCategory by remember { mutableStateOf(vm.categories.firstOrNull()?.id ?: 1L) }
    var limit by remember { mutableStateOf("") }
    var rows by remember { mutableStateOf(listOf<Budget>()) }
    var message by remember { mutableStateOf("") }
    fun reload() { scope.launch { rows=vm.db.budgetDao().forMonth(month) } }
    LaunchedEffect(month) { rows=vm.db.budgetDao().forMonth(month) }
    AppScaffold(vm,nav,"Budgets") {
        OutlinedTextField(month,{month=it},label={BattukText("Month (YYYY-MM)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        DropdownField("Category",vm.categories.firstOrNull{it.id==selectedCategory}?.name ?: "Other",vm.categories.map{it.id to it.name}){ selectedCategory=it }
        OutlinedTextField(limit,{limit=it.filter{c->c.isDigit()||c=='.'}},label={BattukText("Budget limit (₹)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Button(enabled=limit.toDoubleOrNull()!=null,onClick={
            scope.launch {
                val newLimit=limit.toDouble(); vm.db.budgetDao().upsert(Budget(categoryId=selectedCategory,monthKey=month,limitAmount=newLimit,alertEnabled=true)); val alreadySpent=vm.expenses.filter{it.categoryId==selectedCategory && it.date.startsWith(month)}.sumOf{it.amount}; if(alreadySpent>newLimit) notifyBudgetExceeded(nav.context,vm.categories.firstOrNull{it.id==selectedCategory}?.name ?: "Category",alreadySpent,newLimit); message="Budget saved."; reload()
            }
        },modifier=Modifier.fillMaxWidth()){BattukText("Add Budget")}
        if(message.isNotBlank()) BattukText(message,color=Green,fontWeight=FontWeight.Bold)
        BattukText("Current month budgets",fontWeight=FontWeight.Bold)
        if(rows.isEmpty()) BattukText("No category budgets set for $month.")
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()) {
            items(rows) { b ->
                val category=vm.categories.firstOrNull{it.id==b.categoryId}?.name ?: "Other"
                val spent=vm.expenses.filter{it.categoryId==b.categoryId && it.date.startsWith(b.monthKey)}.sumOf{it.amount}
                val progress=(spent/b.limitAmount.coerceAtLeast(1.0)).coerceIn(0.0,1.0).toFloat()
                Card(shape=RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){BattukText(category,fontWeight=FontWeight.Bold);BattukText("${phase2Money(spent)} / ${phase2Money(b.limitAmount)}")}
                        LinearProgressIndicator(progress={progress},modifier=Modifier.fillMaxWidth())
                        BattukText(if(spent>b.limitAmount)"Budget exceeded" else "Remaining ${phase2Money(b.limitAmount-spent)}")
                    }
                }
            }
        }
    }
}

@Composable
fun LoanEmiScreen(vm:BattukViewModel, nav:NavHostController) {
    val scope=rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var lender by remember { mutableStateOf("") }
    var principal by remember { mutableStateOf("") }
    var emi by remember { mutableStateOf("") }
    var dueDay by remember { mutableStateOf("5") }
    var note by remember { mutableStateOf("") }
    var loans by remember { mutableStateOf(listOf<LoanEmi>()) }
    fun reload() { scope.launch { loans=vm.db.loanEmiDao().all() } }
    LaunchedEffect(Unit) { loans=vm.db.loanEmiDao().all() }
    AppScaffold(vm,nav,"Loans / EMI") {
        BattukText("Schedule EMI due-date reminders on this device.")
        OutlinedTextField(title,{title=it},label={BattukText("Loan / EMI name")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        OutlinedTextField(lender,{lender=it},label={BattukText("Lender")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()) {
            OutlinedTextField(principal,{principal=it},label={BattukText("Principal (₹)")},modifier=Modifier.weight(1f),singleLine=true)
            OutlinedTextField(emi,{emi=it},label={BattukText("EMI (₹)")},modifier=Modifier.weight(1f),singleLine=true)
        }
        OutlinedTextField(dueDay,{dueDay=it.filter(Char::isDigit)},label={BattukText("Due day")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        OutlinedTextField(note,{note=it},label={BattukText("Note (optional)")},modifier=Modifier.fillMaxWidth())
        Button(enabled=title.isNotBlank() && lender.isNotBlank() && principal.toDoubleOrNull()!=null && emi.toDoubleOrNull()!=null && dueDay.toIntOrNull() in 1..31,onClick={
            scope.launch {
                val d=dueDay.toInt()
                val loan=LoanEmi(title=title,lender=lender,principalAmount=principal.toDouble(),emiAmount=emi.toDouble(),dueDay=d,nextDueDate=nextDueForDay(d).toString(),note=note.ifBlank{null})
                val id=vm.db.loanEmiDao().insert(loan)
                scheduleLoanReminder(nav.context,loan.copy(id=id))
                title=""; lender=""; principal=""; emi=""; note=""; reload()
            }
        },modifier=Modifier.fillMaxWidth()){BattukText("Add Loan / EMI")}
        if(loans.isNotEmpty()) BattukText("Scheduled EMIs",fontWeight=FontWeight.Bold)
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()) {
            items(loans) { loan ->
                Card(shape=RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){BattukText(loan.title,fontWeight=FontWeight.Bold);BattukText(phase2Money(loan.emiAmount),fontWeight=FontWeight.Bold)}
                        BattukText("${loan.lender} • ${loan.nextDueDate}")
                        if(loan.note!=null) BattukText(loan.note!!,style=MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                            Button(onClick={scope.launch{val next=nextDueForDay(loan.dueDay,LocalDate.parse(loan.nextDueDate).plusDays(1));val updated=loan.copy(nextDueDate=next.toString());vm.db.loanEmiDao().update(updated);scheduleLoanReminder(nav.context,updated);reload()} }){BattukText("Mark paid / next month")}
                            OutlinedButton(onClick={scope.launch{val updated=loan.copy(active=!loan.active);vm.db.loanEmiDao().update(updated);if(updated.active)scheduleLoanReminder(nav.context,updated);reload()}}){BattukText(if(loan.active)"Pause" else "Resume")}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreakScreen(vm:BattukViewModel, nav:NavHostController) {
    val current=currentStreak(vm.expenses.map{it.date}.toSet())
    val best=bestStreak(vm.expenses.map{it.date}.toSet())
    val badges=listOf(
        1 to "First log",3 to "3-day starter",7 to "7-day streak",30 to "30-day champion"
    )
    AppScaffold(vm,nav,"Streaks & Badges") {
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()) {
            MetricCard("Current streak","$current days",Color(0xFFEAF8EE),Modifier.weight(1f))
            MetricCard("Best streak","$best days",Color(0xFFFFF2DD),Modifier.weight(1f))
        }
        BattukText("Badges",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)
        badges.forEach { (need,name) ->
            val unlocked=best>=need
            Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=if(unlocked)SoftGreen else MaterialTheme.colorScheme.surfaceVariant)) {
                ListItem(headlineContent={BattukText(if(unlocked)"🏆 $name" else "🔒 $name",fontWeight=FontWeight.Bold)},supportingContent={BattukText("Log expenses on $need consecutive ${if(need==1)"day" else "days"}.")})
            }
        }
        BattukText("Streaks use the dates of your logged expenses only; no account or cloud is required.",style=MaterialTheme.typography.bodySmall)
    }
}

private fun currentStreak(dates:Set<String>):Int {
    if(dates.isEmpty()) return 0
    var day=LocalDate.now()
    if(!dates.contains(day.toString())) day=day.minusDays(1)
    var count=0
    while(dates.contains(day.toString())) { count++; day=day.minusDays(1) }
    return count
}
private fun bestStreak(dates:Set<String>):Int {
    if(dates.isEmpty()) return 0
    val all=dates.mapNotNull{runCatching{LocalDate.parse(it)}.getOrNull()}.sorted()
    var best=1;var run=1
    for(i in 1 until all.size){if(all[i]==all[i-1].plusDays(1)){run++;best=maxOf(best,run)}else if(all[i]!=all[i-1])run=1}
    return best
}

@Composable
fun InflationModelScreen(vm:BattukViewModel, nav:NavHostController) {
    val scope=rememberCoroutineScope()
    var mode by remember{mutableStateOf("Product")}
    var itemId by remember{mutableStateOf(vm.items.firstOrNull()?.id ?: 1L)}
    var categoryId by remember{mutableStateOf(vm.categories.firstOrNull()?.id ?: 1L)}
    var from by remember{mutableStateOf(if(vm.expenses.isNotEmpty()) vm.expenses.minOf{it.date} else daysAgo(183))}
    var to by remember{mutableStateOf(today())}
    var seasonal by remember{mutableStateOf(true)}
    var result by remember{mutableStateOf<Double?>(null)}
    var detail by remember{mutableStateOf("")}
    AppScaffold(vm,nav,"Inflation Model") {
        Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            listOf("Product","Category").forEach{m->FilterChip(selected=mode==m,onClick={mode=m},label={BattukText(m)})}
        }
        if(mode=="Product") DropdownField("Product",vm.items.firstOrNull{it.id==itemId}?.let{"${it.nameEnglish} (${it.nameMarathi})"} ?: "Product",vm.items.map{it.id to "${it.nameEnglish} (${it.nameMarathi})"}){itemId=it}
        else DropdownField("Category",vm.categories.firstOrNull{it.id==categoryId}?.name ?: "Other",vm.categories.map{it.id to it.name}){categoryId=it}
        OutlinedTextField(from,{from=it},label={BattukText("From (YYYY-MM-DD)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        OutlinedTextField(to,{to=it},label={BattukText("To (YYYY-MM-DD)")},modifier=Modifier.fillMaxWidth(),singleLine=true)
        Row(horizontalArrangement=Arrangement.SpaceBetween,modifier=Modifier.fillMaxWidth()) { BattukText("Seasonal adjustment"); Switch(seasonal,{seasonal=it}) }
        Button(onClick={scope.launch {
            val answer=if(mode=="Product") computeProductInflation(vm,itemId,from,to,seasonal) else computeCategoryInflation(vm,categoryId,from,to,seasonal)
            result=answer.first; detail=answer.second
        }},modifier=Modifier.fillMaxWidth()){BattukText("Calculate")}
        result?.let{value->Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=SoftGreen)){Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){BattukText("Weighted average",fontWeight=FontWeight.Bold);BattukText("${if(value>=0)"+" else ""}${String.format(Locale.US,"%.1f",value)}%",style=MaterialTheme.typography.displaySmall,fontWeight=FontWeight.Bold);BattukText(detail)}}}
        BattukText("The model weights product observations by quantity. Category mode weights item-level price changes by baseline spend. Seasonal adjustment uses the selected period's month-of-year history when enough data exists.",style=MaterialTheme.typography.bodySmall)
    }
}

private suspend fun computeProductInflation(vm:BattukViewModel,id:Long,from:String,to:String,seasonal:Boolean):Pair<Double,String>{
    val rows=vm.expenses.filter{it.itemId==id && it.date>=from && it.date<=to && it.quantity>0}.sortedBy{it.date}
    if(rows.size<2)return 0.0 to "Not enough purchase history in this period."
    val first=weightedUnitPrice(rows.take(rows.size.coerceAtMost(3)))
    var last=weightedUnitPrice(rows.takeLast(rows.size.coerceAtMost(3)))
    var factor=1.0
    if(seasonal){factor=seasonFactorForItem(vm,id,to);if(factor>0)last/=factor}
    val pct=if(first==0.0)0.0 else (last-first)/first*100.0
    return pct to "${rows.size} observations • baseline ${phase2Money(first)}/unit • latest adjusted ${phase2Money(last)}/unit • seasonal factor ${String.format(Locale.US,"%.2f",factor)}"
}

private suspend fun computeCategoryInflation(vm:BattukViewModel,categoryId:Long,from:String,to:String,seasonal:Boolean):Pair<Double,String>{
    val rows=vm.expenses.filter{it.categoryId==categoryId && it.date>=from && it.date<=to && it.itemId!=null && it.quantity>0}
    val grouped=rows.groupBy{it.itemId!!}
    data class Change(val pct:Double,val weight:Double)
    val changes=grouped.mapNotNull{(id,list)->val sorted=list.sortedBy{it.date};if(sorted.size<2)null else{val base=weightedUnitPrice(sorted.take(3));var latest=weightedUnitPrice(sorted.takeLast(3));var factor=1.0;if(seasonal){factor=seasonFactorForItem(vm,id,to);if(factor>0)latest/=factor};if(base==0.0)null else Change((latest-base)/base*100.0,base*sorted.take(3).sumOf{it.quantity})}}
    if(changes.isEmpty())return 0.0 to "Not enough item-level history in this category."
    val weightTotal=changes.sumOf{it.weight}.coerceAtLeast(1.0)
    val pct=changes.sumOf{it.pct*it.weight}/weightTotal
    return pct to "${changes.size} items contributed • category change is weighted by baseline spend proxy${if(seasonal)" with seasonal normalization" else ""}."
}

private fun weightedUnitPrice(rows:List<Expense>):Double { val q=rows.sumOf{it.quantity}; return if(q==0.0)0.0 else rows.sumOf{it.amount}/q }
private fun seasonFactorForItem(vm:BattukViewModel,id:Long,endDate:String):Double {
    val month=runCatching{LocalDate.parse(endDate).monthValue}.getOrElse{LocalDate.now().monthValue}
    val history=vm.expenses.filter{it.itemId==id && it.quantity>0}.mapNotNull{e->runCatching{LocalDate.parse(e.date)}.getOrNull()?.let{d->d.monthValue to (e.amount/e.quantity)}}
    if(history.size<3)return 1.0
    val monthAvg=history.filter{it.first==month}.map{it.second}.average()
    val overall=history.map{it.second}.average()
    if(monthAvg>0 && overall>0) monthAvg/overall else 1.0
}

@Composable
fun SyncScreen(vm:BattukViewModel, nav:NavHostController) {
    val scope=rememberCoroutineScope()
    var status by remember{mutableStateOf("")}
    val exportLauncher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")){uri->if(uri!=null)scope.launch{status=runCatching{val json=buildSyncJson(vm);nav.context.contentResolver.openOutputStream(uri)!!.use{it.write(json.toByteArray())};"Sync package exported successfully."}.getOrElse{"Export failed: ${it.message}"}}}
    val importLauncher=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->if(uri!=null)scope.launch{status=runCatching{val text=nav.context.contentResolver.openInputStream(uri)!!.use{it.bufferedReader().readText()};restoreSyncJson(vm,text);vm.db.loanEmiDao().all().filter{it.active}.forEach{scheduleLoanReminder(nav.context,it)};vm.refresh();"Sync package imported successfully."}.getOrElse{"Import failed: ${it.message}"}}}
    AppScaffold(vm,nav,"Cloud Backup & Sync") {
        Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=SoftGreen)){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                BattukText("Provider not configured",fontWeight=FontWeight.Bold)
                BattukText("Cloud sync is provider-neutral. Save a complete sync package to Drive or another cloud storage, then import it on another phone.")
            }
        }
        Button({exportLauncher.launch("battuk_sync_${today()}.json")},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.CloudUpload,null);Spacer(Modifier.width(8.dp));BattukText("Export sync package")}
        OutlinedButton({importLauncher.launch(arrayOf("application/json"))},modifier=Modifier.fillMaxWidth()){Icon(Icons.Default.CloudDownload,null);Spacer(Modifier.width(8.dp));BattukText("Import sync package")}
        if(status.isNotBlank()) BattukText(status,color=if(status.contains("success",true))Green else Color(0xFFB3261E),fontWeight=FontWeight.Bold)
        BattukText("The package contains local profile, family, categories, items, expenses, trips, income, calculator history, shopping lists, budgets and EMI schedules.",style=MaterialTheme.typography.bodySmall)
    }
}

suspend fun buildSyncJson(vm:BattukViewModel):String = withContext(Dispatchers.IO) {
    val root=JSONObject().put("formatVersion",1).put("generatedAt",System.currentTimeMillis())
    root.put("profile",vm.db.profileDao().get()?.let{JSONObject().put("id",it.id).put("name",it.name).put("dob",it.dob).put("photoUri",it.photoUri)})
    suspend fun arr(factory:suspend (JSONArray)->Unit):JSONArray { val a=JSONArray(); factory(a); return a }
    root.put("family",arr{a->vm.db.familyDao().all().forEach{a.put(JSONObject().put("id",it.id).put("name",it.name).put("relation",it.relation).put("dob",it.dob).put("photoUri",it.photoUri).put("hasIncome",it.hasIncome))}})
    root.put("categories",arr{a->vm.db.categoryDao().all().forEach{a.put(JSONObject().put("id",it.id).put("name",it.name).put("type",it.type).put("icon",it.icon))}})
    root.put("items",arr{a->vm.db.itemDao().all().forEach{a.put(JSONObject().put("id",it.id).put("nameEnglish",it.nameEnglish).put("nameMarathi",it.nameMarathi).put("subtype",it.subtype).put("defaultUnit",it.defaultUnit))}})
    root.put("trips",arr{a->vm.db.tripDao().all().forEach{a.put(JSONObject().put("id",it.id).put("date",it.date).put("storeName",it.storeName).put("tripReceiptImageUri",it.tripReceiptImageUri))}})
    root.put("expenses",arr{a->vm.db.expenseDao().all().forEach{a.put(JSONObject().put("id",it.id).put("itemId",it.itemId).put("description",it.description).put("categoryId",it.categoryId).put("amount",it.amount).put("quantity",it.quantity).put("unit",it.unit).put("date",it.date).put("familyMemberId",it.familyMemberId).put("storeName",it.storeName).put("receiptImageUri",it.receiptImageUri).put("tripId",it.tripId))}})
    root.put("income",arr{a->vm.db.incomeDao().all().forEach{a.put(JSONObject().put("id",it.id).put("familyMemberId",it.familyMemberId).put("amount",it.amount).put("source",it.source).put("date",it.date).put("note",it.note))}})
    root.put("calculator",arr{a->vm.db.calcDao().all().forEach{a.put(JSONObject().put("id",it.id).put("expression",it.expression).put("result",it.result).put("timestamp",it.timestamp))}})
    root.put("shoppingLists",arr{a->vm.db.shoppingDao().lists().forEach{a.put(JSONObject().put("id",it.id).put("name",it.name).put("createdDate",it.createdDate))}})
    root.put("shoppingItems",arr{a->vm.db.shoppingDao().lists().forEach{listRow->vm.db.shoppingDao().items(listRow.id).forEach{it2->a.put(JSONObject().put("id",it2.id).put("listId",it2.listId).put("itemName",it2.itemName).put("categoryId",it2.categoryId).put("checked",it2.checked))}}})
    root.put("budgets",arr{a->vm.db.budgetDao().all().forEach{a.put(JSONObject().put("id",it.id).put("categoryId",it.categoryId).put("monthKey",it.monthKey).put("limitAmount",it.limitAmount).put("alertEnabled",it.alertEnabled))}})
    root.put("loans",arr{a->vm.db.loanEmiDao().all().forEach{a.put(JSONObject().put("id",it.id).put("title",it.title).put("lender",it.lender).put("principalAmount",it.principalAmount).put("emiAmount",it.emiAmount).put("dueDay",it.dueDay).put("nextDueDate",it.nextDueDate).put("active",it.active).put("note",it.note))}})
    root.toString(2)
}


suspend fun restoreSyncJson(vm:BattukViewModel,text:String) = withContext(Dispatchers.IO) {
    val root=JSONObject(text)
    require(root.optInt("formatVersion",0)==1){"Unsupported sync package version"}
    val db=vm.db
    db.shoppingDao().clearItems(); db.shoppingDao().clearLists(); db.expenseDao().clear(); db.incomeDao().clear(); db.calcDao().clear(); db.tripDao().clear(); db.budgetDao().clear(); db.loanEmiDao().clear(); db.familyDao().clear(); db.itemDao().clear(); db.categoryDao().clear(); db.profileDao().clear()
    if(root.optJSONObject("profile")!=null){val o=root.getJSONObject("profile");db.profileDao().save(Profile(o.getInt("id"),o.getString("name"),nullable(o,"dob"),nullable(o,"photoUri")))}
    root.optJSONArray("categories")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.categoryDao().insert(Category(o.getLong("id"),o.getString("name"),o.getString("type"),o.getString("icon")))}}
    root.optJSONArray("items")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.itemDao().insert(Item(o.getLong("id"),o.getString("nameEnglish"),o.getString("nameMarathi"),o.getString("subtype"),o.getString("defaultUnit")))}}
    root.optJSONArray("family")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.familyDao().insert(FamilyMember(o.getLong("id"),o.getString("name"),o.getString("relation"),nullable(o,"dob"),nullable(o,"photoUri"),o.optBoolean("hasIncome",false)))}}
    root.optJSONArray("trips")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.tripDao().insert(Trip(o.getLong("id"),o.getString("date"),o.getString("storeName"),nullable(o,"tripReceiptImageUri")))}}
    root.optJSONArray("expenses")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.expenseDao().insert(Expense(o.getLong("id"),nullableLong(o,"itemId"),nullable(o,"description"),o.getLong("categoryId"),o.getDouble("amount"),o.getDouble("quantity"),o.getString("unit"),o.getString("date"),nullableLong(o,"familyMemberId"),nullable(o,"storeName"),nullable(o,"receiptImageUri"),nullableLong(o,"tripId")))}}
    root.optJSONArray("income")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.incomeDao().insert(Income(o.getLong("id"),o.getLong("familyMemberId"),o.getDouble("amount"),o.getString("source"),o.getString("date"),nullable(o,"note")))}}
    root.optJSONArray("calculator")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.calcDao().insert(CalculatorEntry(o.getLong("id"),o.getString("expression"),o.getString("result"),o.getLong("timestamp")))}}
    root.optJSONArray("shoppingLists")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.shoppingDao().insertList(ShoppingList(o.getLong("id"),o.getString("name"),o.getString("createdDate")))}}
    root.optJSONArray("shoppingItems")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.shoppingDao().insertItem(ShoppingListItem(o.getLong("id"),o.getLong("listId"),o.getString("itemName"),nullableLong(o,"categoryId"),o.optBoolean("checked",false)))}}
    root.optJSONArray("budgets")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.budgetDao().upsert(Budget(o.getLong("id"),o.getLong("categoryId"),o.getString("monthKey"),o.getDouble("limitAmount"),o.optBoolean("alertEnabled",true)))}}
    root.optJSONArray("loans")?.let{a->for(i in 0 until a.length()){val o=a.getJSONObject(i);db.loanEmiDao().insert(LoanEmi(o.getLong("id"),o.getString("title"),o.getString("lender"),o.getDouble("principalAmount"),o.getDouble("emiAmount"),o.getInt("dueDay"),o.getString("nextDueDate"),o.optBoolean("active",true),nullable(o,"note")))}}
}
private fun nullable(o:JSONObject,k:String):String? = if(!o.has(k)||o.isNull(k))null else o.getString(k)
private fun nullableLong(o:JSONObject,k:String):Long? = if(!o.has(k)||o.isNull(k))null else o.getLong(k)

@Composable
fun LanguageScreen(vm:BattukViewModel, nav:NavHostController) {
    AppScaffold(vm,nav,"Language") {
        BattukText("Choose the app interface language.")
        Row(horizontalArrangement=Arrangement.spacedBy(10.dp),modifier=Modifier.fillMaxWidth()) {
            FilterChip(selected=vm.language=="en",onClick={vm.setLanguage("en")},label={BattukText("English")},modifier=Modifier.weight(1f))
            FilterChip(selected=vm.language=="mr",onClick={vm.setLanguage("mr")},label={BattukText("Marathi")},modifier=Modifier.weight(1f))
        }
        Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=SoftGreen)){
            Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(6.dp)){
                BattukText(if(vm.language=="mr")"UI आता मराठीत बदलत आहे." else "The UI switches immediately between English and Marathi.",fontWeight=FontWeight.Bold)
                BattukText("Produce names remain bilingual in either mode.")
            }
        }
    }
}
