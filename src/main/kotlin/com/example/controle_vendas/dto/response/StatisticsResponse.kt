package com.example.controle_vendas.dto.response

data class StatisticsResponse(
    val averageWeight: Double,
    val averageExpectedPrice: Double,
    val averageSoldPrice: Double,
    val totalExpectedPrice: Double,
    val totalSoldPrice: Double,
    val totalSales: Long,
    val totalProfit: Double,
    val productProfits: List<ProductProfit>
) {
    data class ProductProfit(
        val productName: String,
        val totalProfit: Double
    )
}