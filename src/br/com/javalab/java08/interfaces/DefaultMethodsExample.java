package br.com.javalab.java08.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java 8 - Default Methods em interfaces (JSR 335)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: métodos com implementação em interfaces, marcados com {@code default}.
 * As implementações herdam o comportamento e podem sobrescrevê-lo.</p>
 * <p>Benefício: evoluir interfaces sem quebrar implementações existentes. Foi assim que
 * {@code Collection.stream()}, {@code Iterable.forEach} e {@code List.sort} foram
 * adicionados no Java 8.</p>
 * <p>Antes: adicionar método a uma interface quebrava todas as implementações; usava-se
 * classes abstratas "Adapter" ou classes utilitárias estáticas.</p>
 * <p>Conflito (diamante): se duas interfaces fornecem o mesmo default, a classe deve
 * sobrescrever e pode escolher com {@code Interface.super.metodo()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/interfaces/DefaultMethodsExample.java
 * </pre>
 */
public class DefaultMethodsExample {

    interface PaymentMethod {
        String name();

        double fee(double amount);

        // novo método adicionado depois: implementações antigas continuam compilando
        default double totalWithFee(double amount) {
            return amount + fee(amount);
        }

        default String describe() {
            return name() + " (taxa sobre R$100: R$ " + String.format("%.2f", fee(100)) + ")";
        }
    }

    interface Auditable {
        default String describe() {
            return "registro auditável";
        }
    }

    static class Pix implements PaymentMethod {
        @Override
        public String name() {
            return "PIX";
        }

        @Override
        public double fee(double amount) {
            return 0;
        }
    }

    static class CreditCard implements PaymentMethod, Auditable {
        @Override
        public String name() {
            return "Cartão de crédito";
        }

        @Override
        public double fee(double amount) {
            return amount * 0.0299;
        }

        // obrigatório: PaymentMethod e Auditable têm describe() default
        @Override
        public String describe() {
            return PaymentMethod.super.describe() + " + " + Auditable.super.describe();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Default methods herdados ===");
        List<PaymentMethod> methods = Arrays.asList(new Pix(), new CreditCard());
        for (PaymentMethod m : methods) {
            System.out.printf("%-18s total de R$200 = R$ %.2f%n", m.name(), m.totalWithFee(200));
        }

        System.out.println();
        System.out.println("=== Resolvendo conflito com Interface.super ===");
        for (PaymentMethod m : methods) {
            System.out.println(m.describe());
        }

        System.out.println();
        System.out.println("=== Default methods do próprio JDK 8 ===");
        List<String> list = new ArrayList<>(Arrays.asList("c", "a", "b"));
        list.sort(null);                    // List.sort (default)
        list.forEach(System.out::print);    // Iterable.forEach (default)
        System.out.println();
        list.removeIf(s -> s.equals("b"));  // Collection.removeIf (default)
        System.out.println("Após removeIf: " + list);
    }
}
