package br.com.javalab.java08.streams;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Java 8 - Stream API: reduce (JEP 107)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code reduce} combina todos os elementos em um único resultado.
 * Três formas: {@code reduce(identity, accumulator)}, {@code reduce(accumulator)}
 * (retorna Optional) e {@code reduce(identity, accumulator, combiner)} (para tipos
 * diferentes e streams paralelas).</p>
 * <p>Benefício: agregações genéricas (soma de BigDecimal, máximo, concatenação).</p>
 * <p>Requisito: o acumulador deve ser associativo e sem efeitos colaterais.</p>
 * <p>Antes: variável acumuladora mutável num laço for.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/streams/StreamReduceExample.java
 * </pre>
 */
public class StreamReduceExample {

    public static void main(String[] args) {
        List<BigDecimal> payments = Arrays.asList(
                new BigDecimal("150.00"), new BigDecimal("89.90"), new BigDecimal("1200.10"));

        System.out.println("=== Abordagem antiga ===");
        BigDecimal oldTotal = BigDecimal.ZERO;
        for (BigDecimal p : payments) {
            oldTotal = oldTotal.add(p);
        }
        System.out.println("Total (for): " + oldTotal);

        System.out.println();
        System.out.println("=== reduce(identity, accumulator) ===");
        BigDecimal total = payments.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total (reduce): " + total);

        System.out.println();
        System.out.println("=== reduce(accumulator) -> Optional ===");
        Optional<BigDecimal> max = payments.stream().reduce(BigDecimal::max);
        System.out.println("Maior pagamento: " + max.get());
        Optional<BigDecimal> none = payments.stream()
                .filter(p -> p.compareTo(new BigDecimal("5000")) > 0)
                .reduce(BigDecimal::add);
        System.out.println("Soma acima de 5000 (vazia): " + none.isPresent());

        System.out.println();
        System.out.println("=== reduce(identity, accumulator, combiner): tipo de resultado diferente ===");
        List<String> words = Arrays.asList("pedido", "confirmado", "com", "sucesso");
        int totalChars = words.parallelStream()
                .reduce(0, (acc, word) -> acc + word.length(), Integer::sum);
        System.out.println("Total de caracteres: " + totalChars);

        System.out.println();
        System.out.println("=== Atalhos prontos (preferíveis quando existem) ===");
        System.out.println("IntStream.sum(): " + words.stream().mapToInt(String::length).sum());
        System.out.println("max por comparator: " + words.stream().max(String::compareTo).get());
        System.out.println("Concatenação via reduce: "
                + words.stream().reduce("", (a, b) -> a.isEmpty() ? b : a + " " + b));
    }
}
