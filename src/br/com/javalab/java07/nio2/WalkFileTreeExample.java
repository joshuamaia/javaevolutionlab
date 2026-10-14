package br.com.javalab.java07.nio2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

/**
 * Java 7 - NIO.2: Files.walkFileTree e FileVisitor (JSR 203)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: percorre recursivamente uma árvore de diretórios chamando um
 * {@link java.nio.file.FileVisitor} (preVisitDirectory, visitFile, visitFileFailed,
 * postVisitDirectory). {@link SimpleFileVisitor} fornece implementações padrão.</p>
 * <p>Benefício: travessia recursiva robusta, com controle (CONTINUE, SKIP_SUBTREE,
 * TERMINATE) e tratamento de falhas por arquivo.</p>
 * <p>Antes: recursão manual com {@code File.listFiles()}, que retorna {@code null} em erro.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/nio2/WalkFileTreeExample.java
 * </pre>
 */
public class WalkFileTreeExample {

    public static void main(String[] args) throws IOException {
        final Path root = Files.createTempDirectory("javalab-walk-");
        createSampleTree(root);

        System.out.println("=== Visitando a árvore e somando tamanhos de .log ===");
        final long[] totalLogBytes = {0};
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                System.out.println("[dir]  " + root.relativize(dir));
                if (dir.getFileName().toString().equals("cache")) {
                    System.out.println("       (pulando subárvore 'cache')");
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                System.out.println("[file] " + root.relativize(file) + " (" + attrs.size() + " bytes)");
                if (file.toString().endsWith(".log")) {
                    totalLogBytes[0] += attrs.size();
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.out.println("[erro] " + file + ": " + exc);
                return FileVisitResult.CONTINUE;
            }
        });
        System.out.println("Total em arquivos .log: " + totalLogBytes[0] + " bytes");

        System.out.println();
        System.out.println("=== Apagando recursivamente com postVisitDirectory ===");
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir); // só depois que o conteúdo foi apagado
                return FileVisitResult.CONTINUE;
            }
        });
        System.out.println("Árvore removida? " + !Files.exists(root));
    }

    private static void createSampleTree(Path root) throws IOException {
        Files.createDirectories(root.resolve("app/logs"));
        Files.createDirectories(root.resolve("app/cache"));
        Files.write(root.resolve("app/config.properties"),
                Arrays.asList("env=prod"), StandardCharsets.UTF_8);
        Files.write(root.resolve("app/logs/server.log"),
                Arrays.asList("INFO start", "WARN slow query"), StandardCharsets.UTF_8);
        Files.write(root.resolve("app/logs/access.log"),
                Arrays.asList("GET /orders 200"), StandardCharsets.UTF_8);
        Files.write(root.resolve("app/cache/tmp.log"),
                Arrays.asList("ignored"), StandardCharsets.UTF_8);
    }
}
