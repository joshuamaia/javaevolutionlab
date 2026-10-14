package br.com.javalab.java25.kdf;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.KDF;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.HKDFParameterSpec;

/**
 * Java 25 - Key Derivation Function API (JEP 510)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 24 (JEP 478), sem mudanças de API.</p>
 *
 * <p>O que é: {@link KDF} com HKDF (RFC 5869). O HKDF tem duas etapas: <b>extract</b>
 * (concentra a entropia do material de entrada + sal numa chave pseudoaleatória, PRK) e
 * <b>expand</b> (deriva da PRK quantas chaves forem necessárias, cada uma com um "info"
 * de contexto).</p>
 * <p>Foco (o exemplo do Java 24 fez extract+expand num passo): as etapas SEPARADAS -
 * extrair uma PRK uma vez e expandir várias chaves de sessão - e usar a chave derivada
 * para cifrar/decifrar com AES-GCM.</p>
 * <p>Benefício: KDF padronizada na JCA, plugável por provedores, base para HPKE/TLS 1.3 e
 * para combinar com ML-KEM (pós-quântico).</p>
 * <p>Antes: HMAC manual ({@code Mac.getInstance("HmacSHA256")}) seguindo a RFC passo a passo.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/kdf/KeyDerivationExample.java
 * </pre>
 */
public class KeyDerivationExample {

    public static void main(String[] args) throws Exception {
        HexFormat hex = HexFormat.of();
        byte[] masterSecret = new byte[32];
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(masterSecret);
        random.nextBytes(salt);

        KDF hkdf = KDF.getInstance("HKDF-SHA256");

        System.out.println("=== Etapa 1: extract (material + sal -> PRK) ===");
        SecretKey prk = hkdf.deriveKey("Generic",
                HKDFParameterSpec.ofExtract().addIKM(masterSecret).addSalt(salt).extractOnly());
        System.out.println("PRK: " + hex.formatHex(prk.getEncoded()));

        System.out.println();
        System.out.println("=== Etapa 2: expand (PRK + info -> chaves por sessão) ===");
        SecretKey session1 = hkdf.deriveKey("AES",
                HKDFParameterSpec.expandOnly(prk, info("sessao-1"), 32));
        SecretKey session2 = hkdf.deriveKey("AES",
                HKDFParameterSpec.expandOnly(prk, info("sessao-2"), 32));
        System.out.println("Sessão 1 (AES-256): " + hex.formatHex(session1.getEncoded()));
        System.out.println("Sessão 2 (AES-256): " + hex.formatHex(session2.getEncoded()));

        System.out.println();
        System.out.println("=== Usando a chave derivada em AES-GCM ===");
        byte[] iv = new byte[12];
        random.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, session1, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal("Cartão final 4242 - pedido #9001".getBytes(StandardCharsets.UTF_8));
        System.out.println("Cifrado: " + hex.formatHex(encrypted));

        // O outro lado, com o mesmo segredo e sal, re-deriva a mesma chave e decifra
        SecretKey sameSession = hkdf.deriveKey("AES", HKDFParameterSpec.ofExtract()
                .addIKM(masterSecret).addSalt(salt).thenExpand(info("sessao-1"), 32));
        cipher.init(Cipher.DECRYPT_MODE, sameSession, new GCMParameterSpec(128, iv));
        System.out.println("Decifrado com chave re-derivada: " + new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8));

        try {
            cipher.init(Cipher.DECRYPT_MODE, session2, new GCMParameterSpec(128, iv));
            cipher.doFinal(encrypted);
        } catch (javax.crypto.AEADBadTagException e) {
            System.out.println("Chave da sessão 2 não decifra: AEADBadTagException");
        }
    }

    static byte[] info(String context) {
        return ("app-pedidos/" + context).getBytes(StandardCharsets.UTF_8);
    }
}
