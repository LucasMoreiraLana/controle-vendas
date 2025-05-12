package com.example.controle_vendas.controller

import com.example.controle_vendas.dto.response.ProductResponse
import com.example.controle_vendas.dto.response.SalesResponse
import com.example.controle_vendas.model.ProductType // Importação correta
import com.example.controle_vendas.service.CreateSalesService
import com.example.controle_vendas.service.GetSalesService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class GetSalesTest {

    private lateinit var createSalesService: CreateSalesService
    private lateinit var getSalesService: GetSalesService
    private lateinit var getSales: GetSales

    @BeforeEach
    fun setUp() {
        createSalesService = mockk()
        getSalesService = mockk()
        getSales = GetSales(createSalesService, getSalesService)
    }

    @Test
    fun `should return all sales successfully with non-empty list`() {
        val product = ProductResponse(
            id = "prod1",
            name = "Queijo",
            weight = 1.0,
            type = ProductType.VENDA, // Usando o valor real do enum
            expectedPrice = 50.0,
            soldPrice = 45.0,
            reason = "Desconto"
        )
        val salesList = listOf(
            SalesResponse(
                id = "sale1",
                date = "2025-05-12",
                client = "Cliente 1",
                city = "São Paulo",
                route = "Rota 1",
                description = "Venda teste",
                payment = "Cartão",
                saledBy = "Vendedor 1",
                create = LocalDateTime.now().toString(),
                products = listOf(product)
            ),
            SalesResponse(
                id = "sale2",
                date = "2025-05-12",
                client = "Cliente 2",
                city = "Rio de Janeiro",
                route = "Rota 2",
                description = "Outra venda",
                payment = "Dinheiro",
                saledBy = "Vendedor 2",
                create = LocalDateTime.now().toString(),
                products = emptyList()
            )
        )
        every { getSalesService.findAllSales() } returns salesList

        val response = getSales.getAllSales()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(salesList, response.body)
        assertEquals(2, response.body?.size)
    }

    @Test
    fun `should return empty list when no sales found`() {
        every { getSalesService.findAllSales() } returns emptyList()

        val response = getSales.getAllSales()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(emptyList<SalesResponse>(), response.body)
    }

    @Test
    fun `should throw exception when service fails to get all sales`() {
        every { getSalesService.findAllSales() } throws RuntimeException("Database error")

        assertThrows<RuntimeException> {
            getSales.getAllSales()
        }.also { assertEquals("Database error", it.message) }
    }

    @Test
    fun `should return sale by id successfully`() {
        val product = ProductResponse(
            id = "prod1",
            name = "Queijo",
            weight = 1.0,
            type = ProductType.VENDA, // Usando o valor real do enum
            expectedPrice = 50.0,
            soldPrice = 45.0,
            reason = "Desconto"
        )
        val sale = SalesResponse(
            id = "sale1",
            date = "2025-05-12",
            client = "Cliente 1",
            city = "São Paulo",
            route = "Rota 1",
            description = "Venda teste",
            payment = "Cartão",
            saledBy = "Vendedor 1",
            create = LocalDateTime.now().toString(),
            products = listOf(product)
        )
        every { getSalesService.findSaleById("sale1") } returns sale

        val response = getSales.getSaleById("sale1")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(sale, response.body)
        assertEquals("sale1", response.body?.id)
    }

    @Test
    fun `should throw exception when sale id not found`() {
        every { getSalesService.findSaleById("invalid-id") } throws IllegalArgumentException("Venda com ID invalid-id não encontrada")

        assertThrows<IllegalArgumentException> {
            getSales.getSaleById("invalid-id")
        }.also { assertEquals("Venda com ID invalid-id não encontrada", it.message) }
    }

    @Test
    fun `should throw exception when service fails to get sale by id`() {
        every { getSalesService.findSaleById("sale1") } throws RuntimeException("Database error")

        assertThrows<RuntimeException> {
            getSales.getSaleById("sale1")
        }.also { assertEquals("Database error", it.message) }
    }

}