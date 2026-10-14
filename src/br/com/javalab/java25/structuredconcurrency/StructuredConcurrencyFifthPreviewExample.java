package br.com.javalab.java25.structuredconcurrency;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * Java 25 - Structured Concurrency, quinta Preview (JEP 505)
 *
 * <p>Status: Quinta Preview. Histórico: incubadora nos 19/20 (JEPs 428, 437), Preview nos
 * 21-24 (JEPs 453, 462, 480, 499) com {@code new StructuredTaskScope.ShutdownOnFailure()} /
 * {@code ShutdownOnSuccess} (exemplos em {@code legacy/java22..24}). Exige {@code --enable-preview}.</p>
 *
 * <p>O que mudou no 25: a API foi redesenhada. {@code StructuredTaskScope} virou interface,
 * criada por fábricas estáticas {@code StructuredTaskScope.open(...)}; a política de conclusão
 * deixou de ser subclasse e passou a ser um {@link Joiner}:</p>
 * <ul>
 *   <li>{@code open()} / {@code Joiner.awaitAllSuccessfulOrThrow()} - antigo ShutdownOnFailure;</li>
 *   <li>{@code Joiner.allSuccessfulOrThrow()} - devolve um stream de {@link Subtask};</li>
 *   <li>{@code Joiner.anySuccessfulResultOrThrow()} - antigo ShutdownOnSuccess;</li>
 *   <li>{@code join()} já lança a exceção ({@code FailedException}) e devolve o resultado do
 *       Joiner - não existe mais {@code throwIfFailed()}; o prazo vai em
 *       {@code open(joiner, cfg -> cfg.withTimeout(...))} em vez de {@code joinUntil}.</li>
 * </ul>
 * <p>Benefício: o grupo de subtarefas tem o tempo de vida do bloco try; falhas cancelam as
 * irmãs; nenhuma thread vaza; Scoped Values do pai são vistos pelas subtarefas.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java --enable-preview --source 25 src/br/com/javalab/java25/structuredconcurrency/StructuredConcurrencyFifthPreviewExample.java
 * </pre>
 */
public class StructuredConcurrencyFifthPreviewExample {

    record Customer(String name) {}
    record Order(String id, double total) {}

    static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    static Customer findCustomer(int id) throws InterruptedException {
        Thread.sleep(100);
        return new Customer("Cliente " + id + " [" + REQUEST_ID.get() + "]"); // herda o ScopedValue
    }

    static Order findLastOrder(int id, boolean fail) throws InterruptedException {
        Thread.sleep(50);
        if (fail) throw new IllegalStateException("serviço de pedidos indisponível");
        return new Order("PED-" + id, 249.90);
    }

    static String quote(String carrier, long millis, boolean fail) throws InterruptedException {
        Thread.sleep(millis);
        if (fail) throw new IllegalStateException(carrier + " fora do ar");
        return carrier + " (" + millis + " ms)";
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== open(): todas precisam ter sucesso (antigo ShutdownOnFailure) ===");
        ScopedValue.where(REQUEST_ID, "req-77").call(() -> {
            try (var scope = StructuredTaskScope.open()) {
                Subtask<Customer> customer = scope.fork(() -> findCustomer(7));
                Subtask<Order> order = scope.fork(() -> findLastOrder(7, false));
                scope.join(); // lança FailedException se alguma falhar
                System.out.println(customer.get().name() + " -> " + order.get().id());
            }
            return null;
        });

        System.out.println();
        System.out.println("=== Falha cancela a irmã e é propagada pelo join() ===");
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> quote("lenta", 2_000, false));
            scope.fork(() -> findLastOrder(8, true));
            scope.join();
        } catch (StructuredTaskScope.FailedException e) {
            System.out.println("FailedException, causa: " + e.getCause().getMessage());
        }

        System.out.println();
        System.out.println("=== Joiner.allSuccessfulOrThrow(): coleta todos os resultados ===");
        try (var scope = StructuredTaskScope.open(Joiner.<String>allSuccessfulOrThrow())) {
            for (String carrier : List.of("Correios", "Transportadora X", "Motoboy")) {
                scope.fork(() -> quote(carrier, 30 + carrier.length() * 5, false));
            }
            List<String> quotes = scope.join().map(Subtask::get).toList();
            System.out.println("Cotações: " + quotes);
        }

        System.out.println();
        System.out.println("=== Joiner.anySuccessfulResultOrThrow(): a primeira que der certo (antigo ShutdownOnSuccess) ===");
        try (var scope = StructuredTaskScope.open(Joiner.<String>anySuccessfulResultOrThrow())) {
            scope.fork(() -> quote("replica-sp", 300, false));
            scope.fork(() -> quote("replica-rj", 80, true));
            scope.fork(() -> quote("replica-mg", 150, false));
            System.out.println("Vencedora: " + scope.join());
        }
    }
}
