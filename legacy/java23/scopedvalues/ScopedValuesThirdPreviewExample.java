import java.io.IOException;

/**
 * Java 23 - Scoped Values, terceira Preview (JEP 481) - API ORIGINAL (legado)
 *
 * <p>Status: Terceira Preview. Histórico: incubadora no 20 (JEP 429), Preview no 21
 * (JEP 446), segunda Preview no 22 (JEP 464); quarta no 24 (JEP 487) e definitivo no 25
 * (JEP 506).</p>
 *
 * <p>O que mudou no 23: o parâmetro de {@code callWhere} e de {@code Carrier.call} passou a
 * ser a nova interface funcional {@code ScopedValue.CallableOp<T, X extends Throwable>},
 * que permite ao compilador inferir QUAL exceção checada pode ser lançada (no 22 era um
 * {@code Callable}, que declara {@code throws Exception} genérico). Com isso o método
 * {@code getWhere} (para {@code Supplier}) ficou desnecessário e foi removido.</p>
 *
 * <p>Por que é legado: este arquivo usa os métodos estáticos {@code ScopedValue.runWhere} e
 * {@code ScopedValue.callWhere}, REMOVIDOS no Java 24 (JEP 487), que deixou a API só fluente
 * ({@code ScopedValue.where(k, v).run/call}). Não compila no JDK 24 nem no 25.</p>
 *
 * <p>Execução (precisa de JDK 23 - não validado, JDK 23 indisponível):</p>
 * <pre>
 * java --enable-preview --source 23 legacy/java23/scopedvalues/ScopedValuesThirdPreviewExample.java
 * </pre>
 */
public class ScopedValuesThirdPreviewExample {

    private static final ScopedValue<String> TENANT = ScopedValue.newInstance();

    public static void main(String[] args) {
        System.out.println("=== runWhere (estático, removido no Java 24) ===");
        ScopedValue.runWhere(TENANT, "loja-sul", () ->
                System.out.println("Processando pedidos do tenant " + TENANT.get()));

        System.out.println();
        System.out.println("=== callWhere com CallableOp: exceção checada inferida (IOException) ===");
        try {
            // O compilador infere X = IOException: basta tratar IOException, não Exception.
            String report = ScopedValue.callWhere(TENANT, "loja-norte", ScopedValuesThirdPreviewExample::loadReport);
            System.out.println(report);
        } catch (IOException e) {
            System.out.println("Falha de I/O: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== callWhere sem exceção checada: nenhum try/catch necessário ===");
        int count = ScopedValue.callWhere(TENANT, "loja-leste", () -> TENANT.get().length());
        System.out.println("Tamanho do nome do tenant: " + count);
    }

    static String loadReport() throws IOException {
        if (TENANT.get().isBlank()) throw new IOException("tenant vazio");
        return "Relatório do tenant " + TENANT.get();
    }
}
