package com.example.controle_vendas.dto.response

data class SalesResponse(
    val id: String,
    val date: String,
    val client: String,
    val city: String,
    val route: String,
    val description: String,
    val payment: String,
    val saledBy: String,
    val create: String,
    val products: List<ProductResponse> = emptyList()
)


