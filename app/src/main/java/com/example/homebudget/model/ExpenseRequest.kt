package com.example.homebudget.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpenseRequest(
    val description: String,
    val amount: Int,
    val date: String,
    val category: Category
)
