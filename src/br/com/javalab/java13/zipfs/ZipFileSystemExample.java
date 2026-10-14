package br.com.javalab.java13.zipfs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Java 13 - FileSystems.newFileSystem(Path) e newFileSystem(Path, Map)
 *
 * <p>Status: Definitivo (novas sobrecargas, sem JEP).</p>
 *
 * <p>O que e: abrir um arquivo ZIP/JAR como um {@code FileSystem} passando apenas o
 * {@code Path}, sem precisar de URI "jar:file:..." nem de um ClassLoader nulo.</p>
 * <p>Beneficio: ler e escrever entradas de um ZIP com a API comum de Files/Path.</p>
 * <p>Antes: {@code FileSystems.newFileSystem(URI.create("jar:" + path.toUri()), env)}
 * ou {@code newFileSystem(path, (ClassLoader) null)} (ambiguo no 13 por causa do null).</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java13/zipfs/ZipFileSystemExample.java
 * </pre>
 */
public class ZipFileSystemExample {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("zipfs-demo");
        Path zip = dir.resolve("orders.zip");
        try {
            System.out.println("=== Abordagem antiga: URI jar: para criar o ZIP ===");
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:" + zip.toUri()), env)) {
                Files.writeString(fs.getPath("/order-1001.txt"), "Notebook;4500.00");
            }
            System.out.println("Criado: " + zip.getFileName() + " (" + Files.size(zip) + " bytes)");

            System.out.println("=== Java 13: newFileSystem(Path) para abrir e alterar ===");
            try (FileSystem fs = FileSystems.newFileSystem(zip)) {
                Files.createDirectories(fs.getPath("/2019"));
                Files.writeString(fs.getPath("/2019/order-1002.txt"), "Mouse;89.90");
            }

            System.out.println("=== Listando entradas ===");
            try (FileSystem fs = FileSystems.newFileSystem(zip);
                 Stream<Path> entries = Files.walk(fs.getPath("/"))) {
                entries.filter(Files::isRegularFile).forEach(p -> {
                    try {
                        System.out.println(p + " -> " + Files.readString(p));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } finally {
            Files.deleteIfExists(zip);
            Files.delete(dir);
        }
    }
}
