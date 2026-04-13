package com.capgemini.biblioapi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracao de seguranca da aplicacao.
 * 
 * Caracteristicas:
 * - API Stateless (sem sessao) - ideal para JWT
 * - CSRF desabilitado (nao necessario para APIs REST stateless)
 * - Autenticacao via JWT no header Authorization
 * 
 * Endpoints publicos:
 * - /api/auth/** (login, registro)
 * - /api/crypto/** (criptografia auxiliar)
 * - GET /api/livros/** (consulta de livros)
 * - Swagger UI e H2 Console
 * 
 * Endpoints protegidos (requerem JWT):
 * - /api/usuarios/**
 * - /api/emprestimos/**
 * - POST/DELETE /api/livros/**
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura a cadeia de filtros de seguranca.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (nao necessario para API REST stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Configura sessao como stateless (sem cookies de sessao)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publicos - nao requerem autenticacao
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/crypto/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Arquivos estaticos (login.html, etc)
                        .requestMatchers("/*.html", "/css/**", "/js/**", "/images/**").permitAll()
                        // Leitura de livros e disponibilidade - publico
                        .requestMatchers(HttpMethod.GET, "/api/livros/**").permitAll()
                        // Demais endpoints requerem autenticacao JWT
                        .anyRequest().authenticated()
                )
                // Adiciona filtro JWT antes do filtro padrao de autenticacao
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Permite frames do mesmo origem (necessario para H2 Console)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}
