package com.capgemini.biblioapi.controller;

import com.capgemini.biblioapi.dto.EmprestimoCreateDTO;
import com.capgemini.biblioapi.dto.EmprestimoResponseDTO;
import com.capgemini.biblioapi.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de emprestimos.
 * Todos os endpoints requerem autenticacao JWT.
 */
@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    /** Lista todos os emprestimos (ativos e finalizados) */
    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(emprestimoService.listarTodos());
    }

    /** Busca emprestimo por ID */
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    /** Realiza emprestimo de livro para usuario */
    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> realizarEmprestimo(@Valid @RequestBody EmprestimoCreateDTO dto) {
        EmprestimoResponseDTO response = emprestimoService.realizarEmprestimo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Registra devolucao de livro */
    @PutMapping("/{id}/devolver")
    public ResponseEntity<EmprestimoResponseDTO> realizarDevolucao(@PathVariable Long id) {
        EmprestimoResponseDTO response = emprestimoService.realizarDevolucao(id);
        return ResponseEntity.ok(response);
    }
}
