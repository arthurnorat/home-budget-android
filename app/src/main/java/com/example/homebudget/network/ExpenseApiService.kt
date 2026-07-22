package com.example.homebudget.network

import com.example.homebudget.model.Expense
import com.example.homebudget.model.ExpenseRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ExpenseApiService {

    @GET("expenses")
    suspend fun getExpenses(@Query("month") month: String): Response<List<Expense>>

    @POST("expenses")
    suspend fun createExpense(@Body request: ExpenseRequest): Response<Expense>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): Response<Unit>
}
