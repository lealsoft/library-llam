package com.capgemini.biblioapi.controller;

import com.capgemini.biblioapi.config.JwtAuthenticationFilter;
import com.capgemini.biblioapi.dto.LivroCreateDTO;
import com.capgemini.biblioapi.dto.LivroResponseDTO;
import com.capgemini.biblioapi.entity.StatusLivro;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integracao para LivroController.
 * Testa endpoints REST de livros.
 */
@WebMvcTest(value = LivroController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LivroService livroService;

    private LivroResponseDTO livroResponse1;
    private LivroResponseDTO livroResponse2;

    @BeforeEach
    void setUp() {
        livroResponse1 = LivroResponseDTO.builder()
                .id(1L)
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .isbn("978-0132350884")
                .status(StatusLivro.DISPONIVEL)
                .build();

        livroResponse2 = LivroResponseDTO.builder()
                .id(2L)
                .titulo("Design Patterns")
                .autor("Gang of Four")
                .isbn("978-0201633610")
                .status(StatusLivro.EMPRESTADO)
                .build();
    }

    @Nested
    @DisplayName("GET /api/livros")
    class ListarTodosTests {

        @Test
        @DisplayName("Deve listar todos os livros com sucesso")
        void deveListarTodosOsLivrosComSucesso() throws Exception {
            when(livroService.listarTodos("recentes")).thenReturn(Arrays.asList(livroResponse1, livroResponse2));

            mockMvc.perform(get("/api/livros"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].titulo").value("Clean Code"))
                    .andExpect(jsonPath("$[1].titulo").value("Design Patterns"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nao ha livros")
        void deveRetornarListaVaziaQuandoNaoHaLivros() throws Exception {
            when(livroService.listarTodos("recentes")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/livros"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Deve listar livros ordenados por popularidade")
        void deveListarLivrosOrdenadosPorPopularidade() throws Exception {
            when(livroService.listarTodos("populares")).thenReturn(Arrays.asList(livroResponse2, livroResponse1));

            mockMvc.perform(get("/api/livros").param("ordenacao", "populares"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/livros/disponiveis")
    class ListarDisponiveisTests {

        @Test
        @DisplayName("Deve listar apenas livros disponiveis")
        void deveListarApenasLivrosDisponiveis() throws Exception {
            when(livroService.listarDisponiveis("recentes")).thenReturn(Collections.singletonList(livroResponse1));

            mockMvc.perform(get("/api/livros/disponiveis"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].status").value("DISPONIVEL"));
        }

        @Test
        @DisplayName("Deve listar livros disponiveis ordenados por popularidade")
        void deveListarLivrosDisponiveisOrdenadosPorPopularidade() throws Exception {
            when(livroService.listarDisponiveis("populares")).thenReturn(Collections.singletonList(livroResponse1));

            mockMvc.perform(get("/api/livros/disponiveis").param("ordenacao", "populares"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/livros/{id}")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve buscar livro por ID com sucesso")
        void deveBuscarLivroPorIdComSucesso() throws Exception {
            when(livroService.buscarPorId(1L)).thenReturn(livroResponse1);

            mockMvc.perform(get("/api/livros/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.titulo").value("Clean Code"))
                    .andExpect(jsonPath("$.autor").value("Robert C. Martin"))
                    .andExpect(jsonPath("$.isbn").value("978-0132350884"))
                    .andExpect(jsonPath("$.status").value("DISPONIVEL"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando livro nao encontrado")
        void deveRetornar404QuandoLivroNaoEncontrado() throws Exception {
            when(livroService.buscarPorId(99L))
                    .thenThrow(new BusinessException("Livro nao encontrado", HttpStatus.NOT_FOUND));

            mockMvc.perform(get("/api/livros/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/livros")
    class CriarTests {

        @Test
        @DisplayName("Deve criar livro com sucesso")
        void deveCriarLivroComSucesso() throws Exception {
            LivroCreateDTO createDTO = LivroCreateDTO.builder()
                    .titulo("Novo Livro")
                    .autor("Novo Autor")
                    .isbn("978-1234567890")
                    .build();

            LivroResponseDTO responseDTO = LivroResponseDTO.builder()
                    .id(3L)
                    .titulo("Novo Livro")
                    .autor("Novo Autor")
                    .isbn("978-1234567890")
                    .status(StatusLivro.DISPONIVEL)
                    .build();

            when(livroService.criar(any(LivroCreateDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/livros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(3))
                    .andExpect(jsonPath("$.titulo").value("Novo Livro"))
                    .andExpect(jsonPath("$.status").value("DISPONIVEL"));
        }

        @Test
        @DisplayName("Deve retornar 400 quando titulo vazio")
        void deveRetornar400QuandoTituloVazio() throws Exception {
            LivroCreateDTO createDTO = LivroCreateDTO.builder()
                    .titulo("")
                    .autor("Autor")
                    .isbn("978-1234567890")
                    .build();

            mockMvc.perform(post("/api/livros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando autor vazio")
        void deveRetornar400QuandoAutorVazio() throws Exception {
            LivroCreateDTO createDTO = LivroCreateDTO.builder()
                    .titulo("Titulo")
                    .autor("")
                    .isbn("978-1234567890")
                    .build();

            mockMvc.perform(post("/api/livros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando ISBN vazio")
        void deveRetornar400QuandoIsbnVazio() throws Exception {
            LivroCreateDTO createDTO = LivroCreateDTO.builder()
                    .titulo("Titulo")
                    .autor("Autor")
                    .isbn("")
                    .build();

            mockMvc.perform(post("/api/livros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 409 quando ISBN ja existe")
        void deveRetornar409QuandoIsbnJaExiste() throws Exception {
            LivroCreateDTO createDTO = LivroCreateDTO.builder()
                    .titulo("Livro Duplicado")
                    .autor("Autor")
                    .isbn("978-0132350884")
                    .build();

            when(livroService.criar(any(LivroCreateDTO.class)))
                    .thenThrow(new BusinessException("ISBN ja cadastrado", HttpStatus.CONFLICT));

            mockMvc.perform(post("/api/livros")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/livros/{id}")
    class DeletarTests {

        @Test
        @DisplayName("Deve deletar livro com sucesso")
        void deveDeletarLivroComSucesso() throws Exception {
            doNothing().when(livroService).deletar(1L);

            mockMvc.perform(delete("/api/livros/1"))
                    .andExpect(status().isNoContent());

            verify(livroService).deletar(1L);
        }

        @Test
        @DisplayName("Deve retornar 404 quando livro nao encontrado")
        void deveRetornar404QuandoLivroNaoEncontrado() throws Exception {
            doThrow(new BusinessException("Livro nao encontrado", HttpStatus.NOT_FOUND))
                    .when(livroService).deletar(99L);

            mockMvc.perform(delete("/api/livros/99"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 400 quando livro esta emprestado")
        void deveRetornar400QuandoLivroEstaEmprestado() throws Exception {
            doThrow(new BusinessException("Nao e possivel deletar um livro emprestado", HttpStatus.BAD_REQUEST))
                    .when(livroService).deletar(2L);

            mockMvc.perform(delete("/api/livros/2"))
                    .andExpect(status().isBadRequest());
        }
    }
}
