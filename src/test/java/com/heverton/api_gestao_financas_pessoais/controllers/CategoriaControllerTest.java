package com.heverton.api_gestao_financas_pessoais.controllers;

import com.heverton.api_gestao_financas_pessoais.dtos.requests.CategoriaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.CategoriaResponseDTO;
import com.heverton.api_gestao_financas_pessoais.exceptions.CategoriaJaExistenteException;
import com.heverton.api_gestao_financas_pessoais.exceptions.DataBaseException;
import com.heverton.api_gestao_financas_pessoais.exceptions.RecursoNaoEncontradoException;
import com.heverton.api_gestao_financas_pessoais.services.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import utils.ControllerTestAssertions;
import utils.ControllerTestHelper;
import utils.MockMvcHelper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoriaRequestDTO requestDTO;
    private CategoriaResponseDTO responseDTO;
    private UUID id;

    private static final String URL_BASE_ERROS =
            "https://api.financas.com.br/erros/";

    private static final String ENDPOINT_CATEGORIAS =
            "/v1/categorias";

    @BeforeEach
    public void setUp()
    {
        id = java.util.UUID.randomUUID();
        requestDTO = new CategoriaRequestDTO("Combustível");
        responseDTO = new CategoriaResponseDTO(id, "Combustível");
    }

    @Test
    @DisplayName("Deve retornar categoria cadastrada 201 CREATED ")
    public void deveRetornarCadastroDeCategoriaCREATED() throws Exception {

        when(categoriaService.salvar(any(CategoriaRequestDTO.class)))
                .thenReturn(responseDTO);

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_CATEGORIAS),
                requestDTO,
                objectMapper
        );

        ControllerTestHelper.validarResposta(
                201,
                result,
                Map.of(
                        "idCategoria", id.toString(),
                        "nome", "Combustível"
                ));

        verify(categoriaService).salvar(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro Conflict 409")
    public void deveRetornarErroConflict() throws Exception {

        when(categoriaService.salvar(any(CategoriaRequestDTO.class)))
                .thenThrow(new CategoriaJaExistenteException("Categoria Combustível já existente"));

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_CATEGORIAS),
                requestDTO,
                objectMapper
        );

        ControllerTestAssertions.validarErro(
                result,
                409,
                "Conflict",
                "Categoria Combustível já existente",
                URL_BASE_ERROS +
                        "recurso-ja-existe",
                ENDPOINT_CATEGORIAS
        );

        verify(categoriaService).salvar(any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erros de validacões")
    public void deveRetornarErrosValidacoes() throws Exception {

        CategoriaRequestDTO request = new CategoriaRequestDTO("");

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                post(ENDPOINT_CATEGORIAS),
                request,
                objectMapper
        );

        Map<String, List<String>> erros = Map.of(
                "nome", List.of(
                        "Campo requerido",
                        "O campo nome deve ter no mínimo 2 e no máximo 50 caracteres."
                )
        );

        ControllerTestAssertions.validarErroDadosInvalidos(result, ENDPOINT_CATEGORIAS,erros);
    }

    @Test
    @DisplayName("Deve mostra uma lista com uma categoria")
    public void deveMostrarUmaListaComUmaCategoria() throws Exception {

        Page<CategoriaResponseDTO> page = new PageImpl<>(List.of(responseDTO));

        when(categoriaService.listarTodas(any(Pageable.class)))
                .thenReturn(page);

        ResultActions result = MockMvcHelper.realizarRequisicao(mockMvc, get(ENDPOINT_CATEGORIAS));

        ControllerTestHelper.validarRespostaPaginada(
                result,
                0,
                Map.of(
                        "idCategoria", id.toString(),
                        "nome", "Combustível"
                ));

        verify(categoriaService).listarTodas(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar exceção devido id inexistente, tentativa de editar uma categoria")
    public void naoEditaCategoriaDevidoIdInexistente() throws  Exception{

        when(categoriaService.editar(any(UUID.class), any(CategoriaRequestDTO.class)))
                .thenThrow(new RecursoNaoEncontradoException("Recurso não encontrado"));

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                put(ENDPOINT_CATEGORIAS + "/" + id),
                requestDTO,
                objectMapper
        );

        ControllerTestAssertions.validarErro(
                result,
                404,
                "recurso não encontrado",
                "Recurso não encontrado",
                URL_BASE_ERROS + "recurso-nao-encontrado",
                ENDPOINT_CATEGORIAS + "/" + id
        );

        verify(categoriaService).editar(any(UUID.class), any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar exceção devido dados inválidos, tentativa de editar uma categoria")
    public void naoDeveEditarCategoriaDevidoDadosInvalidos() throws  Exception{

        CategoriaRequestDTO update = new CategoriaRequestDTO("");

        ResultActions result = MockMvcHelper.realizarRequisicao(
                mockMvc,
                put(ENDPOINT_CATEGORIAS + "/" + id),
                update,
                objectMapper
        );

        Map<String, List<String>> erros = Map.of(
                "nome", List.of(
                        "Campo requerido",
                        "O campo nome deve ter no mínimo 2 e no máximo 50 caracteres."
                )
        );

        ControllerTestAssertions.validarErroDadosInvalidos(result, ENDPOINT_CATEGORIAS + "/" + id, erros);
    }

    @Test
    @DisplayName("Deve editar uma categoria existente e retornar OK")
    public void deveEditarUmaCategoriaExistenteERetornarOK() throws  Exception{

       CategoriaRequestDTO  update = new CategoriaRequestDTO("Lazer");

        when(categoriaService.editar(any(UUID.class),any(CategoriaRequestDTO.class)))
                .thenReturn(new CategoriaResponseDTO(id, "Lazer"));

         ResultActions result = MockMvcHelper.realizarRequisicao(
                 mockMvc,
                 put(ENDPOINT_CATEGORIAS + "/" + id),
                 update,
                 objectMapper
         );

        ControllerTestHelper.validarResposta(
                200,
                result,
                Map.of(
                        "idCategoria", id.toString(),
                        "nome", "Lazer"
                ));

        verify(categoriaService).editar(any(UUID.class), any(CategoriaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve deletar uma categoria e retornar No Content")
    public void deveDeletarUmaCategoriaERetornarNoContent() throws Exception {

        doNothing().when(categoriaService).deletar(any(UUID.class));

        MockMvcHelper.realizarRequisicao(
                mockMvc,
                delete(ENDPOINT_CATEGORIAS + "/" + id))
                .andExpect(status().isNoContent());

        verify(categoriaService).deletar(any(UUID.class));
    }

    @Test
    @DisplayName("Deve retornar código 404, tentativa de deleçao")
    public void naoDeveDeletarPortantoRetornarCodigo404() throws Exception{

        doThrow(new RecursoNaoEncontradoException("Recurso não encontrado"))
                .when(categoriaService).deletar(any(UUID.class));

        ResultActions result = MockMvcHelper.realizarRequisicao(mockMvc,
                delete(ENDPOINT_CATEGORIAS + "/" + id));

        ControllerTestAssertions.validarErro(
                result,
                404,
                "recurso não encontrado",
                "Recurso não encontrado",
                URL_BASE_ERROS + "recurso-nao-encontrado",
                ENDPOINT_CATEGORIAS + "/" + id);

        verify(categoriaService).deletar(any(UUID.class));
    }

    @Test
    @DisplayName("Deve retornar código 400, tentativa de deleção")
    public void naoDeveDeletarPortantoDeveRetornarCodigo400() throws Exception{

        doThrow(new DataBaseException("Falha de integridade referencial"))
                .when(categoriaService).deletar(any(UUID.class));

        ResultActions result = MockMvcHelper.realizarRequisicao(mockMvc,
                delete(ENDPOINT_CATEGORIAS + "/" + id));

        ControllerTestAssertions.validarErro(
                result,
                400,
                "Transação vinculada",
                "Falha de integridade referencial",
                URL_BASE_ERROS + "transacao-vinculada",
                ENDPOINT_CATEGORIAS + "/" + id);

        verify(categoriaService).deletar(any(UUID.class));
    }
}