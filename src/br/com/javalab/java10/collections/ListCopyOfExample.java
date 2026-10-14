package br.com.javalab.java10.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java 10 - List.copyOf
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code List.copyOf(colecao)} cria uma cópia <b>imutável</b> (snapshot) de
 * qualquer coleção. Se a origem já for uma lista imutável (ex.: {@code List.of}), a mesma
 * instância pode ser devolvida, sem copiar.</p>
 * <p>Benefício: cópia defensiva em uma linha — ideal para getters e construtores.</p>
 * <p>Antes: {@code Collections.unmodifiableList(new ArrayList<>(origem))}; e o
 * {@code unmodifiableList} sozinho é só uma <i>view</i> que reflete mudanças na origem.</p>
 *
 * <p>Regras: {@code null} na origem lança {@code NullPointerException}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/collections/ListCopyOfExample.java
 * </pre>
 */
public class ListCopyOfExample {

    static final class ShoppingCart {
        private final List<String> items;

        ShoppingCart(List<String> items) {
            this.items = List.copyOf(items); // cópia defensiva imutável
        }

        List<String> items() {
            return items; // pode devolver direto: ninguém consegue alterar
        }
    }

    public static void main(String[] args) {
        List<String> source = new ArrayList<>(List.of("Teclado", "Mouse"));

        System.out.println("=== View (Java 8) x cópia (Java 10) ===");
        List<String> view = Collections.unmodifiableList(source);
        List<String> copy = List.copyOf(source);
        source.add("Monitor");
        System.out.println("Origem alterada      : " + source);
        System.out.println("unmodifiableList     : " + view + "  <- mudou junto");
        System.out.println("List.copyOf          : " + copy + "  <- snapshot");

        System.out.println();
        System.out.println("=== Cópia defensiva em construtor ===");
        ShoppingCart cart = new ShoppingCart(source);
        source.clear();
        System.out.println("Origem limpa, carrinho mantém: " + cart.items());
        try {
            cart.items().add("Cabo");
        } catch (UnsupportedOperationException e) {
            System.out.println("cart.items().add -> UnsupportedOperationException");
        }

        System.out.println();
        System.out.println("=== Origem já imutável: sem cópia ===");
        List<String> immutable = List.of("A", "B");
        System.out.println("List.copyOf(List.of(...)) é a mesma instância? " + (List.copyOf(immutable) == immutable));

        System.out.println();
        System.out.println("=== null na origem ===");
        List<String> withNull = new ArrayList<>();
        withNull.add(null);
        try {
            List.copyOf(withNull);
        } catch (NullPointerException e) {
            System.out.println("List.copyOf([null]) -> NullPointerException");
        }
    }
}
