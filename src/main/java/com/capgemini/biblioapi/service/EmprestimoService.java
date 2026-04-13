package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.dto.EmprestimoCreateDTO;
import com.capgemini.biblioapi.dto.EmprestimoResponseDTO;
import com.capgemini.biblioapi.dto.LivroResponseDTO;
import com.capgemini.biblioapi.entity.Emprestimo;
import com.capgemini.biblioapi.entity.Livro;
import com.capgemini.biblioapi.entity.StatusEmprestimo;
import com.capgemini.biblioapi.entity.StatusLivro;
import com.capgemini.biblioapi.entity.Usuario;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.repository.EmprestimoRepository;
import com.capgemini.biblioapi.repository.LivroRepository;
import com.capgemini.biblioapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servico de negocio para gerenciamento de emprestimos de livros.
 * Controla o fluxo de emprestimo e devolucao, mantendo a consistencia
 * entre o status do emprestimo e a disponibilidade do livro.
 */
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> listarTodos() {
        return emprestimoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO buscarPorId(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Emprestimo nao encontrado", HttpStatus.NOT_FOUND));
        return toResponseDTO(emprestimo);
    }

    /**
     * Realiza o emprestimo de um livro para um usuario.
     * 
     * Regras de negocio:
     * - Usuario e livro devem existir
     * - Livro deve estar com status DISPONIVEL
     * - Apos emprestimo, livro fica com status EMPRESTADO
     * 
     * @param dto IDs do usuario e livro
     * @return Dados do emprestimo criado
     * @throws BusinessException se livro indisponivel ou entidades nao encontradas
     */
    @Transactional
    public EmprestimoResponseDTO realizarEmprestimo(EmprestimoCreateDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new BusinessException("Usuario nao encontrado", HttpStatus.NOT_FOUND));

        Livro livro = livroRepository.findById(dto.getLivroId())
                .orElseThrow(() -> new BusinessException("Livro nao encontrado", HttpStatus.NOT_FOUND));

        // Verifica disponibilidade do livro
        if (livro.getStatus() == StatusLivro.EMPRESTADO) {
            throw new BusinessException("Livro nao esta disponivel para emprestimo", HttpStatus.BAD_REQUEST);
        }

        // Atualiza status do livro para EMPRESTADO
        livro.setStatus(StatusLivro.EMPRESTADO);
        livroRepository.save(livro);

        // Cria registro de emprestimo
        Emprestimo emprestimo = Emprestimo.builder()
                .usuario(usuario)
                .livro(livro)
                .dataEmprestimo(LocalDateTime.now())
                .status(StatusEmprestimo.EM_ANDAMENTO)
                .build();

        emprestimo = emprestimoRepository.save(emprestimo);
        return toResponseDTO(emprestimo);
    }

    /**
     * Realiza a devolucao de um livro emprestado.
     * 
     * Regras de negocio:
     * - Emprestimo deve existir e estar EM_ANDAMENTO
     * - Apos devolucao, emprestimo fica FINALIZADO
     * - Livro volta ao status DISPONIVEL
     * 
     * @param emprestimoId ID do emprestimo a ser finalizado
     * @return Dados do emprestimo atualizado
     * @throws BusinessException se emprestimo nao encontrado ou ja finalizado
     */
    @Transactional
    public EmprestimoResponseDTO realizarDevolucao(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new BusinessException("Emprestimo nao encontrado", HttpStatus.NOT_FOUND));

        // Verifica se emprestimo ainda esta em andamento
        if (emprestimo.getStatus() == StatusEmprestimo.FINALIZADO) {
            throw new BusinessException("Este emprestimo ja foi finalizado", HttpStatus.BAD_REQUEST);
        }

        // Finaliza emprestimo
        emprestimo.setStatus(StatusEmprestimo.FINALIZADO);
        emprestimo.setDataDevolucao(LocalDateTime.now());

        // Libera livro para novos emprestimos
        Livro livro = emprestimo.getLivro();
        livro.setStatus(StatusLivro.DISPONIVEL);
        livroRepository.save(livro);

        emprestimo = emprestimoRepository.save(emprestimo);
        return toResponseDTO(emprestimo);
    }

    private EmprestimoResponseDTO toResponseDTO(Emprestimo emprestimo) {
        return EmprestimoResponseDTO.builder()
                .id(emprestimo.getId())
                .livro(toLivroDTO(emprestimo.getLivro()))
                .dataEmprestimo(emprestimo.getDataEmprestimo())
                .dataDevolucao(emprestimo.getDataDevolucao())
                .status(emprestimo.getStatus())
                .build();
    }

    private LivroResponseDTO toLivroDTO(Livro livro) {
        return LivroResponseDTO.builder()
                .id(livro.getId())
                .titulo(livro.getTitulo())
                .autor(livro.getAutor())
                .isbn(livro.getIsbn())
                .status(livro.getStatus())
                .build();
    }
}
