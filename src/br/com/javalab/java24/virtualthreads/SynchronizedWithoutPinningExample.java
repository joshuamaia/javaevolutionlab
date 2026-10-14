package br.com.javalab.java24.virtualthreads;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java 24 - Synchronize Virtual Threads without Pinning (JEP 491)
 *
 * <p>Status: Definitivo (mudança de implementação da JVM, sem API nova). Contexto:
 * virtual threads chegaram no Java 21 (JEP 444).</p>
 *
 * <p>O que é: até o Java 23, uma virtual thread que bloqueava (sleep, I/O, wait) DENTRO de
 * um bloco {@code synchronized} ficava "pinned" (presa) à sua thread carregadora (carrier):
 * não podia ser desmontada, e a carrier ficava ociosa e indisponível. Com poucas carriers
 * (= nº de núcleos), o paralelismo despencava. No Java 24 os monitores do
 * {@code synchronized} foram reimplementados para pertencer à virtual thread, e ela pode
 * ser desmontada mesmo segurando o monitor.</p>
 * <p>Benefício: código legado e bibliotecas com {@code synchronized} escalam com virtual
 * threads sem reescrita.</p>
 * <p>Antes: a recomendação era trocar {@code synchronized} por {@code ReentrantLock} e usar
 * {@code -Djdk.tracePinnedThreads=full} (removido no 24) para achar pontos de pinning.</p>
 *
 * <p>Exemplo de observação: N virtual threads (N = 4 x nº de núcleos), cada uma com seu
 * PRÓPRIO lock (sem disputa), dormem 100 ms dentro de {@code synchronized}. No JDK 24+ o
 * tempo total fica perto de 100 ms, igual à versão com {@code ReentrantLock}. No JDK 21-23
 * o {@code synchronized} levaria ~4 x 100 ms, pois só "nº de núcleos" threads progridem por vez.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/virtualthreads/SynchronizedWithoutPinningExample.java
 * </pre>
 * <p>Para ver o pinning, rode o mesmo arquivo com um JDK 21 ({@code java SynchronizedWithoutPinningExample.java})
 * ou observe eventos {@code jdk.VirtualThreadPinned} no JFR.</p>
 */
public class SynchronizedWithoutPinningExample {

    static final Duration WORK = Duration.ofMillis(100);

    public static void main(String[] args) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        int tasks = cores * 4;
        System.out.println("JDK " + Runtime.version().feature() + " | núcleos (carriers) = " + cores
                + " | virtual threads = " + tasks);

        System.out.println();
        System.out.println("=== Bloqueio dentro de synchronized (pinava até o Java 23) ===");
        long syncMs = run(tasks, () -> {
            Object lock = new Object();         // lock próprio: não há disputa
            synchronized (lock) {
                sleep();                         // bloqueia segurando o monitor
            }
        });
        System.out.println("Tempo: " + syncMs + " ms");

        System.out.println();
        System.out.println("=== Mesma coisa com ReentrantLock (nunca pinou) ===");
        long lockMs = run(tasks, () -> {
            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            try {
                sleep();
            } finally {
                lock.unlock();
            }
        });
        System.out.println("Tempo: " + lockMs + " ms");

        System.out.println();
        boolean pinned = syncMs > lockMs * 2;
        System.out.println(pinned
                ? "synchronized foi bem mais lento: as carriers ficaram presas (pinning)."
                : "Tempos equivalentes: synchronized não prende mais a carrier (JEP 491).");
    }

    static long run(int tasks, Runnable task) throws InterruptedException {
        long start = System.nanoTime();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < tasks; i++) {
                executor.submit(task);
            }
        } // close() espera todas terminarem
        return (System.nanoTime() - start) / 1_000_000;
    }

    static void sleep() {
        try {
            Thread.sleep(WORK);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
