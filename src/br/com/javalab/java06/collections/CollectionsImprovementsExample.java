package br.com.javalab.java06.collections;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Java 6 - Melhorias no Collections Framework
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: utilitários e implementações adicionados no Java 6 (além de
 * NavigableMap/NavigableSet/Deque, que têm exemplos próprios):</p>
 * <ul>
 *   <li>{@code Arrays.copyOf} / {@code Arrays.copyOfRange}: copiar/redimensionar arrays.</li>
 *   <li>{@code Collections.newSetFromMap}: cria um Set a partir de qualquer Map (ex.: um
 *   Set concorrente com ConcurrentHashMap, que não tinha um "ConcurrentHashSet").</li>
 *   <li>{@code Collections.asLifoQueue}: enxerga um Deque como uma Queue LIFO.</li>
 *   <li>{@code ConcurrentSkipListMap/Set}: mapa/conjunto ORDENADO e concorrente.</li>
 *   <li>{@code AbstractMap.SimpleEntry/SimpleImmutableEntry}: implementação pronta de
 *   {@code Map.Entry} (pares chave-valor).</li>
 * </ul>
 * <p>Antes: {@code System.arraycopy} com array criado à mão, classes Entry/Pair
 * escritas em todo projeto, {@code Collections.synchronizedSortedMap}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/collections/CollectionsImprovementsExample.java
 * </pre>
 */
public class CollectionsImprovementsExample {

    public static void main(String[] args) {
        String[] skus = {"SKU-1", "SKU-2", "SKU-3", "SKU-4", "SKU-5"};

        System.out.println("=== Abordagem antiga: System.arraycopy ===");
        String[] firstThreeOld = new String[3];
        System.arraycopy(skus, 0, firstThreeOld, 0, 3);
        System.out.println(Arrays.toString(firstThreeOld));

        System.out.println();
        System.out.println("=== Arrays.copyOf / copyOfRange ===");
        System.out.println("copyOf(skus, 3)       = " + Arrays.toString(Arrays.copyOf(skus, 3)));
        System.out.println("copyOf(skus, 7)       = " + Arrays.toString(Arrays.copyOf(skus, 7)) + " (completa com null)");
        System.out.println("copyOfRange(skus,1,4) = " + Arrays.toString(Arrays.copyOfRange(skus, 1, 4)));
        int[] grown = Arrays.copyOf(new int[] {1, 2}, 4);
        System.out.println("int[] ampliado        = " + Arrays.toString(grown));

        System.out.println();
        System.out.println("=== Collections.newSetFromMap: Set concorrente ===");
        Set<String> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        onlineUsers.add("ana");
        onlineUsers.add("bruno");
        onlineUsers.add("ana"); // duplicado ignorado
        System.out.println("Usuários online: " + onlineUsers.size() + " " + onlineUsers.contains("ana"));

        // Outro uso clássico: cache LRU de "já processados" com tamanho máximo
        final int maxSize = 3;
        Set<String> recentlyProcessed = Collections.newSetFromMap(new LinkedHashMap<String, Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
                return size() > maxSize;
            }
        });
        for (String order : new String[] {"PED-1", "PED-2", "PED-3", "PED-4"}) {
            recentlyProcessed.add(order);
        }
        System.out.println("Últimos 3 processados: " + recentlyProcessed);

        System.out.println();
        System.out.println("=== Collections.asLifoQueue: Queue com comportamento de pilha ===");
        Queue<String> undo = Collections.asLifoQueue(new java.util.ArrayDeque<String>());
        undo.offer("digitar 'a'");
        undo.offer("negrito");
        undo.offer("apagar linha");
        System.out.println("Desfazer na ordem: " + undo.poll() + " -> " + undo.poll() + " -> " + undo.poll());

        System.out.println();
        System.out.println("=== ConcurrentSkipListMap / ConcurrentSkipListSet ===");
        ConcurrentNavigableMap<Long, String> events = new ConcurrentSkipListMap<Long, String>();
        events.put(1700L, "pagamento aprovado");
        events.put(1500L, "pedido criado");
        events.put(1900L, "nota fiscal emitida");
        System.out.println("Eventos ordenados por timestamp: " + events);
        System.out.println("Eventos a partir de 1600: " + events.tailMap(1600L));
        ConcurrentSkipListSet<String> tags = new ConcurrentSkipListSet<String>(Arrays.asList("urgente", "b2b", "frete-gratis"));
        System.out.println("Tags ordenadas: " + tags + ", primeira: " + tags.first());

        System.out.println();
        System.out.println("=== AbstractMap.SimpleEntry / SimpleImmutableEntry ===");
        List<Map.Entry<String, Integer>> ranking = new ArrayList<Map.Entry<String, Integer>>();
        ranking.add(new AbstractMap.SimpleEntry<String, Integer>("Caneta", 120));
        ranking.add(new AbstractMap.SimpleEntry<String, Integer>("Caderno", 80));
        ranking.get(1).setValue(95); // SimpleEntry é mutável
        System.out.println("Ranking: " + ranking);
        Map.Entry<String, Integer> fixed = new AbstractMap.SimpleImmutableEntry<String, Integer>("Mochila", 10);
        try {
            fixed.setValue(11);
        } catch (UnsupportedOperationException e) {
            System.out.println("SimpleImmutableEntry.setValue -> UnsupportedOperationException");
        }
    }
}
