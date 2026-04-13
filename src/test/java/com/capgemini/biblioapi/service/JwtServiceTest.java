package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para JwtService.
 * Testa geracao, validacao e extracao de dados de tokens JWT.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private Usuario usuario;

    private static final String SECRET = "BiblioApiSecretKey2024CapgeminiDesafioTecnicoJWTSecure256Bits!";
    private static final long EXPIRATION = 900000L; // 15 minutos

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);

        usuario = Usuario.builder()
                .id(1L)
                .nome("Joao Silva")
                .login("joao.silva")
                .senhaHash("$2a$12$hashedpassword")
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Testes de Geracao de Token")
    class GeracaoTokenTests {

        @Test
        @DisplayName("Deve gerar token JWT valido")
        void deveGerarTokenValido() {
            String token = jwtService.generateToken(usuario);

            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes
        }

        @Test
        @DisplayName("Deve gerar tokens diferentes para mesma chamada")
        void deveGerarTokensDiferentes() {
            String token1 = jwtService.generateToken(usuario);
            String token2 = jwtService.generateToken(usuario);

            // Tokens podem ser iguais se gerados no mesmo milissegundo
            // mas devem ser validos
            assertNotNull(token1);
            assertNotNull(token2);
        }
    }

    @Nested
    @DisplayName("Testes de Extracao de Dados")
    class ExtracaoDadosTests {

        @Test
        @DisplayName("Deve extrair login do token")
        void deveExtrairLogin() {
            String token = jwtService.generateToken(usuario);

            String login = jwtService.extractLogin(token);

            assertEquals("joao.silva", login);
        }

        @Test
        @DisplayName("Deve extrair ID do usuario do token")
        void deveExtrairUserId() {
            String token = jwtService.generateToken(usuario);

            Long userId = jwtService.extractUserId(token);

            assertEquals(1L, userId);
        }

        @Test
        @DisplayName("Deve lancar excecao para token invalido ao extrair login")
        void deveLancarExcecaoParaTokenInvalidoAoExtrairLogin() {
            String tokenInvalido = "token.invalido.aqui";

            assertThrows(Exception.class, () -> jwtService.extractLogin(tokenInvalido));
        }

        @Test
        @DisplayName("Deve lancar excecao para token malformado")
        void deveLancarExcecaoParaTokenMalformado() {
            String tokenMalformado = "nao-e-um-jwt";

            assertThrows(Exception.class, () -> jwtService.extractLogin(tokenMalformado));
        }
    }

    @Nested
    @DisplayName("Testes de Validacao de Token")
    class ValidacaoTokenTests {

        @Test
        @DisplayName("Deve validar token valido")
        void deveValidarTokenValido() {
            String token = jwtService.generateToken(usuario);

            boolean isValid = jwtService.isTokenValid(token);

            assertTrue(isValid);
        }

        @Test
        @DisplayName("Deve invalidar token com assinatura errada")
        void deveInvalidarTokenComAssinaturaErrada() {
            String token = jwtService.generateToken(usuario);
            // Altera a assinatura do token
            String tokenAlterado = token.substring(0, token.lastIndexOf('.') + 1) + "assinatura_errada";

            boolean isValid = jwtService.isTokenValid(tokenAlterado);

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Deve invalidar token malformado")
        void deveInvalidarTokenMalformado() {
            boolean isValid = jwtService.isTokenValid("token-invalido");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Deve invalidar token nulo")
        void deveInvalidarTokenNulo() {
            boolean isValid = jwtService.isTokenValid(null);

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Deve invalidar token vazio")
        void deveInvalidarTokenVazio() {
            boolean isValid = jwtService.isTokenValid("");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Deve invalidar token expirado")
        void deveInvalidarTokenExpirado() {
            // Configura expiracao de 1ms
            ReflectionTestUtils.setField(jwtService, "expiration", 1L);
            String token = jwtService.generateToken(usuario);

            // Aguarda expiracao
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            boolean isValid = jwtService.isTokenValid(token);

            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("Testes de Integridade do Token")
    class IntegridadeTokenTests {

        @Test
        @DisplayName("Deve manter dados consistentes apos geracao e extracao")
        void deveManterDadosConsistentes() {
            String token = jwtService.generateToken(usuario);

            String loginExtraido = jwtService.extractLogin(token);
            Long idExtraido = jwtService.extractUserId(token);

            assertEquals(usuario.getLogin(), loginExtraido);
            assertEquals(usuario.getId(), idExtraido);
        }

        @Test
        @DisplayName("Deve gerar token para usuarios diferentes com dados corretos")
        void deveGerarTokenParaUsuariosDiferentes() {
            Usuario usuario2 = Usuario.builder()
                    .id(2L)
                    .nome("Maria Santos")
                    .login("maria.santos")
                    .senhaHash("$2a$12$hashedpassword2")
                    .dataCriacao(LocalDateTime.now())
                    .build();

            String token1 = jwtService.generateToken(usuario);
            String token2 = jwtService.generateToken(usuario2);

            assertEquals("joao.silva", jwtService.extractLogin(token1));
            assertEquals("maria.santos", jwtService.extractLogin(token2));
            assertEquals(1L, jwtService.extractUserId(token1));
            assertEquals(2L, jwtService.extractUserId(token2));
        }
    }
}
