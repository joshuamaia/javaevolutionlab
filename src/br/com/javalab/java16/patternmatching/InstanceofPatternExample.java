package br.com.javalab.java16.patternmatching;

import java.util.List;

/**
 * Java 16 - Pattern Matching for instanceof (JEP 394)
 *
 * <p>Status: Definitivo. Historico: Preview no Java 14 (JEP 305), segunda Preview no
 * Java 15 (JEP 375).</p>
 *
 * <p>O que e: {@code x instanceof Tipo t} testa e ja declara a variavel t do tipo.</p>
 * <p>O que mudou no definitivo em relacao as Previews:</p>
 * <ul>
 *   <li>pattern variables deixaram de ser implicitamente {@code final}: podem ser
 *       reatribuidas (e podem ser declaradas {@code final} explicitamente);</li>
 *   <li>e erro de compilacao quando o teste e inutil, isto e, quando a expressao ja e
 *       sempre do tipo do pattern ({@code String s = ...; s instanceof String t}).</li>
 * </ul>
 * <p>Comparacao detalhada com a forma antiga: {@code InstanceofVsPatternMatchingComparison}.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/patternmatching/InstanceofPatternExample.java
 * </pre>
 */
public class InstanceofPatternExample {

    public static void main(String[] args) {
        List<Object> inputs = List.of("  Notebook  ", 3, 4500.0, List.of("Mouse", "Teclado"));

        System.out.println("=== Pattern variable reatribuivel (novo no 16) ===");
        for (Object input : inputs) {
            if (input instanceof String name) {
                name = name.strip().toUpperCase();   // nas Previews isto era erro (variavel final)
                System.out.println("Produto: " + name);
            } else if (input instanceof final Integer quantity) {   // final explicito permitido
                System.out.println("Quantidade: " + quantity);
            } else if (input instanceof Double price && price > 1000) {
                System.out.println("Preco alto: " + price);
            } else if (input instanceof List<?> list) {
                System.out.println("Lista com " + list.size() + " itens");
            }
        }

        System.out.println("=== Teste inutil agora e erro de compilacao ===");
        String sku = "NB-01";
        // if (sku instanceof String s) {}   // ERRO no 16: "expression type String is a subtype of pattern type String"
        System.out.println("'" + sku + "' ja e String: nao ha o que testar");
    }
}
