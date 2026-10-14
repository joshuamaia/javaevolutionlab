package br.com.javalab.java05.foreach;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Java 5 - Enhanced for loop / for-each (JSR 201)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code for (Tipo item : colecao)} percorre arrays e qualquer
 * {@code java.lang.Iterable} (interface criada no Java 5 justamente para isso).
 * O compilador traduz para um laço com índice (arrays) ou com {@code Iterator}.</p>
 * <p>Benefício: menos código, sem variáveis de controle e sem erros de "off by one".</p>
 * <p>Antes: laço com índice ou {@code Iterator} explícito com cast.</p>
 * <p>Limitação: não dá acesso ao índice nem ao {@code Iterator}; para remover durante a
 * iteração ainda é preciso usar {@code Iterator.remove()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/foreach/EnhancedForExample.java
 * </pre>
 */
public class EnhancedForExample {

    /** Classe própria que implementa Iterable: pode ser usada no for-each. */
    static class OrderBook implements Iterable<String> {
        private final List<String> orders = new ArrayList<String>();

        void add(String order) {
            orders.add(order);
        }

        public Iterator<String> iterator() { // sem @Override: método de interface no Java 5
            return orders.iterator();
        }
    }

    public static void main(String[] args) {
        List<String> customers = new ArrayList<String>();
        customers.add("Ana");
        customers.add("Bruno");
        customers.add("Carla");

        System.out.println("=== Abordagem antiga 1: índice ===");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println("Cliente: " + customers.get(i));
        }

        System.out.println();
        System.out.println("=== Abordagem antiga 2: Iterator explícito ===");
        for (Iterator<String> it = customers.iterator(); it.hasNext();) {
            String customer = it.next();
            System.out.println("Cliente: " + customer);
        }

        System.out.println();
        System.out.println("=== Abordagem nova: for-each ===");
        for (String customer : customers) {
            System.out.println("Cliente: " + customer);
        }

        double[] prices = {10.0, 25.5, 7.25};
        double sum = 0;
        for (double p : prices) {      // arrays de primitivos também
            sum += p;
        }
        System.out.println("Soma dos preços: " + sum);

        Map<String, Integer> stock = new LinkedHashMap<String, Integer>();
        stock.put("Caneta", 10);
        stock.put("Caderno", 0);
        stock.put("Mochila", 3);
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        OrderBook book = new OrderBook();
        book.add("PED-1");
        book.add("PED-2");
        for (String order : book) {    // funciona com qualquer Iterable
            System.out.println("Pedido do OrderBook: " + order);
        }

        System.out.println();
        System.out.println("=== Limitação: remover durante o for-each ===");
        try {
            for (String product : stock.keySet()) {
                if (stock.get(product) == 0) {
                    stock.remove(product); // modifica a coleção sob o iterador oculto
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException: use Iterator.remove()");
        }
        stock.put("Caderno", 0); // restaura o item (pode ter sido removido antes da exceção)
        System.out.println("Estoque antes: " + stock);
        for (Iterator<Map.Entry<String, Integer>> it = stock.entrySet().iterator(); it.hasNext();) {
            if (it.next().getValue() == 0) {
                it.remove();
            }
        }
        System.out.println("Estoque após remover zerados com Iterator: " + stock);
    }
}
