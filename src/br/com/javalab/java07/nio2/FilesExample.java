package br.com.javalab.java07.nio2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.List;

/**
 * Java 7 - NIO.2: classe utilitária Files (JSR 203)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@link Files} concentra operações de arquivos: criar, ler, escrever, copiar,
 * mover, apagar e consultar atributos, sempre com exceções descritivas.</p>
 * <p>Benefício: leitura/escrita em uma linha, cópia/move atômicos, atributos ricos.</p>
 * <p>Antes: streams manuais de {@code java.io}, sem cópia nativa, {@code File.renameTo}
 * retornando apenas boolean.</p>
 * <p>Obs.: aqui usamos apenas os métodos do Java 7 (ex.: {@code readAllLines(path, charset)});
 * {@code Files.lines} é do Java 8 e {@code Files.readString} do Java 11.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/nio2/FilesExample.java
 * </pre>
 */
public class FilesExample {

    public static void main(String[] args) throws IOException {
        Path workDir = Files.createTempDirectory("javalab-files-");
        try {
            System.out.println("Diretório temporário: " + workDir);

            System.out.println();
            System.out.println("=== Escrever e ler todas as linhas ===");
            Path orders = workDir.resolve("orders.csv");
            List<String> lines = Arrays.asList("id;customer;total", "1;Alice;150.00", "2;Bob;89.90");
            Files.write(orders, lines, StandardCharsets.UTF_8);
            List<String> read = Files.readAllLines(orders, StandardCharsets.UTF_8);
            System.out.println("Linhas lidas: " + read);

            System.out.println();
            System.out.println("=== Append e leitura com BufferedReader ===");
            try (BufferedWriter writer = Files.newBufferedWriter(orders, StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND)) {
                writer.write("3;Carol;42.00");
                writer.newLine();
            }
            try (BufferedReader reader = Files.newBufferedReader(orders, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("  " + line);
                }
            }

            System.out.println();
            System.out.println("=== Copiar e mover ===");
            Path backupDir = Files.createDirectories(workDir.resolve("backup/2011"));
            Path copy = Files.copy(orders, backupDir.resolve("orders-copy.csv"),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copiado para: " + workDir.relativize(copy));
            Path moved = Files.move(copy, backupDir.resolve("orders-archived.csv"),
                    StandardCopyOption.ATOMIC_MOVE);
            System.out.println("Movido para:  " + workDir.relativize(moved));
            System.out.println("Cópia ainda existe? " + Files.exists(copy));

            System.out.println();
            System.out.println("=== Atributos ===");
            System.out.println("size:        " + Files.size(orders) + " bytes");
            System.out.println("isReadable:  " + Files.isReadable(orders));
            System.out.println("isDirectory: " + Files.isDirectory(backupDir));
            System.out.println("contentType: " + Files.probeContentType(orders) + " (pode ser null)");
            BasicFileAttributes attrs = Files.readAttributes(orders, BasicFileAttributes.class);
            System.out.println("creationTime: " + attrs.creationTime());
            System.out.println("isRegularFile: " + attrs.isRegularFile());
            Files.setLastModifiedTime(orders, FileTime.fromMillis(0));
            System.out.println("lastModified alterado para: " + Files.getLastModifiedTime(orders));

            System.out.println();
            System.out.println("=== Apagar ===");
            Files.delete(moved);
            System.out.println("deleteIfExists (já apagado): " + Files.deleteIfExists(moved));
        } finally {
            deleteRecursively(workDir);
            System.out.println("Diretório temporário removido: " + !Files.exists(workDir));
        }
    }

    /** Remoção simples (sem lambdas) — ver WalkFileTreeExample para a forma com FileVisitor. */
    private static void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (java.nio.file.DirectoryStream<Path> children = Files.newDirectoryStream(path)) {
                for (Path child : children) {
                    deleteRecursively(child);
                }
            }
        }
        Files.deleteIfExists(path);
    }
}
