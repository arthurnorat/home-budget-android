package com.example.homebudget.model

import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val expenseId: String,
    val description: String,
    val amount: Int,      // sempre em centavos, nunca Double/Float
    val date: String,     // "yyyy-MM-dd", sem timezone
    val category: Category,
    val createdAt: String // string bruta, nunca parseada
)
