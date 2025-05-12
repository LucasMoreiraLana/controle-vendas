package com.example.controle_vendas.controller

import com.example.controle_vendas.dto.response.ProductResponse
import com.example.controle_vendas.dto.response.SalesResponse
import com.example.controle_vendas.model.ProductType
import com.example.controle_vendas.service.CreateSalesService
import com.example.controle_vendas.service.GetSalesService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDateTime

@WebMvcTest(GetSales::class)
class GetSalesIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var getSalesService: GetSalesService

    @MockkBean
    lateinit var createSalesService: CreateSalesService

    @Test
    fun `should return empty list when no sales found via HTTP`() {
        every { getSalesService.findAllSales() } returns emptyList()

        mockMvc.get("/v1/sales")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$").isArray()
                jsonPath("$.length()") { value(0) }
            }
    }


    @Test
    fun `should return list of sales via HTTP`() {
        val product = ProductResponse(
            id = "prod1",
            name = "Queijo",
            weight = 1.0,
            type = ProductType.VENDA,
            expectedPrice = 50.0,
            soldPrice = 45.0,
            reason = "Desconto"
        )

        val sales = listOf(
            SalesResponse(
                id = "sale1",
                date = "2025-05-12",
                client = "Cliente 1",
                city = "São Paulo",
                route = "Rota 1",
                description = "Teste",
                payment = "Cartão",
                saledBy = "Lucas",
                create = LocalDateTime.now().toString(),
                products = listOf(product)
            )
        )

        every { getSalesService.findAllSales() } returns sales

        mockMvc.get("/v1/sales")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.length()") { value(1) }
                jsonPath("$[0].id") { value("sale1") }
                jsonPath("$[0].products[0].name") { value("Queijo") }
            }
    }
}
