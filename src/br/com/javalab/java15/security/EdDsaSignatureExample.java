package br.com.javalab.java15.security;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

/**
 * Java 15 - Edwards-Curve Digital Signature Algorithm, EdDSA (JEP 339)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que e: suporte nativo (provedor SunEC) a assinaturas EdDSA conforme a RFC 8032,
 * com as curvas Ed25519 e Ed448, via as APIs padrao KeyPairGenerator e Signature.</p>
 * <p>Beneficio: assinaturas rapidas, deterministicas (nao dependem de um bom gerador
 * aleatorio a cada assinatura), resistentes a ataques de canal lateral e de tamanho
 * fixo (64 bytes para Ed25519). Usada em SSH, TLS 1.3, JWT (EdDSA), etc.</p>
 * <p>Antes: ECDSA (SHA256withECDSA), em que uma falha no aleatorio pode vazar a chave,
 * ou bibliotecas externas como Bouncy Castle.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/security/EdDsaSignatureExample.java
 * </pre>
 */
public class EdDsaSignatureExample {

    public static void main(String[] args) throws GeneralSecurityException {
        byte[] invoice = "NF-1001;cliente=Ana;total=4500.00".getBytes(StandardCharsets.UTF_8);

        System.out.println("=== Abordagem antiga: ECDSA (secp256r1) ===");
        KeyPairGenerator ecGenerator = KeyPairGenerator.getInstance("EC");
        ecGenerator.initialize(new ECGenParameterSpec("secp256r1"));
        KeyPair ecKeys = ecGenerator.generateKeyPair();
        byte[] ecSig1 = sign("SHA256withECDSA", ecKeys, invoice);
        byte[] ecSig2 = sign("SHA256withECDSA", ecKeys, invoice);
        System.out.println("Tamanho: " + ecSig1.length + " e " + ecSig2.length + " bytes (DER, variavel)");
        System.out.println("Duas assinaturas iguais? " + java.util.Arrays.equals(ecSig1, ecSig2) + " (aleatoria)");

        System.out.println("=== Java 15: Ed25519 ===");
        KeyPair edKeys = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        System.out.println("Algoritmo da chave: " + edKeys.getPublic().getAlgorithm());
        byte[] edSig1 = sign("Ed25519", edKeys, invoice);
        byte[] edSig2 = sign("Ed25519", edKeys, invoice);
        System.out.println("Assinatura (Base64): " + Base64.getEncoder().encodeToString(edSig1));
        System.out.println("Tamanho: " + edSig1.length + " bytes (fixo)");
        System.out.println("Duas assinaturas iguais? " + java.util.Arrays.equals(edSig1, edSig2) + " (deterministica)");

        System.out.println("=== Verificacao ===");
        System.out.println("Nota original valida?   " + verify(edKeys, invoice, edSig1));
        byte[] tampered = "NF-1001;cliente=Ana;total=45.00".getBytes(StandardCharsets.UTF_8);
        System.out.println("Nota adulterada valida? " + verify(edKeys, tampered, edSig1));
    }

    private static byte[] sign(String algorithm, KeyPair keys, byte[] data) throws GeneralSecurityException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(keys.getPrivate());
        signature.update(data);
        return signature.sign();
    }

    private static boolean verify(KeyPair keys, byte[] data, byte[] sig) throws GeneralSecurityException {
        Signature verifier = Signature.getInstance("Ed25519");
        verifier.initVerify(keys.getPublic());
        verifier.update(data);
        return verifier.verify(sig);
    }
}
