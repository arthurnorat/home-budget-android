package com.example.homebudget.viewmodel

import com.example.homebudget.model.Category
import com.example.homebudget.model.Expense
import com.example.homebudget.util.Formatters

data class ExpenseEntryUiState(
    val description: String = "",
    val amountText: String = "",
    val dateIso: String = Formatters.todayIsoDate(),
    val category: Category = Category.VARIABLE,
    val todayExpenses: List<Expense> = emptyList(),
    val isLoadingToday: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showSavedConfirmation: Boolean = false
) {
    val canSave: Boolean
        get() = description.isNotBlank() && (Formatters.parseAmountToCents(amountText) ?: 0) > 0
}
