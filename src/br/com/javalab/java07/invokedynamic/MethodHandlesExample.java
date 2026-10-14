package br.com.javalab.java07.invokedynamic;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Java 7 - invokedynamic e MethodHandles (JSR 292)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o JSR 292 adicionou a instrução de bytecode {@code invokedynamic} e a API
 * {@code java.lang.invoke}. Um {@link MethodHandle} é uma referência tipada e executável
 * a um método/construtor/campo, verificada no momento do lookup.</p>
 * <p>Benefício: base para linguagens dinâmicas na JVM (JRuby, Groovy, Nashorn) e, a partir
 * do Java 8, para lambdas; no Java 9+ também para concatenação de Strings (JEP 280).
 * Chamadas via MethodHandle podem ser otimizadas pelo JIT muito melhor que reflexão.</p>
 * <p>Antes: apenas reflexão ({@link Method#invoke}), com checagem de acesso a cada chamada,
 * boxing de argumentos e exceções embrulhadas em InvocationTargetException.</p>
 * <p>Obs.: o Java (linguagem) 7 não gera {@code invokedynamic}; ele é usado por outras
 * linguagens e, depois, pelo javac 8 para lambdas. Aqui usamos a API diretamente.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/invokedynamic/MethodHandlesExample.java
 * </pre>
 */
public class MethodHandlesExample {

    public static class PriceCalculator {
        private final double taxRate;

        public PriceCalculator(double taxRate) {
            this.taxRate = taxRate;
        }

        public double withTax(double price) {
            return price * (1 + taxRate);
        }

        public static String currency(double value) {
            return String.format("R$ %.2f", value);
        }
    }

    public static void main(String[] args) throws Throwable {
        PriceCalculator calculator = new PriceCalculator(0.10);

        System.out.println("=== Abordagem antiga: reflexão ===");
        Method reflective = PriceCalculator.class.getMethod("withTax", double.class);
        Object result = reflective.invoke(calculator, 100.0); // retorna Object (boxing)
        System.out.println("Method.invoke -> " + result);

        System.out.println();
        System.out.println("=== Java 7: MethodHandles.lookup().findVirtual ===");
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle withTax = lookup.findVirtual(PriceCalculator.class, "withTax",
                MethodType.methodType(double.class, double.class));
        double exact = (double) withTax.invokeExact(calculator, 100.0); // assinatura exata
        System.out.println("invokeExact -> " + exact);
        System.out.println("invoke (com adaptação de tipos) -> " + withTax.invoke(calculator, 250));
        System.out.println("type() = " + withTax.type());

        System.out.println();
        System.out.println("=== findStatic e findConstructor ===");
        MethodHandle currency = lookup.findStatic(PriceCalculator.class, "currency",
                MethodType.methodType(String.class, double.class));
        System.out.println("currency(99.9) -> " + (String) currency.invokeExact(99.9));
        MethodHandle constructor = lookup.findConstructor(PriceCalculator.class,
                MethodType.methodType(void.class, double.class));
        PriceCalculator noTax = (PriceCalculator) constructor.invoke(0.0);
        System.out.println("novo calculador sem imposto -> " + noTax.withTax(100.0));

        System.out.println();
        System.out.println("=== Combinadores: bindTo e filterReturnValue ===");
        MethodHandle bound = withTax.bindTo(calculator);                       // fixa o receptor
        MethodHandle formatted = MethodHandles.filterReturnValue(bound, currency); // compõe
        System.out.println("bound.invoke(50.0)     -> " + bound.invoke(50.0));
        System.out.println("formatted.invoke(50.0) -> " + formatted.invoke(50.0));

        System.out.println();
        System.out.println("=== Erros detectados no lookup, não na chamada ===");
        try {
            lookup.findVirtual(PriceCalculator.class, "withTax",
                    MethodType.methodType(int.class, int.class));
        } catch (NoSuchMethodException e) {
            System.out.println("NoSuchMethodException: assinatura (int)int não existe");
        }
    }
}
