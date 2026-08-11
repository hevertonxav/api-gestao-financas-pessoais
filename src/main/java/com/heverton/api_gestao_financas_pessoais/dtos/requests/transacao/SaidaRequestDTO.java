package com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.UUID;

public record SaidaRequestDTO (

        @NotNull(message = "Campo requerido")
        @DecimalMin(message = "O valor mínimo deve ser 0.01", value = "0.01")
        BigDecimal valor,

        @PastOrPresent(message = "Não pode ser uma data futura")
        LocalDate data,

        @NotBlank(message = "Campo requerido")
        @Size(
                min = 3,
                max = 100,
                message = "O campo nome deve ter no mínimo {min} e no máximo {max} caracteres."
        )
        String descricao,

        @NotNull(message = "Informe o id da Categoria")
        UUID idCategoria
) {
}
