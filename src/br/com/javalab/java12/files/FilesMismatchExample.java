package br.com.javalab.java12.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Java 12 - Files.mismatch
 *
 * <p>Status: Definitivo (API nova, sem JEP).</p>
 *
 * <p>O que e: {@code Files.mismatch(a, b)} devolve a posicao do primeiro byte
 * diferente entre dois arquivos, ou -1 se forem identicos.</p>
 * <p>Beneficio: comparar arquivos sem carregar tudo em memoria e sem laco manual.</p>
 * <p>Antes: ler os dois arquivos (readAllBytes) e comparar com Arrays.equals, ou
 * escrever um laco com dois InputStreams.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java12/files/FilesMismatchExample.java
 * </pre>
 */
public class FilesMismatchExample {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("mismatch-demo");
        Path original = Files.writeString(dir.resolve("app-2019-03-01.log"),
                "INFO start\nINFO order=1001 OK\nINFO stop\n");
        Path copy = Files.writeString(dir.resolve("copy.log"),
                "INFO start\nINFO order=1001 OK\nINFO stop\n");
        Path changed = Files.writeString(dir.resolve("changed.log"),
                "INFO start\nWARN order=1001 RETRY\nINFO stop\n");
        try {
            System.out.println("=== Abordagem antiga: ler tudo e comparar ===");
            boolean equal = Arrays.equals(Files.readAllBytes(original), Files.readAllBytes(changed));
            System.out.println("Iguais? " + equal + " (mas onde esta a diferenca?)");

            System.out.println("=== Java 12: Files.mismatch ===");
            System.out.println("original x copy    -> " + Files.mismatch(original, copy));
            long pos = Files.mismatch(original, changed);
            System.out.println("original x changed -> " + pos);
            String text = Files.readString(original);
            System.out.println("Diferenca a partir de: \"" + text.substring((int) pos).lines().findFirst().orElse("") + "\"");
        } finally {
            Files.delete(original);
            Files.delete(copy);
            Files.delete(changed);
            Files.delete(dir);
        }
    }
}
