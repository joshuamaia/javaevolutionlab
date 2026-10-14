package br.com.javalab.java08.functionalinterfaces;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Java 8 - Composição de funções: andThen, compose, and/or/negate (JSR 335)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: as interfaces funcionais trazem métodos default para combinar funções:
 * {@code Function.andThen/compose/identity}, {@code Predicate.and/or/negate/isEqual},
 * {@code Consumer.andThen}.</p>
 * <p>Benefício: montar pipelines a partir de peças pequenas e testáveis.</p>
 * <p>Diferença: {@code f.andThen(g)} = g(f(x)); {@code f.compose(g)} = f(g(x)).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/functionalinterfaces/FunctionCompositionExample.java
 * </pre>
 */
public class FunctionCompositionExample {

    public static void main(String[] args) {
        System.out.println("=== Function: andThen vs compose ===");
        Function<Double, Double> addShipping = price -> price + 15.0;
        Function<Double, Double> applyTax = price -> price * 1.10;

        System.out.println("addShipping.andThen(applyTax)(100) = " + addShipping.andThen(applyTax).apply(100.0)
                + "  -> (100 + 15) * 1.10");
        System.out.println("addShipping.compose(applyTax)(100) = " + addShipping.compose(applyTax).apply(100.0)
                + "  -> (100 * 1.10) + 15");
        System.out.println("Function.identity()(\"x\") = " + Function.<String>identity().apply("x"));

        System.out.println();
        System.out.println("=== Pipeline de normalização de texto ===");
        Function<String, String> trim = String::trim;
        Function<String, String> pipeline = trim
                .andThen(String::toLowerCase)
                .andThen(s -> s.replaceAll("\\s+", "-"));
        System.out.println("slug('  Notebook Gamer  PRO ') = " + pipeline.apply("  Notebook Gamer  PRO "));

        System.out.println();
        System.out.println("=== Predicate: and, or, negate ===");
        Predicate<String> notEmpty = s -> !s.isEmpty();
        Predicate<String> validEmail = s -> s.contains("@");
        Predicate<String> corporate = s -> s.endsWith("@empresa.com");
        Predicate<String> acceptable = notEmpty.and(validEmail).and(corporate.negate().or(s -> s.startsWith("admin")));

        List<String> emails = Arrays.asList("alice@gmail.com", "", "bob@empresa.com", "admin@empresa.com", "sem-arroba");
        for (String email : emails) {
            System.out.println("'" + email + "' aceito? " + acceptable.test(email));
        }

        System.out.println();
        System.out.println("=== Consumer.andThen ===");
        Consumer<String> save = order -> System.out.println("Salvando " + order);
        Consumer<String> notify = order -> System.out.println("Notificando cliente sobre " + order);
        save.andThen(notify).accept("pedido #77");
    }
}
