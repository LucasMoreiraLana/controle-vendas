package com.example.controle_vendas.service

import com.example.controle_vendas.dto.response.StatisticsResponse
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service
import kotlin.math.round

data class StatisticResult(
    val averageWeight: Double?,
    val averageExpectedPrice: Double?,
    val averageSoldPrice: Double?,
    val totalExpectedPrice: Double?,
    val totalSoldPrice: Double?,
    val totalSales: Long?
)

data class ProfitResult(
    val productName: String,
    val totalProfit: Double?
)

@Service
class GetStatisticsService(
    private val mongoTemplate: MongoTemplate
) {

    fun getAverageWeightBySaleId(id: String): Double {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("_id").`is`(id)),
            Aggregation.unwind("products"),
            Aggregation.group()
                .avg("products.weight").`as`("averageWeight")
        )

        val results: AggregationResults<StatisticResult> = mongoTemplate.aggregate(aggregation, "sales", StatisticResult::class.java)
        val result = results.uniqueMappedResult

        return result?.averageWeight
            ?: throw IllegalArgumentException("Não foi possível calcular a média de peso para a venda com ID $id")
    }

    fun getStatisticsReport(startDate: String, endDate: String): StatisticsResponse {
        if (startDate > endDate) {
            throw IllegalArgumentException("startDate não pode ser posterior a endDate")
        }
        
        val countAggregation = Aggregation.newAggregation(
            Aggregation.match(
                Criteria.where("date").gte(startDate).lte(endDate)
            ),
            Aggregation.count().`as`("totalSales")
        )

        val countResults: AggregationResults<StatisticResult> = mongoTemplate.aggregate(countAggregation, "sales", StatisticResult::class.java)
        val countResult = countResults.uniqueMappedResult ?: StatisticResult(null, null, null, null, null, 0L)
        val totalSales = countResult.totalSales ?: 0L

        val statsAggregation = Aggregation.newAggregation(
            Aggregation.match(
                Criteria.where("date").gte(startDate).lte(endDate)
            ),
            Aggregation.unwind("products"),
            Aggregation.group()
                .avg("products.weight").`as`("averageWeight")
                .avg("products.expectedPrice").`as`("averageExpectedPrice")
                .avg("products.soldPrice").`as`("averageSoldPrice")
                .sum("products.expectedPrice").`as`("totalExpectedPrice")
                .sum("products.soldPrice").`as`("totalSoldPrice")
        )

        val statsResults: AggregationResults<StatisticResult> = mongoTemplate.aggregate(statsAggregation, "sales", StatisticResult::class.java)
        val statsResult = statsResults.uniqueMappedResult ?: StatisticResult(null, null, null, null, null, null)

        val totalProfit = (statsResult.totalSoldPrice ?: 0.0) - (statsResult.totalExpectedPrice ?: 0.0)

        // Obter lucros por produto
        val profitAggregation = Aggregation.newAggregation(
            Aggregation.match(
                Criteria.where("date").gte(startDate).lte(endDate)
            ),
            Aggregation.unwind("products"),
            Aggregation.match(
                Criteria.where("products.type").`is`("VENDA")
            ),
            Aggregation.project()
                .and("products.name").`as`("productName")
                .andExpression("(products.expectedPrice - products.soldPrice) * products.weight").`as`("profitPerUnit"),
            Aggregation.group("productName")
                .sum("profitPerUnit").`as`("totalProfit")
                .first("productName").`as`("productName")
        )

        val profitResults: AggregationResults<ProfitResult> = mongoTemplate.aggregate(profitAggregation, "sales", ProfitResult::class.java)
        val productProfits = profitResults.mappedResults.map { result ->
            val productName = result.productName as String
            val totalProfit = result.totalProfit as Double? ?: 0.0
            val roundedProfit = round(totalProfit * 100) / 100
            StatisticsResponse.ProductProfit(
                productName = productName,
                totalProfit = -roundedProfit
            )
        }

        return StatisticsResponse(
            averageWeight = statsResult.averageWeight ?: 0.0,
            averageExpectedPrice = statsResult.averageExpectedPrice ?: 0.0,
            averageSoldPrice = statsResult.averageSoldPrice ?: 0.0,
            totalExpectedPrice = statsResult.totalExpectedPrice ?: 0.0,
            totalSoldPrice = statsResult.totalSoldPrice ?: 0.0,
            totalSales = totalSales,
            totalProfit = totalProfit,
            productProfits = productProfits
        )
    }
}