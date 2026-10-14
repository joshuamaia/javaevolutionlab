package br.com.javalab.java09.streams;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 9 - Stream.dropWhile
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code dropWhile(predicado)} descarta elementos do <b>início</b> enquanto o
 * predicado for verdadeiro; a partir do primeiro que falhar, devolve todo o restante
 * (mesmo que elementos posteriores voltem a satisfazer o predicado).</p>
 * <p>Benefício: pular cabeçalhos, comentários iniciais ou registros antigos de dados
 * ordenados.</p>
 * <p>Antes: laço com flag booleana "já comecei?".</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/streams/StreamDropWhileExample.java
 * </pre>
 */
public class StreamDropWhileExample {

    public static void main(String[] args) {
        List<String> csvFile = List.of(
                "# relatório de vendas",
                "# gerado automaticamente",
                "pedido;cliente;valor",
                "1;Ana;150.00",
                "# comentário no meio (não é descartado!)",
                "2;Bruno;89.90");

        System.out.println("=== Abordagem antiga: flag booleana ===");
        boolean started = false;
        for (String line : csvFile) {
            if (!started && line.startsWith("#")) {
                continue;
            }
            started = true;
            System.out.println(line);
        }

        System.out.println();
        System.out.println("=== Java 9: dropWhile (pula comentários do início) ===");
        List<String> content = csvFile.stream()
                .dropWhile(line -> line.startsWith("#"))
                .collect(Collectors.toList());
        content.forEach(System.out::println);

        System.out.println();
        System.out.println("=== takeWhile + dropWhile dividem o stream ===");
        List<Integer> temperatures = List.of(18, 19, 21, 25, 30, 22);
        System.out.println("Até 21 graus (início): "
                + temperatures.stream().takeWhile(t -> t <= 21).collect(Collectors.toList()));
        System.out.println("Restante            : "
                + temperatures.stream().dropWhile(t -> t <= 21).collect(Collectors.toList()));
    }
}
