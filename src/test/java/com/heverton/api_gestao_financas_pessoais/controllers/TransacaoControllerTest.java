package com.heverton.api_gestao_financas_pessoais.controllers;

import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.EntradaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.SaidaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.TransacaoUpdateDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.CategoriaResponseDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.TransacaoResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import com.heverton.api_gestao_financas_pessoais.exceptions.RecursoNaoEncontradoException;
import com.heverton.api_gestao_financas_pessoais.exceptions.RegraNegocioException;
import com.heverton.api_gestao_financas_pessoais.services.TransacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import utils.ControllerTestAssertions;
import utils.ControllerTestHelper;
import utils.MockMvcHelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransacaoService transacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    private EntradaRequestDTO entradaRequestDTO;
    private SaidaRequestDTO saidaRequestDTO;
    private TransacaoUpdateDTO transacaoUpdateDTO;
    private TransacaoResponseDTO entradaResponseDTO;
    private TransacaoResponseDTO saidaResponseDTO;
    private UUID idTransacao;
    private UUID idCategoria;
    private OffsetDateTime dataCriacao;
    LocalDate dataInicio;
    LocalDate dataFim;

    private static final String URL_BASE_ERROS =
            "https://api.financas.com.br/erros/";

    private static final String ENDPOINT_TRANSACOES =
            "/v1/transacoes";

    @BeforeEach
    void setUp() {
        idTransacao = java.util.UUID.randomUUID();
        idCategoria = java.util.UUID.randomUUID();
        dataCriacao = OffsetDateTime.now();

        dataInicio = LocalDate.of(2026, 6, 1);
        dataFim = LocalDate.of(2026, 7, 1);


        entradaRequestDTO = new EntradaRequestDTO(
                new BigDecimal("200.00"),
                LocalDate.of(2026, 6, 21),
                "Estorno do cartão de crédito"
        );
        saidaRequestDTO = new SaidaRequestDTO(
                new BigDecimal("70.00"),
                LocalDate.of(2026, 7, 1),
                "Cinema",
                idCategoria
        );
        entradaResponseDTO = new TransacaoResponseDTO(
                idTransacao,
                TipoTransacao.ENTRADA,
                new BigDecimal("200.00"),
                LocalDate.of(2026, 6, 21),
                "Estorno do cartão de crédito",
                null,
                dataCriacao
        );

        saidaResponseDTO = new TransacaoResponseDTO(
                idTransacao,
                TipoTransacao.SAIDA,
                new BigDecimal("70.00"),
                LocalDate.of(2026, 7, 1),
                "Cinema",
                new CategoriaResponseDTO(
                        idCategoria,
                        "Lazer"
                ),
                dataCriacao
        );

        transacaoUpdateDTO = new TransacaoUpdateDTO(
                new BigDecimal("200.00"),
                LocalDate.of(2026, 6, 21),
                "Descrição atualizada",
                idCategoria
        );
    }

    @Test
    @DisplayName("Deve salvar uma entrada retornando código 201")
    void deveSalvarUmaEntradaRetornandoCodigo201() throws Exception {

        when(transacaoService.salvarEntrada(any(EntradaRequestDTO.class)))
                .thenReturn(entradaResponseDTO);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_TRANSACOES + "/entradas"),
                entradaRequestDTO,
                objectMapper
        );

        ControllerTestHelper.validarResposta(
                201,
                result,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "ENTRADA",
                        "valor", 200.00,
                        "data", "2026-06-21",
                        "descricao", "Estorno do cartão de crédito",
                        "categoria", ControllerTestHelper.EMPTY,
                        "dataCriacao", ControllerTestHelper.EXISTS
                ));

        verify(transacaoService).salvarEntrada(any(EntradaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve salvar uma saida retornando código 201")
    void deveSalvarUmaSaidaRetornandoCodigo201() throws Exception {

        when(transacaoService.salvarSaida(any(SaidaRequestDTO.class)))
                .thenReturn(saidaResponseDTO);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_TRANSACOES + "/saidas"),
                saidaRequestDTO,
                objectMapper
        );

        ControllerTestHelper.validarResposta(
                201,
                result,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "SAIDA",
                        "valor", 70.00,
                        "descricao", "Cinema",
                        "data", "2026-07-01",
                        "categoria.idCategoria", idCategoria.toString(),
                        "categoria.nome", "Lazer",
                        "dataCriacao", ControllerTestHelper.EXISTS
                ));

        verify(transacaoService).salvarSaida(any(SaidaRequestDTO.class));
    }


    @Test
    @DisplayName("Deve retornar erros de validacões para entradas possibilidade 01")
    public void deveRetornarErrosValidacoesParaEntradasPossibilidade01() throws Exception {

        EntradaRequestDTO request = new EntradaRequestDTO(
                null,
                LocalDate.of(2027, 7, 1),
                ""
        );

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_TRANSACOES + "/entradas"),
                request,
                objectMapper
        );

        Map<String, List<String>> erros = Map.of(
                "valor", List.of("Campo requerido"),
                "data", List.of("Não pode ser uma data futura"),
                "descricao", List.of("Campo requerido",
                        "O campo nome deve ter no mínimo 3 e no máximo 100 caracteres.")
        );

        ControllerTestAssertions.validarErroDadosInvalidos(result, ENDPOINT_TRANSACOES + "/entradas", erros);
        verify(transacaoService, never()).salvarEntrada(any(EntradaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erros de validacões para entradas possibilidade 02")
    public void deveRetornarErrosValidacoesParaEntradasPossibilidade02() throws Exception {

        EntradaRequestDTO request = new EntradaRequestDTO(
                new BigDecimal(0.00),
                LocalDate.of(2026, 7, 1),
                "Estorno do cartão de crédito"
        );

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_TRANSACOES + "/entradas"),
                request,
                objectMapper
        );

        Map<String, List<String>> erros = Map.of("valor", List.of("O valor mínimo deve ser 0.01"));

        ControllerTestAssertions.validarErroDadosInvalidos(result, ENDPOINT_TRANSACOES + "/entradas", erros);
        verify(transacaoService, never()).salvarEntrada(any(EntradaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erros de validacões para saídas possibilidade 01")
    public void deveRetornarErrosValidacoesParaSaidasPossibilidade01() throws Exception {

        SaidaRequestDTO request = new SaidaRequestDTO(
                null,
                LocalDate.of(2027, 7, 1),
                "",
                idCategoria
        );

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_TRANSACOES + "/saidas"),
                request,
                objectMapper
        );

        Map<String, List<String>> erros = Map.of(
                "valor", List.of("Campo requerido"),
                "data", List.of("Não pode ser uma data futura"),
                "descricao", List.of("Campo requerido",
                        "O campo nome deve ter no mínimo 3 e no máximo 100 caracteres.")
        );
    }

    @Test
    @DisplayName("Deve retornar erros de validacões para saídas possibilidade 02")
    public void deveRetornarErrosValidacoesParaSaidasPossibilidade02() throws Exception {

        SaidaRequestDTO request = new SaidaRequestDTO(
                new BigDecimal(0.00),
                LocalDate.of(2026, 7, 1),
                "Estorno do cartão de crédito",
                idCategoria
        );

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_TRANSACOES + "/saidas"),
                request,
                objectMapper
        );

        Map<String, List<String>> erros = Map.of("valor", List.of("O valor mínimo deve ser 0.01"));
    }

    @Test
    @DisplayName("Deve retornar NOT FOUND 404 devido Recurso não encontrado")
    public void naoDeveCriarNovaTransacaoSaidasMasRetornarCodido404() throws Exception {

        doThrow(new RecursoNaoEncontradoException("Recurso não encontrado"))
                .when(transacaoService).salvarSaida(any(SaidaRequestDTO.class));

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_TRANSACOES + "/saidas"),
                saidaRequestDTO,
                objectMapper
        );

        ControllerTestAssertions.validarErro(
                result,
                404,
                "recurso não encontrado",
                "Recurso não encontrado",
                URL_BASE_ERROS + "recurso-nao-encontrado",
                ENDPOINT_TRANSACOES + "/saidas"
        );

        verify(transacaoService).salvarSaida(any(SaidaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve listar as entradas dentro do período informado")
    public void deveListarEntradasPorPeriodo() throws Exception {

        Page<TransacaoResponseDTO> page = new PageImpl<>(List.of(entradaResponseDTO));

        when(transacaoService.listarTodasEntradas(
                any(LocalDate.class),
                any(LocalDate.class),
                any(Pageable.class))
        ).thenReturn(page);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                get(ENDPOINT_TRANSACOES + "/entradas")
                        .param("dataInicio", "2026-06-01")
                        .param("dataFim", "2026-07-01")
        );

        ControllerTestHelper.validarRespostaPaginada(
                result,
                0,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "ENTRADA",
                        "valor", 200.00,
                        "data", "2026-06-21",
                        "descricao", "Estorno do cartão de crédito",
                        "categoria", ControllerTestHelper.EMPTY,
                        "dataCriacao", ControllerTestHelper.EXISTS
                ));

        verify(transacaoService).listarTodasEntradas(
                any(LocalDate.class),
                any(LocalDate.class),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Deve listar as saidas dentro do período informado")
    public void deveListarSaidasPorPeriodo() throws Exception {

        Page<TransacaoResponseDTO> page = new PageImpl<>(List.of(saidaResponseDTO));

        when(transacaoService.listarTodasSaidas(
                isNull(),
                any(LocalDate.class),
                any(LocalDate.class),
                any(Pageable.class))
        ).thenReturn(page);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                get(ENDPOINT_TRANSACOES + "/saidas")
                        .param("dataInicio", "2026-06-01")
                        .param("dataFim", "2026-07-01")
        );

        ControllerTestHelper.validarRespostaPaginada(
                result,
                0,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "SAIDA",
                        "valor", 70.00,
                        "data", "2026-07-01",
                        "descricao", "Cinema",
                        "dataCriacao", ControllerTestHelper.EXISTS,
                        "categoria.idCategoria", idCategoria.toString(),
                        "categoria.nome", "Lazer"
                ));

        verify(transacaoService).listarTodasSaidas(
                isNull(),
                any(LocalDate.class),
                any(LocalDate.class),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Deve listar entradas quando não houver período informado")
    public void deveListarTodasAsEntradasSemPeriodo() throws Exception {

        Page<TransacaoResponseDTO> page = new PageImpl<>(List.of(entradaResponseDTO));

        when(transacaoService.listarTodasEntradas(
                isNull(),
                isNull(),
                any(Pageable.class))
        ).thenReturn(page);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                get(ENDPOINT_TRANSACOES + "/entradas")
        );

        ControllerTestHelper.validarRespostaPaginada(
                result,
                0,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "ENTRADA",
                        "valor", 200.00,
                        "data", "2026-06-21",
                        "descricao", "Estorno do cartão de crédito",
                        "categoria", ControllerTestHelper.EMPTY,
                        "dataCriacao", ControllerTestHelper.EXISTS
                ));

        verify(transacaoService).listarTodasEntradas(
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Deve listar  saídas quando não houver período informado")
    public void deveListarTodasAsSaidasSemPeriodo() throws  Exception{

        Page<TransacaoResponseDTO> page = new PageImpl<>(List.of(saidaResponseDTO));

        when(transacaoService.listarTodasSaidas(
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class))
        ).thenReturn(page);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                get(ENDPOINT_TRANSACOES + "/saidas")
        );

        ControllerTestHelper.validarRespostaPaginada(
                result,
                0,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "SAIDA",
                        "valor", 70.00,
                        "data", "2026-07-01",
                        "descricao", "Cinema",
                        "dataCriacao", ControllerTestHelper.EXISTS,
                        "categoria.idCategoria", idCategoria.toString(),
                        "categoria.nome", "Lazer"
                ));

        verify(transacaoService).listarTodasSaidas(
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Deve listar todas as transaçoes por filtro de categoria")
    void deveListarPorFiltroDeCategoria() throws Exception{

        Page<TransacaoResponseDTO> page = new PageImpl<>(List.of(saidaResponseDTO));

        when(transacaoService.listarTodasSaidas(
                eq(idCategoria),
                isNull(),
                isNull(),
                any(Pageable.class))
        ).thenReturn(page);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                get(ENDPOINT_TRANSACOES + "/saidas")
                        .param("idCategoria", idCategoria.toString())
        );

        ControllerTestHelper.validarRespostaPaginada(
                result,
                0,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "SAIDA",
                        "valor", 70.00,
                        "data", "2026-07-01",
                        "descricao", "Cinema",
                        "dataCriacao", ControllerTestHelper.EXISTS,
                        "categoria.idCategoria", idCategoria.toString(),
                        "categoria.nome", "Lazer"
                ));

        verify(transacaoService).listarTodasSaidas(
                eq(idCategoria),
                isNull(),
                isNull(),
                any(Pageable.class)
        );

    }

    @Test
    @DisplayName("Deve listar todas as transaçoes existentes sem uso de filtros")
    void deveListarTodasTransacoesExistentesSemUsodeFiltros() throws Exception{

        Page<TransacaoResponseDTO> page = new PageImpl<>(List.of(entradaResponseDTO, saidaResponseDTO));

        when(transacaoService.listarTodasTransacoes(
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class))
        ).thenReturn(page);

        ResultActions result = MockMvcHelper.realizarRequisicao(mockMvc, get(ENDPOINT_TRANSACOES));

        ControllerTestHelper.validarRespostaPaginada(
                result,
                0,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "ENTRADA",
                        "valor", 200.00,
                        "data", "2026-06-21",
                        "descricao", "Estorno do cartão de crédito",
                        "categoria", ControllerTestHelper.EMPTY,
                        "dataCriacao", ControllerTestHelper.EXISTS
                ));

        ControllerTestHelper.validarRespostaPaginada(
                result,
                1,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "SAIDA",
                        "valor", 70.00,
                        "data", "2026-07-01",
                        "descricao", "Cinema",
                        "dataCriacao", ControllerTestHelper.EXISTS,
                        "categoria.idCategoria", idCategoria.toString(),
                        "categoria.nome", "Lazer"
                ));

        verify(transacaoService).listarTodasTransacoes(
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    @DisplayName("Deve retornar exceção devido id inexistente, tentativa de editar uma transação")
    public void naoDeveEditarTransacaoMasDeveRetornarNotFound404() throws Exception {

        when(transacaoService.editarTransacao(any(UUID.class), any(TransacaoUpdateDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException("Recurso não encontrado"));

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                put(ENDPOINT_TRANSACOES  + "/" + idTransacao),
                transacaoUpdateDTO,
                objectMapper
        );

        ControllerTestAssertions.validarErro(
                result,
                404,
                "recurso não encontrado",
                "Recurso não encontrado",
                URL_BASE_ERROS + "recurso-nao-encontrado",
                ENDPOINT_TRANSACOES  + "/" + idTransacao
        );

        verify(transacaoService).editarTransacao(any(UUID.class), any(TransacaoUpdateDTO.class));
    }

    @Test
    @DisplayName("Deve editar uma transação existente e retornar OK")
    public void deveEditarUmaTransacaoExistenteERetornarOK() throws Exception {

        when(transacaoService.editarTransacao(eq(idTransacao),any(TransacaoUpdateDTO.class)))
                .thenReturn(new TransacaoResponseDTO(
                        idTransacao,
                        TipoTransacao.ENTRADA,
                        new BigDecimal("200.00"),
                        LocalDate.of(2026, 6, 21),
                        "Descrição atualizada",
                        null,
                        OffsetDateTime.now()
                ));

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                put(ENDPOINT_TRANSACOES + "/" + idTransacao),
                transacaoUpdateDTO,
                objectMapper
        );

        ControllerTestHelper.validarResposta(
                200,
                result,
                Map.of(
                        "idTransacao", idTransacao.toString(),
                        "tipo", "ENTRADA",
                        "valor", 200.00,
                        "data", "2026-06-21",
                        "descricao", "Descrição atualizada",
                        "categoria", ControllerTestHelper.EMPTY,
                        "dataCriacao", ControllerTestHelper.EXISTS
                ));

        verify(transacaoService).editarTransacao(eq(idTransacao), any(TransacaoUpdateDTO.class));
    }

    @Test
    @DisplayName("Não dever editar, deve retornar erros de validacões para saídas possibilidade 01")
    public void nadaDeveEditarMasDeveRetornarErrosValidacoesParaSaidasPossibilidade01() throws Exception {

        TransacaoUpdateDTO update = new TransacaoUpdateDTO(
                null,
                LocalDate.of(2027, 7, 1),
                "",
                idCategoria
        );

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                put(ENDPOINT_TRANSACOES + "/" + idTransacao),
                update,
                objectMapper
        );

        Map<String, List<String>> erros = Map.of(
                "valor", List.of("Campo requerido"),
                "data", List.of("Não pode ser uma data futura"),
                "descricao", List.of("Campo requerido",
                        "O campo nome deve ter no mínimo 3 e no máximo 100 caracteres.")
        );

        ControllerTestAssertions.validarErroDadosInvalidos(
                result,
                ENDPOINT_TRANSACOES + "/" + idTransacao,
                erros
        );

        verify(transacaoService, never())
                .editarTransacao(any(UUID.class), any(TransacaoUpdateDTO.class));
    }

    @Test
    @DisplayName("Não deve editar, mas deve retornar erros de validacões para saídas possibilidade 02")
    public void naoDeveEditarMasDeveRetornarErrosValidacoesParaSaidasPossibilidade02() throws Exception {

        SaidaRequestDTO request = new SaidaRequestDTO(
                new BigDecimal(0.00),
                LocalDate.of(2026, 7, 1),
                "Estorno do cartão de crédito",
                idCategoria
        );

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                put(ENDPOINT_TRANSACOES + "/" + idTransacao),
                request,
                objectMapper
        );

        Map<String, List<String>> erros = Map.of("valor", List.of("O valor mínimo deve ser 0.01"));

        ControllerTestAssertions.validarErroDadosInvalidos(
                result,
                ENDPOINT_TRANSACOES + "/" + idTransacao,
                erros
        );

        verify(transacaoService, never())
                .editarTransacao(any(UUID.class), any(TransacaoUpdateDTO.class));
    }

    @Test
    @DisplayName("Deve retornar exceção devido exceção Saída sem categoria, tentativa de editar uma transação")
    public void naoDeveEditarTransacaoMasDeveRetornarBadRequest404SaidaSemCategoria() throws Exception {

        when(transacaoService.editarTransacao(any(UUID.class), any(TransacaoUpdateDTO.class)))
                .thenThrow(new RegraNegocioException("Saída sem categoria"));

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                put(ENDPOINT_TRANSACOES  + "/" + idTransacao),
                transacaoUpdateDTO,
                objectMapper
        );

        ControllerTestAssertions.validarErro(
                result,
                400,
                "Regra de negócio",
                "Saída sem categoria",
                URL_BASE_ERROS + "regra-de-negocio",
                ENDPOINT_TRANSACOES  + "/" + idTransacao
        );

        verify(transacaoService).editarTransacao(any(UUID.class), any(TransacaoUpdateDTO.class));
    }

    @Test
    @DisplayName("Deve retornar código 404, tentativa de deleçao")
    public void naoDeveDeletarPortantoRetornarCodigo404() throws Exception{

        doThrow(new RecursoNaoEncontradoException("Recurso não encontrado"))
                .when(transacaoService).deletar(any(UUID.class));

        ResultActions result = MockMvcHelper.realizarRequisicao(mockMvc,
                delete(ENDPOINT_TRANSACOES + "/" + idTransacao));

        ControllerTestAssertions.validarErro(
                result,
                404,
                "recurso não encontrado",
                "Recurso não encontrado",
                URL_BASE_ERROS + "recurso-nao-encontrado",
                ENDPOINT_TRANSACOES + "/" + idTransacao);

        verify(transacaoService).deletar(any(UUID.class));
    }

    @Test
    @DisplayName("Deve deletar uma categoria e retornar No Content")
    public void deveDeletarUmaCategoriaERetornarNoContent() throws Exception {

        doNothing().when(transacaoService).deletar(any(UUID.class));

        MockMvcHelper.realizarRequisicao(
                        mockMvc,
                        delete(ENDPOINT_TRANSACOES + "/" + idTransacao))
                .andExpect(status().isNoContent());

        verify(transacaoService).deletar(any(UUID.class));
    }
}


