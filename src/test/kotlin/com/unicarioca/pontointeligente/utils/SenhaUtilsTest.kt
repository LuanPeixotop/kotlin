package com.unicarioca.pontointeligente.utils

import org.junit.jupiter.api.Assertions
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.junit.jupiter.api.Test

class SenhaUtilsTest {
    private val SENHA = "SenhaFoda"
    private val bCryptEncoder = BCryptPasswordEncoder()

    @Test
    fun testGerarBcrypt() {
        val bcrypt = SenhaUtils().gerarBcrypt(SENHA)
        Assertions.assertTrue(bCryptEncoder.matches(SENHA, bcrypt))
    }
}