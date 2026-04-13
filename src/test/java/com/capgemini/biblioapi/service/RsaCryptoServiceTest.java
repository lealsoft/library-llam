package com.capgemini.biblioapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do RsaCryptoService")
class RsaCryptoServiceTest {

    private RsaCryptoService rsaCryptoService;

    @BeforeEach
    void setUp() {
        rsaCryptoService = new RsaCryptoService();
        rsaCryptoService.init();
    }

    @Test
    @DisplayName("Deve gerar chave publica valida")
    void deveGerarChavePublicaValida() {
        String publicKeyBase64 = rsaCryptoService.getPublicKeyBase64();
        
        assertNotNull(publicKeyBase64);
        assertFalse(publicKeyBase64.isEmpty());
        
        // Verifica se e Base64 valido
        assertDoesNotThrow(() -> Base64.getDecoder().decode(publicKeyBase64));
    }

    @Test
    @DisplayName("Deve descriptografar dados hibridos corretamente")
    void deveDescriptografarDadosHibridosCorretamente() throws Exception {
        String textoOriginal = "Senha@123";
        
        // Simula o que o frontend faz
        String dadosCriptografados = encryptHybridLikeJavaScript(textoOriginal);
        
        // Descriptografa
        String textoDescriptografado = rsaCryptoService.decryptHybrid(dadosCriptografados);
        
        assertEquals(textoOriginal, textoDescriptografado);
    }

    @Test
    @DisplayName("Deve identificar formato hibrido corretamente")
    void deveIdentificarFormatoHibrido() {
        assertTrue(rsaCryptoService.isHybridFormat("abc.def"));
        assertTrue(rsaCryptoService.isHybridFormat("chaveAES.dadosCriptografados"));
        
        assertFalse(rsaCryptoService.isHybridFormat("semPonto"));
        assertFalse(rsaCryptoService.isHybridFormat(null));
        assertFalse(rsaCryptoService.isHybridFormat(""));
        assertFalse(rsaCryptoService.isHybridFormat("a.b.c")); // mais de um ponto
    }

    @Test
    @DisplayName("Deve falhar com formato invalido")
    void deveFalharComFormatoInvalido() {
        assertThrows(RuntimeException.class, () -> 
            rsaCryptoService.decryptHybrid("dadosInvalidos")
        );
    }

    /**
     * Simula a criptografia hibrida que o JavaScript faz no frontend.
     */
    private String encryptHybridLikeJavaScript(String plainText) throws Exception {
        // 1. Obtem chave publica RSA
        String publicKeyBase64 = rsaCryptoService.getPublicKeyBase64();
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // 2. Gera chave AES aleatoria de 256 bits
        byte[] aesKeyBytes = new byte[32];
        new SecureRandom().nextBytes(aesKeyBytes);
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // 3. Criptografa dados com AES-GCM
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
        byte[] encryptedData = aesCipher.doFinal(plainText.getBytes());

        // Concatena IV + CipherText
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        String encryptedDataBase64 = Base64.getEncoder().encodeToString(combined);

        // 4. Criptografa chave AES com RSA (mesmo formato do Web Crypto API)
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,  // Web Crypto usa SHA-256 para MGF1 tambem
                PSource.PSpecified.DEFAULT
        );
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
        byte[] encryptedAesKey = rsaCipher.doFinal(aesKeyBytes);
        String encryptedAesKeyBase64 = Base64.getEncoder().encodeToString(encryptedAesKey);

        // Formato hibrido: encryptedKey.encryptedData
        return encryptedAesKeyBase64 + "." + encryptedDataBase64;
    }
}
