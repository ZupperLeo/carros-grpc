package br.com.zup.edu

import br.com.zup.edu.customvalidation.model.Carro
import br.com.zup.edu.customvalidation.repository.CarroRepository
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest(
    rollback = false, //Desliga o rollback
    transactionMode = TransactionMode.SINGLE_TRANSACTION, //faz com que o @BeforeEach rode na mesma transação do @Test
    transactional = false
)
class CarrosGrpcTest {

    /**
     * Metafora da louça suja
     * louça: sujou, limpou -> @AfterEach
     * louça: limpou, usou -> @BeforeEach [Favorita do Ponte]
     * louça: usa louça descartavel -> rollback
     * louça: uso a louça, jogo fora, compro nova louça -> recriar o banco de dados a cada novo teste
     * */

    @Inject
    lateinit var repository: CarroRepository

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @AfterEach
    fun cleanUp(){
        repository.deleteAll()
    }

    @Test
    fun `deve inserir um novo carro`() {
        //cenário

        //ação
        repository.save(Carro(modelo = "Gol", placa = "AAA1A34"))

        //validação
        assertEquals(1, repository.count())

    }

    @Test
    fun `deve encontrar um carro pela placa`() {
        //cenário
        repository.save(Carro(modelo = "Gol", placa = "BBB1B34"))

        //ação
        val encontrado = repository.existsByPlaca("BBB1B34")

        //validação
        assertTrue(encontrado)
    }

}
