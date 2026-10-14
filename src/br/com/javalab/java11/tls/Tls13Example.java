package br.com.javalab.java11.tls;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

/**
 * Java 11 - TLS 1.3 (JEP 332)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: suporte ao TLS 1.3 (RFC 8446) no JSSE, habilitado por padrão em clientes e
 * servidores. Handshake com 1 round-trip (em vez de 2), apenas cifras AEAD modernas
 * (AES-GCM, ChaCha20-Poly1305), sigilo futuro obrigatório e remoção de algoritmos fracos
 * (RSA key exchange, SHA-1, RC4, CBC).</p>
 * <p>Benefício: conexões HTTPS mais rápidas e seguras sem mudar código.</p>
 * <p>Antes (Java 8–10): TLS 1.2 no máximo.</p>
 *
 * <p>Flags para experimentar:</p>
 * <pre>
 * java -Djdk.tls.client.protocols=TLSv1.3 ...          (forçar só TLS 1.3 no cliente)
 * java -Djavax.net.debug=ssl:handshake ...              (ver o handshake)
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/tls/Tls13Example.java
 * </pre>
 */
public class Tls13Example {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        SSLContext context = SSLContext.getDefault();

        System.out.println("=== Protocolos suportados ===");
        SSLParameters supported = context.getSupportedSSLParameters();
        System.out.println(Arrays.toString(supported.getProtocols()));

        System.out.println();
        System.out.println("=== Protocolos habilitados por padrão ===");
        SSLParameters defaults = context.getDefaultSSLParameters();
        System.out.println(Arrays.toString(defaults.getProtocols()));

        System.out.println();
        System.out.println("=== Cipher suites exclusivas do TLS 1.3 ===");
        List<String> tls13Suites = Arrays.stream(defaults.getCipherSuites())
                .filter(suite -> suite.startsWith("TLS_AES_") || suite.startsWith("TLS_CHACHA20_"))
                .collect(Collectors.toList());
        tls13Suites.forEach(suite -> System.out.println("  " + suite));

        System.out.println();
        System.out.println("=== Contexto específico para TLS 1.3 ===");
        SSLContext tls13 = SSLContext.getInstance("TLSv1.3");
        System.out.println("Protocolo: " + tls13.getProtocol() + " | provedor: " + tls13.getProvider().getName());
    }
}
