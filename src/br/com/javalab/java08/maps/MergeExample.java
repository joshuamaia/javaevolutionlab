package br.com.javalab.java08.maps;

import java.util.Map;
import java.util.TreeMap;

/**
 * Java 8 - Map.merge
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code merge(chave, valor, remapping)} insere o valor se a chave não existir;
 * se existir, combina valor antigo e novo com a função. Se a função retornar null,
 * a chave é removida.</p>
 * <p>Benefício: contadores e acumuladores em uma linha.</p>
 * <p>Antes: {@code Integer c = map.get(k); map.put(k, c == null ? 1 : c + 1);}</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/maps/MergeExample.java
 * </pre>
 */
public class MergeExample {

    public static void main(String[] args) {
        String[] accessLog = {"/home", "/cart", "/home", "/checkout", "/home", "/cart"};

        System.out.println("=== Abordagem antiga: contador ===");
        Map<String, Integer> oldCounts = new TreeMap<>();
        for (String page : accessLog) {
            Integer current = oldCounts.get(page);
            oldCounts.put(page, current == null ? 1 : current + 1);
        }
        System.out.println(oldCounts);

        System.out.println();
        System.out.println("=== Java 8: merge ===");
        Map<String, Integer> hits = new TreeMap<>();
        for (String page : accessLog) {
            hits.merge(page, 1, Integer::sum);
        }
        System.out.println(hits);

        System.out.println();
        System.out.println("=== Somando valores de pedidos por cliente ===");
        Map<String, Double> revenue = new TreeMap<>();
        revenue.merge("Alice", 120.0, Double::sum);
        revenue.merge("Bob", 80.0, Double::sum);
        revenue.merge("Alice", 30.5, Double::sum);
        System.out.println(revenue);

        System.out.println();
        System.out.println("=== Concatenando e removendo com null ===");
        Map<String, String> notes = new TreeMap<>();
        notes.merge("pedido-1", "pago", (a, b) -> a + "; " + b);
        notes.merge("pedido-1", "enviado", (a, b) -> a + "; " + b);
        notes.merge("pedido-2", "pago", (a, b) -> a + "; " + b);
        System.out.println("Antes:  " + notes);
        notes.merge("pedido-2", "cancelado", (oldValue, newValue) -> null); // função retorna null -> remove
        System.out.println("Depois: " + notes);

        System.out.println();
        System.out.println("=== Mesclando dois mapas de estoque ===");
        Map<String, Integer> warehouseSp = new TreeMap<>();
        warehouseSp.put("mouse", 10);
        warehouseSp.put("monitor", 2);
        Map<String, Integer> warehouseRj = new TreeMap<>();
        warehouseRj.put("mouse", 5);
        warehouseRj.put("cabo", 30);
        Map<String, Integer> total = new TreeMap<>(warehouseSp);
        warehouseRj.forEach((product, qty) -> total.merge(product, qty, Integer::sum));
        System.out.println("Estoque total: " + total);
    }
}
