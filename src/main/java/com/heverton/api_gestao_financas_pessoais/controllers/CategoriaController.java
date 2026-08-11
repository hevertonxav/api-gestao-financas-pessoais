package com.heverton.api_gestao_financas_pessoais.controllers;

import com.heverton.api_gestao_financas_pessoais.dtos.requests.CategoriaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.CategoriaResponseDTO;
import com.heverton.api_gestao_financas_pessoais.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/categorias")
public class CategoriaController {


    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> inserirNovaCategoria(
            @Valid @RequestBody CategoriaRequestDTO categoriaRequestDTO
    ){
        CategoriaResponseDTO categoriaResponseDTO =  categoriaService.salvar(categoriaRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(categoriaResponseDTO.idCategoria()).toUri();
        return ResponseEntity.created(uri).body(categoriaResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaResponseDTO>> listarTodasCategorias(Pageable pageable){
        Page<CategoriaResponseDTO> categoriaResponseDTOS = categoriaService.listarTodas(pageable);
        return ResponseEntity.ok(categoriaResponseDTOS);
    }

    @PutMapping(value = "/{idCategoria}")
    public ResponseEntity<CategoriaResponseDTO> editarCategoria(
            @PathVariable UUID idCategoria,
            @Valid @RequestBody CategoriaRequestDTO categoriaRequestDTO
    ) {
        CategoriaResponseDTO categoriaResponseDTO = categoriaService.editar(idCategoria, categoriaRequestDTO);
        return ResponseEntity.ok(categoriaResponseDTO);
    }

    @DeleteMapping(value = "/{idCategoria}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable UUID idCategoria){
        categoriaService.deletar(idCategoria);
        return ResponseEntity.noContent().build();
    }
}
