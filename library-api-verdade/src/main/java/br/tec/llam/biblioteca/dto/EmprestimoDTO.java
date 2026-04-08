package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoDTO {
    private String id;
    
    @NotBlank(message = "Leitor é obrigatório")
    private String leitorId;
    
    @NotBlank(message = "Exemplar é obrigatório")
    private String exemplarId;
    
    private String tituloId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataRealDevolucao;
    private String statusEmprestimo;
    private Integer renovacoes;
    private Double valorMulta;
    private Boolean multaPaga;
    private String tenantId;
}
