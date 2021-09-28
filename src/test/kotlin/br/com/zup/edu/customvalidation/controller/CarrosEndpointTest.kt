package br.com.zup.edu.customvalidation.controller

import br.com.zup.edu.CarrosGrpcRequest
import br.com.zup.edu.CarrosGrpcServiceGrpc
import br.com.zup.edu.customvalidation.model.Carro
import br.com.zup.edu.customvalidation.repository.CarroRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
internal class CarrosEndpointTest(
    val repository: CarroRepository,
    val grpcClient: CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub
) {

    /**
     * 1. happy path - ok!
     * 2. quando a placa de um novo carro ja esta cadastrada - ok!
     * 3. quando os dados de entrada sao invalidos - ok!
     * */

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve adicionar um novo carro`() {
        // cenário

        // ação
        val response = grpcClient.send(
            CarrosGrpcRequest.newBuilder()
                .setModelo("Punto")
                .setPlaca("ASD-1234")
                .build()
        )

        // validação
        with(response) {
            assertNotNull(id)
            assertTrue(repository.existsById(id))//efeito colateral
        }

    }

    @Test
    fun `nao deve cadastrar novo carro com placa existente no banco de dados`() {
        //cenario
        val existente = repository.save(Carro(modelo = "Palio", placa = "ZXC-6789"))

        //ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.send(
                CarrosGrpcRequest.newBuilder()
                    .setModelo("Ferrari")
                    .setPlaca(existente.placa)
                    .build()
            )
        }

        //validação
        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Carro com placa já cadastrada", status.description)
        }
    }

    @Test
    fun `nao deve cadastrar novo carro com dados invalidos`() {
        //cenario

        //ação
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.send(
                CarrosGrpcRequest.newBuilder()
                    .setModelo("")
                    .setPlaca("")
                    .build()
            )
        }

        //validação
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados de entrada inválidos!", status.description)
        }
    }

    @Factory
    class CLients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub {
            return CarrosGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}