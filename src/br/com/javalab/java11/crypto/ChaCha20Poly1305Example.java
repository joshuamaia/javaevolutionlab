package br.com.javalab.java11.crypto;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Java 11 - Algoritmos criptográficos ChaCha20 e ChaCha20-Poly1305 (JEP 329)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: implementação da cifra de fluxo ChaCha20 e do modo AEAD ChaCha20-Poly1305
 * (RFC 7539/8439) no provedor SunJCE. AEAD = cifra + autenticação: qualquer alteração no
 * texto cifrado ou nos dados associados é detectada na decifragem.</p>
 * <p>Benefício: alternativa rápida ao AES-GCM em hardware sem instruções AES (celulares,
 * IoT) e usada no TLS 1.3.</p>
 * <p>Antes: apenas via bibliotecas externas (Bouncy Castle).</p>
 *
 * <p>Regra importante: nunca reutilize o mesmo nonce (12 bytes) com a mesma chave.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/crypto/ChaCha20Poly1305Example.java
 * </pre>
 */
public class ChaCha20Poly1305Example {

    public static void main(String[] args) throws GeneralSecurityException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("ChaCha20");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();

        byte[] nonce = new byte[12];
        new SecureRandom().nextBytes(nonce);
        byte[] associatedData = "pedido-42".getBytes(StandardCharsets.UTF_8); // autenticado, não cifrado
        String cardData = "Cartão 5555-4444-3333-1111 validade 12/30";

        System.out.println("=== Cifrando ===");
        Cipher encryptor = Cipher.getInstance("ChaCha20-Poly1305");
        encryptor.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(nonce));
        encryptor.updateAAD(associatedData);
        byte[] cipherText = encryptor.doFinal(cardData.getBytes(StandardCharsets.UTF_8));
        System.out.println("Texto claro : " + cardData);
        System.out.println("Cifrado     : " + Base64.getEncoder().encodeToString(cipherText));
        System.out.println("Tamanho     : " + cardData.getBytes(StandardCharsets.UTF_8).length
                + " bytes -> " + cipherText.length + " bytes (+16 da tag Poly1305)");

        System.out.println();
        System.out.println("=== Decifrando ===");
        System.out.println("Resultado   : " + decrypt(key, nonce, associatedData, cipherText));

        System.out.println();
        System.out.println("=== Detecção de adulteração ===");
        byte[] tampered = cipherText.clone();
        tampered[0] ^= 1;
        try {
            decrypt(key, nonce, associatedData, tampered);
        } catch (AEADBadTagException e) {
            System.out.println("Texto cifrado alterado -> AEADBadTagException");
        }
        try {
            decrypt(key, nonce, "pedido-43".getBytes(StandardCharsets.UTF_8), cipherText);
        } catch (AEADBadTagException e) {
            System.out.println("Dados associados alterados -> AEADBadTagException");
        }
    }

    private static String decrypt(SecretKey key, byte[] nonce, byte[] associatedData, byte[] cipherText)
            throws GeneralSecurityException {
        Cipher decryptor = Cipher.getInstance("ChaCha20-Poly1305");
        decryptor.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(nonce));
        decryptor.updateAAD(associatedData);
        return new String(decryptor.doFinal(cipherText), StandardCharsets.UTF_8);
    }
}
