package br.com.javalab.java07.diamond;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java 7 - Diamond Operator (Project Coin, JSR 334)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o operador {@code <>} permite omitir os argumentos de tipo no lado direito
 * de uma instanciação genérica; o compilador os infere a partir da declaração.</p>
 * <p>Benefício: menos repetição e declarações genéricas muito mais legíveis.</p>
 * <p>Antes: era obrigatório repetir os tipos: {@code new HashMap<String, List<Integer>>()}.</p>
 * <p>Observação: no Java 7 o diamond não funcionava com classes anônimas
 * (isso só chegou no Java 9, JEP 213).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/diamond/DiamondOperatorExample.java
 * </pre>
 */
public class DiamondOperatorExample {

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (Java 5/6) ===");
        Map<String, List<Integer>> ordersByCustomerOld = new HashMap<String, List<Integer>>();
        List<Integer> aliceOrders = new ArrayList<Integer>();
        aliceOrders.add(1001);
        aliceOrders.add(1002);
        ordersByCustomerOld.put("Alice", aliceOrders);
        System.out.println("Pedidos (tipos repetidos): " + ordersByCustomerOld);

        System.out.println();
        System.out.println("=== Java 7: diamond operator ===");
        Map<String, List<Integer>> ordersByCustomer = new HashMap<>();
        List<Integer> bobOrders = new ArrayList<>();
        bobOrders.add(2001);
        bobOrders.add(2002);
        bobOrders.add(2003);
        ordersByCustomer.put("Bob", bobOrders);
        System.out.println("Pedidos (tipos inferidos): " + ordersByCustomer);

        System.out.println();
        System.out.println("=== Diamond com construtor que recebe argumentos ===");
        List<String> copy = new ArrayList<>(ordersByCustomer.keySet());
        System.out.println("Cópia das chaves: " + copy);

        System.out.println();
        System.out.println("=== Classe genérica própria ===");
        Pair<String, Integer> stock = new Pair<>("Notebook", 15);
        System.out.println(stock.getFirst() + " -> " + stock.getSecond() + " unidades");
    }

    /** Classe genérica simples usada para mostrar a inferência com tipos próprios. */
    private static class Pair<A, B> {
        private final A first;
        private final B second;

        Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        A getFirst() {
            return first;
        }

        B getSecond() {
            return second;
        }
    }
}
