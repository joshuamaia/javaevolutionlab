package br.com.javalab.java09.optional;

import java.util.Map;
import java.util.Optional;

/**
 * Java 9 - Optional.ifPresentOrElse
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code ifPresentOrElse(acaoSePresente, acaoSeVazio)} executa uma ação
 * com o valor, ou outra ação quando o {@code Optional} está vazio.</p>
 * <p>Benefício: trata os dois casos em uma única chamada, sem {@code isPresent()} +
 * {@code get()}.</p>
 * <p>Antes (Java 8): {@code ifPresent} só cobria o caso presente; para o vazio era
 * preciso {@code if (opt.isPresent()) ... else ...}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/optional/OptionalIfPresentOrElseExample.java
 * </pre>
 */
public class OptionalIfPresentOrElseExample {

    private static final Map<Integer, String> ORDERS = Map.of(1, "Pedido #1 - Teclado", 2, "Pedido #2 - Mouse");

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (Java 8) ===");
        Optional<String> order = findOrder(1);
        if (order.isPresent()) {
            System.out.println("Encontrado: " + order.get());
        } else {
            System.out.println("Pedido não encontrado");
        }

        System.out.println();
        System.out.println("=== Java 9: ifPresentOrElse ===");
        for (int id = 1; id <= 3; id++) {
            final int orderId = id;
            findOrder(orderId).ifPresentOrElse(
                    found -> System.out.println("Encontrado: " + found),
                    () -> System.out.println("Pedido " + orderId + " não encontrado"));
        }
    }

    private static Optional<String> findOrder(int id) {
        return Optional.ofNullable(ORDERS.get(id));
    }
}
