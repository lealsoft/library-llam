package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.RelatorioDTO;
import br.tec.llam.biblioteca.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatórios", description = "Relatórios e estatísticas")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/emprestimos")
    @Operation(summary = "Relatório de empréstimos")
    public ResponseEntity<RelatorioDTO> relatorioEmprestimos(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioEmprestimos(tenantId, dataInicio, dataFim));
    }

    @GetMapping("/multas")
    @Operation(summary = "Relatório de multas")
    public ResponseEntity<RelatorioDTO> relatorioMultas(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioMultas(tenantId, dataInicio, dataFim));
    }

    @GetMapping("/acervo")
    @Operation(summary = "Relatório de acervo")
    public ResponseEntity<RelatorioDTO> relatorioAcervo(
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioAcervo(tenantId));
    }

    @GetMapping("/leitores")
    @Operation(summary = "Relatório de leitores")
    public ResponseEntity<RelatorioDTO> relatorioLeitores(
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioLeitores(tenantId));
    }
}
