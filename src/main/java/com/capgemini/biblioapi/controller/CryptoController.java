package com.capgemini.biblioapi.controller;

import com.capgemini.biblioapi.service.CryptoService;
import com.capgemini.biblioapi.service.RsaCryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller auxiliar para criptografia - APENAS AMBIENTE DE DESENVOLVIMENTO.
 * 
 * Este endpoint permite criptografar textos para uso nos testes manuais.
 * NAO DEVE EXISTIR EM PRODUCAO pois expoe a capacidade de criptografia.
 * 
 * Em producao, o frontend deve fazer a criptografia AES no cliente.
 */
@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
@Profile("dev")
public class CryptoController {

    private final CryptoService cryptoService;
    private final RsaCryptoService rsaCryptoService;

    /**
     * Retorna a chave publica RSA para criptografia hibrida.
     * Esta chave pode ser compartilhada publicamente.
     */
    @GetMapping("/public-key")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        return ResponseEntity.ok(Map.of(
                "publicKey", rsaCryptoService.getPublicKeyBase64(),
                "algorithm", "RSA-OAEP-256",
                "format", "spki"
        ));
    }

    /**
     * Criptografa um texto usando AES-256-GCM.
     * Util para testes manuais com CURL ou Postman.
     */
    @PostMapping("/encrypt")
    public ResponseEntity<Map<String, String>> encrypt(@RequestBody Map<String, String> request) {
        String plainText = request.get("texto");
        if (plainText == null || plainText.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Campo 'texto' e obrigatorio"));
        }
        String encrypted = cryptoService.encryptAES(plainText);
        return ResponseEntity.ok(Map.of("criptografado", encrypted));
    }
}
