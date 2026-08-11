package com.heverton.api_gestao_financas_pessoais.services;

import com.heverton.api_gestao_financas_pessoais.dtos.responses.ResumoFinanceiroResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.Periodo;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import com.heverton.api_gestao_financas_pessoais.repositories.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ResumoFinanceiroService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    public ResumoFinanceiroResponseDTO obterResumoFinanceiro (Periodo periodo){


        BigDecimal totalEntradas = transacaoRepository.somarPorTipoEPeriodo(
                TipoTransacao.ENTRADA,
                periodo.dataInicio(),
                periodo.dataFim()
        );
        BigDecimal totalSaidas = transacaoRepository.somarPorTipoEPeriodo(
                TipoTransacao.SAIDA,
                periodo.dataInicio(),
                periodo.dataFim()
        );
        BigDecimal saldo = totalEntradas.subtract(totalSaidas);
        
        return new ResumoFinanceiroResponseDTO(
                periodo,
                totalEntradas,
                totalSaidas,
                saldo
        );
    }


}
