package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.HistoricoLeitorDTO;
import br.tec.llam.biblioteca.service.HistoricoLeitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historico")
@Tag(name = "Histórico", description = "Histórico de leitores")
public class HistoricoLeitorController {

    private final HistoricoLeitorService historicoService;

    public HistoricoLeitorController(HistoricoLeitorService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping("/leitor/{leitorId}")
    @Operation(summary = "Buscar histórico do leitor")
    public ResponseEntity<HistoricoLeitorDTO> buscarHistorico(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable("leitorId") String leitorId) {
        return ResponseEntity.ok(historicoService.buscarHistorico(leitorId, tenantId));
    }
}
