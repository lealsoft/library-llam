package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.dto.LivroCreateDTO;
import com.capgemini.biblioapi.dto.LivroResponseDTO;
import com.capgemini.biblioapi.entity.Livro;
import com.capgemini.biblioapi.entity.StatusLivro;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servico de negocio para gerenciamento de livros.
 * Responsavel por CRUD de livros e controle de disponibilidade.
 */
@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    /** Lista todos os livros cadastrados com ordenacao */
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> listarTodos(String ordenacao) {
        List<Livro> livros = switch (ordenacao != null ? ordenacao.toLowerCase() : "recentes") {
            case "antigos" -> livroRepository.findAllByOrderByDataCadastroAsc();
            case "titulo" -> livroRepository.findAllByOrderByTituloAsc();
            case "populares" -> livroRepository.findAllOrderByTotalEmprestimosDesc();
            default -> livroRepository.findAllByOrderByDataCadastroDesc();
        };
        return livros.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** Lista apenas livros com status DISPONIVEL com ordenacao */
    @Transactional(readOnly = true)
    public List<LivroResponseDTO> listarDisponiveis(String ordenacao) {
        List<Livro> livros = switch (ordenacao != null ? ordenacao.toLowerCase() : "recentes") {
            case "populares" -> livroRepository.findByStatusOrderByTotalEmprestimosDesc(StatusLivro.DISPONIVEL);
            default -> livroRepository.findByStatusOrderByDataCadastroDesc(StatusLivro.DISPONIVEL);
        };
        return livros.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** Busca livro por ID */
    @Transactional(readOnly = true)
    public LivroResponseDTO buscarPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Livro nao encontrado", HttpStatus.NOT_FOUND));
        return toResponseDTO(livro);
    }

    /**
     * Cadastra novo livro no acervo.
     * ISBN deve ser unico.
     */
    @Transactional
    public LivroResponseDTO criar(LivroCreateDTO dto) {
        // Verifica unicidade do ISBN
        if (livroRepository.existsByIsbn(dto.getIsbn())) {
            throw new BusinessException("ISBN ja cadastrado", HttpStatus.CONFLICT);
        }

        // Cria livro com status inicial DISPONIVEL
        Livro livro = Livro.builder()
                .titulo(dto.getTitulo())
                .autor(dto.getAutor())
                .isbn(dto.getIsbn())
                .status(StatusLivro.DISPONIVEL)
                .build();

        livro = livroRepository.save(livro);
        return toResponseDTO(livro);
    }

    /**
     * Remove livro do acervo.
     * Nao permite deletar livros emprestados.
     */
    @Transactional
    public void deletar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Livro nao encontrado", HttpStatus.NOT_FOUND));

        // Impede exclusao de livro emprestado
        if (livro.getStatus() == StatusLivro.EMPRESTADO) {
            throw new BusinessException("Nao e possivel deletar um livro emprestado", HttpStatus.BAD_REQUEST);
        }

        livroRepository.delete(livro);
    }

    /** Converte entidade para DTO de resposta */
    private LivroResponseDTO toResponseDTO(Livro livro) {
        return LivroResponseDTO.builder()
                .id(livro.getId())
                .titulo(livro.getTitulo())
                .autor(livro.getAutor())
                .isbn(livro.getIsbn())
                .status(livro.getStatus())
                .dataCadastro(livro.getDataCadastro())
                .totalEmprestimos(livro.getEmprestimos() != null ? livro.getEmprestimos().size() : 0)
                .build();
    }
}
