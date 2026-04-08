package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.EmprestimoDTO;
import br.tec.llam.biblioteca.dto.EmprestimoMultiploDTO;
import br.tec.llam.biblioteca.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
@Tag(name = "Empréstimos", description = "Gerenciamento de empréstimos de livros")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empréstimo por ID")
    public ResponseEntity<EmprestimoDTO> buscarPorId(@PathVariable("id") String id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Realizar novo empréstimo")
    public ResponseEntity<EmprestimoDTO> realizarEmprestimo(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody EmprestimoDTO dto) {
        EmprestimoDTO created = emprestimoService.realizarEmprestimo(dto, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/devolucao")
    @Operation(summary = "Realizar devolução de empréstimo")
    public ResponseEntity<EmprestimoDTO> realizarDevolucao(@PathVariable("id") String id) {
        return ResponseEntity.ok(emprestimoService.realizarDevolucao(id));
    }

    @PostMapping("/{id}/renovacao")
    @Operation(summary = "Renovar empréstimo")
    public ResponseEntity<EmprestimoDTO> renovar(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable("id") String id) {
        return ResponseEntity.ok(emprestimoService.renovar(id, tenantId));
    }

    @PostMapping("/multiplo")
    @Operation(summary = "Realizar empréstimo de múltiplos exemplares")
    public ResponseEntity<EmprestimoMultiploDTO> realizarEmprestimoMultiplo(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody EmprestimoMultiploDTO dto) {
        EmprestimoMultiploDTO resultado = emprestimoService.realizarEmprestimoMultiplo(dto, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @PostMapping("/devolucao/multipla")
    @Operation(summary = "Realizar devolução de múltiplos empréstimos")
    public ResponseEntity<List<EmprestimoDTO>> realizarDevolucaoMultipla(
            @RequestBody List<String> emprestimoIds) {
        return ResponseEntity.ok(emprestimoService.realizarDevolucaoMultipla(emprestimoIds));
    }
}
