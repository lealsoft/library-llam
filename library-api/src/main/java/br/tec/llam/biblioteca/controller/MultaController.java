package br.tec.llam.biblioteca.controller;

import br.tec.llam.biblioteca.dto.MultaDTO;
import br.tec.llam.biblioteca.service.MultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/multas")
@Tag(name = "Multas", description = "Gerenciamento de multas")
public class MultaController {

    private final MultaService multaService;

    public MultaController(MultaService multaService) {
        this.multaService = multaService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar multa por ID")
    public ResponseEntity<MultaDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(multaService.buscarPorId(id));
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Pagar multa")
    public ResponseEntity<MultaDTO> pagar(@PathVariable String id) {
        return ResponseEntity.ok(multaService.pagar(id));
    }

    @PostMapping("/{id}/isentar")
    @Operation(summary = "Isentar multa")
    public ResponseEntity<MultaDTO> isentar(
            @PathVariable String id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(multaService.isentar(id, motivo));
    }
}
