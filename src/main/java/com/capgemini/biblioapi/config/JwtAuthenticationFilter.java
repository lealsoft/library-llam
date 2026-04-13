package com.capgemini.biblioapi.config;

import com.capgemini.biblioapi.entity.Usuario;
import com.capgemini.biblioapi.repository.UsuarioRepository;
import com.capgemini.biblioapi.service.JwtService;
import com.capgemini.biblioapi.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro de autenticacao JWT que intercepta todas as requisicoes.
 * 
 * Fluxo:
 * 1. Verifica se existe header "Authorization: Bearer <token>"
 * 2. Verifica se o token esta na blacklist (logout)
 * 3. Valida o token JWT (assinatura e expiracao)
 * 4. Extrai o login do token e busca o usuario
 * 5. Configura o contexto de seguranca do Spring
 * 
 * Requisicoes sem token ou com token invalido continuam sem autenticacao,
 * sendo bloqueadas posteriormente pelo SecurityConfig se necessario.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Busca header de autorizacao
        final String authHeader = request.getHeader("Authorization");

        // Se nao houver header ou nao for Bearer, continua sem autenticacao
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrai token removendo prefixo "Bearer "
        final String token = authHeader.substring(7);

        // Verifica se token esta na blacklist (logout)
        if (tokenBlacklistService.isBlacklisted(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Valida token (assinatura e expiracao)
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrai login do token e configura contexto de seguranca
        final String login = jwtService.extractLogin(token);

        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Usuario usuario = usuarioRepository.findByLogin(login).orElse(null);

            if (usuario != null) {
                // Cria token de autenticacao do Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        Collections.emptyList()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Define usuario autenticado no contexto
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
