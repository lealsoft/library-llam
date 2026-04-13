package com.capgemini.biblioapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoCreateDTO {

    @NotNull(message = "ID do usuario e obrigatorio")
    private Long usuarioId;

    @NotNull(message = "ID do livro e obrigatorio")
    private Long livroId;
}
