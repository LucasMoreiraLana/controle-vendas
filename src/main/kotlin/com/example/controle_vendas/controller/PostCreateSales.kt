package com.example.controle_vendas.controller

import com.example.controle_vendas.dto.request.SalesRequest
import com.example.controle_vendas.dto.response.SalesResponse
import com.example.controle_vendas.service.CreateSalesService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/v1/sales")
class PostCreateSales(
    val createSalesService: CreateSalesService
) {

    @PostMapping
    fun createSale(@Valid @RequestBody request: SalesRequest): ResponseEntity<SalesResponse> {
        val response = createSalesService.createSale(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }


    //apagar depois
    @DeleteMapping
    fun deleteAllSales(): ResponseEntity<Void> {
        createSalesService.deleteAllSales()
        return ResponseEntity.noContent().build()
    }


}