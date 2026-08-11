package com.heverton.api_gestao_financas_pessoais.dtos.responses;


import com.heverton.api_gestao_financas_pessoais.entities.Transacao;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransacaoResponseDTO(

        UUID idTransacao,
        TipoTransacao tipo,
        BigDecimal valor,
        LocalDate data,
        String descricao,
        CategoriaResponseDTO categoria,
        OffsetDateTime dataCriacao

) {

    public TransacaoResponseDTO(Transacao entity) {
        this(
                entity.getIdTransacao(),
                entity.getTipo(),
                entity.getValor(),
                entity.getData(),
                entity.getDescricao(),
                entity.getCategoria() != null
                        ? new CategoriaResponseDTO(entity.getCategoria())
                        : null,
                entity.getDataCriacao()

        );
    }
}
