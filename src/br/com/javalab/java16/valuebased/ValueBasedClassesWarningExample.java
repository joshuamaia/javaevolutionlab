package br.com.javalab.java16.valuebased;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Java 16 - Warnings for Value-Based Classes (JEP 390)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que e: classes "value-based" (Integer, Long, Double, Optional, LocalDate, ...)
 * passam a ser anotadas internamente com {@code @jdk.internal.ValueBased}, e:</p>
 * <ul>
 *   <li>javac emite o aviso {@code attempt to synchronize on an
 *       instance of a value-based class} ao usar {@code synchronized} sobre elas
 *       (categoria -Xlint "synchronization" no Java 16; renomeada para "identity" em
 *       JDKs recentes, como o 25);</li>
 *   <li>a JVM pode diagnosticar isso em tempo de execucao (opcao de diagnostico,
 *       requer {@code -XX:+UnlockDiagnosticVMOptions}):
 *       {@code -XX:DiagnoseSyncOnValueBasedClasses=1} (erro fatal) ou {@code =2} (log);</li>
 *   <li>os construtores dos wrappers ({@code new Integer(5)}) ficam
 *       {@code @Deprecated(forRemoval = true)}.</li>
 * </ul>
 * <p>Beneficio: prepara o terreno para as classes de valor do Projeto Valhalla, que
 * nao terao identidade (e portanto nao poderao ser usadas como monitor).</p>
 * <p>Antes: sincronizar em Integer "funcionava", mas e um bug: {@code Integer.valueOf}
 * reutiliza instancias em cache (-128..127), entao codigos sem relacao acabam
 * compartilhando o mesmo lock.</p>
 *
 * <p>Ao compilar este arquivo o javac EMITE um aviso de proposito (linha marcada).</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/valuebased/ValueBasedClassesWarningExample.java
 * java -XX:+UnlockDiagnosticVMOptions -XX:DiagnoseSyncOnValueBasedClasses=2 src/br/com/javalab/java16/valuebased/ValueBasedClassesWarningExample.java
 * </pre>
 */
public class ValueBasedClassesWarningExample {

    private static final Integer STOCK_LOCK = 100;          // NAO faca isso
    private static final Integer OTHER_MODULE_LOCK = 100;   // mesma instancia do cache!
    private static final ReentrantLock stockLock = new ReentrantLock();
    private static int stock = 10;

    public static void main(String[] args) {
        System.out.println("=== Problema: Integer em cache compartilha identidade ===");
        System.out.println("STOCK_LOCK == OTHER_MODULE_LOCK? " + (STOCK_LOCK == OTHER_MODULE_LOCK)
                + " -> dois 'locks' independentes sao o MESMO objeto");

        System.out.println("=== Abordagem antiga (gera aviso no javac 16+) ===");
        synchronized (STOCK_LOCK) {   // warning: attempt to synchronize on an instance of a value-based class
            stock--;
        }
        System.out.println("Estoque: " + stock);

        System.out.println("=== Forma correta: objeto dedicado ou Lock ===");
        stockLock.lock();
        try {
            stock--;
        } finally {
            stockLock.unlock();
        }
        System.out.println("Estoque: " + stock);
    }
}
