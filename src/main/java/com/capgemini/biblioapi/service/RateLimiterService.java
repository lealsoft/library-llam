package com.capgemini.biblioapi.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servico de Rate Limiting para protecao contra brute force.
 * 
 * Utiliza o algoritmo Token Bucket (Bucket4j) para limitar requisicoes.
 * Cada IP tem seu proprio bucket de tokens.
 * 
 * Configuracao padrao:
 * - Login: 5 tentativas por minuto
 * - Geral: 100 requisicoes por minuto
 */
@Service
public class RateLimiterService {

    /** Cache de buckets por IP para endpoint de login */
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    
    /** Cache de buckets por IP para requisicoes gerais */
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    /**
     * Verifica se o IP pode fazer tentativa de login.
     * Limite: 5 tentativas por minuto.
     * 
     * @param ip Endereco IP do cliente
     * @return true se permitido, false se limite excedido
     */
    public boolean tryConsumeLogin(String ip) {
        Bucket bucket = loginBuckets.computeIfAbsent(ip, this::createLoginBucket);
        return bucket.tryConsume(1);
    }

    /**
     * Verifica se o IP pode fazer requisicao geral.
     * Limite: 100 requisicoes por minuto.
     * 
     * @param ip Endereco IP do cliente
     * @return true se permitido, false se limite excedido
     */
    public boolean tryConsumeGeneral(String ip) {
        Bucket bucket = generalBuckets.computeIfAbsent(ip, this::createGeneralBucket);
        return bucket.tryConsume(1);
    }

    /**
     * Retorna quantas tentativas de login restam para o IP.
     */
    public long getLoginAttemptsRemaining(String ip) {
        Bucket bucket = loginBuckets.get(ip);
        return bucket != null ? bucket.getAvailableTokens() : 5;
    }

    /**
     * Retorna informacoes de todos os IPs com rate limit de login ativo.
     */
    public Map<String, Long> getAllLoginBuckets() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        loginBuckets.forEach((ip, bucket) -> 
            result.put(ip, bucket.getAvailableTokens())
        );
        return result;
    }

    /**
     * Retorna informacoes de todos os IPs com rate limit geral ativo.
     */
    public Map<String, Long> getAllGeneralBuckets() {
        Map<String, Long> result = new ConcurrentHashMap<>();
        generalBuckets.forEach((ip, bucket) -> 
            result.put(ip, bucket.getAvailableTokens())
        );
        return result;
    }

    /**
     * Retorna quantidade de IPs monitorados.
     */
    public int getLoginBucketsCount() {
        return loginBuckets.size();
    }

    /**
     * Retorna quantidade de IPs monitorados (geral).
     */
    public int getGeneralBucketsCount() {
        return generalBuckets.size();
    }

    /**
     * Cria bucket para login: 5 tokens, recarrega 5 por minuto.
     */
    private Bucket createLoginBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Cria bucket geral: 100 tokens, recarrega 100 por minuto.
     */
    private Bucket createGeneralBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Limpa buckets antigos (pode ser chamado por scheduled task).
     */
    public void clearOldBuckets() {
        loginBuckets.clear();
        generalBuckets.clear();
    }
}
