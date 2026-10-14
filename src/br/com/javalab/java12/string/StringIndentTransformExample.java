package br.com.javalab.java12.string;

/**
 * Java 12 - String.indent e String.transform
 *
 * <p>Status: Definitivo (APIs novas, sem JEP).</p>
 *
 * <p>O que e: {@code indent(n)} adiciona (n &gt; 0) ou remove (n &lt; 0) espacos no
 * inicio de cada linha e normaliza os terminadores para "\n";
 * {@code transform(f)} aplica uma funcao a string e devolve o resultado (qualquer tipo).</p>
 * <p>Beneficio: formatacao de texto multilinha sem lacos manuais e encadeamento
 * fluente de transformacoes.</p>
 * <p>Antes: dividir em linhas e concatenar prefixos manualmente; chamar funcoes
 * aninhadas f(g(h(s))).</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java12/string/StringIndentTransformExample.java
 * </pre>
 */
public class StringIndentTransformExample {

    public static void main(String[] args) {
        String report = "Pedido 1001\nCliente: Ana\nTotal: R$ 250,00";

        System.out.println("=== Abordagem antiga: indentar linha a linha ===");
        StringBuilder sb = new StringBuilder();
        for (String line : report.split("\n")) {
            sb.append("    ").append(line).append("\n");
        }
        System.out.print(sb);

        System.out.println("=== Java 12: indent(4) ===");
        System.out.print(report.indent(4));

        System.out.println("=== indent(-2) remove ate 2 espacos de cada linha ===");
        String nested = "    item A\n    item B\n  item C";
        System.out.print(nested.indent(-2));

        System.out.println("=== Abordagem antiga: funcoes aninhadas ===");
        String rawName = "   maria da silva  ";
        System.out.println(normalize(capitalize(rawName.trim())));

        System.out.println("=== Java 12: transform encadeado ===");
        String name = rawName
                .transform(String::trim)
                .transform(StringIndentTransformExample::capitalize)
                .transform(StringIndentTransformExample::normalize);
        System.out.println(name);

        // transform pode devolver outro tipo
        int length = rawName.transform(String::strip).transform(String::length);
        System.out.println("Tamanho sem espacos: " + length);
    }

    private static String capitalize(String text) {
        StringBuilder result = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (word.length() > 2) {
                word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            }
            result.append(word).append(' ');
        }
        return result.toString().trim();
    }

    private static String normalize(String text) {
        return "[" + text + "]";
    }
}
