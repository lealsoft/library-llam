package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.dto.LivroCreateDTO;
import com.capgemini.biblioapi.dto.LivroResponseDTO;
import com.capgemini.biblioapi.entity.Livro;
import com.capgemini.biblioapi.entity.StatusLivro;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitarios para LivroService.
 * Testa CRUD de livros e regras de negocio.
 */
@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private LivroService livroService;

    private Livro livroDisponivel;
    private Livro livroEmprestado;

    @BeforeEach
    void setUp() {
        livroDisponivel = Livro.builder()
                .id(1L)
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .isbn("978-0132350884")
                .status(StatusLivro.DISPONIVEL)
                .build();

        livroEmprestado = Livro.builder()
                .id(2L)
                .titulo("Design Patterns")
                .autor("Gang of Four")
                .isbn("978-0201633610")
                .status(StatusLivro.EMPRESTADO)
                .build();
    }

    @Nested
    @DisplayName("Testes de Listagem")
    class ListagemTests {

        @Test
        @DisplayName("Deve listar todos os livros ordenados por data recente")
        void deveListarTodosOsLivros() {
            when(livroRepository.findAllByOrderByDataCadastroDesc())
                    .thenReturn(Arrays.asList(livroDisponivel, livroEmprestado));

            List<LivroResponseDTO> resultado = livroService.listarTodos("recentes");

            assertEquals(2, resultado.size());
            assertEquals("Clean Code", resultado.get(0).getTitulo());
            assertEquals("Design Patterns", resultado.get(1).getTitulo());
            verify(livroRepository).findAllByOrderByDataCadastroDesc();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nao ha livros")
        void deveRetornarListaVaziaQuandoNaoHaLivros() {
            when(livroRepository.findAllByOrderByDataCadastroDesc()).thenReturn(Collections.emptyList());

            List<LivroResponseDTO> resultado = livroService.listarTodos(null);

            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve listar livros ordenados por titulo")
        void deveListarLivrosOrdenadosPorTitulo() {
            when(livroRepository.findAllByOrderByTituloAsc())
                    .thenReturn(Arrays.asList(livroDisponivel, livroEmprestado));

            List<LivroResponseDTO> resultado = livroService.listarTodos("titulo");

            assertEquals(2, resultado.size());
            verify(livroRepository).findAllByOrderByTituloAsc();
        }

        @Test
        @DisplayName("Deve listar livros ordenados por popularidade")
        void deveListarLivrosOrdenadosPorPopularidade() {
            when(livroRepository.findAllOrderByTotalEmprestimosDesc())
                    .thenReturn(Arrays.asList(livroEmprestado, livroDisponivel));

            List<LivroResponseDTO> resultado = livroService.listarTodos("populares");

            assertEquals(2, resultado.size());
            verify(livroRepository).findAllOrderByTotalEmprestimosDesc();
        }

        @Test
        @DisplayName("Deve listar apenas livros disponiveis")
        void deveListarApenasLivrosDisponiveis() {
            when(livroRepository.findByStatusOrderByDataCadastroDesc(StatusLivro.DISPONIVEL))
                    .thenReturn(Collections.singletonList(livroDisponivel));

            List<LivroResponseDTO> resultado = livroService.listarDisponiveis("recentes");

            assertEquals(1, resultado.size());
            assertEquals(StatusLivro.DISPONIVEL, resultado.get(0).getStatus());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nao ha livros disponiveis")
        void deveRetornarListaVaziaQuandoNaoHaLivrosDisponiveis() {
            when(livroRepository.findByStatusOrderByDataCadastroDesc(StatusLivro.DISPONIVEL))
                    .thenReturn(Collections.emptyList());

            List<LivroResponseDTO> resultado = livroService.listarDisponiveis(null);

            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve listar livros disponiveis ordenados por popularidade")
        void deveListarLivrosDisponiveisOrdenadosPorPopularidade() {
            when(livroRepository.findByStatusOrderByTotalEmprestimosDesc(StatusLivro.DISPONIVEL))
                    .thenReturn(Collections.singletonList(livroDisponivel));

            List<LivroResponseDTO> resultado = livroService.listarDisponiveis("populares");

            assertEquals(1, resultado.size());
            verify(livroRepository).findByStatusOrderByTotalEmprestimosDesc(StatusLivro.DISPONIVEL);
        }
    }

    @Nested
    @DisplayName("Testes de Busca por ID")
    class BuscaPorIdTests {

        @Test
        @DisplayName("Deve buscar livro por ID com sucesso")
        void deveBuscarLivroPorIdComSucesso() {
            when(livroRepository.findById(1L)).thenReturn(Optional.of(livroDisponivel));

            LivroResponseDTO resultado = livroService.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Clean Code", resultado.getTitulo());
            assertEquals("Robert C. Martin", resultado.getAutor());
            assertEquals("978-0132350884", resultado.getIsbn());
            assertEquals(StatusLivro.DISPONIVEL, resultado.getStatus());
        }

        @Test
        @DisplayName("Deve lancar excecao quando livro nao encontrado")
        void deveLancarExcecaoQuandoLivroNaoEncontrado() {
            when(livroRepository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> livroService.buscarPorId(99L));

            assertEquals("Livro nao encontrado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de Criacao")
    class CriacaoTests {

        @Test
        @DisplayName("Deve criar livro com sucesso")
        void deveCriarLivroComSucesso() {
            LivroCreateDTO dto = LivroCreateDTO.builder()
                    .titulo("Novo Livro")
                    .autor("Novo Autor")
                    .isbn("978-1234567890")
                    .build();

            Livro livroSalvo = Livro.builder()
                    .id(3L)
                    .titulo("Novo Livro")
                    .autor("Novo Autor")
                    .isbn("978-1234567890")
                    .status(StatusLivro.DISPONIVEL)
                    .build();

            when(livroRepository.existsByIsbn("978-1234567890")).thenReturn(false);
            when(livroRepository.save(any(Livro.class))).thenReturn(livroSalvo);

            LivroResponseDTO resultado = livroService.criar(dto);

            assertNotNull(resultado);
            assertEquals(3L, resultado.getId());
            assertEquals("Novo Livro", resultado.getTitulo());
            assertEquals("Novo Autor", resultado.getAutor());
            assertEquals("978-1234567890", resultado.getIsbn());
            assertEquals(StatusLivro.DISPONIVEL, resultado.getStatus());

            verify(livroRepository).existsByIsbn("978-1234567890");
            verify(livroRepository).save(any(Livro.class));
        }

        @Test
        @DisplayName("Deve lancar excecao quando ISBN ja existe")
        void deveLancarExcecaoQuandoIsbnJaExiste() {
            LivroCreateDTO dto = LivroCreateDTO.builder()
                    .titulo("Livro Duplicado")
                    .autor("Autor")
                    .isbn("978-0132350884")
                    .build();

            when(livroRepository.existsByIsbn("978-0132350884")).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> livroService.criar(dto));

            assertEquals("ISBN ja cadastrado", exception.getMessage());
            verify(livroRepository, never()).save(any(Livro.class));
        }
    }

    @Nested
    @DisplayName("Testes de Delecao")
    class DelecaoTests {

        @Test
        @DisplayName("Deve deletar livro disponivel com sucesso")
        void deveDeletarLivroDisponivelComSucesso() {
            when(livroRepository.findById(1L)).thenReturn(Optional.of(livroDisponivel));

            livroService.deletar(1L);

            verify(livroRepository).delete(livroDisponivel);
        }

        @Test
        @DisplayName("Deve lancar excecao ao tentar deletar livro emprestado")
        void deveLancarExcecaoAoTentarDeletarLivroEmprestado() {
            when(livroRepository.findById(2L)).thenReturn(Optional.of(livroEmprestado));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> livroService.deletar(2L));

            assertEquals("Nao e possivel deletar um livro emprestado", exception.getMessage());
            verify(livroRepository, never()).delete(any(Livro.class));
        }

        @Test
        @DisplayName("Deve lancar excecao ao tentar deletar livro inexistente")
        void deveLancarExcecaoAoTentarDeletarLivroInexistente() {
            when(livroRepository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> livroService.deletar(99L));

            assertEquals("Livro nao encontrado", exception.getMessage());
            verify(livroRepository, never()).delete(any(Livro.class));
        }
    }
}
