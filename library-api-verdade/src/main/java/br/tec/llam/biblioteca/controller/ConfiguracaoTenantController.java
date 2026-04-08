package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.ConfiguracaoTenantDTO;
import br.tec.llam.biblioteca.service.ConfiguracaoTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracoes")
@Tag(name = "Configurações", description = "Configurações do tenant")
public class ConfiguracaoTenantController {

    private final ConfiguracaoTenantService configuracaoService;

    public ConfiguracaoTenantController(ConfiguracaoTenantService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    @GetMapping
    @Operation(summary = "Buscar configurações do tenant")
    public ResponseEntity<ConfiguracaoTenantDTO> buscar(
            @RequestHeader("X-Tenant-Id") String tenantId) {
        return ResponseEntity.ok(configuracaoService.buscarConfiguracoes(tenantId));
    }

    @PutMapping
    @Operation(summary = "Atualizar configurações do tenant")
    public ResponseEntity<ConfiguracaoTenantDTO> atualizar(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestBody ConfiguracaoTenantDTO dto) {
        return ResponseEntity.ok(configuracaoService.atualizarConfiguracoes(tenantId, dto));
    }
}
