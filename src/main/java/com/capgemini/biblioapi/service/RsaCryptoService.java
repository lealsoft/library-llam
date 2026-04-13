package com.capgemini.biblioapi.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * Servico de criptografia hibrida RSA + AES.
 * 
 * Fluxo:
 * 1. Frontend recebe chave publica RSA
 * 2. Frontend gera chave AES aleatoria e criptografa dados
 * 3. Frontend criptografa chave AES com RSA publica
 * 4. Backend descriptografa chave AES com RSA privada
 * 5. Backend descriptografa dados com AES
 * 
 * Seguranca:
 * - RSA-2048 para troca de chaves
 * - AES-256-GCM para dados (confidencialidade + autenticidade)
 * - Chave privada nunca sai do servidor
 */
@Service
public class RsaCryptoService {

    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int RSA_KEY_SIZE = 2048;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private KeyPair keyPair;
    private String publicKeyBase64;

    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(RSA_KEY_SIZE, new SecureRandom());
            this.keyPair = keyGen.generateKeyPair();
            this.publicKeyBase64 = Base64.getEncoder().encodeToString(
                    keyPair.getPublic().getEncoded()
            );
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar par de chaves RSA", e);
        }
    }

    /**
     * Retorna a chave publica RSA em formato Base64 (SPKI/X.509).
     * Esta chave pode ser compartilhada publicamente.
     */
    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    /**
     * Descriptografa dados usando criptografia hibrida.
     * 
     * Formato esperado: Base64(encryptedAesKey) + "." + Base64(IV + CipherText + AuthTag)
     * 
     * @param hybridEncryptedData Dados criptografados no formato hibrido
     * @return Texto original descriptografado
     */
    public String decryptHybrid(String hybridEncryptedData) {
        try {
            String[] parts = hybridEncryptedData.split("\\.");
            
            if (parts.length != 2) {
                throw new IllegalArgumentException("Formato invalido. Esperado: encryptedKey.encryptedData");
            }

            String encryptedAesKeyBase64 = parts[0];
            String encryptedDataBase64 = parts[1];

            // 1. Descriptografa a chave AES com RSA privada
            // Web Crypto API usa SHA-256 para hash E para MGF1 quando importamos com hash: 'SHA-256'
            byte[] encryptedAesKey = Base64.getDecoder().decode(encryptedAesKeyBase64);
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA256,  // Web Crypto usa SHA-256 para MGF1 tambem
                    PSource.PSpecified.DEFAULT
            );
            rsaCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), oaepParams);
            byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAesKey);

            // 2. Descriptografa os dados com AES-GCM
            byte[] encryptedData = Base64.getDecoder().decode(encryptedDataBase64);
            byte[] iv = Arrays.copyOfRange(encryptedData, 0, GCM_IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(encryptedData, GCM_IV_LENGTH, encryptedData.length);

            SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
            Cipher aesCipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

            byte[] decrypted = aesCipher.doFinal(cipherText);
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar dados hibridos: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se o dado esta no formato hibrido (contem ".").
     */
    public boolean isHybridFormat(String data) {
        return data != null && data.contains(".") && data.split("\\.").length == 2;
    }
}
