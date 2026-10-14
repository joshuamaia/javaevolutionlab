package br.com.javalab.java10.cds;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Java 10 - Application Class-Data Sharing / AppCDS (JEP 310)
 *
 * <p>Status: Produto (JVM).</p>
 *
 * <p>O que é: o CDS (Java 5) guardava classes do JDK pré-processadas num arquivo mapeado em
 * memória. O AppCDS estende isso às classes <b>da aplicação</b> (classpath), reduzindo o
 * tempo de inicialização e a memória compartilhada entre várias JVMs.</p>
 * <p>Benefício: startup mais rápido (microserviços, CLIs, contêineres).</p>
 *
 * <p>Fluxo do Java 10 (3 passos, também válido hoje):</p>
 * <pre>
 * java -XX:+UseAppCDS -XX:DumpLoadedClassList=classes.lst -cp app.jar Main        (JDK 10 exigia +UseAppCDS)
 * java -XX:+UseAppCDS -Xshare:dump -XX:SharedClassListFile=classes.lst -XX:SharedArchiveFile=app.jsa -cp app.jar
 * java -XX:+UseAppCDS -XX:SharedArchiveFile=app.jsa -cp app.jar Main
 * </pre>
 * <p>Evolução: JDK 12 passou a trazer um arquivo CDS padrão para o JDK (JEP 341); JDK 13
 * trouxe o arquivo dinâmico (JEP 350):
 * {@code java -XX:ArchiveClassesAtExit=app.jsa -cp app.jar Main} e depois
 * {@code java -XX:SharedArchiveFile=app.jsa -cp app.jar Main}; JDK 19 criou
 * {@code -XX:+AutoCreateSharedArchive}. Classes vindas de <b>diretórios</b> não são
 * arquivadas — por isso o exemplo empacota a si mesmo num JAR temporário.</p>
 *
 * <p>Este exemplo relança a si mesmo (modo "child") com e sem CDS e mede o tempo de cada
 * execução. Precisa rodar a partir de classes compiladas (não funciona no modo código-fonte,
 * onde a classe só existe em memória).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * javac -d /tmp/out src/br/com/javalab/java10/cds/ApplicationClassDataSharingExample.java
 * java -cp /tmp/out br.com.javalab.java10.cds.ApplicationClassDataSharingExample
 * </pre>
 */
public class ApplicationClassDataSharingExample {

    private static final String CLASS_FILE = "ApplicationClassDataSharingExample.class";

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("child")) {
            runWorkload();
            return;
        }
        byte[] classBytes;
        try (InputStream in = ApplicationClassDataSharingExample.class.getResourceAsStream(CLASS_FILE)) {
            classBytes = in == null ? null : in.readAllBytes();
        }
        if (classBytes == null) {
            System.out.println("Classe compilada não encontrada (modo código-fonte?). Compile com javac e rode pelo -cp.");
            return;
        }

        String java = ProcessHandle.current().info().command().orElse("java");
        String mainClass = ApplicationClassDataSharingExample.class.getName();
        boolean jdk10 = Runtime.version().feature() == 10;
        Path dir = Files.createTempDirectory("appcds");
        Path jar = dir.resolve("app.jar");
        Path classList = dir.resolve("classes.lst");
        Path archive = dir.resolve("app.jsa");
        try {
            createJar(jar, mainClass.replace('.', '/') + ".class", classBytes);
            List<String> appCds = jdk10 ? List.of("-XX:+UseAppCDS") : List.of();

            System.out.println("=== Passo 1: lista de classes carregadas ===");
            run(java, concat(appCds, "-XX:DumpLoadedClassList=" + classList, "-cp", jar.toString(), mainClass, "child"));
            System.out.println("Classes na lista: " + Files.readAllLines(classList).size());

            System.out.println();
            System.out.println("=== Passo 2: -Xshare:dump gera o arquivo " + archive.getFileName() + " ===");
            run(java, concat(appCds, "-Xshare:dump", "-XX:SharedClassListFile=" + classList,
                    "-XX:SharedArchiveFile=" + archive, "-cp", jar.toString()));
            System.out.println("Tamanho do arquivo CDS: " + Files.size(archive) / 1024 + " KB");

            System.out.println();
            System.out.println("=== Passo 3: comparando inicialização (melhor de 5 execuções) ===");
            long off = best(java, concat(List.of("-Xshare:off"), "-cp", jar.toString(), mainClass, "child"));
            long jdkOnly = best(java, concat(List.of(), "-cp", jar.toString(), mainClass, "child"));
            long app = best(java, concat(appCds, "-XX:SharedArchiveFile=" + archive, "-cp", jar.toString(), mainClass, "child"));
            System.out.printf("  -Xshare:off (sem CDS)           : %4d ms%n", off);
            System.out.printf("  CDS padrão (só classes do JDK)  : %4d ms%n", jdkOnly);
            System.out.printf("  AppCDS (JDK + aplicação)        : %4d ms%n", app);

            System.out.println();
            System.out.println("=== De onde veio a classe da aplicação? (-Xlog:class+load) ===");
            String log = run(java, concat(appCds, "-Xlog:class+load", "-XX:SharedArchiveFile=" + archive,
                    "-cp", jar.toString(), mainClass, "child"));
            for (String line : log.split("\\R")) { // String.lines() só chegou no Java 11
                if (line.contains(mainClass + " ")) {
                    System.out.println("  " + line.trim());
                }
            }
        } finally {
            for (Path file : List.of(jar, classList, archive, dir)) {
                deleteWithRetry(file);
            }
        }
    }

    /** Carga de trabalho do processo filho: usa várias áreas do JDK para carregar classes. */
    private static void runWorkload() {
        List<String> orders = new ArrayList<>(List.of("P3;2018-03-20;150.0", "P1;2018-03-21;80.5", "P2;2018-01-05;300.0"));
        Pattern separator = Pattern.compile(";");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String report = orders.stream()
                .map(separator::split)
                .sorted(Comparator.comparing(fields -> fields[0]))
                .map(fields -> fields[0] + " " + LocalDate.parse(fields[1]).format(formatter))
                .collect(Collectors.joining(", "));
        if (report.isEmpty()) {
            System.out.println("nenhum pedido");
        }
    }

    private static long best(String java, List<String> args) throws IOException, InterruptedException {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            run(java, args);
            best = Math.min(best, (System.nanoTime() - start) / 1_000_000);
        }
        return best;
    }

    private static String run(String java, List<String> args) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(java);
        command.addAll(args);
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), Charset.defaultCharset());
        if (process.waitFor() != 0) {
            throw new IllegalStateException("Falhou: " + command + "\n" + output);
        }
        return output;
    }

    private static List<String> concat(List<String> first, String... rest) {
        List<String> all = new ArrayList<>(first);
        all.addAll(List.of(rest));
        return all;
    }

    /**
     * Alguns JDKs gravam o .jsa como somente leitura (no Windows isso impede apagar) e o arquivo
     * pode ficar travado por instantes após o processo filho terminar.
     */
    private static void deleteWithRetry(Path file) throws InterruptedException {
        for (int attempt = 1; attempt <= 10; attempt++) {
            try {
                file.toFile().setWritable(true);
                Files.deleteIfExists(file);
                return;
            } catch (IOException e) {
                Thread.sleep(200);
            }
        }
        file.toFile().deleteOnExit();
        System.out.println("Aviso: não foi possível apagar " + file + " agora; será tentado ao sair.");
    }

    private static void createJar(Path jar, String entryName, byte[] content) throws IOException {
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar))) {
            out.putNextEntry(new JarEntry(entryName));
            out.write(content);
            out.closeEntry();
        }
    }
}
