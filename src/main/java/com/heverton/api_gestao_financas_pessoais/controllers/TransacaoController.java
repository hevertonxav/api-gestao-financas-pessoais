package com.heverton.api_gestao_financas_pessoais.controllers;

import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.EntradaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.SaidaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.TransacaoUpdateDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.TransacaoResponseDTO;
import com.heverton.api_gestao_financas_pessoais.services.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping(value = "/entradas")
    public ResponseEntity<TransacaoResponseDTO> inserirNovaEntrada(
            @Valid @RequestBody EntradaRequestDTO entradaRequestDTO
            )
    {
        TransacaoResponseDTO transacaoResponseDTO =  transacaoService.salvarEntrada(entradaRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(transacaoResponseDTO.idTransacao()).toUri();
        return ResponseEntity.created(uri).body(transacaoResponseDTO);
    }

    @GetMapping(value = "/entradas")
    public ResponseEntity<Page<TransacaoResponseDTO>> listarTodasEntradas(

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false)
            LocalDate dataInicio,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false)
            LocalDate dataFim,

            Pageable pageable){
        Page<TransacaoResponseDTO> transacaoResponseDTOS = transacaoService
                .listarTodasEntradas(dataInicio, dataFim, pageable);
        return ResponseEntity.ok(transacaoResponseDTOS);
    }

    @PostMapping(value = "/saidas")
    public ResponseEntity<TransacaoResponseDTO> inserirNovaSaida(
            @Valid @RequestBody SaidaRequestDTO saidaRequestDTO
    )
    {
        TransacaoResponseDTO transacaoResponseDTO =  transacaoService.salvarSaida(saidaRequestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(transacaoResponseDTO.idTransacao()).toUri();
        return ResponseEntity.created(uri).body(transacaoResponseDTO);
    }

    @GetMapping(value = "/saidas")
    public ResponseEntity<Page<TransacaoResponseDTO>> listarTodasSaidas(
            @RequestParam (required = false) UUID idCategoria,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            Pageable pageable){
        Page<TransacaoResponseDTO> transacaoResponseDTOS = transacaoService
                .listarTodasSaidas(idCategoria, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(transacaoResponseDTOS);
    }

    @GetMapping
    public ResponseEntity<Page<TransacaoResponseDTO>> listarTodasTransacoes(
            @RequestParam(required = false) UUID idCategoria,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            Pageable pageable
    ){
        Page<TransacaoResponseDTO> transacaoResponseDTOS = transacaoService
                .listarTodasTransacoes(idCategoria, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(transacaoResponseDTOS);
    }

    @PutMapping(value = "/{idTransacao}")
    public ResponseEntity<TransacaoResponseDTO> editarTransacao(
            @PathVariable UUID idTransacao,
            @Valid @RequestBody TransacaoUpdateDTO transacaoUpdateDTO
    ) {
        TransacaoResponseDTO transacaoResponseDTO = transacaoService.editarTransacao(idTransacao, transacaoUpdateDTO);
        return ResponseEntity.ok(transacaoResponseDTO);
    }

    @DeleteMapping(value = "/{idTransacao}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable UUID idTransacao){
        transacaoService.deletar(idTransacao);
        return ResponseEntity.noContent().build();
    }





}
