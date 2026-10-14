package br.com.javalab.java05.reentrantlock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java 5 - ReentrantLock e Condition (JSR 166, java.util.concurrent.locks)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: lock explícito com a mesma semântica de exclusão mútua e reentrância do
 * {@code synchronized}, mas com recursos extras: {@code tryLock()} (com ou sem
 * timeout), {@code lockInterruptibly()}, opção de justiça (fair) e várias
 * {@code Condition}s por lock (substituem {@code wait/notify}).</p>
 * <p>Benefício: evitar esperas infinitas, tratar contenção e separar condições de
 * espera ("não cheio" / "não vazio").</p>
 * <p>Cuidado: o {@code unlock()} deve estar SEMPRE em um {@code finally}; com
 * {@code synchronized} a liberação é automática.</p>
 * <p>Antes: {@code synchronized} + {@code Object.wait()/notifyAll()} (um único conjunto
 * de espera por objeto).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/reentrantlock/ReentrantLockExample.java
 * </pre>
 */
public class ReentrantLockExample {

    /** Antes: conta bancária com synchronized. */
    static class SyncAccount {
        private double balance = 100;

        synchronized void withdraw(double amount) { // lock implícito em "this"
            balance -= amount;
        }

        synchronized double getBalance() {
            return balance;
        }
    }

    /** Depois: mesma conta com ReentrantLock. */
    static class LockAccount {
        private final ReentrantLock lock = new ReentrantLock(true); // fair = ordem de chegada
        private double balance = 100;

        void withdraw(double amount) {
            lock.lock();
            try {
                balance -= amount;
                audit(); // reentrância: a mesma thread adquire o lock de novo
            } finally {
                lock.unlock();
            }
        }

        private void audit() {
            lock.lock();
            try {
                System.out.println("  auditoria: saldo=" + balance + ", holdCount=" + lock.getHoldCount());
            } finally {
                lock.unlock();
            }
        }
    }

    /** Buffer limitado com duas Conditions sobre o mesmo lock. */
    static class BoundedOrderBuffer {
        private final Lock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();
        private final Queue<String> items = new LinkedList<String>();
        private final int capacity;

        BoundedOrderBuffer(int capacity) {
            this.capacity = capacity;
        }

        void put(String order) throws InterruptedException {
            lock.lock();
            try {
                while (items.size() == capacity) {
                    notFull.await();
                }
                items.add(order);
                notEmpty.signal(); // acorda apenas quem espera por "não vazio"
            } finally {
                lock.unlock();
            }
        }

        String take() throws InterruptedException {
            lock.lock();
            try {
                while (items.isEmpty()) {
                    notEmpty.await();
                }
                String order = items.poll();
                notFull.signal();
                return order;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Abordagem antiga: synchronized ===");
        SyncAccount sync = new SyncAccount();
        sync.withdraw(30);
        System.out.println("Saldo: " + sync.getBalance());

        System.out.println();
        System.out.println("=== Abordagem nova: ReentrantLock (reentrante) ===");
        new LockAccount().withdraw(30);

        System.out.println();
        System.out.println("=== tryLock com timeout: não esperar para sempre ===");
        final ReentrantLock reportLock = new ReentrantLock();
        Thread holder = new Thread(new Runnable() {
            public void run() {
                reportLock.lock();
                try {
                    Thread.sleep(500); // segura o lock por 500 ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    reportLock.unlock();
                }
            }
        });
        holder.start();
        Thread.sleep(50);
        if (reportLock.tryLock(100, TimeUnit.MILLISECONDS)) {
            reportLock.unlock();
            System.out.println("Conseguiu o lock");
        } else {
            System.out.println("Lock ocupado (isLocked=" + reportLock.isLocked()
                    + "): relatório adiado em vez de travar a thread");
        }
        holder.join();

        System.out.println();
        System.out.println("=== Condition: buffer limitado (capacidade 2) ===");
        final BoundedOrderBuffer buffer = new BoundedOrderBuffer(2);
        Thread producer = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 1; i <= 4; i++) {
                        buffer.put("PED-" + i);
                        System.out.println("Produziu PED-" + i);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        producer.start();
        for (int i = 0; i < 4; i++) {
            Thread.sleep(30);
            System.out.println("  Consumiu " + buffer.take());
        }
        producer.join();
    }
}
