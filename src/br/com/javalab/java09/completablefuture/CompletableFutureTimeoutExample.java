package br.com.javalab.java09.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java 9 - CompletableFuture.orTimeout e completeOnTimeout (JEP 266 - More Concurrency Updates)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code orTimeout(t, unidade)} completa o future com
 * {@code TimeoutException} se não terminar a tempo; {@code completeOnTimeout(valor, t,
 * unidade)} completa com um valor padrão. Também vieram {@code delayedExecutor},
 * {@code failedFuture} e {@code completedStage}.</p>
 * <p>Benefício: timeouts não bloqueantes, dentro do pipeline assíncrono.</p>
 * <p>Antes (Java 8): só {@code get(timeout)} (bloqueante) ou um
 * {@code ScheduledExecutorService} manual para completar o future.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/completablefuture/CompletableFutureTimeoutExample.java
 * </pre>
 */
public class CompletableFutureTimeoutExample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            System.out.println("=== orTimeout: serviço de frete lento falha em 200 ms ===");
            CompletableFuture<String> shipping = CompletableFuture
                    .supplyAsync(() -> slowService("frete calculado", 1_000), executor)
                    .orTimeout(200, TimeUnit.MILLISECONDS)
                    .exceptionally(error -> "erro: " + rootCause(error).getClass().getSimpleName());
            System.out.println("Resultado: " + shipping.join());

            System.out.println();
            System.out.println("=== completeOnTimeout: usa valor padrão ===");
            CompletableFuture<String> recommendation = CompletableFuture
                    .supplyAsync(() -> slowService("recomendação personalizada", 1_000), executor)
                    .completeOnTimeout("mais vendidos (padrão)", 200, TimeUnit.MILLISECONDS);
            System.out.println("Resultado: " + recommendation.join());

            System.out.println();
            System.out.println("=== Serviço rápido: nenhum timeout acionado ===");
            CompletableFuture<String> stock = CompletableFuture
                    .supplyAsync(() -> slowService("estoque: 42 unidades", 50), executor)
                    .orTimeout(500, TimeUnit.MILLISECONDS);
            System.out.println("Resultado: " + stock.join());

            System.out.println();
            System.out.println("=== Bônus: delayedExecutor e failedFuture ===");
            CompletableFuture<String> delayed = CompletableFuture.supplyAsync(() -> "executado após 100 ms",
                    CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS, executor));
            System.out.println(delayed.join());
            System.out.println("failedFuture concluído com erro? "
                    + CompletableFuture.failedFuture(new IllegalStateException()).isCompletedExceptionally());
        } finally {
            executor.shutdownNow();
        }
    }

    private static String slowService(String result, long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    private static Throwable rootCause(Throwable error) {
        return error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
    }
}
