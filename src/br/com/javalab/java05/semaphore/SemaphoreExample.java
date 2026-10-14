package br.com.javalab.java05.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java 5 - Semaphore (JSR 166)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: semáforo contador com N permissões. {@code acquire()} pega uma permissão
 * (bloqueando se não houver), {@code release()} devolve; {@code tryAcquire} tenta sem
 * bloquear ou com timeout.</p>
 * <p>Benefício: limitar quantas threads usam um recurso ao mesmo tempo (conexões,
 * chamadas a uma API externa, licenças), sem escrever contadores com wait/notify.</p>
 * <p>Antes: contador + {@code synchronized} + {@code wait()/notifyAll()} feito à mão.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/semaphore/SemaphoreExample.java
 * </pre>
 */
public class SemaphoreExample {

    /** Simula um gateway de pagamento que aceita no máximo 2 chamadas simultâneas. */
    static class PaymentGateway {
        private final Semaphore permits = new Semaphore(2);
        private final AtomicInteger inUse = new AtomicInteger();
        private final AtomicInteger maxObserved = new AtomicInteger();

        void charge(String order) throws InterruptedException {
            permits.acquire();
            try {
                int now = inUse.incrementAndGet();
                updateMax(now);
                System.out.println(order + " cobrando... chamadas simultâneas=" + now);
                Thread.sleep(100);
            } finally {
                inUse.decrementAndGet();
                permits.release(); // sempre devolver no finally
            }
        }

        boolean tryQuickCharge(String order) {
            if (permits.tryAcquire()) {
                try {
                    System.out.println(order + " cobrado na hora");
                    return true;
                } finally {
                    permits.release();
                }
            }
            System.out.println(order + ": gateway ocupado, enviado para fila de reprocessamento");
            return false;
        }

        private void updateMax(int now) {
            int max;
            do {
                max = maxObserved.get();
            } while (now > max && !maxObserved.compareAndSet(max, now));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final PaymentGateway gateway = new PaymentGateway();

        System.out.println("=== 6 pedidos, no máximo 2 cobranças simultâneas ===");
        ExecutorService pool = Executors.newFixedThreadPool(6);
        for (int i = 1; i <= 6; i++) {
            final String order = "PED-" + i;
            pool.execute(new Runnable() {
                public void run() {
                    try {
                        gateway.charge(order);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        Thread.sleep(30); // as duas permissões estão em uso agora
        System.out.println();
        System.out.println("=== tryAcquire: não bloquear quando está cheio ===");
        gateway.tryQuickCharge("PED-URGENTE");

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println();
        System.out.println("Máximo de chamadas simultâneas observado: " + gateway.maxObserved.get());
        System.out.println("Permissões disponíveis ao final: " + gateway.permits.availablePermits());
    }
}
