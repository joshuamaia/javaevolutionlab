import java.util.concurrent.Future;
import jdk.incubator.concurrent.ScopedValue;
import jdk.incubator.concurrent.StructuredTaskScope;

/**
 * Java 20 - Structured Concurrency, segunda Incubadora (JEP 437) - API ORIGINAL
 *
 * <p>Status: Segunda Incubadora no Java 20. Histórico: 1a Incubadora no 19 (JEP 428); Preview no
 * {@code java.util.concurrent} a partir do 21 (JEP 453); redesenhada no 25 (JEP 505).
 * O módulo {@code jdk.incubator.concurrent} não existe mais, por isso este arquivo não compila no JDK 25.</p>
 *
 * <p>Única mudança do Java 20 (foco deste exemplo): threads criadas por {@code fork} dentro de um
 * {@code StructuredTaskScope} <b>herdam os Scoped Values</b> vinculados na thread dona do escopo
 * (integração com a JEP 429). A API ({@code fork} devolvendo {@link Future}, {@code join},
 * {@code throwIfFailed}, {@code resultNow}) é a mesma do Java 19 - veja
 * {@code legacy/java19/structuredconcurrency/StructuredConcurrencyIncubator19.java}.</p>
 *
 * <p>Execução (requer JDK 20 - não validado: JDK 20 indisponível neste ambiente):</p>
 * <pre>
 * java --enable-preview --source 20 --add-modules jdk.incubator.concurrent legacy/java20/structuredconcurrency/StructuredConcurrencySecondIncubator20.java
 * </pre>
 * <p>({@code --enable-preview} é necessário porque o escopo usa virtual threads, Preview no 20.)</p>
 */
public class StructuredConcurrencySecondIncubator20 {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    record Checkout(String customer, double freight) { }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Subtarefas herdam o Scoped Value da thread dona do escopo ===");
        Checkout checkout = ScopedValue.where(REQUEST_ID, "req-2023-0420").call(() -> loadCheckout());
        System.out.println("Resultado: " + checkout);
    }

    static Checkout loadCheckout() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> customer = scope.fork(() -> log("buscando cliente", "Ana"));
            Future<Double> freight = scope.fork(() -> log("calculando frete", 19.90));

            scope.join();
            scope.throwIfFailed();
            return new Checkout(customer.resultNow(), freight.resultNow());
        }
    }

    /** Roda numa virtual thread filha, mas enxerga o REQUEST_ID vinculado no pai. */
    static <T> T log(String action, T result) {
        System.out.println("[" + REQUEST_ID.get() + "] " + action + " em " + Thread.currentThread());
        return result;
    }
}
