package com.example.controle_vendas.repository

import com.example.controle_vendas.model.Sales
import org.springframework.data.mongodb.repository.MongoRepository

interface SalesRepository: MongoRepository<Sales,String>  {
}