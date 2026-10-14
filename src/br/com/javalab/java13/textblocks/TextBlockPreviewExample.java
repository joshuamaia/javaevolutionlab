package br.com.javalab.java13.textblocks;

/**
 * Java 13 - Text Blocks (JEP 355)
 *
 * <p>Status: Preview. Segunda Preview no Java 14 (JEP 368, novos escapes \s e
 * \&lt;nova-linha&gt;); Definitivo no Java 15 (JEP 378).</p>
 *
 * <p>O que e: literais de string multilinha delimitados por tres aspas duplas.
 * O compilador remove a indentacao "incidental" (a margem comum a todas as linhas,
 * definida inclusive pela posicao das aspas de fechamento), normaliza as quebras de
 * linha para \n e so depois interpreta os escapes.</p>
 * <p>Beneficio: SQL, JSON e HTML legiveis dentro do codigo, sem "\n" + concatenacao.</p>
 * <p>Antes: concatenacao de linhas com "\n" e aspas escapadas.</p>
 *
 * <p>No Java 13 existiam APENAS os delimitadores e os escapes tradicionais (\n, \t,
 * \", ...). Os escapes \s (espaco preservado) e \&lt;nova-linha&gt; (juntar linhas)
 * so chegaram no Java 14. Este arquivo usa somente o que existia no 13; essa sintaxe
 * ficou igual no definitivo, por isso compila no JDK 25 sem flags.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java13/textblocks/TextBlockPreviewExample.java
 * </pre>
 */
public class TextBlockPreviewExample {

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: concatenacao ===");
        String oldSql = "SELECT id, name, total\n"
                + "  FROM orders\n"
                + " WHERE status = 'PAID'\n";
        System.out.print(oldSql);

        System.out.println("=== Java 13: text block (indentacao incidental removida) ===");
        String sql = """
                SELECT id, name, total
                  FROM orders
                 WHERE status = 'PAID'
                """;
        System.out.print(sql);
        System.out.println("Mesmo conteudo da versao antiga? " + sql.equals(oldSql));

        System.out.println("=== Fechamento mais a esquerda = indentacao preservada ===");
        String indented = """
                <ul>
                  <li>Notebook</li>
                </ul>
            """;
        System.out.print(indented);

        System.out.println("=== Fechamento na mesma linha = sem \\n final ===");
        String noTrailingNewline = """
                {"id": 1, "status": "PAID"}""";
        System.out.println("[" + noTrailingNewline + "]");

        System.out.println("=== Aspas nao precisam de escape; escapes tradicionais funcionam ===");
        String json = """
                {
                  "customer": "Ana",
                  "note": "linha1\\nlinha2 (barra literal) e tab:\t<-"
                }
                """;
        System.out.print(json);

        System.out.println("=== Espacos finais sao removidos (no 13 nao havia \\s para evita-lo) ===");
        String trailing = """
                coluna1
                """;
        System.out.println("Tamanho: " + trailing.length() + " -> " + trailing.replace("\n", "\\n"));
    }
}
