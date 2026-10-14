package br.com.javalab.java15.patternmatching;

import java.util.Objects;

/**
 * Java 15 - Pattern Matching for instanceof, segunda Preview (JEP 375)
 *
 * <p>Status: Segunda Preview. Preview no Java 14 (JEP 305); Definitivo no Java 16
 * (JEP 394). A JEP 375 repetiu a Preview SEM mudancas para colher mais feedback; a
 * sintaxe compila no JDK 25 sem flags.</p>
 *
 * <p>Como o 14 ja mostra o uso basico, aqui o foco e o ESCOPO das pattern variables
 * ("flow scoping"): a variavel existe somente onde o compilador consegue provar que
 * o teste foi verdadeiro.</p>
 * <ul>
 *   <li>{@code a instanceof T t && t...}: t visivel a direita do &amp;&amp;;</li>
 *   <li>{@code a instanceof T t || t...}: ERRO, t pode nao existir;</li>
 *   <li>{@code if (!(a instanceof T t)) return;}: t visivel DEPOIS do if;</li>
 *   <li>idioma classico em {@code equals}.</li>
 * </ul>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/patternmatching/InstanceofPatternSecondPreviewExample.java
 * </pre>
 */
public class InstanceofPatternSecondPreviewExample {

    static final class Product {
        private final String sku;
        private final double price;

        Product(String sku, double price) {
            this.sku = sku;
            this.price = price;
        }

        @Override
        public boolean equals(Object o) {
            // Antes: if (!(o instanceof Product)) return false; Product other = (Product) o; ...
            return o instanceof Product other
                    && sku.equals(other.sku)
                    && Double.compare(price, other.price) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sku, price);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Escopo com && ===");
        Object input = "  cupom10  ";
        if (input instanceof String s && !s.isBlank()) {
            System.out.println("Cupom normalizado: " + s.strip().toUpperCase());
        }
        // if (input instanceof String s || s.isBlank()) {}  // ERRO: s nao esta no escopo

        System.out.println("=== Escopo apos negacao com retorno antecipado ===");
        System.out.println(parseQuantity("3"));
        System.out.println(parseQuantity(7));
        System.out.println(parseQuantity(2.5));

        System.out.println("=== Escopo em expressao ternaria ===");
        Object discount = 15;
        int percent = discount instanceof Integer i ? i : 0;
        System.out.println("Desconto: " + percent + "%");

        System.out.println("=== equals com pattern matching ===");
        Product a = new Product("NB-01", 4500.0);
        Product b = new Product("NB-01", 4500.0);
        System.out.println("a.equals(b)? " + a.equals(b));
        System.out.println("a.equals(\"NB-01\")? " + a.equals("NB-01"));
    }

    private static String parseQuantity(Object value) {
        if (value instanceof String text) {
            return "texto -> " + Integer.parseInt(text.trim());
        }
        if (!(value instanceof Integer number)) {
            return "tipo nao suportado: " + value.getClass().getSimpleName();
        }
        // aqui o compilador sabe que value e Integer: 'number' continua em escopo
        return "inteiro -> " + number;
    }
}
