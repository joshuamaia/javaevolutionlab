package br.com.javalab.java08.streams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Java 8 - Stream API: map, mapToInt/mapToDouble, sorted (JEP 107)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code map(Function)} transforma cada elemento em outro valor;
 * {@code mapToInt}/{@code mapToDouble} produzem streams primitivas ({@code IntStream},
 * {@code DoubleStream}) com {@code sum}, {@code average}, {@code summaryStatistics}.</p>
 * <p>Benefício: projeções e cálculos sem listas temporárias e sem boxing.</p>
 * <p>Antes: laço preenchendo uma nova lista e acumuladores manuais.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/streams/StreamMapExample.java
 * </pre>
 */
public class StreamMapExample {

    static class Product {
        final String name;
        final double price;
        final int stock;

        Product(String name, double price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        String getName() {
            return name;
        }

        double getPrice() {
            return price;
        }
    }

    public static void main(String[] args) {
        List<Product> catalog = Arrays.asList(
                new Product("notebook", 3500.0, 4),
                new Product("mouse", 60.0, 120),
                new Product("monitor", 900.0, 15),
                new Product("headset", 250.0, 40));

        System.out.println("=== map: projetar nomes ===");
        List<String> names = catalog.stream()
                .map(Product::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println(names);

        System.out.println();
        System.out.println("=== map + sorted: rótulos ordenados por preço desc ===");
        catalog.stream()
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed())
                .map(p -> String.format("%-10s R$ %8.2f", p.name, p.price))
                .forEach(System.out::println);

        System.out.println();
        System.out.println("=== mapToInt / mapToDouble: streams primitivas ===");
        int totalUnits = catalog.stream().mapToInt(p -> p.stock).sum();
        double inventoryValue = catalog.stream().mapToDouble(p -> p.price * p.stock).sum();
        System.out.println("Unidades em estoque: " + totalUnits);
        System.out.printf("Valor do inventário: R$ %.2f%n", inventoryValue);

        DoubleSummaryStatistics stats = catalog.stream().mapToDouble(Product::getPrice).summaryStatistics();
        System.out.println("Estatísticas de preço: " + stats);

        System.out.println();
        System.out.println("=== IntStream.range + mapToObj ===");
        IntStream.rangeClosed(1, 3)
                .mapToObj(i -> "Parcela " + i + "/3")
                .forEach(System.out::println);

        System.out.println();
        System.out.println("=== peek para depuração ===");
        List<Integer> lengths = names.stream()
                .peek(n -> System.out.println("  processando " + n))
                .map(String::length)
                .collect(Collectors.toList());
        System.out.println("Tamanhos: " + lengths);
    }
}
