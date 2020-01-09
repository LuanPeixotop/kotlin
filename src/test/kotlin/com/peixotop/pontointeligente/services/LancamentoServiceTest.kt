package com.peixotop.pontointeligente.services

import com.peixotop.pontointeligente.documents.Lancamento
import com.peixotop.pontointeligente.enums.TipoEnum
import com.peixotop.pontointeligente.repositories.LancamentoRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class LancamentoServiceTest {

    @MockBean
    private val lancamentoRepository: LancamentoRepository? = null

    @Autowired
    private val lancamentoService: LancamentoService? = null

    private val data: LocalDateTime = LocalDateTime.of(2020, Month.JANUARY, 1, 12, 0,0)
    private val tipo: TipoEnum = TipoEnum.INICIO_TRABALHO
    private val funcionarioId: String = "1"
    private val id: String = "1"

    @BeforeEach
    fun setUp() {
        BDDMockito.given<Page<Lancamento>>(lancamentoRepository?.findByFuncionarioId(funcionarioId, PageRequest.of(0, 10))).willReturn(lancamentos())
        BDDMockito.given(lancamentoRepository?.findById(id)).willReturn(Optional.of(lancamento()))
        BDDMockito.given(lancamentoRepository?.save(Mockito.any(Lancamento::class.java))).willReturn(lancamento())
        BDDMockito.doNothing().`when`(lancamentoRepository)?.delete(lancamento())
    }

    @Test
    fun testBuscarPorFuncionarioId() {
        val lancamentos: Page<Lancamento>? = this.lancamentoService?.buscarPorFuncionarioId(funcionarioId, PageRequest.of(0, 10))
        Assertions.assertNotNull(lancamentos)
    }

    @Test
    fun testBuscarPorId() {
        val lancamento: Optional<Lancamento>? = this.lancamentoService?.buscarPorId(id)
        Assertions.assertNotNull(lancamento)
    }

    @Test
    fun testPersistir() {
        val lancamento: Lancamento? = this.lancamentoService?.persistir(lancamento())
        Assertions.assertNotNull(lancamento)
    }

    @Test
    fun testRemover() {
        val lancamento: Unit? = this.lancamentoService?.remover(id)
        Assertions.assertNotNull(lancamento)
    }

    private fun lancamento() : Lancamento = Lancamento(data, tipo, funcionarioId)
    private fun lancamentos() : Page<Lancamento> = Page.empty()
}