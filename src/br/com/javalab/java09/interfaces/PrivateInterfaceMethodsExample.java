package br.com.javalab.java09.interfaces;

/**
 * Java 9 - Métodos privados em interfaces (JEP 213 - Milling Project Coin)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: interfaces podem declarar métodos {@code private} (de instância ou
 * {@code static}), visíveis apenas dentro da própria interface.</p>
 * <p>Benefício: reaproveitar código entre métodos {@code default}/{@code static} sem
 * expor esse código auxiliar como parte da API pública.</p>
 * <p>Antes (Java 8): a lógica comum ficava duplicada ou virava um método {@code default}
 * público que qualquer implementação podia chamar/sobrescrever.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/interfaces/PrivateInterfaceMethodsExample.java
 * </pre>
 */
public class PrivateInterfaceMethodsExample {

    interface PriceFormatter {

        double price();

        default String asRetail() {
            return format("Varejo", price());
        }

        default String asWholesale() {
            return format("Atacado", price() * 0.85);
        }

        static String asPromotion(double price) {
            return "[PROMO] " + money(price * 0.5);
        }

        // Método privado de instância: compartilhado pelos métodos default
        private String format(String channel, double value) {
            return channel + ": " + money(value);
        }

        // Método privado estático: usável por métodos static e default
        private static String money(double value) {
            return String.format("R$ %.2f", value);
        }
    }

    static class Product implements PriceFormatter {
        private final double price;

        Product(double price) {
            this.price = price;
        }

        @Override
        public double price() {
            return price;
        }
    }

    public static void main(String[] args) {
        Product keyboard = new Product(200.0);

        System.out.println("=== Métodos default reutilizando métodos privados ===");
        System.out.println(keyboard.asRetail());
        System.out.println(keyboard.asWholesale());
        System.out.println(PriceFormatter.asPromotion(keyboard.price()));

        System.out.println();
        System.out.println("=== Encapsulamento ===");
        System.out.println("keyboard.format(...) e PriceFormatter.money(...) não compilam fora da interface:");
        System.out.println("são detalhes de implementação, não fazem parte do contrato.");
    }
}
