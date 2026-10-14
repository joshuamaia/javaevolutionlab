package br.com.javalab.java22.unnamed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Java 22 - Unnamed Variables &amp; Patterns (JEP 456)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 21 (JEP 443, "Unnamed Patterns and Variables").
 * O Java 9 já havia proibido {@code _} como identificador (preparando este uso).</p>
 *
 * <p>O que é: o caractere {@code _} pode substituir o nome de uma variável que precisa ser
 * declarada mas nunca é usada (catch, laço, lambda, try-with-resources) e de um pattern
 * cujo valor não interessa ({@code case Shipped(_, var date)} ou {@code case Cancelled _}).</p>
 * <p>Benefício: deixa explícita a intenção "não uso isto", elimina avisos de variável
 * não usada e encurta record patterns aninhados.</p>
 * <p>Antes: inventar nomes como {@code ignored}, {@code unused}, {@code e}, ou escrever
 * todos os componentes do record pattern com {@code var x} mesmo sem usar.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java22/unnamed/UnnamedVariablesExample.java
 * </pre>
 */
public class UnnamedVariablesExample {

    sealed interface OrderStatus permits Pending, Shipped, Cancelled {}
    record Pending(String orderId) implements OrderStatus {}
    record Shipped(String orderId, String trackingCode, String date) implements OrderStatus {}
    record Cancelled(String orderId, String reason) implements OrderStatus {}

    public static void main(String[] args) {
        List<OrderStatus> statuses = List.of(
                new Pending("A-1"),
                new Shipped("A-2", "BR123", "2024-03-19"),
                new Cancelled("A-3", "sem estoque"));

        System.out.println("=== Abordagem antiga: nomes inventados ===");
        for (OrderStatus status : statuses) {
            if (status instanceof Shipped shipped) {
                // orderId e trackingCode não interessam, mas precisam existir no pattern (Java 21)
                System.out.println("Enviado em " + shipped.date());
            }
        }

        System.out.println();
        System.out.println("=== Patterns sem nome (_) ===");
        for (OrderStatus status : statuses) {
            String text = switch (status) {
                case Shipped(_, _, var date) -> "Enviado em " + date;
                // vários patterns no mesmo case: permitido porque nenhum declara variável
                case Pending _, Cancelled _ -> "Não enviado (" + status.getClass().getSimpleName() + ")";
            };
            System.out.println(text);
        }

        System.out.println();
        System.out.println("=== Variáveis sem nome (_) ===");
        int count = 0;
        for (OrderStatus _ : statuses) { // laço só para contar
            count++;
        }
        System.out.println("Total de pedidos: " + count);

        String quantityText = "doze";
        try {
            Integer.parseInt(quantityText);
        } catch (NumberFormatException _) { // a exceção em si não importa
            System.out.println("Quantidade inválida: " + quantityText);
        }

        Map<String, Integer> stockByProduct = Map.of("caneta", 10, "caderno", 5);
        List<String> labels = new ArrayList<>();
        stockByProduct.forEach((product, _) -> labels.add(product.toUpperCase())); // valor ignorado
        System.out.println("Produtos: " + labels.stream().sorted().collect(Collectors.joining(", ")));

        var pairs = List.of("a", "b", "c").stream()
                .collect(Collectors.toMap(s -> s, _ -> 0)); // parâmetro de lambda sem nome
        System.out.println("Mapa inicializado: " + pairs.size() + " chaves com zero");
    }
}
