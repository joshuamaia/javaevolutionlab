package br.com.javalab.java09.flow;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

/**
 * Java 9 - Reactive Streams: Flow API (JEP 266 - More Concurrency Updates)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code java.util.concurrent.Flow} traz as interfaces padrão de Reactive
 * Streams ({@code Publisher}, {@code Subscriber}, {@code Subscription}, {@code Processor})
 * e {@code SubmissionPublisher}, uma implementação pronta de Publisher.</p>
 * <p>Benefício: processamento assíncrono com <b>backpressure</b>: o assinante pede
 * {@code request(n)} quantos itens consegue tratar, e o publicador nunca envia mais do
 * que isso. Bibliotecas como RxJava, Reactor e Akka interoperam por essas interfaces.</p>
 * <p>Antes: cada biblioteca tinha suas próprias interfaces (org.reactivestreams).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/flow/FlowApiExample.java
 * </pre>
 */
public class FlowApiExample {

    /** Assinante lento que pede um pedido por vez (backpressure). */
    static class OrderSubscriber implements Flow.Subscriber<String> {
        private final CountDownLatch done;
        private Flow.Subscription subscription;

        OrderSubscriber(CountDownLatch done) {
            this.done = done;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            System.out.println("[assinante] inscrito; pedindo 1 item");
            subscription.request(1);
        }

        @Override
        public void onNext(String order) {
            System.out.println("[assinante] processando " + order);
            sleep(50); // simula processamento lento
            subscription.request(1); // só pede o próximo quando terminou
        }

        @Override
        public void onError(Throwable error) {
            System.out.println("[assinante] erro: " + error);
            done.countDown();
        }

        @Override
        public void onComplete() {
            System.out.println("[assinante] todos os pedidos processados");
            done.countDown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch done = new CountDownLatch(1);
        int bufferCapacity = 2;

        System.out.println("=== Publisher (SubmissionPublisher) com buffer de " + bufferCapacity + " ===");
        try (SubmissionPublisher<String> publisher = new SubmissionPublisher<>(executor, bufferCapacity)) {
            publisher.subscribe(new OrderSubscriber(done));
            List<String> orders = List.of("pedido-1", "pedido-2", "pedido-3", "pedido-4", "pedido-5", "pedido-6");
            for (String order : orders) {
                // submit bloqueia quando o buffer está cheio: o produtor é freado pelo consumidor
                int lag = publisher.submit(order);
                System.out.println("[publicador] enviou " + order + " (itens aguardando: " + lag + ")");
            }
        } // close() sinaliza onComplete depois que o buffer esvaziar

        done.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Fim.");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
