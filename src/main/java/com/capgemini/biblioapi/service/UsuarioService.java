package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.dto.*;
import com.capgemini.biblioapi.entity.Emprestimo;
import com.capgemini.biblioapi.entity.StatusEmprestimo;
import com.capgemini.biblioapi.entity.Usuario;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.repository.EmprestimoRepository;
import com.capgemini.biblioapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servico de negocio para gerenciamento de usuarios.
 * Responsavel por CRUD, autenticacao e validacao de senhas.
 * 
 * Fluxo de seguranca:
 * 1. Senha recebida criptografada (AES) -> descriptografada
 * 2. Validacao de forca da senha
 * 3. Hash BCrypt para armazenamento
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final CryptoService cryptoService;

    /**
     * Cria um novo usuario no sistema.
     * 
     * Processo:
     * 1. Descriptografa a senha recebida (AES)
     * 2. Valida forca da senha (min 8 chars, maiuscula, minuscula, numero, especial)
     * 3. Verifica unicidade do login
     * 4. Gera hash BCrypt da senha
     * 5. Persiste o usuario
     * 
     * @param dto Dados do usuario com senha criptografada
     * @return Usuario criado (sem dados sensiveis)
     */
    @Transactional
    public UsuarioResponseDTO criar(UsuarioCreateDTO dto) {
        // Descriptografa a senha (suporta AES simples ou RSA+AES hibrido)
        String senhaPura = cryptoService.decrypt(dto.getSenhaCriptografada());

        // Valida requisitos de forca da senha
        validarForcaSenha(senhaPura);

        // Verifica se login ja existe
        if (usuarioRepository.existsByLogin(dto.getLogin())) {
            throw new BusinessException("Login ja existe", HttpStatus.CONFLICT);
        }

        // Cria usuario com senha hasheada (BCrypt)
        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .login(dto.getLogin())
                .senhaHash(cryptoService.hashPassword(senhaPura))
                .build();

        usuario = usuarioRepository.save(usuario);

        return toResponseDTO(usuario, List.of(), List.of());
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario nao encontrado", HttpStatus.NOT_FOUND));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            usuario.setNome(dto.getNome());
        }

        if (dto.getLogin() != null && !dto.getLogin().isBlank()) {
            if (!usuario.getLogin().equals(dto.getLogin()) && usuarioRepository.existsByLogin(dto.getLogin())) {
                throw new BusinessException("Login ja existe", HttpStatus.CONFLICT);
            }
            usuario.setLogin(dto.getLogin());
        }

        if (dto.getSenhaCriptografada() != null && !dto.getSenhaCriptografada().isBlank()) {
            String senhaPura = cryptoService.decrypt(dto.getSenhaCriptografada());
            validarForcaSenha(senhaPura);
            usuario.setSenhaHash(cryptoService.hashPassword(senhaPura));
        }

        usuario = usuarioRepository.save(usuario);

        List<Emprestimo> emAndamento = emprestimoRepository.findByUsuarioIdAndStatus(id, StatusEmprestimo.EM_ANDAMENTO);
        List<Emprestimo> finalizados = emprestimoRepository.findByUsuarioIdAndStatus(id, StatusEmprestimo.FINALIZADO);

        return toResponseDTO(usuario, emAndamento, finalizados);
    }

    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new BusinessException("Usuario nao encontrado", HttpStatus.NOT_FOUND);
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> {
                    List<Emprestimo> emAndamento = emprestimoRepository.findByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.EM_ANDAMENTO);
                    List<Emprestimo> finalizados = emprestimoRepository.findByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.FINALIZADO);
                    return toResponseDTO(usuario, emAndamento, finalizados);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario nao encontrado", HttpStatus.NOT_FOUND));
        
        List<Emprestimo> emAndamento = emprestimoRepository.findByUsuarioIdAndStatus(id, StatusEmprestimo.EM_ANDAMENTO);
        List<Emprestimo> finalizados = emprestimoRepository.findByUsuarioIdAndStatus(id, StatusEmprestimo.FINALIZADO);
        
        return toResponseDTO(usuario, emAndamento, finalizados);
    }

    /**
     * Autentica usuario e retorna seus dados com emprestimos.
     * 
     * Seguranca:
     * - Credenciais recebidas criptografadas (AES)
     * - Mensagem de erro generica para evitar enumeracao de usuarios
     * - Verificacao de senha via BCrypt (tempo constante)
     * 
     * @param dto Credenciais criptografadas
     * @return Dados do usuario autenticado
     * @throws BusinessException se credenciais invalidas
     */
    @Transactional(readOnly = true)
    public UsuarioResponseDTO autenticarEBuscar(UsuarioAuthDTO dto) {
        String loginPuro;
        String senhaPura;

        // Descriptografa credenciais (suporta AES simples ou RSA+AES hibrido)
        try {
            loginPuro = cryptoService.decrypt(dto.getLoginCriptografado());
            senhaPura = cryptoService.decrypt(dto.getSenhaCriptografada());
        } catch (Exception e) {
            throw new BusinessException("Credenciais invalidas", HttpStatus.UNAUTHORIZED);
        }

        // Busca usuario - mensagem generica para nao revelar se login existe
        Usuario usuario = usuarioRepository.findByLogin(loginPuro)
                .orElseThrow(() -> new BusinessException("Credenciais invalidas", HttpStatus.UNAUTHORIZED));

        // Verifica senha via BCrypt (comparacao em tempo constante)
        if (!cryptoService.verifyPassword(senhaPura, usuario.getSenhaHash())) {
            throw new BusinessException("Credenciais invalidas", HttpStatus.UNAUTHORIZED);
        }

        List<Emprestimo> emAndamento = emprestimoRepository.findByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.EM_ANDAMENTO);
        List<Emprestimo> finalizados = emprestimoRepository.findByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.FINALIZADO);

        return toResponseDTO(usuario, emAndamento, finalizados);
    }

    /**
     * Valida requisitos de forca da senha.
     * 
     * Requisitos:
     * - Minimo 8 caracteres
     * - Pelo menos 1 letra maiuscula
     * - Pelo menos 1 letra minuscula
     * - Pelo menos 1 numero
     * - Pelo menos 1 caractere especial
     * 
     * @param senha Senha em texto puro
     * @throws BusinessException se nao atender requisitos
     */
    private void validarForcaSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new BusinessException("Senha deve ter no minimo 8 caracteres", HttpStatus.BAD_REQUEST);
        }
        if (!senha.matches(".*[A-Z].*")) {
            throw new BusinessException("Senha deve conter pelo menos uma letra maiuscula", HttpStatus.BAD_REQUEST);
        }
        if (!senha.matches(".*[a-z].*")) {
            throw new BusinessException("Senha deve conter pelo menos uma letra minuscula", HttpStatus.BAD_REQUEST);
        }
        if (!senha.matches(".*\\d.*")) {
            throw new BusinessException("Senha deve conter pelo menos um numero", HttpStatus.BAD_REQUEST);
        }
        if (!senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new BusinessException("Senha deve conter pelo menos um caractere especial", HttpStatus.BAD_REQUEST);
        }
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario, List<Emprestimo> emAndamento, List<Emprestimo> finalizados) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .login(usuario.getLogin())
                .dataCriacao(usuario.getDataCriacao())
                .emprestimosEmAndamento(emAndamento.stream().map(this::toEmprestimoDTO).collect(Collectors.toList()))
                .emprestimosFinalizados(finalizados.stream().map(this::toEmprestimoDTO).collect(Collectors.toList()))
                .build();
    }

    private EmprestimoResponseDTO toEmprestimoDTO(Emprestimo emprestimo) {
        return EmprestimoResponseDTO.builder()
                .id(emprestimo.getId())
                .livro(toLivroDTO(emprestimo.getLivro()))
                .dataEmprestimo(emprestimo.getDataEmprestimo())
                .dataDevolucao(emprestimo.getDataDevolucao())
                .status(emprestimo.getStatus())
                .build();
    }

    private LivroResponseDTO toLivroDTO(com.capgemini.biblioapi.entity.Livro livro) {
        return LivroResponseDTO.builder()
                .id(livro.getId())
                .titulo(livro.getTitulo())
                .autor(livro.getAutor())
                .isbn(livro.getIsbn())
                .status(livro.getStatus())
                .build();
    }
}
