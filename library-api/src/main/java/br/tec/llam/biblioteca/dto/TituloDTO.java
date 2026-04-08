package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TituloDTO {
    private String id;
    @NotBlank(message = "Título é obrigatório")
    private String titulo;
    private String subtitulo;
    private String isbn;
    private String autor;
    private String editora;
    private Integer anoPublicacao;
    private String categoria;
    private String sinopse;
    private String formato;
    private String statusTitulo;
    private String tenantId;
}
