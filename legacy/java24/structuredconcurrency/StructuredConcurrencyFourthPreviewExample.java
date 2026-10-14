import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Java 24 - Structured Concurrency, quarta Preview (JEP 499) - API ORIGINAL (legado)
 *
 * <p>Status: Quarta Preview (sem mudanças de API desde o 21). Histórico: incubadora nos
 * 19/20 (JEPs 428, 437), Preview no 21 (JEP 453), 22 (JEP 462), 23 (JEP 480); quinta Preview
 * no 25 (JEP 505) com API nova ({@code StructuredTaskScope.open(Joiner)}). Esta API
 * (construtores públicos, subclasses de {@code StructuredTaskScope}, {@code joinUntil})
 * NÃO compila no JDK 25.</p>
 *
 * <p>Foco (22 mostrou ShutdownOnFailure, 23 mostrou ShutdownOnSuccess): (1) prazo com
 * {@code joinUntil(Instant)}, e (2) uma POLÍTICA PRÓPRIA criada estendendo
 * {@code StructuredTaskScope} e sobrescrevendo {@code handleComplete} - aqui, coletar os
 * resultados bem-sucedidos e ignorar as falhas. No Java 25 isso virou a interface
 * {@code StructuredTaskScope.Joiner} (ex.: {@code Joiner.allUntil(...)} ou um Joiner próprio).</p>
 *
 * <p>Execução (JDK 24 - validado com OpenJDK 24.0.2):</p>
 * <pre>
 * java --enable-preview --source 24 legacy/java24/structuredconcurrency/StructuredConcurrencyFourthPreviewExample.java
 * </pre>
 */
public class StructuredConcurrencyFourthPreviewExample {

    /** Política própria: guarda os resultados das subtarefas que tiveram sucesso. */
    static class CollectSuccesses<T> extends StructuredTaskScope<T> {
        private final ConcurrentLinkedQueue<T> results = new ConcurrentLinkedQueue<>();

        @Override
        protected void handleComplete(Subtask<? extends T> subtask) {
            if (subtask.state() == Subtask.State.SUCCESS) {
                results.add(subtask.get());
            }
        }

        List<T> results() {
            ensureOwnerAndJoined(); // só o dono, depois do join
            return List.copyOf(results);
        }
    }

    static String quote(String carrier, long millis, boolean fail) throws InterruptedException {
        Thread.sleep(millis);
        if (fail) throw new IllegalStateException(carrier + " indisponível");
        return carrier + ": R$ " + (10 + millis / 20);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Política própria: cotações de frete que deram certo ===");
        try (var scope = new CollectSuccesses<String>()) {
            scope.fork(() -> quote("Correios", 120, false));
            scope.fork(() -> quote("Transportadora X", 60, true));
            scope.fork(() -> quote("Transportadora Y", 90, false));
            scope.join();
            scope.results().stream().sorted().forEach(System.out::println);
        }

        System.out.println();
        System.out.println("=== joinUntil: prazo de 200 ms (uma subtarefa leva 2 s) ===");
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Supplier<String> fast = scope.fork(() -> quote("Motoboy", 50, false));
            scope.fork(() -> quote("Navio", 2_000, false));
            scope.joinUntil(Instant.now().plus(Duration.ofMillis(200)));
            System.out.println("Não deveria chegar aqui: " + fast.get());
        } catch (TimeoutException e) {
            System.out.println("Prazo esgotado: o escopo cancelou as subtarefas pendentes ao fechar.");
        }
    }
}
