package com.example.homebudget.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homebudget.model.Expense
import com.example.homebudget.model.ExpenseRequest
import com.example.homebudget.model.Category
import com.example.homebudget.network.ExpenseRepository
import com.example.homebudget.network.ExpenseServiceException
import com.example.homebudget.util.Formatters
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseEntryViewModel(
    private val repository: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseEntryUiState())
    val uiState: StateFlow<ExpenseEntryUiState> = _uiState.asStateFlow()

    init {
        loadTodayExpenses()
    }

    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onAmountChange(value: String) = _uiState.update { it.copy(amountText = value) }
    fun onDateChange(iso: String) = _uiState.update { it.copy(dateIso = iso) }
    fun onCategoryChange(category: Category) = _uiState.update { it.copy(category = category) }
    fun dismissError() = _uiState.update { it.copy(errorMessage = null) }
    fun dismissSavedConfirmation() = _uiState.update { it.copy(showSavedConfirmation = false) }

    fun loadTodayExpenses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingToday = true, errorMessage = null) }
            try {
                val expenses = repository.fetchExpenses(Formatters.monthKey())
                val today = Formatters.todayIsoDate()
                _uiState.update { it.copy(todayExpenses = expenses.filter { e -> e.date == today }) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = friendlyMessage(e)) }
            } finally {
                _uiState.update { it.copy(isLoadingToday = false) }
            }
        }
    }

    fun saveExpense() {
        val state = _uiState.value
        val cents = Formatters.parseAmountToCents(state.amountText)
        if (!state.canSave || cents == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val request = ExpenseRequest(
                    description = state.description.trim(),
                    amount = cents,
                    date = state.dateIso,
                    category = state.category
                )
                val created = repository.create(request)
                _uiState.update { current ->
                    val updated = if (created.date == Formatters.todayIsoDate()) {
                        listOf(created) + current.todayExpenses
                    } else current.todayExpenses
                    current.copy(
                        todayExpenses = updated,
                        description = "",
                        amountText = "",
                        showSavedConfirmation = true
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = friendlyMessage(e)) }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    /** Suspend (não lançada em viewModelScope internamente) para que a UI de
     *  swipe-to-delete possa aguardar o resultado e decidir se reverte a
     *  animação de arraste ou deixa o item sumir. */
    suspend fun deleteExpense(expense: Expense): Boolean {
        return try {
            repository.delete(expense.expenseId)
            _uiState.update { current ->
                current.copy(todayExpenses = current.todayExpenses.filterNot { it.expenseId == expense.expenseId })
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = friendlyMessage(e)) }
            false
        }
    }

    private fun friendlyMessage(e: Exception): String =
        (e as? ExpenseServiceException)?.message
            ?: "Não foi possível conectar ao servidor. Verifique sua internet."
}
