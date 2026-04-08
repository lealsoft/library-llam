package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.ExemplarDTO;
import br.tec.llam.biblioteca.service.ExemplarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exemplares")
@Tag(name = "Exemplares", description = "Gerenciamento de exemplares físicos")
public class ExemplarController {

    private final ExemplarService exemplarService;

    public ExemplarController(ExemplarService exemplarService) {
        this.exemplarService = exemplarService;
    }

    @GetMapping("/{classId}/{classSchemeId}")
    @Operation(summary = "Buscar exemplar por ID")
    public ResponseEntity<ExemplarDTO> buscarPorId(
            @PathVariable Integer classId,
            @PathVariable Integer classSchemeId) {
        return ResponseEntity.ok(exemplarService.buscarPorId(classId, classSchemeId));
    }

    @PostMapping
    @Operation(summary = "Criar novo exemplar")
    public ResponseEntity<ExemplarDTO> criar(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody ExemplarDTO dto) {
        ExemplarDTO created = exemplarService.criar(dto, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
