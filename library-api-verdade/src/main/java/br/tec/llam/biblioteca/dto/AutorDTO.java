package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutorDTO {
    private String id;
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String sobrenome;
    private String nacionalidade;
    private String biografia;
    private String tenantId;
}
