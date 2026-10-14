package br.com.javalab.java05.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Java 5 - Variáveis atômicas (JSR 166, java.util.concurrent.atomic)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code AtomicInteger}, {@code AtomicLong}, {@code AtomicBoolean},
 * {@code AtomicReference}, arrays atômicos etc. Operações como
 * {@code incrementAndGet} e {@code compareAndSet} são atômicas sem usar locks: usam a
 * instrução de hardware CAS (compare-and-swap).</p>
 * <p>Benefício: contadores e atualizações simples thread-safe, sem bloqueio e com
 * melhor escalabilidade que {@code synchronized}.</p>
 * <p>Antes: {@code synchronized} em todo acesso, ou bugs com {@code count++} (que é
 * ler-modificar-escrever, não atômico; {@code volatile} sozinho não resolve).</p>
 * <p>Evolução: {@code LongAdder} (Java 8) para contadores muito disputados.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/atomic/AtomicVariablesExample.java
 * </pre>
 */
public class AtomicVariablesExample {

    static final int THREADS = 4;
    static final int INCREMENTS = 100000;

    static int unsafeCounter = 0;                 // count++ sem proteção
    static int synchronizedCounter = 0;
    static final Object LOCK = new Object();
    static final AtomicInteger atomicCounter = new AtomicInteger();

    /** Configuração imutável trocada atomicamente via AtomicReference. */
    static class PriceTable {
        final String version;
        final double discount;

        PriceTable(String version, double discount) {
            this.version = version;
            this.discount = discount;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        for (int t = 0; t < THREADS; t++) {
            pool.execute(new Runnable() {
                public void run() {
                    for (int i = 0; i < INCREMENTS; i++) {
                        unsafeCounter++;                 // corrida: atualizações perdidas
                        synchronized (LOCK) {
                            synchronizedCounter++;       // correto, mas com lock
                        }
                        atomicCounter.incrementAndGet(); // correto, sem lock (CAS)
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        int expected = THREADS * INCREMENTS;
        System.out.println("=== Contadores com " + THREADS + " threads (esperado " + expected + ") ===");
        System.out.println("int sem proteção (antigo, ERRADO): " + unsafeCounter
                + (unsafeCounter < expected ? "  <- atualizações perdidas" : "  (sorte desta vez)"));
        System.out.println("int + synchronized (antigo):     " + synchronizedCounter);
        System.out.println("AtomicInteger (novo):             " + atomicCounter.get());

        System.out.println();
        System.out.println("=== compareAndSet: reservar a última unidade do estoque ===");
        AtomicInteger stock = new AtomicInteger(1);
        for (String customer : new String[] {"Ana", "Bruno"}) {
            int current = stock.get();
            boolean reserved = current > 0 && stock.compareAndSet(current, current - 1);
            System.out.println(customer + " reservou? " + reserved + " (estoque=" + stock.get() + ")");
        }

        System.out.println();
        System.out.println("=== AtomicLong: gerador de IDs ===");
        AtomicLong ids = new AtomicLong(1000);
        System.out.println("IDs: " + ids.incrementAndGet() + ", " + ids.incrementAndGet()
                + ", " + ids.getAndAdd(10) + " -> " + ids.get());

        System.out.println();
        System.out.println("=== AtomicReference: troca atômica de configuração ===");
        AtomicReference<PriceTable> table = new AtomicReference<PriceTable>(new PriceTable("v1", 0.05));
        PriceTable old = table.get();
        boolean swapped = table.compareAndSet(old, new PriceTable("v2", 0.10));
        System.out.println("Troca v1->v2? " + swapped + "; atual=" + table.get().version
                + " desconto=" + table.get().discount);
        boolean staleSwap = table.compareAndSet(old, new PriceTable("v3", 0.0));
        System.out.println("Troca usando referência antiga (v1)? " + staleSwap + " (outro já alterou)");
    }
}
