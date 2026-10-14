package br.com.javalab.java10.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Java 10 - Map.copyOf
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code Map.copyOf(mapa)} cria uma cópia imutável de um mapa.</p>
 * <p>Benefício: expor configurações/tabelas sem risco de alteração externa e sem a
 * armadilha da <i>view</i>.</p>
 * <p>Antes: {@code Collections.unmodifiableMap(new HashMap<>(mapa))}.</p>
 *
 * <p>Regras: chaves ou valores {@code null} lançam {@code NullPointerException}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/collections/MapCopyOfExample.java
 * </pre>
 */
public class MapCopyOfExample {

    public static void main(String[] args) {
        Map<String, Double> prices = new HashMap<>();
        prices.put("TECLADO", 199.90);
        prices.put("MOUSE", 89.90);

        System.out.println("=== Abordagem antiga: view + cópia manual ===");
        Map<String, Double> oldCopy = Collections.unmodifiableMap(new HashMap<>(prices));
        Map<String, Double> view = Collections.unmodifiableMap(prices);

        System.out.println();
        System.out.println("=== Java 10: Map.copyOf ===");
        Map<String, Double> priceTable = Map.copyOf(prices);
        prices.put("MOUSE", 79.90); // reajuste no mapa de origem
        System.out.println("Origem        : MOUSE = " + prices.get("MOUSE"));
        System.out.println("view          : MOUSE = " + view.get("MOUSE") + "  <- reflete a origem");
        System.out.println("cópia antiga  : MOUSE = " + oldCopy.get("MOUSE"));
        System.out.println("Map.copyOf    : MOUSE = " + priceTable.get("MOUSE") + "  <- snapshot");

        System.out.println();
        System.out.println("=== Regras ===");
        try {
            priceTable.put("MONITOR", 1299.0);
        } catch (UnsupportedOperationException e) {
            System.out.println("put -> UnsupportedOperationException");
        }
        Map<String, Double> withNull = new HashMap<>();
        withNull.put("BRINDE", null);
        try {
            Map.copyOf(withNull);
        } catch (NullPointerException e) {
            System.out.println("valor null -> NullPointerException");
        }
    }
}
