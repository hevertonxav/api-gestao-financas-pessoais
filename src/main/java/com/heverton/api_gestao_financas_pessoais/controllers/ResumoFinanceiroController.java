package com.heverton.api_gestao_financas_pessoais.controllers;

import com.heverton.api_gestao_financas_pessoais.dtos.responses.ResumoFinanceiroResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.Periodo;
import com.heverton.api_gestao_financas_pessoais.services.ResumoFinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/resumo")
public class ResumoFinanceiroController {

    @Autowired
    private ResumoFinanceiroService resumoFinanceiroService;


    @GetMapping
    public ResponseEntity<ResumoFinanceiroResponseDTO> obterResumoFinanceiro(
            @ModelAttribute Periodo periodo){
        ResumoFinanceiroResponseDTO  resumoFinanceiroResponseDTO = resumoFinanceiroService
                .obterResumoFinanceiro(periodo);
        return ResponseEntity.ok(resumoFinanceiroResponseDTO);
    }
}
