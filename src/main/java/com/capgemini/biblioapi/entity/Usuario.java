package com.capgemini.biblioapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um usuario do sistema de biblioteca.
 * 
 * Seguranca:
 * - senhaHash armazena o hash BCrypt da senha (nunca texto puro)
 * - login e unico para evitar duplicidade
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    /** Login unico do usuario (usado para autenticacao) */
    @Column(nullable = false, unique = true)
    private String login;

    /** Hash BCrypt da senha - NUNCA armazena senha em texto puro */
    @Column(nullable = false)
    private String senhaHash;

    /** Data de criacao do usuario (preenchida automaticamente) */
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    /** Lista de emprestimos do usuario */
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Emprestimo> emprestimos = new ArrayList<>();

    /** Callback executado antes de persistir - define data de criacao */
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }
}
