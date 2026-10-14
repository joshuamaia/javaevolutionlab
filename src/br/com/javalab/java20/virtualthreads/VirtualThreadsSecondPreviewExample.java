package br.com.javalab.java20.virtualthreads;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java 20 - Virtual Threads, segunda Preview (JEP 436)
 *
 * <p>Status: Segunda Preview no Java 20. Histórico: 1a Preview no 19 (JEP 425); definitivo no 21 (JEP 444).
 * O Java 20 trouxe poucas mudanças de API (APIs como {@code Thread.join(Duration)} e
 * {@code ExecutorService.close()} tornaram-se permanentes). Este código usa a API que permaneceu
 * igual e compila no JDK 25 sem flags (no JDK 20 exigia {@code --enable-preview --source 20}).</p>
 *
 * <p>Foco deste exemplo (diferente do Java 19):</p>
 * <ul>
 *   <li>{@link Thread.Builder} reutilizável com nomes numerados ({@code name("worker-", 0)})
 *       e {@link ThreadFactory} de virtual threads;</li>
 *   <li>{@link ThreadLocal} funciona em virtual threads (cada uma tem a sua cópia), mas com milhões
 *       de threads o custo de memória cresce - motivação para Scoped Values;</li>
 *   <li>milhares de tarefas bloqueantes ({@code sleep}) rodando sobre poucas carrier threads.</li>
 * </ul>
 * <p>(No Java 20 existia {@code Thread.Builder.allowSetThreadLocals(false)}, removido no 21.)</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java20/virtualthreads/VirtualThreadsSecondPreviewExample.java
 * </pre>
 */
public class VirtualThreadsSecondPreviewExample {

    private static final ThreadLocal<String> CURRENT_REQUEST = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Thread.Builder reutilizável com contador de nomes ===");
        Thread.Builder builder = Thread.ofVirtual().name("worker-", 0);
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            workers.add(builder.start(() -> System.out.println("Olá de " + Thread.currentThread().getName())));
        }
        for (Thread worker : workers) {
            worker.join(Duration.ofSeconds(1));
        }

        System.out.println();
        System.out.println("=== ThreadLocal: cada virtual thread tem sua própria cópia ===");
        ThreadFactory factory = Thread.ofVirtual().name("request-", 100).factory();
        try (ExecutorService executor = Executors.newThreadPerTaskExecutor(factory)) {
            for (String customer : List.of("Ana", "Bruno", "Carla")) {
                executor.submit(() -> {
                    CURRENT_REQUEST.set("pedido de " + customer);
                    Thread.sleep(50);
                    System.out.println(Thread.currentThread().getName() + " vê: " + CURRENT_REQUEST.get());
                    return null;
                });
            }
        }
        System.out.println("Na thread main: " + CURRENT_REQUEST.get());

        System.out.println();
        System.out.println("=== 10.000 tarefas que dormem 200 ms ===");
        Set<String> carriers = ConcurrentHashMap.newKeySet();
        AtomicInteger done = new AtomicInteger();
        long start = System.nanoTime();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                executor.submit(() -> {
                    carriers.add(carrierName());
                    Thread.sleep(200);
                    done.incrementAndGet();
                    return null;
                });
            }
        }
        long millis = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("%d tarefas concluídas em %d ms usando ~%d carrier threads (CPUs: %d)%n",
                done.get(), millis, carriers.size(), Runtime.getRuntime().availableProcessors());
    }

    /** O toString de uma virtual thread montada mostra o carrier: VirtualThread[#id]/runnable@ForkJoinPool-1-worker-3 */
    private static String carrierName() {
        String text = Thread.currentThread().toString();
        int at = text.indexOf('@');
        return at >= 0 ? text.substring(at + 1) : "?";
    }
}
