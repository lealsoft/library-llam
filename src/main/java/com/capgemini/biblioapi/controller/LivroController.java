package com.capgemini.biblioapi.controller;

import com.capgemini.biblioapi.dto.LivroCreateDTO;
import com.capgemini.biblioapi.dto.LivroResponseDTO;
import com.capgemini.biblioapi.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de livros.
 * 
 * Endpoints publicos (GET): consulta de livros
 * Endpoints protegidos (POST/DELETE): requerem JWT
 */
@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    /**
     * Lista todos os livros do acervo (publico).
     * 
     * @param ordenacao Criterio de ordenacao: recentes (default), antigos, titulo, populares
     */
    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> listarTodos(
            @RequestParam(required = false, defaultValue = "recentes") String ordenacao) {
        return ResponseEntity.ok(livroService.listarTodos(ordenacao));
    }

    /**
     * Lista apenas livros disponiveis para emprestimo (publico).
     * 
     * @param ordenacao Criterio de ordenacao: recentes (default), populares
     */
    @GetMapping("/disponiveis")
    public ResponseEntity<List<LivroResponseDTO>> listarDisponiveis(
            @RequestParam(required = false, defaultValue = "recentes") String ordenacao) {
        return ResponseEntity.ok(livroService.listarDisponiveis(ordenacao));
    }

    /** Busca livro por ID (publico) */
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    /** Cadastra novo livro (requer JWT) */
    @PostMapping
    public ResponseEntity<LivroResponseDTO> criar(@Valid @RequestBody LivroCreateDTO dto) {
        LivroResponseDTO response = livroService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Remove livro do acervo (requer JWT, livro deve estar disponivel) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
