package br.com.javalab.java12.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Java 12 - CompletableFuture.exceptionallyCompose / exceptionallyAsync
 *
 * <p>Status: Definitivo (APIs novas na interface CompletionStage, sem JEP).</p>
 *
 * <p>O que e: {@code exceptionallyCompose} permite recuperar de uma falha com OUTRA
 * operacao assincrona; {@code exceptionallyAsync} executa a recuperacao em outro
 * executor.</p>
 * <p>Beneficio: fallback assincrono (ex.: consultar um servico reserva) sem
 * "handle(...).thenCompose(...)" e sem bloquear threads.</p>
 * <p>Antes: {@code exceptionally} so permitia devolver um valor sincrono.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java12/concurrency/CompletableFutureExceptionallyExample.java
 * </pre>
 */
public class CompletableFutureExceptionallyExample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            System.out.println("=== Abordagem antiga: exceptionally (valor sincrono) ===");
            double price = fetchPriceFromMainService(executor)
                    .exceptionally(ex -> 0.0)
                    .join();
            System.out.println("Preco (fallback fixo): " + price);

            System.out.println("=== Java 12: exceptionallyCompose (fallback assincrono) ===");
            double price2 = fetchPriceFromMainService(executor)
                    .exceptionallyCompose(ex -> {
                        System.out.println("Falha: " + ex.getCause().getMessage() + " -> usando servico reserva");
                        return fetchPriceFromBackupService(executor);
                    })
                    .join();
            System.out.println("Preco (servico reserva): " + price2);

            System.out.println("=== Java 12: exceptionallyAsync ===");
            double price3 = fetchPriceFromMainService(executor)
                    .exceptionallyAsync(ex -> {
                        System.out.println("Recuperando na thread " + Thread.currentThread().getName());
                        return 99.9;
                    }, executor)
                    .join();
            System.out.println("Preco (recuperado): " + price3);
        } finally {
            executor.shutdown();
        }
    }

    private static CompletableFuture<Double> fetchPriceFromMainService(ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException("servico principal indisponivel");
        }, executor);
    }

    private static CompletableFuture<Double> fetchPriceFromBackupService(ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> 4500.00, executor);
    }
}
