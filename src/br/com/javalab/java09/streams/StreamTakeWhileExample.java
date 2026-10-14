package br.com.javalab.java09.streams;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 9 - Stream.takeWhile
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code takeWhile(predicado)} devolve os elementos do <b>início</b> do stream
 * enquanto o predicado for verdadeiro e <b>para</b> no primeiro elemento que falhar.</p>
 * <p>Benefício: processar prefixos de dados ordenados (logs por horário, leituras de
 * sensor) sem percorrer o resto, inclusive em streams infinitos.</p>
 * <p>Antes: laço {@code for} com {@code break}. Diferente de {@code filter}, que
 * percorre todos os elementos.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/streams/StreamTakeWhileExample.java
 * </pre>
 */
public class StreamTakeWhileExample {

    public static void main(String[] args) {
        List<String> logLines = List.of(
                "08:00 INFO  servidor iniciado",
                "08:05 INFO  pedido 1 recebido",
                "08:07 ERROR falha no pagamento",
                "08:10 INFO  pedido 2 recebido");

        System.out.println("=== Abordagem antiga: for + break ===");
        for (String line : logLines) {
            if (line.contains("ERROR")) {
                break;
            }
            System.out.println(line);
        }

        System.out.println();
        System.out.println("=== Java 9: takeWhile (linhas até o primeiro erro) ===");
        List<String> beforeFirstError = logLines.stream()
                .takeWhile(line -> !line.contains("ERROR"))
                .collect(Collectors.toList());
        beforeFirstError.forEach(System.out::println);

        System.out.println();
        System.out.println("=== Diferença para filter ===");
        List<String> allInfo = logLines.stream()
                .filter(line -> !line.contains("ERROR"))
                .collect(Collectors.toList());
        System.out.println("filter devolve " + allInfo.size() + " linhas (inclui 08:10, após o erro)");
        System.out.println("takeWhile devolve " + beforeFirstError.size() + " linhas (para no erro)");

        System.out.println();
        System.out.println("=== Stream ordenado: preços até R$ 100 ===");
        List<Integer> sortedPrices = List.of(15, 40, 99, 120, 30);
        System.out.println(sortedPrices.stream().takeWhile(p -> p <= 100).collect(Collectors.toList())
                + "  (30 não entra: takeWhile já parou no 120)");
    }
}
