package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.AutorDTO;
import br.tec.llam.biblioteca.service.AutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autores")
@Tag(name = "Autores", description = "Gerenciamento de autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar autor por ID")
    public ResponseEntity<AutorDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(autorService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo autor")
    public ResponseEntity<AutorDTO> criar(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody AutorDTO dto) {
        AutorDTO created = autorService.criar(dto, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
