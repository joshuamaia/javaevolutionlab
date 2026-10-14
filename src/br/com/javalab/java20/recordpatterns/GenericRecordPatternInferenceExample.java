package br.com.javalab.java20.recordpatterns;

import java.util.List;

/**
 * Java 20 - Record Patterns, segunda Preview (JEP 432): inferência de argumentos de tipo
 *
 * <p>Status: Segunda Preview no Java 20. Histórico: 1a Preview no 19 (JEP 405); definitivo no 21 (JEP 440).
 * Este código usa a sintaxe que permaneceu igual e compila no JDK 25 sem flags.</p>
 *
 * <p>Mudanças do Java 20:</p>
 * <ul>
 *   <li><b>Inferência de argumentos de tipo</b> em record patterns genéricos: em vez de
 *       {@code Box<Box<String>>(Box<String>(var s))} basta {@code Box(Box(var s))} (este exemplo);</li>
 *   <li>record patterns no cabeçalho do enhanced for - REMOVIDO no 21, ver
 *       {@code legacy/java20/recordpatterns/RecordPatternsInEnhancedFor20.java};</li>
 *   <li>remoção dos named record patterns do Java 19.</li>
 * </ul>
 * <p>Antes (Java 19): os argumentos de tipo precisavam ser escritos ou o pattern usava tipo raw.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java20/recordpatterns/GenericRecordPatternInferenceExample.java
 * </pre>
 */
public class GenericRecordPatternInferenceExample {

    /** Embalagem genérica de um item enviado. */
    record Box<T>(T content) { }

    /** Par genérico (ex.: produto e quantidade). */
    record Pair<A, B>(A first, B second) { }

    public static void main(String[] args) {
        Box<Box<String>> giftBox = new Box<>(new Box<>("Relógio"));

        System.out.println("=== Java 19: argumentos de tipo explícitos ===");
        if (giftBox instanceof Box<Box<String>>(Box<String>(String item))) {
            System.out.println("Item embrulhado: " + item);
        }

        System.out.println();
        System.out.println("=== Java 20+: argumentos de tipo inferidos ===");
        if (giftBox instanceof Box(Box(var item))) {
            // 'item' foi inferido como String (não Object): podemos chamar métodos de String
            System.out.println("Item embrulhado: " + item.toUpperCase());
        }

        System.out.println();
        System.out.println("=== Inferência também em switch ===");
        List<Pair<String, Integer>> cart = List.of(new Pair<>("Caneta", 3), new Pair<>("Caderno", 0));
        for (Pair<String, Integer> line : cart) {
            System.out.println(describe(line));
        }
    }

    static String describe(Pair<String, Integer> line) {
        return switch (line) {
            // Pair(var p, var q) é inferido como Pair<String, Integer>: q é Integer, p é String
            case Pair(var product, var quantity) when quantity == 0 -> product + ": sem estoque reservado";
            case Pair(var product, var quantity) -> product + " x" + quantity + " (" + product.length() + " letras)";
        };
    }
}
