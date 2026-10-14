package br.com.javalab.java09.multirelease;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

/**
 * Java 9 - Multi-Release JAR Files (JEP 238)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: um único JAR pode conter versões diferentes da mesma classe/recurso para
 * versões diferentes do Java. Com {@code Multi-Release: true} no MANIFEST, a JVM procura
 * primeiro em {@code META-INF/versions/N/} (N = maior versão &lt;= runtime) e cai para a
 * entrada base se não achar.</p>
 * <p>Benefício: bibliotecas podem usar APIs novas (ex.: VarHandle no 9) e continuar
 * funcionando no Java 8 com a implementação antiga, num só artefato.</p>
 * <p>Antes: publicar um JAR por versão do Java ou usar reflexão para detectar APIs.</p>
 *
 * <p>Este exemplo cria, num diretório temporário, um JAR multi-release com entradas de
 * texto (o mecanismo de resolução é o mesmo para .class) e o abre com
 * {@code new JarFile(file, true, ZipFile.OPEN_READ, versao)}.</p>
 *
 * <p>Na prática, o JAR é criado pela ferramenta {@code jar}:</p>
 * <pre>
 * javac --release 8 -d classes   src/main/java/...
 * javac --release 9 -d classes-9 src/main/java9/...
 * jar --create --file lib.jar -C classes . --release 9 -C classes-9 .
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/multirelease/MultiReleaseJarExample.java
 * </pre>
 */
public class MultiReleaseJarExample {

    private static final String RESOURCE = "br/com/javalab/strategy.txt";

    public static void main(String[] args) throws IOException {
        Path tempDir = Files.createTempDirectory("mrjar");
        Path jarPath = tempDir.resolve("inventory-lib.jar");
        try {
            createMultiReleaseJar(jarPath);

            System.out.println("=== Conteúdo do JAR ===");
            try (JarFile plain = new JarFile(jarPath.toFile())) {
                plain.stream().forEach(e -> System.out.println("  " + e.getName()));
            }

            System.out.println();
            System.out.println("=== Qual versão da entrada é resolvida? ===");
            String[] versions = {"8", "9", "10", "11", "25"};
            for (String version : versions) {
                Runtime.Version runtimeVersion = Runtime.Version.parse(version);
                try (JarFile jar = new JarFile(jarPath.toFile(), true, ZipFile.OPEN_READ, runtimeVersion)) {
                    JarEntry entry = jar.getJarEntry(RESOURCE);
                    // (JarEntry.getRealName(), que mostra o caminho real resolvido, só chegou no Java 10)
                    System.out.printf("Java %-3s (jar.getVersion()=%-2s) -> %s%n",
                            version, jar.getVersion(), read(jar, entry));
                }
            }

            System.out.println();
            System.out.println("=== Runtime atual (" + Runtime.version() + ") ===");
            try (JarFile jar = new JarFile(jarPath.toFile(), true, ZipFile.OPEN_READ, Runtime.version())) {
                System.out.println("isMultiRelease(): " + jar.isMultiRelease());
                System.out.println("Resolvido: " + read(jar, jar.getJarEntry(RESOURCE)));
            }
        } finally {
            Files.deleteIfExists(jarPath);
            Files.deleteIfExists(tempDir);
        }
    }

    private static void createMultiReleaseJar(Path jarPath) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(new Attributes.Name("Multi-Release"), "true");
        try (OutputStream out = Files.newOutputStream(jarPath);
             JarOutputStream jar = new JarOutputStream(out, manifest)) {
            addEntry(jar, RESOURCE, "base (Java 8): sincronização com synchronized");
            addEntry(jar, "META-INF/versions/9/" + RESOURCE, "Java 9+: atualização atômica com VarHandle");
            addEntry(jar, "META-INF/versions/11/" + RESOURCE, "Java 11+: cliente HTTP com java.net.http");
        }
    }

    private static void addEntry(JarOutputStream jar, String name, String content) throws IOException {
        jar.putNextEntry(new JarEntry(name));
        jar.write(content.getBytes(StandardCharsets.UTF_8));
        jar.closeEntry();
    }

    private static String read(JarFile jar, JarEntry entry) throws IOException {
        try (InputStream in = jar.getInputStream(entry)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
