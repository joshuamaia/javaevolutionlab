package br.com.javalab.java08.streams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 8 - Stream API: filter (JEP 107 - Bulk Data Operations for Collections)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code filter(Predicate)} é uma operação intermediária que mantém apenas os
 * elementos que satisfazem a condição. Streams são preguiçosas (lazy): nada executa até
 * uma operação terminal ({@code collect}, {@code count}, {@code forEach}...).</p>
 * <p>Benefício: código declarativo ("o quê", não "como"), encadeável e paralelizável.</p>
 * <p>Antes: laço for com if e uma lista auxiliar.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/streams/StreamFilterExample.java
 * </pre>
 */
public class StreamFilterExample {

    static class Order {
        final int id;
        final String customer;
        final double total;
        final boolean paid;

        Order(int id, String customer, double total, boolean paid) {
            this.id = id;
            this.customer = customer;
            this.total = total;
            this.paid = paid;
        }

        @Override
        public String toString() {
            return "#" + id + " " + customer + " R$" + total + (paid ? " (pago)" : " (pendente)");
        }
    }

    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
                new Order(1, "Alice", 120.0, true),
                new Order(2, "Bob", 35.5, false),
                new Order(3, "Carol", 980.0, true),
                new Order(4, "Alice", 15.0, true),
                new Order(5, "Davi", 450.0, false));

        System.out.println("=== Pedidos pagos acima de R$100 ===");
        List<Order> bigPaid = orders.stream()
                .filter(o -> o.paid)
                .filter(o -> o.total > 100)
                .collect(Collectors.toList());
        bigPaid.forEach(System.out::println);

        System.out.println();
        System.out.println("=== Contagem e busca ===");
        long pending = orders.stream().filter(o -> !o.paid).count();
        System.out.println("Pendentes: " + pending);
        System.out.println("Algum pedido acima de R$900? " + orders.stream().anyMatch(o -> o.total > 900));
        System.out.println("Todos acima de R$10? " + orders.stream().allMatch(o -> o.total > 10));
        System.out.println("Primeiro pedido da Alice: "
                + orders.stream().filter(o -> o.customer.equals("Alice")).findFirst().orElse(null));

        System.out.println();
        System.out.println("=== Laziness: o filter só roda quando há operação terminal ===");
        java.util.stream.Stream<Order> lazy = orders.stream().filter(o -> {
            System.out.println("  avaliando " + o.id);
            return o.total > 400;
        });
        System.out.println("Stream criada, nada avaliado ainda. Agora findFirst():");
        System.out.println("Resultado: " + lazy.findFirst().get() + " (parou no primeiro que casou)");

        System.out.println();
        System.out.println("=== distinct, limit e skip ===");
        List<String> customers = orders.stream()
                .map(o -> o.customer)
                .distinct()
                .skip(1)
                .limit(2)
                .collect(Collectors.toList());
        System.out.println("Clientes distintos (pulando 1, máx. 2): " + customers);
    }
}
