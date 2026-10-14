package br.com.javalab.java10.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import com.sun.management.HotSpotDiagnosticMXBean;

/**
 * Java 10 - Melhorias de GC: Parallel Full GC para G1 (JEP 307) e GC Interface (JEP 304)
 *
 * <p>Status: Produto (JVM).</p>
 *
 * <p>O que é:</p>
 * <ul>
 *   <li>JEP 307: o Full GC do G1 (último recurso quando a coleta concorrente não dá conta,
 *       ou em {@code System.gc()}) passou a usar <b>várias threads</b>
 *       ({@code -XX:ParallelGCThreads}); antes era single-thread e podia pausar por segundos.</li>
 *   <li>JEP 304: interface interna e limpa entre os coletores e o resto da HotSpot. Não muda
 *       nada para o desenvolvedor, mas facilitou a chegada de Epsilon (11), ZGC (11) e
 *       Shenandoah (12).</li>
 * </ul>
 * <p>Benefício: pausas de Full GC muito menores no G1 em máquinas com vários núcleos.</p>
 *
 * <p>Exemplo de observação: mostra os coletores e as flags ativas, gera lixo, força um
 * {@code System.gc()} (que no G1 é um Full GC paralelo) e mostra contagens/tempos.</p>
 *
 * <p>Flags para experimentar:</p>
 * <pre>
 * java -XX:+UseG1GC -Xlog:gc src/br/com/javalab/java10/gc/G1ParallelFullGcExample.java
 * java -XX:+UseG1GC -Xlog:gc*,gc+task=debug -XX:ParallelGCThreads=1 src/.../G1ParallelFullGcExample.java
 * java -XX:+UseG1GC -Xlog:gc -XX:ParallelGCThreads=8 src/.../G1ParallelFullGcExample.java
 *   (compare o tempo da linha "Pause Full (System.gc())")
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/gc/G1ParallelFullGcExample.java
 * </pre>
 */
public class G1ParallelFullGcExample {

    public static void main(String[] args) {
        HotSpotDiagnosticMXBean hotSpot = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);

        System.out.println("=== Coletor e flags ativas ===");
        for (String flag : List.of("UseG1GC", "UseParallelGC", "UseSerialGC", "ParallelGCThreads", "ConcGCThreads",
                "ExplicitGCInvokesConcurrent")) {
            System.out.printf("  %-28s = %s%n", flag, hotSpot.getVMOption(flag).getValue());
        }
        List<GarbageCollectorMXBean> collectors = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gc : collectors) {
            System.out.println("  MXBean: " + gc.getName());
        }

        System.out.println();
        System.out.println("=== Gerando objetos que sobrevivem (cache de pedidos) ===");
        List<byte[]> cache = new ArrayList<>();
        for (int i = 0; i < 2_000; i++) {
            cache.add(new byte[50_000]); // ~100 MB que ficam vivos
        }
        print("Após alocação", collectors);

        System.out.println();
        System.out.println("=== System.gc() -> Full GC (paralelo no G1 desde o Java 10) ===");
        long start = System.nanoTime();
        System.gc();
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;
        print("Após System.gc()", collectors);
        System.out.println("Duração observada de System.gc(): " + elapsedMillis + " ms com "
                + hotSpot.getVMOption("ParallelGCThreads").getValue() + " threads paralelas");
        System.out.println("Itens no cache: " + cache.size());
    }

    private static void print(String label, List<GarbageCollectorMXBean> collectors) {
        System.out.println(label + ":");
        for (GarbageCollectorMXBean gc : collectors) {
            System.out.printf("  %-22s coletas=%d tempo=%d ms%n", gc.getName(), gc.getCollectionCount(), gc.getCollectionTime());
        }
    }
}
