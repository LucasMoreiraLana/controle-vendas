package com.example.controle_vendas.service

import com.example.controle_vendas.dto.request.SalesRequest
import com.example.controle_vendas.dto.response.ProductResponse
import com.example.controle_vendas.dto.response.SalesResponse
import com.example.controle_vendas.model.Product
import com.example.controle_vendas.model.ProductType
import com.example.controle_vendas.model.Sales
import com.example.controle_vendas.repository.SalesRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateSalesService(
    val salesRepository: SalesRepository
) {

    fun createSale(request: SalesRequest): SalesResponse {
        if (request.products.isEmpty()) {
            throw IllegalArgumentException("Uma venda, bonificação ou devolução deve ter pelo menos um produto")
        }

        val products = request.products.map { productRequest ->

            when (productRequest.type) {
                ProductType.VENDA -> {
                    if (productRequest.expectedPrice == null || productRequest.expectedPrice <= 0) {
                        throw IllegalArgumentException("Para tipo 'VENDA', expectedPrice deve ser maior que 0")
                    }
                    if (productRequest.soldPrice == null || productRequest.soldPrice <= 0) {
                        throw IllegalArgumentException("Para tipo 'VENDA', soldPrice deve ser maior que 0")
                    }
                }
                ProductType.BONIFICACAO -> {
                    if (productRequest.expectedPrice != null || productRequest.soldPrice != null || productRequest.reason != null) {
                        throw IllegalArgumentException("Para tipo 'BONIFICACAO', expectedPrice, soldPrice e reason devem ser nulos")
                    }
                }
                ProductType.DEVOLUCAO -> {
                    if (productRequest.reason == null || productRequest.reason.isBlank()) {
                        throw IllegalArgumentException("Para tipo 'DEVOLUCAO', reason é obrigatório")
                    }
                    if (productRequest.expectedPrice != null || productRequest.soldPrice != null) {
                        throw IllegalArgumentException("Para tipo 'DEVOLUCAO', expectedPrice e soldPrice devem ser nulos")
                    }
                }
            }

            Product(
                name = productRequest.name,
                weight = productRequest.weight,
                type = productRequest.type,
                expectedPrice = productRequest.expectedPrice,
                soldPrice = productRequest.soldPrice,
                reason = productRequest.reason
            )
        }

        val sale = Sales(
            id = UUID.randomUUID().toString(),
            date = request.date,
            client = request.client,
            city = request.city,
            route = request.route,
            description = request.description,
            payment = request.payment,
            saledBy = request.saledBy,
            create = request.create,
            products = products
        )

        val savedSale = salesRepository.save(sale)

        val productResponses = savedSale.products.map { product ->
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
            id = savedSale.id,
            date = savedSale.date,
            client = savedSale.client,
            city = savedSale.city,
            route = savedSale.route,
            description = savedSale.description,
            payment = savedSale.payment,
            saledBy = savedSale.saledBy,
            create = savedSale.create,
            products = productResponses
        )
    }

    //lembrar de apagar depois
    fun deleteAllSales() {
        salesRepository.deleteAll()
    }

}