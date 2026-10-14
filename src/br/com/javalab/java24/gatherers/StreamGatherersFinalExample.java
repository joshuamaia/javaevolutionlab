package br.com.javalab.java24.gatherers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

/**
 * Java 24 - Stream Gatherers (JEP 485)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 22 (JEP 461) e segunda Preview no
 * Java 23 (JEP 473), sem mudanças de API.</p>
 *
 * <p>O que é: {@code Stream.gather(Gatherer)} - operações intermediárias customizáveis
 * (com estado, 1:N, N:1, com interrupção), mais os prontos de {@code Gatherers}.</p>
 * <p>Benefício: lógica que antes "quebrava" o stream num laço imperativo cabe no pipeline,
 * de forma reutilizável e combinável ({@code andThen}).</p>
 *
 * <p>Foco deste exemplo: comparação direta com a abordagem antiga - detectar picos de
 * temperatura em leituras de sensores usando janelas deslizantes - e composição de
 * gatherers com {@code andThen}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/gatherers/StreamGatherersFinalExample.java
 * </pre>
 */
public class StreamGatherersFinalExample {

    record Reading(int minute, double celsius) {}

    public static void main(String[] args) {
        List<Reading> readings = List.of(
                new Reading(1, 21.0), new Reading(2, 21.4), new Reading(3, 25.9),
                new Reading(4, 26.3), new Reading(5, 22.0), new Reading(6, 29.8));

        System.out.println("=== Abordagem antiga: laço com índices e sublistas ===");
        List<String> oldAlerts = new ArrayList<>();
        for (int i = 0; i + 2 <= readings.size(); i++) {
            Reading previous = readings.get(i);
            Reading current = readings.get(i + 1);
            if (current.celsius() - previous.celsius() > 3.0) {
                oldAlerts.add("minuto " + current.minute() + ": +"
                        + String.format("%.1f", current.celsius() - previous.celsius()) + " °C");
            }
        }
        oldAlerts.forEach(System.out::println);

        System.out.println();
        System.out.println("=== Com Gatherers.windowSliding(2) ===");
        readings.stream()
                .gather(Gatherers.windowSliding(2))
                .filter(pair -> pair.get(1).celsius() - pair.get(0).celsius() > 3.0)
                .map(pair -> "minuto " + pair.get(1).minute() + ": +"
                        + String.format("%.1f", pair.get(1).celsius() - pair.get(0).celsius()) + " °C")
                .forEach(System.out::println);

        System.out.println();
        System.out.println("=== Composição: janela fixa de 3 min -> média -> só as médias acima de 23 °C ===");
        Gatherer<Reading, ?, List<Reading>> perThreeMinutes = Gatherers.windowFixed(3);
        Gatherer<List<Reading>, ?, Double> average = Gatherer.of((_, window, downstream) ->
                downstream.push(window.stream().mapToDouble(Reading::celsius).average().orElse(0)));
        readings.stream()
                .gather(perThreeMinutes.andThen(average))
                .filter(avg -> avg > 23.0)
                .forEach(avg -> System.out.printf("média do bloco: %.2f °C%n", avg));
    }
}
