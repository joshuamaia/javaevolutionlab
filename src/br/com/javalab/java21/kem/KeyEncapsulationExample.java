package br.com.javalab.java21.kem;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.KEM;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Java 21 - Key Encapsulation Mechanism API (JEP 452)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: a classe {@link KEM}, API padrão para mecanismos de encapsulamento de chave. O
 * remetente usa a chave PÚBLICA do destinatário para gerar uma chave simétrica secreta e um
 * "encapsulamento" (bytes enviados pela rede); o destinatário, com a chave PRIVADA, recupera a
 * mesma chave simétrica a partir do encapsulamento. O JDK 21 traz o DHKEM (RFC 9180) com X25519,
 * X448 e curvas EC. (O JDK 24 adicionou ML-KEM, resistente a computação quântica - JEP 496.)</p>
 * <p>Benefício: base para HPKE, TLS 1.3 híbrido e criptografia pós-quântica, sem misturar
 * manualmente Diffie-Hellman + KDF.</p>
 * <p>Antes: {@code KeyAgreement} + derivação de chave feita à mão, ou cifrar uma chave
 * aleatória com RSA ("key wrapping").</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/kem/KeyEncapsulationExample.java
 * </pre>
 */
public class KeyEncapsulationExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Destinatário (loja) gera seu par de chaves X25519 ===");
        KeyPair storeKeys = KeyPairGenerator.getInstance("X25519").generateKeyPair();
        System.out.println("Chave pública: " + storeKeys.getPublic().getAlgorithm()
                + " (" + storeKeys.getPublic().getEncoded().length + " bytes codificados)");

        System.out.println();
        System.out.println("=== Remetente (cliente) encapsula uma chave AES de 256 bits ===");
        KEM kem = KEM.getInstance("DHKEM");
        KEM.Encapsulator encapsulator = kem.newEncapsulator(storeKeys.getPublic());
        KEM.Encapsulated encapsulated = encapsulator.encapsulate(0, 32, "AES");
        SecretKey senderKey = encapsulated.key();
        byte[] encapsulation = encapsulated.encapsulation(); // vai pela rede junto com a mensagem
        System.out.println("Encapsulamento (" + encapsulation.length + " bytes): "
                + HexFormat.of().formatHex(encapsulation, 0, 16) + "...");

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        Cipher encrypt = Cipher.getInstance("AES/GCM/NoPadding");
        encrypt.init(Cipher.ENCRYPT_MODE, senderKey, new GCMParameterSpec(128, iv));
        byte[] ciphertext = encrypt.doFinal("Cartão final 4242, pedido PED-21".getBytes(StandardCharsets.UTF_8));
        System.out.println("Mensagem cifrada com AES-GCM: " + ciphertext.length + " bytes");

        System.out.println();
        System.out.println("=== Destinatário decapsula com a chave privada ===");
        KEM.Decapsulator decapsulator = kem.newDecapsulator(storeKeys.getPrivate());
        SecretKey receiverKey = decapsulator.decapsulate(encapsulation, 0, 32, "AES");
        System.out.println("Mesma chave simétrica dos dois lados? "
                + Arrays.equals(senderKey.getEncoded(), receiverKey.getEncoded()));

        Cipher decrypt = Cipher.getInstance("AES/GCM/NoPadding");
        decrypt.init(Cipher.DECRYPT_MODE, receiverKey, new GCMParameterSpec(128, iv));
        System.out.println("Mensagem decifrada: " + new String(decrypt.doFinal(ciphertext), StandardCharsets.UTF_8));
    }
}
