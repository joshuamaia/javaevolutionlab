package br.com.javalab.java25.jfr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedMethod;
import jdk.jfr.consumer.RecordingFile;

/**
 * Java 25 - JFR Method Timing &amp; Tracing (JEP 520)
 *
 * <p>Status: Definitivo (recurso do JDK Flight Recorder). Mesma versão: JFR CPU-Time Profiling
 * (JEP 509, experimental, só Linux) e JFR Cooperative Sampling (JEP 518).</p>
 *
 * <p>O que é: dois novos eventos do JFR que usam instrumentação de bytecode sob demanda,
 * restrita aos métodos escolhidos por um FILTRO ({@code pacote.Classe::metodo}, classe inteira
 * ou anotação):</p>
 * <ul>
 *   <li>{@code jdk.MethodTiming}: nº de invocações e tempo mínimo/médio/máximo de cada método;</li>
 *   <li>{@code jdk.MethodTrace}: um evento por chamada, com duração e stack trace (quem chamou).</li>
 * </ul>
 * <p>Benefício: medir exatamente métodos específicos em produção, sem amostragem estatística,
 * sem agentes Java de terceiros e sem alterar código. Pode ser ligado na inicialização ou em
 * tempo de execução ({@code jcmd PID JFR.start method-timing=...}).</p>
 * <p>Antes: {@code System.nanoTime()} espalhado pelo código, agentes (byte-buddy) ou
 * profilers por amostragem, que não contam invocações.</p>
 *
 * <p>Exemplo: inicia uma gravação JFR programaticamente ({@link Recording}), mede
 * {@code calculateFreight} e rastreia {@code slowLookup}, grava num arquivo temporário e lê
 * os eventos com {@link RecordingFile}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/jfr/MethodTimingExample.java
 * # equivalente por linha de comando, para qualquer aplicação:
 * java '-XX:StartFlightRecording:method-timing=br.com.javalab.java25.jfr.MethodTimingExample::calculateFreight,filename=rec.jfr' -cp out br.com.javalab.java25.jfr.MethodTimingExample
 * jfr view method-timing rec.jfr
 * </pre>
 */
public class MethodTimingExample {

    static final String CLASS = MethodTimingExample.class.getName();

    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("method-timing", ".jfr");
        try (Recording recording = new Recording()) {
            recording.enable("jdk.MethodTiming").with("filter", CLASS + "::calculateFreight");
            recording.enable("jdk.MethodTrace").with("filter", CLASS + "::slowLookup");
            recording.start();

            System.out.println("=== Executando a carga de trabalho com a gravação ativa ===");
            double total = 0;
            for (int i = 0; i < 2_000; i++) {
                total += calculateFreight(i % 50 + 1);
            }
            for (int i = 0; i < 3; i++) {
                slowLookup("CEP-0" + i);
            }
            System.out.printf("Frete total calculado: R$ %.2f%n", total);

            recording.stop();
            recording.dump(file);
        }

        System.out.println();
        System.out.println("=== Eventos jdk.MethodTiming ===");
        for (RecordedEvent event : RecordingFile.readAllEvents(file)) {
            if (event.getEventType().getName().equals("jdk.MethodTiming") && event.getLong("invocations") > 0) {
                RecordedMethod method = event.getValue("method");
                System.out.printf("%s: %d invocações | média %s | máx %s%n", method.getName(),
                        event.getLong("invocations"), format(event.getDuration("average")),
                        format(event.getDuration("maximum")));
            }
        }

        System.out.println();
        System.out.println("=== Eventos jdk.MethodTrace (um por chamada) ===");
        for (RecordedEvent event : RecordingFile.readAllEvents(file)) {
            if (event.getEventType().getName().equals("jdk.MethodTrace")) {
                RecordedMethod method = event.getValue("method");
                // primeiro frame da nossa classe que não seja o próprio método (pula frames internos do JFR)
                String caller = event.getStackTrace() == null ? "?" : event.getStackTrace().getFrames().stream()
                        .map(frame -> frame.getMethod())
                        .filter(m -> m.getType().getName().equals(CLASS) && !m.getName().equals(method.getName()))
                        .map(RecordedMethod::getName).findFirst().orElse("?");
                System.out.println(method.getName() + " levou " + format(event.getDuration()) + " (chamado por " + caller + ")");
            }
        }
        Files.deleteIfExists(file);
    }

    static double calculateFreight(int weightKg) {
        double base = 12.5;
        for (int i = 0; i < weightKg * 50; i++) {
            base += Math.sqrt(i) * 0.0001;
        }
        return base + weightKg * 1.8;
    }

    static String slowLookup(String zipCode) throws InterruptedException {
        Thread.sleep(30); // simula consulta remota
        return zipCode + " -> São Paulo/SP";
    }

    static String format(Duration d) {
        // valores ausentes (Long.MIN_VALUE em ticks) viram durações absurdas: tratamos como "n/d"
        if (d == null || d.isNegative() || d.toDays() > 1) return "n/d";
        return String.format("%.3f ms", d.toNanos() / 1_000_000.0);
    }
}
