package br.com.javalab.java07.concurrency;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

/**
 * Java 7 - TransferQueue / LinkedTransferQueue (JSR 166y)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: uma {@code BlockingQueue} em que o produtor pode esperar até que um
 * consumidor RECEBA o elemento ({@code transfer}), ou tentar entregar só se houver
 * consumidor aguardando ({@code tryTransfer}).</p>
 * <p>Benefício: back-pressure/hand-off direto sem perder a possibilidade de enfileirar
 * ({@code put}). Implementação não bloqueante e eficiente.</p>
 * <p>Antes: {@code SynchronousQueue} (só hand-off) ou {@code LinkedBlockingQueue} (só fila).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/concurrency/TransferQueueExample.java
 * </pre>
 */
public class TransferQueueExample {

    public static void main(String[] args) throws InterruptedException {
        final TransferQueue<String> queue = new LinkedTransferQueue<>();

        System.out.println("=== tryTransfer sem consumidor ===");
        boolean delivered = queue.tryTransfer("order-1");
        System.out.println("tryTransfer(order-1) entregue? " + delivered + " | tamanho da fila: " + queue.size());

        System.out.println();
        System.out.println("=== transfer: produtor espera o consumidor receber ===");
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    for (int i = 0; i < 2; i++) {
                        String order = queue.poll(2, TimeUnit.SECONDS); // timeout: nunca bloqueia para sempre
                        System.out.println("Consumidor recebeu: " + order);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "consumer");
        consumer.start();

        long start = System.nanoTime();
        queue.transfer("order-2"); // bloqueia até ser consumido
        long waited = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Produtor: order-2 entregue após ~" + waited + " ms de espera");
        System.out.println("hasWaitingConsumer? " + queue.hasWaitingConsumer());

        System.out.println();
        System.out.println("=== tryTransfer com timeout ===");
        boolean ok = queue.tryTransfer("order-3", 300, TimeUnit.MILLISECONDS);
        System.out.println("tryTransfer(order-3, 300ms) entregue? " + ok
                + " (se false, o elemento NÃO fica na fila)");

        consumer.join();
        System.out.println("Restante na fila: " + queue);
    }
}
