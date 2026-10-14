import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * Java 21 - Structured Concurrency, Preview (JEP 453) - API DA ÉPOCA
 *
 * <p>Status: Preview no Java 21. Histórico: Incubadora no 19 (JEP 428) e 20 (JEP 437); Previews no
 * 22 (JEP 462), 23 (JEP 480), 24 (JEP 499); redesenhada no 25 (JEP 505, quinta Preview).</p>
 *
 * <p>O que mudou no Java 21 em relação às incubadoras: a API foi para
 * {@code java.util.concurrent}, e {@code fork} passou a devolver {@link Subtask} (com
 * {@code get()} e {@code state()}) em vez de {@code Future}.</p>
 * <p>Por que está em legacy/: no Java 25 (JEP 505) os construtores públicos e as subclasses
 * {@code ShutdownOnFailure}/{@code ShutdownOnSuccess} foram removidos; agora se usa
 * {@code StructuredTaskScope.open()} com um {@code Joiner}. Este arquivo não compila no JDK 25.</p>
 *
 * <p>O que é: subtarefas concorrentes tratadas como uma unidade dentro de um bloco
 * try-with-resources; com {@code ShutdownOnFailure}, a primeira falha cancela as demais.</p>
 * <p>Benefício: sem threads órfãs, cancelamento automático, observabilidade (thread dumps
 * mostram a hierarquia).</p>
 *
 * <p>Execução (requer JDK 21):</p>
 * <pre>
 * java --enable-preview --source 21 legacy/java21/structuredconcurrency/StructuredConcurrencyPreview21.java
 * </pre>
 */
public class StructuredConcurrencyPreview21 {

    record ProductPage(String description, double price) { }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Sucesso: as duas subtarefas terminam ===");
        System.out.println(loadPage(false));

        System.out.println();
        System.out.println("=== Falha: a primeira exceção cancela a outra subtarefa ===");
        try {
            loadPage(true);
        } catch (ExecutionException e) {
            System.out.println("Falhou rápido: " + e.getCause());
        }
    }

    static ProductPage loadPage(boolean failPrice) throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<String> description = scope.fork(() -> loadDescription());
            Subtask<Double> price = scope.fork(() -> loadPrice(failPrice));

            scope.join().throwIfFailed(); // espera e propaga a primeira falha

            System.out.println("Estados: " + description.state() + " / " + price.state());
            return new ProductPage(description.get(), price.get());
        }
    }

    static String loadDescription() throws InterruptedException {
        Thread.sleep(500);
        System.out.println("   descrição carregada (não aparece se a subtarefa for cancelada)");
        return "Monitor 27 polegadas";
    }

    static double loadPrice(boolean fail) throws InterruptedException {
        Thread.sleep(100);
        if (fail) {
            throw new IllegalStateException("serviço de preços fora do ar");
        }
        return 1299.0;
    }
}
