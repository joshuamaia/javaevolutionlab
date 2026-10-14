package br.com.javalab.java11.gc;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Java 11 - Epsilon: um coletor de lixo que não coleta (JEP 318)
 *
 * <p>Status: Experimental (continua experimental no JDK 25).</p>
 *
 * <p>O que é: um GC "no-op" que só aloca memória e nunca a recupera. Quando o heap acaba,
 * a JVM termina com {@code OutOfMemoryError}.</p>
 * <p>Benefício: medir o custo real do GC em benchmarks (sem barreiras nem pausas), testar a
 * pressão de alocação de um código, e jobs curtíssimos que terminam antes de encher o heap.</p>
 *
 * <p>Exemplo de observação: mostra o coletor atual e relança {@code java -version} com
 * Epsilon e {@code -Xlog:gc} para ver o coletor sendo inicializado.</p>
 *
 * <p>Flags para experimentar:</p>
 * <pre>
 * java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xlog:gc src/br/com/javalab/java11/gc/EpsilonGcExample.java
 * java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xmx150m src/br/com/javalab/java11/gc/EpsilonGcExample.java
 *   (heap pequeno: a JVM termina com "Terminating due to java.lang.OutOfMemoryError: Java heap space")
 * </pre>
 * <p>ZGC (JEP 333) também chegou no Java 11 como Experimental (só Linux/x64):
 * {@code -XX:+UnlockExperimentalVMOptions -XX:+UseZGC}. Virou produto no Java 15 (JEP 377)
 * e geracional no 21 (JEP 439).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/gc/EpsilonGcExample.java
 * </pre>
 */
public class EpsilonGcExample {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("=== Coletor desta JVM ===");
        List<GarbageCollectorMXBean> collectors = ManagementFactory.getGarbageCollectorMXBeans();
        collectors.forEach(gc -> System.out.println("  " + gc.getName()));
        boolean epsilon = collectors.stream().anyMatch(gc -> gc.getName().startsWith("Epsilon"));
        System.out.println(epsilon ? "Rodando com Epsilon: nada será coletado!" : "Não é Epsilon.");

        System.out.println();
        System.out.println("=== Alocando 200 MB de lixo (em blocos de 1 MB) ===");
        Runtime runtime = Runtime.getRuntime();
        try {
            long checksum = 0;
            for (int i = 0; i < 200; i++) {
                byte[] garbage = new byte[1024 * 1024];
                checksum += garbage.length;
            }
            System.out.println("Alocados " + checksum / (1024 * 1024) + " MB; heap usado agora: "
                    + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB"
                    + (epsilon ? " (com Epsilon, nunca diminui)" : " (este coletor pode recuperar o lixo a qualquer momento)"));
        } catch (OutOfMemoryError e) {
            System.out.println("OutOfMemoryError: Epsilon não recupera memória e o heap acabou.");
        }

        System.out.println();
        System.out.println("=== Relançando: java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xlog:gc -version ===");
        String java = ProcessHandle.current().info().command().orElse("java");
        Process process = new ProcessBuilder(java, "-XX:+UnlockExperimentalVMOptions", "-XX:+UseEpsilonGC",
                "-Xmx128m", "-Xlog:gc", "-version").redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), Charset.defaultCharset());
        process.waitFor();
        output.lines().filter(line -> line.contains("[gc]")).forEach(line -> System.out.println("  " + line));
    }
}
