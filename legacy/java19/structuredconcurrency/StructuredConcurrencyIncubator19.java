import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jdk.incubator.concurrent.StructuredTaskScope;

/**
 * Java 19 - Structured Concurrency, Incubadora (JEP 428) - API ORIGINAL
 *
 * <p>Status: Incubadora no Java 19 e 20 (JEP 437). Depois virou Preview no pacote
 * {@code java.util.concurrent} (Java 21, JEP 453, com {@code Subtask} no lugar de {@code Future}),
 * e foi redesenhada no Java 25 (JEP 505: {@code StructuredTaskScope.open()} + {@code Joiner}).
 * O módulo {@code jdk.incubator.concurrent} não existe mais, por isso este arquivo não compila no JDK 25.</p>
 *
 * <p>O que é: tratar um grupo de subtarefas concorrentes como uma unidade. As subtarefas
 * ("forks") vivem dentro de um escopo léxico (try-with-resources): o escopo espera todas
 * terminarem; com {@code ShutdownOnFailure}, a primeira falha cancela as demais.</p>
 * <p>Benefício: sem vazamento de threads, cancelamento automático e stack traces/dumps
 * que mostram a hierarquia pai-filho.</p>
 * <p>Antes: {@code ExecutorService} + {@code Future}: se uma tarefa falhava, as outras
 * continuavam rodando e era preciso cancelar manualmente.</p>
 *
 * <p>API desta versão: {@code fork} devolve {@link Future}; resultado via {@code resultNow()}.</p>
 *
 * <p>Execução (requer JDK 19 - não validado: JDK 19 indisponível neste ambiente):</p>
 * <pre>
 * java --enable-preview --source 19 --add-modules jdk.incubator.concurrent legacy/java19/structuredconcurrency/StructuredConcurrencyIncubator19.java
 * </pre>
 * <p>({@code --enable-preview} é necessário porque o escopo usa virtual threads, Preview no 19.)</p>
 */
public class StructuredConcurrencyIncubator19 {

    record OrderPage(String customer, int openOrders) { }

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

    static OrderPage loadPage(boolean failOrders) throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> customer = scope.fork(() -> findCustomer());
            Future<Integer> orders = scope.fork(() -> countOrders(failOrders));

            scope.join();          // espera as duas (ou o shutdown após a primeira falha)
            scope.throwIfFailed(); // propaga a falha como ExecutionException

            return new OrderPage(customer.resultNow(), orders.resultNow());
        }
    }

    static String findCustomer() throws InterruptedException {
        Thread.sleep(500);
        System.out.println("   cliente carregado (só aparece se não for cancelado)");
        return "Ana";
    }

    static int countOrders(boolean fail) throws InterruptedException {
        Thread.sleep(100);
        if (fail) {
            throw new IllegalStateException("serviço de pedidos indisponível");
        }
        return 3;
    }
}
