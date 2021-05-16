package com.peixotop.pontointeligente.controllers

import com.peixotop.pontointeligente.Response
import com.peixotop.pontointeligente.documents.Funcionario
import com.peixotop.pontointeligente.documents.Lancamento
import com.peixotop.pontointeligente.dtos.LancamentoDto
import com.peixotop.pontointeligente.enums.PerfilEnum
import com.peixotop.pontointeligente.enums.TipoEnum
import com.peixotop.pontointeligente.security.FuncionarioPrincipal
import com.peixotop.pontointeligente.security.IAuthenticationFacade
import com.peixotop.pontointeligente.services.FuncionarioService
import com.peixotop.pontointeligente.services.LancamentoService
import org.apache.commons.lang3.EnumUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.validation.Valid


@RestController
@RequestMapping("/api/lancamentos")
class LancamentoController(val lancamentoService: LancamentoService,
                           val funcionarioService: FuncionarioService) {

    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @Autowired
    private val authenticationFacade: IAuthenticationFacade? = null

    @Value("\${paginacao.qtd_por_pagina}")
    val qtdPorPagina: Int = 0

    @PostMapping
    fun adicionar(@Valid @RequestBody lancamentoDto: LancamentoDto,
                  result: BindingResult): ResponseEntity<Response<LancamentoDto>> {

        val response: Response<LancamentoDto> = Response<LancamentoDto>()


        validarFuncionario(lancamentoDto, result)

        if (!pertenceAoUsuario(lancamentoDto.funcionarioId!!)) {
            result.addError(ObjectError("funcionario",
                    "Você não pode adicionar lançamentos do funcionário " + lancamentoDto.funcionarioId))
        }

        validaTipoLancamento(lancamentoDto.tipo, result)
        validaDataLancamento(lancamentoDto.data, result)

        if (result.hasErrors()) {
            for (error in result.allErrors) error.defaultMessage?.let { response.errors.add(it) }
            return ResponseEntity.badRequest().body(response)
        }

        val lancamento: Lancamento =
                lancamentoService.persistir(converterDtoParaLancamento(lancamentoDto, result))

        response.data = converterLancamentoParaDto(lancamento)
        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(value = ["/{id}"])
    fun atualizar(@PathVariable("id") id: String,
                  @Valid @RequestBody lancamentoDto: LancamentoDto,
                  result: BindingResult):
            ResponseEntity<Response<LancamentoDto>> {

        val response: Response<LancamentoDto> = Response<LancamentoDto>()

        val lancamento: Lancamento? = lancamentoService.buscarPorId(id)

        if (lancamento == null) result.addError(ObjectError("lancamento",
                "O Lancamento de id $id não foi encontrado."))

        if (lancamentoDto.id != id) result.addError(ObjectError("lancamento",
                "O ID do objeto enviado (${lancamentoDto.id}) é diferente do ID passado como parâmetro ($id)"))

        validarFuncionario(lancamentoDto, result)
        validaTipoLancamento(lancamentoDto.tipo, result)
        validaDataLancamento(lancamentoDto.data, result)

        if (result.hasErrors()) {
            for (error in result.allErrors) error.defaultMessage?.let { response.errors.add(it) }
            return ResponseEntity.badRequest().body(response)
        }

        var lancamentoAtualizado: Lancamento = converterDtoParaLancamento(lancamentoDto, result)
        lancamentoAtualizado = lancamentoService.persistir(lancamentoAtualizado)

        response.data = converterLancamentoParaDto(lancamentoAtualizado)
        return ResponseEntity.ok(response)
    }

    @GetMapping(value = ["/{id}"])
    fun listarPorId(@PathVariable("id") id: String): ResponseEntity<Response<LancamentoDto>> {
        val response: Response<LancamentoDto> = Response<LancamentoDto>()
        val lancamento: Lancamento? = lancamentoService.buscarPorId(id)

        if (lancamento == null) {
            response.errors.add("Lançamento $id não encontrado")
            return ResponseEntity.badRequest().body(response)
        }

        if (!pertenceAoUsuario(lancamento.funcionarioId)) {
            response.errors.add("Você não tem acesso ao Lançamento $id")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
        }

        response.data = converterLancamentoParaDto(lancamento)
        return ResponseEntity.ok(response)
    }

    @GetMapping(value = ["/funcionario/{funcionarioId}"])
    fun listarPorfuncionarioId(@PathVariable("funcionarioId") funcionarioId: String,
                               @RequestParam(value = "pag", defaultValue = "0") pag: Int,
                               @RequestParam(value = "ord", defaultValue = "id") ord: String,
                               @RequestParam(value = "dir", defaultValue = "DESC") dir: String):
            ResponseEntity<Response<Page<LancamentoDto>>> {

        val response: Response<Page<LancamentoDto>> = Response<Page<LancamentoDto>>()

        if (!pertenceAoUsuario(funcionarioId)) {
            response.errors.add("Você não tem acesso aos Lançamentos do usuário $funcionarioId")
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
        }

        val direction: String = dir.toUpperCase()

        if (!EnumUtils.isValidEnum(Sort.Direction::class.java, direction)) {
            response.errors.add("Tipo de direção não suportado. Tipos Suportados: " +
                    Sort.Direction.values().joinToString())
            return ResponseEntity.badRequest().body(response)
        }

        val pageRequest: PageRequest = PageRequest.of(pag, qtdPorPagina, Sort.Direction.valueOf(direction), ord)
        val lancamentos: Page<Lancamento> = lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest)

        val lancamentoDto: Page<LancamentoDto> =
                lancamentos.map { lancamento -> converterLancamentoParaDto(lancamento) }

        response.data = lancamentoDto

        return ResponseEntity.ok(response)
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping(value = ["/{id}"])
    fun remover(@PathVariable("id") id: String): ResponseEntity<Response<String>> {
        val response: Response<String> = Response<String>()
        val lancamento: Lancamento? = lancamentoService.buscarPorId(id)

        if (lancamento == null) {
            response.errors.add("Erro ao remover o lançamento $id. Registro não encontrado")
            return ResponseEntity.badRequest().body(response)
        }

        lancamentoService.remover(id)
        response.data = "O lançamento $id foi removido com sucesso."
        return ResponseEntity.ok(response)
    }


    private fun converterDtoParaLancamento(lancamentoDto: LancamentoDto, result: BindingResult): Lancamento {
        if (lancamentoDto.id != null) {
            val lancamento: Lancamento? = lancamentoService.buscarPorId(lancamentoDto.id)
            if (lancamento == null) result.addError(ObjectError("lancamento",
                    "Lançamento não encontrado."))
        }

        return Lancamento(
                LocalDateTime.parse(lancamentoDto.data, dateTimeFormatter),
                TipoEnum.valueOf(lancamentoDto.tipo),
                lancamentoDto.funcionarioId!!,
                lancamentoDto.descricao,
                lancamentoDto.localizacao,
                lancamentoDto.id
        )
    }

    private fun converterLancamentoParaDto(lancamento: Lancamento?): LancamentoDto =
            LancamentoDto(
                    lancamento?.data.toString(),
                    lancamento?.tipo.toString(),
                    lancamento?.funcionarioId,
                    lancamento?.descricao,
                    lancamento?.localizacao,
                    lancamento?.id
            )

    private fun validarFuncionario(lancamentoDto: LancamentoDto, result: BindingResult) {
        if (lancamentoDto.funcionarioId.isNullOrBlank()) {
            result.addError(ObjectError("funcionario",
                    "Funcionário Não Informado."))
            return
        }

        val funcionario: Funcionario? = funcionarioService.buscarPorId(lancamentoDto.funcionarioId)

        if (funcionario == null) {
            result.addError(ObjectError("funcionario",
                    "Funcionário não encontrado."))
        }
    }

    private fun validaTipoLancamento(valorEnum: String,
                                     result: BindingResult) {
        if (!EnumUtils.isValidEnum(TipoEnum::class.java,
                        valorEnum)) {
            result.addError(ObjectError("lancamento",
                    "Tipo de lancamento Não existente. Tipos Suportados: " +
                            TipoEnum.values().joinToString()))
        }
    }

    private fun validaDataLancamento(data: String?, result: BindingResult) {
        try {
            LocalDateTime.parse(data, dateTimeFormatter)
        } catch (e: DateTimeParseException) {
            result.addError(ObjectError("lancamento",
                    "Data do lançamento fora do padrão ISO_LOCAL_DATE_TIME"))
        }
    }

    private fun retornaUsuarioLogado(): FuncionarioPrincipal {
        val authentication = authenticationFacade!!.getAuthentication()
        return authentication.principal as FuncionarioPrincipal
    }

    private fun pertenceAoUsuario(funcionarioId: String): Boolean {
        val usuarioLogado = retornaUsuarioLogado()
        if (usuarioLogado.getPerfilFuncionario() == PerfilEnum.ROLE_ADMIN ||
                usuarioLogado.getUserId() == funcionarioId) return true

        return false
    }
}