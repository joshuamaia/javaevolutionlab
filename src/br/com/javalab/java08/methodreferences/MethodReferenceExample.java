package br.com.javalab.java08.methodreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Java 8 - Method References: os 4 tipos (JSR 335)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: atalho {@code ::} para um lambda que apenas chama um método existente.</p>
 * <ol>
 *   <li>Método estático: {@code Integer::parseInt}</li>
 *   <li>Método de instância de um objeto específico: {@code printer::print}</li>
 *   <li>Método de instância de um objeto arbitrário do tipo: {@code String::toUpperCase}</li>
 *   <li>Construtor: {@code ArrayList::new}, {@code Product::new}</li>
 * </ol>
 * <p>Benefício: ainda mais conciso e legível que o lambda equivalente.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/methodreferences/MethodReferenceExample.java
 * </pre>
 */
public class MethodReferenceExample {

    static class Product {
        private final String name;
        private final double price;

        Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        static int compareByPrice(Product a, Product b) {
            return Double.compare(a.price, b.price);
        }

        @Override
        public String toString() {
            return name + " (R$ " + price + ")";
        }
    }

    static class LabelPrinter {
        private final String prefix;

        LabelPrinter(String prefix) {
            this.prefix = prefix;
        }

        void print(Object item) {
            System.out.println(prefix + item);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 1) Referência a método estático ===");
        Function<String, Integer> parseLambda = s -> Integer.parseInt(s);
        Function<String, Integer> parseRef = Integer::parseInt;
        System.out.println("lambda: " + parseLambda.apply("42") + " | ref: " + parseRef.apply("42"));

        List<Product> products = new ArrayList<>(Arrays.asList(
                new Product("Monitor", 899.0), new Product("Mouse", 59.9), new Product("Teclado", 199.0)));
        products.sort(Product::compareByPrice);
        System.out.println("Ordenados por preço: " + products);

        System.out.println();
        System.out.println("=== 2) Método de instância de objeto específico ===");
        LabelPrinter printer = new LabelPrinter("[ETIQUETA] ");
        products.forEach(printer::print);   // equivale a p -> printer.print(p)
        products.forEach(System.out::println); // System.out é um objeto específico

        System.out.println();
        System.out.println("=== 3) Método de instância de objeto arbitrário ===");
        Function<String, String> upper = String::toUpperCase; // s -> s.toUpperCase()
        BiFunction<String, String, Boolean> startsWith = String::startsWith; // (s, p) -> s.startsWith(p)
        System.out.println("upper(\"pedido\") = " + upper.apply("pedido"));
        System.out.println("startsWith(\"SKU-123\", \"SKU\") = " + startsWith.apply("SKU-123", "SKU"));

        System.out.println();
        System.out.println("=== 4) Referência a construtor ===");
        Supplier<List<String>> listFactory = ArrayList::new;
        BiFunction<String, Double, Product> productFactory = Product::new;
        Function<Integer, int[]> arrayFactory = int[]::new;
        List<String> tags = listFactory.get();
        tags.add("promo");
        System.out.println("Lista criada: " + tags);
        System.out.println("Produto criado: " + productFactory.apply("Webcam", 249.0));
        System.out.println("Array criado com tamanho: " + arrayFactory.apply(5).length);
    }
}
