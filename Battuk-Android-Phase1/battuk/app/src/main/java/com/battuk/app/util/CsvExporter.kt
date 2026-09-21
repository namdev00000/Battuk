package com.battuk.app.util

import android.content.Context
import androidx.core.content.FileProvider
import com.battuk.app.data.entity.Expense
import android.net.Uri
import java.io.File
import java.io.FileWriter

object CsvExporter {
    fun exportExpenses(context: Context, year: Int, expenses: List<Expense>): Uri {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "battuk_expenses_$year.csv")
        FileWriter(file).use { writer ->
            writer.append("Date,Item,Category ID,Amount,Quantity,Unit,Store,Family Member ID\n")
            expenses.forEach { e ->
                writer.append(
                    "${DateUtils.formatDate(e.dateMillis, "yyyy-MM-dd")}," +
                        "\"${e.itemName}\",${e.categoryId},${e.amount},${e.quantity},${e.unit}," +
                        "\"${e.storeName ?: ""}\",${e.familyMemberId ?: ""}\n"
                )
            }
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
