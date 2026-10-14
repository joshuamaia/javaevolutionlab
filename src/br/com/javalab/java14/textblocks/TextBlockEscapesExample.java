package br.com.javalab.java14.textblocks;

/**
 * Java 14 - Text Blocks, segunda Preview (JEP 368): escapes \s e \&lt;nova-linha&gt;
 *
 * <p>Status: Segunda Preview. Preview no Java 13 (JEP 355); Definitivo no Java 15
 * (JEP 378). Os escapes deste arquivo permaneceram iguais e compilam no JDK 25 sem flags.</p>
 *
 * <p>O que mudou no 14:</p>
 * <ul>
 *   <li>{@code \<nova-linha>}: barra invertida no fim da linha SUPRIME a quebra de linha,
 *       permitindo quebrar uma linha longa no codigo sem quebra-la no texto.</li>
 *   <li>{@code \s}: um espaco simples que NAO e removido pela limpeza de espacos finais
 *       (no 13 nao havia como manter espacos no fim da linha).</li>
 * </ul>
 * <p>Beneficio: textos de largura fixa e frases longas legiveis no codigo-fonte.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java14/textblocks/TextBlockEscapesExample.java
 * </pre>
 */
public class TextBlockEscapesExample {

    public static void main(String[] args) {
        System.out.println("=== Java 13: frase longa = linha longa no codigo ===");
        String longLine = """
                Prezado cliente, seu pedido 1001 foi aprovado e sera enviado em ate 2 dias uteis.
                """;
        System.out.print(longLine);

        System.out.println("=== Java 14: \\<nova-linha> quebra so no codigo ===");
        String joined = """
                Prezado cliente, seu pedido 1001 foi aprovado \
                e sera enviado em ate 2 dias uteis.
                """;
        System.out.print(joined);
        System.out.println("Textos iguais? " + longLine.equals(joined));

        System.out.println("=== Java 14: \\s preserva espacos finais (colunas de largura fixa) ===");
        String fixedWidth = """
                Notebook  \s
                Mouse     \s
                Teclado   \s
                """;
        fixedWidth.lines().forEach(line -> System.out.println("[" + line + "] tamanho=" + line.length()));

        System.out.println("=== Sem \\s os espacos finais sumiriam ===");
        String stripped = """
                Mouse
                """;
        System.out.println("[" + stripped.strip() + "] tamanho=" + stripped.lines().findFirst().orElse("").length());
    }
}
