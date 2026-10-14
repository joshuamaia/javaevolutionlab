/**
 * Java 21 - Scoped Values, Preview (JEP 446) - MÉTODOS ESTÁTICOS REMOVIDOS DEPOIS
 *
 * <p>Status: Preview no Java 21. Histórico: Incubadora no 20 (JEP 429); definitivo no Java 25 (JEP 506).</p>
 *
 * <p>O que existia no Java 21 e não existe mais: os atalhos estáticos
 * {@code ScopedValue.runWhere(key, value, Runnable)}, {@code callWhere(key, value, Callable)} e
 * {@code getWhere(key, value, Supplier)} (que no Java 20 se chamavam todos {@code where}).
 * A partir do Java 23 (JEP 481) só resta a forma {@code ScopedValue.where(key, value).run/call(...)},
 * e {@code call} passou a receber {@code ScopedValue.CallableOp} (que declara o tipo exato da exceção)
 * no lugar de {@code Callable}. Por isso este arquivo não compila no JDK 25.
 * A forma que continua válida está em
 * {@code src/br/com/javalab/java21/scopedvalues/ScopedValuesExample.java}.</p>
 *
 * <p>Execução (requer JDK 21):</p>
 * <pre>
 * java --enable-preview --source 21 legacy/java21/scopedvalues/ScopedValuesStaticMethodsPreview21.java
 * </pre>
 */
public class ScopedValuesStaticMethodsPreview21 {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("=== runWhere: executa um Runnable com o valor vinculado ===");
        ScopedValue.runWhere(REQUEST_ID, "req-001", () -> log("calculando frete"));

        System.out.println();
        System.out.println("=== callWhere: Callable que pode lançar exceção checada ===");
        Integer stock = ScopedValue.callWhere(REQUEST_ID, "req-002", () -> {
            log("consultando estoque");
            return 42;
        });
        System.out.println("Estoque retornado: " + stock);

        System.out.println();
        System.out.println("=== getWhere: Supplier, sem exceção checada ===");
        String receipt = ScopedValue.getWhere(REQUEST_ID, "req-003", () -> "recibo da " + REQUEST_ID.get());
        System.out.println(receipt);
    }

    static void log(String action) {
        System.out.println("[" + REQUEST_ID.get() + "] " + action);
    }
}
