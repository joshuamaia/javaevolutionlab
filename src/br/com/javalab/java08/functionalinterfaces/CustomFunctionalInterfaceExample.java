package br.com.javalab.java08.functionalinterfaces;

/**
 * Java 8 - @FunctionalInterface próprio (JSR 335)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: qualquer interface com exatamente um método abstrato pode ser alvo de lambda.
 * A anotação {@link FunctionalInterface} é opcional, mas faz o compilador garantir isso.
 * Métodos default/static e métodos de {@code Object} não contam.</p>
 * <p>Benefício: nomes de domínio expressivos ({@code DiscountPolicy} em vez de
 * {@code Function<Order, Double>}) e exceções checadas na assinatura.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/functionalinterfaces/CustomFunctionalInterfaceExample.java
 * </pre>
 */
public class CustomFunctionalInterfaceExample {

    /** Política de desconto aplicada a um valor de pedido. */
    @FunctionalInterface
    interface DiscountPolicy {
        double apply(double amount);

        // default não conta como método abstrato
        default DiscountPolicy then(DiscountPolicy next) {
            return amount -> next.apply(apply(amount));
        }

        static DiscountPolicy none() {
            return amount -> amount;
        }

        // método de Object também não conta
        @Override
        String toString();

        // double other(double x); // descomentar: erro de compilação por causa de @FunctionalInterface
    }

    /** Interface funcional que pode lançar exceção checada (as de java.util.function não podem). */
    @FunctionalInterface
    interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Políticas de desconto como lambdas ===");
        DiscountPolicy blackFriday = amount -> amount * 0.70;
        DiscountPolicy loyalty = amount -> amount - 10;
        DiscountPolicy cap = amount -> Math.max(amount, 50);

        double order = 200.0;
        System.out.println("Sem desconto: " + DiscountPolicy.none().apply(order));
        System.out.println("Black Friday: " + blackFriday.apply(order));
        System.out.println("Black Friday + fidelidade + mínimo: "
                + blackFriday.then(loyalty).then(cap).apply(order));
        System.out.println("Pedido pequeno com mínimo: " + blackFriday.then(loyalty).then(cap).apply(60.0));

        System.out.println();
        System.out.println("=== Interface funcional com exceção checada ===");
        CheckedSupplier<String> loadConfig = () -> {
            if (System.getProperty("javalab.fail") != null) {
                throw new Exception("Configuração indisponível");
            }
            return "timeout=30s";
        };
        System.out.println("Config carregada: " + loadConfig.get());
    }
}
