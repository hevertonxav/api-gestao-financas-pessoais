package com.heverton.api_gestao_financas_pessoais.services;

import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.EntradaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.SaidaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.requests.transacao.TransacaoUpdateDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.CategoriaResponseDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.TransacaoResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.Categoria;
import com.heverton.api_gestao_financas_pessoais.entities.Periodo;
import com.heverton.api_gestao_financas_pessoais.entities.Transacao;
import com.heverton.api_gestao_financas_pessoais.entities.enums.TipoTransacao;
import com.heverton.api_gestao_financas_pessoais.exceptions.RecursoNaoEncontradoException;
import com.heverton.api_gestao_financas_pessoais.exceptions.RegraNegocioException;
import com.heverton.api_gestao_financas_pessoais.repositories.CategoriaRepository;
import com.heverton.api_gestao_financas_pessoais.repositories.TransacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public TransacaoResponseDTO salvarEntrada(final EntradaRequestDTO entradaRequestDTO){
        return salvarTransacao(entradaRequestDTO, TipoTransacao.ENTRADA);
    }

    @Transactional
    public TransacaoResponseDTO salvarSaida(final SaidaRequestDTO saidaRequestDTO) {
        return salvarTransacao(saidaRequestDTO, TipoTransacao.SAIDA);
    }

    @Transactional
    public TransacaoResponseDTO salvarTransacao(final Object requestDTO, TipoTransacao tipo){

        var transacaoEntity = new Transacao();
        BeanUtils.copyProperties(requestDTO, transacaoEntity);
        transacaoEntity.setTipo(tipo);

        if (requestDTO instanceof SaidaRequestDTO saidaRequestDTO) {

            Categoria categoria = categoriaRepository.findById(saidaRequestDTO.idCategoria())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Recurso não encontrado."
                            ));

            transacaoEntity.setCategoria(categoria);
        }

        transacaoRepository.save(transacaoEntity);

        return new TransacaoResponseDTO(transacaoEntity);

    }

    public Page<TransacaoResponseDTO> listarTodasEntradas(
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) {


        return listarTransacoes(
                TipoTransacao.ENTRADA,
                null,
                dataInicio,
                dataFim,
                pageable);
    }

    public Page<TransacaoResponseDTO> listarTodasSaidas(
            UUID idCategoria,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) {
        return listarTransacoes(
                TipoTransacao.SAIDA,
                idCategoria,
                dataInicio,
                dataFim,
                pageable);
    }

    public Page<TransacaoResponseDTO> listarTodasTransacoes(
            UUID idCategoria,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    ) {

        return listarTransacoes(
                null,
                idCategoria,
                dataInicio,
                dataFim,
                pageable
        );
    }

    @Transactional
    public TransacaoResponseDTO editarTransacao(UUID id, TransacaoUpdateDTO transacaoUpdateDTO){

        try {

            Transacao entity = transacaoRepository.getReferenceById(id);

            BeanUtils.copyProperties(transacaoUpdateDTO, entity);

            if (transacaoUpdateDTO.idCategoria() != null) {

                Categoria categoria = categoriaRepository
                        .getReferenceById(transacaoUpdateDTO.idCategoria());

                entity.setCategoria(categoria);

            } else {

                entity.setCategoria(null);
            }

            if (entity.getTipo() == TipoTransacao.SAIDA && entity.getCategoria() == null) {
                throw new RegraNegocioException("Saídas devem possuir categoria");
            }

            if (entity.getTipo() == TipoTransacao.ENTRADA) {
                entity.setCategoria(null);
            }

            entity = transacaoRepository.save(entity);

            CategoriaResponseDTO categoriaDTO = null;

            if (entity.getCategoria() != null) {
                categoriaDTO = new CategoriaResponseDTO(
                        entity.getCategoria().getIdCategoria(),
                        entity.getCategoria().getNome()
                );
            }

            return new TransacaoResponseDTO(
                    entity.getIdTransacao(),
                    entity.getTipo(),
                    entity.getValor(),
                    entity.getData(),
                    entity.getDescricao(),
                    categoriaDTO,
                    entity.getDataCriacao()
            );
        } catch (EntityNotFoundException e) {
            throw new RecursoNaoEncontradoException("Recurso não encontrado");
        }  catch (FatalBeanException e) {
            throw new RecursoNaoEncontradoException("Recurso não encontrado");
        }
    }

    @Transactional(readOnly = true)
    public Page<TransacaoResponseDTO> listarTransacoes(
            TipoTransacao tipo,
            UUID idCategoria,
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable) {

        Periodo periodo = obterPeriodo(dataInicio, dataFim);

        return transacaoRepository.buscar(
                        tipo,
                        idCategoria,
                        periodo.dataInicio(),
                        periodo.dataFim(),
                        pageable)
                .map(TransacaoResponseDTO::new);
    }

    @Transactional
    public void deletar(UUID id){

        if (!transacaoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Recurso não encontrado");
        }

        transacaoRepository.deleteById(id);
    }

    private Periodo obterPeriodo(LocalDate dataInicio, LocalDate dataFim){

        if(dataInicio == null){
            dataInicio = LocalDate.of(2020,1,1);
        }

        if(dataFim == null){
            dataFim = LocalDate.now();
        }

        return new Periodo(dataInicio, dataFim);
    }
}


