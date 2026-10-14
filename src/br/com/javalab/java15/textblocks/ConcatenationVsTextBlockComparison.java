package br.com.javalab.java15.textblocks;

/**
 * Java 15 - Comparacao: concatenacao tradicional vs Text Blocks (JEP 378)
 *
 * <p>Status: Definitivo (Java 15).</p>
 *
 * <p>O que e: os mesmos textos JSON, SQL e HTML escritos das duas formas. As
 * versoes antigas exigem "\n", aspas escapadas (\") e "+" em cada linha; as novas
 * sao copiadas diretamente do formato original. O programa confirma que os dois
 * resultados sao identicos.</p>
 * <p>Beneficio: legibilidade, menos erros (esquecer "\n" ou um espaco entre
 * clausulas SQL) e facilidade para copiar/colar entre o codigo e outras ferramentas.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/textblocks/ConcatenationVsTextBlockComparison.java
 * </pre>
 */
public class ConcatenationVsTextBlockComparison {

    public static void main(String[] args) {
        System.out.println("=== JSON ===");
        String jsonOld = "{\n"
                + "  \"id\": 1001,\n"
                + "  \"customer\": \"Ana\",\n"
                + "  \"items\": [\"Notebook\", \"Mouse\"]\n"
                + "}\n";
        String jsonNew = """
                {
                  "id": 1001,
                  "customer": "Ana",
                  "items": ["Notebook", "Mouse"]
                }
                """;
        compare(jsonOld, jsonNew);

        System.out.println("=== SQL ===");
        String sqlOld = "SELECT p.name, SUM(i.quantity) AS total\n"
                + "  FROM order_items i\n"
                + "  JOIN products p ON p.id = i.product_id\n"
                + " GROUP BY p.name\n"
                + " ORDER BY total DESC\n";
        String sqlNew = """
                SELECT p.name, SUM(i.quantity) AS total
                  FROM order_items i
                  JOIN products p ON p.id = i.product_id
                 GROUP BY p.name
                 ORDER BY total DESC
                """;
        compare(sqlOld, sqlNew);

        System.out.println("=== HTML ===");
        String htmlOld = "<html>\n"
                + "  <body>\n"
                + "    <h1 class=\"title\">Pedido 1001</h1>\n"
                + "    <p>Status: <b>PAID</b></p>\n"
                + "  </body>\n"
                + "</html>\n";
        String htmlNew = """
                <html>
                  <body>
                    <h1 class="title">Pedido 1001</h1>
                    <p>Status: <b>PAID</b></p>
                  </body>
                </html>
                """;
        compare(htmlOld, htmlNew);

        System.out.println("=== Erro classico da concatenacao: espaco esquecido ===");
        String buggy = "SELECT * FROM orders" + "WHERE status = 'PAID'";
        System.out.println(buggy + "   <- 'ordersWHERE'");
    }

    private static void compare(String oldStyle, String textBlock) {
        System.out.print(textBlock);
        System.out.println("-> identicos? " + oldStyle.equals(textBlock)
                + " | aspas escapadas na versao antiga: " + countEscapedQuotes(oldStyle));
    }

    private static long countEscapedQuotes(String text) {
        return text.chars().filter(c -> c == '"').count();
    }
}
