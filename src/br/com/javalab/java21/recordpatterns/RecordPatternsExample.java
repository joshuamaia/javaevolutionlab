package br.com.javalab.java21.recordpatterns;

import java.util.List;

/**
 * Java 21 - Record Patterns (JEP 440)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 19 (JEP 405) e 20 (JEP 432).
 * Na versão final saíram os named record patterns (19) e os record patterns no enhanced for (20).</p>
 *
 * <p>O que é: desestruturar records diretamente em {@code instanceof} e {@code switch}, extraindo
 * as componentes para variáveis - inclusive de records aninhados, em qualquer profundidade, com
 * {@code var} e com inferência de argumentos de tipo.</p>
 * <p>Benefício: consultas a dados ("data-oriented programming") curtas e seguras, sem cadeias de
 * getters e casts; o compilador verifica tipos e exaustividade.</p>
 * <p>Antes: {@code if (o instanceof Order) { Order order = (Order) o; Customer c = order.customer(); ... }}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/recordpatterns/RecordPatternsExample.java
 * </pre>
 */
public class RecordPatternsExample {

    record Address(String city, String state) { }
    record Customer(String name, Address address) { }
    record Item(String product, int quantity, double unitPrice) { }
    record Order(String id, Customer customer, List<Item> items) { }

    public static void main(String[] args) {
        Object order = new Order("PED-21", new Customer("Ana", new Address("Campinas", "SP")),
                List.of(new Item("Monitor", 1, 1299.0), new Item("Cabo HDMI", 2, 39.9)));

        System.out.println("=== Abordagem antiga: cast + getters encadeados ===");
        if (order instanceof Order) {
            Order o = (Order) order;
            String state = o.customer().address().state();
            System.out.println("Pedido " + o.id() + " para o estado " + state);
        }

        System.out.println();
        System.out.println("=== Record pattern aninhado em instanceof ===");
        if (order instanceof Order(String id, Customer(var name, Address(var city, var state)), var items)) {
            System.out.println("Pedido " + id + " de " + name + " (" + city + "/" + state + "), " + items.size() + " itens");
        }

        System.out.println();
        System.out.println("=== Record patterns em switch ===");
        List<Object> shipments = List.of(
                order,
                new Order("PED-22", new Customer("Bruno", new Address("Recife", "PE")), List.of()),
                new Item("Brinde", 1, 0.0),
                "texto qualquer");
        for (Object shipment : shipments) {
            System.out.println(freight(shipment));
        }
    }

    static String freight(Object shipment) {
        return switch (shipment) {
            case Order(var id, var customer, var items) when items.isEmpty() -> id + ": pedido vazio, nada a enviar";
            case Order(var id, Customer(var name, Address(var city, String state)), var items) when state.equals("SP") ->
                    id + ": frete local para " + name + " em " + city;
            case Order(var id, Customer(var name, Address(var city, var state)), var items) ->
                    id + ": frete interestadual para " + city + "/" + state;
            case Item(var product, var quantity, var price) when price == 0 -> "Brinde '" + product + "' vai junto";
            case Item item -> "Item avulso: " + item;
            default -> "Não é um envio: " + shipment;
        };
    }
}
