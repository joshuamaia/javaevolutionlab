package br.com.javalab.java24.pqc;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.NamedParameterSpec;

/**
 * Java 24 - Quantum-Resistant Module-Lattice-Based Digital Signature Algorithm, ML-DSA (JEP 497)
 *
 * <p>Status: Definitivo. ML-DSA é o padrão FIPS 204 do NIST (antigo CRYSTALS-Dilithium).</p>
 *
 * <p>O que é: algoritmo de ASSINATURA digital resistente a computadores quânticos,
 * disponível pelas APIs de sempre: {@link KeyPairGenerator}, {@link Signature} e
 * {@code KeyFactory}. Parâmetros: ML-DSA-44, ML-DSA-65 (padrão) e ML-DSA-87.</p>
 * <p>Benefício: autenticar documentos, código e certificados de forma que um futuro
 * computador quântico não consiga forjar assinaturas (como faria com RSA/ECDSA).</p>
 * <p>Antes: só RSA, DSA, ECDSA e EdDSA - todos vulneráveis ao algoritmo de Shor.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/pqc/MlDsaExample.java
 * </pre>
 */
public class MlDsaExample {

    public static void main(String[] args) throws Exception {
        byte[] invoice = "NF-e 1234 | Cliente: Loja Central | Total: R$ 1.250,00".getBytes(StandardCharsets.UTF_8);

        System.out.println("=== Gerando par de chaves ML-DSA-65 ===");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("ML-DSA");
        generator.initialize(new NamedParameterSpec("ML-DSA-65"));
        KeyPair keys = generator.generateKeyPair();
        System.out.println("Chave pública: " + keys.getPublic().getEncoded().length + " bytes");

        System.out.println();
        System.out.println("=== Assinando a nota fiscal ===");
        Signature signer = Signature.getInstance("ML-DSA");
        signer.initSign(keys.getPrivate());
        signer.update(invoice);
        byte[] signature = signer.sign();
        System.out.println("Assinatura: " + signature.length + " bytes (bem maior que ECDSA, ~70 bytes)");

        System.out.println();
        System.out.println("=== Verificando ===");
        Signature verifier = Signature.getInstance("ML-DSA");
        verifier.initVerify(keys.getPublic());
        verifier.update(invoice);
        System.out.println("Documento original válido? " + verifier.verify(signature));

        byte[] tampered = "NF-e 1234 | Cliente: Loja Central | Total: R$ 9.250,00".getBytes(StandardCharsets.UTF_8);
        verifier.initVerify(keys.getPublic());
        verifier.update(tampered);
        System.out.println("Documento adulterado válido? " + verifier.verify(signature));
    }
}
