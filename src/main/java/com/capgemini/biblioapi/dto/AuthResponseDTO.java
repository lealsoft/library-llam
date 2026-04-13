package com.capgemini.biblioapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta de autenticacao.
 * Contem access token (JWT) e refresh token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    /** Access token JWT (curta duracao) */
    private String accessToken;
    
    /** Refresh token (longa duracao) */
    private String refreshToken;
    
    /** Tipo do token (Bearer) */
    private String tipo;
    
    /** Tempo de expiracao do access token em segundos */
    private Long expiraEm;
    
    /** Dados do usuario autenticado */
    private UsuarioResponseDTO usuario;
}
