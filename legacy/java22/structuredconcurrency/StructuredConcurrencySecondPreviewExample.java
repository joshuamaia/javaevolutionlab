import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

/**
 * Java 22 - Structured Concurrency, segunda Preview (JEP 462) - API ORIGINAL (legado)
 *
 * <p>Status: Segunda Preview. Histórico: incubadora nos Java 19/20 (JEPs 428, 437),
 * Preview no 21 (JEP 453), segunda Preview no 22 (JEP 462, sem mudanças de API),
 * terceira no 23 (JEP 480), quarta no 24 (JEP 499) e QUINTA no 25 (JEP 505), quando a API
 * foi redesenhada: {@code new StructuredTaskScope.ShutdownOnFailure()} deu lugar a
 * {@code StructuredTaskScope.open(Joiner)}. Por isso este arquivo NÃO compila no JDK 25 e
 * fica em {@code legacy/} (sem pacote).</p>
 *
 * <p>O que é: trata um grupo de subtarefas concorrentes como uma unidade: todas são
 * criadas ({@code fork}) dentro de um escopo, esperadas juntas ({@code join}) e, com a
 * política {@code ShutdownOnFailure}, se uma falha as outras são canceladas.</p>
 * <p>Benefício: sem threads "órfãs", cancelamento automático, erros propagados e
 * observabilidade (thread dumps mostram a hierarquia).</p>
 * <p>Antes: {@code ExecutorService.submit} + {@code Future.get()}, onde a falha de uma
 * tarefa não cancelava as demais.</p>
 *
 * <p>Execução (precisa de JDK 22 - não validado, JDK 22 indisponível; a mesma API existe
 * nos JDKs 21-24, então o código também compila/roda com {@code --source 21} ou {@code 24}):</p>
 * <pre>
 * java --enable-preview --source 22 legacy/java22/structuredconcurrency/StructuredConcurrencySecondPreviewExample.java
 * </pre>
 */
public class StructuredConcurrencySecondPreviewExample {

    record Customer(String name) {}
    record Order(String id, double total) {}

    static Customer findCustomer(int id) throws InterruptedException {
        Thread.sleep(100);
        return new Customer("Cliente " + id);
    }

    static Order findLastOrder(int customerId, boolean fail) throws InterruptedException {
        Thread.sleep(50);
        if (fail) throw new IllegalStateException("serviço de pedidos indisponível");
        return new Order("PED-" + customerId, 249.90);
    }

    static String loadDashboard(int customerId, boolean failOrders) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<Customer> customer = scope.fork(() -> findCustomer(customerId));
            Subtask<Order> order = scope.fork(() -> findLastOrder(customerId, failOrders));

            scope.join()            // espera as duas subtarefas
                 .throwIfFailed();  // propaga a primeira falha (e as outras foram canceladas)

            return customer.get().name() + " - último pedido " + order.get().id()
                    + " (R$ " + order.get().total() + ")";
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== ShutdownOnFailure: sucesso ===");
        System.out.println(loadDashboard(7, false));

        System.out.println();
        System.out.println("=== ShutdownOnFailure: uma subtarefa falha ===");
        try {
            loadDashboard(8, true);
        } catch (java.util.concurrent.ExecutionException e) {
            System.out.println("Falha propagada: " + e.getCause().getMessage());
        }
    }
}
