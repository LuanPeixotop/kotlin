package com.unicarioca.pontointeligente.repositories

import com.unicarioca.pontointeligente.documents.Empresa
import org.springframework.data.mongodb.repository.MongoRepository

interface EmpresaRepository : MongoRepository<Empresa, String> {
    fun findByCnpj(cnpj: String) : Empresa
}