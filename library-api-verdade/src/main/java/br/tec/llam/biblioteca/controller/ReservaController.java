package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.ReservaDTO;
import br.tec.llam.biblioteca.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gerenciamento de reservas de livros")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ResponseEntity<ReservaDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova reserva")
    public ResponseEntity<ReservaDTO> criar(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody ReservaDTO dto) {
        ReservaDTO created = reservaService.criar(dto, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva")
    public ResponseEntity<ReservaDTO> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }
}
