package com.unicarioca.pontointeligente.services.impl

import com.unicarioca.pontointeligente.documents.Empresa
import com.unicarioca.pontointeligente.repositories.EmpresaRepository
import com.unicarioca.pontointeligente.services.EmpresaService
import org.springframework.stereotype.Service

@Service
class EmpresaServiceImpl(val empresaRepository: EmpresaRepository) : EmpresaService {
    override fun buscarPorCnpj(cnpj: String): Empresa? = empresaRepository.findByCnpj(cnpj)

    override fun persistir(empresa: Empresa): Empresa = empresaRepository.save(empresa)
}