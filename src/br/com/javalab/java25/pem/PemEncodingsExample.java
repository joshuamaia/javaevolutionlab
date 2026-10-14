package br.com.javalab.java25.pem;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PEMDecoder;
import java.security.PEMEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

/**
 * Java 25 - PEM Encodings of Cryptographic Objects (JEP 470)
 *
 * <p>Status: Preview (primeira). Exige {@code --enable-preview}.</p>
 *
 * <p>O que é: {@link PEMEncoder} e {@link PEMDecoder} convertem chaves, certificados e CRLs
 * de/para o formato PEM (texto Base64 entre linhas {@code -----BEGIN ...-----} /
 * {@code -----END ...-----}), usado por OpenSSL, servidores web, Kubernetes, etc.
 * Suporta também chaves privadas cifradas com senha ({@code withEncryption}/{@code withDecryption}).</p>
 * <p>Benefício: ler um {@code .pem} gerado pelo OpenSSL ou exportar uma chave para ele sem
 * código manual nem bibliotecas externas (Bouncy Castle).</p>
 * <p>Antes: montar o texto à mão - {@code Base64.getMimeEncoder()} + cabeçalhos - e, para
 * ler, remover cabeçalhos, decodificar Base64 e usar {@code X509EncodedKeySpec} /
 * {@code PKCS8EncodedKeySpec} + {@code KeyFactory} sabendo de antemão o algoritmo.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java --enable-preview --source 25 src/br/com/javalab/java25/pem/PemEncodingsExample.java
 * </pre>
 */
public class PemEncodingsExample {

    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(256);
        KeyPair keys = generator.generateKeyPair();

        System.out.println("=== Abordagem antiga: PEM montado à mão ===");
        String manualPem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(keys.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----";
        System.out.println(manualPem.lines().limit(2).toList() + " ...");

        System.out.println();
        System.out.println("=== PEMEncoder: chave pública ===");
        PEMEncoder encoder = PEMEncoder.of();
        String publicPem = encoder.encodeToString(keys.getPublic());
        System.out.println(publicPem);
        System.out.println("Mesmo conteúdo do PEM manual? " + publicPem.lines().toList().equals(manualPem.lines().toList()));

        System.out.println();
        System.out.println("=== PEMEncoder: chave privada cifrada com senha ===");
        char[] password = "senha-forte".toCharArray();
        String encryptedPrivatePem = encoder.withEncryption(password).encodeToString(keys.getPrivate());
        System.out.println(encryptedPrivatePem.lines().findFirst().orElse("") + " ... ("
                + encryptedPrivatePem.lines().count() + " linhas)");

        System.out.println();
        System.out.println("=== PEMDecoder: de volta para objetos Java ===");
        PEMDecoder decoder = PEMDecoder.of();
        PublicKey publicKey = decoder.decode(publicPem, PublicKey.class);
        PrivateKey privateKey = decoder.withDecryption(password).decode(encryptedPrivatePem, PrivateKey.class);
        System.out.println("Pública: " + publicKey.getAlgorithm() + " | Privada: " + privateKey.getAlgorithm()
                + " (" + privateKey.getFormat() + ")");

        // Prova de que as chaves decodificadas funcionam: assinar e verificar
        byte[] order = "pedido #7, total R$ 99,90".getBytes();
        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(privateKey);
        signer.update(order);
        byte[] signature = signer.sign();
        Signature verifier = Signature.getInstance("SHA256withECDSA");
        verifier.initVerify(publicKey);
        verifier.update(order);
        System.out.println("Assinatura com chaves lidas do PEM é válida? " + verifier.verify(signature));
    }
}
