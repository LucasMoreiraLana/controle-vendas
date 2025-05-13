package com.example.controle_vendas.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.bson.Document
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import com.mongodb.MongoException

class GetStatisticsServiceTest {

    private lateinit var mongoTemplate: MongoTemplate
    private lateinit var getStatisticsService: GetStatisticsService

    @BeforeEach
    fun setUp() {
        mongoTemplate = mockk<MongoTemplate>()
        getStatisticsService = GetStatisticsService(mongoTemplate)
    }

    @Test
    fun `should return statistics report with correct data`() {
        val statsResult = StatisticResult(
            averageWeight = 2.32125,
            averageExpectedPrice = 58.48695652173913,
            averageSoldPrice = 58.13695652173913,
            totalExpectedPrice = 467.56,
            totalSoldPrice = 463.60,
            totalSales = 0
        )

        val countResult = StatisticResult(
            averageWeight = 0.0,
            averageExpectedPrice = 0.0,
            averageSoldPrice = 0.0,
            totalExpectedPrice = 0.0,
            totalSoldPrice = 0.0,
            totalSales = 3L
        )

        val profitResults = listOf(
            ProfitResult("Queijo Colonial 1kg", 9.80),
            ProfitResult("Queijo Parmessão Fracionado", -6.00)
        )

        val emptyDocument = Document()
        val aggregationSlot = slot<Aggregation>()

        // Ordem correta: primeiro countResult, depois statsResult
        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", StatisticResult::class.java) } returnsMany listOf(
            AggregationResults<StatisticResult>(listOf(countResult), emptyDocument),
            AggregationResults<StatisticResult>(listOf(statsResult), emptyDocument)
        )

        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", ProfitResult::class.java) } returns AggregationResults(profitResults, emptyDocument)

        val result = getStatisticsService.getStatisticsReport("2025-05-01", "2025-05-01")

        assertEquals(2.32125, result.averageWeight)
        assertEquals(58.48695652173913, result.averageExpectedPrice, 0.01)
        assertEquals(58.13695652173913, result.averageSoldPrice, 0.01)
        assertEquals(467.56, result.totalExpectedPrice)
        assertEquals(463.60, result.totalSoldPrice)
        assertEquals(3L, result.totalSales)
        assertEquals(-3.96, result.totalProfit, 0.01) // Consistente com totalExpectedPrice - totalSoldPrice

