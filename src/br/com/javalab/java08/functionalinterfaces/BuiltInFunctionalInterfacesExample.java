package br.com.javalab.java08.functionalinterfaces;

import java.time.LocalDate;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * Java 8 - Interfaces funcionais de java.util.function (JSR 335)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: pacote com ~43 interfaces funcionais genéricas reutilizáveis.
 * As principais: {@code Function<T,R>}, {@code Predicate<T>}, {@code Supplier<T>},
 * {@code Consumer<T>}, {@code BiFunction<T,U,R>}, {@code UnaryOperator<T>},
 * {@code BinaryOperator<T>} e especializações primitivas ({@code IntPredicate},
 * {@code ToIntFunction}...) que evitam boxing.</p>
 * <p>Benefício: não é preciso criar uma interface para cada tipo de callback.</p>
 * <p>Antes: interfaces próprias ou de bibliotecas (Guava {@code Function}/{@code Predicate}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/functionalinterfaces/BuiltInFunctionalInterfacesExample.java
 * </pre>
 */
public class BuiltInFunctionalInterfacesExample {

    public static void main(String[] args) {
        System.out.println("=== Function<T, R>: transforma T em R ===");
        Function<String, Integer> nameLength = s -> s.length();
        System.out.println("nameLength(\"Notebook\") = " + nameLength.apply("Notebook"));

        System.out.println();
        System.out.println("=== Predicate<T>: testa uma condição ===");
        Predicate<Double> expensive = price -> price > 1000;
        System.out.println("1500 é caro? " + expensive.test(1500.0));
        System.out.println("negate(): 1500 é barato? " + expensive.negate().test(1500.0));

        System.out.println();
        System.out.println("=== Supplier<T>: fornece um valor (lazy) ===");
        Supplier<LocalDate> today = LocalDate::now;
        Supplier<String> orderId = () -> "ORD-" + System.nanoTime() % 10000;
        System.out.println("Hoje: " + today.get() + " | novo id: " + orderId.get());

        System.out.println();
        System.out.println("=== Consumer<T>: consome sem retornar ===");
        Consumer<String> audit = msg -> System.out.println("[AUDIT] " + msg);
        audit.accept("pedido 42 aprovado");

        System.out.println();
        System.out.println("=== BiFunction<T, U, R>: dois argumentos ===");
        BiFunction<Double, Integer, Double> total = (price, qty) -> price * qty;
        System.out.println("total(19.90, 3) = " + total.apply(19.90, 3));

        System.out.println();
        System.out.println("=== UnaryOperator<T> e BinaryOperator<T>: mesmo tipo de entrada e saída ===");
        UnaryOperator<String> normalize = s -> s.trim().toUpperCase();
        BinaryOperator<Integer> max = BinaryOperator.maxBy((a, b) -> a - b);
        System.out.println("normalize(\"  sku-1 \") = '" + normalize.apply("  sku-1 ") + "'");
        System.out.println("max(7, 3) = " + max.apply(7, 3));

        System.out.println();
        System.out.println("=== Especializações primitivas (sem boxing) ===");
        IntPredicate even = n -> n % 2 == 0;
        ToIntFunction<String> wordCount = s -> s.split("\\s+").length;
        System.out.println("even(10) = " + even.test(10));
        System.out.println("wordCount(\"entrega em dois dias\") = " + wordCount.applyAsInt("entrega em dois dias"));
    }
}
