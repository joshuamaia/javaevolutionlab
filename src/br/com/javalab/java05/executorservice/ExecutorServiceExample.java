package br.com.javalab.java05.executorservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Java 5 - ExecutorService / Executor Framework (JSR 166)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: separa a SUBMISSÃO de tarefas da forma de EXECUTÁ-LAS. Pools de threads
 * prontos via {@code Executors} ({@code newFixedThreadPool},
 * {@code newCachedThreadPool}, {@code newSingleThreadExecutor},
 * {@code newScheduledThreadPool}), com ciclo de vida ({@code shutdown},
 * {@code awaitTermination}).</p>
 * <p>Benefício: reaproveita threads (criar thread é caro), limita a concorrência,
 * centraliza políticas (fila, nomes, agendamento) e facilita encerrar tudo.</p>
 * <p>Antes: {@code new Thread(runnable).start()} para cada tarefa, {@code join()} manual
 * e {@code java.util.Timer} para agendamentos.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/executorservice/ExecutorServiceExample.java
 * </pre>
 */
public class ExecutorServiceExample {

    /** Tarefa simples: "processa" uma nota fiscal. */
    static class InvoiceTask implements Runnable {
        private final int invoice;

        InvoiceTask(int invoice) {
            this.invoice = invoice;
        }

        public void run() { // sem @Override: Runnable é interface (Java 5)
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("NF-" + invoice + " processada por " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Abordagem antiga: uma Thread por tarefa ===");
        Thread[] threads = new Thread[6];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new InvoiceTask(i + 1), "manual-" + (i + 1));
            threads[i].start();   // 6 tarefas = 6 threads criadas e descartadas
        }
        for (Thread t : threads) {
            t.join();
        }

        System.out.println();
        System.out.println("=== Abordagem nova: pool fixo com 2 threads ===");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        for (int i = 1; i <= 6; i++) {
            pool.execute(new InvoiceTask(100 + i)); // 6 tarefas reaproveitando 2 threads
        }
        pool.shutdown();          // não aceita novas tarefas; termina as enfileiradas
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Pool terminou? " + finished + " / isShutdown=" + pool.isShutdown());

        System.out.println();
        System.out.println("=== ScheduledExecutorService (substitui java.util.Timer) ===");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final long start = System.nanoTime();
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                System.out.println("Heartbeat do servidor em ~" + ms + " ms");
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                System.out.println("Relatório agendado (atraso de 250 ms) gerado");
            }
        }, 250, TimeUnit.MILLISECONDS);
        Thread.sleep(380);
        scheduler.shutdownNow();  // cancela o heartbeat periódico
        scheduler.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Agendador encerrado.");
    }
}
