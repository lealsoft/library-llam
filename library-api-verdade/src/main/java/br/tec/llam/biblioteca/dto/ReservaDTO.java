package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaDTO {
    private String id;
    
    @NotBlank(message = "Leitor é obrigatório")
    private String leitorId;
    
    @NotBlank(message = "Título é obrigatório")
    private String tituloId;
    
    private LocalDate dataReserva;
    private LocalDate dataExpiracao;
    private String statusReserva;
    private Integer posicaoFila;
    private String tenantId;
}
