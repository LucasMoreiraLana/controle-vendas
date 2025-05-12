package com.example.controle_vendas.dto.response

import com.example.controle_vendas.model.ProductType

data class ProductResponse(
    val id: String?,
    val name: String,
    val weight: Double,
    val type: com.example.controle_vendas.model.ProductType,
    val expectedPrice: Double?,
    val soldPrice: Double?,
    val reason: String?
)
