package com.example.controle_vendas.dto.request

import com.example.controle_vendas.model.ProductType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ProductRequest(
    @field:NotBlank val name: String,
    val weight: Double,
    @field:NotNull val type: ProductType,
    val expectedPrice: Double?,
    val soldPrice: Double?,
    val reason: String?
)
