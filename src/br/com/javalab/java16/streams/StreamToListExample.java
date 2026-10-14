package br.com.javalab.java16.streams;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 16 - Stream.toList()
 *
 * <p>Status: Definitivo (API nova, sem JEP).</p>
 *
 * <p>O que e: {@code stream.toList()} coleta os elementos em uma lista NAO
 * modificavel, de forma mais curta que {@code collect(Collectors.toList())}.</p>
 * <p>Diferencas: {@code Collectors.toList()} nao garante o tipo da lista e, na pratica,
 * devolve um ArrayList modificavel; {@code toList()} devolve lista imutavel que ACEITA
 * nulos (diferente de {@code Collectors.toUnmodifiableList()} e {@code List.of}).</p>
 * <p>Beneficio: menos codigo e imutabilidade por padrao.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/streams/StreamToListExample.java
 * </pre>
 */
public class StreamToListExample {

    public static void main(String[] args) {
        List<String> products = List.of("notebook", "mouse", "teclado", "monitor");

        System.out.println("=== Abordagem antiga: collect(Collectors.toList()) ===");
        List<String> oldList = products.stream().map(String::toUpperCase).collect(Collectors.toList());
        oldList.add("EXTRA");
        System.out.println(oldList + " <- modificavel (" + oldList.getClass().getSimpleName() + ")");

        System.out.println("=== Java 16: toList() ===");
        List<String> newList = products.stream().map(String::toUpperCase).toList();
        System.out.println(newList);
        try {
            newList.add("EXTRA");
        } catch (UnsupportedOperationException e) {
            System.out.println("add() lanca UnsupportedOperationException: lista nao modificavel");
        }

        System.out.println("=== Nulos: toList() aceita, toUnmodifiableList() nao ===");
        List<String> withNull = products.stream().map(p -> p.startsWith("m") ? null : p).toList();
        System.out.println("toList(): " + withNull);
        try {
            products.stream().map(p -> p.startsWith("m") ? null : p).collect(Collectors.toUnmodifiableList());
        } catch (NullPointerException e) {
            System.out.println("Collectors.toUnmodifiableList(): NullPointerException");
        }
    }
}
