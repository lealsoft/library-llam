package com.capgemini.biblioapi.controller;

import com.capgemini.biblioapi.config.JwtAuthenticationFilter;
import com.capgemini.biblioapi.dto.EmprestimoCreateDTO;
import com.capgemini.biblioapi.dto.EmprestimoResponseDTO;
import com.capgemini.biblioapi.dto.LivroResponseDTO;
import com.capgemini.biblioapi.entity.StatusEmprestimo;
import com.capgemini.biblioapi.entity.StatusLivro;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.service.EmprestimoService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integracao para EmprestimoController.
 * Testa endpoints REST de emprestimos.
 */
@WebMvcTest(value = EmprestimoController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmprestimoService emprestimoService;

    private LivroResponseDTO livroDTO;
    private EmprestimoResponseDTO emprestimoEmAndamento;
    private EmprestimoResponseDTO emprestimoFinalizado;

    @BeforeEach
    void setUp() {
        livroDTO = LivroResponseDTO.builder()
                .id(1L)
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .isbn("978-0132350884")
                .status(StatusLivro.EMPRESTADO)
                .build();

        emprestimoEmAndamento = EmprestimoResponseDTO.builder()
                .id(1L)
                .livro(livroDTO)
                .dataEmprestimo(LocalDateTime.now().minusDays(5))
                .dataDevolucao(null)
                .status(StatusEmprestimo.EM_ANDAMENTO)
                .build();

        emprestimoFinalizado = EmprestimoResponseDTO.builder()
                .id(2L)
                .livro(livroDTO)
                .dataEmprestimo(LocalDateTime.now().minusDays(10))
                .dataDevolucao(LocalDateTime.now().minusDays(3))
                .status(StatusEmprestimo.FINALIZADO)
                .build();
    }

    @Nested
    @DisplayName("GET /api/emprestimos")
    class ListarTodosTests {

        @Test
        @DisplayName("Deve listar todos os emprestimos com sucesso")
        void deveListarTodosOsEmprestimosComSucesso() throws Exception {
            when(emprestimoService.listarTodos())
                    .thenReturn(Arrays.asList(emprestimoEmAndamento, emprestimoFinalizado));

            mockMvc.perform(get("/api/emprestimos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].status").value("EM_ANDAMENTO"))
                    .andExpect(jsonPath("$[1].status").value("FINALIZADO"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nao ha emprestimos")
        void deveRetornarListaVaziaQuandoNaoHaEmprestimos() throws Exception {
            when(emprestimoService.listarTodos()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/emprestimos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/emprestimos/{id}")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Deve buscar emprestimo por ID com sucesso")
        void deveBuscarEmprestimoPorIdComSucesso() throws Exception {
            when(emprestimoService.buscarPorId(1L)).thenReturn(emprestimoEmAndamento);

            mockMvc.perform(get("/api/emprestimos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                    .andExpect(jsonPath("$.livro.titulo").value("Clean Code"))
                    .andExpect(jsonPath("$.dataDevolucao").isEmpty());
        }

        @Test
        @DisplayName("Deve retornar 404 quando emprestimo nao encontrado")
        void deveRetornar404QuandoEmprestimoNaoEncontrado() throws Exception {
            when(emprestimoService.buscarPorId(99L))
                    .thenThrow(new BusinessException("Emprestimo nao encontrado", HttpStatus.NOT_FOUND));

            mockMvc.perform(get("/api/emprestimos/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/emprestimos")
    class RealizarEmprestimoTests {

        @Test
        @DisplayName("Deve realizar emprestimo com sucesso")
        void deveRealizarEmprestimoComSucesso() throws Exception {
            EmprestimoCreateDTO createDTO = EmprestimoCreateDTO.builder()
                    .usuarioId(1L)
                    .livroId(1L)
                    .build();

            when(emprestimoService.realizarEmprestimo(any(EmprestimoCreateDTO.class)))
                    .thenReturn(emprestimoEmAndamento);

            mockMvc.perform(post("/api/emprestimos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                    .andExpect(jsonPath("$.livro").exists());
        }

        @Test
        @DisplayName("Deve retornar 400 quando usuarioId nulo")
        void deveRetornar400QuandoUsuarioIdNulo() throws Exception {
            EmprestimoCreateDTO createDTO = EmprestimoCreateDTO.builder()
                    .usuarioId(null)
                    .livroId(1L)
                    .build();

            mockMvc.perform(post("/api/emprestimos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando livroId nulo")
        void deveRetornar400QuandoLivroIdNulo() throws Exception {
            EmprestimoCreateDTO createDTO = EmprestimoCreateDTO.builder()
                    .usuarioId(1L)
                    .livroId(null)
                    .build();

            mockMvc.perform(post("/api/emprestimos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 404 quando usuario nao encontrado")
        void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
            EmprestimoCreateDTO createDTO = EmprestimoCreateDTO.builder()
                    .usuarioId(99L)
                    .livroId(1L)
                    .build();

            when(emprestimoService.realizarEmprestimo(any(EmprestimoCreateDTO.class)))
                    .thenThrow(new BusinessException("Usuario nao encontrado", HttpStatus.NOT_FOUND));

            mockMvc.perform(post("/api/emprestimos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 404 quando livro nao encontrado")
        void deveRetornar404QuandoLivroNaoEncontrado() throws Exception {
            EmprestimoCreateDTO createDTO = EmprestimoCreateDTO.builder()
                    .usuarioId(1L)
                    .livroId(99L)
                    .build();

            when(emprestimoService.realizarEmprestimo(any(EmprestimoCreateDTO.class)))
                    .thenThrow(new BusinessException("Livro nao encontrado", HttpStatus.NOT_FOUND));

            mockMvc.perform(post("/api/emprestimos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 400 quando livro ja esta emprestado")
        void deveRetornar400QuandoLivroJaEstaEmprestado() throws Exception {
            EmprestimoCreateDTO createDTO = EmprestimoCreateDTO.builder()
                    .usuarioId(1L)
                    .livroId(1L)
                    .build();

            when(emprestimoService.realizarEmprestimo(any(EmprestimoCreateDTO.class)))
                    .thenThrow(new BusinessException("Livro nao esta disponivel para emprestimo", HttpStatus.BAD_REQUEST));

            mockMvc.perform(post("/api/emprestimos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/emprestimos/{id}/devolver")
    class DevolucaoTests {

        @Test
        @DisplayName("Deve realizar devolucao com sucesso")
        void deveRealizarDevolucaoComSucesso() throws Exception {
            when(emprestimoService.realizarDevolucao(1L)).thenReturn(emprestimoFinalizado);

            mockMvc.perform(put("/api/emprestimos/1/devolver"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("FINALIZADO"))
                    .andExpect(jsonPath("$.dataDevolucao").exists());
        }

        @Test
        @DisplayName("Deve retornar 404 quando emprestimo nao encontrado")
        void deveRetornar404QuandoEmprestimoNaoEncontrado() throws Exception {
            when(emprestimoService.realizarDevolucao(99L))
                    .thenThrow(new BusinessException("Emprestimo nao encontrado", HttpStatus.NOT_FOUND));

            mockMvc.perform(put("/api/emprestimos/99/devolver"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 400 quando emprestimo ja foi finalizado")
        void deveRetornar400QuandoEmprestimoJaFoiFinalizado() throws Exception {
            when(emprestimoService.realizarDevolucao(2L))
                    .thenThrow(new BusinessException("Este emprestimo ja foi finalizado", HttpStatus.BAD_REQUEST));

            mockMvc.perform(put("/api/emprestimos/2/devolver"))
                    .andExpect(status().isBadRequest());
        }
    }
}
