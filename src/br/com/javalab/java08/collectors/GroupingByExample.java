package br.com.javalab.java08.collectors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Java 8 - Collectors.groupingBy com collectors downstream (JEP 107)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code groupingBy(classificador)} agrupa elementos num {@code Map<K, List<T>>};
 * as sobrecargas aceitam um collector "downstream" (counting, mapping, summingDouble,
 * maxBy...) e uma fábrica de mapa.</p>
 * <p>Benefício: o equivalente ao GROUP BY do SQL em uma expressão.</p>
 * <p>Antes: {@code Map} manual com {@code if (!map.containsKey(k)) map.put(k, new ArrayList())}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/collectors/GroupingByExample.java
 * </pre>
 */
public class GroupingByExample {

    static class Sale {
        final String region;
        final String seller;
        final String product;
        final double amount;

        Sale(String region, String seller, String product, double amount) {
            this.region = region;
            this.seller = seller;
            this.product = product;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return seller + ":" + product;
        }
    }

    public static void main(String[] args) {
        List<Sale> sales = Arrays.asList(
                new Sale("Sudeste", "Ana", "notebook", 3500),
                new Sale("Sudeste", "Ana", "mouse", 60),
                new Sale("Sul", "Bruno", "monitor", 900),
                new Sale("Nordeste", "Caio", "notebook", 3400),
                new Sale("Sul", "Bruno", "mouse", 55),
                new Sale("Sudeste", "Duda", "monitor", 950));

        System.out.println("=== groupingBy simples: Map<String, List<Sale>> ===");
        Map<String, List<Sale>> byRegion = sales.stream().collect(Collectors.groupingBy(s -> s.region));
        System.out.println(byRegion);

        System.out.println();
        System.out.println("=== downstream counting (mapa ordenado com TreeMap) ===");
        Map<String, Long> countByRegion = sales.stream()
                .collect(Collectors.groupingBy(s -> s.region, TreeMap::new, Collectors.counting()));
        System.out.println(countByRegion);

        System.out.println();
        System.out.println("=== downstream mapping + toSet: produtos vendidos por região ===");
        Map<String, Set<String>> productsByRegion = sales.stream()
                .collect(Collectors.groupingBy(s -> s.region, Collectors.mapping(s -> s.product, Collectors.toSet())));
        System.out.println(productsByRegion);

        System.out.println();
        System.out.println("=== downstream summingDouble e maxBy ===");
        Map<String, Double> revenueBySeller = sales.stream()
                .collect(Collectors.groupingBy(s -> s.seller, Collectors.summingDouble(s -> s.amount)));
        System.out.println("Faturamento por vendedor: " + revenueBySeller);
        Map<String, Optional<Sale>> biggestByRegion = sales.stream()
                .collect(Collectors.groupingBy(s -> s.region,
                        Collectors.maxBy((a, b) -> Double.compare(a.amount, b.amount))));
        System.out.println("Maior venda por região: " + biggestByRegion);

        System.out.println();
        System.out.println("=== Agrupamento em dois níveis: região -> produto -> quantidade ===");
        Map<String, Map<String, Long>> nested = sales.stream()
                .collect(Collectors.groupingBy(s -> s.region, TreeMap::new,
                        Collectors.groupingBy(s -> s.product, TreeMap::new, Collectors.counting())));
        nested.forEach((region, counts) -> System.out.println("  " + region + " -> " + counts));
    }
}
