package com.example.controle_vendas.model

import org.springframework.data.annotation.Id
import java.util.UUID

data class Product(
    val id: String? = null,
    val name: String,
    val weight: Double,
    val type: ProductType,
    val expectedPrice: Double?,
    val soldPrice: Double?,
    val reason: String?
)


