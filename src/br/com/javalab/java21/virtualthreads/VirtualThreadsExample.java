package br.com.javalab.java21.virtualthreads;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java 21 - Virtual Threads (JEP 444)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 19 (JEP 425) e 20 (JEP 436).</p>
 *
 * <p>O que é: threads leves da JVM ({@link Thread#ofVirtual()},
 * {@link Executors#newVirtualThreadPerTaskExecutor()}) que tornam barato bloquear: o código
 * continua síncrono e legível, e a JVM desmonta a virtual thread do carrier enquanto ela espera.</p>
 * <p>Mudanças em relação às Previews: virtual threads agora SEMPRE suportam {@link ThreadLocal}
 * (a opção de desabilitá-las saiu) e não precisam mais de {@code --enable-preview}.</p>
 * <p>Benefício: servidores "thread-per-request" escalam para centenas de milhares de requisições
 * concorrentes sem reescrever para código reativo.</p>
 * <p>Antes: pools pequenos de threads de plataforma ou {@code CompletableFuture} encadeado.</p>
 *
 * <p>Boas práticas mostradas aqui: não fazer pool de virtual threads (crie uma por tarefa);
 * limitar recursos escassos com semáforo; preferir {@link ReentrantLock} a {@code synchronized}
 * em trechos bloqueantes longos no Java 21-23 (no 21, bloquear dentro de {@code synchronized}
 * "prende" o carrier - pinning; resolvido no Java 24 pela JEP 491). Para diagnosticar pinning
 * no Java 21: {@code java -Djdk.tracePinnedThreads=full ...}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/virtualthreads/VirtualThreadsExample.java
 * </pre>
 */
public class VirtualThreadsExample {

    record Quote(String supplier, double price) { }

    private static final ReentrantLock AUDIT_LOCK = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Cotação em vários fornecedores: uma virtual thread por chamada ===");
        List<String> suppliers = List.of("Fornecedor A", "Fornecedor B", "Fornecedor C", "Fornecedor D");
        long start = System.nanoTime();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Quote>> futures = suppliers.stream()
                    .map(supplier -> executor.submit(() -> requestQuote(supplier)))
                    .toList();
            Quote best = null;
            for (Future<Quote> future : futures) {
                Quote quote = future.get();
                System.out.println("  " + quote);
                if (best == null || quote.price() < best.price()) {
                    best = quote;
                }
            }
            System.out.println("Melhor cotação: " + best);
        }
        System.out.printf("Tempo total: %d ms (as chamadas de ~400 ms rodaram em paralelo)%n",
                (System.nanoTime() - start) / 1_000_000);

        System.out.println();
        System.out.println("=== Builder sem start: thread virtual criada e iniciada depois ===");
        Thread report = Thread.ofVirtual().name("relatorio-diario").unstarted(() ->
                System.out.println("Gerando relatório em " + Thread.currentThread()));
        System.out.println("Estado antes do start: " + report.getState());
        report.start();
        report.join();
        System.out.println("Estado depois do join: " + report.getState());
    }

    static Quote requestQuote(String supplier) throws InterruptedException {
        Thread.sleep(Duration.ofMillis(400)); // simula IO de rede
        double price = 100 + Math.abs(supplier.hashCode() % 50);
        AUDIT_LOCK.lock(); // ReentrantLock não prende o carrier enquanto espera
        try {
            return new Quote(supplier, price);
        } finally {
            AUDIT_LOCK.unlock();
        }
    }
}
