package com.heverton.api_gestao_financas_pessoais.services;

import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.EntradaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.SaidaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.TransacaoUpdateDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.TransacaoResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.Categoria;
import com.heverton.api_gestao_financas_pessoais.entities.Transacao;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import com.heverton.api_gestao_financas_pessoais.exceptions.RecursoNaoEncontradoException;
import com.heverton.api_gestao_financas_pessoais.repositories.CategoriaRepository;
import com.heverton.api_gestao_financas_pessoais.repositories.TransacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {


    @InjectMocks
    private  TransacaoService transacaoService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;


    @Test
    @DisplayName("Deve salvar uma transação de entrada")
    public void deveSalvarUmaEntrada() {

        EntradaRequestDTO request = new EntradaRequestDTO(
                new BigDecimal("4500.00"),
                LocalDate.now(),
                "Salário"
        );

        TransacaoResponseDTO response = transacaoService.salvarEntrada(request);

        assertEquals(new BigDecimal("4500.00"), response.valor());
        assertEquals(LocalDate.now(), response.data());
        assertEquals("Salário", response.descricao());
        assertNull(response.categoria());

        verify(transacaoRepository).save(Mockito.any(Transacao.class));
    }

    @Test
    @DisplayName("Deve salvar uma transação de saída")
    public void deveSalvarUmaSaida() {

        UUID idCategoria = UUID.randomUUID();

        SaidaRequestDTO request = new SaidaRequestDTO(
                new BigDecimal("4500.00"),
                LocalDate.now(),
                "Salário",
                idCategoria
        );

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idCategoria);
        categoria.setNome("Salário");

        when(categoriaRepository.findById(idCategoria))
                .thenReturn(Optional.of(categoria));

        TransacaoResponseDTO response = transacaoService.salvarSaida(request);

        assertEquals(new BigDecimal("4500.00"), response.valor());
        assertEquals(LocalDate.now(), response.data());
        assertEquals("Salário", response.descricao());

        assertNotNull(response.categoria());
        assertEquals(idCategoria, response.categoria().idCategoria());
        assertEquals("Salário", response.categoria().nome());

        verify(categoriaRepository).findById(idCategoria);
        verify(transacaoRepository).save(Mockito.any(Transacao.class));
    }

    @Test
    @DisplayName("Deve listar entradas quando não houver período informado")
    void deveListarTodasAsEntradasSemPeriodo() {

        Pageable pageable = PageRequest.of(0, 10);

        Transacao transacao = new Transacao();
        transacao.setIdTransacao(UUID.randomUUID());
        transacao.setValor(new BigDecimal("4500.00"));
        transacao.setTipo(TipoTransacao.ENTRADA);
        transacao.setDescricao("Salário");
        transacao.setData(LocalDate.now());

        Page<Transacao> page = new PageImpl<>(List.of(transacao));

        when(transacaoRepository.buscar(
                eq(TipoTransacao.ENTRADA),
                isNull(),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        )).thenReturn(page);

        Page<TransacaoResponseDTO> response =
                transacaoService.listarTodasEntradas(null, null, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(new BigDecimal("4500.00"), response.getContent().get(0).valor());

        verify(transacaoRepository).buscar(
                eq(TipoTransacao.ENTRADA),
                isNull(),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        );
    }




    @Test
    @DisplayName("Deve listar as entradas dentro do período informado")
    void deveListarEntradasPorPeriodo() {


        Pageable pageable = PageRequest.of(0, 10);

        Transacao transacao = new Transacao();
        transacao.setIdTransacao(UUID.randomUUID());
        transacao.setValor(new BigDecimal("4500.00"));
        transacao.setTipo(TipoTransacao.ENTRADA);
        transacao.setDescricao("Salário");
        transacao.setData(LocalDate.now());

        Page<Transacao> page = new PageImpl<>(List.of(transacao));

        LocalDate dataInicio = LocalDate.of(2026, 6, 1);
        LocalDate dataFim = LocalDate.of(2026, 6, 30);

        when(transacaoRepository.buscar(
                eq(TipoTransacao.ENTRADA),
                isNull(),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        )).thenReturn(page);

        Page<TransacaoResponseDTO> response =
                transacaoService.listarTodasEntradas(dataInicio, dataFim, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(transacao.getValor(), response.getContent().get(0).valor());

        verify(transacaoRepository).buscar(
                eq(TipoTransacao.ENTRADA),
                isNull(),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        );
    }

    @Test
    @DisplayName("Deve listar  saídas quando não houver período informado")
    void deveListarTodasAsSaidasSemPeriodo() {

        Pageable pageable = PageRequest.of(0, 10);

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(UUID.randomUUID());
        categoria.setNome("Moradia");

        Transacao transacao = new Transacao();
        transacao.setIdTransacao(UUID.randomUUID());
        transacao.setValor(new BigDecimal("600.00"));
        transacao.setTipo(TipoTransacao.SAIDA);
        transacao.setDescricao("Condominio");
        transacao.setData(LocalDate.now());
        transacao.setCategoria(categoria);

        transacao.setTipo(TipoTransacao.SAIDA);

        Page<Transacao> page = new PageImpl<>(List.of(transacao));

        when(transacaoRepository.buscar(
                eq(TipoTransacao.SAIDA),
                eq(categoria.getIdCategoria()),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        )).thenReturn(page);
        Page<TransacaoResponseDTO> response =
                transacaoService.listarTodasSaidas(categoria.getIdCategoria(), null, null, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(new BigDecimal("600.00"), response.getContent().get(0).valor());

        verify(transacaoRepository).buscar(
                eq(TipoTransacao.SAIDA),
                eq(categoria.getIdCategoria()),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        );
    }

    @Test
    @DisplayName("Deve listar as saídas dentro do período informado")
    void deveListarTodasAsSaidasPorPeriodo() {

        Pageable pageable = PageRequest.of(0, 10);

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(UUID.randomUUID());
        categoria.setNome("Moradia");

        Transacao transacao = new Transacao();
        transacao.setIdTransacao(UUID.randomUUID());
        transacao.setValor(new BigDecimal("600.00"));
        transacao.setTipo(TipoTransacao.SAIDA);
        transacao.setDescricao("Condominio");
        transacao.setData(LocalDate.now());
        transacao.setCategoria(categoria);

        LocalDate dataInicio = LocalDate.of(2026, 6, 1);
        LocalDate dataFim = LocalDate.of(2026, 6, 30);

        transacao.setTipo(TipoTransacao.SAIDA);

        Page<Transacao> page = new PageImpl<>(List.of(transacao));

        when(transacaoRepository.buscar(
                eq(TipoTransacao.SAIDA),
                eq(categoria.getIdCategoria()),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        )).thenReturn(page);

        Page<TransacaoResponseDTO> response =
                transacaoService.listarTodasSaidas(categoria.getIdCategoria(), dataInicio, dataFim, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(new BigDecimal("600.00"), response.getContent().get(0).valor());


        verify(transacaoRepository).buscar(
                eq(TipoTransacao.SAIDA),
                eq(categoria.getIdCategoria()),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        );
    }

    @Test
    @DisplayName("Deve listar todas as transaçoes por filtro de categoria")
    void deveListarPorFiltroDeCategoria() {

        Pageable pageable = PageRequest.of(0, 10);

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(UUID.randomUUID());
        categoria.setNome("Moradia");

        Transacao transacao = new Transacao();
        transacao.setIdTransacao(UUID.randomUUID());
        transacao.setValor(new BigDecimal("600.00"));
        transacao.setTipo(TipoTransacao.SAIDA);
        transacao.setDescricao("Condominio");
        transacao.setData(LocalDate.now());
        transacao.setCategoria(categoria);


        Page<Transacao> page = new PageImpl<>(List.of(transacao));

        when(transacaoRepository.buscar(
                isNull(),
                eq(categoria.getIdCategoria()),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        )).thenReturn(page);

        Page<TransacaoResponseDTO> response =
                transacaoService.listarTodasTransacoes(categoria.getIdCategoria(), null, null, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(new BigDecimal("600.00"), response.getContent().get(0).valor());
        assertEquals(categoria.getIdCategoria(),
                response.getContent().get(0).categoria().idCategoria());
        assertEquals(
                TipoTransacao.SAIDA,
                response.getContent().get(0).tipo()
        );

        verify(transacaoRepository).buscar(
                isNull(),
                eq(categoria.getIdCategoria()),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        );
    }

    @Test
    @DisplayName("Deve listar todas as transaçoes existentes sem uso de filtros")
    void deveListarTodasTransacoesExistentesSemUsodeFiltros() {

        Pageable pageable = PageRequest.of(0, 10);

        Transacao entrada = new Transacao();
        entrada.setIdTransacao(UUID.randomUUID());
        entrada.setValor(new BigDecimal("1000.00"));
        entrada.setTipo(TipoTransacao.ENTRADA);
        entrada.setDescricao("Salário");
        entrada.setData(LocalDate.now());

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(UUID.randomUUID());
        categoria.setNome("Mercado");

        Transacao saida = new Transacao();
        saida.setIdTransacao(UUID.randomUUID());
        saida.setValor(new BigDecimal("200.00"));
        saida.setTipo(TipoTransacao.SAIDA);
        saida.setDescricao("Compras da semana");
        saida.setData(LocalDate.now());
        saida.setCategoria(categoria);



        Page<Transacao> page = new PageImpl<>(List.of(entrada, saida));

        when(transacaoRepository.buscar(
                isNull(),
                isNull(),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        )).thenReturn(page);

        Page<TransacaoResponseDTO> response =
                transacaoService.listarTodasTransacoes(null, null, null, pageable);

        assertEquals(2, response.getTotalElements());
        assertEquals(TipoTransacao.ENTRADA, response.getContent().get(0).tipo());
        assertEquals(TipoTransacao.SAIDA, response.getContent().get(1).tipo());


        verify(transacaoRepository).buscar(
                isNull(),
                isNull(),
                any(LocalDate.class),
                any(LocalDate.class),
                eq(pageable)
        );
    }


    @Test
    @DisplayName("Deve retornar exceção devido id inexistente, tentativa de editar uma transação")
    public void deveRetornarExcecaoDevidoIdInexistente() {

        UUID id = UUID.randomUUID();
        TransacaoUpdateDTO patch = new TransacaoUpdateDTO(
                new BigDecimal("5500.00"),
                LocalDate.of(2026, 5, 1),
                "Salário",
                null
        );
        when(transacaoRepository.getReferenceById(id)).thenThrow(new EntityNotFoundException());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> transacaoService.editarTransacao(id, patch)
        );

        assertEquals("Recurso não encontrado", exception.getMessage());
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    @DisplayName("Deve editar uma transação existente")
    public void deveEditarUmaTransacaoExistente() {

        UUID id = UUID.randomUUID();

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(UUID.randomUUID());
        categoria.setNome("Salários");

        TransacaoUpdateDTO patch = new TransacaoUpdateDTO(
                new BigDecimal("5500.00"),
                LocalDate.of(2026, 5, 1),
                "Salário",
                null
        );

        Transacao transacao = new Transacao(
                id,
                new BigDecimal("4500.00"),
                TipoTransacao.ENTRADA,
                "Salário Antigo",
                LocalDate.of(2026, 6, 1),
                OffsetDateTime.now(),
                categoria
        );

        when(transacaoRepository.getReferenceById(id)).thenReturn(transacao);
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacao);

        TransacaoResponseDTO response = transacaoService.editarTransacao(id, patch);

        assertEquals(new BigDecimal("5500.00"), response.valor());
        assertEquals(LocalDate.of(2026, 5, 1), response.data());
        assertEquals("Salário", response.descricao());

        verify(transacaoRepository).getReferenceById(id);
        verify(transacaoRepository).save(any(Transacao.class));
    }




    @Test
    @DisplayName("Deve deletar uma transacao")
    public void deveDeletarUmaTransacao() {

        UUID id = UUID.fromString("8a22dd63-ef2c-4b08-92c2-5189c7af9f83");
        when(transacaoRepository.existsById(id)).thenReturn(true);
        transacaoService.deletar(id);
        verify(transacaoRepository).existsById(id);
        verify(transacaoRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando transação não existir, tentativa de deleção")
    public void deveLancarExcecaoQuandoIdNaoExistir() {

        UUID id = UUID.randomUUID();

        when(transacaoRepository.existsById(id)).thenReturn(false);

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> transacaoService.deletar(id)
        );

        assertEquals("Recurso não encontrado", exception.getMessage());

        verify(transacaoRepository).existsById(id);
        verify(transacaoRepository, never()).deleteById(id);
    }

}