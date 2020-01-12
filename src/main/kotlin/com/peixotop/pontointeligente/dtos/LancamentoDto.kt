package com.peixotop.pontointeligente.dtos

import javax.validation.constraints.NotEmpty

data class LancamentoDto (
        @get:NotEmpty
        val data: String? = null,

        @get:NotEmpty
        val tipo: String,

        val funcionarioId: String? = null,
        val descricao: String? = null,
        val localizacao: String? = null,
        val id: String? = null
)