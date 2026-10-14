package br.com.javalab.java25.aot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Java 25 - Ahead-of-Time Method Profiling (JEP 515)
 *
 * <p>Status: Produto (JVM). Parte do Projeto Leyden, sobre o cache AOT do Java 24 (JEP 483) e
 * o fluxo em um passo do Java 25 (JEP 514).</p>
 *
 * <p>O que é: durante o treino, além das classes carregadas/linkadas, a JVM grava no cache AOT
 * os PERFIS DE EXECUÇÃO dos métodos (quantas vezes rodaram, quais ramos e tipos foram vistos -
 * os mesmos dados que o JIT C2 coleta para otimizar). Na produção, o JIT já parte desses
 * perfis e compila os métodos quentes mais cedo, em vez de passar o início da execução
 * interpretando e coletando estatísticas.</p>
 * <p>Benefício: AQUECIMENTO (warm-up) mais rápido - a aplicação atinge o desempenho de pico
 * antes. A JEP relata ~19% menos tempo de aquecimento num benchmark de streams. Nenhuma flag
 * nova: os perfis entram automaticamente no cache criado com {@code -XX:AOTCacheOutput}.</p>
 * <p>Diferença para o exemplo da JEP 514: lá medimos STARTUP (até o fim do main); aqui
 * medimos AQUECIMENTO - o tempo dos primeiros lotes de uma operação repetida (o código quente).</p>
 *
 * <p>Exemplo de observação: o filho executa 30 lotes de um cálculo com streams e imprime o
 * tempo do 1º lote e o total; o pai roda sem cache, treina com {@code -XX:AOTCacheOutput} e roda
 * com {@code -XX:AOTCache}. Os números variam conforme CPU e carga - rode algumas vezes.
 * Medido neste projeto (JDK 25, Windows): os 5 primeiros lotes ficaram ~40% mais rápidos com o
 * cache (parte do ganho também vem das classes de streams já carregadas - JEP 483), mas o total
 * dos 30 lotes NÃO melhorou neste micro-exemplo: o ganho da JEP 515 é no início da execução, e
 * em programas tão curtos o JIT pode tomar decisões diferentes. Ganhos consistentes aparecem
 * em aplicações maiores, com treino representativo.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/aot/AotMethodProfilingExample.java
 * # manualmente; o log de compilação mostra os métodos sendo compilados mais cedo:
 * java -XX:AOTCacheOutput=app.aot -cp app.jar br.com.javalab.java25.aot.AotMethodProfilingExample child
 * java -XX:AOTCache=app.aot -XX:+PrintCompilation -cp app.jar br.com.javalab.java25.aot.AotMethodProfilingExample child
 * </pre>
 */
public class AotMethodProfilingExample {

    static final int BATCHES = 30;

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("child")) {
            warmUp();
            return;
        }
        Path dir = Files.createTempDirectory("aot-profile-demo");
        try {
            Path cache = dir.resolve("app.aot");
            System.out.println("=== Sem cache: o JIT começa sem perfis ===");
            System.out.println(runChild().getLast());

            System.out.println();
            System.out.println("=== Treino: grava classes + perfis de métodos no cache ===");
            runChild("-XX:AOTCacheOutput=" + cache, "-Xlog:aot=off", "-Xlog:cds=off");
            System.out.println("Cache: " + Files.size(cache) / 1024 / 1024 + " MB");

            System.out.println();
            System.out.println("=== Com cache: perfis disponíveis desde o início ===");
            System.out.println(runChild("-XX:AOTCache=" + cache, "-Xlog:aot=off", "-Xlog:cds=off").getLast());
        } catch (Exception e) {
            System.out.println("Relançamento indisponível (" + e.getMessage() + "). Use os comandos do Javadoc.");
        } finally {
            try (Stream<Path> files = Files.walk(dir)) {
                files.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
    }

    /** Código "quente": calcula descontos de pedidos repetidamente. */
    static void warmUp() {
        long start = System.nanoTime();
        long firstBatchUs = 0;
        long firstFiveUs = 0;
        double checksum = 0;
        for (int batch = 0; batch < BATCHES; batch++) {
            long t0 = System.nanoTime();
            checksum += IntStream.range(0, 200_000)
                    .mapToDouble(i -> orderTotal(i))
                    .filter(total -> total > 50)
                    .map(AotMethodProfilingExample::applyDiscount)
                    .sum();
            if (batch == 0) firstBatchUs = (System.nanoTime() - t0) / 1_000;
            if (batch == 4) firstFiveUs = (System.nanoTime() - start) / 1_000;
        }
        long totalMs = (System.nanoTime() - start) / 1_000_000;
        // saída em ASCII: o pai lê pela pipe (codificação pode diferir do console)
        System.out.printf("1o lote: %d us | 5 primeiros: %d us | %d lotes: %d ms (checksum %.0f)%n",
                firstBatchUs, firstFiveUs, BATCHES, totalMs, checksum);
    }

    static double orderTotal(int i) {
        return (i % 97) * 1.5 + (i % 13);
    }

    static double applyDiscount(double total) {
        return total > 100 ? total * 0.9 : total * 0.95;
    }

    static List<String> runChild(String... flags) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(ProcessHandle.current().info().command().orElse("java"));
        cmd.addAll(List.of(flags));
        String sourceFile = System.getProperty("jdk.launcher.sourcefile"); // modo código-fonte
        if (sourceFile != null) {
            cmd.add(sourceFile);
        } else {
            cmd.addAll(List.of("-cp", System.getProperty("java.class.path"), AotMethodProfilingExample.class.getName()));
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
