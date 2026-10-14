package br.com.javalab.java05.generics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Java 5 - Generics (JSR 14)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: tipos e métodos parametrizados por tipo ({@code List<String>},
 * {@code Map<K, V>}, {@code <T> T max(...)}). O compilador verifica os tipos e
 * insere os casts automaticamente. Em tempo de execução os parâmetros de tipo são
 * apagados (type erasure), o que manteve compatibilidade com o bytecode antigo.</p>
 * <p>Benefício: erros de tipo aparecem na compilação e não como
 * {@code ClassCastException} em produção; o código fica sem casts manuais.</p>
 * <p>Antes: coleções "raw" que guardavam {@code Object}; cada leitura exigia cast e
 * nada impedia que um objeto do tipo errado fosse inserido.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/generics/GenericsExample.java
 * </pre>
 */
public class GenericsExample {

    static class Product {
        private final String name;
        private final double price;

        Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return name + " (R$ " + price + ")";
        }
    }

    static class DigitalProduct extends Product {
        DigitalProduct(String name, double price) {
            super(name, price);
        }
    }

    /** Classe genérica: uma "caixa" que guarda qualquer tipo, sem cast na leitura. */
    static class Box<T> {
        private T content;

        void put(T content) {
            this.content = content;
        }

        T get() {
            return content;
        }
    }

    /** Método genérico com bound: funciona para qualquer tipo comparável. */
    static <T extends Comparable<? super T>> T max(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    /** "? extends": a coleção PRODUZ produtos (só leitura). Aceita List<DigitalProduct>. */
    static double totalPrice(Collection<? extends Product> products) {
        double total = 0;
        for (Product p : products) {
            total += p.getPrice();
        }
        return total;
    }

    /** "? super": a coleção CONSOME DigitalProduct (escrita). Aceita List<Product> e List<Object>. */
    static void addFreeEbook(List<? super DigitalProduct> target) {
        target.add(new DigitalProduct("E-book brinde", 0.0));
    }

    /** Raw types: como se programava no Java 1.4. */
    @SuppressWarnings("unchecked")
    static void rawTypesDemo() {
        List orders = new ArrayList();      // List de Object
        orders.add("PED-001");
        orders.add(Integer.valueOf(42));    // compila! nada impede o tipo errado
        for (Iterator it = orders.iterator(); it.hasNext();) {
            try {
                String code = (String) it.next(); // cast manual obrigatório
                System.out.println("Pedido: " + code);
            } catch (ClassCastException e) {
                System.out.println("ClassCastException em tempo de execução: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: raw types + cast ===");
        rawTypesDemo();

        System.out.println();
        System.out.println("=== Abordagem nova: generics ===");
        List<String> orders = new ArrayList<String>(); // sem diamond: <> só chegou no Java 7
        orders.add("PED-001");
        orders.add("PED-002");
        // orders.add(Integer.valueOf(42)); // NÃO compila: erro detectado cedo
        String first = orders.get(0);       // sem cast
        System.out.println("Primeiro pedido: " + first);

        Map<String, List<Product>> byCategory = new HashMap<String, List<Product>>();
        byCategory.put("livros", new ArrayList<Product>());
        byCategory.get("livros").add(new Product("Livro Java", 89.90));
        System.out.println("Categoria livros: " + byCategory.get("livros"));

        System.out.println();
        System.out.println("=== Classe genérica Box<T> ===");
        Box<Product> box = new Box<Product>();
        box.put(new Product("Teclado", 150.0));
        Product fromBox = box.get();
        System.out.println("Conteúdo da caixa: " + fromBox);

        System.out.println();
        System.out.println("=== Método genérico <T extends Comparable<? super T>> ===");
        System.out.println("Maior código: " + max(Arrays.asList("PED-010", "PED-300", "PED-099")));
        System.out.println("Maior quantidade: " + max(Arrays.asList(3, 17, 8)));

        System.out.println();
        System.out.println("=== Wildcards: ? extends (leitura) e ? super (escrita) ===");
        List<DigitalProduct> digital = new ArrayList<DigitalProduct>();
        digital.add(new DigitalProduct("Curso online", 199.0));
        digital.add(new DigitalProduct("Assinatura", 29.9));
        // totalPrice(List<Product>) não aceitaria List<DigitalProduct> sem o wildcard
        System.out.println("Total dos digitais: " + totalPrice(digital));

        List<Product> cart = new ArrayList<Product>();
        addFreeEbook(cart);   // List<Product> é "super" de DigitalProduct
        addFreeEbook(digital);
        System.out.println("Carrinho após brinde: " + cart);

        System.out.println();
        System.out.println("=== Type erasure ===");
        List<String> a = new ArrayList<String>();
        List<Integer> b = new ArrayList<Integer>();
        System.out.println("Mesma classe em runtime? " + (a.getClass() == b.getClass())
                + " -> " + a.getClass().getName());
    }
}
