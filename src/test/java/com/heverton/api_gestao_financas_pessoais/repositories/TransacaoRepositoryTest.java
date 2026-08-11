package com.heverton.api_gestao_financas_pessoais.repositories;

import com.heverton.api_gestao_financas_pessoais.entities.Categoria;
import com.heverton.api_gestao_financas_pessoais.entities.Transacao;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;


import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Teste para o repositório de transações.")
class TransacaoRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransacaoRepository repository;

    private Categoria alimentacao;
    private Categoria lazer;

    private void salvar(
            BigDecimal valor,
            TipoTransacao tipo,
            LocalDate data,
            String descricao,
            Categoria categoria
    ) {

        Transacao transacao = new Transacao();
        transacao.setValor(valor);
        transacao.setTipo(tipo);
        transacao.setData(data);
        transacao.setDescricao(descricao);
        transacao.setCategoria(categoria);

        repository.save(transacao);
    }

    @BeforeEach
    void setUp() {

        alimentacao = new Categoria();
        alimentacao.setNome("Alimentação");

        lazer = new Categoria();
        lazer.setNome("Lazer");

        entityManager.persist(alimentacao);
        entityManager.persist(lazer);


        salvar(new BigDecimal("3000.00"), TipoTransacao.ENTRADA, LocalDate.of(2025, 1, 5),  "Salário Janeiro", alimentacao);
        salvar(new BigDecimal("500.00"),  TipoTransacao.ENTRADA, LocalDate.of(2025, 2, 10), "Freelance", lazer);
        salvar(new BigDecimal("150.00"),  TipoTransacao.SAIDA,   LocalDate.of(2025, 2, 15), "Mercado", alimentacao);
        salvar(new BigDecimal("80.00"),   TipoTransacao.SAIDA,   LocalDate.of(2025, 3, 1),  "Cinema", lazer);
        salvar(new BigDecimal("3200.00"), TipoTransacao.ENTRADA, LocalDate.of(2025, 4, 5),  "Salário Abril", alimentacao);
        salvar(new BigDecimal("200.00"),  TipoTransacao.SAIDA,   LocalDate.of(2025, 4, 20), "Restaurante", alimentacao);
        salvar(new BigDecimal("100.00"),  TipoTransacao.SAIDA,   LocalDate.of(2025, 5, 12), "Parque", lazer);
        salvar(new BigDecimal("400.00"),  TipoTransacao.ENTRADA, LocalDate.of(2025, 6, 10), "Venda", lazer);
        salvar(new BigDecimal("120.00"),  TipoTransacao.SAIDA,   LocalDate.of(2025, 6, 15), "Mercado", alimentacao);
        salvar(new BigDecimal("250.00"),  TipoTransacao.ENTRADA, LocalDate.of(2025, 6, 25), "Bônus", alimentacao);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void deveSomarTodasAsEntradas() {

        BigDecimal total = repository.somarPorTipoEPeriodo(
                TipoTransacao.ENTRADA,
                LocalDate.of(2025,1,1),
                LocalDate.of(2026,12,31)
        );
        assertEquals(new BigDecimal("7350.00"), total);
    }

    @Test
    public void deveSomarTodasAsSaidas(){

        BigDecimal total = repository.somarPorTipoEPeriodo(
                TipoTransacao.SAIDA,
                java.time.LocalDate.of(2025,1,1),
                LocalDate.of(2025,12,31)
        );
        assertEquals(new BigDecimal("650.00"), total);
    }

    @Test
    public void deveSomarTodasAsSaidasEIgnorarTransacaoForaFiltroDatas(){

        BigDecimal total = repository.somarPorTipoEPeriodo(
                TipoTransacao.SAIDA,
                LocalDate.of(2025,6,1),
                LocalDate.of(2025,6,15)
        );
        assertEquals(new BigDecimal("120.00"), total);
    }

    @Test
    public void deveBuscarEntradasSemFiltrarCategoria() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transacao> resultado = repository.buscar(
                TipoTransacao.ENTRADA,
                null,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                pageable
        );

        assertEquals(5, resultado.getTotalElements());
        imprimirTransacoes(resultado);
    }

    @Test
    public void deveBuscarSaidasPorCategoriaAlimentacao() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transacao> resultado = repository.buscar(
                TipoTransacao.SAIDA,
                alimentacao.getIdCategoria(),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                pageable
        );

        assertEquals(3, resultado.getTotalElements());

        assertEquals(
                alimentacao.getIdCategoria(),
                resultado.getContent().getFirst().getCategoria().getIdCategoria()
        );

        imprimirTransacoes(resultado);
    }

    @Test
    public void deveBuscarTodasTransacoesQuandoTipoForNull() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transacao> resultado = repository.buscar(
                null, // ignora filtro de tipo
                null, // ignora filtro de categoria
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                pageable
        );

        assertEquals(10, resultado.getTotalElements());
        imprimirTransacoes(resultado);
    }

    @Test
   public void deveBuscarTransacoesPorCategoriaIgnorandoTipo() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Transacao> resultado = repository.buscar(
                null, // ignora tipo
                alimentacao.getIdCategoria(),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                pageable
        );

        assertEquals(6, resultado.getTotalElements());

        assertTrue(
                resultado.getContent()
                        .stream()
                        .allMatch(t ->
                                t.getCategoria().getIdCategoria()
                                .equals(alimentacao.getIdCategoria()))
        );
        imprimirTransacoes(resultado);

    }

    private void imprimirTransacoes(Page<Transacao> resultado) {

        resultado.getContent().forEach(t ->
                System.out.println(
                        "Tipo: " + t.getTipo() +
                                ", Valor: " + t.getValor() +
                                ", Data: " + t.getData() +
                                ", Categoria: " + t.getCategoria().getNome()
                )
        );
    }
}