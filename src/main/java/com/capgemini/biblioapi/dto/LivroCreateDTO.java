package com.capgemini.biblioapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivroCreateDTO {

    @NotBlank(message = "Titulo e obrigatorio")
    private String titulo;

    @NotBlank(message = "Autor e obrigatorio")
    private String autor;

    @NotBlank(message = "ISBN e obrigatorio")
    private String isbn;
}
