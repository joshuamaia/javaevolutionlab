package br.com.javalab.java09.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Java 9 - Map.of e Map.ofEntries: fábricas de mapas imutáveis (JEP 269)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code Map.of(k1, v1, ..., k10, v10)} (até 10 pares) e
 * {@code Map.ofEntries(Map.entry(k, v), ...)} (quantidade ilimitada) criam mapas imutáveis.</p>
 * <p>Benefício: tabelas constantes (preços, códigos, traduções) em uma expressão.</p>
 * <p>Antes: criar um {@code HashMap}, chamar {@code put} várias vezes e embrulhar com
 * {@code Collections.unmodifiableMap} (ou o "double brace initialization", que cria uma
 * subclasse anônima).</p>
 *
 * <p>Regras: chave duplicada lança {@code IllegalArgumentException}; chaves e valores
 * {@code null} são proibidos; ordem de iteração não especificada.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/collections/MapOfExample.java
 * </pre>
 */
public class MapOfExample {

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (Java 8) ===");
        Map<String, Double> oldPrices = new HashMap<>();
        oldPrices.put("TECLADO", 199.90);
        oldPrices.put("MOUSE", 89.90);
        oldPrices = Collections.unmodifiableMap(oldPrices);
        System.out.println("Preços: " + oldPrices);

        System.out.println();
        System.out.println("=== Abordagem nova (Java 9): Map.of ===");
        Map<String, Double> prices = Map.of("TECLADO", 199.90, "MOUSE", 89.90, "MONITOR", 1299.00);
        System.out.println("Preços: " + prices);
        System.out.println("Preço do MOUSE: " + prices.get("MOUSE"));

        System.out.println();
        System.out.println("=== Map.ofEntries (mais de 10 pares, mais legível) ===");
        Map<Integer, String> httpStatus = Map.ofEntries(
                Map.entry(200, "OK"),
                Map.entry(201, "Created"),
                Map.entry(404, "Not Found"),
                Map.entry(500, "Internal Server Error"));
        System.out.println("404 = " + httpStatus.get(404));

        System.out.println();
        System.out.println("=== Regras ===");
        try {
            prices.put("CADEIRA", 899.0);
        } catch (UnsupportedOperationException e) {
            System.out.println("put() -> UnsupportedOperationException");
        }
        try {
            Map.of("MOUSE", 1.0, "MOUSE", 2.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Chave duplicada -> IllegalArgumentException: " + e.getMessage());
        }
        try {
            Map.of("MOUSE", null);
        } catch (NullPointerException e) {
            System.out.println("Valor null -> NullPointerException");
        }
    }
}
