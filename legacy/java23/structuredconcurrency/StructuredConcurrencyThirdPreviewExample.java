import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * Java 23 - Structured Concurrency, terceira Preview (JEP 480) - API ORIGINAL (legado)
 *
 * <p>Status: Terceira Preview (sem mudanças de API em relação ao 21/22). Histórico:
 * incubadora nos 19/20, Preview no 21 (JEP 453), segunda no 22 (JEP 462); quarta no 24
 * (JEP 499); quinta no 25 (JEP 505) com API redesenhada ({@code StructuredTaskScope.open} +
 * {@code Joiner}). Esta API ({@code ShutdownOnSuccess}) não compila no JDK 25.</p>
 *
 * <p>Foco (diferente do exemplo do 22, que usou {@code ShutdownOnFailure}): a política
 * {@code ShutdownOnSuccess} - dispara a mesma consulta em várias réplicas e fica com a
 * PRIMEIRA resposta bem-sucedida; as demais subtarefas são canceladas automaticamente.
 * No Java 25 o equivalente é {@code Joiner.anySuccessfulResultOrThrow()}.</p>
 * <p>Antes: {@code ExecutorService.invokeAny}, sem relação de escopo entre as threads.</p>
 *
 * <p>Execução (precisa de JDK 23 - não validado com JDK 23, indisponível; a API é idêntica
 * nos JDKs 21-24, então também roda com {@code --source 24} no JDK 24):</p>
 * <pre>
 * java --enable-preview --source 23 legacy/java23/structuredconcurrency/StructuredConcurrencyThirdPreviewExample.java
 * </pre>
 */
public class StructuredConcurrencyThirdPreviewExample {

    static String queryReplica(String replica, long latencyMs, boolean fail) throws InterruptedException {
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            System.out.println("  " + replica + " cancelada");
            throw e;
        }
        if (fail) throw new IllegalStateException(replica + " fora do ar");
        return "cotação do dólar via " + replica + " (" + latencyMs + " ms)";
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ShutdownOnSuccess: primeira réplica que responder vence ===");
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            scope.fork(() -> queryReplica("replica-sp", 300, false));
            scope.fork(() -> queryReplica("replica-rj", 80, true));   // mais rápida, mas falha
            scope.fork(() -> queryReplica("replica-mg", 150, false));
            scope.join();
            System.out.println("Resultado: " + scope.result());
        } catch (ExecutionException e) {
            System.out.println("Todas falharam: " + e.getCause().getMessage());
        }
    }
}
