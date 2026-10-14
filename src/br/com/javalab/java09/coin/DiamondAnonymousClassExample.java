package br.com.javalab.java09.coin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Java 9 - Operador diamante com classes anônimas (JEP 213 - Milling Project Coin)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code new Comparator<>() { ... }} passa a compilar — o tipo genérico da
 * classe anônima é inferido, desde que seja denotável.</p>
 * <p>Benefício: menos repetição de tipos genéricos longos.</p>
 * <p>Antes (Java 7/8): o diamante era proibido em classes anônimas
 * ("cannot use '&lt;&gt;' with anonymous inner classes"), obrigando a repetir
 * {@code new Comparator<Order>()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/coin/DiamondAnonymousClassExample.java
 * </pre>
 */
public class DiamondAnonymousClassExample {

    static class Order {
        final String id;
        final double total;

        Order(String id, double total) {
            this.id = id;
            this.total = total;
        }

        @Override
        public String toString() {
            return id + "=" + total;
        }
    }

    public static void main(String[] args) {
        List<Order> orders = new ArrayList<>(List.of(
                new Order("P-3", 300.0), new Order("P-1", 50.0), new Order("P-2", 120.0)));

        System.out.println("=== Abordagem antiga (Java 8): tipo repetido ===");
        Comparator<Order> oldByTotal = new Comparator<Order>() {
            @Override
            public int compare(Order a, Order b) {
                return Double.compare(a.total, b.total);
            }
        };
        Collections.sort(orders, oldByTotal);
        System.out.println(orders);

        System.out.println();
        System.out.println("=== Java 9: diamante em classe anônima ===");
        Comparator<Order> byIdDesc = new Comparator<>() {
            @Override
            public int compare(Order a, Order b) {
                return b.id.compareTo(a.id);
            }
        };
        orders.sort(byIdDesc);
        System.out.println(orders);
    }
}
