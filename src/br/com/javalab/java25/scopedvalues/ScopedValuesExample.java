package br.com.javalab.java25.scopedvalues;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Java 25 - Scoped Values (JEP 506)
 *
 * <p>Status: Definitivo. Histórico: incubadora no 20 (JEP 429); Preview no 21 (JEP 446),
 * 22 (JEP 464), 23 (JEP 481, {@code CallableOp}) e 24 (JEP 487, remoção de
 * {@code runWhere/callWhere}). Mudança final no 25: {@code orElse(null)} não é mais aceito
 * (lança {@code NullPointerException}).</p>
 *
 * <p>O que é: {@code ScopedValue<T>} compartilha um valor imutável com todo o código chamado
 * dentro de um escopo ({@code ScopedValue.where(KEY, v).run/call}), sem parâmetros extras e
 * sem os problemas do {@code ThreadLocal}. Ver {@code ThreadLocalVsScopedValueComparison}.</p>
 * <p>Benefício: contexto implícito (usuário, transação, id de requisição) barato e seguro,
 * ideal para servidores com uma virtual thread por requisição.</p>
 *
 * <p>Foco: um mini servidor que atende requisições em virtual threads; cada requisição
 * vincula seu próprio usuário/id e as camadas (controller, service, repository) leem o
 * contexto sem recebê-lo por parâmetro.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/scopedvalues/ScopedValuesExample.java
 * </pre>
 */
public class ScopedValuesExample {

    record RequestContext(String requestId, String user) {}

    static final ScopedValue<RequestContext> CONTEXT = ScopedValue.newInstance();

    public static void main(String[] args) {
        System.out.println("=== Requisições concorrentes, cada uma com seu contexto ===");
        String[][] requests = {{"r1", "ana"}, {"r2", "bruno"}, {"r3", "carla"}};
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String[] request : requests) {
                RequestContext ctx = new RequestContext(request[0], request[1]);
                executor.submit(() -> ScopedValue.where(CONTEXT, ctx).run(() -> controller("/pedidos")));
            }
        }

        System.out.println();
        System.out.println("=== Fora de qualquer escopo ===");
        System.out.println("isBound() = " + CONTEXT.isBound());
        try {
            CONTEXT.get();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("get() sem vínculo -> NoSuchElementException");
        }
        try {
            CONTEXT.orElse(null); // aceito nas Previews; proibido no Java 25
        } catch (NullPointerException e) {
            System.out.println("orElse(null) -> NullPointerException (mudança do Java 25)");
        }
    }

    static void controller(String path) {
        String result = service();
        System.out.println("[" + CONTEXT.get().requestId() + "] GET " + path + " -> " + result);
    }

    static String service() {
        return repository() + " (consultado por " + CONTEXT.get().user() + ")";
    }

    static String repository() {
        return "pedidos de " + CONTEXT.get().user();
    }
}
