package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditoraDTO {
    private String id;
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String cidade;
    private String pais;
    private String website;
    private String tenantId;
}
