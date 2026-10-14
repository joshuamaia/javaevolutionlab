package br.com.javalab.java19.patternswitch;

import java.util.Arrays;

/**
 * Java 19 - Pattern Matching for switch, terceira Preview (JEP 427)
 *
 * <p>Status: Terceira Preview no Java 19. Histórico: 1a Preview no 17 (JEP 406), 2a no 18 (JEP 420);
 * depois 4a no 20 (JEP 433) e definitivo no 21 (JEP 441). Este código usa sintaxe que permaneceu
 * igual e compila no JDK 25 sem flags.</p>
 *
 * <p>O que mudou no Java 19 (foco deste exemplo):</p>
 * <ul>
 *   <li><b>Guards com {@code when}</b> substituem os guarded patterns com {@code &&}
 *       ({@code case Order o && o.total() > 1000} virou {@code case Order o when o.total() > 1000});</li>
 *   <li><b>{@code null}</b>: no 17/18 um pattern incondicional ({@code case Object o}) também casava
 *       com null. A partir do 19 só {@code case null} casa com null; sem ele o switch lança
 *       {@link NullPointerException}, como o switch tradicional. {@code default} sozinho também
 *       NÃO captura null; para isso usa-se {@code case null, default}.</li>
 * </ul>
 * <p>Antes (Java 17/18): {@code &&} no rótulo, ambíguo com o operador lógico dentro do guard.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java19/patternswitch/PatternSwitchWhenGuardsExample.java
 * </pre>
 */
public class PatternSwitchWhenGuardsExample {

    record Order(String id, double total, boolean express) { }

    public static void main(String[] args) {
        System.out.println("=== Guards com 'when' ===");
        Object[] inputs = {
                new Order("P1", 1500, true), new Order("P2", 1500, false),
                new Order("P3", 80, true), "", "cupom10", 7};
        for (Object input : inputs) {
            System.out.println(input + " -> " + shippingRule(input));
        }

        System.out.println();
        System.out.println("=== null sem 'case null': NullPointerException ===");
        try {
            kind(null);
        } catch (NullPointerException e) {
            System.out.println("NPE: nem 'case Object o' casa com null desde o Java 19");
        }

        System.out.println();
        System.out.println("=== 'case null, default' trata nulo junto com o restante ===");
        Arrays.asList("ABC", null, 3.14).forEach(v -> System.out.println(v + " -> " + label(v)));
    }

    static String shippingRule(Object input) {
        return switch (input) {
            // O guard é uma expressão booleana qualquer (pode conter && sem ambiguidade)
            case Order o when o.total() > 1000 && o.express() -> "frete grátis expresso";
            case Order o when o.total() > 1000 -> "frete grátis";
            case Order o -> "frete normal para " + o.id();
            case String s when s.isBlank() -> "código vazio";
            case String s -> "cupom " + s.toUpperCase();
            default -> "entrada ignorada";
        };
    }

    /** Pattern incondicional: no Java 17/18 casaria com null; no 19+ null gera NPE. */
    static String kind(Object value) {
        return switch (value) {
            case String s -> "texto";
            case Object o -> "objeto " + o.getClass().getSimpleName();
        };
    }

    static String label(Object value) {
        return switch (value) {
            case String s -> "texto(" + s.length() + ")";
            case null, default -> "sem tratamento específico";
        };
    }
}
