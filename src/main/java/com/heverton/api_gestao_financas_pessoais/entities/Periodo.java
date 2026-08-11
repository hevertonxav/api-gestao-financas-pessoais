package com.heverton.api_gestao_financas_pessoais.entities;

import com.heverton.api_gestao_financas_pessoais.exceptions.RegraNegocioException;

import java.time.LocalDate;

public record Periodo(
        LocalDate dataInicio,
        LocalDate dataFim) {
    public Periodo {

        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new RegraNegocioException(
                    "Data inicial não pode ser depois da data final.");
        }


    }
}

