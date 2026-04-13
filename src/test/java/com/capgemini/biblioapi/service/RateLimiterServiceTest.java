package com.capgemini.biblioapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para RateLimiterService.
 * Testa rate limiting para login e requisicoes gerais.
 */
class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService();
    }

    @Nested
    @DisplayName("Testes de Rate Limiting para Login")
    class LoginRateLimitTests {

        @Test
        @DisplayName("Deve permitir 5 tentativas de login")
        void devePermitir5TentativasDeLogin() {
            String ip = "192.168.1.1";

            for (int i = 0; i < 5; i++) {
                assertTrue(rateLimiterService.tryConsumeLogin(ip),
                        "Tentativa " + (i + 1) + " deveria ser permitida");
            }
        }

        @Test
        @DisplayName("Deve bloquear apos 5 tentativas de login")
        void deveBloquearApos5Tentativas() {
            String ip = "192.168.1.2";

            // Consome as 5 tentativas
            for (int i = 0; i < 5; i++) {
                rateLimiterService.tryConsumeLogin(ip);
            }

            // 6a tentativa deve ser bloqueada
            assertFalse(rateLimiterService.tryConsumeLogin(ip));
        }

        @Test
        @DisplayName("Deve ter buckets separados por IP")
        void deveTerBucketsSeparadosPorIp() {
            String ip1 = "192.168.1.3";
            String ip2 = "192.168.1.4";

            // Consome todas as tentativas do IP1
            for (int i = 0; i < 5; i++) {
                rateLimiterService.tryConsumeLogin(ip1);
            }

            // IP2 ainda deve ter tentativas
            assertTrue(rateLimiterService.tryConsumeLogin(ip2));
        }

        @Test
        @DisplayName("Deve retornar tentativas restantes corretamente")
        void deveRetornarTentativasRestantes() {
            String ip = "192.168.1.5";

            // Inicialmente deve ter 5 tentativas
            assertEquals(5, rateLimiterService.getLoginAttemptsRemaining(ip));

            // Apos consumir 2, deve ter 3
            rateLimiterService.tryConsumeLogin(ip);
            rateLimiterService.tryConsumeLogin(ip);
            assertEquals(3, rateLimiterService.getLoginAttemptsRemaining(ip));
        }
    }

    @Nested
    @DisplayName("Testes de Rate Limiting Geral")
    class GeneralRateLimitTests {

        @Test
        @DisplayName("Deve permitir 100 requisicoes gerais")
        void devePermitir100RequisicoesGerais() {
            String ip = "192.168.1.10";

            for (int i = 0; i < 100; i++) {
                assertTrue(rateLimiterService.tryConsumeGeneral(ip),
                        "Requisicao " + (i + 1) + " deveria ser permitida");
            }
        }

        @Test
        @DisplayName("Deve bloquear apos 100 requisicoes gerais")
        void deveBloquearApos100Requisicoes() {
            String ip = "192.168.1.11";

            // Consome as 100 requisicoes
            for (int i = 0; i < 100; i++) {
                rateLimiterService.tryConsumeGeneral(ip);
            }

            // 101a requisicao deve ser bloqueada
            assertFalse(rateLimiterService.tryConsumeGeneral(ip));
        }
    }

    @Nested
    @DisplayName("Testes de Limpeza de Buckets")
    class LimpezaBucketsTests {

        @Test
        @DisplayName("Deve limpar buckets corretamente")
        void deveLimparBuckets() {
            String ip = "192.168.1.20";

            // Consome todas as tentativas
            for (int i = 0; i < 5; i++) {
                rateLimiterService.tryConsumeLogin(ip);
            }
            assertFalse(rateLimiterService.tryConsumeLogin(ip));

            // Limpa buckets
            rateLimiterService.clearOldBuckets();

            // Deve permitir novamente
            assertTrue(rateLimiterService.tryConsumeLogin(ip));
        }
    }
}
