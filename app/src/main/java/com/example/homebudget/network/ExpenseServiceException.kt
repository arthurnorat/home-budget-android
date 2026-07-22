package com.example.homebudget.network

sealed class ExpenseServiceException(message: String) : Exception(message) {
    object InvalidResponse :
        ExpenseServiceException("Resposta inválida do servidor.")

    data class RequestFailed(val statusCode: Int) :
        ExpenseServiceException("Não foi possível completar a operação. Tente novamente.")

    data class DecodingFailed(val decodingCause: Throwable) :
        ExpenseServiceException("Não foi possível interpretar os dados recebidos.")
}
