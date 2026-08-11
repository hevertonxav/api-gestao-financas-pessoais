package com.heverton.api_gestao_financas_pessoais.services;

import com.heverton.api_gestao_financas_pessoais.dtos.requests.CategoriaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.CategoriaResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.Categoria;
import com.heverton.api_gestao_financas_pessoais.exceptions.CategoriaJaExistenteException;
import com.heverton.api_gestao_financas_pessoais.exceptions.DataBaseException;
import com.heverton.api_gestao_financas_pessoais.exceptions.RecursoNaoEncontradoException;
import com.heverton.api_gestao_financas_pessoais.repositories.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class
CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public CategoriaResponseDTO salvar(final CategoriaRequestDTO categoriaRequestDTO){

        verificarCategoriaExistente(categoriaRequestDTO);
        var categoriaEntity = new Categoria();
        BeanUtils.copyProperties(categoriaRequestDTO, categoriaEntity);
        categoriaRepository.save(categoriaEntity);

        return new CategoriaResponseDTO(
                categoriaEntity.getIdCategoria(),
                categoriaEntity.getNome()
        );
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponseDTO> listarTodas(Pageable pageable){
        Page<Categoria> result = categoriaRepository.findAll(pageable);
        return result.map(CategoriaResponseDTO::new);
    }

    @Transactional
    public CategoriaResponseDTO editar (UUID id, CategoriaRequestDTO categoriaRequestDTO){

        try {

            Categoria entity = categoriaRepository.getReferenceById(id);
            verificarCategoriaExistente(categoriaRequestDTO);
            BeanUtils.copyProperties(categoriaRequestDTO, entity);
            entity = categoriaRepository.save(entity);

            return new CategoriaResponseDTO(
                    entity.getIdCategoria(),
                    entity.getNome()
            );
        } catch (EntityNotFoundException e) {
            throw new RecursoNaoEncontradoException("Recurso não encontrado");
        } catch (FatalBeanException e) {
            throw new RecursoNaoEncontradoException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deletar(UUID id){

        if (!categoriaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Recurso não encontrado");
        }

        try {
            categoriaRepository.deleteById(id);
        } catch(DataIntegrityViolationException e){
            throw new DataBaseException("Falha de integridade referencial");
        }

    }

    private void verificarCategoriaExistente(final CategoriaRequestDTO categoriaRequestDTO){

        final var nomeCategoria = categoriaRequestDTO.nome();

        if(categoriaRepository.existsByNome(nomeCategoria)){
            throw  new CategoriaJaExistenteException("Categoria " + nomeCategoria + " já existente");
        }
    }
}
