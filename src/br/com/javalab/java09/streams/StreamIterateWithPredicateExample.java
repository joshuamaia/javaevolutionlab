package br.com.javalab.java09.streams;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 9 - Stream.iterate com predicado (seed, hasNext, next)
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: nova sobrecarga {@code Stream.iterate(seed, hasNext, next)} que funciona
 * como um laço {@code for(init; condição; incremento)} — o stream termina quando a
 * condição fica falsa.</p>
 * <p>Benefício: gerar sequências finitas sem precisar de {@code limit} (que exige saber
 * a quantidade de antemão).</p>
 * <p>Antes (Java 8): {@code Stream.iterate(seed, next)} era infinito e precisava de
 * {@code limit(n)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/streams/StreamIterateWithPredicateExample.java
 * </pre>
 */
public class StreamIterateWithPredicateExample {

    public static void main(String[] args) {
        System.out.println("=== Java 8: iterate infinito + limit ===");
        System.out.println(Stream.iterate(1, n -> n * 2).limit(8).collect(Collectors.toList()));

        System.out.println();
        System.out.println("=== Java 9: iterate(seed, hasNext, next) ===");
        System.out.println("Potências de 2 menores que 200: "
                + Stream.iterate(1, n -> n < 200, n -> n * 2).collect(Collectors.toList()));

        System.out.println();
        System.out.println("=== Exemplo realista: parcelas mensais de um pedido ===");
        LocalDate firstDueDate = LocalDate.of(2017, 9, 21);
        LocalDate lastDueDate = LocalDate.of(2018, 1, 31);
        Stream.iterate(firstDueDate, date -> !date.isAfter(lastDueDate), date -> date.plusMonths(1))
                .forEach(date -> System.out.println("Vencimento: " + date));

        System.out.println();
        System.out.println("=== Equivale ao laço for ===");
        for (int n = 1; n < 200; n = n * 2) {
            System.out.print(n + " ");
        }
        System.out.println();
    }
}
