package br.com.javalab.java08.base64;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * Java 8 - Base64 API (JEP 135)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@link Base64} com codificadores/decodificadores padrão (RFC 4648),
 * URL-safe ({@code getUrlEncoder}) e MIME (RFC 2045, linhas de 76 caracteres).</p>
 * <p>Benefício: API pública e eficiente no próprio JDK.</p>
 * <p>Antes: a classe interna não suportada {@code sun.misc.BASE64Encoder},
 * {@code javax.xml.bind.DatatypeConverter} (JAXB, removido do JDK no Java 11)
 * ou Apache Commons Codec.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/base64/Base64Example.java
 * </pre>
 */
public class Base64Example {

    public static void main(String[] args) {
        System.out.println("=== Básico: credenciais HTTP Basic ===");
        String credentials = "api-user:s3cr3t?";
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        System.out.println("Authorization: Basic " + encoded);
        System.out.println("Decodificado: " + decoded);

        System.out.println();
        System.out.println("=== URL-safe: sem '+' e '/' (útil em tokens e URLs) ===");
        byte[] token = {(byte) 0xFB, (byte) 0xFF, (byte) 0xBF, 0x01, 0x02};
        System.out.println("Básico:   " + Base64.getEncoder().encodeToString(token));
        System.out.println("URL-safe: " + Base64.getUrlEncoder().encodeToString(token));
        System.out.println("URL-safe sem padding: " + Base64.getUrlEncoder().withoutPadding().encodeToString(token));

        System.out.println();
        System.out.println("=== MIME: quebra de linha a cada 76 caracteres ===");
        byte[] attachment = new byte[90];
        for (int i = 0; i < attachment.length; i++) {
            attachment[i] = (byte) i;
        }
        String mime = Base64.getMimeEncoder().encodeToString(attachment);
        System.out.println(mime);
        System.out.println("Ida e volta preserva bytes? "
                + Arrays.equals(attachment, Base64.getMimeDecoder().decode(mime)));

        System.out.println();
        System.out.println("=== Entrada inválida ===");
        try {
            Base64.getDecoder().decode("isso não é base64!");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
        }
    }
}
