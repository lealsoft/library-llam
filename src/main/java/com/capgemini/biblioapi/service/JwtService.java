package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Servico responsavel pela geracao e validacao de tokens JWT.
 * Utiliza o algoritmo HMAC-SHA256 para assinatura dos tokens.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Gera um token JWT para o usuario autenticado.
     * O token contem: login (subject), id, nome e data de expiracao.
     * 
     * @param usuario Usuario autenticado
     * @return Token JWT assinado
     */
    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getLogin())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrai o login (subject) do token JWT.
     */
    public String extractLogin(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrai o ID do usuario do token JWT.
     */
    public Long extractUserId(String token) {
        return getClaims(token).get("id", Long.class);
    }

    /**
     * Valida se o token ainda esta dentro do prazo de expiracao.
     * Retorna false para tokens expirados ou invalidos.
     */
    public boolean isTokenValid(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Decodifica e valida o token, retornando os claims.
     * Lanca excecao se o token for invalido ou expirado.
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Gera a chave de assinatura HMAC a partir do secret configurado.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
