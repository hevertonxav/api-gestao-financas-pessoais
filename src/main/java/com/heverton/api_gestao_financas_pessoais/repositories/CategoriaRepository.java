package com.heverton.api_gestao_financas_pessoais.repositories;

import com.heverton.api_gestao_financas_pessoais.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    boolean existsByNome(String nomeCategoria);
}
