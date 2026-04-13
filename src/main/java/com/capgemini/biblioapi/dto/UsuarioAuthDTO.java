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
public class UsuarioAuthDTO {

    @NotBlank(message = "Login criptografado e obrigatorio")
    private String loginCriptografado;

    @NotBlank(message = "Senha criptografada e obrigatoria")
    private String senhaCriptografada;
}
