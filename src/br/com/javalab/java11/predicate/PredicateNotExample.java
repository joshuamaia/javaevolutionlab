package br.com.javalab.java11.predicate;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Java 11 - Predicate.not
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: método estático {@code Predicate.not(p)} que nega um predicado — útil
 * principalmente com <b>referências de método</b>.</p>
 * <p>Benefício: {@code filter(not(String::isBlank))} em vez de transformar a referência de
 * método em lambda só para negar.</p>
 * <p>Antes: {@code filter(s -> !s.isBlank())} ou
 * {@code filter(((Predicate<String>) String::isBlank).negate())}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/predicate/PredicateNotExample.java
 * </pre>
 */
public class PredicateNotExample {

    static class Order {
        final String id;
        final boolean paid;

        Order(String id, boolean paid) {
            this.id = id;
            this.paid = paid;
        }

        boolean isPaid() {
            return paid;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    public static void main(String[] args) {
        List<String> comments = List.of("Ótimo produto", "  ", "", "Chegou rápido");
        List<Order> orders = List.of(new Order("P1", true), new Order("P2", false), new Order("P3", false));

        System.out.println("=== Antes: lambda ou cast + negate ===");
        System.out.println(comments.stream().filter(c -> !c.isBlank()).collect(Collectors.toList()));
        System.out.println(comments.stream()
                .filter(((Predicate<String>) String::isBlank).negate())
                .collect(Collectors.toList()));

        System.out.println();
        System.out.println("=== Java 11: Predicate.not ===");
        System.out.println("Comentários preenchidos: "
                + comments.stream().filter(Predicate.not(String::isBlank)).collect(Collectors.toList()));
        System.out.println("Pedidos pendentes: "
                + orders.stream().filter(Predicate.not(Order::isPaid)).collect(Collectors.toList()));
    }
}
