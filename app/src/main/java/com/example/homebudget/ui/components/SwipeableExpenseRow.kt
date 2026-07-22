package com.example.homebudget.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.homebudget.model.Expense
import com.example.homebudget.ui.theme.DeleteRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableExpenseRow(
    expense: Expense,
    onDelete: suspend (Expense) -> Boolean,
    modifier: Modifier = Modifier
) {
    // confirmValueChange sempre retorna false: nunca deixamos o box "confirmar"
    // sozinho a exclusão. Em vez disso, observamos o alvo do arraste
    // (targetValue) para disparar a exclusão real via ViewModel; se falhar,
    // o próprio "false" já faz o item voltar ao lugar automaticamente.
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = { false })

    LaunchedEffect(dismissState.targetValue) {
        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete(expense)
            // Em caso de sucesso, o item some da lista de origem
            // (uiState.todayExpenses) e este composable é removido do
            // LazyColumn naturalmente.
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeleteRed)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Apagar", tint = Color.White)
            }
        }
    ) {
        ExpenseRow(expense = expense)
    }
}
