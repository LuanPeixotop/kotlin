package com.peixotop.pontointeligente.dtos

import com.peixotop.pontointeligente.enums.TipoEnum
import javax.validation.constraints.NotEmpty

data class LancamentoDto (
        @get:NotEmpty
        val data: String? = null,

        @get:NotEmpty
        val tipo: TipoEnum,

        val funcionarioId: String? = null,
        val descricao: String? = null,
        val localizacao: String? = null,
        val id: String? = null
)