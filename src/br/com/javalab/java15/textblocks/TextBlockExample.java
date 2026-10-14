package br.com.javalab.java15.textblocks;

/**
 * Java 15 - Text Blocks (JEP 378)
 *
 * <p>Status: Definitivo. Historico: Preview no Java 13 (JEP 355, delimitadores e
 * remocao de indentacao), segunda Preview no Java 14 (JEP 368, escapes \s e
 * \&lt;nova-linha&gt;). No Java 15 nada mudou na sintaxe; o recurso e os metodos
 * String.stripIndent/translateEscapes/formatted deixaram de ser Preview.</p>
 *
 * <p>O que e: literal de String multilinha entre tres aspas duplas.</p>
 * <p>Beneficio: textos estruturados legiveis e copiaveis diretamente para o codigo.</p>
 * <p>Antes: concatenacao com "\n" (ver {@code ConcatenationVsTextBlockComparison}).</p>
 *
 * <p>Este exemplo reune o recurso completo como ficou no definitivo e mostra que um
 * text block e apenas um literal de String comum (constante de compilacao).</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/textblocks/TextBlockExample.java
 * </pre>
 */
public class TextBlockExample {

    // constante de compilacao: pode ser usada em anotacoes, switch etc.
    static final String ORDER_QUERY = """
            SELECT o.id, c.name, o.total
              FROM orders o
              JOIN customers c ON c.id = o.customer_id
             WHERE o.id = ?
            """;

    public static void main(String[] args) {
        System.out.println("=== Text block definitivo: SQL como constante ===");
        System.out.print(ORDER_QUERY);

        System.out.println("=== Recursos combinados: formatted, \\s, \\<nova-linha>, \\\"\"\" ===");
        String email = """
                Ola, %s!
                Seu pedido %d foi enviado. Codigo de rastreio: \
                %s
                Itens:
                  - %-10s\s
                Para buscar no sistema use: \"""%s\"""
                """.formatted("Ana", 1001, "BR123456789", "Notebook", "PEDIDO-1001");
        System.out.print(email);

        System.out.println("=== E so uma String: mesmo objeto do literal equivalente ===");
        String block = """
                PAID""";
        String literal = "PAID";
        System.out.println("block == literal? " + (block == literal) + " (ambos internados no constant pool)");
        System.out.println("Linhas da query: " + ORDER_QUERY.lines().count());
    }
}
