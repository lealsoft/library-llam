package com.capgemini.biblioapi.repository;

import com.capgemini.biblioapi.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para RefreshToken.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /** Busca token pelo valor */
    Optional<RefreshToken> findByToken(String token);

    /** Busca token ativo do usuario */
    Optional<RefreshToken> findByUsuarioIdAndRevokedFalse(Long usuarioId);

    /** Revoga todos os tokens do usuario (logout de todas as sessoes) */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.usuario.id = :usuarioId")
    void revokeAllByUsuarioId(Long usuarioId);

    /** Remove tokens expirados (limpeza) */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
