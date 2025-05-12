package com.example.controle_vendas.controller

import com.example.controle_vendas.dto.request.SalesRequest
import com.example.controle_vendas.dto.response.SalesResponse
import com.example.controle_vendas.service.CreateSalesService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@WebMvcTest(PostCreateSales::class)
class PostCreateSalesTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var createSalesService: CreateSalesService  // Mock do serviço injetado pelo Spring

    private val objectMapper = ObjectMapper()

    @Test
    fun `should create a sale successfully`() {
        val salesRequest = SalesRequest(
            date = "12/05/2025",
            client = "Lucas",
            city = "Timóteo",
            route = "RotaB",
            description = "aaaaaaa",
            payment = "Boleto",
            saledBy = "Italo",
            create = "12/05/2025"
        )

        val salesResponse = SalesResponse(
            id = "id1",
            date = "12/05/2025",
            client = "Lucas",
            city = "Timóteo",
            route = "RotaB",
            description = "aaaaaaa",
            payment = "Boleto",
            saledBy = "Italo",
            create = "12/05/2025"
        )

        // Mockando o serviço para retornar o response esperado
        every { createSalesService.createSale(salesRequest) } returns salesResponse

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v1/sales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(salesRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(content().json(objectMapper.writeValueAsString(salesResponse)))
    }
    @BeforeEach
    fun setUp() {
        // Injetando o mock do serviço no controller
        every { createSalesService.deleteAllSales() } returns Unit
    }

    @Test
    fun `should delete all sales successfully`() {
        // Mockando o serviço para não fazer nada no delete
        every { createSalesService.deleteAllSales() } returns Unit

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/v1/sales")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent) // Verifica se o status retornado é 204 (No Content)
    }
}
