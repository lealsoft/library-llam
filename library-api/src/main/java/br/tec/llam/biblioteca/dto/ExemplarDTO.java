package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExemplarDTO {
    private Integer id;
    
    @NotBlank(message = "Título é obrigatório")
    private String tituloId;
    
    private String codigoBarras;
    private String localizacao;
    private String statusExemplar;
    private String observacoes;
    private String tenantId;
}
