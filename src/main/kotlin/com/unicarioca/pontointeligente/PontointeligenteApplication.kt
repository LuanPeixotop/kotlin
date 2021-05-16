package com.unicarioca.pontointeligente

import com.unicarioca.pontointeligente.documents.Empresa
import com.unicarioca.pontointeligente.documents.Funcionario
import com.unicarioca.pontointeligente.enums.PerfilEnum
import com.unicarioca.pontointeligente.repositories.EmpresaRepository
import com.unicarioca.pontointeligente.repositories.FuncionarioRepository
import com.unicarioca.pontointeligente.utils.SenhaUtils
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
				SenhaUtils().gerarBcrypt("admin"),
				"12345678909",
				PerfilEnum.ROLE_ADMIN,
				empresa.id!!
		))
        funcionarioRepository.save(admin)

        val funcionario: Funcionario = funcionarioRepository.save(Funcionario(
				"Funcionario",
				"funcionario@email.com",
				SenhaUtils().gerarBcrypt("funcionario"),
				"12345678901",
				PerfilEnum.ROLE_USUARIO,
				empresa.id
		))

		val funcionario2: Funcionario = funcionarioRepository.save(Funcionario(
				"Funcionario2",
				"funcionario2@email.com",
				SenhaUtils().gerarBcrypt("funcionario2"),
				"12345678901",
				PerfilEnum.ROLE_USUARIO,
				empresa.id
		))

		println("Empresa ID: " + empresa.id)
		println("Admin ID: " + admin.id)
		println("Funcionario ID: " + funcionario.id)
		println("Funcionario2 ID: " + funcionario2.id)
    }

}

fun main(args: Array<String>) {
    runApplication<PontointeligenteApplication>(*args)
}
