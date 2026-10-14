package br.com.javalab.java18.javadocsnippets;

import java.util.List;

/**
 * Java 18 - Code Snippets in Java API Documentation (JEP 413)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: a tag {@code @snippet} do Javadoc para trechos de código. Suporta
 * conteúdo inline, arquivos externos ({@code file=}/{@code class=}), regiões
 * ({@code @start}/{@code @end}), destaque ({@code @highlight}), substituição ({@code @replace})
 * e links ({@code @link}) por meio de comentários de marcação dentro do próprio trecho.</p>
 * <p>Benefício: código de exemplo legível na fonte (sem escapar {@code <}, {@code >}, {@code &} e
 * {@code @}), formatação consistente e possibilidade de validar trechos externos compilando-os.</p>
 * <p>Antes: {@code <pre>{@code ...}</pre>}, que não aceitava chaves desbalanceadas nem
 * anotações ({@code @Override}) e não tinha destaque.</p>
 *
 * <p>Exemplo de snippet inline com destaque:</p>
 * {@snippet :
 * List<Product> catalog = CodeSnippetsExample.sampleCatalog();
 * double total = CodeSnippetsExample.totalPrice(catalog); // @highlight substring="totalPrice"
 * System.out.println("Total: " + total);
 * }
 *
 * <p>Gerando a documentação (diretório temporário, fora do projeto):</p>
 * <pre>
 * javadoc -d %TEMP%\javalab-doc src/br/com/javalab/java18/javadocsnippets/CodeSnippetsExample.java
 * </pre>
 * <p>Abra {@code index.html} no diretório gerado e veja os trechos formatados.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java18/javadocsnippets/CodeSnippetsExample.java
 * </pre>
 */
public class CodeSnippetsExample {

    /**
     * Produto do catálogo.
     *
     * @param name  nome do produto
     * @param price preço unitário
     */
    public record Product(String name, double price) { }

    /**
     * Soma os preços de uma lista de produtos.
     *
     * <p>Snippet com região, destaque por expressão regular e substituição de texto:</p>
     * {@snippet :
     * // @start region="uso"
     * var products = List.of(new Product("Teclado", 199.90), new Product("Mouse", 89.90));
     * double total = totalPrice(products); // @highlight regex="\d+\.\d+" type=italic
     * System.out.println(total); // @replace substring="System.out.println(total);" replacement="..."
     * // @end region="uso"
     * }
     *
     * <p>Snippet com link para a API e anotação (impossível com {@code {@code}} puro):</p>
     * {@snippet :
     * @Override // anotações dentro do snippet não confundem o Javadoc
     * public String toString() {
     *     return String.valueOf(totalPrice(List.of())); // @link substring="List.of" target="List#of()"
     * }
     * }
     *
     * @param products produtos a somar
     * @return soma dos preços
     */
    public static double totalPrice(List<Product> products) {
        return products.stream().mapToDouble(Product::price).sum();
    }

    /**
     * Catálogo de exemplo.
     *
     * @return lista fixa de produtos
     */
    public static List<Product> sampleCatalog() {
        return List.of(new Product("Teclado", 199.90), new Product("Mouse", 89.90), new Product("Monitor", 1299.00));
    }

    /**
     * Executa o exemplo descrito nos snippets acima.
     *
     * @param args não utilizado
     */
    public static void main(String[] args) {
        System.out.println("=== O código documentado com @snippet roda normalmente ===");
        List<Product> catalog = sampleCatalog();
        catalog.forEach(p -> System.out.printf("%-8s R$ %8.2f%n", p.name(), p.price()));
        System.out.printf("Total: R$ %.2f%n", totalPrice(catalog));
        System.out.println();
        System.out.println("Para ver os snippets renderizados, gere o Javadoc (comando no cabeçalho da classe).");
    }
}
