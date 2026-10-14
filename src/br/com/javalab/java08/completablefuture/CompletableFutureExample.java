package br.com.javalab.java08.completablefuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Java 8 - CompletableFuture (JEP 155 - Concurrency Updates)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: um {@code Future} que pode ser completado e composto, implementando
 * {@code CompletionStage}: {@code supplyAsync}, {@code thenApply}, {@code thenCompose},
 * {@code thenCombine}, {@code allOf}, {@code exceptionally}, {@code handle}...</p>
 * <p>Benefício: pipelines assíncronos sem bloquear threads e sem "callback hell";
 * tratamento de erro declarativo.</p>
 * <p>Antes: {@code Future.get()} bloqueante (ver {@code FutureVsCompletableFutureComparison}).</p>
 * <p>Boa prática: passar um {@code Executor} próprio para tarefas de I/O (o padrão é o
 * {@code ForkJoinPool.commonPool()}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/completablefuture/CompletableFutureExample.java
 * </pre>
 */
public class CompletableFutureExample {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            System.out.println("=== supplyAsync + thenApply + thenAccept ===");
            CompletableFuture<Void> pipeline = CompletableFuture
                    .supplyAsync(() -> fetch("pedido #10", 200, 150.0), executor)
                    .thenApply(total -> total * 1.10)
                    .thenAccept(total -> System.out.printf("Total com imposto: R$ %.2f%n", total));
            pipeline.join();

            System.out.println();
            System.out.println("=== thenCombine: frete e estoque em paralelo ===");
            long start = System.currentTimeMillis();
            CompletableFuture<Double> shipping = CompletableFuture.supplyAsync(() -> fetch("frete", 300, 25.0), executor);
            CompletableFuture<Double> price = CompletableFuture.supplyAsync(() -> fetch("preço", 300, 199.0), executor);
            double total = shipping.thenCombine(price, (s, p) -> s + p).join();
            System.out.println("Total: R$ " + total + " em ~" + (System.currentTimeMillis() - start)
                    + " ms (paralelo, não 600 ms)");

            System.out.println();
            System.out.println("=== thenCompose: chamada dependente ===");
            String status = CompletableFuture.supplyAsync(() -> "CUST-7", executor)
                    .thenCompose(id -> CompletableFuture.supplyAsync(() -> "cliente " + id + " ativo", executor))
                    .join();
            System.out.println(status);

            System.out.println();
            System.out.println("=== allOf: aguardar várias consultas ===");
            List<String> warehouses = Arrays.asList("SP", "RJ", "MG");
            List<CompletableFuture<String>> futures = warehouses.stream()
                    .map(w -> CompletableFuture.supplyAsync(() -> w + "=" + fetch("estoque " + w, 100, 10.0).intValue(), executor))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            System.out.println("Estoques: " + futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));

            System.out.println();
            System.out.println("=== exceptionally e handle: tratamento de erro ===");
            double fallback = CompletableFuture
                    .supplyAsync(() -> failingCall(), executor)
                    .exceptionally(ex -> {
                        System.out.println("Falhou: " + ex.getCause().getMessage() + " -> usando cache");
                        return 99.0;
                    })
                    .join();
            System.out.println("Valor final: " + fallback);
            String handled = CompletableFuture.supplyAsync(() -> failingCall(), executor)
                    .handle((value, ex) -> ex == null ? "ok: " + value : "erro tratado em handle")
                    .join();
            System.out.println(handled);
        } finally {
            executor.shutdown();
        }
    }

    private static Double fetch(String what, long millis, double value) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("  [" + Thread.currentThread().getName() + "] obteve " + what);
        return value;
    }

    private static Double failingCall() {
        throw new IllegalStateException("serviço de preços indisponível");
    }
}
