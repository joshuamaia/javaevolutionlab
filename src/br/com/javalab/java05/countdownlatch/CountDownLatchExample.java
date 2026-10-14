package br.com.javalab.java05.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Java 5 - CountDownLatch (JSR 166)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: uma "trava" com contador. Threads chamam {@code await()} e ficam
 * esperando até que o contador chegue a zero via {@code countDown()}. É de uso único
 * (não pode ser reiniciada; para isso existe {@code CyclicBarrier}).</p>
 * <p>Benefício: coordenar "esperar N eventos" (ex.: serviços inicializados) ou dar a
 * largada simultânea para várias threads.</p>
 * <p>Antes: {@code join()} em cada thread (só serve se a thread termina) ou contador +
 * {@code wait/notifyAll}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/countdownlatch/CountDownLatchExample.java
 * </pre>
 */
public class CountDownLatchExample {

    public static void main(String[] args) throws InterruptedException {
        final String[] services = {"banco-de-dados", "cache", "fila-de-mensagens"};
        final long[] startupMs = {150, 50, 100};

        final CountDownLatch startSignal = new CountDownLatch(1);             // largada
        final CountDownLatch ready = new CountDownLatch(services.length);     // N eventos

        ExecutorService pool = Executors.newFixedThreadPool(services.length);
        for (int i = 0; i < services.length; i++) {
            final String service = services[i];
            final long delay = startupMs[i];
            pool.execute(new Runnable() {
                public void run() {
                    try {
                        startSignal.await();     // todos começam juntos
                        Thread.sleep(delay);     // simula inicialização
                        System.out.println("Serviço pronto: " + service);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        ready.countDown();       // no finally: conta mesmo em caso de erro
                    }
                }
            });
        }

        System.out.println("=== Dando a largada para a inicialização dos serviços ===");
        startSignal.countDown();
        boolean allReady = ready.await(5, TimeUnit.SECONDS); // espera com timeout
        System.out.println("Todos prontos? " + allReady + " (contador=" + ready.getCount() + ")");
        System.out.println("Aplicação aceitando requisições.");

        System.out.println();
        System.out.println("=== Latch de uso único ===");
        ready.countDown(); // não tem efeito: já está em zero
        System.out.println("Contador continua " + ready.getCount() + "; await() agora retorna na hora: "
                + ready.await(1, TimeUnit.MILLISECONDS));

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }
}
