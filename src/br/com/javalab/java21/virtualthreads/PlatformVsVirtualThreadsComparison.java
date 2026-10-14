package br.com.javalab.java21.virtualthreads;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Java 21 - Comparação: threads de plataforma (tradicionais) vs virtual threads (JEP 444)
 *
 * <p>Status: Definitivo (virtual threads). Histórico: Preview no Java 19 (JEP 425) e 20 (JEP 436).</p>
 *
 * <p>O que é: a mesma carga - 10.000 tarefas que bloqueiam 100 ms (simulando uma chamada HTTP ou
 * consulta a banco) - executada de duas formas:</p>
 * <ul>
 *   <li><b>Pool fixo de 200 threads de plataforma</b>: cada thread do SO fica parada durante o
 *       {@code sleep}; no máximo 200 tarefas progridem ao mesmo tempo, então o tempo mínimo é
 *       10.000 / 200 x 100 ms = 5 s. Aumentar o pool custa memória (pilha de ~1 MB reservada por
 *       thread) e trocas de contexto do SO.</li>
 *   <li><b>Uma virtual thread por tarefa</b>: as 10.000 tarefas dormem ao mesmo tempo; as poucas
 *       carrier threads ficam livres durante o bloqueio. Tempo próximo de 100 ms + overhead.</li>
 * </ul>
 * <p>Importante: virtual threads aumentam a VAZÃO (throughput) de tarefas que esperam IO;
 * não tornam cálculos de CPU mais rápidos.</p>
 *
 * <p>Execução (JDK 25; leva ~5-6 s por causa do pool de plataforma):</p>
 * <pre>
 * java src/br/com/javalab/java21/virtualthreads/PlatformVsVirtualThreadsComparison.java
 * </pre>
 */
public class PlatformVsVirtualThreadsComparison {

    private static final int TASKS = 10_000;
    private static final int PLATFORM_POOL_SIZE = 200;
    private static final Duration IO_LATENCY = Duration.ofMillis(100);

    public static void main(String[] args) {
        System.out.printf("Carga: %,d tarefas, cada uma bloqueando %d ms%n%n", TASKS, IO_LATENCY.toMillis());

        System.out.println("=== Abordagem antiga: pool fixo de " + PLATFORM_POOL_SIZE + " threads de plataforma ===");
        long platformMillis = run(() -> Executors.newFixedThreadPool(PLATFORM_POOL_SIZE));

        System.out.println();
        System.out.println("=== Abordagem nova: uma virtual thread por tarefa ===");
        long virtualMillis = run(Executors::newVirtualThreadPerTaskExecutor);

        System.out.println();
        System.out.println("=== Resultado ===");
        System.out.printf("Plataforma: %,6d ms (mínimo teórico %,d ms)%n",
                platformMillis, TASKS / PLATFORM_POOL_SIZE * IO_LATENCY.toMillis());
        System.out.printf("Virtual   : %,6d ms (mínimo teórico %,d ms)%n", virtualMillis, IO_LATENCY.toMillis());
        System.out.printf("Virtual threads foram ~%.0fx mais rápidas nesta carga de IO.%n",
                (double) platformMillis / Math.max(1, virtualMillis));
    }

    private static long run(Supplier<ExecutorService> executorFactory) {
        AtomicInteger completed = new AtomicInteger();
        long start = System.nanoTime();
        try (ExecutorService executor = executorFactory.get()) { // close() espera todas as tarefas
            for (int i = 0; i < TASKS; i++) {
                executor.submit(() -> {
                    Thread.sleep(IO_LATENCY);
                    return completed.incrementAndGet();
                });
            }
        }
        long millis = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("%,d tarefas concluídas em %,d ms%n", completed.get(), millis);
        return millis;
    }
}
