package br.com.javalab.java14.jfr;

import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.consumer.RecordingStream;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java 14 - JFR Event Streaming (JEP 349)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que e: {@code jdk.jfr.consumer.RecordingStream} permite CONSUMIR eventos do
 * JDK Flight Recorder continuamente, dentro da propria aplicacao, sem gravar um
 * arquivo .jfr e analisa-lo depois.</p>
 * <p>Beneficio: monitoramento continuo de baixo custo (CPU, GC, eventos de negocio)
 * enviando metricas para logs/dashboards em tempo real.</p>
 * <p>Antes: iniciar uma gravacao, gerar o arquivo e abri-lo com JMC ou
 * {@code RecordingFile} apos o fim.</p>
 *
 * <p>Este exemplo escuta, por cerca de 3 segundos: jdk.CPULoad, jdk.GarbageCollection
 * e um evento customizado de negocio (OrderProcessed), e depois encerra.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java14/jfr/JfrEventStreamingExample.java
 * </pre>
 */
public class JfrEventStreamingExample {

    @Name("br.com.javalab.OrderProcessed")
    @Label("Pedido processado")
    static class OrderProcessedEvent extends Event {
        @Label("Id do pedido")
        long orderId;
        @Label("Total")
        double total;
    }

    public static void main(String[] args) throws Exception {
        AtomicInteger gcCount = new AtomicInteger();
        try (RecordingStream stream = new RecordingStream()) {
            stream.enable("jdk.CPULoad").withPeriod(Duration.ofMillis(500));
            stream.enable("jdk.GarbageCollection");
            stream.enable(OrderProcessedEvent.class);

            stream.onEvent("jdk.CPULoad", e -> System.out.printf(
                    "[CPU] jvmUser=%.1f%% machine=%.1f%%%n",
                    e.getFloat("jvmUser") * 100, e.getFloat("machineTotal") * 100));
            stream.onEvent("jdk.GarbageCollection", e -> {
                if (gcCount.incrementAndGet() <= 3) {
                    System.out.println("[GC] " + e.getString("name") + " pausa=" + e.getDuration("sumOfPauses").toMillis() + " ms");
                }
            });
            stream.onEvent("br.com.javalab.OrderProcessed", e ->
                    System.out.println("[Negocio] pedido " + e.getLong("orderId") + " total=" + e.getDouble("total")));

            System.out.println("=== Streaming iniciado (cerca de 3 segundos) ===");
            stream.startAsync();

            for (int i = 1; i <= 4; i++) {
                OrderProcessedEvent event = new OrderProcessedEvent();
                event.orderId = 1000 + i;
                event.total = i * 150.0;
                event.commit();
                generateGarbage();
                Thread.sleep(400);
            }
            System.gc();
            // o JFR entrega os eventos em lotes (flush aproximadamente a cada 1 s):
            // aguarda um pouco antes de fechar para receber os ultimos
            Thread.sleep(1500);
        }
        System.out.println("=== Streaming encerrado. Coletas observadas: " + gcCount.get() + " ===");
    }

    private static void generateGarbage() {
        List<byte[]> garbage = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            garbage.add(new byte[10_000]);
        }
        garbage.clear();
    }
}
