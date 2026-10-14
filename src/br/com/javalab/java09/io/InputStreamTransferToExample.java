package br.com.javalab.java09.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Java 9 - InputStream.transferTo, readAllBytes e readNBytes
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code in.transferTo(out)} copia todo o conteúdo de um InputStream para um
 * OutputStream e devolve o número de bytes. {@code readAllBytes()} lê tudo para um array.</p>
 * <p>Benefício: fim do laço manual com buffer, fonte comum de bugs.</p>
 * <p>Antes: {@code byte[] buf = new byte[8192]; while ((n = in.read(buf)) != -1) out.write(buf, 0, n);}</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/io/InputStreamTransferToExample.java
 * </pre>
 */
public class InputStreamTransferToExample {

    public static void main(String[] args) throws IOException {
        byte[] invoice = "NF-e 123\nCliente: Ana\nTotal: R$ 289,80\n".getBytes(StandardCharsets.UTF_8);
        Path tempDir = Files.createTempDirectory("transferto");
        Path oldCopy = tempDir.resolve("nota-antiga.txt");
        Path newCopy = tempDir.resolve("nota-nova.txt");
        try {
            System.out.println("=== Abordagem antiga: laço com buffer ===");
            try (InputStream in = new ByteArrayInputStream(invoice);
                 OutputStream out = Files.newOutputStream(oldCopy)) {
                byte[] buffer = new byte[8];
                int read;
                long total = 0;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    total += read;
                }
                System.out.println("Copiados " + total + " bytes");
            }

            System.out.println();
            System.out.println("=== Java 9: transferTo ===");
            try (InputStream in = new ByteArrayInputStream(invoice);
                 OutputStream out = Files.newOutputStream(newCopy)) {
                long total = in.transferTo(out);
                System.out.println("Copiados " + total + " bytes");
            }

            System.out.println();
            System.out.println("=== Java 9: readAllBytes ===");
            try (InputStream in = Files.newInputStream(newCopy)) {
                System.out.print(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            }
        } finally {
            Files.deleteIfExists(oldCopy);
            Files.deleteIfExists(newCopy);
            Files.deleteIfExists(tempDir);
        }
    }
}
