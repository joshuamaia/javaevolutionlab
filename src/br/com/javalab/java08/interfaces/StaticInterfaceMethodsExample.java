package br.com.javalab.java08.interfaces;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Java 8 - Métodos estáticos em interfaces (JSR 335)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: interfaces podem declarar métodos {@code static} com implementação.
 * São chamados pelo nome da interface e NÃO são herdados pelas implementações.</p>
 * <p>Benefício: fábricas e utilitários ficam junto da interface (ex.: {@code Comparator.comparing},
 * {@code Function.identity}, {@code Stream.of}), sem classes companheiras como
 * {@code Collections}/{@code Arrays}.</p>
 * <p>Antes: classe utilitária separada ({@code Validators}, {@code Comparators}...).</p>
 * <p>Obs.: métodos {@code private} em interfaces só chegaram no Java 9.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/interfaces/StaticInterfaceMethodsExample.java
 * </pre>
 */
public class StaticInterfaceMethodsExample {

    @FunctionalInterface
    interface Validator {
        boolean isValid(String input);

        static Validator notBlank() {
            return s -> s != null && !s.trim().isEmpty();
        }

        static Validator maxLength(int max) {
            return s -> s != null && s.length() <= max;
        }

        static Validator allOf(Validator... validators) {
            return s -> Arrays.stream(validators).allMatch(v -> v.isValid(s));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Fábricas estáticas na própria interface ===");
        Validator customerName = Validator.allOf(Validator.notBlank(), Validator.maxLength(10));
        List<String> inputs = Arrays.asList("Alice", "   ", "Maximiliano Augusto");
        for (String input : inputs) {
            System.out.println("'" + input + "' válido? " + customerName.isValid(input));
        }

        System.out.println();
        System.out.println("=== Métodos estáticos de interfaces do JDK 8 ===");
        List<String> products = Arrays.asList("monitor", "mouse", "cabo", "notebook");
        products.sort(Comparator.comparing(String::length));
        System.out.println("Comparator.comparing(length): " + products);
        products.sort(Comparator.naturalOrder());
        System.out.println("Comparator.naturalOrder():   " + products);
        products.sort(Comparator.reverseOrder());
        System.out.println("Comparator.reverseOrder():   " + products);

        // Não são herdados: algo como customerName.notBlank() não compila;
        // a chamada deve ser sempre Validator.notBlank().
    }
}
