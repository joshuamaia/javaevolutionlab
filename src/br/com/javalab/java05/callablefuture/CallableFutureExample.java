package br.com.javalab.java05.callablefuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java 5 - Callable e Future (JSR 166)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code Callable<V>} é uma tarefa que RETORNA um valor e pode lançar
 * exceção checada; {@code Future<V>} representa o resultado pendente: {@code get()}
 * (com ou sem timeout), {@code cancel()}, {@code isDone()}. {@code invokeAll} e
 * {@code ExecutorCompletionService} ajudam a lidar com várias tarefas.</p>
 * <p>Benefício: obter resultados e exceções de tarefas assíncronas sem variáveis
 * compartilhadas nem {@code join()}.</p>
 * <p>Antes: {@code Runnable.run()} retorna void e não lança exceção checada; o
 * resultado era passado por campos compartilhados + {@code join()}.</p>
 * <p>Evolução: {@code CompletableFuture} (Java 8) permite compor sem bloquear.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/callablefuture/CallableFutureExample.java
 * </pre>
 */
public class CallableFutureExample {

    /** Calcula o frete de um pedido (simula consulta lenta). */
    static class ShippingQuote implements Callable<Double> {
        private final String carrier;
        private final long delayMs;
        private final double price;

        ShippingQuote(String carrier, long delayMs, double price) {
            this.carrier = carrier;
            this.delayMs = delayMs;
            this.price = price;
        }

        public Double call() throws Exception { // sem @Override (interface, Java 5)
            Thread.sleep(delayMs);
            if (price < 0) {
                throw new IllegalStateException("transportadora " + carrier + " fora do ar");
            }
            System.out.println("  cotação " + carrier + " pronta");
            return price;
        }
    }

    /** Antes: resultado em campo compartilhado. */
    static class OldQuoteThread extends Thread {
        volatile double result;

        public void run() {
            result = 42.0; // não há como "retornar"; nem lançar exceção checada
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Abordagem antiga: Thread + campo compartilhado + join ===");
        OldQuoteThread old = new OldQuoteThread();
        old.start();
        old.join();
        System.out.println("Resultado lido do campo: " + old.result);

        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            System.out.println();
            System.out.println("=== Callable + Future.get() ===");
            Future<Double> future = pool.submit(new ShippingQuote("Rápida", 100, 25.0));
            System.out.println("Pronto logo após submit? " + future.isDone());
            System.out.println("Frete: R$ " + future.get()); // bloqueia até o resultado

            System.out.println();
            System.out.println("=== get com timeout e cancel ===");
            Future<Double> slow = pool.submit(new ShippingQuote("Lenta", 5000, 9.0));
            try {
                slow.get(200, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                System.out.println("Timeout! Cancelando: " + slow.cancel(true) + ", isCancelled=" + slow.isCancelled());
            }

            System.out.println();
            System.out.println("=== Exceção da tarefa chega como ExecutionException ===");
            Future<Double> broken = pool.submit(new ShippingQuote("Quebrada", 10, -1));
            try {
                broken.get();
            } catch (ExecutionException e) {
                System.out.println("Causa: " + e.getCause());
            }

            System.out.println();
            System.out.println("=== invokeAll: espera todas ===");
            List<Callable<Double>> quotes = new ArrayList<Callable<Double>>();
            quotes.add(new ShippingQuote("A", 150, 30.0));
            quotes.add(new ShippingQuote("B", 50, 22.5));
            quotes.add(new ShippingQuote("C", 100, 27.0));
            double best = Double.MAX_VALUE;
            for (Future<Double> f : pool.invokeAll(quotes)) {
                best = Math.min(best, f.get());
            }
            System.out.println("Melhor frete: R$ " + best);

            System.out.println();
            System.out.println("=== CompletionService: resultados na ordem em que terminam ===");
            CompletionService<Double> completion = new ExecutorCompletionService<Double>(pool);
            for (Callable<Double> q : quotes) {
                completion.submit(q);
            }
            for (int i = 0; i < quotes.size(); i++) {
                System.out.println("Chegou: R$ " + completion.take().get());
            }
        } finally {
            pool.shutdownNow();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
