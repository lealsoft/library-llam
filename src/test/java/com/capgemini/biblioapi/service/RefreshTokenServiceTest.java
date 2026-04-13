package com.capgemini.biblioapi.service;

import com.capgemini.biblioapi.entity.RefreshToken;
import com.capgemini.biblioapi.entity.Usuario;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitarios para RefreshTokenService.
 * Testa criacao, validacao, rotacao e revogacao de refresh tokens.
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private Usuario usuario;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationDays", 7);

        usuario = Usuario.builder()
                .id(1L)
                .nome("Joao Silva")
                .login("joao.silva")
                .senhaHash("$2a$12$hashedpassword")
                .dataCriacao(LocalDateTime.now())
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token("550e8400-e29b-41d4-a716-446655440000")
                .usuario(usuario)
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .revoked(false)
                .build();
    }

    @Nested
    @DisplayName("Testes de Criacao de Refresh Token")
    class CriacaoRefreshTokenTests {

        @Test
        @DisplayName("Deve criar refresh token para usuario")
        void deveCriarRefreshToken() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            RefreshToken result = refreshTokenService.createRefreshToken(usuario);

            assertNotNull(result);
            assertNotNull(result.getToken());
            assertEquals(usuario, result.getUsuario());
            assertFalse(result.isRevoked());
            assertTrue(result.getExpiryDate().isAfter(Instant.now()));

            verify(refreshTokenRepository).revokeAllByUsuarioId(usuario.getId());
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Deve revogar tokens anteriores ao criar novo")
        void deveRevogarTokensAnteriores() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            refreshTokenService.createRefreshToken(usuario);

            verify(refreshTokenRepository).revokeAllByUsuarioId(usuario.getId());
        }

        @Test
        @DisplayName("Deve gerar token UUID unico")
        void deveGerarTokenUnico() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            RefreshToken token1 = refreshTokenService.createRefreshToken(usuario);
            RefreshToken token2 = refreshTokenService.createRefreshToken(usuario);

            assertNotEquals(token1.getToken(), token2.getToken());
        }
    }

    @Nested
    @DisplayName("Testes de Validacao de Refresh Token")
    class ValidacaoRefreshTokenTests {

        @Test
        @DisplayName("Deve validar token valido")
        void deveValidarTokenValido() {
            when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                    .thenReturn(Optional.of(refreshToken));

            RefreshToken result = refreshTokenService.validateRefreshToken(refreshToken.getToken());

            assertNotNull(result);
            assertEquals(refreshToken.getToken(), result.getToken());
        }

        @Test
        @DisplayName("Deve lancar excecao para token inexistente")
        void deveLancarExcecaoParaTokenInexistente() {
            when(refreshTokenRepository.findByToken("token-inexistente"))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> refreshTokenService.validateRefreshToken("token-inexistente"));

            assertEquals("Refresh token invalido", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lancar excecao para token revogado")
        void deveLancarExcecaoParaTokenRevogado() {
            refreshToken.setRevoked(true);
            when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                    .thenReturn(Optional.of(refreshToken));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> refreshTokenService.validateRefreshToken(refreshToken.getToken()));

            assertEquals("Refresh token foi revogado", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lancar excecao para token expirado")
        void deveLancarExcecaoParaTokenExpirado() {
            refreshToken.setExpiryDate(Instant.now().minusSeconds(1));
            when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                    .thenReturn(Optional.of(refreshToken));

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> refreshTokenService.validateRefreshToken(refreshToken.getToken()));

            assertEquals("Refresh token expirado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de Rotacao de Refresh Token")
    class RotacaoRefreshTokenTests {

        @Test
        @DisplayName("Deve rotacionar token - revogar antigo e criar novo")
        void deveRotacionarToken() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            RefreshToken newToken = refreshTokenService.rotateRefreshToken(refreshToken);

            assertTrue(refreshToken.isRevoked());
            assertNotNull(newToken);
            assertNotEquals(refreshToken.getToken(), newToken.getToken());
            assertFalse(newToken.isRevoked());

            verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Deve manter mesmo usuario apos rotacao")
        void deveManterUsuarioAposRotacao() {
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            RefreshToken newToken = refreshTokenService.rotateRefreshToken(refreshToken);

            assertEquals(refreshToken.getUsuario(), newToken.getUsuario());
        }
    }

    @Nested
    @DisplayName("Testes de Revogacao de Refresh Token")
    class RevogacaoRefreshTokenTests {

        @Test
        @DisplayName("Deve revogar todos os tokens do usuario")
        void deveRevogarTodosTokensDoUsuario() {
            refreshTokenService.revokeAllUserTokens(usuario.getId());

            verify(refreshTokenRepository).revokeAllByUsuarioId(usuario.getId());
        }

        @Test
        @DisplayName("Deve revogar token especifico")
        void deveRevogarTokenEspecifico() {
            when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                    .thenReturn(Optional.of(refreshToken));
            when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

            refreshTokenService.revokeToken(refreshToken.getToken());

            assertTrue(refreshToken.isRevoked());
            verify(refreshTokenRepository).save(refreshToken);
        }

        @Test
        @DisplayName("Deve lancar excecao ao revogar token inexistente")
        void deveLancarExcecaoAoRevogarTokenInexistente() {
            when(refreshTokenRepository.findByToken("token-inexistente"))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> refreshTokenService.revokeToken("token-inexistente"));

            assertEquals("Refresh token invalido", exception.getMessage());
        }
    }
}
