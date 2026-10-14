package br.com.javalab.java06.navigableset;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Java 6 - NavigableSet
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: extensão de {@code SortedSet} com {@code floor}, {@code ceiling},
 * {@code lower}, {@code higher}, {@code pollFirst/pollLast}, {@code descendingSet()},
 * {@code descendingIterator()} e {@code headSet/tailSet/subSet} com limites
 * inclusivos/exclusivos. Implementações: {@code TreeSet} e
 * {@code ConcurrentSkipListSet}.</p>
 * <p>Benefício: "qual o horário livre mais próximo?", "qual o maior preço abaixo de X?"
 * em O(log n), sem percorrer o conjunto.</p>
 * <p>Antes: {@code SortedSet} só tinha {@code first/last} e visões com limite inferior
 * inclusivo e superior exclusivo fixos; ordem decrescente exigia outro TreeSet com
 * {@code Collections.reverseOrder()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/navigableset/NavigableSetExample.java
 * </pre>
 */
public class NavigableSetExample {

    public static void main(String[] args) {
        // Preços (em centavos) de produtos disponíveis
        NavigableSet<Integer> prices = new TreeSet<Integer>();
        for (int p : new int[] {1990, 4990, 9990, 14990, 29990}) {
            prices.add(p);
        }
        int budget = 12000;

        System.out.println("=== Abordagem antiga: SortedSet ===");
        SortedSet<Integer> sorted = prices;
        SortedSet<Integer> affordable = sorted.headSet(budget + 1); // truque do +1 para incluir o limite
        System.out.println("Mais caro que cabe em " + budget + ": "
                + (affordable.isEmpty() ? "nenhum" : affordable.last()));

        System.out.println();
        System.out.println("=== Abordagem nova: NavigableSet ===");
        System.out.println("floor(" + budget + ")   = " + prices.floor(budget) + " (maior <= orçamento)");
        System.out.println("ceiling(" + budget + ") = " + prices.ceiling(budget) + " (menor >= orçamento)");
        System.out.println("lower(4990) = " + prices.lower(4990) + ", higher(4990) = " + prices.higher(4990));
        System.out.println("floor(100)  = " + prices.floor(100) + " (null, sem exceção)");
        System.out.println("headSet(9990, true)  = " + prices.headSet(9990, true));
        System.out.println("subSet(1990, false, 14990, true) = " + prices.subSet(1990, false, 14990, true));
        System.out.println("descendingSet() = " + prices.descendingSet());

        System.out.print("descendingIterator(): ");
        for (Iterator<Integer> it = prices.descendingIterator(); it.hasNext();) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        System.out.println();
        System.out.println("=== Horários de atendimento: próximo horário livre ===");
        NavigableSet<String> freeSlots = new TreeSet<String>();
        freeSlots.add("09:00");
        freeSlots.add("10:30");
        freeSlots.add("13:00");
        freeSlots.add("15:30");
        String now = "11:05";
        System.out.println("Agora são " + now + "; próximo horário livre: " + freeSlots.ceiling(now));
        System.out.println("Último horário que já passou: " + freeSlots.lower(now));
        System.out.println("pollFirst() = " + freeSlots.pollFirst() + ", pollLast() = " + freeSlots.pollLast()
                + ", restantes = " + freeSlots);
    }
}
