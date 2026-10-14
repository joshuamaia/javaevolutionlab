package br.com.javalab.java12.jvm;

import com.sun.management.HotSpotDiagnosticMXBean;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Java 12 - Melhorias de JVM: Shenandoah (JEP 189), Default CDS Archives (JEP 341),
 * G1 Abortable Mixed Collections (JEP 344) e G1 Promptly Return Unused Memory (JEP 346)
 *
 * <p>Status: Shenandoah era Experimental no Java 12 (Produto no Java 15, JEP 379).
 * Os demais itens sao Produto (JVM) desde o Java 12.</p>
 *
 * <p>O que e:</p>
 * <ul>
 *   <li>JEP 189: GC de pausas curtas que compacta concorrentemente com a aplicacao.
 *       No Java 12: {@code -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC}.
 *       Atencao: alguns builds (ex.: Oracle JDK) nao incluem o Shenandoah.</li>
 *   <li>JEP 341: o JDK passa a vir com um arquivo CDS (Class Data Sharing) padrao
 *       (lib/server/classes.jsa), ligado por padrao: classes do JDK ja "pre-processadas"
 *       aceleram a inicializacao. Desligar: {@code -Xshare:off}.</li>
 *   <li>JEP 344: coletas mistas do G1 podem ser abortadas para cumprir a meta de pausa
 *       ({@code -XX:MaxGCPauseMillis}). Nao ha flag; e automatico.</li>
 *   <li>JEP 346: G1 devolve memoria nao usada ao SO quando ocioso, via coleta periodica:
 *       {@code -XX:G1PeriodicGCInterval=<ms>} (0 = desligado, padrao).</li>
 * </ul>
 * <p>Beneficio: inicializacao mais rapida e menor consumo de memoria sem mudar codigo.</p>
 *
 * <p>Este exemplo observa a JVM atual e relanca o executavel "java" com flags diferentes
 * (somente "-version"), medindo o tempo de inicializacao com e sem CDS.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java12/jvm/GcAndCdsObservationExample.java
 * java -Xlog:cds -version
 * java -XX:+UseG1GC -XX:G1PeriodicGCInterval=5000 -Xlog:gc -version
 * </pre>
 */
public class GcAndCdsObservationExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== JVM atual ===");
        System.out.println("java.vm.info = " + System.getProperty("java.vm.info")
                + "  (\"sharing\" indica CDS ativo)");
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("Coletor: " + gc.getName());
        }
        HotSpotDiagnosticMXBean hotspot = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        printOption(hotspot, "G1PeriodicGCInterval");
        printOption(hotspot, "MaxGCPauseMillis");

        String java = ProcessHandle.current().info().command().orElse("java");

        System.out.println("=== JEP 341: inicializacao com e sem CDS (5 execucoes cada) ===");
        System.out.println("Com CDS padrao (-Xshare:auto): " + averageMillis(java, "-Xshare:auto") + " ms");
        System.out.println("Sem CDS        (-Xshare:off) : " + averageMillis(java, "-Xshare:off") + " ms");

        System.out.println("=== JEP 189: Shenandoah disponivel neste build? ===");
        int exit = run(java, "-XX:+UnlockExperimentalVMOptions", "-XX:+UseShenandoahGC", "-version");
        System.out.println(exit == 0
                ? "Sim: java -XX:+UseShenandoahGC funciona neste JDK"
                : "Nao: este build do JDK nao inclui o Shenandoah (comum no Oracle JDK)");
    }

    private static void printOption(HotSpotDiagnosticMXBean hotspot, String name) {
        try {
            System.out.println("-XX:" + name + " = " + hotspot.getVMOption(name).getValue());
        } catch (IllegalArgumentException e) {
            System.out.println("-XX:" + name + " nao existe nesta JVM");
        }
    }

    private static long averageMillis(String java, String flag) throws IOException, InterruptedException {
        long total = 0;
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            run(java, flag, "-version");
            total += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        }
        return total / 5;
    }

    private static int run(String java, String... flags) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(java);
        command.addAll(List.of(flags));
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start();
        return process.waitFor();
    }
}
