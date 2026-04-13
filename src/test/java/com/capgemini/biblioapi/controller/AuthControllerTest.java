package com.capgemini.biblioapi.controller;

import com.capgemini.biblioapi.config.JwtAuthenticationFilter;
import com.capgemini.biblioapi.dto.AuthResponseDTO;
import com.capgemini.biblioapi.dto.RefreshTokenRequestDTO;
import com.capgemini.biblioapi.dto.UsuarioAuthDTO;
import com.capgemini.biblioapi.dto.UsuarioCreateDTO;
import com.capgemini.biblioapi.dto.UsuarioResponseDTO;
import com.capgemini.biblioapi.entity.RefreshToken;
import com.capgemini.biblioapi.entity.Usuario;
import com.capgemini.biblioapi.exception.BusinessException;
import com.capgemini.biblioapi.repository.UsuarioRepository;
import com.capgemini.biblioapi.service.CryptoService;
import com.capgemini.biblioapi.service.JwtService;
import com.capgemini.biblioapi.service.RateLimiterService;
import com.capgemini.biblioapi.service.RefreshTokenService;
import com.capgemini.biblioapi.service.TokenBlacklistService;
import com.capgemini.biblioapi.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integracao para AuthController.
 * Testa endpoints de login, registro, refresh e logout.
 */
