import static java.util.FormatProcessor.FMT;

import java.util.List;

/**
 * Java 21 - String Templates, Preview (JEP 430) - RECURSO RETIRADO
 *
 * <p>Status: Retirado. Preview no Java 21 (JEP 430) e 22 (JEP 459); a terceira Preview prevista
 * (JEP 465) foi cancelada e o recurso foi REMOVIDO do JDK 23. Não existe no JDK 25 - por isso
 * este arquivo está em legacy/ e só roda no JDK 21 (ou 22) com {@code --enable-preview}.</p>
 *
 * <p>O que era: literais com expressões embutidas {@code \{expr}}, processados por um
 * "template processor": {@code STR} (interpolação simples, importado automaticamente),
 * {@code FMT} (com especificadores do Formatter) e {@code RAW} (devolve o {@code StringTemplate}
 * sem processar). Processadores customizados podiam validar/escapar valores (ex.: SQL, JSON).</p>
 * <p>Por que foi retirado: o design (sintaxe {@code PROCESSOR."..."} e o modelo de processadores)
 * recebeu críticas durante as Previews; a equipe decidiu recomeçar o design do zero.</p>
 * <p>Alternativas atuais (JDK 25): {@code String.formatted}, concatenação, {@code MessageFormat},
 * {@code StringBuilder} - veja
 * {@code src/br/com/javalab/java21/stringtemplates/StringTemplatesAlternativesExample.java}.</p>
 *
 * <p>Execução (requer JDK 21):</p>
 * <pre>
 * java --enable-preview --source 21 legacy/java21/stringtemplates/StringTemplatesPreview21.java
 * </pre>
 */
public class StringTemplatesPreview21 {

    record Item(String product, int quantity, double unitPrice) {
        double subtotal() { return quantity * unitPrice; }
    }

    public static void main(String[] args) {
        String customer = "Ana";
        List<Item> items = List.of(new Item("Caneta", 3, 2.5), new Item("Caderno", 2, 15.9));

        System.out.println("=== STR: interpolação ===");
        System.out.println(STR."Olá, \{customer}! Seu pedido tem \{items.size()} itens.");
        System.out.println(STR."Total: \{items.stream().mapToDouble(Item::subtotal).sum()}");

        System.out.println();
        System.out.println("=== FMT: com especificadores de formato ===");
        for (Item item : items) {
            System.out.println(FMT."%-8s\{item.product()} %3d\{item.quantity()} x %6.2f\{item.unitPrice()} = %7.2f\{item.subtotal()}");
        }

        System.out.println();
        System.out.println("=== Template multilinha (text block) ===");
        String json = STR."""
                {
                  "cliente": "\{customer}",
                  "itens": \{items.size()}
                }
                """;
        System.out.print(json);

        System.out.println();
        System.out.println("=== RAW: o StringTemplate antes de ser processado ===");
        StringTemplate template = StringTemplate.RAW."Cliente \{customer} comprou \{items.size()} itens";
        System.out.println("fragments: " + template.fragments());
        System.out.println("values   : " + template.values());
        System.out.println("process  : " + STR.process(template));
    }
}
