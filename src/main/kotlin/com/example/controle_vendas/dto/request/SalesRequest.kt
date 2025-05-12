package com.example.controle_vendas.dto.request

import jakarta.validation.constraints.NotBlank

data class SalesRequest(
    @field:NotBlank val date: String,
    @field:NotBlank val client: String,
    @field:NotBlank val city: String,
    @field:NotBlank val route: String,
    @field:NotBlank val description: String,
    @field:NotBlank val payment: String,
    @field:NotBlank val saledBy: String,
    @field:NotBlank val create: String,
    val products: List<ProductRequest> = emptyList()
)

