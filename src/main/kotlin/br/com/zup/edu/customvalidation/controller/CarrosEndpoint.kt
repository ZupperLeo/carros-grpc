package br.com.zup.edu.customvalidation.controller

import br.com.zup.edu.CarrosGrpcReply
import br.com.zup.edu.CarrosGrpcRequest
import br.com.zup.edu.CarrosGrpcServiceGrpc
import br.com.zup.edu.customvalidation.model.Carro
import br.com.zup.edu.customvalidation.repository.CarroRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarrosEndpoint(@Inject val repository: CarroRepository) : CarrosGrpcServiceGrpc.CarrosGrpcServiceImplBase() {

    override fun send(request: CarrosGrpcRequest?, responseObserver: StreamObserver<CarrosGrpcReply>?) {

        if (request != null) {
            if (repository.existsByPlaca(request.placa)) {
                responseObserver?.onError(
                    Status.ALREADY_EXISTS
                        .withDescription("Carro com placa já cadastrada")
                        .asRuntimeException()
                )
                return
            }
        }

        val carro = Carro(modelo = request?.modelo, placa = request?.placa)

        try {
            repository.save(carro)
        } catch (e: ConstraintViolationException) {
            responseObserver?.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Dados de entrada inválidos!")
                    .asRuntimeException()
            )
            return
        }

        responseObserver?.onNext(
            CarrosGrpcReply
                .newBuilder()
                .setId(carro.id!!)
                .build()
        )

        responseObserver?.onCompleted()
    }
}