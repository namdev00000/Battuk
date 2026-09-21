package com.battuk.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import java.util.Locale

/** Every amount in Battuk is fixed to Indian Rupees. */
object CurrencyUtils {
    fun format(amount: Double): String {
        val rounded = Math.round(amount * 100.0) / 100.0
        return if (rounded == rounded.toLong().toDouble()) {
            "\u20B9${String.format(Locale.US, "%,d", rounded.toLong())}"
        } else {
            "\u20B9${String.format(Locale.US, "%,.2f", rounded)}"
        }
    }
}

object ClipboardUtils {
    fun copy(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    fun paste(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(context)?.toString()
    }
}
