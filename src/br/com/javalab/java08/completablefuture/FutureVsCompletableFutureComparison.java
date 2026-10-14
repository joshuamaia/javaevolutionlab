package br.com.javalab.java08.completablefuture;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Java 8 - Comparação: Future (Java 5) vs CompletableFuture (JEP 155)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o mesmo fluxo "buscar cotação do dólar, converter preço e notificar"
 * com {@code Future} e com {@code CompletableFuture}.</p>
 * <p>Limitações do Future: só dá para obter o resultado com {@code get()} (bloqueante);
 * não há como encadear, combinar, completar manualmente nem tratar erro sem try/catch
 * de {@code ExecutionException}.</p>
 * <p>CompletableFuture: callbacks não bloqueantes, composição, combinação, erro tratado
 * no pipeline e possibilidade de completar manualmente ({@code complete}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/completablefuture/FutureVsCompletableFutureComparison.java
 * </pre>
 */
public class FutureVsCompletableFutureComparison {

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            System.out.println("=== Future (Java 5): get() bloqueia a thread chamadora ===");
            Future<Double> rateFuture = executor.submit(new Callable<Double>() {
                @Override
                public Double call() throws Exception {
                    Thread.sleep(300);
                    return 5.10;
                }
            });
            System.out.println("main: fazendo outra coisa... isDone=" + rateFuture.isDone());
            try {
                double rate = rateFuture.get(); // bloqueia
                double price = 100 * rate;      // a continuação é escrita "à mão" após o bloqueio
                System.out.println("Preço convertido: R$ " + price);
            } catch (ExecutionException e) {
                System.out.println("Erro: " + e.getCause());
            }

            System.out.println();
            System.out.println("=== CompletableFuture (Java 8): pipeline não bloqueante ===");
            CompletableFuture<Void> flow = CompletableFuture
                    .supplyAsync(() -> {
                        sleep(300);
                        return 5.10;
                    }, executor)
                    .thenApply(rate -> 100 * rate)
                    .thenAccept(price -> System.out.println("Preço convertido: R$ " + price
                            + " [" + Thread.currentThread().getName() + "]"))
                    .exceptionally(ex -> {
                        System.out.println("Erro: " + ex);
                        return null;
                    });
            System.out.println("main: livre enquanto o pipeline roda... isDone=" + flow.isDone());
            flow.join(); // só para o exemplo terminar de forma ordenada

            System.out.println();
            System.out.println("=== Completar manualmente (impossível com Future) ===");
            CompletableFuture<String> manual = new CompletableFuture<>();
            manual.thenAccept(msg -> System.out.println("Callback recebeu: " + msg));
            manual.complete("pagamento confirmado pelo webhook");

            System.out.println();
            System.out.println("=== Resumo ===");
            System.out.println("Future:            get() bloqueante, sem encadeamento, sem combinação");
            System.out.println("CompletableFuture: thenApply/thenCombine/allOf, exceptionally, complete()");
        } finally {
            executor.shutdown();
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
