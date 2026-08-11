package com.heverton.api_gestao_financas_pessoais.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoriaRequestDTO(

        @NotBlank(message = "Campo requerido")
        @Size(
                min = 2,
                max =50,
                message = "O campo nome deve ter no mínimo {min} e no máximo {max} caracteres."
        )
        String  nome
) {

}
