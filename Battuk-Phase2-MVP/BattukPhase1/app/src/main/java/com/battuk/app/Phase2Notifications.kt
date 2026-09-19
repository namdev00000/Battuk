package com.battuk.app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.battuk.app.data.BattukDatabase
import com.battuk.app.data.LoanEmi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private const val BUDGET_CHANNEL = "budget_alerts"
private const val EMI_CHANNEL = "emi_reminders"
private fun notificationMoney(v:Double)="₹"+String.format(java.util.Locale.US,"%,.0f",v)

fun ensureNotificationChannels(ctx: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel(BUDGET_CHANNEL, "Budget alerts", NotificationManager.IMPORTANCE_DEFAULT))
        nm.createNotificationChannel(NotificationChannel(EMI_CHANNEL, "EMI reminders", NotificationManager.IMPORTANCE_DEFAULT))
    }
}

fun notifyBudgetExceeded(ctx: Context, categoryName: String, amount: Double, limit: Double) {
    ensureNotificationChannels(ctx)
    val intent = Intent(ctx, MainActivity::class.java)
    val pi = PendingIntent.getActivity(ctx, 7001, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val n = NotificationCompat.Builder(ctx, BUDGET_CHANNEL)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Budget limit exceeded")
        .setContentText("$categoryName is ${notificationMoney(amount)} against your ${notificationMoney(limit)} limit.")
        .setStyle(NotificationCompat.BigTextStyle().bigText("Your $categoryName spending reached ${notificationMoney(amount)}, above the monthly budget of ${notificationMoney(limit)}."))
        .setAutoCancel(true)
        .setContentIntent(pi)
        .build()
    ctx.getSystemService(NotificationManager::class.java).notify((categoryName.hashCode() and 0x7fffffff), n)
}

fun scheduleLoanReminder(ctx: Context, loan: LoanEmi) {
    val trigger = runCatching { LocalDate.parse(loan.nextDueDate) }.getOrNull() ?: return
    val millis = trigger.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    if (millis <= System.currentTimeMillis()) return
    val intent = Intent(ctx, LoanReminderReceiver::class.java).apply { putExtra("loanId", loan.id) }
    val pi = PendingIntent.getBroadcast(ctx, loan.id.toInt().coerceAtLeast(1), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    val alarm = ctx.getSystemService(AlarmManager::class.java)
    alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pi)
}

class LoanReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val loanId = intent.getLongExtra("loanId", 0L)
        if (loanId == 0L) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = BattukDatabase.get(context)
                val loan = db.loanEmiDao().all().firstOrNull { it.id == loanId }
                if (loan != null && loan.active) {
                    ensureNotificationChannels(context)
                    val main = Intent(context, MainActivity::class.java)
                    val pi = PendingIntent.getActivity(context, 7100 + loan.id.toInt(), main, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    val n = NotificationCompat.Builder(context, EMI_CHANNEL)
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        .setContentTitle("EMI due today")
                        .setContentText("${loan.title} • ${notificationMoney(loan.emiAmount)}")
                        .setStyle(NotificationCompat.BigTextStyle().bigText("Your EMI for ${loan.title} is due today. Amount: ${notificationMoney(loan.emiAmount)}."))
                        .setAutoCancel(true)
                        .setContentIntent(pi)
                        .build()
                    context.getSystemService(NotificationManager::class.java).notify(loan.id.toInt() + 9000, n)
                    val nextMonth = YearMonth.from(LocalDate.parse(loan.nextDueDate)).plusMonths(1)
                    val day = loan.dueDay.coerceIn(1, nextMonth.lengthOfMonth())
                    val nextDate = nextMonth.atDay(day).format(DateTimeFormatter.ISO_LOCAL_DATE)
                    db.loanEmiDao().update(loan.copy(nextDueDate = nextDate))
                    scheduleLoanReminder(context, loan.copy(nextDueDate = nextDate))
                }
            } finally {
                pending.finish()
            }
        }
    }
}
