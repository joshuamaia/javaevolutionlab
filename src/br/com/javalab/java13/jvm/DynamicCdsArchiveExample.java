package br.com.javalab.java13.jvm;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

/**
 * Java 13 - Dynamic CDS Archives (JEP 350) e ZGC: Uncommit Unused Memory (JEP 351)
 *
 * <p>Status: Produto (JVM).</p>
 *
 * <p>O que e (JEP 350): ao final da execucao, a JVM grava num arquivo .jsa as classes
 * da APLICACAO que foram carregadas ({@code -XX:ArchiveClassesAtExit=app.jsa}). Nas
 * proximas execucoes, {@code -XX:SharedArchiveFile=app.jsa} mapeia essas classes ja
 * processadas, reduzindo o tempo de inicializacao. Complementa o arquivo CDS padrao do
 * JDK (Java 12, JEP 341), que cobre so as classes do proprio JDK.
 * (Java 19 trouxe {@code -XX:+AutoCreateSharedArchive}; Java 24/25 evoluiram para o
 * cache AOT, JEPs 483/514/515.)</p>
 * <p>Antes: AppCDS exigia 3 passos manuais (gerar lista de classes, -Xshare:dump, usar).</p>
 *
 * <p>JEP 351 (somente documentado): o ZGC passa a devolver ao sistema operacional a
 * memoria do heap nao usada (ligado por padrao; {@code -XX:ZUncommitDelay=<segundos>},
 * desligar com {@code -XX:-ZUncommit}).</p>
 *
 * <p>Este exemplo gera e compila um pequeno programa em um diretorio temporario e o
 * executa 3 vezes: sem arquivo, gravando o arquivo e usando o arquivo.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java13/jvm/DynamicCdsArchiveExample.java
 * java -XX:ArchiveClassesAtExit=app.jsa -cp minha-app.jar MinhaApp
 * java -XX:SharedArchiveFile=app.jsa -Xlog:cds -cp minha-app.jar MinhaApp
 * </pre>
 */
public class DynamicCdsArchiveExample {

    private static final String APP_SOURCE = String.join("\n",
            "import java.util.*;",
            "import java.util.stream.*;",
            "public class OrderApp {",
            "  public static void main(String[] a) {",
            "    List<Double> totals = IntStream.range(0, 100).mapToObj(i -> i * 1.5).collect(Collectors.toList());",
            "    System.out.println(\"Total: \" + totals.stream().mapToDouble(Double::doubleValue).sum());",
            "  }",
            "}");

    public static void main(String[] args) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("Compilador indisponivel (JRE sem jdk.compiler); veja os comandos no Javadoc.");
            return;
        }
        Path dir = Files.createTempDirectory("dyncds-demo");
        try {
            Path source = Files.writeString(dir.resolve("OrderApp.java"), APP_SOURCE);
            compiler.run(null, null, null, "-d", dir.toString(), source.toString());
            // CDS so arquiva classes vindas de JARs no classpath (diretorios nao sao suportados)
            Path jar = dir.resolve("order-app.jar");
            try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
                out.putNextEntry(new JarEntry("OrderApp.class"));
                out.write(Files.readAllBytes(dir.resolve("OrderApp.class")));
                out.closeEntry();
            }
            String java = ProcessHandle.current().info().command().orElse("java");
            Path archive = dir.resolve("app.jsa");

            System.out.println("=== 1) Sem arquivo dinamico ===");
            run(java, jar, "-Xshare:auto");

            System.out.println("=== 2) Gravando com -XX:ArchiveClassesAtExit ===");
            run(java, jar, "-XX:ArchiveClassesAtExit=" + archive);
            System.out.println("Arquivo gerado: " + archive.getFileName() + " (" + Files.size(archive) / 1024 + " KB)");

            System.out.println("=== 3) Usando com -XX:SharedArchiveFile ===");
            run(java, jar, "-XX:SharedArchiveFile=" + archive);

            System.out.println("(Diferencas de tempo sao pequenas num programa minusculo;");
            System.out.println(" em aplicacoes com milhares de classes o ganho e maior.)");
        } finally {
            try (Stream<Path> files = Files.walk(dir)) {
                files.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
    }

    private static void run(String java, Path classpath, String flag) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>(List.of(java, flag, "-cp", classpath.toString(), "OrderApp"));
        long start = System.nanoTime();
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes()).trim();
        int exit = process.waitFor();
        long millis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        System.out.println("  saida: " + output + " | exit=" + exit + " | " + millis + " ms");
    }
}
