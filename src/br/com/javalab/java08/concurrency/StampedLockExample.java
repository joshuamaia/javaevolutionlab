package br.com.javalab.java08.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

/**
 * Java 8 - StampedLock (JEP 155 - Concurrency Updates)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: lock com três modos — escrita, leitura e <b>leitura otimista</b>.
 * A leitura otimista ({@code tryOptimisticRead}) não bloqueia: lê os dados e depois
 * valida ({@code validate(stamp)}) se houve escrita no meio; se houve, refaz com lock de leitura.</p>
 * <p>Benefício: leituras muito frequentes com escritas raras ficam mais rápidas que com
 * {@code ReentrantReadWriteLock}.</p>
 * <p>Cuidados: NÃO é reentrante, não suporta {@code Condition} e o stamp precisa ser
 * passado ao unlock.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/concurrency/StampedLockExample.java
 * </pre>
 */
public class StampedLockExample {

    /** Posição de um entregador (x, y) atualizada raramente e lida com frequência. */
    static class CourierPosition {
        private final StampedLock lock = new StampedLock();
        private double x;
        private double y;
        private final AtomicInteger optimisticFailures = new AtomicInteger();

        void move(double dx, double dy) {
            long stamp = lock.writeLock();
            try {
                x += dx;
                y += dy;
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        double distanceFromOrigin() {
            long stamp = lock.tryOptimisticRead(); // não bloqueia
            double currentX = x;
            double currentY = y;
            if (!lock.validate(stamp)) {          // houve escrita no meio da leitura?
                stamp = lock.readLock();          // fallback: leitura pessimista
                try {
                    optimisticFailures.incrementAndGet();
                    currentX = x;
                    currentY = y;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CourierPosition position = new CourierPosition();

        System.out.println("=== Leitura otimista sem concorrência ===");
        position.move(3, 4);
        System.out.println("Distância: " + position.distanceFromOrigin());

        System.out.println();
        System.out.println("=== Leitores frequentes + escritor ocasional ===");
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            for (int i = 0; i < 1_000; i++) {
                position.move(0.001, 0.001);
            }
        });
        for (int r = 0; r < 3; r++) {
            executor.execute(() -> {
                double last = 0;
                for (int i = 0; i < 200_000; i++) {
                    last = position.distanceFromOrigin();
                }
                System.out.printf("  leitor terminou, última distância lida: %.3f%n", last);
            });
        }
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        System.out.printf("Distância final: %.3f%n", position.distanceFromOrigin());
        System.out.println("Leituras otimistas que precisaram de fallback: " + position.optimisticFailures
                + " (valor varia a cada execução)");

        System.out.println();
        System.out.println("=== Conversão de lock: leitura -> escrita ===");
        StampedLock lock = new StampedLock();
        long stamp = lock.readLock();
        long writeStamp = lock.tryConvertToWriteLock(stamp);
        System.out.println("Conversão para escrita bem-sucedida? " + (writeStamp != 0L));
        lock.unlock(writeStamp != 0L ? writeStamp : stamp);
    }
}
