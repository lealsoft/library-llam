package com.capgemini.biblioapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um livro no acervo da biblioteca.
 * 
 * Status:
 * - DISPONIVEL: pode ser emprestado
 * - EMPRESTADO: ja esta com algum usuario
 */
@Entity
@Table(name = "livros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    /** ISBN unico - identificador internacional do livro */
    @Column(nullable = false, unique = true)
    private String isbn;

    /** Status atual: DISPONIVEL ou EMPRESTADO */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusLivro status = StatusLivro.DISPONIVEL;

    /** Data de cadastro do livro no acervo */
    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    /** Historico de emprestimos deste livro */
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Emprestimo> emprestimos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
    }
}
