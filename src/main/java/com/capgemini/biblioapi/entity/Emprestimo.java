package com.capgemini.biblioapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um emprestimo de livro.
 * 
 * Ciclo de vida:
 * 1. Criacao: status = EM_ANDAMENTO, dataEmprestimo = agora
 * 2. Devolucao: status = FINALIZADO, dataDevolucao = agora
 * 
 * Relacionamentos:
 * - ManyToOne com Usuario (quem pegou emprestado)
 * - ManyToOne com Livro (qual livro foi emprestado)
 */
@Entity
@Table(name = "emprestimos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario que realizou o emprestimo */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Livro emprestado */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    /** Data em que o emprestimo foi realizado */
    @Column(nullable = false)
    private LocalDateTime dataEmprestimo;

    /** Data da devolucao (null enquanto em andamento) */
    private LocalDateTime dataDevolucao;

    /** Status atual: EM_ANDAMENTO ou FINALIZADO */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusEmprestimo status = StatusEmprestimo.EM_ANDAMENTO;

    @PrePersist
    protected void onCreate() {
        this.dataEmprestimo = LocalDateTime.now();
    }
}
