package br.com.javalab.java21.stringtemplates;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

/**
 * Java 21 - String Templates (JEP 430): alternativas que existem no JDK 25
 *
 * <p>Status: Retirado. String Templates foram Preview no Java 21 (JEP 430) e 22 (JEP 459) e
 * REMOVIDOS no JDK 23. O código original com {@code STR."..."} está em
 * {@code legacy/java21/stringtemplates/StringTemplatesPreview21.java} (roda só no JDK 21/22).</p>
 *
 * <p>Este arquivo NÃO simula templates: mostra as formas padrão de montar texto que continuam
 * disponíveis, lado a lado, para o mesmo resultado que o exemplo legado produzia.</p>
 * <ul>
 *   <li>concatenação com {@code +} (otimizada via invokedynamic desde o Java 9, JEP 280);</li>
 *   <li>{@code String.formatted} (Java 15) / {@code String.format};</li>
 *   <li>{@link MessageFormat} (índices posicionais, útil com internacionalização);</li>
 *   <li>{@link StringBuilder} para montagem incremental;</li>
 *   <li>text blocks (Java 15) + {@code formatted} para textos multilinha.</li>
 * </ul>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/stringtemplates/StringTemplatesAlternativesExample.java
 * </pre>
 */
public class StringTemplatesAlternativesExample {

    record Item(String product, int quantity, double unitPrice) {
        double subtotal() { return quantity * unitPrice; }
    }

    public static void main(String[] args) {
        String customer = "Ana";
        List<Item> items = List.of(new Item("Caneta", 3, 2.5), new Item("Caderno", 2, 15.9));

        System.out.println("=== Concatenação (equivalente a STR) ===");
        System.out.println("Olá, " + customer + "! Seu pedido tem " + items.size() + " itens.");

        System.out.println();
        System.out.println("=== String.formatted (equivalente a FMT) ===");
        for (Item item : items) {
            System.out.println("%-8s %3d x %6.2f = %7.2f".formatted(
                    item.product(), item.quantity(), item.unitPrice(), item.subtotal()));
        }

        System.out.println();
        System.out.println("=== MessageFormat (posicional, localizável) ===");
        MessageFormat message = new MessageFormat("{0} comprou {1,number,integer} itens, total {2,number,currency}",
                Locale.forLanguageTag("pt-BR"));
        System.out.println(message.format(new Object[] {customer, items.size(), items.stream().mapToDouble(Item::subtotal).sum()}));

        System.out.println();
        System.out.println("=== Text block + formatted (multilinha) ===");
        String json = """
                {
                  "cliente": "%s",
                  "itens": %d
                }
                """.formatted(customer, items.size());
        System.out.print(json);

        System.out.println();
        System.out.println("=== StringBuilder (montagem incremental) ===");
        StringBuilder receipt = new StringBuilder("Recibo de ").append(customer).append(':');
        items.forEach(i -> receipt.append(' ').append(i.product()).append('=').append(i.subtotal()));
        System.out.println(receipt);
    }
}
