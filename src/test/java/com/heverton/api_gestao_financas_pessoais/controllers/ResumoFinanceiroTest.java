package com.heverton.api_gestao_financas_pessoais.controllers;


import com.heverton.api_gestao_financas_pessoais.dtos.responses.CategoriaResponseDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.ResumoFinanceiroResponseDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.TransacaoResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.Periodo;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import com.heverton.api_gestao_financas_pessoais.services.ResumoFinanceiroService;
import com.heverton.api_gestao_financas_pessoais.services.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import utils.ControllerTestHelper;
import utils.MockMvcHelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ResumoFinanceiroController.class)
public class ResumoFinanceiroTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResumoFinanceiroService resumoFinanceiroService;

    @MockitoBean
    private TransacaoService transacaoService;

    @Autowired
    private ObjectMapper objectMapper;
    Periodo periodo;

    private static final String ENDPOINT_RESUMO =
            "/v1/resumo";
    private static final String URL_BASE_ERROS =
            "https://api.financas.com.br/erros/";

    private List<CategoriaResponseDTO> categoriaResponseDTOs;
    private List<TransacaoResponseDTO> transacaoResponseDTOs;
    private ResumoFinanceiroResponseDTO resumoFinanceiroResponseDTO;

    @BeforeEach
    public void setUp(){


        categoriaResponseDTOs = List.of(
                new CategoriaResponseDTO(UUID.fromString(
                        "c56916df-a1b8-41f3-b3ee-191fee8c2de0"),
                        "Alimentação" ),
                new CategoriaResponseDTO(
                        UUID.fromString("68725262-bbb3-4164-a383-17461f2b21c3"),
                        "Lazer" )
        );

        transacaoResponseDTOs = List.of(
                new TransacaoResponseDTO(
                        UUID.fromString("65cdc031-d716-45ab-a1c2-31c48968cb7c"),
                        TipoTransacao.ENTRADA,
                        new BigDecimal(3000.00),
                        LocalDate.of(2026, 2, 5),
                        "Salário do mês",
                        null,
                        OffsetDateTime.now()
                ),

                new TransacaoResponseDTO(
                        UUID.fromString("18275cca-e9eb-4cfc-beea-fb107968a7cc"),
                        TipoTransacao.SAIDA,
                        new BigDecimal(900.00),
                        LocalDate.of(2026, 2, 6),
                        "Compras do mês",
                        new CategoriaResponseDTO(
                                UUID.fromString("c56916df-a1b8-41f3-b3ee-191fee8c2de0"),
                        "Alimentação"),
                        OffsetDateTime.now()
                ),

                new TransacaoResponseDTO(
                        UUID.fromString("881e8a21-674d-4da0-8bef-6388a5a8ed70"),
                        TipoTransacao.ENTRADA,
                        new BigDecimal(300.00),
                        LocalDate.of(2026, 2, 7),
                        "Freelance",
                        null,
                        OffsetDateTime.now()),

                new TransacaoResponseDTO(
                        UUID.fromString("18275cca-e9eb-4cfc-beea-fb107968a7cc"),
                        TipoTransacao.SAIDA,
                        new BigDecimal(100.00),
                        LocalDate.of(2026, 2, 8),
                        "Kart",
                        new CategoriaResponseDTO(
                                UUID.fromString("68725262-bbb3-4164-a383-17461f2b21c3"),
                                "Lazer" ),
                        OffsetDateTime.now()
                )
        );



    }

    @Test
    public void deveSomarTodasAsTransacoes() throws Exception{

        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 5),
                LocalDate.of(2026, 2, 8)
        );

        resumoFinanceiroResponseDTO = new ResumoFinanceiroResponseDTO(
                periodo,
                new BigDecimal("3300.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("2300.00")
        );

        when(resumoFinanceiroService.obterResumoFinanceiro(any(Periodo.class)))
                .thenReturn(resumoFinanceiroResponseDTO);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                get(ENDPOINT_RESUMO +
                        "?dataInicio=" + periodo.dataInicio() +
                        "&dataFim=" + periodo.dataFim()),
                resumoFinanceiroResponseDTO,
                objectMapper
        );

        ControllerTestHelper.validarResposta(
                200,
                result,
                Map.of(
                        "periodo.dataInicio", "2026-02-05",
                        "periodo.dataFim", "2026-02-08",
                        "totalEntradas", 3300.00,
                        "totalSaidas", 1000.00,
                        "saldo", 2300.00
                )
        );

        verify(resumoFinanceiroService).obterResumoFinanceiro(any(Periodo.class));
    }

    @Test
    public void deveSomarTodasAsTransacoesExcetoAUltima() throws Exception{

        Periodo periodo = new Periodo(
                LocalDate.of(2026, 2, 5),
                LocalDate.of(2026, 2, 7)
        );

        resumoFinanceiroResponseDTO = new ResumoFinanceiroResponseDTO(
                periodo,
                new BigDecimal("3300.00"),
                new BigDecimal("900.00"),
                new BigDecimal("2400.00")
        );

        when(resumoFinanceiroService.obterResumoFinanceiro(any(Periodo.class)))
                .thenReturn(resumoFinanceiroResponseDTO);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                get(ENDPOINT_RESUMO +
                        "?dataInicio=" + periodo.dataInicio() +
                        "&dataFim=" + periodo.dataFim()),
                resumoFinanceiroResponseDTO,
                objectMapper
        );

        ControllerTestHelper.validarResposta(
                200,
                result,
                Map.of(
                        "periodo.dataInicio", "2026-02-05",
                        "periodo.dataFim", "2026-02-07",
                        "totalEntradas", 3300.00,
                        "totalSaidas", 900.00,
                        "saldo", 2400.00
                )
        );

        verify(resumoFinanceiroService).obterResumoFinanceiro(any(Periodo.class));
    }

}
