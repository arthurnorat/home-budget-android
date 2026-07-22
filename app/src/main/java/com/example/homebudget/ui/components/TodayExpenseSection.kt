package com.example.homebudget.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homebudget.model.Expense
import com.example.homebudget.viewmodel.ExpenseEntryUiState
import kotlinx.coroutines.delay

fun LazyListScope.todayExpenseSection(
    uiState: ExpenseEntryUiState,
    onDelete: suspend (Expense) -> Boolean
) {
    when {
        uiState.isLoadingToday && uiState.todayExpenses.isEmpty() -> {
            item {
                var showColdStartHint by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(3000)
                    showColdStartHint = true
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    if (showColdStartHint) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Conectando ao servidor... isso pode levar alguns segundos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        uiState.todayExpenses.isEmpty() -> {
            item {
                Text(
                    "Nenhum gasto lançado hoje",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        else -> {
            items(uiState.todayExpenses, key = { it.expenseId }) { expense ->
                SwipeableExpenseRow(
                    expense = expense,
                    onDelete = onDelete,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}
