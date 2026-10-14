package br.com.javalab.java13.textblocks;

/**
 * Java 13 - Metodos de String associados aos Text Blocks:
 * stripIndent, translateEscapes e formatted
 *
 * <p>Status: Preview (APIs). No Java 13 e 14 esses metodos foram adicionados junto com
 * a Preview de Text Blocks, marcados como {@code @Deprecated(forRemoval = true)} para
 * sinalizar que poderiam mudar. Tornaram-se Definitivos (sem deprecacao) no Java 15,
 * junto com o JEP 378. As assinaturas nao mudaram, por isso este codigo compila no
 * JDK 25 (e com --release 15) sem flags.</p>
 *
 * <p>O que e:</p>
 * <ul>
 *   <li>{@code stripIndent()}: aplica a uma string comum o mesmo algoritmo de remocao
 *       de indentacao incidental que o compilador aplica aos text blocks.</li>
 *   <li>{@code translateEscapes()}: interpreta sequencias de escape (\n, \t, \") que
 *       estao literalmente no texto (ex.: lidas de um arquivo).</li>
 *   <li>{@code formatted(args)}: equivalente a {@code String.format(this, args)},
 *       pensado para encadear apos um text block.</li>
 * </ul>
 * <p>Beneficio: tratar templates lidos em tempo de execucao como se fossem text blocks.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java13/textblocks/TextBlockStringMethodsExample.java
 * </pre>
 */
public class TextBlockStringMethodsExample {

    public static void main(String[] args) {
        System.out.println("=== stripIndent: texto montado em tempo de execucao ===");
        String fromConfig = "        Pedido: 1001\n          Item: Notebook\n        Status: PAID";
        System.out.println(fromConfig);
        System.out.println("-- apos stripIndent --");
        System.out.println(fromConfig.stripIndent());

        System.out.println("=== translateEscapes: escapes lidos como texto ===");
        String rawTemplate = "Cliente:\\tAna\\nObs.:\\t\\\"entregar a tarde\\\"";
        System.out.println("Bruto     : " + rawTemplate);
        System.out.println("Traduzido :");
        System.out.println(rawTemplate.translateEscapes());

        System.out.println("=== Abordagem antiga: String.format ===");
        String template = """
                Pedido %d de %s
                Total: R$ %.2f
                """;
        System.out.print(String.format(template, 1001, "Ana", 4500.0));

        System.out.println("=== formatted encadeado ao text block ===");
        System.out.print("""
                Pedido %d de %s
                Total: R$ %.2f
                """.formatted(1002, "Bruno", 89.9));
    }
}
