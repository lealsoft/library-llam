package br.tec.llam.biblioteca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.tec.llam.biblioteca.entity.UsuarioLocal;

@Repository
public interface UsuarioLocalRepository extends JpaRepository<UsuarioLocal, Long> {
    Optional<UsuarioLocal> findByDocumento(String documento);
}
