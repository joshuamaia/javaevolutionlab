package br.com.javalab.java08.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * Java 8 - LongAdder e LongAccumulator (JEP 155 - Concurrency Updates)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: contadores que espalham as atualizações em várias células internas
 * (striping) e só somam tudo ao ler ({@code sum()}). {@code LongAccumulator} generaliza
 * para qualquer operação associativa (máximo, mínimo...).</p>
 * <p>Benefício: muito menos contenção que {@code AtomicLong} quando muitas threads
 * incrementam ao mesmo tempo (métricas, contadores de requisições).</p>
 * <p>Custo: mais memória e {@code sum()} não é um snapshot atômico; para sequências
 * (ex.: gerar IDs) continue usando AtomicLong.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/concurrency/LongAdderExample.java
 * </pre>
 */
public class LongAdderExample {

    private static final int THREADS = 8;
    private static final int INCREMENTS = 2_000_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== AtomicLong vs LongAdder (" + THREADS + " threads x " + INCREMENTS + ") ===");
        for (int round = 1; round <= 2; round++) {
            AtomicLong atomic = new AtomicLong();
            long atomicMs = measure(atomic::incrementAndGet);
            LongAdder adder = new LongAdder();
            long adderMs = measure(adder::increment);
            System.out.printf("Rodada %d: AtomicLong=%d (%d ms) | LongAdder=%d (%d ms)%n",
                    round, atomic.get(), atomicMs, adder.sum(), adderMs);
        }

        System.out.println();
        System.out.println("=== LongAccumulator: maior tempo de resposta ===");
        LongAccumulator maxLatency = new LongAccumulator(Long::max, Long.MIN_VALUE);
        long[] latencies = {120, 45, 300, 87, 210};
        for (long l : latencies) {
            maxLatency.accumulate(l);
        }
        System.out.println("Maior latência: " + maxLatency.get() + " ms");

        System.out.println();
        System.out.println("=== Outras operações do LongAdder ===");
        LongAdder requests = new LongAdder();
        requests.add(10);
        requests.decrement();
        System.out.println("sum=" + requests.sum() + " | sumThenReset=" + requests.sumThenReset()
                + " | após reset=" + requests.sum());
    }

    private static long measure(Runnable increment) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        long start = System.nanoTime();
        for (int t = 0; t < THREADS; t++) {
            executor.execute(() -> {
                for (int i = 0; i < INCREMENTS; i++) {
                    increment.run();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        return (System.nanoTime() - start) / 1_000_000;
    }
}
