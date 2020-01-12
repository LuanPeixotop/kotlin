package com.peixotop.pontointeligente

import com.peixotop.pontointeligente.documents.Empresa
import com.peixotop.pontointeligente.documents.Funcionario
import com.peixotop.pontointeligente.enums.PerfilEnum
import com.peixotop.pontointeligente.repositories.EmpresaRepository
import com.peixotop.pontointeligente.repositories.FuncionarioRepository
import com.peixotop.pontointeligente.utils.SenhaUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PontointeligenteApplication(val empresaRepository: EmpresaRepository,
                                  val funcionarioRepository: FuncionarioRepository,
								  val lancamentoRepository: FuncionarioRepository) : CommandLineRunner {
    override fun run(vararg args: String?) {
        empresaRepository.deleteAll()
        funcionarioRepository.deleteAll()
		lancamentoRepository.deleteAll()

        val empresa: Empresa = empresaRepository.save(Empresa(
				"Empresa",
				"5740125000171"
		))

        val admin: Funcionario =  funcionarioRepository.save(Funcionario(
				"Admin",
				"admin@email.com",
				SenhaUtils().gerarBcrypt("senhafoda"),
				"12345678909",
				PerfilEnum.ROLE_ADMIN,
				empresa.id!!
		))
        funcionarioRepository.save(admin)

        val funcionario: Funcionario = funcionarioRepository.save(Funcionario(
				"Funcionario",
				"funcionario@email.com",
				SenhaUtils().gerarBcrypt("senhafodadefuncionario"),
				"12345678901",
				PerfilEnum.ROLE_USUARIO,
				empresa.id!!
		))

		println("Empresa ID: " + empresa.id)
		println("Admin ID: " + admin.id)
		println("Funcionario ID: " + funcionario.id)
    }

}

fun main(args: Array<String>) {
    runApplication<PontointeligenteApplication>(*args)
}
