package br.tec.llam.biblioteca.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "llam.biblioteca.titulo.attributes")
public class TituloAttributeConfig {
    
    private Integer titulo;
    private Integer subtitulo;
    private Integer isbn;
    private Integer autor;
    private Integer editora;
    private Integer categoria;
    private Integer capaUrl;
    private Integer sinopse;
    private Integer anoPublicacao;
    private Integer numeroPaginas;
    private Integer edicao;
}
