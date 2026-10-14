package br.com.javalab.java07.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Java 7 - Phaser (JSR 166y)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: barreira de sincronização reutilizável com várias fases e número dinâmico
 * de participantes ({@code register}/{@code arriveAndDeregister}).</p>
 * <p>Benefício: combina e generaliza {@code CountDownLatch} (uso único) e
 * {@code CyclicBarrier} (participantes fixos).</p>
 * <p>Antes: encadear vários CountDownLatch ou usar CyclicBarrier com número fixo de threads.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/concurrency/PhaserExample.java
 * </pre>
 */
public class PhaserExample {

    private static final String[] PHASES = {"validar", "reservar estoque", "faturar"};

    public static void main(String[] args) throws InterruptedException {
        // onAdvance é chamado quando todos chegam ao fim de uma fase
        final Phaser phaser = new Phaser(1) { // 1 = a thread main (coordenadora)
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                System.out.println("--- Fase " + phase + " (" + PHASES[phase] + ") concluída por "
                        + registeredParties + " participantes ---");
                return phase >= PHASES.length - 1 || registeredParties == 0; // true = termina
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(3);
        String[] warehouses = {"SP", "RJ", "MG"};
        for (final String warehouse : warehouses) {
            phaser.register(); // participante dinâmico
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    for (int phase = 0; phase < PHASES.length; phase++) {
                        System.out.println("Armazém " + warehouse + ": " + PHASES[phase]);
                        if (warehouse.equals("MG") && phase == 1) {
                            System.out.println("Armazém MG sai do processo após reservar estoque");
                            phaser.arriveAndDeregister();
                            return;
                        }
                        phaser.arriveAndAwaitAdvance();
                    }
                    phaser.arriveAndDeregister();
                }
            });
        }

        // A thread main participa de cada fase até o phaser terminar
        while (!phaser.isTerminated()) {
            phaser.arriveAndAwaitAdvance();
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Phaser terminado? " + phaser.isTerminated());
    }
}
