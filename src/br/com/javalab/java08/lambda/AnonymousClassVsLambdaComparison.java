package br.com.javalab.java08.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Java 8 - Comparação: classe anônima vs lambda (JEP 126 / JSR 335)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o mesmo comportamento escrito com classe anônima (Java 1.1+) e com lambda.</p>
 * <p>Benefício do lambda: menos código, sem nova classe .class gerada por expressão
 * (lambdas são ligados via {@code invokedynamic}/{@code LambdaMetafactory}).</p>
 * <p>Diferenças importantes: em classe anônima, {@code this} é a própria instância anônima;
 * no lambda, {@code this} é a instância da classe envolvente. Classes anônimas podem ter
 * estado (campos) e implementar interfaces com vários métodos; lambdas não.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/lambda/AnonymousClassVsLambdaComparison.java
 * </pre>
 */
public class AnonymousClassVsLambdaComparison {

    private final String name = "classe envolvente";

    public static void main(String[] args) {
        new AnonymousClassVsLambdaComparison().run();
    }

    private void run() {
        List<String> customers = new ArrayList<>(Arrays.asList("Carla", "ana", "Bruno", "Davi"));

        System.out.println("=== Classe anônima (antes do Java 8) ===");
        Collections.sort(customers, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareToIgnoreCase(b);
            }
        });
        System.out.println(customers);

        System.out.println();
        System.out.println("=== Lambda (Java 8) ===");
        Collections.sort(customers, (a, b) -> b.compareToIgnoreCase(a));
        System.out.println(customers + " (ordem inversa)");

        System.out.println();
        System.out.println("=== Significado de 'this' ===");
        Runnable anonymous = new Runnable() {
            @Override
            public void run() {
                System.out.println("Anônima: this = " + this.getClass().getName());
            }

            @Override
            public String toString() {
                return "instância anônima";
            }
        };
        Runnable lambda = () -> System.out.println("Lambda:  this.name = " + this.name);
        anonymous.run();
        lambda.run();

        System.out.println();
        System.out.println("=== Classe anônima ainda é útil quando há estado ===");
        Runnable counter = new Runnable() {
            private int calls; // lambdas não podem declarar campos

            @Override
            public void run() {
                calls++;
                System.out.println("Chamada número " + calls);
            }
        };
        counter.run();
        counter.run();

        System.out.println();
        System.out.println("=== Classes geradas ===");
        System.out.println("Anônima: " + anonymous.getClass().getName());
        System.out.println("Lambda:  " + lambda.getClass().getName() + " (classe oculta gerada em runtime)");
    }
}
