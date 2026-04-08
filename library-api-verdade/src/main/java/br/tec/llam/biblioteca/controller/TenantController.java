package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.TenantDTO;
import br.tec.llam.biblioteca.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
@Tag(name = "Tenants", description = "Gerenciamento de bibliotecas contratantes")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tenant por ID")
    public ResponseEntity<TenantDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(tenantService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo tenant")
    public ResponseEntity<TenantDTO> criar(@Valid @RequestBody TenantDTO dto) {
        TenantDTO created = tenantService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tenant existente")
    public ResponseEntity<TenantDTO> atualizar(
            @PathVariable String id,
            @Valid @RequestBody TenantDTO dto) {
        return ResponseEntity.ok(tenantService.atualizar(id, dto));
    }
}
