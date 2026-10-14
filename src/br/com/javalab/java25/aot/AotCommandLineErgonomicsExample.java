package br.com.javalab.java25.aot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 25 - Ahead-of-Time Command-Line Ergonomics (JEP 514)
 *
 * <p>Status: Produto (JVM). Histórico: o cache AOT surgiu no Java 24 (JEP 483) com um fluxo
 * de DOIS passos ({@code -XX:AOTMode=record} e depois {@code -XX:AOTMode=create}). Parte do
 * Projeto Leyden; no mesmo release a JEP 515 adicionou perfis de métodos ao cache.</p>
 *
 * <p>O que é: a nova opção {@code -XX:AOTCacheOutput=arquivo} faz treino + criação do cache em
 * UM comando: a JVM executa a aplicação, grava a configuração num arquivo temporário e, ao
 * terminar, cria o cache. Também existe a variável de ambiente {@code JDK_AOT_VM_OPTIONS}
 * para passar opções só à etapa de criação.</p>
 * <p>Benefício: adotar o cache AOT (startup mais rápido) fica tão simples quanto o CDS.</p>
 *
 * <p>Exemplo de observação: relança a si mesmo (1) sem cache, (2) com
 * {@code -XX:AOTCacheOutput} num diretório temporário (treino) e (3) com {@code -XX:AOTCache},
 * medindo o tempo do início da JVM até o fim do main nos processos filhos. As classes da
 * aplicação só entram no cache se vierem de um JAR - em modo código-fonte ou classes soltas
 * em diretório, apenas as classes do JDK são cacheadas (o que já dá bom ganho).
 * Medido neste projeto (JDK 25, Windows): compilado ~42 ms -&gt; ~27 ms; em modo código-fonte
 * ~445 ms -&gt; ~224 ms (o próprio compilador javac usado pelo launcher entra no cache).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/aot/AotCommandLineErgonomicsExample.java
 * # manualmente (com um JAR):
 * java -XX:AOTCacheOutput=app.aot -cp app.jar br.com.javalab.java25.aot.AotCommandLineErgonomicsExample child
 * java -XX:AOTCache=app.aot -cp app.jar br.com.javalab.java25.aot.AotCommandLineErgonomicsExample child
 * </pre>
 */
public class AotCommandLineErgonomicsExample {

    record Order(String customer, String category, double total, LocalDate date) {}

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("child")) {
            startupWork();
            System.out.println(ManagementFactory.getRuntimeMXBean().getUptime());
            return;
        }
        Path dir = Files.createTempDirectory("aot-demo");
        try {
            Path cache = dir.resolve("app.aot");
            System.out.println("=== 1) Sem cache AOT (média de 3 execuções) ===");
            System.out.println("Tempo até o fim do main: " + average(3) + " ms");

            System.out.println();
            System.out.println("=== 2) Treino + criação do cache em UM passo (-XX:AOTCacheOutput) ===");
            runChild("-XX:AOTCacheOutput=" + cache, "-Xlog:aot=off", "-Xlog:cds=off");
            System.out.println("Cache criado: " + Files.exists(cache) + " (" + Files.size(cache) / 1024 / 1024 + " MB)");

            System.out.println();
            System.out.println("=== 3) Com o cache (-XX:AOTCache), média de 3 execuções ===");
            System.out.println("Tempo até o fim do main: " + average(3, "-XX:AOTCache=" + cache, "-Xlog:aot=off", "-Xlog:cds=off") + " ms");
        } catch (Exception e) {
            System.out.println("Relançamento indisponível (" + e.getMessage() + "). Use os comandos do Javadoc.");
        } finally {
            try (Stream<Path> files = Files.walk(dir)) {
                files.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
    }

    /** Trabalho típico de inicialização: muitas classes do JDK (streams, lambdas, regex, java.time). */
    static void startupWork() {
        List<Order> orders = List.of(
                new Order("ana", "livros", 89.9, LocalDate.of(2025, 9, 1)),
                new Order("bruno", "eletrônicos", 1299.0, LocalDate.of(2025, 9, 2)));
        Map<String, Double> totals = orders.stream()
                .collect(Collectors.groupingBy(Order::category, Collectors.summingDouble(Order::total)));
        boolean valid = Pattern.compile("^[\\w.]+@[\\w.]+$").matcher("ana@loja.com").matches();
        String date = LocalDate.of(2025, 9, 16).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (totals.isEmpty() || !valid || date.isEmpty()) throw new AssertionError();
    }

    static long average(int runs, String... flags) throws Exception {
        long sum = 0;
        for (int i = 0; i < runs; i++) {
            List<String> out = runChild(flags);
            sum += Long.parseLong(out.getLast().trim());
        }
        return sum / runs;
    }

    static List<String> runChild(String... flags) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(ProcessHandle.current().info().command().orElse("java"));
        cmd.addAll(List.of(flags));
        String sourceFile = System.getProperty("jdk.launcher.sourcefile"); // modo código-fonte
        if (sourceFile != null) {
            cmd.add(sourceFile);
        } else {
            cmd.addAll(List.of("-cp", System.getProperty("java.class.path"), AotCommandLineErgonomicsExample.class.getName()));
        }
        cmd.add("child");
        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            lines = reader.lines().filter(l -> !l.isBlank()).toList();
        }
        if (process.waitFor() != 0 || lines.isEmpty()) {
            throw new IllegalStateException("processo filho falhou: " + lines);
        }
        return lines;
    }
}
