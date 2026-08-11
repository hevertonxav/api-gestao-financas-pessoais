package com.heverton.api_gestao_financas_pessoais.dtos.responses;

import com.heverton.api_gestao_financas_pessoais.entities.Periodo;

import java.math.BigDecimal;

public record ResumoFinanceiroResponseDTO(

        Periodo periodo,
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldo

) {
}
