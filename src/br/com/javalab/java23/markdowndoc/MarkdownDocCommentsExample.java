package br.com.javalab.java23.markdowndoc;

import java.util.List;

/// Java 23 - Markdown Documentation Comments (JEP 467)
///
/// Status: Definitivo (entrou direto como recurso final, sem Preview).
///
/// **O que é:** comentários de documentação podem ser escritos em **Markdown**
/// (CommonMark + tabelas) usando linhas que começam com `///`, em vez do bloco
/// `/** ... */` com HTML e tags como `<p>`, `<pre>` e `{@code ...}`.
///
/// **Benefício:** documentação mais legível no próprio código-fonte e mais fácil de
/// escrever; continua aceitando as tags do javadoc (`@param`, `@return`, `{@link}`...).
///
/// **Antes:** só `/** ... */` com HTML. Compare na classe aninhada [LegacyStyle].
///
/// Execução (JDK 25):
///
/// ```
/// java src/br/com/javalab/java23/markdowndoc/MarkdownDocCommentsExample.java
/// ```
///
/// Para ver o HTML gerado (em um diretório temporário qualquer):
///
/// ```
/// javadoc -d <dir-temporario> src/br/com/javalab/java23/markdowndoc/MarkdownDocCommentsExample.java
/// ```
public class MarkdownDocCommentsExample {

    private MarkdownDocCommentsExample() {
    }

    /// Item de um carrinho de compras.
    ///
    /// @param product nome do produto
    /// @param unitPrice preço unitário em reais
    /// @param quantity quantidade (deve ser **maior que zero**)
    public record CartItem(String product, double unitPrice, int quantity) {}

    /// Calcula o total do carrinho.
    ///
    /// Regras:
    ///
    /// 1. soma `unitPrice * quantity` de cada item;
    /// 2. aplica 5% de desconto quando o total passa de `R$ 500`.
    ///
    /// | Total bruto | Desconto |
    /// |-------------|----------|
    /// | até 500     | 0%       |
    /// | acima de 500| 5%       |
    ///
    /// Veja também [List#stream()] e [CartItem].
    ///
    /// @param items itens do carrinho
    /// @return total com desconto aplicado
    public static double total(List<CartItem> items) {
        double gross = items.stream().mapToDouble(i -> i.unitPrice() * i.quantity()).sum();
        return gross > 500 ? gross * 0.95 : gross;
    }

    /**
     * Estilo antigo (HTML) para comparação.
     *
     * <p>Calcula o total <b>sem</b> desconto. Ver {@link java.util.List#stream()}.</p>
     * <ul>
     *   <li>Listas exigem tags HTML;</li>
     *   <li>código inline exige {@code {@code ...}}.</li>
     * </ul>
     */
    public static final class LegacyStyle {
        private LegacyStyle() {
        }
    }

    /// Ponto de entrada do exemplo.
    ///
    /// @param args não utilizado
    public static void main(String[] args) {
        List<CartItem> cart = List.of(
                new CartItem("Monitor", 450.0, 1),
                new CartItem("Cabo HDMI", 25.0, 2));

        System.out.println("=== Código documentado com comentários Markdown (///) ===");
        cart.forEach(item -> System.out.println(item));
        System.out.printf("Total com desconto: R$ %.2f%n", total(cart));
        System.out.println();
        System.out.println("Gere o javadoc deste arquivo para ver listas, tabela e links renderizados.");
    }
}
