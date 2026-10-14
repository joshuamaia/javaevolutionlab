package br.com.javalab.java11.files;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Java 11 - Files.readString e Files.writeString
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: ler ou gravar um arquivo de texto inteiro com uma chamada. O charset padrão é
 * UTF-8 (há sobrecargas que recebem um {@code Charset}); {@code writeString} aceita
 * {@code OpenOption}s como {@code APPEND}.</p>
 * <p>Benefício: código curto e correto para arquivos pequenos/médios (configs, templates,
 * JSON).</p>
 * <p>Antes: {@code new String(Files.readAllBytes(p), UTF_8)} e
 * {@code Files.write(p, texto.getBytes(UTF_8))}; antes do Java 7, BufferedReader/Writer.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/files/FilesReadWriteStringExample.java
 * </pre>
 */
public class FilesReadWriteStringExample {

    public static void main(String[] args) throws IOException {
        Path tempDir = Files.createTempDirectory("readstring");
        Path receipt = tempDir.resolve("recibo.txt");
        try {
            System.out.println("=== Antes (Java 7/8) ===");
            Files.write(receipt, "Recibo nº 1 - Ana - R$ 150,00\n".getBytes(StandardCharsets.UTF_8));
            System.out.print(new String(Files.readAllBytes(receipt), StandardCharsets.UTF_8));

            System.out.println();
            System.out.println("=== Java 11: writeString / readString ===");
            Files.writeString(receipt, "Recibo nº 2 - Bruno - R$ 89,90\n");
            Files.writeString(receipt, "Pagamento: PIX\n", StandardOpenOption.APPEND);
            String content = Files.readString(receipt);
            System.out.print(content);
            System.out.println("Linhas: " + content.lines().count() + " | arquivo: " + Files.size(receipt) + " bytes");

            System.out.println();
            System.out.println("=== Charset explícito ===");
            Files.writeString(receipt, "Ação concluída", StandardCharsets.ISO_8859_1);
            System.out.println("ISO-8859-1: " + Files.readString(receipt, StandardCharsets.ISO_8859_1)
                    + " (" + Files.size(receipt) + " bytes)");
            try {
                Files.readString(receipt); // lê como UTF-8 um arquivo que não é UTF-8
            } catch (IOException e) {
                System.out.println("readString em UTF-8 de arquivo Latin-1 -> " + e.getClass().getSimpleName());
            }
        } finally {
            Files.deleteIfExists(receipt);
            Files.deleteIfExists(tempDir);
        }
    }
}
