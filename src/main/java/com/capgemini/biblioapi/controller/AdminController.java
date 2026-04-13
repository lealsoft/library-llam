package com.capgemini.biblioapi.controller;

import com.capgemini.biblioapi.service.RateLimiterService;
import com.capgemini.biblioapi.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller administrativo - APENAS AMBIENTE DE DESENVOLVIMENTO.
 * 
 * Fornece endpoints para monitoramento e debug de:
 * - Blacklist de tokens JWT
 * - Rate limiting
 * 
 * NAO DEVE EXISTIR EM PRODUCAO.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Profile("dev")
public class AdminController {

    private final TokenBlacklistService tokenBlacklistService;
    private final RateLimiterService rateLimiterService;

    /**
     * Retorna informacoes sobre a blacklist de tokens.
     */
    @GetMapping("/blacklist/info")
    public ResponseEntity<Map<String, Object>> getBlacklistInfo() {
        return ResponseEntity.ok(Map.of(
                "tokensNaBlacklist", tokenBlacklistService.getBlacklistSize(),
                "descricao", "Tokens JWT invalidados antes da expiracao natural"
        ));
    }

    /**
     * Verifica se um token especifico esta na blacklist.
     */
    @GetMapping("/blacklist/check")
    public ResponseEntity<Map<String, Object>> checkTokenInBlacklist(
            @RequestParam String token) {
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);
        return ResponseEntity.ok(Map.of(
                "token", token.length() > 20 ? token.substring(0, 20) + "..." : token,
                "blacklisted", isBlacklisted
        ));
    }

    /**
     * Limpa toda a blacklist (usar com cuidado).
     */
    @DeleteMapping("/blacklist/clear")
    public ResponseEntity<Map<String, String>> clearBlacklist() {
        tokenBlacklistService.clearBlacklist();
        return ResponseEntity.ok(Map.of(
                "status", "Blacklist limpa com sucesso"
        ));
    }

    /**
     * Retorna tentativas de login restantes para um IP.
     */
    @GetMapping("/ratelimit/login/{ip}")
    public ResponseEntity<Map<String, Object>> getLoginAttemptsRemaining(
            @PathVariable String ip) {
        long remaining = rateLimiterService.getLoginAttemptsRemaining(ip);
        return ResponseEntity.ok(Map.of(
                "ip", ip,
                "tentativasRestantes", remaining,
                "limitePorMinuto", 5
        ));
    }

    /**
     * Retorna todos os IPs com rate limit de login ativo.
     */
    @GetMapping("/ratelimit/login")
    public ResponseEntity<Map<String, Object>> getAllLoginRateLimits() {
        return ResponseEntity.ok(Map.of(
                "totalIps", rateLimiterService.getLoginBucketsCount(),
                "limitePorMinuto", 5,
                "ips", rateLimiterService.getAllLoginBuckets()
        ));
    }

    /**
     * Retorna todos os IPs com rate limit geral ativo.
     */
    @GetMapping("/ratelimit/general")
    public ResponseEntity<Map<String, Object>> getAllGeneralRateLimits() {
        return ResponseEntity.ok(Map.of(
                "totalIps", rateLimiterService.getGeneralBucketsCount(),
                "limitePorMinuto", 100,
                "ips", rateLimiterService.getAllGeneralBuckets()
        ));
    }

    /**
     * Limpa todos os buckets de rate limiting.
     */
    @DeleteMapping("/ratelimit/clear")
    public ResponseEntity<Map<String, String>> clearRateLimits() {
        rateLimiterService.clearOldBuckets();
        return ResponseEntity.ok(Map.of(
                "status", "Rate limits resetados com sucesso"
        ));
    }
}
