package br.com.javalab.java22.scopedvalues;

/**
 * Java 22 - Scoped Values, segunda Preview (JEP 464)
 *
 * <p>Status: Segunda Preview. Histórico: incubadora no Java 20 (JEP 429), Preview no
 * Java 21 (JEP 446); terceira Preview no 23 (JEP 481), quarta no 24 (JEP 487) e
 * definitivo no Java 25 (JEP 506). O 22 não trouxe mudanças de API em relação ao 21.</p>
 *
 * <p>O que é: {@code ScopedValue<T>} é um valor imutável vinculado a um escopo dinâmico:
 * {@code ScopedValue.where(KEY, valor).run(...)} torna o valor visível para todo código
 * chamado dentro do {@code run}, sem passá-lo como parâmetro, e o vínculo some ao sair.</p>
 * <p>Benefício: alternativa mais segura e barata ao {@code ThreadLocal} para "contexto
 * implícito" (usuário logado, id de requisição), especialmente com virtual threads.</p>
 * <p>Antes: {@code ThreadLocal} (mutável, exige {@code remove()} manual) ou passar o
 * contexto por todos os métodos.</p>
 *
 * <p>Compatibilidade: este código usa apenas {@code where(...).run/call}, que existe no 22
 * e no 25, por isso compila no JDK 25 sem flags (no 22 exigia {@code --enable-preview}).
 * Diferença da época: no 22 {@code Carrier.call} recebia um {@code Callable} e declarava
 * {@code throws Exception}, obrigando o {@code try/catch} abaixo; havia também os estáticos
 * {@code ScopedValue.runWhere/callWhere/getWhere}, removidos depois (ver Java 23 e 24).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java22/scopedvalues/ScopedValuesSecondPreviewExample.java
 * </pre>
 */
public class ScopedValuesSecondPreviewExample {

    private static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();

    public static void main(String[] args) {
        System.out.println("=== where(...).run(...): contexto visível em toda a cadeia de chamadas ===");
        ScopedValue.where(CURRENT_USER, "alice").run(() -> handleRequest("listar pedidos"));

        System.out.println();
        System.out.println("=== Fora do escopo o valor não está vinculado ===");
        System.out.println("isBound() fora do run: " + CURRENT_USER.isBound());
        System.out.println("orElse: " + CURRENT_USER.orElse("anônimo"));

        System.out.println();
        System.out.println("=== where(...).call(...): devolve um resultado ===");
        try {
            // No Java 22, call(Callable) declarava 'throws Exception' -> try/catch obrigatório.
            String receipt = ScopedValue.where(CURRENT_USER, "bob").call(() -> buildReceipt(150.0));
            System.out.println(receipt);
        } catch (Exception e) {
            System.out.println("Falha: " + e.getMessage());
        }
    }

    static void handleRequest(String action) {
        // Nenhum parâmetro "user" foi passado: o valor vem do escopo
        System.out.println("[" + CURRENT_USER.get() + "] executando: " + action);
        audit(action);
    }

    static void audit(String action) {
        System.out.println("auditoria -> usuário=" + CURRENT_USER.get() + ", ação=" + action);
    }

    static String buildReceipt(double total) {
        return "Recibo de " + CURRENT_USER.get() + ": R$ " + total;
    }
}
