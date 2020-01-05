package com.peixotop.pontointeligente.repositories

import com.peixotop.pontointeligente.documents.Lancamento
import org.springframework.data.domain.Page
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.domain.Pageable

interface LancamentoRepository : MongoRepository<Lancamento, String> {
    fun findByFuncionarioId(funcionarioId: String, pageable: Pageable): Page<Lancamento>
}