package br.com.javalab.java11.lambda;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Java 11 - {@code var} em parâmetros de lambda (JEP 323)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: os parâmetros de uma lambda implícita podem ser declarados com {@code var}:
 * {@code (var a, var b) -> ...}. O tipo continua sendo inferido.</p>
 * <p>Benefício: uniformidade com variáveis locais e, principalmente, poder <b>anotar</b>
 * parâmetros de lambda (ex.: {@code @NonNull}, {@code final}) sem escrever o tipo completo.</p>
 * <p>Antes: para anotar era preciso declarar o tipo explícito:
 * {@code (@NonNull String a) -> ...} — às vezes um tipo genérico enorme.</p>
 *
 * <p>Regras: ou todos os parâmetros usam {@code var}, ou nenhum. Não pode misturar com
 * tipos explícitos nem com parâmetros implícitos: {@code (var a, b)} e
 * {@code (var a, String b)} não compilam. Lambda de um parâmetro com {@code var} exige
 * parênteses: {@code (var x) -> ...}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/lambda/VarInLambdaParametersExample.java
 * </pre>
 */
public class VarInLambdaParametersExample {

    /** Anotação de exemplo (como @NonNull de Checker Framework/JSpecify). */
    @Retention(RetentionPolicy.CLASS)
    @Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
    @interface NotBlank {
    }

    public static void main(String[] args) {
        Map<String, List<Double>> ordersByCustomer = Map.of(
                "Ana", List.of(150.0, 89.9),
                "Bruno", List.of(299.0));

        System.out.println("=== Lambda implícita (Java 8) ===");
        ordersByCustomer.forEach((customer, totals) -> System.out.println(customer + " -> " + totals));

        System.out.println();
        System.out.println("=== Antes: anotar exigia o tipo completo ===");
        ordersByCustomer.forEach((@NotBlank String customer, List<Double> totals) ->
                System.out.println(customer + " gastou " + sum(totals)));

        System.out.println();
        System.out.println("=== Java 11: var + anotação ===");
        ordersByCustomer.forEach((@NotBlank var customer, final var totals) ->
                System.out.println(customer + " fez " + totals.size() + " pedido(s)"));

        BiFunction<Double, Double, Double> applyDiscount = (var price, var percent) -> price * (1 - percent / 100);
        System.out.println("R$ 200 com 15% de desconto = " + applyDiscount.apply(200.0, 15.0));

        System.out.println();
        System.out.println("=== Em streams ===");
        List<String> vipCustomers = ordersByCustomer.entrySet().stream()
                .filter((var entry) -> sum(entry.getValue()) > 200)
                .map((@NotBlank var entry) -> entry.getKey())
                .collect(Collectors.toList());
        System.out.println("Clientes acima de R$ 200: " + vipCustomers);

        // (var a, b) -> ...        não compila: não pode misturar var com implícito
        // (var a, String b) -> ... não compila: não pode misturar var com explícito
        // var x -> ...             não compila: var exige parênteses
    }

    private static double sum(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).sum();
    }
}
