package br.com.javalab.java11.optional;

import java.util.Map;
import java.util.Optional;

/**
 * Java 11 - Optional.isEmpty
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code isEmpty()} é o oposto de {@code isPresent()}.</p>
 * <p>Benefício: leitura mais natural em verificações de ausência, sem {@code !}.</p>
 * <p>Antes: {@code !optional.isPresent()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/optional/OptionalIsEmptyExample.java
 * </pre>
 */
public class OptionalIsEmptyExample {

    private static final Map<String, String> COUPONS = Map.of("BEMVINDO", "10% de desconto");

    public static void main(String[] args) {
        System.out.println("=== Antes: !isPresent() ===");
        Optional<String> coupon = findCoupon("NATAL");
        if (!coupon.isPresent()) {
            System.out.println("Cupom NATAL inválido");
        }

        System.out.println();
        System.out.println("=== Java 11: isEmpty() ===");
        for (String code : new String[] {"BEMVINDO", "NATAL"}) {
            Optional<String> result = findCoupon(code);
            if (result.isEmpty()) {
                System.out.println("Cupom " + code + " inválido");
                continue;
            }
            System.out.println("Cupom " + code + ": " + result.get());
        }
    }

    private static Optional<String> findCoupon(String code) {
        return Optional.ofNullable(COUPONS.get(code));
    }
}
