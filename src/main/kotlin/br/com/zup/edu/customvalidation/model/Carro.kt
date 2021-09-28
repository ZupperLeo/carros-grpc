package br.com.zup.edu.customvalidation.model

import br.com.zup.edu.customvalidation.validator.Placa
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Carro(
    @field:NotBlank @Column(nullable = false) val modelo: String?,
    @field:NotBlank @field:Placa @Column(nullable = false, unique = true) val placa: String?
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:  Long? = null
}