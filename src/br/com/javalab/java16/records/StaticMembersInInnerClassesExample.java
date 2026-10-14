package br.com.javalab.java16.records;

import java.util.ArrayList;
import java.util.List;

/**
 * Java 16 - Membros estaticos em classes internas (parte da JEP 395)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que e: para permitir records (implicitamente static) aninhados em classes
 * internas, a JEP 395 relaxou uma regra antiga: classes INTERNAS (nao static) agora
 * podem declarar membros estaticos - campos, metodos, records, enums e interfaces.</p>
 * <p>Beneficio: agrupar constantes/fabricas e tipos auxiliares junto da classe interna
 * que os usa.</p>
 * <p>Antes (ate o Java 15): "Illegal static declaration in inner class"; somente
 * constantes {@code static final} de tipo primitivo/String eram aceitas.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/records/StaticMembersInInnerClassesExample.java
 * </pre>
 */
public class StaticMembersInInnerClassesExample {

    private final String storeName;

    StaticMembersInInnerClassesExample(String storeName) {
        this.storeName = storeName;
    }

    /** Classe interna (nao static): tem acesso a storeName da instancia externa. */
    class Cart {
        static int cartsCreated = 0;                       // campo static nao-constante: proibido ate o 15

        record Item(String product, int quantity, double unitPrice) {   // record em classe interna
            double subtotal() { return quantity * unitPrice; }
        }

        static Item item(String product, int quantity, double price) {  // metodo static em classe interna
            return new Item(product, quantity, price);
        }

        private final List<Item> items = new ArrayList<>();

        Cart() {
            cartsCreated++;
        }

        Cart add(Item item) {
            items.add(item);
            return this;
        }

        String summary() {
            double total = items.stream().mapToDouble(Item::subtotal).sum();
            return storeName + ": " + items.size() + " itens, total R$ " + total;
        }
    }

    public static void main(String[] args) {
        StaticMembersInInnerClassesExample store = new StaticMembersInInnerClassesExample("Loja Centro");

        Cart cart1 = store.new Cart()
                .add(Cart.item("Notebook", 1, 4500.0))
                .add(Cart.item("Mouse", 2, 89.9));
        Cart cart2 = store.new Cart().add(Cart.item("Teclado", 1, 250.0));

        System.out.println(cart1.summary());
        System.out.println(cart2.summary());
        System.out.println("Carrinhos criados (campo static da classe interna): " + Cart.cartsCreated);
    }
}
