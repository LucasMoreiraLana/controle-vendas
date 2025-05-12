package com.example.controle_vendas.service

import com.example.controle_vendas.dto.response.ProductResponse
import com.example.controle_vendas.dto.response.SalesResponse
import com.example.controle_vendas.model.Sales
import com.example.controle_vendas.repository.SalesRepository
import org.springframework.stereotype.Service

@Service
class GetSalesService(
    private val salesRepository: SalesRepository
) {

    fun findAllSales(): List<SalesResponse> {
        val sales = salesRepository.findAll()

        return sales.map { sale ->
            val productResponses = sale.products.map { product ->
                ProductResponse(
                    id = product.id,
                    name = product.name,
                    weight = product.weight,
                    type = product.type,
                    expectedPrice = product.expectedPrice,
                    soldPrice = product.soldPrice,
                    reason = product.reason
                )
            }

            SalesResponse(
                id = sale.id,
                date = sale.date,
                client = sale.client,
                city = sale.city,
                route = sale.route,
                description = sale.description,
                payment = sale.payment,
                saledBy = sale.saledBy,
                create = sale.create,
                products = productResponses
            )
        }
    }

    fun findSaleById(id: String): SalesResponse {
        val sale: Sales = salesRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Venda com ID $id não encontrada") }

        val productResponses = sale.products.map { product ->
            ProductResponse(
                id = product.id,
                name = product.name,
                weight = product.weight,
                type = product.type,
                expectedPrice = product.expectedPrice,
                soldPrice = product.soldPrice,
                reason = product.reason
            )
        }

        return SalesResponse(
            id = sale.id,
            date = sale.date,
            client = sale.client,
            city = sale.city,
            route = sale.route,
            description = sale.description,
            payment = sale.payment,
            saledBy = sale.saledBy,
            create = sale.create,
            products = productResponses
        )
    }
}