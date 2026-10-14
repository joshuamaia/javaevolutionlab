package br.com.javalab.java08.concurrency;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Java 8 - ConcurrentHashMap: operações em massa (JEP 155 - Concurrency Updates)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o {@link ConcurrentHashMap} foi reescrito no Java 8 e ganhou operações
 * paralelas {@code forEach}, {@code search} e {@code reduce} (com variantes Keys,
 * Values, Entries, ToLong...), que recebem um {@code parallelismThreshold}:
 * abaixo dele roda sequencialmente; {@code 1} = máximo paralelismo, {@code Long.MAX_VALUE} = sequencial.
 * Também: {@code mappingCount()} e {@code ConcurrentHashMap.newKeySet()}.</p>
 * <p>Benefício: agregações thread-safe sobre mapas compartilhados sem copiar os dados.</p>
 * <p>Obs.: os resultados refletem o estado em algum momento da execução (não é snapshot).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/concurrency/ConcurrentHashMapBulkExample.java
 * </pre>
 */
public class ConcurrentHashMapBulkExample {

    public static void main(String[] args) {
        ConcurrentHashMap<String, Long> salesByProduct = new ConcurrentHashMap<>();
        for (int i = 1; i <= 1_000; i++) {
            salesByProduct.put("SKU-" + i, (long) (i * 7 % 500));
        }
        long threshold = 1; // paralelismo máximo (usa o ForkJoinPool.commonPool)

        System.out.println("=== reduceValuesToLong: total vendido ===");
        long total = salesByProduct.reduceValuesToLong(threshold, Long::longValue, 0L, Long::sum);
        System.out.println("Total: " + total);

        System.out.println();
        System.out.println("=== search: primeiro produto encontrado com venda >= 499 ===");
        String found = salesByProduct.search(threshold, (sku, qty) -> qty >= 499 ? sku + "=" + qty : null);
        System.out.println("Encontrado: " + found + " (qualquer um que satisfaça; ordem não garantida)");

        System.out.println();
        System.out.println("=== reduce: produto mais vendido ===");
        String best = salesByProduct.reduce(threshold,
                (sku, qty) -> sku + "=" + qty,
                (a, b) -> Long.parseLong(a.split("=")[1]) >= Long.parseLong(b.split("=")[1]) ? a : b);
        System.out.println("Mais vendido (um dos empatados): " + best);

        System.out.println();
        System.out.println("=== forEach paralelo com transformação ===");
        ConcurrentHashMap.KeySetView<String, Boolean> zeroSales = ConcurrentHashMap.newKeySet();
        salesByProduct.forEach(threshold, (sku, qty) -> {
            if (qty == 0) {
                zeroSales.add(sku);
            }
        });
        System.out.println("Produtos sem venda: " + zeroSales.size() + " (newKeySet é um Set concorrente)");

        System.out.println();
        System.out.println("=== mappingCount e merge atômico ===");
        System.out.println("mappingCount(): " + salesByProduct.mappingCount() + " (long, preferível a size())");
        salesByProduct.merge("SKU-1", 100L, Long::sum);
        System.out.println("SKU-1 após merge(+100): " + salesByProduct.get("SKU-1"));
    }
}
