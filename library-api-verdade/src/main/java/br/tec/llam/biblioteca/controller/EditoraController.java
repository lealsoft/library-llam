package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.EditoraDTO;
import br.tec.llam.biblioteca.service.EditoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/editoras")
@Tag(name = "Editoras", description = "Gerenciamento de editoras")
public class EditoraController {

    private final EditoraService editoraService;

    public EditoraController(EditoraService editoraService) {
        this.editoraService = editoraService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar editora por ID")
    public ResponseEntity<EditoraDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(editoraService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova editora")
    public ResponseEntity<EditoraDTO> criar(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody EditoraDTO dto) {
        EditoraDTO created = editoraService.criar(dto, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
