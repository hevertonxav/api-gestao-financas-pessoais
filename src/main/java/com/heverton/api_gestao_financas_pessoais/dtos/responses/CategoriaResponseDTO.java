package com.heverton.api_gestao_financas_pessoais.dtos.responses;

import com.heverton.api_gestao_financas_pessoais.entities.Categoria;
import java.util.UUID;

public record CategoriaResponseDTO(
        UUID idCategoria,
        String  nome
){
    public CategoriaResponseDTO(Categoria entity) {
        this(
                entity.getIdCategoria(),
                entity.getNome()
        );
    }
}
