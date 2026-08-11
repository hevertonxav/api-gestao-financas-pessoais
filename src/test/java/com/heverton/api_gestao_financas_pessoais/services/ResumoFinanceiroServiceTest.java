package com.heverton.api_gestao_financas_pessoais.services;

import com.heverton.api_gestao_financas_pessoais.dtos.responses.ResumoFinanceiroResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.Periodo;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import com.heverton.api_gestao_financas_pessoais.repositories.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class ResumoFinanceiroServiceTest {

    @InjectMocks
    private ResumoFinanceiroService resumoFinanceiroService;

    @Mock
    private TransacaoRepository transacaoRepository;


    @Test
    public void obterResumoFinanceiro() {


        Periodo periodo = new Periodo(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 5, 1)
        );

        when(transacaoRepository.
                somarPorTipoEPeriodo(
                        TipoTransacao.ENTRADA,
                        periodo.dataInicio(),
                        periodo.dataFim()
                )).thenReturn(new BigDecimal("9800.00"));

        when(transacaoRepository.
                somarPorTipoEPeriodo(
                        TipoTransacao.SAIDA,
                        periodo.dataInicio(),
                        periodo.dataFim()
                )).thenReturn(new BigDecimal("1700.00"));

        ResumoFinanceiroResponseDTO resultado =
                resumoFinanceiroService.obterResumoFinanceiro(periodo);


        assertEquals(periodo, resultado.periodo());
        assertEquals(new BigDecimal("9800.00"), resultado.totalEntradas());
        assertEquals(new BigDecimal("1700.00"), resultado.totalSaidas());
        assertEquals(new BigDecimal("8100.00"), resultado.saldo());
    }
}