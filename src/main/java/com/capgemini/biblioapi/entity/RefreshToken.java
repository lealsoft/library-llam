package com.capgemini.biblioapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entidade que representa um Refresh Token.
 * 
 * Caracteristicas:
 * - Token unico e aleatorio (UUID)
 * - Associado a um usuario
 * - Possui data de expiracao (7 dias por padrao)
 * - Pode ser revogado (logout)
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Token unico (UUID) */
    @Column(nullable = false, unique = true)
    private String token;

    /** Usuario dono do token */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Data/hora de expiracao */
    @Column(nullable = false)
    private Instant expiryDate;

    /** Indica se o token foi revogado (logout) */
    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;
}
