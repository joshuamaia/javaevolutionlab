import jdk.incubator.concurrent.ScopedValue;

/**
 * Java 20 - Scoped Values, Incubadora (JEP 429) - API ORIGINAL
 *
 * <p>Status: Incubadora no Java 20. Depois: Preview no 21 (JEP 446, já em {@code java.lang}),
 * 22 (JEP 464), 23 (JEP 481), 24 (JEP 487) e definitivo no Java 25 (JEP 506).
 * O módulo {@code jdk.incubator.concurrent} não existe mais, por isso este arquivo não compila no JDK 25.</p>
 *
 * <p>O que é: um valor imutável vinculado a uma thread durante a execução de um bloco
 * ({@code where(KEY, valor).run(...)}), visível a todo o código chamado por ele, e automaticamente
 * desvinculado ao final. Pode ser "re-vinculado" num escopo interno.</p>
 * <p>Benefício: alternativa a {@code ThreadLocal} para passar contexto (usuário, request id)
 * sem parâmetros: imutável, tempo de vida limitado, barato com milhões de virtual threads e
 * herdado por subtarefas de {@code StructuredTaskScope}.</p>
 * <p>Antes: {@code ThreadLocal} (mutável, vazamentos se esquecer {@code remove()}, cópia cara em
 * {@code InheritableThreadLocal}).</p>
 *
 * <p>API específica desta versão: pacote {@code jdk.incubator.concurrent} e os métodos estáticos
 * {@code ScopedValue.where(key, value, Runnable)} / {@code where(key, value, Callable)}
 * (renomeados para {@code runWhere}/{@code callWhere} no Java 21 e removidos depois).</p>
 *
 * <p>Execução (requer JDK 20 - não validado: JDK 20 indisponível neste ambiente):</p>
 * <pre>
 * java --add-modules jdk.incubator.concurrent --source 20 legacy/java20/scopedvalues/ScopedValuesIncubator20.java
 * </pre>
 */
public class ScopedValuesIncubator20 {

    private static final ScopedValue<String> LOGGED_USER = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Fora de qualquer escopo ===");
        System.out.println("isBound? " + LOGGED_USER.isBound() + " | orElse: " + LOGGED_USER.orElse("anônimo"));

        System.out.println();
        System.out.println("=== where(...).run(...) ===");
        ScopedValue.where(LOGGED_USER, "ana@loja.com").run(() -> {
            processOrder("PED-1");

            // Re-vinculação num escopo interno (ex.: rotina executada "como sistema")
            ScopedValue.where(LOGGED_USER, "sistema").run(() -> processOrder("PED-AUTO"));

            processOrder("PED-2"); // de volta ao valor externo
        });

        System.out.println();
        System.out.println("=== Forma estática do Java 20: where(key, value, Callable) ===");
        String receipt = ScopedValue.where(LOGGED_USER, "bruno@loja.com", () -> "recibo emitido por " + LOGGED_USER.get());
        System.out.println(receipt);

        System.out.println();
        System.out.println("Depois do escopo, isBound? " + LOGGED_USER.isBound());
    }

    /** Código "profundo" que lê o contexto sem recebê-lo por parâmetro. */
    static void processOrder(String orderId) {
        System.out.println("Processando " + orderId + " para " + LOGGED_USER.get());
    }
}
