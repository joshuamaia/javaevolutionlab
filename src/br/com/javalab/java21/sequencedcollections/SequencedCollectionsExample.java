package br.com.javalab.java21.sequencedcollections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedCollection;
import java.util.SequencedMap;

/**
 * Java 21 - Sequenced Collections (JEP 431)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: três novas interfaces para coleções com ordem de encontro definida:
 * {@link SequencedCollection} ({@code getFirst}, {@code getLast}, {@code addFirst}, {@code addLast},
 * {@code removeFirst}, {@code removeLast}, {@code reversed}), {@code SequencedSet} e
 * {@link SequencedMap} ({@code firstEntry}, {@code lastEntry}, {@code pollFirstEntry},
 * {@code putFirst}, {@code sequencedKeySet}, {@code sequencedValues}, {@code reversed}...).
 * {@code List}, {@code Deque}, {@code LinkedHashSet}, {@code SortedSet}, {@code LinkedHashMap} e
 * {@code SortedMap} foram encaixadas nessa hierarquia.</p>
 * <p>Benefício: uma API uniforme para "primeiro", "último" e "ordem reversa", e um tipo comum
 * para parâmetros que exigem ordem.</p>
 * <p>Antes: {@code list.get(0)}, {@code list.get(list.size() - 1)}, {@code deque.peekLast()},
 * {@code set.iterator().next()}, e para o último elemento de um {@code LinkedHashSet} era preciso
 * percorrer tudo.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/sequencedcollections/SequencedCollectionsExample.java
 * </pre>
 */
public class SequencedCollectionsExample {

    public static void main(String[] args) {
        List<String> queue = new ArrayList<>(List.of("PED-1", "PED-2", "PED-3"));

        System.out.println("=== Abordagem antiga ===");
        System.out.println("primeiro: " + queue.get(0) + " | último: " + queue.get(queue.size() - 1));

        System.out.println();
        System.out.println("=== List como SequencedCollection ===");
        System.out.println("getFirst: " + queue.getFirst() + " | getLast: " + queue.getLast());
        queue.addFirst("PED-URGENTE");
        queue.addLast("PED-4");
        System.out.println("após addFirst/addLast: " + queue);
        System.out.println("reversed (visão, sem cópia): " + queue.reversed());
        queue.reversed().removeFirst(); // remove o último da lista original
        System.out.println("após reversed().removeFirst(): " + queue);

        System.out.println();
        System.out.println("=== LinkedHashSet: agora com último elemento e ordem reversa ===");
        LinkedHashSet<String> visitedPages = new LinkedHashSet<>(List.of("/home", "/produtos", "/carrinho"));
        visitedPages.addFirst("/login");   // se já existisse, seria movido para o início
        visitedPages.addLast("/home");     // move "/home" para o final
        System.out.println("páginas: " + visitedPages);
        System.out.println("última página: " + visitedPages.getLast());
        System.out.println("histórico reverso: " + visitedPages.reversed());

        System.out.println();
        System.out.println("=== LinkedHashMap como SequencedMap ===");
        SequencedMap<String, Integer> stock = new LinkedHashMap<>(); // tipo comum para mapas ordenados
        stock.put("Teclado", 10);
        stock.put("Mouse", 25);
        stock.put("Monitor", 4);
        stock.putFirst("Notebook", 2);
        System.out.println("mapa: " + stock);
        System.out.println("firstEntry: " + stock.firstEntry() + " | lastEntry: " + stock.lastEntry());
        System.out.println("sequencedKeySet().reversed(): " + stock.sequencedKeySet().reversed());
        System.out.println("pollFirstEntry: " + stock.pollFirstEntry() + " -> restante " + stock);

        System.out.println();
        System.out.println("=== Um método para qualquer coleção ordenada ===");
        printEnds(queue);
        printEnds(visitedPages);
        printEnds(new java.util.ArrayDeque<>(List.of(1, 2, 3)));
        printEnds(new java.util.TreeSet<>(List.of("c", "a", "b")));
    }

    static void printEnds(SequencedCollection<?> collection) {
        String kind = collection.getClass().getSimpleName();
        System.out.println(kind + ": primeiro=" + collection.getFirst() + ", último=" + collection.getLast());
    }
}

