package br.com.javalab.java09.objects;

import java.util.Objects;

/**
 * Java 9 - Objects.requireNonNullElse / requireNonNullElseGet (+ checkIndex)
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code requireNonNullElse(valor, padrao)} devolve o valor ou o padrão se for
 * {@code null}; {@code requireNonNullElseGet} calcula o padrão só quando necessário.
 * {@code Objects.checkIndex} valida índices.</p>
 * <p>Benefício: valores padrão sem ternários espalhados e sem criar {@code Optional} só
 * para isso.</p>
 * <p>Antes: {@code valor != null ? valor : padrao} ou
 * {@code Optional.ofNullable(valor).orElse(padrao)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/objects/ObjectsRequireNonNullElseExample.java
 * </pre>
 */
public class ObjectsRequireNonNullElseExample {

    public static void main(String[] args) {
        String customerCity = null;

        System.out.println("=== Abordagem antiga ===");
        String oldCity = customerCity != null ? customerCity : "Não informada";
        System.out.println("Cidade: " + oldCity);

        System.out.println();
        System.out.println("=== Java 9: requireNonNullElse ===");
        System.out.println("Cidade: " + Objects.requireNonNullElse(customerCity, "Não informada"));
        System.out.println("Cidade: " + Objects.requireNonNullElse("Recife", "Não informada"));

        System.out.println();
        System.out.println("=== Java 9: requireNonNullElseGet (padrão calculado sob demanda) ===");
        String shipping = Objects.requireNonNullElseGet(null, ObjectsRequireNonNullElseExample::defaultShipping);
        System.out.println("Frete: " + shipping);

        System.out.println();
        System.out.println("=== Os dois null -> NullPointerException ===");
        try {
            Objects.requireNonNullElse(null, null);
        } catch (NullPointerException e) {
            System.out.println("NPE: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Bônus Java 9: Objects.checkIndex ===");
        String[] items = {"Teclado", "Mouse"};
        try {
            Objects.checkIndex(5, items.length);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("checkIndex(5, 2) -> " + e.getMessage());
        }
    }

    private static String defaultShipping() {
        System.out.println("  (calculando frete padrão...)");
        return "PAC - 7 dias úteis";
    }
}
