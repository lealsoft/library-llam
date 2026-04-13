package com.capgemini.biblioapi.repository;

import com.capgemini.biblioapi.entity.Livro;
import com.capgemini.biblioapi.entity.StatusLivro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByIsbn(String isbn);

    List<Livro> findByStatus(StatusLivro status);

    boolean existsByIsbn(String isbn);

    /** Ordenado por data de cadastro (mais recentes primeiro) */
    List<Livro> findAllByOrderByDataCadastroDesc();

    /** Ordenado por data de cadastro (mais antigos primeiro) */
    List<Livro> findAllByOrderByDataCadastroAsc();

    /** Ordenado por titulo alfabeticamente */
    List<Livro> findAllByOrderByTituloAsc();

    /** Livros disponiveis ordenados por data de cadastro */
    List<Livro> findByStatusOrderByDataCadastroDesc(StatusLivro status);

    /** Livros mais emprestados (ordenado por quantidade de emprestimos) */
    @Query("SELECT l FROM Livro l LEFT JOIN l.emprestimos e GROUP BY l ORDER BY COUNT(e) DESC")
    List<Livro> findAllOrderByTotalEmprestimosDesc();

    /** Livros disponiveis mais emprestados */
    @Query("SELECT l FROM Livro l LEFT JOIN l.emprestimos e WHERE l.status = :status GROUP BY l ORDER BY COUNT(e) DESC")
    List<Livro> findByStatusOrderByTotalEmprestimosDesc(StatusLivro status);
}
