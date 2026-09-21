package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.CalculatorEntry
import com.battuk.app.util.DateUtils
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted

class CalculatorViewModel(private val repository: BattukRepository) : ViewModel() {

    var expression by mutableStateOf("")
        private set

    var lastResult by mutableStateOf<Double?>(null)
        private set

    var lastCopiedNotice by mutableStateOf<String?>(null)
        private set

    val history: StateFlow<List<CalculatorEntry>> = repository.observeCalculatorHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onDigit(value: String) {
        expression += value
    }

    fun onOperator(op: String) {
        if (expression.isEmpty() && op != "-") return
        expression += op
    }

    fun onClear() {
        expression = ""
        lastResult = null
    }

    fun onBackspace() {
        if (expression.isNotEmpty()) expression = expression.dropLast(1)
    }

    fun onEquals() {
        val result = evaluate(expression)
        if (result != null) {
            lastResult = result
            viewModelScope.launch {
                repository.addCalculatorEntry(
                    CalculatorEntry(
                        expression = expression,
                        result = result,
                        timestampMillis = DateUtils.nowMillis()
                    )
                )
            }
        }
    }

    fun noticeCopied(text: String) {
        lastCopiedNotice = text
    }

    fun clearNotice() {
        lastCopiedNotice = null
    }

    /** Very small safe evaluator for + - x ÷ % and parentheses, no external parser dependency. */
    private fun evaluate(expr: String): Double? {
        val sanitized = expr
            .replace("×", "*")
            .replace("x", "*")
            .replace("X", "*")
            .replace("÷", "/")
            .replace("%", "*0.01*")
        if (sanitized.isBlank()) return null
        return try {
            ExpressionParser(sanitized).parse()
        } catch (e: Exception) {
            null
        }
    }

    /** Minimal recursive-descent parser supporting + - * / and parentheses. */
    private class ExpressionParser(private val text: String) {
        private var pos = 0

        fun parse(): Double {
            val value = parseExpression()
            return value
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (pos < text.length && (text[pos] == '+' || text[pos] == '-')) {
                val op = text[pos]; pos++
                val next = parseTerm()
                value = if (op == '+') value + next else value - next
            }
            return value
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (pos < text.length && (text[pos] == '*' || text[pos] == '/')) {
                val op = text[pos]; pos++
                val next = parseFactor()
                value = if (op == '*') value * next else value / next
            }
            return value
        }

        private fun parseFactor(): Double {
            if (pos < text.length && text[pos] == '-') {
                pos++
                return -parseFactor()
            }
            if (pos < text.length && text[pos] == '(') {
                pos++
                val value = parseExpression()
                if (pos < text.length && text[pos] == ')') pos++
                return value
            }
            val start = pos
            while (pos < text.length && (text[pos].isDigit() || text[pos] == '.')) pos++
            return text.substring(start, pos).toDoubleOrNull() ?: 0.0
        }
    }
}
