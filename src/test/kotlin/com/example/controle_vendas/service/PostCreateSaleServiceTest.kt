package com.example.controle_vendas.service

import com.example.controle_vendas.dto.request.ProductRequest
import com.example.controle_vendas.dto.request.SalesRequest
import com.example.controle_vendas.model.Product
import com.example.controle_vendas.model.ProductType
import com.example.controle_vendas.model.Sales
import com.example.controle_vendas.repository.SalesRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PostCreateSalesServiceTest {

    private lateinit var salesRepository: SalesRepository
    private lateinit var createSalesService: CreateSalesService

    @BeforeEach
    fun setup() {
        salesRepository = mockk()
        createSalesService = CreateSalesService(salesRepository)
    }

    @Test
    fun `should create sale successfully with valid VENDA product`() {
        val productRequest = ProductRequest(
            name = "Queijo",
            weight = 1.0,
            type = ProductType.VENDA,
            expectedPrice = 50.0,
            soldPrice = 45.0,
            reason = "Desconto"
        )
        val saleRequest = SalesRequest(
            date = "2025-05-12",
            client = "Cliente Teste",
            city = "São Paulo",
            route = "Rota 1",
            description = "Descrição de teste",
            payment = "Cartão",
            saledBy = "Vendedor Teste",
            create = LocalDateTime.now().toString(),
            products = listOf(productRequest)
        )

        val savedSale = Sales(
            id = "generated-id",
            date = saleRequest.date,
            client = saleRequest.client,
            city = saleRequest.city,
            route = saleRequest.route,
            description = saleRequest.description,
            payment = saleRequest.payment,
            saledBy = saleRequest.saledBy,
            create = saleRequest.create,
            products = listOf(
                Product(
                    id = "prod-id",
                    name = "Queijo",
                    weight = 1.0,
                    type = ProductType.VENDA,
                    expectedPrice = 50.0,
                    soldPrice = 45.0,
                    reason = "Desconto"
                )
            )
        )

        every { salesRepository.save(any()) } returns savedSale

        val response = createSalesService.createSale(saleRequest)

        assertEquals("generated-id", response.id)
        assertEquals("Cliente Teste", response.client)
        assertEquals(1, response.products.size)
        assertEquals(ProductType.VENDA, response.products.first().type)
    }

    @Test
    fun `should throw exception when product list is empty`() {
        val saleRequest = SalesRequest(
            date = "2025-05-12",
            client = "Cliente Teste",
            city = "São Paulo",
            route = "Rota 1",
            description = "Descrição de teste",
            payment = "Cartão",
            saledBy = "Vendedor Teste",
            create = LocalDateTime.now().toString(),
            products = emptyList()
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            createSalesService.createSale(saleRequest)
        }

        assertEquals("Uma venda, bonificação ou devolução deve ter pelo menos um produto", exception.message)
    }

    @Test
    fun `should throw exception when VENDA has null or invalid prices`() {
        val invalidProduct = ProductRequest(
            name = "Produto inválido",
            weight = 1.0,
            type = ProductType.VENDA,
            expectedPrice = null,
            soldPrice = 0.0,
            reason = "Desconto"
        )

        val saleRequest = SalesRequest(
            date = "2025-05-12",
            client = "Cliente",
            city = "Cidade",
            route = "Rota",
            description = "Descrição",
            payment = "Cartão",
            saledBy = "Vendedor",
            create = LocalDateTime.now().toString(),
            products = listOf(invalidProduct)
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            createSalesService.createSale(saleRequest)
        }

        assertEquals("Para tipo 'VENDA', expectedPrice deve ser maior que 0", exception.message)
    }

    @Test
    fun `should throw exception when BONIFICACAO has non-null fields`() {
        val bonusProduct = ProductRequest(
            name = "Brinde",
            weight = 1.0,
            type = ProductType.BONIFICACAO,
            expectedPrice = 10.0,
            soldPrice = 0.0,
            reason = "Presente"
        )

        val saleRequest = SalesRequest(
            date = "2025-05-12",
            client = "Cliente",
            city = "Cidade",
            route = "Rota",
            description = "Descrição",
            payment = "Cartão",
            saledBy = "Vendedor",
            create = LocalDateTime.now().toString(),
            products = listOf(bonusProduct)
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            createSalesService.createSale(saleRequest)
        }

        assertEquals("Para tipo 'BONIFICACAO', expectedPrice, soldPrice e reason devem ser nulos", exception.message)
    }

    @Test
    fun `should throw exception when DEVOLUCAO has no reason`() {
        val devolucao = ProductRequest(
            name = "Produto devolvido",
            weight = 1.0,
            type = ProductType.DEVOLUCAO,
            expectedPrice = null,
            soldPrice = null,
            reason = ""
        )

        val saleRequest = SalesRequest(
            date = "2025-05-12",
            client = "Cliente",
            city = "Cidade",
            route = "Rota",
            description = "Descrição",
            payment = "Cartão",
            saledBy = "Vendedor",
            create = LocalDateTime.now().toString(),
            products = listOf(devolucao)
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            createSalesService.createSale(saleRequest)
        }

        assertEquals("Para tipo 'DEVOLUCAO', reason é obrigatório", exception.message)
    }
}
