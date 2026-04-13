package com.capgemini.biblioapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para TokenBlacklistService.
 * Testa adicao, verificacao e remocao de tokens da blacklist.
 */
class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
        ReflectionTestUtils.setField(tokenBlacklistService, "jwtExpiration", 900000L);
        tokenBlacklistService.init();
    }

    @Nested
    @DisplayName("Testes de Adicao a Blacklist")
    class AdicaoBlacklistTests {

        @Test
        @DisplayName("Deve adicionar token a blacklist")
        void deveAdicionarTokenABlacklist() {
            String token = "eyJhbGciOiJIUzI1NiJ9.test.signature";

            tokenBlacklistService.blacklistToken(token);

            assertTrue(tokenBlacklistService.isBlacklisted(token));
        }

        @Test
        @DisplayName("Deve ignorar token nulo")
        void deveIgnorarTokenNulo() {
            tokenBlacklistService.blacklistToken(null);

            assertEquals(0, tokenBlacklistService.getBlacklistSize());
        }

        @Test
        @DisplayName("Deve ignorar token vazio")
        void deveIgnorarTokenVazio() {
            tokenBlacklistService.blacklistToken("");
            tokenBlacklistService.blacklistToken("   ");

            assertEquals(0, tokenBlacklistService.getBlacklistSize());
        }
    }

    @Nested
    @DisplayName("Testes de Verificacao na Blacklist")
    class VerificacaoBlacklistTests {

        @Test
        @DisplayName("Deve retornar true para token na blacklist")
        void deveRetornarTrueParaTokenNaBlacklist() {
            String token = "token-blacklisted";
            tokenBlacklistService.blacklistToken(token);

            assertTrue(tokenBlacklistService.isBlacklisted(token));
        }

        @Test
        @DisplayName("Deve retornar false para token nao na blacklist")
        void deveRetornarFalseParaTokenNaoNaBlacklist() {
            assertFalse(tokenBlacklistService.isBlacklisted("token-nao-blacklisted"));
        }

        @Test
        @DisplayName("Deve retornar false para token nulo")
        void deveRetornarFalseParaTokenNulo() {
            assertFalse(tokenBlacklistService.isBlacklisted(null));
        }

        @Test
        @DisplayName("Deve retornar false para token vazio")
        void deveRetornarFalseParaTokenVazio() {
            assertFalse(tokenBlacklistService.isBlacklisted(""));
        }
    }

    @Nested
    @DisplayName("Testes de Remocao da Blacklist")
    class RemocaoBlacklistTests {

        @Test
        @DisplayName("Deve remover token da blacklist")
        void deveRemoverTokenDaBlacklist() {
            String token = "token-para-remover";
            tokenBlacklistService.blacklistToken(token);
            assertTrue(tokenBlacklistService.isBlacklisted(token));

            tokenBlacklistService.removeFromBlacklist(token);

            assertFalse(tokenBlacklistService.isBlacklisted(token));
        }

        @Test
        @DisplayName("Deve limpar toda a blacklist")
        void deveLimparTodaBlacklist() {
            tokenBlacklistService.blacklistToken("token1");
            tokenBlacklistService.blacklistToken("token2");
            tokenBlacklistService.blacklistToken("token3");
            assertEquals(3, tokenBlacklistService.getBlacklistSize());

            tokenBlacklistService.clearBlacklist();

            assertEquals(0, tokenBlacklistService.getBlacklistSize());
        }
    }

    @Nested
    @DisplayName("Testes de Contagem")
    class ContagemBlacklistTests {

        @Test
        @DisplayName("Deve retornar tamanho correto da blacklist")
        void deveRetornarTamanhoCorreto() {
            assertEquals(0, tokenBlacklistService.getBlacklistSize());

            tokenBlacklistService.blacklistToken("token1");
            assertEquals(1, tokenBlacklistService.getBlacklistSize());

            tokenBlacklistService.blacklistToken("token2");
            assertEquals(2, tokenBlacklistService.getBlacklistSize());
        }

        @Test
        @DisplayName("Nao deve contar tokens duplicados")
        void naoDeveContarTokensDuplicados() {
            tokenBlacklistService.blacklistToken("mesmo-token");
            tokenBlacklistService.blacklistToken("mesmo-token");
            tokenBlacklistService.blacklistToken("mesmo-token");

            assertEquals(1, tokenBlacklistService.getBlacklistSize());
        }
    }
}
