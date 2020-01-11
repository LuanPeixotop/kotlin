package com.peixotop.pontointeligente.dtos

import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.br.CNPJ
import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class CadastroPfDto (

        @get:NotEmpty
        @get:Length(min = 3, max = 200, message = "Nome deve conter entre 3 e 200 caracteres.")
        val nome: String? = "",

        @get:NotEmpty
        @get:Length(min = 5, max = 200, message = "Email deve conter entre 5 e 200 caracteres.")
        @get:Email(message = "Email Inválido.")
        val email: String = "",

        @get:NotEmpty
        @get:Length(min = 8, max = 50, message = "Senha deve conter entre 8 e 50 caracteres.")
        val senha: String = "",

        @get:NotEmpty
        @get:CPF
        val cpf: String = "",

        @get:NotEmpty
        @get:CNPJ
        val cnpj: String = "",

        val empresaId: String = "",

        val valorHora: String? = null,

        val qtdHorasTrabalhoDia: String? = null,

        val qtdHoraAlmoco: String? = null,

        val id: String? = null
)