package br.com.javalab.java08.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 8 - Stream API: flatMap (JEP 107)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code flatMap(Function<T, Stream<R>>)} transforma cada elemento em uma
 * stream e "achata" todas em uma única stream.</p>
 * <p>Benefício: percorrer estruturas aninhadas (pedidos -> itens, linhas -> palavras)
 * sem laços aninhados.</p>
 * <p>Antes: dois laços for aninhados adicionando numa lista.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/streams/StreamFlatMapExample.java
 * </pre>
 */
public class StreamFlatMapExample {

    static class Order {
        final String customer;
        final List<String> items;

        Order(String customer, String... items) {
            this.customer = customer;
            this.items = Arrays.asList(items);
        }
    }

    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
                new Order("Alice", "mouse", "teclado"),
                new Order("Bob", "monitor"),
                new Order("Carol", "mouse", "headset", "webcam"));

        System.out.println("=== map vs flatMap ===");
        List<List<String>> nested = orders.stream().map(o -> o.items).collect(Collectors.toList());
        System.out.println("map:     " + nested + " (lista de listas)");
        List<String> flat = orders.stream().flatMap(o -> o.items.stream()).collect(Collectors.toList());
        System.out.println("flatMap: " + flat);

        System.out.println();
        System.out.println("=== Itens distintos vendidos, ordenados ===");
        System.out.println(orders.stream()
                .flatMap(o -> o.items.stream())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", ")));

        System.out.println();
        System.out.println("=== Linhas de log -> palavras ===");
        List<String> logLines = Arrays.asList("ERROR db timeout", "INFO user login", "ERROR db deadlock");
        long errorWords = logLines.stream()
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .filter(w -> w.equals("ERROR"))
                .count();
        System.out.println("Ocorrências de ERROR: " + errorWords);

        System.out.println();
        System.out.println("=== Pares cliente/item (flatMap mantendo contexto) ===");
        orders.stream()
                .flatMap(o -> o.items.stream().map(item -> o.customer + " -> " + item))
                .forEach(System.out::println);

        System.out.println();
        System.out.println("=== Stream.of + flatMap para juntar coleções ===");
        List<String> a = Arrays.asList("SP", "RJ");
        List<String> b = Arrays.asList("MG");
        System.out.println(Stream.of(a, b).flatMap(List::stream).collect(Collectors.toList()));
    }
}
