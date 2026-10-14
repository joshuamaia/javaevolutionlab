package br.com.javalab.java10.container;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.List;

import com.sun.management.HotSpotDiagnosticMXBean;

/**
 * Java 10 - Reconhecimento de contêineres (Docker) pela JVM (JDK-8146115)
 *
 * <p>Status: Produto (JVM). Sem JEP própria; também foi portado para o 8u191.</p>
 *
 * <p>O que é: no Linux, a JVM passou a ler os limites de CPU e memória do <b>cgroup</b> do
 * contêiner ({@code -XX:+UseContainerSupport}, ligado por padrão) em vez dos números da
 * máquina hospedeira. Vieram também as flags {@code -XX:ActiveProcessorCount},
 * {@code -XX:InitialRAMPercentage}, {@code -XX:MaxRAMPercentage} e
 * {@code -XX:MinRAMPercentage}.</p>
 * <p>Benefício: tamanho de heap, número de threads de GC/JIT e
 * {@code Runtime.availableProcessors()} coerentes com o contêiner — fim dos
 * "OOMKilled" causados por uma JVM que achava ter a RAM inteira do host.</p>
 * <p>Antes: a JVM via 64 CPUs e 256 GB do host mesmo num contêiner com 1 CPU e 512 MB.</p>
 *
 * <p>Comandos para experimentar (Linux/Docker):</p>
 * <pre>
 * docker run --rm --cpus=2 -m 512m eclipse-temurin:25 java -XshowSettings:system -version
 * docker run --rm --cpus=2 -m 512m eclipse-temurin:25 java -XX:MaxRAMPercentage=75 -XX:+PrintFlagsFinal -version | grep MaxHeapSize
 * java -Xlog:os+container=info -version
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/container/ContainerAwarenessExample.java
 * </pre>
 */
public class ContainerAwarenessExample {

    public static void main(String[] args) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("=== O que a JVM enxerga ===");
        System.out.println("Sistema operacional   : " + System.getProperty("os.name"));
        System.out.println("availableProcessors() : " + runtime.availableProcessors());
        System.out.println("maxMemory() (heap máx): " + runtime.maxMemory() / (1024 * 1024) + " MB");

        System.out.println();
        System.out.println("=== Flags relacionadas (HotSpotDiagnosticMXBean) ===");
        HotSpotDiagnosticMXBean hotSpot = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        for (String flag : List.of("UseContainerSupport", "ActiveProcessorCount", "MaxRAMPercentage",
                "InitialRAMPercentage", "MinRAMPercentage")) {
            try {
                System.out.printf("  %-22s = %s%n", flag, hotSpot.getVMOption(flag).getValue());
            } catch (IllegalArgumentException e) {
                System.out.printf("  %-22s = (não existe nesta plataforma; só Linux)%n", flag);
            }
        }

        System.out.println();
        System.out.println("=== Simulando um contêiner pequeno: -XX:ActiveProcessorCount=2 -XX:MaxRAMPercentage=5 ===");
        String java = ProcessHandle.current().info().command().orElse("java");
        Process process = new ProcessBuilder(java, "-XX:ActiveProcessorCount=2", "-XX:MaxRAMPercentage=5",
                "-XX:+PrintFlagsFinal", "-version").redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), Charset.defaultCharset());
        process.waitFor();
        for (String line : output.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("int ActiveProcessorCount") || trimmed.startsWith("size_t MaxHeapSize")
                    || trimmed.startsWith("uint ParallelGCThreads") || trimmed.contains(" CICompilerCount ")) {
                System.out.println("  " + trimmed.replaceAll("\\s+", " "));
            }
        }
        System.out.println("Heap, threads de GC e de JIT foram dimensionados para 2 CPUs e 5% da RAM.");
    }
}
