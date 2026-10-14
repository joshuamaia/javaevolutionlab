package br.com.javalab.java16.streams;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 16 - Stream.mapMulti
 *
 * <p>Status: Definitivo (API nova, sem JEP).</p>
 *
 * <p>O que e: {@code mapMulti((elemento, consumer) -> ...)} substitui cada elemento por
 * zero ou mais elementos, chamando {@code consumer.accept(...)} quantas vezes quiser.</p>
 * <p>Beneficio: alternativa ao flatMap que nao exige criar um Stream por elemento;
 * util quando cada elemento gera poucos (ou nenhum) resultados, e para filtrar e
 * converter tipo ao mesmo tempo.</p>
 * <p>Antes: {@code flatMap(e -> Stream.of(...))} ou filter + map.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/streams/StreamMapMultiExample.java
 * </pre>
 */
public class StreamMapMultiExample {

    record Order(int id, List<String> items) {}

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(1001, List.of("Notebook", "Mouse")),
                new Order(1002, List.of()),
                new Order(1003, List.of("Teclado")));

        System.out.println("=== Abordagem antiga: flatMap (um Stream por pedido) ===");
        List<String> old = orders.stream()
                .flatMap(o -> o.items().stream().map(item -> o.id() + ":" + item))
                .collect(Collectors.toList());
        System.out.println(old);

        System.out.println("=== Java 16: mapMulti ===");
        List<String> lines = orders.stream()
                .<String>mapMulti((order, downstream) -> {
                    for (String item : order.items()) {
                        downstream.accept(order.id() + ":" + item);
                    }
                })
                .toList();
        System.out.println(lines);

        System.out.println("=== Filtrar e converter tipo de uma vez ===");
        List<Object> mixed = List.of(10, "abc", 25, 3.5, 40);
        List<Integer> quantities = mixed.stream()
                .<Integer>mapMulti((value, downstream) -> {
                    if (value instanceof Integer i) {
                        downstream.accept(i);
                    }
                })
                .toList();
        System.out.println("Somente inteiros: " + quantities);
    }
}
