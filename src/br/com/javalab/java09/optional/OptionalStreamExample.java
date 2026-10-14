package br.com.javalab.java09.optional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Java 9 - Optional.stream
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code optional.stream()} converte o {@code Optional} em um stream de 0 ou
 * 1 elemento.</p>
 * <p>Benefício: com {@code flatMap(Optional::stream)} é possível descartar os vazios de
 * uma lista de Optionals em um passo.</p>
 * <p>Antes: {@code .filter(Optional::isPresent).map(Optional::get)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/optional/OptionalStreamExample.java
 * </pre>
 */
public class OptionalStreamExample {

    private static final Map<String, Double> PRICES = Map.of("TECLADO", 199.90, "MOUSE", 89.90);

    public static void main(String[] args) {
        List<String> skus = List.of("TECLADO", "WEBCAM", "MOUSE");

        System.out.println("=== Abordagem antiga (Java 8) ===");
        List<Double> oldPrices = skus.stream()
                .map(OptionalStreamExample::findPrice)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        System.out.println(oldPrices);

        System.out.println();
        System.out.println("=== Java 9: flatMap(Optional::stream) ===");
        List<Double> prices = skus.stream()
                .map(OptionalStreamExample::findPrice)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        System.out.println(prices + "  (WEBCAM sem preço foi descartada)");
    }

    private static Optional<Double> findPrice(String sku) {
        return Optional.ofNullable(PRICES.get(sku));
    }
}
