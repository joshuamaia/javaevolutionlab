package br.com.javalab.java08.collectors;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Java 8 - Collectors: toList, toSet, toMap, joining, summarizing, averaging (JEP 107)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@link Collectors} fornece implementações prontas de {@code Collector} para
 * a operação terminal {@code collect}: coleções, mapas, Strings e estatísticas.</p>
 * <p>Benefício: agregações comuns em uma linha, combináveis entre si.</p>
 * <p>Obs.: {@code Stream.toList()} só existe no Java 16; aqui usamos {@code Collectors.toList()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/collectors/CollectorsBasicExample.java
 * </pre>
 */
public class CollectorsBasicExample {

    static class Product {
        final String sku;
        final String name;
        final String category;
        final double price;

        Product(String sku, String name, String category, double price) {
            this.sku = sku;
            this.name = name;
            this.category = category;
            this.price = price;
        }
    }

    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
                new Product("P1", "Notebook", "informatica", 3500),
                new Product("P2", "Mouse", "informatica", 60),
                new Product("P3", "Geladeira", "eletro", 2800),
                new Product("P4", "Micro-ondas", "eletro", 650),
                new Product("P5", "Livro Java", "livros", 120));

        System.out.println("=== toList / toSet / toCollection ===");
        List<String> names = products.stream().map(p -> p.name).collect(Collectors.toList());
        Set<String> categories = products.stream().map(p -> p.category).collect(Collectors.toSet());
        TreeSet<String> sortedCategories = products.stream().map(p -> p.category)
                .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("toList: " + names);
        System.out.println("toSet:  " + categories);
        System.out.println("toCollection(TreeSet): " + sortedCategories);

        System.out.println();
        System.out.println("=== toMap ===");
        Map<String, Double> priceBySku = products.stream()
                .collect(Collectors.toMap(p -> p.sku, p -> p.price));
        System.out.println("sku -> preço: " + priceBySku);
        // chave duplicada exige função de merge (senão IllegalStateException)
        Map<String, Double> maxPriceByCategory = products.stream()
                .collect(Collectors.toMap(p -> p.category, p -> p.price, Math::max, TreeMap::new));
        System.out.println("maior preço por categoria: " + maxPriceByCategory);

        System.out.println();
        System.out.println("=== joining ===");
        System.out.println(products.stream().map(p -> p.name).collect(Collectors.joining(", ", "[", "]")));

        System.out.println();
        System.out.println("=== averaging / summing / counting ===");
        System.out.println("Preço médio: " + products.stream().collect(Collectors.averagingDouble(p -> p.price)));
        System.out.println("Soma:        " + products.stream().collect(Collectors.summingDouble(p -> p.price)));
        System.out.println("Quantidade:  " + products.stream().collect(Collectors.counting()));

        System.out.println();
        System.out.println("=== summarizingDouble ===");
        DoubleSummaryStatistics stats = products.stream().collect(Collectors.summarizingDouble(p -> p.price));
        System.out.printf("min=%.2f max=%.2f média=%.2f soma=%.2f n=%d%n",
                stats.getMin(), stats.getMax(), stats.getAverage(), stats.getSum(), stats.getCount());
    }
}
