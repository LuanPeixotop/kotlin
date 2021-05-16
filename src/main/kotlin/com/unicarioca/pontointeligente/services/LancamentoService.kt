package com.unicarioca.pontointeligente.services

import com.unicarioca.pontointeligente.documents.Lancamento
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface LancamentoService {
    fun buscarPorFuncionarioId(funcionarioId: String, pageRequest: PageRequest) : Page<Lancamento>

    fun buscarPorId(id: String): Lancamento?

    fun persistir(lancamento: Lancamento) : Lancamento

    fun remover(id: String)
}