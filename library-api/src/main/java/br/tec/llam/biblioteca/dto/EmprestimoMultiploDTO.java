package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoMultiploDTO {
    
    @NotBlank(message = "Leitor é obrigatório")
    private String leitorId;
    
    @NotEmpty(message = "Lista de exemplares é obrigatória")
    private List<String> exemplarIds;
    
    private List<EmprestimoDTO> emprestimosRealizados;
    private List<String> erros;
}
