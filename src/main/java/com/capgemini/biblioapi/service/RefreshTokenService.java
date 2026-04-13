package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.entity.RefreshToken;
import com.capgemini.biblioapi.entity.Usuario;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Servico para gerenciamento de Refresh Tokens.
 * 
 * Responsabilidades:
 * - Criar novos refresh tokens
 * - Validar tokens existentes
 * - Revogar tokens (logout)
 * - Rotacionar tokens (gerar novo apos uso)
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /** Duracao do refresh token em dias (padrao: 7 dias) */
    @Value("${jwt.refresh-expiration-days:7}")
    private int refreshExpirationDays;

    /**
     * Cria um novo refresh token para o usuario.
     * Revoga tokens anteriores do mesmo usuario.
     */
    @Transactional
    public RefreshToken createRefreshToken(Usuario usuario) {
        // Revoga tokens anteriores do usuario
        refreshTokenRepository.revokeAllByUsuarioId(usuario.getId());

        // Cria novo token
        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(refreshExpirationDays * 24 * 60 * 60))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Valida um refresh token.
     * 
     * @param token Token a ser validado
     * @return RefreshToken se valido
     * @throws BusinessException se token invalido, expirado ou revogado
     */
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token invalido", HttpStatus.UNAUTHORIZED));

        // Verifica se foi revogado
        if (refreshToken.isRevoked()) {
            throw new BusinessException("Refresh token foi revogado", HttpStatus.UNAUTHORIZED);
        }

        // Verifica expiracao
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token expirado", HttpStatus.UNAUTHORIZED);
        }

        return refreshToken;
    }

    /**
     * Rotaciona o refresh token (gera novo apos uso).
     * Isso aumenta a seguranca - cada refresh token so pode ser usado uma vez.
     */
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        // Revoga o token antigo
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        // Cria novo token
        return createRefreshToken(oldToken.getUsuario());
    }

    /**
     * Revoga todos os tokens do usuario (logout).
     */
    @Transactional
    public void revokeAllUserTokens(Long usuarioId) {
        refreshTokenRepository.revokeAllByUsuarioId(usuarioId);
    }

    /**
     * Revoga um token especifico.
     */
    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token invalido", HttpStatus.NOT_FOUND));
        
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
