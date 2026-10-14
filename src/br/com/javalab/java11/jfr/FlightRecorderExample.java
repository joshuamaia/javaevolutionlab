package br.com.javalab.java11.jfr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

/**
 * Java 11 - Java Flight Recorder aberto (JEP 328)
 *
 * <p>Status: Produto (JVM) + API {@code jdk.jfr} definitiva.</p>
 *
 * <p>O que é: o JFR, antes recurso comercial do Oracle JDK, foi aberto no OpenJDK 11. Ele
 * coleta eventos da JVM e da aplicação (GC, locks, I/O, alocação, eventos customizados)
 * com overhead baixíssimo (~1%), gravando em arquivos {@code .jfr}.</p>
 * <p>Benefício: profiling e diagnóstico em produção sem agentes externos. Os arquivos são
 * analisados no JDK Mission Control, com {@code jfr print} ou pela API
 * {@code jdk.jfr.consumer}.</p>
 * <p>Antes: só no Oracle JDK com {@code -XX:+UnlockCommercialFeatures}.</p>
 *
 * <p>Comandos para experimentar:</p>
 * <pre>
 * java -XX:StartFlightRecording=duration=30s,filename=app.jfr MinhaApp
 * jcmd &lt;pid&gt; JFR.start / JFR.dump filename=app.jfr / JFR.stop
 * jfr print --events br.com.javalab.OrderPlaced app.jfr
 * jfr summary app.jfr
 * </pre>
 *
 * <p>Este exemplo cria um evento customizado, grava uma {@code Recording} programática num
 * arquivo temporário e lê os eventos de volta com {@code RecordingFile}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/jfr/FlightRecorderExample.java
 * </pre>
 */
public class FlightRecorderExample {

    @Name("br.com.javalab.OrderPlaced")
    @Label("Pedido Realizado")
    @Category("Java Evolution Lab")
    static class OrderPlacedEvent extends Event {
        @Label("ID do pedido")
        String orderId;

        @Label("Valor total")
        double total;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Path file = Files.createTempFile("pedidos", ".jfr");
        try {
            System.out.println("=== Gravando eventos com jdk.jfr.Recording ===");
            try (Recording recording = new Recording()) {
                recording.enable(OrderPlacedEvent.class);
                recording.enable("jdk.GarbageCollection");
                recording.start();

                for (String id : List.of("P-1", "P-2", "P-3")) {
                    OrderPlacedEvent event = new OrderPlacedEvent();
                    event.begin();
                    Thread.sleep(20); // simula o processamento do pedido
                    event.orderId = id;
                    event.total = 150.0 * Integer.parseInt(id.substring(2));
                    event.commit();
                }
                System.gc();

                recording.stop();
                recording.dump(file);
            }
            System.out.println("Arquivo: " + file.getFileName() + " (" + Files.size(file) / 1024 + " KB)");

            System.out.println();
            System.out.println("=== Lendo o arquivo .jfr com RecordingFile ===");
            for (RecordedEvent event : RecordingFile.readAllEvents(file)) {
                String type = event.getEventType().getName();
                if (type.equals("br.com.javalab.OrderPlaced")) {
                    System.out.printf("  %s pedido=%s total=%.2f duração=%d ms%n", event.getEventType().getLabel(),
                            event.getString("orderId"), event.getDouble("total"), event.getDuration().toMillis());
                } else if (type.equals("jdk.GarbageCollection")) {
                    System.out.println("  GC: " + event.getString("name") + " causa=" + event.getString("cause"));
                }
            }
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
