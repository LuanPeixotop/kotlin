package com.peixotop.pontointeligente.controllers

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.peixotop.pontointeligente.documents.Funcionario
import com.peixotop.pontointeligente.documents.Lancamento
import com.peixotop.pontointeligente.dtos.LancamentoDto
import com.peixotop.pontointeligente.enums.PerfilEnum
import com.peixotop.pontointeligente.enums.TipoEnum
import com.peixotop.pontointeligente.services.FuncionarioService
import com.peixotop.pontointeligente.services.LancamentoService
import com.peixotop.pontointeligente.utils.SenhaUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest
@WithMockUser(username = "admin@admin.com", roles = ["ADMIN"])
@AutoConfigureMockMvc
class LancamentoControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @MockBean
    private val lancamentoService: LancamentoService? = null
    @MockBean
    private val funcionarioService: FuncionarioService? = null

    private val urlBase: String = "/api/lancamentos/"
    private val funcionarioId: String = "1"
    private val lancamentoId: String = "1"
    private val tipo: String = TipoEnum.INICIO_TRABALHO.name
    private val data: LocalDateTime = LocalDateTime.now()
    private val descricao = "descrição"
    private val localizacao = "1.234.567.890"

    @Test
    @Throws(Exception::class)
    fun testCadastrarLancamento() {
        val lancamento: Lancamento = obterDadosLancamento()
        BDDMockito.given<Funcionario>(funcionarioService?.buscarPorId(funcionarioId)).willReturn(funcionario())
        BDDMockito.given(lancamentoService?.persistir(obterDadosLancamento())).willReturn(lancamento)
        mvc!!.perform(MockMvcRequestBuilders.post(urlBase)
                .content(obterJsonRequisicaoPost())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.errors").isEmpty)
                .andExpect(jsonPath("$.data.tipo").value(tipo))
                .andExpect(jsonPath("$.data.data").value(data.toString()))
                .andExpect(jsonPath("$.data.funcionarioId").value(funcionarioId))
    }

    @Test
    @Throws(Exception::class)
    fun testCadastrarLancamentoFuncionarioInvalido() {
        BDDMockito.given<Funcionario>(funcionarioService?.buscarPorId(funcionarioId)).willReturn(null)
        mvc!!.perform(MockMvcRequestBuilders.post(urlBase)
                .content(obterJsonRequisicaoPost())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.errors").value("Funcionário não encontrado."))
                .andExpect(jsonPath("$.data").isEmpty)
    }

    @Test
    @Throws(Exception::class)
    fun testRemoverLancamento() {
        BDDMockito.given<Lancamento>(lancamentoService?.buscarPorId(lancamentoId)).willReturn(obterDadosLancamento())

        mvc!!.perform(MockMvcRequestBuilders.delete(urlBase + funcionarioId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data")
                        .value("O lançamento $lancamentoId foi removido com sucesso."))
    }

    private fun obterDadosLancamento(): Lancamento = Lancamento(
            data,
            TipoEnum.valueOf(tipo),
            funcionarioId,
            descricao,
            localizacao,
            lancamentoId
    )

    private fun funcionario(): Funcionario =
            Funcionario(
                    "nome",
                    "email@email.com",
                    SenhaUtils().gerarBcrypt("senha"),
                    "12345678909",
                    PerfilEnum.ROLE_USUARIO,
                    "1"
            )

    @Throws(JsonProcessingException::class)
    private fun obterJsonRequisicaoPost(): String {
        val lancamentoDto = LancamentoDto(
                data.toString(),
                tipo,
                funcionarioId,
                descricao,
                localizacao,
                funcionarioId
        )
        val mapper = ObjectMapper()
        return mapper.writeValueAsString(lancamentoDto)
    }
}