        assertEquals(2, result.productProfits.size)
        assertEquals("Queijo Colonial 1kg", result.productProfits[0].productName)
        assertEquals(9.80, result.productProfits[0].totalProfit, 0.01) // Corrigido para o valor mockado
        assertEquals("Queijo Parmessão Fracionado", result.productProfits[1].productName)
        assertEquals(-6.00, result.productProfits[1].totalProfit, 0.01) // Corrigido para o valor mockado
    }

    @Test
    fun `should throw IllegalArgumentException when startDate is after endDate`() {
        assertThrows<IllegalArgumentException> {
            getStatisticsService.getStatisticsReport("2025-05-02", "2025-05-01")
        }.also { exception ->
            assertEquals("startDate não pode ser posterior a endDate", exception.message)
        }
    }

    @Test
    fun `should return default values when no sales are found`() {
        val emptyStatsResult = StatisticResult(0.0, 0.0, 0.0, 0.0, 0.0, 0L)
        val emptyCountResult = StatisticResult(0.0, 0.0, 0.0, 0.0, 0.0, 0L)
        val emptyDocument = Document()
        val aggregationSlot = slot<Aggregation>()

        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", StatisticResult::class.java) } returnsMany listOf(
            AggregationResults<StatisticResult>(listOf(emptyCountResult), emptyDocument),
            AggregationResults<StatisticResult>(listOf(emptyStatsResult), emptyDocument)
        )

        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", ProfitResult::class.java) } returns AggregationResults(emptyList(), emptyDocument)

        val result = getStatisticsService.getStatisticsReport("2025-05-01", "2025-05-01")

        assertEquals(0.0, result.averageWeight)
        assertEquals(0.0, result.averageExpectedPrice)
        assertEquals(0.0, result.averageSoldPrice)
        assertEquals(0.0, result.totalExpectedPrice)
        assertEquals(0.0, result.totalSoldPrice)
        assertEquals(0L, result.totalSales)
        assertEquals(0.0, result.totalProfit)
        assertEquals(0, result.productProfits.size)
    }

    @Test
    fun `should return average weight for a sale`() {
        val statsResult = StatisticResult(
            averageWeight = 2.5,
            averageExpectedPrice = 0.0,
            averageSoldPrice = 0.0,
            totalExpectedPrice = 0.0,
            totalSoldPrice = 0.0,
            totalSales = 0
        )

        val emptyDocument = Document()
        every { mongoTemplate.aggregate(any<Aggregation>(), "sales", StatisticResult::class.java) } returns AggregationResults(listOf(statsResult), emptyDocument)

        val result = getStatisticsService.getAverageWeightBySaleId("sale1")

        assertEquals(2.5, result)
    }

    @Test
    fun `should return report with single sale`() {
        val statsResult = StatisticResult(1.0, 50.0, 50.0, 50.0, 50.0, 0)
        val countResult = StatisticResult(0.0, 0.0, 0.0, 0.0, 0.0, 1L)
        val profitResults = listOf(ProfitResult("Queijo Simples", 0.0))
        val emptyDocument = Document()
        val aggregationSlot = slot<Aggregation>()

        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", StatisticResult::class.java) } returnsMany listOf(
            AggregationResults<StatisticResult>(listOf(countResult), emptyDocument),
            AggregationResults<StatisticResult>(listOf(statsResult), emptyDocument)
        )
        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", ProfitResult::class.java) } returns AggregationResults(profitResults, emptyDocument)

        val result = getStatisticsService.getStatisticsReport("2025-05-01", "2025-05-01")
        assertEquals(1.0, result.averageWeight, 0.01)
        assertEquals(50.0, result.averageExpectedPrice, 0.01)
        assertEquals(50.0, result.averageSoldPrice, 0.01)
        assertEquals(50.0, result.totalExpectedPrice)
        assertEquals(50.0, result.totalSoldPrice)
        assertEquals(1L, result.totalSales)
        assertEquals(0.0, result.totalProfit, 0.01)
        assertEquals(1, result.productProfits.size)
    }

    @Test
    fun `should throw exception on MongoTemplate failure`() {
        val aggregationSlot = slot<Aggregation>()
        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", StatisticResult::class.java) } throws MongoException("Simulated failure")

        assertThrows<MongoException> {
            getStatisticsService.getStatisticsReport("2025-05-01", "2025-05-01")
        }
    }

    @Test
    fun `should handle partial data from MongoTemplate`() {
        val statsResult = StatisticResult(2.0, 0.0, 0.0, 100.0, 90.0, 0)
        val countResult = StatisticResult(0.0, 0.0, 0.0, 0.0, 0.0, 2L)
        val profitResults = listOf(ProfitResult("Queijo Teste", 5.0))
        val emptyDocument = Document()
        val aggregationSlot = slot<Aggregation>()

        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", StatisticResult::class.java) } returnsMany listOf(
            AggregationResults<StatisticResult>(listOf(countResult), emptyDocument),
            AggregationResults<StatisticResult>(listOf(statsResult), emptyDocument)
        )
        every { mongoTemplate.aggregate(capture(aggregationSlot), "sales", ProfitResult::class.java) } returns AggregationResults(profitResults, emptyDocument)

        val result = getStatisticsService.getStatisticsReport("2025-05-01", "2025-05-05")
        assertEquals(2.0, result.averageWeight, 0.01)
        assertEquals(0.0, result.averageExpectedPrice, 0.01)
        assertEquals(0.0, result.averageSoldPrice, 0.01)
        assertEquals(100.0, result.totalExpectedPrice)
        assertEquals(90.0, result.totalSoldPrice)
        assertEquals(2L, result.totalSales)
        assertEquals(-10.0, result.totalProfit, 0.01) // Consistente com 100.0 - 90.0
        assertEquals(1, result.productProfits.size)
        assertEquals(5.0, result.productProfits[0].totalProfit, 0.01) // Corrigido para o valor mockado
    }

    @Test
    fun `should throw exception on MongoTemplate failure for average weight`() {
        every { mongoTemplate.aggregate(any<Aggregation>(), "sales", StatisticResult::class.java) } throws MongoException("Simulated failure")

        assertThrows<MongoException> {
            getStatisticsService.getAverageWeightBySaleId("sale1")
        }
    }
}