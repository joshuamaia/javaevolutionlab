package br.com.javalab.java09.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 9 - G1 como Garbage Collector padrão (JEP 248)
 *
 * <p>Status: Produto (JVM).</p>
 *
 * <p>O que é: em máquinas "de servidor" (2+ CPUs e 2+ GB de RAM) o coletor padrão passou
 * de Parallel GC para G1 (Garbage-First), que divide o heap em regiões e busca pausas
 * curtas e previsíveis ({@code -XX:MaxGCPauseMillis}, padrão 200 ms).</p>
 * <p>Benefício: menor latência para a maioria das aplicações sem ajuste manual.</p>
 * <p>Antes (Java 8): Parallel GC (foco em throughput, pausas maiores) era o padrão.</p>
 *
 * <p>Exemplo de observação: lista os coletores ativos via {@code GarbageCollectorMXBean}.
 * Em máquinas pequenas (1 CPU ou &lt; 1792 MB) a JVM escolhe Serial GC.</p>
 *
 * <p>Flags para experimentar:</p>
 * <pre>
 * java -XX:+UseG1GC       src/br/com/javalab/java09/gc/G1DefaultGcExample.java
 * java -XX:+UseParallelGC src/br/com/javalab/java09/gc/G1DefaultGcExample.java   (padrão do Java 8)
 * java -XX:+UseSerialGC   src/br/com/javalab/java09/gc/G1DefaultGcExample.java
 * java -Xlog:gc           src/br/com/javalab/java09/gc/G1DefaultGcExample.java   (mostra "Using G1")
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/gc/G1DefaultGcExample.java
 * </pre>
 */
public class G1DefaultGcExample {

    public static void main(String[] args) {
        System.out.println("=== Ambiente ===");
        System.out.println("CPUs disponíveis : " + Runtime.getRuntime().availableProcessors());
        System.out.println("Heap máximo      : " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");

        System.out.println();
        System.out.println("=== Coletores ativos (GarbageCollectorMXBean) ===");
        List<GarbageCollectorMXBean> collectors = ManagementFactory.getGarbageCollectorMXBeans();
        boolean usingG1 = false;
        for (GarbageCollectorMXBean gc : collectors) {
            System.out.println("  " + gc.getName());
            usingG1 |= gc.getName().startsWith("G1");
        }
        System.out.println(usingG1 ? "G1 está em uso (padrão desde o Java 9)." : "G1 não está em uso nesta JVM.");

        System.out.println();
        System.out.println("=== Gerando lixo (pedidos temporários) ===");
        long checksum = 0;
        for (int round = 0; round < 50; round++) {
            List<byte[]> temporaryOrders = new ArrayList<>();
            for (int i = 0; i < 1_000; i++) {
                temporaryOrders.add(new byte[10_000]);
            }
            checksum += temporaryOrders.size();
        }
        System.out.println("Objetos criados: " + checksum);
        for (GarbageCollectorMXBean gc : collectors) {
            System.out.printf("  %-25s coletas=%d tempo=%d ms%n", gc.getName(), gc.getCollectionCount(), gc.getCollectionTime());
        }
    }
}
