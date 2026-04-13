package com.capgemini.biblioapi.config;

import com.capgemini.biblioapi.entity.Usuario;
import com.capgemini.biblioapi.repository.UsuarioRepository;
import com.capgemini.biblioapi.service.JwtService;
import com.capgemini.biblioapi.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitarios para JwtAuthenticationFilter.
 * Testa interceptacao e validacao de tokens JWT nas requisicoes.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        usuario = Usuario.builder()
                .id(1L)
                .nome("Joao Silva")
                .login("joao.silva")
                .senhaHash("$2a$12$hashedpassword")
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Testes de Requisicoes sem Token")
    class RequisicoesSemTokenTests {

        @Test
        @DisplayName("Deve continuar sem autenticacao quando nao ha header Authorization")
        void deveContinuarSemAutenticacaoQuandoNaoHaHeader() throws Exception {
            when(request.getHeader("Authorization")).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Deve continuar sem autenticacao quando header nao comeca com Bearer")
        void deveContinuarSemAutenticacaoQuandoHeaderNaoComecaComBearer() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Basic abc123");

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Deve continuar sem autenticacao quando header Bearer esta vazio")
        void deveContinuarSemAutenticacaoQuandoBearerVazio() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer ");
            when(tokenBlacklistService.isBlacklisted("")).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Testes de Token Invalido")
    class TokenInvalidoTests {

        @Test
        @DisplayName("Deve continuar sem autenticacao quando token e invalido")
        void deveContinuarSemAutenticacaoQuandoTokenInvalido() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token_invalido");
            when(tokenBlacklistService.isBlacklisted("token_invalido")).thenReturn(false);
            when(jwtService.isTokenValid("token_invalido")).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Deve continuar sem autenticacao quando token esta expirado")
        void deveContinuarSemAutenticacaoQuandoTokenExpirado() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token_expirado");
            when(tokenBlacklistService.isBlacklisted("token_expirado")).thenReturn(false);
            when(jwtService.isTokenValid("token_expirado")).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Nested
    @DisplayName("Testes de Token Valido")
    class TokenValidoTests {

        @Test
        @DisplayName("Deve autenticar usuario quando token e valido")
        void deveAutenticarUsuarioQuandoTokenValido() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token_valido");
            when(tokenBlacklistService.isBlacklisted("token_valido")).thenReturn(false);
            when(jwtService.isTokenValid("token_valido")).thenReturn(true);
            when(jwtService.extractLogin("token_valido")).thenReturn("joao.silva");
            when(usuarioRepository.findByLogin("joao.silva")).thenReturn(Optional.of(usuario));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals(usuario, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }

        @Test
        @DisplayName("Deve continuar sem autenticacao quando usuario nao existe")
        void deveContinuarSemAutenticacaoQuandoUsuarioNaoExiste() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer token_valido");
            when(tokenBlacklistService.isBlacklisted("token_valido")).thenReturn(false);
            when(jwtService.isTokenValid("token_valido")).thenReturn(true);
            when(jwtService.extractLogin("token_valido")).thenReturn("usuario_inexistente");
            when(usuarioRepository.findByLogin("usuario_inexistente")).thenReturn(Optional.empty());

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Nao deve sobrescrever autenticacao existente")
        void naoDeveSobrescreverAutenticacaoExistente() throws Exception {
            // Simula autenticacao ja existente
            SecurityContextHolder.getContext().setAuthentication(
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            "outro_usuario", null, java.util.Collections.emptyList()
                    )
            );

            when(request.getHeader("Authorization")).thenReturn("Bearer token_valido");
            when(tokenBlacklistService.isBlacklisted("token_valido")).thenReturn(false);
            when(jwtService.isTokenValid("token_valido")).thenReturn(true);
            when(jwtService.extractLogin("token_valido")).thenReturn("joao.silva");

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            // Autenticacao original deve ser mantida
            assertEquals("outro_usuario", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }
    }

    @Nested
    @DisplayName("Testes de Fluxo do Filtro")
    class FluxoFiltroTests {

        @Test
        @DisplayName("Deve sempre chamar filterChain.doFilter")
        void deveSempreChamarFilterChain() throws Exception {
            when(request.getHeader("Authorization")).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("Deve extrair token corretamente do header")
        void deveExtrairTokenCorretamente() throws Exception {
            String tokenCompleto = "Bearer eyJhbGciOiJIUzI1NiJ9.payload.signature";
            when(request.getHeader("Authorization")).thenReturn(tokenCompleto);
            when(tokenBlacklistService.isBlacklisted("eyJhbGciOiJIUzI1NiJ9.payload.signature")).thenReturn(false);
            when(jwtService.isTokenValid("eyJhbGciOiJIUzI1NiJ9.payload.signature")).thenReturn(true);
            when(jwtService.extractLogin("eyJhbGciOiJIUzI1NiJ9.payload.signature")).thenReturn("joao.silva");
            when(usuarioRepository.findByLogin("joao.silva")).thenReturn(Optional.of(usuario));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(jwtService).isTokenValid("eyJhbGciOiJIUzI1NiJ9.payload.signature");
        }
    }
}
