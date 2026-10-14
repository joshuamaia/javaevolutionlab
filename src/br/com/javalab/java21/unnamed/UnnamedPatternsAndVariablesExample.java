package br.com.javalab.java21.unnamed;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Java 21 - Unnamed Patterns and Variables, Preview (JEP 443)
 *
 * <p>Status: Preview no Java 21; definitivo no Java 22 (JEP 456) com a MESMA sintaxe. Por isso este
 * código fica em {@code src/} e compila no JDK 25 sem flags (no JDK 21 exigia
 * {@code --enable-preview --source 21}).</p>
 *
 * <p>O que é: o caractere {@code _} indica "não preciso deste nome":</p>
 * <ul>
 *   <li><b>unnamed pattern</b>: {@code Point(int x, _)} ignora uma componente de record;</li>
 *   <li><b>unnamed pattern variable</b>: {@code case Circle _} casa o tipo sem criar variável;</li>
 *   <li><b>unnamed variable</b>: em {@code catch (Exception _)}, lambdas {@code (_, v) -> ...},
 *       for-each, try-with-resources e variáveis locais cujo valor não é usado.</li>
 * </ul>
 * <p>Benefício: deixa explícita a intenção de ignorar algo, elimina avisos de "variável não usada"
 * e permite agrupar vários patterns no mesmo {@code case}.</p>
 * <p>Antes: nomes como {@code ignored}, {@code unused}, {@code e} que nunca eram lidos.
 * (Desde o Java 9, {@code _} sozinho era palavra reservada e não podia ser identificador.)</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/unnamed/UnnamedPatternsAndVariablesExample.java
 * </pre>
 */
public class UnnamedPatternsAndVariablesExample {

    sealed interface Event permits OrderPlaced, OrderCancelled, OrderShipped, Heartbeat { }
    record OrderPlaced(String orderId, String customer, double total) implements Event { }
    record OrderCancelled(String orderId, String reason) implements Event { }
    record OrderShipped(String orderId, String carrier) implements Event { }
    record Heartbeat(long timestamp) implements Event { }

    public static void main(String[] args) {
        List<Event> events = List.of(
                new OrderPlaced("P1", "Ana", 250.0),
                new OrderShipped("P1", "Correios"),
                new Heartbeat(System.currentTimeMillis()),
                new OrderCancelled("P2", "cliente desistiu"));

        System.out.println("=== Unnamed pattern em record pattern e vários patterns no mesmo case ===");
        for (Event event : events) {
            System.out.println(describe(event));
        }

        System.out.println();
        System.out.println("=== Unnamed variable no for-each (só contamos) ===");
        int count = 0;
        for (Event _ : events) {
            count++;
        }
        System.out.println("Total de eventos: " + count);

        System.out.println();
        System.out.println("=== Unnamed variable em catch ===");
        for (String quantity : List.of("3", "dez")) {
            try {
                System.out.println("Quantidade: " + Integer.parseInt(quantity));
            } catch (NumberFormatException _) { // a exceção em si não interessa
                System.out.println("Quantidade inválida: " + quantity);
            }
        }

        System.out.println();
        System.out.println("=== Unnamed parameter em lambda ===");
        Map<String, Integer> stock = Map.of("Teclado", 10, "Mouse", 0);
        stock.forEach((_, quantity) -> System.out.println("Unidades: " + quantity));
        Map<String, String> grouped = events.stream()
                .collect(Collectors.toMap(e -> e.getClass().getSimpleName(), _ -> "visto", (a, _) -> a));
        System.out.println("Tipos de evento: " + grouped.keySet().stream().sorted().toList());
    }

    static String describe(Event event) {
        return switch (event) {
            case OrderPlaced(var id, _, var total) -> "Pedido " + id + " criado: R$ " + total;  // ignora o cliente
            case OrderCancelled(var id, _) -> "Pedido " + id + " cancelado";
            case OrderShipped _, Heartbeat _ -> "Evento operacional (" + event.getClass().getSimpleName() + ")";
        };
    }
}
