package com.heverton.api_gestao_financas_pessoais.repositories;

import com.heverton.api_gestao_financas_pessoais.entities.Transacao;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    Page<Transacao> findByDataBetween(
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    );

    Page<Transacao> findByTipoAndCategoria_IdCategoriaAndDataBetween(
            TipoTransacao tipo,
            UUID idCategoria,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    );

    Page<Transacao> findByTipoAndDataBetween(
            TipoTransacao tipo,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    );

    Page<Transacao> findByTipo(TipoTransacao tipo, Pageable pageable);

    @Query("""
           SELECT COALESCE(SUM(t.valor), 0)
           FROM Transacao t
           WHERE t.tipo = :tipo
           AND t.data BETWEEN :dataInicio AND :dataFim
           """)
    BigDecimal somarPorTipoEPeriodo(
            @Param("tipo") TipoTransacao tipo,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("""
    SELECT t
    FROM Transacao t
    WHERE
     (:tipo IS NULL OR t.tipo = :tipo)
      AND (:idCategoria IS NULL OR t.categoria.idCategoria = :idCategoria)
      AND t.data BETWEEN :dataInicio AND :dataFim
""")
    Page<Transacao> buscar(
            @Param("tipo") TipoTransacao tipo,
            @Param("idCategoria") UUID idCategoria,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable
    );

}
