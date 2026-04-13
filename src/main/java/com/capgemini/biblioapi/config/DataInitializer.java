package com.capgemini.biblioapi.config;

import com.capgemini.biblioapi.entity.*;
import com.capgemini.biblioapi.repository.EmprestimoRepository;
import com.capgemini.biblioapi.repository.LivroRepository;
import com.capgemini.biblioapi.repository.UsuarioRepository;
import com.capgemini.biblioapi.service.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final CryptoService cryptoService;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Inicializando dados de exemplo...");

        Usuario usuario1 = Usuario.builder()
                .nome("Joao Silva")
                .login("joao.silva")
                .senhaHash(cryptoService.hashPassword("Senha@123"))
                .build();
        usuario1 = usuarioRepository.save(usuario1);

        Usuario usuario2 = Usuario.builder()
                .nome("Maria Santos")
                .login("maria.santos")
                .senhaHash(cryptoService.hashPassword("Senha@456"))
                .build();
        usuario2 = usuarioRepository.save(usuario2);

        Usuario usuario3 = Usuario.builder()
                .nome("Pedro Oliveira")
                .login("pedro.oliveira")
                .senhaHash(cryptoService.hashPassword("Senha@789"))
                .build();
        usuario3 = usuarioRepository.save(usuario3);

        Livro livro1 = Livro.builder()
                .titulo("Clean Code")
                .autor("Robert C. Martin")
                .isbn("978-0132350884")
                .status(StatusLivro.EMPRESTADO)
                .build();
        livro1 = livroRepository.save(livro1);

        Livro livro2 = Livro.builder()
                .titulo("Design Patterns")
                .autor("Gang of Four")
                .isbn("978-0201633610")
                .status(StatusLivro.DISPONIVEL)
                .build();
        livro2 = livroRepository.save(livro2);

        Livro livro3 = Livro.builder()
                .titulo("Domain-Driven Design")
                .autor("Eric Evans")
                .isbn("978-0321125217")
                .status(StatusLivro.EMPRESTADO)
                .build();
        livro3 = livroRepository.save(livro3);

        Livro livro4 = Livro.builder()
                .titulo("Refactoring")
                .autor("Martin Fowler")
                .isbn("978-0134757599")
                .status(StatusLivro.DISPONIVEL)
                .build();
        livro4 = livroRepository.save(livro4);

        Livro livro5 = Livro.builder()
                .titulo("The Pragmatic Programmer")
                .autor("David Thomas, Andrew Hunt")
                .isbn("978-0135957059")
                .status(StatusLivro.DISPONIVEL)
                .build();
        livro5 = livroRepository.save(livro5);

        Emprestimo emprestimo1 = Emprestimo.builder()
                .usuario(usuario1)
                .livro(livro1)
                .dataEmprestimo(LocalDateTime.now().minusDays(10))
                .status(StatusEmprestimo.EM_ANDAMENTO)
                .build();
        emprestimoRepository.save(emprestimo1);

        Emprestimo emprestimo2 = Emprestimo.builder()
                .usuario(usuario1)
                .livro(livro2)
                .dataEmprestimo(LocalDateTime.now().minusDays(30))
                .dataDevolucao(LocalDateTime.now().minusDays(15))
                .status(StatusEmprestimo.FINALIZADO)
                .build();
        emprestimoRepository.save(emprestimo2);

        Emprestimo emprestimo3 = Emprestimo.builder()
                .usuario(usuario2)
                .livro(livro3)
                .dataEmprestimo(LocalDateTime.now().minusDays(5))
                .status(StatusEmprestimo.EM_ANDAMENTO)
                .build();
        emprestimoRepository.save(emprestimo3);

        log.info("Dados de exemplo inicializados com sucesso!");
        log.info("Usuarios criados: joao.silva (Senha@123), maria.santos (Senha@456), pedro.oliveira (Senha@789)");
    }
}