@WebMvcTest(value = AuthController.class, 
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
                classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private CryptoService cryptoService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private RateLimiterService rateLimiterService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    private Usuario usuario;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Joao Silva")
                .login("joao.silva")
                .senhaHash("$2a$12$hashedpassword")
                .dataCriacao(LocalDateTime.now())
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token("refresh-token-uuid")
                .usuario(usuario)
                .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .revoked(false)
                .build();

        // Mock do rate limiter - permite todas as requisicoes nos testes
        when(rateLimiterService.tryConsumeLogin(anyString())).thenReturn(true);
    }

    @Nested
    @DisplayName("Testes de Login")
    class LoginTests {

        @Test
        @DisplayName("Deve realizar login com sucesso")
        void deveRealizarLoginComSucesso() throws Exception {
            UsuarioAuthDTO authDTO = new UsuarioAuthDTO();
            authDTO.setLoginCriptografado("login_criptografado");
            authDTO.setSenhaCriptografada("senha_criptografada");

            when(cryptoService.decrypt("login_criptografado")).thenReturn("joao.silva");
            when(cryptoService.decrypt("senha_criptografada")).thenReturn("Senha@123");
            when(usuarioRepository.findByLogin("joao.silva")).thenReturn(Optional.of(usuario));
            when(cryptoService.verifyPassword("Senha@123", usuario.getSenhaHash())).thenReturn(true);
            when(jwtService.generateToken(usuario)).thenReturn("jwt_token");
            when(refreshTokenService.createRefreshToken(usuario)).thenReturn(refreshToken);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("jwt_token"))
                    .andExpect(jsonPath("$.refreshToken").value(refreshToken.getToken()))
                    .andExpect(jsonPath("$.tipo").value("Bearer"))
                    .andExpect(jsonPath("$.usuario.login").value("joao.silva"));
        }

        @Test
        @DisplayName("Deve retornar 401 para credenciais invalidas")
        void deveRetornar401ParaCredenciaisInvalidas() throws Exception {
            UsuarioAuthDTO authDTO = new UsuarioAuthDTO();
            authDTO.setLoginCriptografado("login_criptografado");
            authDTO.setSenhaCriptografada("senha_criptografada");

            when(cryptoService.decrypt("login_criptografado")).thenReturn("joao.silva");
            when(cryptoService.decrypt("senha_criptografada")).thenReturn("senha_errada");
            when(usuarioRepository.findByLogin("joao.silva")).thenReturn(Optional.of(usuario));
            when(cryptoService.verifyPassword("senha_errada", usuario.getSenhaHash())).thenReturn(false);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authDTO)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 401 para usuario inexistente")
        void deveRetornar401ParaUsuarioInexistente() throws Exception {
            UsuarioAuthDTO authDTO = new UsuarioAuthDTO();
            authDTO.setLoginCriptografado("login_criptografado");
            authDTO.setSenhaCriptografada("senha_criptografada");

            when(cryptoService.decrypt("login_criptografado")).thenReturn("usuario_inexistente");
            when(cryptoService.decrypt("senha_criptografada")).thenReturn("Senha@123");
            when(usuarioRepository.findByLogin("usuario_inexistente")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authDTO)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 401 para erro de descriptografia")
        void deveRetornar401ParaErroDescriptografia() throws Exception {
            UsuarioAuthDTO authDTO = new UsuarioAuthDTO();
            authDTO.setLoginCriptografado("dados_invalidos");
            authDTO.setSenhaCriptografada("dados_invalidos");

            when(cryptoService.decrypt(anyString())).thenThrow(new RuntimeException("Erro de descriptografia"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authDTO)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Testes de Registro")
    class RegistroTests {

        @Test
        @DisplayName("Deve registrar usuario com sucesso")
        void deveRegistrarUsuarioComSucesso() throws Exception {
            UsuarioCreateDTO createDTO = new UsuarioCreateDTO();
            createDTO.setNome("Novo Usuario");
            createDTO.setLogin("novo.usuario");
            createDTO.setSenhaCriptografada("senha_criptografada");

            UsuarioResponseDTO usuarioResponse = UsuarioResponseDTO.builder()
                    .id(2L)
                    .nome("Novo Usuario")
                    .login("novo.usuario")
                    .dataCriacao(LocalDateTime.now())
                    .build();

            Usuario novoUsuario = Usuario.builder()
                    .id(2L)
                    .nome("Novo Usuario")
                    .login("novo.usuario")
                    .senhaHash("$2a$12$hash")
                    .dataCriacao(LocalDateTime.now())
                    .build();

            RefreshToken novoRefreshToken = RefreshToken.builder()
                    .token("novo-refresh-token")
                    .usuario(novoUsuario)
                    .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                    .revoked(false)
                    .build();

            when(usuarioService.criar(any(UsuarioCreateDTO.class))).thenReturn(usuarioResponse);
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(novoUsuario));
            when(jwtService.generateToken(novoUsuario)).thenReturn("novo_jwt_token");
            when(refreshTokenService.createRefreshToken(novoUsuario)).thenReturn(novoRefreshToken);

            mockMvc.perform(post("/api/auth/registrar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken").value("novo_jwt_token"))
                    .andExpect(jsonPath("$.refreshToken").value("novo-refresh-token"))
                    .andExpect(jsonPath("$.usuario.login").value("novo.usuario"));
        }
    }

    @Nested
    @DisplayName("Testes de Refresh Token")
    class RefreshTokenTests {

        @Test
        @DisplayName("Deve renovar access token com sucesso")
        void deveRenovarAccessTokenComSucesso() throws Exception {
            RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO();
            requestDTO.setRefreshToken(refreshToken.getToken());

            RefreshToken novoRefreshToken = RefreshToken.builder()
                    .token("novo-refresh-token")
                    .usuario(usuario)
                    .expiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                    .revoked(false)
                    .build();

            when(refreshTokenService.validateRefreshToken(refreshToken.getToken())).thenReturn(refreshToken);
            when(refreshTokenService.rotateRefreshToken(refreshToken)).thenReturn(novoRefreshToken);
            when(jwtService.generateToken(usuario)).thenReturn("novo_access_token");

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("novo_access_token"))
                    .andExpect(jsonPath("$.refreshToken").value("novo-refresh-token"));
        }

        @Test
        @DisplayName("Deve retornar 401 para refresh token invalido")
        void deveRetornar401ParaRefreshTokenInvalido() throws Exception {
            RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO();
            requestDTO.setRefreshToken("token-invalido");

            when(refreshTokenService.validateRefreshToken("token-invalido"))
                    .thenThrow(new BusinessException("Refresh token invalido", HttpStatus.UNAUTHORIZED));

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 401 para refresh token expirado")
        void deveRetornar401ParaRefreshTokenExpirado() throws Exception {
            RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO();
            requestDTO.setRefreshToken("token-expirado");

            when(refreshTokenService.validateRefreshToken("token-expirado"))
                    .thenThrow(new BusinessException("Refresh token expirado", HttpStatus.UNAUTHORIZED));

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Testes de Logout")
    class LogoutTests {

        @Test
        @DisplayName("Deve realizar logout com sucesso")
        void deveRealizarLogoutComSucesso() throws Exception {
            RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO();
            requestDTO.setRefreshToken(refreshToken.getToken());

            doNothing().when(refreshTokenService).revokeToken(refreshToken.getToken());

            mockMvc.perform(post("/api/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNoContent());

            verify(refreshTokenService).revokeToken(refreshToken.getToken());
        }

        @Test
        @DisplayName("Deve retornar erro para logout com token inexistente")
        void deveRetornarErroParaLogoutComTokenInexistente() throws Exception {
            RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO();
            requestDTO.setRefreshToken("token-inexistente");

            doThrow(new BusinessException("Refresh token invalido", HttpStatus.NOT_FOUND))
                    .when(refreshTokenService).revokeToken("token-inexistente");

            mockMvc.perform(post("/api/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound());
        }
    }
}
