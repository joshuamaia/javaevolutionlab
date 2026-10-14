package br.com.javalab.java24.kdf;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import javax.crypto.KDF;
import javax.crypto.SecretKey;
import javax.crypto.spec.HKDFParameterSpec;

/**
 * Java 24 - Key Derivation Function API, Preview (JEP 478)
 *
 * <p>Status: Preview. Evolução: definitivo no Java 25 (JEP 510) SEM mudanças na API usada
 * aqui ({@code KDF}, {@code HKDFParameterSpec}) - conferido com {@code javap} nos JDKs 24 e
 * 25. Por isso este código compila no JDK 25 sem flags (no 24 exigia {@code --enable-preview}).</p>
 *
 * <p>O que é: a classe {@link KDF} representa funções de derivação de chaves: a partir de
 * um material secreto inicial (IKM - ex.: segredo combinado num handshake), um "sal" e uma
 * informação de contexto, deriva uma ou mais chaves criptograficamente fortes. A primeira
 * implementação é o HKDF (RFC 5869), em SHA-256/384/512.</p>
 * <p>Benefício: API padrão para KDFs, base para TLS 1.3, HPKE e algoritmos pós-quânticos
 * (ex.: ML-KEM gera um segredo que costuma passar por uma KDF).</p>
 * <p>Antes: implementar HKDF à mão com {@code Mac} (HmacSHA256) ou usar Bouncy Castle.</p>
 *
 * <p>Foco: derivação em UM passo ({@code ofExtract()...thenExpand(...)}) de chaves distintas
 * para usos distintos a partir do mesmo segredo, variando o {@code info}. O exemplo do Java 25
 * mostra extract e expand separados e a chave sendo usada em AES-GCM.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/kdf/KeyDerivationPreviewExample.java
 * </pre>
 */
public class KeyDerivationPreviewExample {

    public static void main(String[] args) throws Exception {
        byte[] sharedSecret = "segredo-combinado-entre-cliente-e-servidor".getBytes(StandardCharsets.UTF_8);
        byte[] salt = HexFormat.of().parseHex("000102030405060708090a0b0c0d0e0f");

        KDF hkdf = KDF.getInstance("HKDF-SHA256");
        System.out.println("=== KDF " + hkdf.getAlgorithm() + " (provedor " + hkdf.getProviderName() + ") ===");

        // Mesmo segredo + mesmo sal, contexto (info) diferente => chaves independentes
        SecretKey encryptionKey = hkdf.deriveKey("AES", HKDFParameterSpec.ofExtract()
                .addIKM(sharedSecret).addSalt(salt)
                .thenExpand("pedidos:criptografia".getBytes(StandardCharsets.UTF_8), 32));
        SecretKey macKey = hkdf.deriveKey("HmacSHA256", HKDFParameterSpec.ofExtract()
                .addIKM(sharedSecret).addSalt(salt)
                .thenExpand("pedidos:integridade".getBytes(StandardCharsets.UTF_8), 32));

        HexFormat hex = HexFormat.of();
        System.out.println("Chave AES (" + encryptionKey.getAlgorithm() + ", " + encryptionKey.getEncoded().length * 8
                + " bits): " + hex.formatHex(encryptionKey.getEncoded()));
        System.out.println("Chave HMAC (" + macKey.getAlgorithm() + "): " + hex.formatHex(macKey.getEncoded()));

        System.out.println();
        System.out.println("=== Determinismo: mesmos parâmetros => mesmos bytes ===");
        byte[] again = hkdf.deriveData(HKDFParameterSpec.ofExtract()
                .addIKM(sharedSecret).addSalt(salt)
                .thenExpand("pedidos:criptografia".getBytes(StandardCharsets.UTF_8), 32));
        System.out.println("Igual à chave AES? " + java.util.Arrays.equals(again, encryptionKey.getEncoded()));
    }
}
