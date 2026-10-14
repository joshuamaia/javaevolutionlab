package br.com.javalab.java19.virtualthreads;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Java 19 - Virtual Threads, Preview (JEP 425)
 *
 * <p>Status: Preview no Java 19. Depois: segunda Preview no 20 (JEP 436) e definitivo no 21 (JEP 444).
 * Este código usa a API que permaneceu igual e compila no JDK 25 sem flags
 * (no JDK 19 exigia {@code --enable-preview --source 19}).</p>
 *
 * <p>O que é: threads leves gerenciadas pela JVM, não pelo SO. Muitas virtual threads são
 * multiplexadas sobre poucas threads de plataforma ("carrier threads"); quando uma virtual thread
 * bloqueia em IO ou {@code sleep}, ela é desmontada do carrier, que fica livre para outra.</p>
 * <p>Benefício: o estilo simples "uma thread por requisição" passa a escalar para milhões de
 * tarefas concorrentes, sem a complexidade de código reativo/assíncrono.</p>
 * <p>Antes: pools limitados de threads de plataforma (cada uma ~1 MB de pilha reservada e uma
 * thread do SO) ou {@code CompletableFuture}/frameworks reativos.</p>
 *
 * <p>APIs introduzidas no Java 19: {@code Thread.ofVirtual()}, {@code Thread.ofPlatform()},
 * {@code Thread.startVirtualThread(Runnable)}, {@code Thread.isVirtual()},
 * {@code Executors.newVirtualThreadPerTaskExecutor()} e {@code ExecutorService} como
 * {@link AutoCloseable} (o {@code close()} espera as tarefas terminarem).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java19/virtualthreads/VirtualThreadsPreviewExample.java
 * </pre>
 */
public class VirtualThreadsPreviewExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Thread de plataforma (tradicional) ===");
        Thread platform = Thread.ofPlatform().name("relatorio").start(() ->
                System.out.println("Executando em " + Thread.currentThread() + " | virtual? "
                        + Thread.currentThread().isVirtual()));
        platform.join();

        System.out.println();
        System.out.println("=== Thread.ofVirtual() e Thread.startVirtualThread() ===");
        Thread virtual = Thread.ofVirtual().name("pedido-1").start(() ->
                System.out.println("Executando em " + Thread.currentThread() + " | virtual? "
                        + Thread.currentThread().isVirtual()));
        virtual.join();
        Thread quick = Thread.startVirtualThread(() -> System.out.println("Virtual thread sem nome: '"
                + Thread.currentThread().getName() + "' (virtual threads não têm nome por padrão)"));
        quick.join();
        System.out.println("Virtual threads são sempre daemon? " + virtual.isDaemon());

        System.out.println();
        System.out.println("=== Executor: uma virtual thread por tarefa ===");
        long start = System.nanoTime();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> customer = executor.submit(() -> slowCall("cliente Ana", 300));
            Future<String> stock = executor.submit(() -> slowCall("estoque OK", 300));
            Future<String> freight = executor.submit(() -> slowCall("frete R$ 19,90", 300));
            System.out.println(customer.get() + " | " + stock.get() + " | " + freight.get());
        } // close() espera todas as tarefas
        System.out.printf("3 chamadas de 300 ms em paralelo: %d ms%n", (System.nanoTime() - start) / 1_000_000);
    }

    private static String slowCall(String result, long millis) throws InterruptedException {
        Thread.sleep(Duration.ofMillis(millis)); // bloqueio barato: libera a carrier thread
        return result;
    }
}
