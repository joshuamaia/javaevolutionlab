package br.com.javalab.java07.nio2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java 7 - Comparação: java.io.File (API antiga) vs NIO.2 (JSR 203)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: lado a lado, as mesmas tarefas feitas com {@code java.io.File}/{@code FileReader}
 * e com {@code java.nio.file.Files}/{@code Path}.</p>
 * <p>Benefício do NIO.2: erros viram exceções descritivas (em vez de {@code false}),
 * charset explícito, cópia nativa, menos código.</p>
 * <p>Antes: {@code File.delete()} e {@code renameTo()} retornavam boolean sem motivo;
 * {@code FileReader} usava o charset padrão da plataforma (antes do Java 18).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/nio2/FileVsNio2Comparison.java
 * </pre>
 */
public class FileVsNio2Comparison {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("javalab-compare-");
        File oldDir = dir.toFile();
        try {
            System.out.println("=== 1) Apagar arquivo inexistente ===");
            boolean deleted = new File(oldDir, "missing.txt").delete();
            System.out.println("Antigo: File.delete() retornou " + deleted + " (por quê? não se sabe)");
            try {
                Files.delete(dir.resolve("missing.txt"));
            } catch (NoSuchFileException e) {
                System.out.println("NIO.2: Files.delete lançou " + e.getClass().getSimpleName()
                        + ": " + e.getFile());
            }

            System.out.println();
            System.out.println("=== 2) Escrever arquivo ===");
            File oldFile = new File(oldDir, "customers-old.txt");
            FileWriter fw = new FileWriter(oldFile); // charset padrão da plataforma
            try {
                fw.write("Alice\nBob\nJosé\n");
            } finally {
                fw.close();
            }
            System.out.println("Antigo: FileWriter + try/finally manual");
            Path newFile = dir.resolve("customers-new.txt");
            Files.write(newFile, Arrays.asList("Alice", "Bob", "José"), StandardCharsets.UTF_8);
            System.out.println("NIO.2: Files.write(path, linhas, UTF_8) em uma linha");

            System.out.println();
            System.out.println("=== 3) Ler todas as linhas ===");
            List<String> oldLines = new ArrayList<String>();
            BufferedReader reader = new BufferedReader(new FileReader(oldFile));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    oldLines.add(line);
                }
            } finally {
                reader.close();
            }
            System.out.println("Antigo: " + oldLines + " (loop + close manual)");
            System.out.println("NIO.2:  " + Files.readAllLines(newFile, StandardCharsets.UTF_8));

            System.out.println();
            System.out.println("=== 4) Copiar arquivo ===");
            File oldCopy = new File(oldDir, "copy-old.txt");
            InputStream in = new FileInputStream(oldFile);
            OutputStream out = new FileOutputStream(oldCopy);
            try {
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
            } finally {
                in.close();
                out.close();
            }
            System.out.println("Antigo: loop de buffer -> " + oldCopy.length() + " bytes");
            Path newCopy = Files.copy(newFile, dir.resolve("copy-new.txt"), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("NIO.2: Files.copy -> " + Files.size(newCopy) + " bytes");

            System.out.println();
            System.out.println("=== 5) Renomear para diretório inexistente ===");
            boolean renamed = oldCopy.renameTo(new File(oldDir, "no-such-dir/x.txt"));
            System.out.println("Antigo: File.renameTo retornou " + renamed);
            try {
                Files.move(newCopy, dir.resolve("no-such-dir/x.txt"));
            } catch (IOException e) {
                System.out.println("NIO.2: Files.move lançou " + e.getClass().getSimpleName());
            }

            System.out.println();
            System.out.println("=== 6) Listar diretório inexistente ===");
            File[] listed = new File(oldDir, "ghost").listFiles();
            System.out.println("Antigo: File.listFiles() retornou " + Arrays.toString(listed) + " (null!)");
            try {
                Files.newDirectoryStream(dir.resolve("ghost")).close();
            } catch (NoSuchFileException e) {
                System.out.println("NIO.2: newDirectoryStream lançou NoSuchFileException");
            }
        } finally {
            File[] children = oldDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    Files.deleteIfExists(child.toPath());
                }
            }
            Files.deleteIfExists(dir);
        }
    }
}
