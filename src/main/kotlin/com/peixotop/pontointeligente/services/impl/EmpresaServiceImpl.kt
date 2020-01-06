package com.peixotop.pontointeligente.services.impl

import com.peixotop.pontointeligente.documents.Empresa
import com.peixotop.pontointeligente.repositories.EmpresaRepository
import com.peixotop.pontointeligente.services.EmpresaService
import org.springframework.stereotype.Service

@Service
class EmpresaServiceImpl(val empresaRepository: EmpresaRepository) : EmpresaService {
    override fun buscarPorCnpj(cnpj: String): Empresa? = empresaRepository.findByCnpj(cnpj)

    override fun persistir(empresa: Empresa): Empresa = empresaRepository.save(empresa)
}