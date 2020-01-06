package com.peixotop.pontointeligente.services

import com.peixotop.pontointeligente.documents.Empresa

interface EmpresaService {
    fun buscarPorCnpj(cnpj: String) : Empresa?

    fun persistir(empresa: Empresa) : Empresa
}