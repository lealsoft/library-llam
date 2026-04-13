package com.capgemini.biblioapi.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Servico de Blacklist de tokens JWT.
 * 
 * Permite invalidar tokens JWT antes da expiracao natural.
 * Utiliza cache Caffeine em memoria com TTL igual ao tempo de expiracao do JWT.
 * 
 * Casos de uso:
 * - Logout imediato (invalida access token atual)
 * - Logout de todas as sessoes (invalida todos os tokens do usuario)
 * - Revogacao de token comprometido
 * 
 * Nota: Em producao com multiplas instancias, usar Redis.
 */
@Service
public class TokenBlacklistService {

    @Value("${jwt.expiration:900000}")
    private long jwtExpiration;

    private Cache<String, Boolean> blacklistedTokens;

    /**
     * Inicializa o cache com TTL igual ao tempo de expiracao do JWT.
     * Tokens expirados sao removidos automaticamente do cache.
     */
    @PostConstruct
    public void init() {
        blacklistedTokens = Caffeine.newBuilder()
                .expireAfterWrite(jwtExpiration, TimeUnit.MILLISECONDS)
                .maximumSize(10000)
                .build();
    }

    /**
     * Adiciona um token a blacklist.
     * O token sera automaticamente removido apos expirar.
     * 
     * @param token Token JWT a ser invalidado
     */
    public void blacklistToken(String token) {
        if (token != null && !token.isBlank()) {
            blacklistedTokens.put(token, true);
        }
    }

    /**
     * Verifica se um token esta na blacklist.
     * 
     * @param token Token JWT a verificar
     * @return true se o token esta na blacklist (invalido)
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return blacklistedTokens.getIfPresent(token) != null;
    }

    /**
     * Remove um token da blacklist (raramente usado).
     * 
     * @param token Token JWT a remover da blacklist
     */
    public void removeFromBlacklist(String token) {
        if (token != null) {
            blacklistedTokens.invalidate(token);
        }
    }

    /**
     * Retorna quantidade de tokens na blacklist (para monitoramento).
     */
    public long getBlacklistSize() {
        return blacklistedTokens.estimatedSize();
    }

    /**
     * Limpa toda a blacklist (usar com cuidado).
     */
    public void clearBlacklist() {
        blacklistedTokens.invalidateAll();
    }
}
