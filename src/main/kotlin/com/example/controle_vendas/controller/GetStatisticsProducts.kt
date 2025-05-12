package com.example.controle_vendas.controller

import com.example.controle_vendas.dto.response.StatisticsResponse
import com.example.controle_vendas.service.GetStatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/sales")
class GetStatisticsProducts(
    private val getStatisticsService: GetStatisticsService,
) {

    @GetMapping("/{id}/statistic")
    fun getAverageWeightBySaleId(@PathVariable id: String): ResponseEntity<Double> {
        val averageWeight = getStatisticsService.getAverageWeightBySaleId(id)
        return ResponseEntity.ok(averageWeight)
    }

    @GetMapping("/statistics")
    fun getStatisticsReport(
        @RequestParam startDate: String,
        @RequestParam endDate: String
    ): ResponseEntity<StatisticsResponse> {
        if (startDate > endDate) {
            throw IllegalArgumentException("startDate não pode ser posterior a endDate")
        }
        val report = getStatisticsService.getStatisticsReport(startDate, endDate)
        return ResponseEntity.ok(report)
    }

}