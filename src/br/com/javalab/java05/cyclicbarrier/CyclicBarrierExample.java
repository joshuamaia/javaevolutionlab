package br.com.javalab.java05.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Java 5 - CyclicBarrier (JSR 166)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: ponto de encontro para um número fixo de threads. Cada uma chama
 * {@code await()} e fica esperando até que TODAS cheguem; então uma "barrier action"
 * opcional executa e todas seguem. A barreira é reutilizável (cíclica).</p>
 * <p>Benefício: algoritmos em fases (processa parte -> consolida -> próxima fase).</p>
 * <p>Diferença para {@code CountDownLatch}: o latch é de uso único e quem conta não
 * precisa esperar; a barreira é reutilizável e as próprias threads esperam entre si.</p>
 * <p>Evolução: {@code Phaser} (Java 7) generaliza com número variável de partes.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/cyclicbarrier/CyclicBarrierExample.java
 * </pre>
 */
public class CyclicBarrierExample {

    static final int REGIONS = 3;
    static final double[] partialTotals = new double[REGIONS];

    public static void main(String[] args) throws InterruptedException {
        final String[] regions = {"Norte", "Sul", "Sudeste"};
        final double[][] salesByMonth = {
                {100, 120},  // Norte: jan, fev
                {200, 180},  // Sul
                {300, 330}   // Sudeste
        };

        // Barrier action: roda uma vez por fase, quando as 3 threads chegam
        final CyclicBarrier barrier = new CyclicBarrier(REGIONS, new Runnable() {
            private int month = 1;

            public void run() {
                double total = 0;
                for (double partial : partialTotals) {
                    total += partial;
                }
                System.out.println(">>> Consolidação do mês " + month++ + ": total geral = " + total);
            }
        });

        System.out.println("=== Relatório de vendas em 2 fases (meses), 3 regiões em paralelo ===");
        ExecutorService pool = Executors.newFixedThreadPool(REGIONS);
        for (int r = 0; r < REGIONS; r++) {
            final int region = r;
            pool.execute(new Runnable() {
                public void run() {
                    try {
                        for (int month = 0; month < 2; month++) {
                            Thread.sleep(20 * (region + 1));
                            partialTotals[region] = salesByMonth[region][month];
                            System.out.println(regions[region] + " calculou mês " + (month + 1)
                                    + ": " + partialTotals[region]);
                            barrier.await(); // espera as outras regiões; barreira é reutilizada
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (BrokenBarrierException e) {
                        System.out.println("Barreira quebrada: outra thread falhou");
                    }
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Barreira quebrada? " + barrier.isBroken() + "; aguardando: " + barrier.getNumberWaiting());
    }
}
