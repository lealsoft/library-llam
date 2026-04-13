package com.capgemini.biblioapi.controller;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticacao - endpoints publicos para login, registro e refresh.
 * 
 * Fluxo de seguranca:
 * 1. Rate limiting por IP (5 tentativas/minuto para login)
 * 2. Credenciais recebidas criptografadas (AES-256-GCM)
 * 3. Descriptografia e validacao
 * 4. Geracao de access token (JWT curta duracao) + refresh token (longa duracao)
 * 5. Refresh token permite renovar o access token sem novo login
 * 6. Logout adiciona access token a blacklist para invalidacao imediata
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final CryptoService cryptoService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RateLimiterService rateLimiterService;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Realiza login e retorna token JWT.
     * Rate limited: 5 tentativas por minuto por IP.
     * 
     * @param dto Credenciais criptografadas (login e senha)
     * @param request HttpServletRequest para obter IP
     * @return Token JWT + dados do usuario
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody UsuarioAuthDTO dto,
            HttpServletRequest request) {
        
        // Rate limiting - protecao contra brute force
        String clientIp = getClientIp(request);
        if (!rateLimiterService.tryConsumeLogin(clientIp)) {
            throw new BusinessException(
                    "Muitas tentativas de login. Aguarde 1 minuto.", 
                    HttpStatus.TOO_MANY_REQUESTS);
        }

        String loginPuro;
        String senhaPura;

        // Descriptografa credenciais (suporta AES simples ou RSA+AES hibrido)
        try {
            loginPuro = cryptoService.decrypt(dto.getLoginCriptografado());
            senhaPura = cryptoService.decrypt(dto.getSenhaCriptografada());
        } catch (Exception e) {
            throw new BusinessException("Credenciais invalidas", HttpStatus.UNAUTHORIZED);
        }

        // Busca usuario e valida senha
        Usuario usuario = usuarioRepository.findByLogin(loginPuro)
                .orElseThrow(() -> new BusinessException("Credenciais invalidas", HttpStatus.UNAUTHORIZED));

        if (!cryptoService.verifyPassword(senhaPura, usuario.getSenhaHash())) {
            throw new BusinessException("Credenciais invalidas", HttpStatus.UNAUTHORIZED);
        }

        // Gera access token (JWT) e refresh token
        String accessToken = jwtService.generateToken(usuario);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);

        UsuarioResponseDTO usuarioResponse = UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .login(usuario.getLogin())
                .dataCriacao(usuario.getDataCriacao())
                .build();

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tipo("Bearer")
                .expiraEm(expiration / 1000) // Converte ms para segundos
                .usuario(usuarioResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Registra novo usuario e retorna tokens.
     * Permite acesso imediato apos registro.
     * 
     * @param dto Dados do usuario com senha criptografada
     * @return Access token + refresh token + dados do usuario criado
     */
    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody UsuarioCreateDTO dto) {
        // Cria usuario (valida senha, gera hash, etc)
        UsuarioResponseDTO usuarioResponse = usuarioService.criar(dto);

        // Busca usuario criado para gerar tokens
        Usuario usuario = usuarioRepository.findById(usuarioResponse.getId())
                .orElseThrow(() -> new BusinessException("Erro ao criar usuario", HttpStatus.INTERNAL_SERVER_ERROR));

        // Gera access token e refresh token
        String accessToken = jwtService.generateToken(usuario);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tipo("Bearer")
                .expiraEm(expiration / 1000)
                .usuario(usuarioResponse)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Renova o access token usando o refresh token.
     * O refresh token e rotacionado (novo token gerado, antigo invalidado).
     * 
     * @param dto Refresh token atual
     * @return Novo access token + novo refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        // Valida refresh token
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(dto.getRefreshToken());
        Usuario usuario = refreshToken.getUsuario();

        // Rotaciona refresh token (gera novo, invalida antigo)
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

        // Gera novo access token
        String accessToken = jwtService.generateToken(usuario);

        UsuarioResponseDTO usuarioResponse = UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .login(usuario.getLogin())
                .dataCriacao(usuario.getDataCriacao())
                .build();

        AuthResponseDTO response = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .tipo("Bearer")
                .expiraEm(expiration / 1000)
                .usuario(usuarioResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Realiza logout revogando o refresh token e invalidando o access token.
     * O access token e adicionado a blacklist para invalidacao imediata.
     * 
     * @param dto Refresh token a ser revogado
     * @param request HttpServletRequest para obter access token
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequestDTO dto,
            HttpServletRequest request) {
        
        // Revoga refresh token
        refreshTokenService.revokeToken(dto.getRefreshToken());
        
        // Adiciona access token a blacklist para invalidacao imediata
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(accessToken);
        }
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Realiza logout de todas as sessoes do usuario.
     * Revoga todos os refresh tokens e invalida o access token atual.
     * 
     * @param request HttpServletRequest para obter access token e usuario
     */
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(HttpServletRequest request) {
        // Obtem usuario do token atual
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("Token de acesso necessario", HttpStatus.UNAUTHORIZED);
        }
        
        String accessToken = authHeader.substring(7);
        
        // Valida token e extrai usuario
        if (!jwtService.isTokenValid(accessToken)) {
            throw new BusinessException("Token invalido", HttpStatus.UNAUTHORIZED);
        }
        
        Long userId = jwtService.extractUserId(accessToken);
        
        // Revoga todos os refresh tokens do usuario
        refreshTokenService.revokeAllUserTokens(userId);
        
        // Adiciona access token atual a blacklist
        tokenBlacklistService.blacklistToken(accessToken);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Extrai IP do cliente considerando proxies.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
