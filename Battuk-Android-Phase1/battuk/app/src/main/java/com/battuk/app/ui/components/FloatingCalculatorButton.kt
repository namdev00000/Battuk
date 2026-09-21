package com.battuk.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.battuk.app.ui.calculator.CalculatorSheet
import com.battuk.app.viewmodel.CalculatorViewModel

/**
 * Wraps [content] and overlays a floating calculator button fixed at the
 * bottom-right of the screen. Tapping it opens the Calculator bottom sheet,
 * whose "Paste to Expense" result flows back through [onPasteToExpense].
 */
@Composable
fun ScreenWithFloatingCalculator(
    calculatorViewModel: CalculatorViewModel,
    onPasteToExpense: ((Double) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var showCalculator by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        FloatingActionButton(
            onClick = { showCalculator = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Filled.Calculate, contentDescription = "Calculator")
        }
    }

    if (showCalculator) {
        CalculatorSheet(
            viewModel = calculatorViewModel,
            onDismiss = { showCalculator = false },
            onPasteToExpense = onPasteToExpense?.let { callback ->
                { value: Double ->
                    callback(value)
                    showCalculator = false
                }
            }
        )
    }
}
