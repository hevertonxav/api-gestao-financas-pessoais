package com.heverton.api_gestao_financas_pessoais.services;

import com.heverton.api_gestao_financas_pessoais.dtos.requests.CategoriaRequestDTO;
import com.heverton.api_gestao_financas_pessoais.dtos.responses.CategoriaResponseDTO;
import com.heverton.api_gestao_financas_pessoais.entities.Categoria;
import com.heverton.api_gestao_financas_pessoais.exceptions.CategoriaJaExistenteException;
import com.heverton.api_gestao_financas_pessoais.exceptions.DataBaseException;
import com.heverton.api_gestao_financas_pessoais.exceptions.RecursoNaoEncontradoException;
import com.heverton.api_gestao_financas_pessoais.repositories.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @InjectMocks
    private  CategoriaService categoriaService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    public void setUp() {
        Categoria categoria1 = new Categoria( UUID.fromString("8a22dd63-ef2c-4b08-92c2-5189c7af9f83"),
                "Alimentação");
        Categoria categoria2 = new Categoria( UUID.fromString("c75ba14c-2fe4-4087-9b90-f0f9f6d2b1a9"),
                "Saúde");
        lenient().when(categoriaRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(categoria1, categoria2)));
    }



    @Test
    @DisplayName("Deve lançar uma exceção quando tiver uma categoria repetida, tentativa de salvar uma nova categoria")
    public void deveLancarExcecaoQuandoTiverCategoriaRepetida() {

        CategoriaRequestDTO request = new CategoriaRequestDTO("Alimentação");
        when(categoriaRepository.existsByNome("Alimentação"))
                .thenReturn(true);
        CategoriaJaExistenteException exception = assertThrows(
                CategoriaJaExistenteException.class,
                () -> categoriaService.salvar(request)
        );
        assertEquals(
                "Categoria Alimentação já existente",
                exception.getMessage()
        );
        verify(categoriaRepository, Mockito.never()).save(Mockito.any(Categoria.class));
    }

    @Test
    @DisplayName("Deve salvar uma categoria")
    public void deveSalvarUmaCategoria() {

        CategoriaRequestDTO request = new CategoriaRequestDTO("Educação");

        when(categoriaRepository.existsByNome("Educação"))
                .thenReturn(false);

        CategoriaResponseDTO response = categoriaService.salvar(request);
        assertEquals("Educação", response.nome());
        verify(categoriaRepository).save(Mockito.any(Categoria.class));
    }

    @Test
    @DisplayName("O teste deve retornar uma lista com  duas categorias")
    public void deveRetornarDuasCategoria() {

        Page<CategoriaResponseDTO> result = categoriaService.listarTodas(Pageable.unpaged());
        Assertions.assertEquals(2, result.getContent().size());
    }

    @Test
    @DisplayName("Deve retornar exceção devido id inexistente, tentativa de editar uma categoria")
    public void deveRetornarExcecaoDevidoIdInexistente() {

        UUID id = UUID.fromString("c75ba14c-2fe4-4087-9b90-f0f9f6d2b1a0");
        CategoriaRequestDTO request = new CategoriaRequestDTO("Pet");
        when(categoriaRepository.getReferenceById(id)).thenThrow(new EntityNotFoundException());

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> categoriaService.editar(id, request)
        );

       assertEquals("Recurso não encontrado", exception.getMessage());
       verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve editar uma categoria existente")
    public void deveEditarUmaCategoriaExistente() {

        UUID id = UUID.fromString("8a22dd63-ef2c-4b08-92c2-5189c7af9f83");
        CategoriaRequestDTO request = new CategoriaRequestDTO("Pet");
        Categoria categoria = new Categoria(id, "Alimentação");
        when(categoriaRepository.getReferenceById(id)).thenReturn(categoria);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        CategoriaResponseDTO response = categoriaService.editar(id, request);
        assertEquals(id, response.idCategoria());
        assertEquals("Pet", response.nome());

        verify(categoriaRepository).save(any(Categoria.class));
    }


    @Test
    @DisplayName("Deve deletar uma categoria")
    public void deveDeletarUmaCategoria() {

        UUID id = UUID.fromString("8a22dd63-ef2c-4b08-92c2-5189c7af9f83");
        when(categoriaRepository.existsById(id)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(id);
        categoriaService.deletar(id);
        verify(categoriaRepository).existsById(id);
        verify(categoriaRepository).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não existir, tentativa de deleção")
    public void deveLancarExcecaoQuandoIdNaoExistir() {

        UUID id = UUID.randomUUID();

        when(categoriaRepository.existsById(id)).thenReturn(false);

        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> categoriaService.deletar(id)
        );

        assertEquals("Recurso não encontrado", exception.getMessage());

        verify(categoriaRepository).existsById(id);
        verify(categoriaRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção de integridade referencial, tentativa de deleção")
    public void deveLancarExcecaoDeIntegridadeReferencial() {

        UUID id = UUID.randomUUID();

        when(categoriaRepository.existsById(id))
                .thenReturn(true);

        doThrow(new DataIntegrityViolationException("erro"))
                .when(categoriaRepository).deleteById(id);

        DataBaseException exception = assertThrows(
                DataBaseException.class,
                () -> categoriaService.deletar(id)
        );

        assertEquals("Falha de integridade referencial", exception.getMessage());

        verify(categoriaRepository).deleteById(id);
    }
}