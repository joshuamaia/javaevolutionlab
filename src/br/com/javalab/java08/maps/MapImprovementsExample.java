package br.com.javalab.java08.maps;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Java 8 - Melhorias em Map: getOrDefault, putIfAbsent, forEach, replaceAll, remove/replace condicionais
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: novos métodos default na interface {@link Map}: {@code getOrDefault},
 * {@code putIfAbsent}, {@code forEach(BiConsumer)}, {@code replaceAll(BiFunction)},
 * {@code remove(k, v)}, {@code replace(k, v)} e {@code replace(k, old, new)}.</p>
 * <p>Benefício: operações comuns sem verificações manuais; vários vieram do
 * {@code ConcurrentMap} e agora existem em todo Map.</p>
 * <p>Obs.: HashMap no Java 8 também passou a converter buckets com muitas colisões em
 * árvores (JEP 180), melhorando o pior caso de O(n) para O(log n).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/maps/MapImprovementsExample.java
 * </pre>
 */
public class MapImprovementsExample {

    public static void main(String[] args) {
        Map<String, Double> prices = new LinkedHashMap<>();
        prices.put("mouse", 59.9);
        prices.put("teclado", 149.0);
        prices.put("monitor", 899.0);

        System.out.println("=== getOrDefault ===");
        System.out.println("webcam: " + prices.getOrDefault("webcam", 0.0) + " (antes: get + null check)");

        System.out.println();
        System.out.println("=== putIfAbsent ===");
        prices.putIfAbsent("mouse", 1.0);   // não sobrescreve
        prices.putIfAbsent("webcam", 249.0); // insere
        System.out.println(prices);

        System.out.println();
        System.out.println("=== forEach(BiConsumer) ===");
        prices.forEach((product, price) -> System.out.printf("  %-8s R$ %.2f%n", product, price));

        System.out.println();
        System.out.println("=== replaceAll: reajuste de 10% ===");
        prices.replaceAll((product, price) -> Math.round(price * 1.10 * 100) / 100.0);
        System.out.println(prices);

        System.out.println();
        System.out.println("=== remove(k, v) e replace(k, old, new) condicionais ===");
        System.out.println("remove(mouse, 1.0)? " + prices.remove("mouse", 1.0) + " (valor não confere)");
        System.out.println("replace(webcam, 273.9, 199.0)? " + prices.replace("webcam", 273.9, 199.0));
        System.out.println("replace(inexistente, 1.0): " + prices.replace("tablet", 1.0) + " (retorna valor anterior)");
        System.out.println(prices);

        System.out.println();
        System.out.println("=== Map.Entry.comparingByValue (Java 8) ===");
        Map<String, Double> copy = new HashMap<>(prices);
        copy.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> System.out.println("  " + e.getKey() + " = " + e.getValue()));
    }
}
