package com.example.controle_vendas.controller

import com.example.controle_vendas.dto.response.SalesResponse
import com.example.controle_vendas.service.CreateSalesService
import com.example.controle_vendas.service.GetSalesService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/sales")
class GetSales(
    private val createSalesService: CreateSalesService,
    private val salesService: GetSalesService
) {

    @GetMapping
    fun getAllSales(): ResponseEntity<List<SalesResponse>>{
        val sales = salesService.findAllSales()
        return ResponseEntity.ok(sales)
    }

    @GetMapping("/{id}")
    fun getSaleById(@PathVariable id: String): ResponseEntity<SalesResponse> {
        val sale = salesService.findSaleById(id)
        return ResponseEntity.ok(sale)
    }

}