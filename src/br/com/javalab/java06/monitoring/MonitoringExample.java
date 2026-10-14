package br.com.javalab.java06.monitoring;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MonitorInfo;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Java 6 - Melhorias de monitoramento e gerenciamento (java.lang.management)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o Java 6 ampliou a API do Java 5 (JSR 174):</p>
 * <ul>
 *   <li>{@code ThreadMXBean.findDeadlockedThreads()}: detecta deadlocks também com
 *   locks de {@code java.util.concurrent} ("ownable synchronizers"); o Java 5 só tinha
 *   {@code findMonitorDeadlockedThreads()} (apenas {@code synchronized}).</li>
 *   <li>{@code dumpAllThreads(lockedMonitors, lockedSynchronizers)} e
 *   {@code ThreadInfo.getLockInfo/getLockedMonitors/getLockedSynchronizers}: thread
 *   dump programático com informação de locks ({@code LockInfo}, {@code MonitorInfo}).</li>
 *   <li>{@code OperatingSystemMXBean.getSystemLoadAverage()} (-1 se indisponível, ex.: Windows).</li>
 *   <li>Attach API ({@code com.sun.tools.attach}): ferramentas como jconsole, jstack, jmap
 *   e jcmd conectam a uma JVM local já em execução sem flags de inicialização.</li>
 * </ul>
 * <p>O exemplo cria dois deadlocks controlados com threads daemon (um com
 * {@code synchronized}, outro com {@code ReentrantLock}), detecta e mostra os locks.
 * O deadlock de {@code ReentrantLock} é desfeito por interrupção
 * ({@code lockInterruptibly}); o de monitor não tem saída, mas as threads são daemon e
 * não impedem a JVM de terminar.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/monitoring/MonitoringExample.java
 * </pre>
 * <p>Para inspecionar ao vivo: {@code jconsole}, {@code jstack <pid>} ou {@code jcmd <pid> Thread.print}.</p>
 */
public class MonitoringExample {

    static final Object STOCK = new Object();
    static final Object PAYMENT = new Object();
    static final ReentrantLock INVOICE = new ReentrantLock();
    static final ReentrantLock SHIPPING = new ReentrantLock();

    /** Deadlock com synchronized: cada thread pega um monitor e espera o outro. */
    static Thread monitorDeadlock(String name, final Object first, final Object second, final CountDownLatch both) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (first) {
                    both.countDown();
                    awaitQuietly(both);
                    synchronized (second) {
                        System.out.println("nunca chega aqui");
                    }
                }
            }
        }, name);
        t.setDaemon(true);
        return t;
    }

    /** Deadlock com ReentrantLock, mas usando lockInterruptibly (pode ser desfeito). */
    static Thread lockDeadlock(String name, final ReentrantLock first, final ReentrantLock second, final CountDownLatch both) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                first.lock();
                try {
                    both.countDown();
                    awaitQuietly(both);
                    second.lockInterruptibly();
                    second.unlock();
                } catch (InterruptedException e) {
                    System.out.println("  " + Thread.currentThread().getName() + " interrompida: liberando locks");
                } finally {
                    first.unlock();
                }
            }
        }, name);
        t.setDaemon(true);
        return t;
    }

    static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();

        CountDownLatch monitorsHeld = new CountDownLatch(2);
        monitorDeadlock("estoque->pagamento", STOCK, PAYMENT, monitorsHeld).start();
        monitorDeadlock("pagamento->estoque", PAYMENT, STOCK, monitorsHeld).start();
        CountDownLatch locksHeld = new CountDownLatch(2);
        Thread a = lockDeadlock("nota->envio", INVOICE, SHIPPING, locksHeld);
        Thread b = lockDeadlock("envio->nota", SHIPPING, INVOICE, locksHeld);
        a.start();
        b.start();

        long[] deadlocked = null;
        for (int i = 0; i < 50 && (deadlocked == null || deadlocked.length < 4); i++) {
            Thread.sleep(50); // aguarda as 4 threads ficarem bloqueadas
            deadlocked = threads.findDeadlockedThreads();
        }

        System.out.println("=== Java 5: findMonitorDeadlockedThreads (só synchronized) ===");
        long[] monitorOnly = threads.findMonitorDeadlockedThreads();
        System.out.println("Threads em deadlock: " + (monitorOnly == null ? 0 : monitorOnly.length));

        System.out.println();
        System.out.println("=== Java 6: findDeadlockedThreads (monitores + ReentrantLock) ===");
        System.out.println("Threads em deadlock: " + (deadlocked == null ? 0 : deadlocked.length));
        if (deadlocked != null) {
            for (ThreadInfo info : threads.getThreadInfo(deadlocked, true, true)) {
                LockInfo waitingFor = info.getLockInfo();
                System.out.println("- " + info.getThreadName() + " [" + info.getThreadState() + "]");
                System.out.println("    espera: " + waitingFor + " (dono: " + info.getLockOwnerName() + ")");
                for (MonitorInfo m : info.getLockedMonitors()) {
                    System.out.println("    segura monitor: " + m + " em " + m.getLockedStackFrame().getMethodName());
                }
                for (LockInfo l : info.getLockedSynchronizers()) {
                    System.out.println("    segura synchronizer: " + l);
                }
            }
        }

        System.out.println();
        System.out.println("=== Desfazendo o deadlock de ReentrantLock por interrupção ===");
        a.interrupt();
        a.join(2000);
        b.join(2000);
        long[] remaining = threads.findDeadlockedThreads();
        System.out.println("Deadlocks restantes: " + (remaining == null ? 0 : remaining.length)
                + " (as de synchronized não podem ser interrompidas; são daemon)");

        System.out.println();
        System.out.println("=== dumpAllThreads(true, true): resumo ===");
        ThreadInfo[] all = threads.dumpAllThreads(true, true);
        System.out.println("Threads no dump: " + all.length);

        System.out.println();
        System.out.println("=== MemoryMXBean e OperatingSystemMXBean ===");
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        System.out.println("Heap: " + memory.getHeapMemoryUsage());
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        double load = os.getSystemLoadAverage(); // novo no Java 6
        System.out.println("Load average (1 min): " + (load < 0 ? "indisponível neste SO (" + load + ")" : String.valueOf(load)));
        System.out.println("Processadores: " + os.getAvailableProcessors());
        System.out.println();
        System.out.println("Attach API: jconsole/jstack/jcmd conectam a esta JVM pelo PID: "
                + ManagementFactory.getRuntimeMXBean().getName());
    }
}
