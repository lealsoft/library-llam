package br.tec.llam.biblioteca.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "UsuarioLocal")
public class UsuarioLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Documento atuará como login do leitor
    private String documento;

    // Guardaremos a senha criptografada via BCrypt
    private String senhaHash;

    // A URI ou ID do Person correspondente no Ecos
    private String personId;
}
