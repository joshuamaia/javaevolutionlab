package br.com.javalab.java21.scopedvalues;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Java 21 - Scoped Values, Preview (JEP 446)
 *
 * <p>Status: Preview no Java 21. Histórico: Incubadora no 20 (JEP 429); Previews no 22 (JEP 464),
 * 23 (JEP 481) e 24 (JEP 487); definitivo no Java 25 (JEP 506).</p>
 *
 * <p>Este arquivo usa apenas a parte da API que existe tanto no Java 21 (Preview) quanto no 25
 * (definitivo): {@code ScopedValue.newInstance()}, {@code ScopedValue.where(k, v).run(...)},
 * {@code get()}, {@code isBound()} e {@code orElse(...)}. Por isso fica em {@code src/} e compila no
 * JDK 25 sem flags. Os atalhos estáticos do Java 21 ({@code runWhere}, {@code callWhere},
 * {@code getWhere}), removidos depois, estão em
 * {@code legacy/java21/scopedvalues/ScopedValuesStaticMethodsPreview21.java}.</p>
 *
 * <p>O que é: um valor imutável vinculado à thread atual durante a execução de um bloco,
 * visível para todo o código chamado a partir dele (sem passar parâmetros) e desvinculado
 * automaticamente ao final.</p>
 * <p>Benefício: contexto (usuário logado, id de requisição, transação) com tempo de vida claro,
 * sem vazamentos e barato mesmo com milhões de virtual threads.</p>
 * <p>Antes: {@code ThreadLocal} - mutável ({@code set} em qualquer lugar), exige {@code remove()}
 * para não vazar em pools, e {@code InheritableThreadLocal} copia valores para cada thread filha.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/scopedvalues/ScopedValuesExample.java
 * </pre>
 * <p>No JDK 21: {@code java --enable-preview --source 21 src/br/com/javalab/java21/scopedvalues/ScopedValuesExample.java}</p>
 */
public class ScopedValuesExample {

    private static final ThreadLocal<String> USER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ScopedValue<String> LOGGED_USER = ScopedValue.newInstance();
    private static final ScopedValue<String> TENANT = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("=== Abordagem antiga: ThreadLocal num pool (vazamento se esquecer remove) ===");
        try (ExecutorService pool = Executors.newFixedThreadPool(1)) {
            pool.submit(() -> USER_THREAD_LOCAL.set("ana@loja.com")).get(); // sem remove()!
            pool.submit(() -> System.out.println("Próxima tarefa na mesma thread ainda vê: "
                    + USER_THREAD_LOCAL.get())).get();
        }

        System.out.println();
        System.out.println("=== Scoped Value: vinculado só durante o bloco ===");
        System.out.println("Antes: isBound = " + LOGGED_USER.isBound());
        ScopedValue.where(LOGGED_USER, "ana@loja.com")
                .where(TENANT, "loja-centro")
                .run(() -> {
                    checkout("PED-1");
                    // Re-vinculação num escopo aninhado: o valor externo volta ao sair
                    ScopedValue.where(LOGGED_USER, "job-noturno").run(() -> checkout("PED-AUTO"));
                    checkout("PED-2");
                });
        System.out.println("Depois: isBound = " + LOGGED_USER.isBound()
                + ", orElse = " + LOGGED_USER.orElse("anônimo"));

        System.out.println();
        System.out.println("=== Ler fora do escopo lança exceção ===");
        try {
            LOGGED_USER.get();
        } catch (java.util.NoSuchElementException e) {
            System.out.println("NoSuchElementException: " + e.getMessage());
        }
    }

    /** Código "profundo": não recebe usuário por parâmetro e não consegue alterá-lo (não há set). */
    static void checkout(String orderId) {
        System.out.println("Checkout " + orderId + " por " + LOGGED_USER.get() + " no tenant " + TENANT.get());
    }
}
