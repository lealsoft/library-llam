package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.TituloDTO;
import br.tec.llam.biblioteca.service.TituloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/titulos")
@Tag(name = "Títulos", description = "Gerenciamento de títulos bibliográficos")
public class TituloController {

    private final TituloService tituloService;

    public TituloController(TituloService tituloService) {
        this.tituloService = tituloService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os títulos")
    public ResponseEntity<List<TituloDTO>> listarTodos(
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(tituloService.listarTodos(tenantId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar título por ID")
    public ResponseEntity<TituloDTO> buscarPorId(@PathVariable("id") String id) {
        return ResponseEntity.ok(tituloService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo título")
    public ResponseEntity<TituloDTO> criar(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody TituloDTO dto) {
        TituloDTO created = tituloService.criar(dto, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar título existente")
    public ResponseEntity<TituloDTO> atualizar(
            @PathVariable("id") String id,
            @Valid @RequestBody TituloDTO dto) {
        return ResponseEntity.ok(tituloService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir título")
    public ResponseEntity<Void> excluir(@PathVariable("id") String id) {
        tituloService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
