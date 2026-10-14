package br.com.javalab.java24.pqc;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.KEM;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Java 24 - Quantum-Resistant Module-Lattice-Based Key Encapsulation Mechanism, ML-KEM (JEP 496)
 *
 * <p>Status: Definitivo. Base: a API {@link KEM} (Key Encapsulation Mechanism) do Java 21
 * (JEP 452). ML-KEM é o padrão FIPS 203 do NIST (antigo CRYSTALS-Kyber).</p>
 *
 * <p>O que é: um mecanismo de encapsulamento de chaves resistente a computadores
 * quânticos. O destinatário publica uma chave pública; o remetente "encapsula" um segredo
 * aleatório com ela (gerando o segredo + um texto encapsulado); o destinatário
 * "decapsula" com a chave privada e obtém o MESMO segredo. Parâmetros: ML-KEM-512,
 * ML-KEM-768 (padrão) e ML-KEM-1024.</p>
 * <p>Benefício: proteção contra "colha agora, decifre depois" - tráfego capturado hoje
 * não poderá ser decifrado por um futuro computador quântico (que quebraria RSA e ECDH).</p>
 * <p>Antes: troca de chaves com RSA ou (EC)DH, vulneráveis ao algoritmo de Shor.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/pqc/MlKemExample.java
 * </pre>
 */
public class MlKemExample {

    public static void main(String[] args) throws Exception {
        HexFormat hex = HexFormat.of();

        System.out.println("=== Destinatário gera o par de chaves ML-KEM-768 ===");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("ML-KEM");
        generator.initialize(new java.security.spec.NamedParameterSpec("ML-KEM-768"));
        KeyPair receiverKeys = generator.generateKeyPair();
        System.out.println("Chave pública: " + receiverKeys.getPublic().getEncoded().length + " bytes ("
                + receiverKeys.getPublic().getAlgorithm() + ")");

        System.out.println();
        System.out.println("=== Remetente encapsula um segredo com a chave pública ===");
        KEM kem = KEM.getInstance("ML-KEM");
        KEM.Encapsulator encapsulator = kem.newEncapsulator(receiverKeys.getPublic());
        // bytes 0..32 do segredo, já como chave "AES" (encapsulate() sem args devolve algoritmo "Generic")
        KEM.Encapsulated encapsulated = encapsulator.encapsulate(0, 32, "AES");
        SecretKey senderSecret = encapsulated.key();
        byte[] toSend = encapsulated.encapsulation();
        System.out.println("Encapsulamento enviado: " + toSend.length + " bytes");
        System.out.println("Segredo do remetente:     " + hex.formatHex(senderSecret.getEncoded()));

        System.out.println();
        System.out.println("=== Destinatário decapsula com a chave privada ===");
        KEM.Decapsulator decapsulator = kem.newDecapsulator(receiverKeys.getPrivate());
        SecretKey receiverSecret = decapsulator.decapsulate(toSend, 0, 32, "AES");
        System.out.println("Segredo do destinatário:  " + hex.formatHex(receiverSecret.getEncoded()));
        System.out.println("Segredos iguais? " + Arrays.equals(senderSecret.getEncoded(), receiverSecret.getEncoded()));

        System.out.println();
        System.out.println("=== Usando o segredo compartilhado como chave AES-GCM ===");
        byte[] iv = new byte[12];
        new java.security.SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, senderSecret, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal("Pedido #42: 3 itens".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, receiverSecret, new GCMParameterSpec(128, iv));
        System.out.println("Mensagem decifrada: " + new String(cipher.doFinal(encrypted), java.nio.charset.StandardCharsets.UTF_8));
    }
}
