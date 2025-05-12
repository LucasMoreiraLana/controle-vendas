package com.example.controle_vendas.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "sales")
data class Sales(
    @Id
    val id: String,
    val date: String,
    val client: String,
    val city: String,
    val route: String,
    val description: String,
    val payment: String,
    val saledBy: String,
    val create: String,
    val products: List<Product>
)