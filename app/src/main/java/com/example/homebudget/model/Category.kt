package com.example.homebudget.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Category {
    @SerialName("FIXED") FIXED,
    @SerialName("VARIABLE") VARIABLE;

    val displayName: String
        get() = when (this) {
            FIXED -> "Fixo"
            VARIABLE -> "Variável"
        }
}
