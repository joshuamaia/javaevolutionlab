package br.com.javalab.java25.scopedvalues;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Java 25 - Comparação ThreadLocal x Scoped Values (JEP 506)
 *
 * <p>Status: Definitivo (Scoped Values). {@code ThreadLocal} existe desde o Java 1.2 e
 * {@code InheritableThreadLocal} também.</p>
 *
 * <p>Quatro diferenças práticas, cada uma em uma seção:</p>
 * <ol>
 *   <li><b>Mutabilidade</b>: qualquer código pode chamar {@code ThreadLocal.set(...)}; um
 *       {@code ScopedValue} não tem {@code set} - o valor é fixo durante o escopo.</li>
 *   <li><b>Vazamento em pool</b>: um {@code ThreadLocal} esquecido sem {@code remove()} "vaza"
 *       para a próxima tarefa que reutilizar a mesma thread do pool; o {@code ScopedValue}
 *       desvincula sozinho ao sair do {@code run}.</li>
 *   <li><b>Herança para threads filhas</b>: {@code InheritableThreadLocal} COPIA o valor para
 *       toda thread criada (custo de memória, e a cópia pode divergir). {@code ScopedValue}
 *       não é herdado por {@code new Thread}; é compartilhado (sem cópia) apenas com subtarefas
 *       de um {@code StructuredTaskScope} (ver o exemplo de Structured Concurrency do Java 25).</li>
 *   <li><b>Rebinding aninhado</b>: com {@code ThreadLocal} é preciso salvar, trocar e restaurar
 *       o valor com {@code try/finally}; com {@code ScopedValue} um {@code where(...).run}
 *       interno sobrepõe o valor e a restauração é automática.</li>
 * </ol>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/scopedvalues/ThreadLocalVsScopedValueComparison.java
 * </pre>
 */
public class ThreadLocalVsScopedValueComparison {

    static final ThreadLocal<String> TL_USER = new ThreadLocal<>();
    static final InheritableThreadLocal<String> ITL_USER = new InheritableThreadLocal<>();
    static final ScopedValue<String> SV_USER = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1) Mutabilidade ===");
        TL_USER.set("ana");
        changeSilently();
        System.out.println("ThreadLocal após chamar um método qualquer: " + TL_USER.get() + " (alterado por baixo dos panos!)");
        TL_USER.remove();
        ScopedValue.where(SV_USER, "ana").run(() -> {
            // SV_USER.set("hacker");  -> não existe: não compila
            System.out.println("ScopedValue: " + SV_USER.get() + " (imutável no escopo)");
        });

        System.out.println();
        System.out.println("=== 2) Vazamento em pool de threads (1 thread reutilizada) ===");
        try (ExecutorService pool = Executors.newSingleThreadExecutor()) {
            pool.submit(() -> TL_USER.set("ana")).get();                    // tarefa 1 esquece o remove()
            Future<String> next = pool.submit(() -> "tarefa 2 vê ThreadLocal = " + TL_USER.get());
            System.out.println(next.get() + "  <- vazou da tarefa anterior");

            pool.submit(() -> ScopedValue.where(SV_USER, "ana").run(() -> { })).get();
            Future<String> nextSv = pool.submit(() -> "tarefa 2 vê ScopedValue vinculado? " + SV_USER.isBound());
            System.out.println(nextSv.get() + "  <- nada vaza");
        }

        System.out.println();
        System.out.println("=== 3) Herança para threads filhas ===");
        ITL_USER.set("bruno");
        ScopedValue.where(SV_USER, "bruno").run(() -> {
            Thread child = new Thread(() -> {
                System.out.println("Filha vê InheritableThreadLocal = " + ITL_USER.get() + " (cópia)");
                ITL_USER.set("alterado-na-filha");
                System.out.println("Filha vê ScopedValue vinculado? " + SV_USER.isBound()
                        + " (só subtarefas de StructuredTaskScope herdam)");
            });
            child.start();
            try {
                child.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        System.out.println("Pai continua com InheritableThreadLocal = " + ITL_USER.get() + " (cópias divergem)");
        ITL_USER.remove();

        System.out.println();
        System.out.println("=== 4) Rebinding aninhado ===");
        TL_USER.set("carla");
        String saved = TL_USER.get();
        TL_USER.set("system");                 // troca manual
        try {
            System.out.println("ThreadLocal dentro da tarefa de sistema: " + TL_USER.get());
        } finally {
            TL_USER.set(saved);                // restauração manual (fácil de esquecer)
        }
        System.out.println("ThreadLocal restaurado: " + TL_USER.get());
        TL_USER.remove();

        ScopedValue.where(SV_USER, "carla").run(() -> {
            ScopedValue.where(SV_USER, "system").run(() ->
                    System.out.println("ScopedValue no escopo interno: " + SV_USER.get()));
            System.out.println("ScopedValue restaurado automaticamente: " + SV_USER.get());
        });
    }

    static void changeSilently() {
        TL_USER.set("hacker");
    }
}
