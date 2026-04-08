package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.LeitorDTO;
import br.tec.llam.biblioteca.dto.AuthRequestDTO;
import br.tec.llam.biblioteca.dto.AuthResponseDTO;
import br.tec.llam.biblioteca.service.LeitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leitores")
@Tag(name = "Leitores", description = "Gerenciamento de leitores da biblioteca")
public class LeitorController {

    private final LeitorService leitorService;

    public LeitorController(LeitorService leitorService) {
        this.leitorService = leitorService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar leitor por ID")
    public ResponseEntity<LeitorDTO> buscarPorId(@PathVariable("id") String id) {
        return ResponseEntity.ok(leitorService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo leitor")
    public ResponseEntity<LeitorDTO> criar(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody LeitorDTO dto) {
        LeitorDTO created = leitorService.criar(dto, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar leitor existente")
    public ResponseEntity<LeitorDTO> atualizar(
            @PathVariable("id") String id,
            @Valid @RequestBody LeitorDTO dto) {
        return ResponseEntity.ok(leitorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir leitor (exclusão lógica)")
    public ResponseEntity<LeitorDTO> excluir(@PathVariable("id") String id) {
        return ResponseEntity.ok(leitorService.excluir(id));
    }

    @PostMapping("/{id}/suspender")
    @Operation(summary = "Suspender leitor")
    public ResponseEntity<LeitorDTO> suspender(
            @PathVariable("id") String id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(leitorService.suspender(id, motivo));
    }

    @PostMapping("/{id}/reativar")
    @Operation(summary = "Reativar leitor suspenso ou excluído")
    public ResponseEntity<LeitorDTO> reativar(@PathVariable("id") String id) {
        return ResponseEntity.ok(leitorService.reativar(id));
    }

    @GetMapping("/excluidos")
    @Operation(summary = "Listar leitores excluídos (lixeira)")
    public ResponseEntity<List<LeitorDTO>> listarExcluidos(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        return ResponseEntity.ok(leitorService.listarExcluidos(tenantId));
    }

    @GetMapping
    @Operation(summary = "Listar leitores ativos")
    public ResponseEntity<List<LeitorDTO>> listarAtivos(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId) {
        return ResponseEntity.ok(leitorService.listarAtivos(tenantId));
    }

    @PostMapping("/autenticar")
    @Operation(summary = "Autenticar leitor (validação segura local)")
    public ResponseEntity<AuthResponseDTO> autenticar(@Valid @RequestBody AuthRequestDTO auth) {
        return ResponseEntity.ok(leitorService.autenticar(auth));
    }

    @PostMapping("/{id}/registrar-acesso")
    @Operation(summary = "Registrar credenciais seguras do leitor no H2 embarcado")
    public ResponseEntity<AuthResponseDTO> registrarAcesso(
            @PathVariable("id") String id,
            @Valid @RequestBody AuthRequestDTO auth) {
        return ResponseEntity.ok(leitorService.registrarAcessoLocal(id, auth));
    }
}
