package br.com.javalab.java06.navigablemap;

import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Java 6 - NavigableMap
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: extensão de {@code SortedMap} com métodos de navegação:
 * {@code floorKey/floorEntry} (maior chave &lt;= x), {@code ceiling} (menor &gt;= x),
 * {@code lower} (&lt;), {@code higher} (&gt;), {@code firstEntry/lastEntry},
 * {@code pollFirstEntry/pollLastEntry}, {@code descendingMap()} e
 * {@code headMap/tailMap/subMap} com limites inclusivos/exclusivos configuráveis.
 * Implementações: {@code TreeMap} e {@code ConcurrentSkipListMap}.</p>
 * <p>Benefício: buscas por faixa (tabelas de frete, faixas de imposto, séries temporais)
 * em O(log n) sem código manual.</p>
 * <p>Antes: com {@code SortedMap} era preciso usar {@code headMap(x).lastKey()} (com
 * truques como "x + 1" para incluir o limite) e tratar exceções de mapa vazio.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/navigablemap/NavigableMapExample.java
 * </pre>
 */
public class NavigableMapExample {

    public static void main(String[] args) {
        // Tabela de frete: peso mínimo (kg) -> valor
        NavigableMap<Integer, Double> shipping = new TreeMap<Integer, Double>();
        shipping.put(0, 15.0);
        shipping.put(5, 25.0);
        shipping.put(10, 40.0);
        shipping.put(30, 90.0);

        System.out.println("=== Abordagem antiga: SortedMap (Java 1.2) ===");
        SortedMap<Integer, Double> sorted = shipping;
        int weight = 7;
        // "maior chave <= 7" = última chave do headMap(7 + 1), pois headMap exclui o limite
        SortedMap<Integer, Double> head = sorted.headMap(weight + 1);
        System.out.println("Frete para " + weight + " kg: " + sorted.get(head.lastKey()));

        System.out.println();
        System.out.println("=== Abordagem nova: NavigableMap ===");
        for (int w : new int[] {0, 7, 10, 45}) {
            Map.Entry<Integer, Double> range = shipping.floorEntry(w);
            System.out.println("Frete para " + w + " kg (faixa a partir de " + range.getKey() + " kg): R$ " + range.getValue());
        }
        System.out.println("ceilingKey(6) = " + shipping.ceilingKey(6) + " (próxima faixa)");
        System.out.println("lowerKey(5) = " + shipping.lowerKey(5) + ", higherKey(5) = " + shipping.higherKey(5));
        System.out.println("higherKey(30) = " + shipping.higherKey(30) + " (null em vez de exceção)");
        System.out.println("firstEntry = " + shipping.firstEntry() + ", lastEntry = " + shipping.lastEntry());

        System.out.println();
        System.out.println("=== Visões com limites inclusivos/exclusivos ===");
        System.out.println("headMap(10, true)  = " + shipping.headMap(10, true));
        System.out.println("tailMap(5, false)  = " + shipping.tailMap(5, false));
        System.out.println("subMap(0, false, 30, true) = " + shipping.subMap(0, false, 30, true));
        System.out.println("descendingMap      = " + shipping.descendingMap());
        System.out.println("navigableKeySet().descendingSet() = " + shipping.navigableKeySet().descendingSet());

        System.out.println();
        System.out.println("=== pollFirstEntry: consumir em ordem (agenda de entregas por horário) ===");
        NavigableMap<String, String> agenda = new TreeMap<String, String>();
        agenda.put("14:00", "PED-3");
        agenda.put("09:30", "PED-1");
        agenda.put("11:15", "PED-2");
        Map.Entry<String, String> next;
        while ((next = agenda.pollFirstEntry()) != null) {
            System.out.println(next.getKey() + " -> entregar " + next.getValue());
        }
        System.out.println("Agenda vazia: " + agenda.isEmpty());
    }
}
