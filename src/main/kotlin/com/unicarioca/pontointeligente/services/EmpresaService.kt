package com.unicarioca.pontointeligente.services

import com.unicarioca.pontointeligente.documents.Empresa

interface EmpresaService {
    fun buscarPorCnpj(cnpj: String) : Empresa?

    fun persistir(empresa: Empresa) : Empresa
}