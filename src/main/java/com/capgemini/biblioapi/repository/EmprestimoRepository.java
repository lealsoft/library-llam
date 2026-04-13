package com.capgemini.biblioapi.repository;

import com.capgemini.biblioapi.entity.Emprestimo;
import com.capgemini.biblioapi.entity.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    List<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);

    List<Emprestimo> findByUsuarioId(Long usuarioId);

    List<Emprestimo> findByLivroIdAndStatus(Long livroId, StatusEmprestimo status);
}
