package br.com.zup.edu.customvalidation.repository

import br.com.zup.edu.customvalidation.model.Carro
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface CarroRepository : JpaRepository<Carro, Long> {

    fun  existsByPlaca(placa: String): Boolean
}