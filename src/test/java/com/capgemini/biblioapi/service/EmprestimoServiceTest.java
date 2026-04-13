package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.dto.EmprestimoCreateDTO;
import com.capgemini.biblioapi.dto.EmprestimoResponseDTO;
import com.capgemini.biblioapi.entity.Emprestimo;
import com.capgemini.biblioapi.entity.Livro;
import com.capgemini.biblioapi.entity.StatusEmprestimo;
import com.capgemini.biblioapi.entity.StatusLivro;
import com.capgemini.biblioapi.entity.Usuario;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.repository.EmprestimoRepository;
import com.capgemini.biblioapi.repository.LivroRepository;
import com.capgemini.biblioapi.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitarios para EmprestimoService.
 * Testa fluxo de emprestimo e devolucao de livros.
 */
@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private Usuario usuario;
    private Livro livroDisponivel;
    private Livro livroEmprestado;
    private Emprestimo emprestimoEmAndamento;
    private Emprestimo emprestimoFinalizado;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Joao Silva")
                .login("joao.silva")
                .senhaHash("$2a$12$hash")
                .dataCriacao(LocalDateTime.now())
                .build();

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

        emprestimoEmAndamento = Emprestimo.builder()
                .id(1L)
                .usuario(usuario)
                .livro(livroEmprestado)
                .dataEmprestimo(LocalDateTime.now().minusDays(5))
                .status(StatusEmprestimo.EM_ANDAMENTO)
                .build();

        emprestimoFinalizado = Emprestimo.builder()
                .id(2L)
                .usuario(usuario)
                .livro(livroDisponivel)
                .dataEmprestimo(LocalDateTime.now().minusDays(10))
                .dataDevolucao(LocalDateTime.now().minusDays(3))
                .status(StatusEmprestimo.FINALIZADO)
                .build();
    }

    @Nested
    @DisplayName("Testes de Listagem")
    class ListagemTests {

        @Test
        @DisplayName("Deve listar todos os emprestimos")
        void deveListarTodosOsEmprestimos() {
            when(emprestimoRepository.findAll())
                    .thenReturn(Arrays.asList(emprestimoEmAndamento, emprestimoFinalizado));

            List<EmprestimoResponseDTO> resultado = emprestimoService.listarTodos();

            assertEquals(2, resultado.size());
            verify(emprestimoRepository).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nao ha emprestimos")
        void deveRetornarListaVaziaQuandoNaoHaEmprestimos() {
            when(emprestimoRepository.findAll()).thenReturn(Collections.emptyList());

            List<EmprestimoResponseDTO> resultado = emprestimoService.listarTodos();

            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("Testes de Busca por ID")
    class BuscaPorIdTests {

        @Test
        @DisplayName("Deve buscar emprestimo por ID com sucesso")
        void deveBuscarEmprestimoPorIdComSucesso() {
            when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimoEmAndamento));

            EmprestimoResponseDTO resultado = emprestimoService.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals(StatusEmprestimo.EM_ANDAMENTO, resultado.getStatus());
            assertNotNull(resultado.getLivro());
            assertEquals("Design Patterns", resultado.getLivro().getTitulo());
        }

        @Test
        @DisplayName("Deve lancar excecao quando emprestimo nao encontrado")
        void deveLancarExcecaoQuandoEmprestimoNaoEncontrado() {
            when(emprestimoRepository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> emprestimoService.buscarPorId(99L));

            assertEquals("Emprestimo nao encontrado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de Realizacao de Emprestimo")
    class RealizarEmprestimoTests {

        @Test
        @DisplayName("Deve realizar emprestimo com sucesso")
        void deveRealizarEmprestimoComSucesso() {
            EmprestimoCreateDTO dto = EmprestimoCreateDTO.builder()
                    .usuarioId(1L)
                    .livroId(1L)
                    .build();

            Emprestimo emprestimoSalvo = Emprestimo.builder()
                    .id(3L)
                    .usuario(usuario)
                    .livro(livroDisponivel)
                    .dataEmprestimo(LocalDateTime.now())
                    .status(StatusEmprestimo.EM_ANDAMENTO)
                    .build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(livroRepository.findById(1L)).thenReturn(Optional.of(livroDisponivel));
            when(livroRepository.save(any(Livro.class))).thenReturn(livroDisponivel);
            when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoSalvo);

            EmprestimoResponseDTO resultado = emprestimoService.realizarEmprestimo(dto);

            assertNotNull(resultado);
            assertEquals(3L, resultado.getId());
            assertEquals(StatusEmprestimo.EM_ANDAMENTO, resultado.getStatus());
            assertNotNull(resultado.getDataEmprestimo());
            assertNull(resultado.getDataDevolucao());

            verify(livroRepository).save(any(Livro.class));
            verify(emprestimoRepository).save(any(Emprestimo.class));
        }

        @Test
        @DisplayName("Deve lancar excecao quando usuario nao encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            EmprestimoCreateDTO dto = EmprestimoCreateDTO.builder()
                    .usuarioId(99L)
                    .livroId(1L)
                    .build();

            when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> emprestimoService.realizarEmprestimo(dto));

            assertEquals("Usuario nao encontrado", exception.getMessage());
            verify(emprestimoRepository, never()).save(any(Emprestimo.class));
        }

        @Test
        @DisplayName("Deve lancar excecao quando livro nao encontrado")
        void deveLancarExcecaoQuandoLivroNaoEncontrado() {
            EmprestimoCreateDTO dto = EmprestimoCreateDTO.builder()
                    .usuarioId(1L)
                    .livroId(99L)
                    .build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(livroRepository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> emprestimoService.realizarEmprestimo(dto));

            assertEquals("Livro nao encontrado", exception.getMessage());
            verify(emprestimoRepository, never()).save(any(Emprestimo.class));
        }

        @Test
        @DisplayName("Deve lancar excecao quando livro ja esta emprestado")
        void deveLancarExcecaoQuandoLivroJaEstaEmprestado() {
            EmprestimoCreateDTO dto = EmprestimoCreateDTO.builder()
                    .usuarioId(1L)
                    .livroId(2L)
                    .build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(livroRepository.findById(2L)).thenReturn(Optional.of(livroEmprestado));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> emprestimoService.realizarEmprestimo(dto));

            assertEquals("Livro nao esta disponivel para emprestimo", exception.getMessage());
            verify(livroRepository, never()).save(any(Livro.class));
            verify(emprestimoRepository, never()).save(any(Emprestimo.class));
        }
    }

    @Nested
    @DisplayName("Testes de Devolucao")
    class DevolucaoTests {

        @Test
        @DisplayName("Deve realizar devolucao com sucesso")
        void deveRealizarDevolucaoComSucesso() {
            Emprestimo emprestimoAtualizado = Emprestimo.builder()
                    .id(1L)
                    .usuario(usuario)
                    .livro(livroEmprestado)
                    .dataEmprestimo(emprestimoEmAndamento.getDataEmprestimo())
                    .dataDevolucao(LocalDateTime.now())
                    .status(StatusEmprestimo.FINALIZADO)
                    .build();

            when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimoEmAndamento));
            when(livroRepository.save(any(Livro.class))).thenReturn(livroEmprestado);
            when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoAtualizado);

            EmprestimoResponseDTO resultado = emprestimoService.realizarDevolucao(1L);

            assertNotNull(resultado);
            assertEquals(StatusEmprestimo.FINALIZADO, resultado.getStatus());
            assertNotNull(resultado.getDataDevolucao());

            verify(livroRepository).save(any(Livro.class));
            verify(emprestimoRepository).save(any(Emprestimo.class));
        }

        @Test
        @DisplayName("Deve lancar excecao quando emprestimo nao encontrado")
        void deveLancarExcecaoQuandoEmprestimoNaoEncontrado() {
            when(emprestimoRepository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> emprestimoService.realizarDevolucao(99L));

            assertEquals("Emprestimo nao encontrado", exception.getMessage());
            verify(livroRepository, never()).save(any(Livro.class));
        }

        @Test
        @DisplayName("Deve lancar excecao quando emprestimo ja foi finalizado")
        void deveLancarExcecaoQuandoEmprestimoJaFoiFinalizado() {
            when(emprestimoRepository.findById(2L)).thenReturn(Optional.of(emprestimoFinalizado));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> emprestimoService.realizarDevolucao(2L));

            assertEquals("Este emprestimo ja foi finalizado", exception.getMessage());
            verify(livroRepository, never()).save(any(Livro.class));
            verify(emprestimoRepository, never()).save(any(Emprestimo.class));
        }
    }

    @Nested
    @DisplayName("Testes de Conversao DTO")
    class ConversaoDTOTests {

        @Test
        @DisplayName("Deve converter emprestimo para DTO corretamente")
        void deveConverterEmprestimoParaDTOCorretamente() {
            when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimoEmAndamento));

            EmprestimoResponseDTO resultado = emprestimoService.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(emprestimoEmAndamento.getId(), resultado.getId());
            assertEquals(emprestimoEmAndamento.getDataEmprestimo(), resultado.getDataEmprestimo());
            assertEquals(emprestimoEmAndamento.getStatus(), resultado.getStatus());

            assertNotNull(resultado.getLivro());
            assertEquals(livroEmprestado.getId(), resultado.getLivro().getId());
            assertEquals(livroEmprestado.getTitulo(), resultado.getLivro().getTitulo());
            assertEquals(livroEmprestado.getAutor(), resultado.getLivro().getAutor());
            assertEquals(livroEmprestado.getIsbn(), resultado.getLivro().getIsbn());
        }
    }
}
