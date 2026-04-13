package com.capgemini.biblioapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Servico de criptografia responsavel por:
 * - Criptografia AES-256-GCM para dados em transito (login/senha)
 * - Hash BCrypt para armazenamento seguro de senhas
 * 
 * Seguranca:
 * - AES-GCM fornece confidencialidade e autenticidade
 * - IV (Initialization Vector) aleatorio de 12 bytes para cada operacao
 * - BCrypt com fator de custo 12 para resistencia a ataques de forca bruta
 */
@Service
public class CryptoService {

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;  // Tamanho recomendado para GCM
    private static final int GCM_TAG_LENGTH = 128; // Tag de autenticacao de 128 bits

    private final BCryptPasswordEncoder passwordEncoder;
    private final SecretKeySpec secretKey;
    private RsaCryptoService rsaCryptoService;

    /**
     * Inicializa o servico com a chave secreta para AES e o encoder BCrypt.
     * A chave e ajustada para 32 bytes (256 bits) se necessario.
     */
    public CryptoService(@Value("${app.crypto.secret-key:BiblioApiSecretKey32BytesLong!!}") String secretKeyString) {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Fator de custo 12

        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Injeta o RsaCryptoService para suporte a criptografia hibrida.
     * Usa setter injection para evitar dependencia circular.
     */
    @org.springframework.beans.factory.annotation.Autowired
    public void setRsaCryptoService(RsaCryptoService rsaCryptoService) {
        this.rsaCryptoService = rsaCryptoService;
    }

    /**
     * Descriptografa dados automaticamente detectando o formato.
     * Suporta:
     * - Formato hibrido RSA+AES: "encryptedKey.encryptedData"
     * - Formato AES simples: Base64(IV + CipherText + AuthTag)
     * 
     * @param encryptedData Dados criptografados
     * @return Texto original descriptografado
     */
    public String decrypt(String encryptedData) {
        if (rsaCryptoService != null && rsaCryptoService.isHybridFormat(encryptedData)) {
            return rsaCryptoService.decryptHybrid(encryptedData);
        }
        return decryptAES(encryptedData);
    }

    /**
     * Descriptografa dados recebidos em Base64 usando AES-GCM.
     * O formato esperado e: Base64(IV + CipherText + AuthTag)
     * 
     * @param encryptedData Dados criptografados em Base64
     * @return Texto original descriptografado
     * @throws RuntimeException se a descriptografia falhar
     */
    public String decryptAES(String encryptedData) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedData);

            // Extrai IV (primeiros 12 bytes) e texto cifrado (restante)
            byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(decoded, GCM_IV_LENGTH, decoded.length);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decrypted = cipher.doFinal(cipherText);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar dados", e);
        }
    }

    /**
     * Criptografa texto usando AES-GCM com IV aleatorio.
     * Retorna: Base64(IV + CipherText + AuthTag)
     * 
     * @param plainText Texto a ser criptografado
     * @return Dados criptografados em Base64
     */
    public String encryptAES(String plainText) {
        try {
            // Gera IV aleatorio para cada operacao (essencial para seguranca)
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Concatena IV + CipherText para transmissao
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar dados", e);
        }
    }

    /**
     * Gera hash BCrypt da senha para armazenamento seguro.
     * Cada hash inclui um salt unico gerado automaticamente.
     */
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verifica se a senha em texto puro corresponde ao hash armazenado.
     * Usa comparacao em tempo constante para prevenir timing attacks.
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
