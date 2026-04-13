package com.capgemini.biblioapi.controller;

import com.capgemini.biblioapi.dto.UsuarioCreateDTO;
import com.capgemini.biblioapi.dto.UsuarioResponseDTO;
import com.capgemini.biblioapi.dto.UsuarioUpdateDTO;
import com.capgemini.biblioapi.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de usuarios.
 * Todos os endpoints requerem autenticacao JWT (exceto os de auth).
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /** Lista todos os usuarios cadastrados */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    /** Busca usuario por ID */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    /** Cria novo usuario (senha deve vir criptografada) */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioCreateDTO dto) {
        UsuarioResponseDTO response = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Atualiza dados do usuario */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO dto) {
        UsuarioResponseDTO response = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    /** Remove usuario do sistema */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
