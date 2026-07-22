package com.example.homebudget.network

import com.example.homebudget.model.Expense
import com.example.homebudget.model.ExpenseRequest
import kotlinx.serialization.SerializationException
import retrofit2.Response

class ExpenseRepository(
    private val api: ExpenseApiService = RetrofitProvider.expenseApi
) {
    suspend fun fetchExpenses(month: String): List<Expense> {
        val response = safeCall { api.getExpenses(month) }
        validate(response)
        return response.body().orEmpty()
    }

    suspend fun create(request: ExpenseRequest): Expense {
        val response = safeCall { api.createExpense(request) }
        validate(response)
        return response.body() ?: throw ExpenseServiceException.InvalidResponse
    }

    suspend fun delete(id: String) {
        val response = safeCall { api.deleteExpense(id) }
        validate(response)
    }

    private suspend fun <T> safeCall(block: suspend () -> Response<T>): Response<T> {
        return try {
            block()
        } catch (e: SerializationException) {
            throw ExpenseServiceException.DecodingFailed(e)
        }
    }

    private fun validate(response: Response<*>) {
        if (!response.isSuccessful) {
            throw ExpenseServiceException.RequestFailed(response.code())
        }
    }
}
