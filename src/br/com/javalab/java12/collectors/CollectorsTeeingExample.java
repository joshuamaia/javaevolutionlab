package br.com.javalab.java12.collectors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 12 - Collectors.teeing
 *
 * <p>Status: Definitivo (API nova, sem JEP).</p>
 *
 * <p>O que e: um coletor que envia cada elemento para DOIS coletores e combina os
 * dois resultados com uma funcao final.</p>
 * <p>Beneficio: calcular duas agregacoes em uma unica passada pelo stream.</p>
 * <p>Antes: percorrer a colecao duas vezes (dois streams) ou escrever um coletor
 * customizado.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java12/collectors/CollectorsTeeingExample.java
 * </pre>
 */
public class CollectorsTeeingExample {

    static final class Order {
        final String customer;
        final double total;

        Order(String customer, double total) {
            this.customer = customer;
            this.total = total;
        }
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("Ana", 120.0), new Order("Bruno", 80.5),
                new Order("Ana", 300.0), new Order("Carla", 45.9));

        System.out.println("=== Abordagem antiga: duas passadas ===");
        double sum = orders.stream().mapToDouble(o -> o.total).sum();
        long count = orders.stream().count();
        System.out.printf("Media: %.2f%n", sum / count);

        System.out.println("=== Java 12: teeing (uma passada) ===");
        double average = orders.stream().collect(Collectors.teeing(
                Collectors.summingDouble(o -> o.total),
                Collectors.counting(),
                (total, n) -> total / n));
        System.out.printf("Media: %.2f%n", average);

        String range = orders.stream().collect(Collectors.teeing(
                Collectors.minBy((a, b) -> Double.compare(a.total, b.total)),
                Collectors.maxBy((a, b) -> Double.compare(a.total, b.total)),
                (min, max) -> "menor pedido: " + min.get().total + " (" + min.get().customer + ")"
                        + ", maior pedido: " + max.get().total + " (" + max.get().customer + ")"));
        System.out.println(range);
    }
}
