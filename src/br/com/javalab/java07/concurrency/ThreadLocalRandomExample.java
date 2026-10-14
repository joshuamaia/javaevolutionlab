package br.com.javalab.java07.concurrency;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Java 7 - ThreadLocalRandom (JSR 166y)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: gerador de números aleatórios isolado por thread, obtido com
 * {@code ThreadLocalRandom.current()}; oferece {@code nextInt(origin, bound)}.</p>
 * <p>Benefício: sem contenção entre threads (um {@link Random} compartilhado usa CAS
 * em um AtomicLong interno e degrada sob concorrência).</p>
 * <p>Antes: um {@code Random} compartilhado ou um {@code ThreadLocal<Random>} manual.</p>
 * <p>Cuidado: nunca guarde a instância em campo nem compartilhe entre threads;
 * chame {@code current()} na thread que vai usar.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/concurrency/ThreadLocalRandomExample.java
 * </pre>
 */
public class ThreadLocalRandomExample {

    private static final int THREADS = 4;
    private static final int DRAWS_PER_THREAD = 2_000_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Intervalos com nextInt(origin, bound) ===");
        for (int i = 0; i < 5; i++) {
            int discountPercent = ThreadLocalRandom.current().nextInt(5, 31); // 5..30
            System.out.println("Cupom gerado: " + discountPercent + "% de desconto");
        }

        System.out.println();
        System.out.println("=== Random compartilhado vs ThreadLocalRandom ===");
        final Random shared = new Random();
        long sharedMs = run(new Runnable() {
            @Override
            public void run() {
                long acc = 0;
                for (int i = 0; i < DRAWS_PER_THREAD; i++) {
                    acc += shared.nextInt(100);
                }
                SINK.addAndGet(acc);
            }
        });
        long localMs = run(new Runnable() {
            @Override
            public void run() {
                long acc = 0;
                for (int i = 0; i < DRAWS_PER_THREAD; i++) {
                    acc += ThreadLocalRandom.current().nextInt(100);
                }
                SINK.addAndGet(acc);
            }
        });
        System.out.println("Random compartilhado: " + sharedMs + " ms");
        System.out.println("ThreadLocalRandom:    " + localMs + " ms");
        System.out.println("(medição ilustrativa; a diferença cresce com o número de threads)");
    }

    private static final AtomicLong SINK = new AtomicLong();

    private static long run(Runnable task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        long start = System.nanoTime();
        for (int i = 0; i < THREADS; i++) {
            executor.execute(task);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        return (System.nanoTime() - start) / 1_000_000;
    }
}
