package br.com.javalab.java08.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Java 8 - Map.computeIfAbsent / computeIfPresent / compute
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code computeIfAbsent(chave, função)} calcula e insere o valor apenas se a
 * chave não existir, retornando o valor atual. Ideal para multimapas e caches.</p>
 * <p>Benefício: substitui o padrão "get, se null cria e put" em uma linha; em
 * {@code ConcurrentHashMap} é atômico.</p>
 * <p>Antes: {@code if (!map.containsKey(k)) map.put(k, new ArrayList<>()); map.get(k).add(v);}</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/maps/ComputeIfAbsentExample.java
 * </pre>
 */
public class ComputeIfAbsentExample {

    public static void main(String[] args) {
        String[][] orders = {{"Alice", "mouse"}, {"Bob", "monitor"}, {"Alice", "teclado"}, {"Carol", "cabo"}};

        System.out.println("=== Abordagem antiga: multimapa manual ===");
        Map<String, List<String>> oldMap = new TreeMap<>();
        for (String[] o : orders) {
            List<String> items = oldMap.get(o[0]);
            if (items == null) {
                items = new ArrayList<>();
                oldMap.put(o[0], items);
            }
            items.add(o[1]);
        }
        System.out.println(oldMap);

        System.out.println();
        System.out.println("=== Java 8: computeIfAbsent ===");
        Map<String, List<String>> itemsByCustomer = new TreeMap<>();
        for (String[] o : orders) {
            itemsByCustomer.computeIfAbsent(o[0], k -> new ArrayList<>()).add(o[1]);
        }
        System.out.println(itemsByCustomer);

        System.out.println();
        System.out.println("=== computeIfAbsent como cache (memoização) ===");
        Map<Integer, Long> cache = new HashMap<>();
        for (int n : new int[] {20, 20, 25}) {
            long result = cache.computeIfAbsent(n, k -> {
                System.out.println("  calculando fatorial de " + k + "...");
                return factorial(k);
            });
            System.out.println("fatorial(" + n + ") = " + result);
        }
        // Cuidado: a função não deve modificar o próprio mapa (ConcurrentModificationException
        // no HashMap a partir do Java 9).

        System.out.println();
        System.out.println("=== computeIfPresent e compute ===");
        Map<String, Integer> stock = new TreeMap<>();
        stock.put("mouse", 10);
        stock.put("cabo", 1);
        stock.computeIfPresent("mouse", (k, v) -> v - 3);      // atualiza se existir
        stock.computeIfPresent("cabo", (k, v) -> v - 1 == 0 ? null : v - 1); // null remove a chave
        stock.computeIfPresent("monitor", (k, v) -> v - 1);    // ignorado: não existe
        stock.compute("webcam", (k, v) -> v == null ? 5 : v + 5); // sempre executa
        System.out.println("Estoque: " + stock);

        System.out.println();
        System.out.println("=== Retorno null em computeIfAbsent não insere ===");
        stock.computeIfAbsent("teclado", k -> null);
        System.out.println("Contém 'teclado'? " + stock.containsKey("teclado"));
    }

    private static long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
