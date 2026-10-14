import java.util.List;

/**
 * Java 20 - Record Patterns, segunda Preview (JEP 432): record patterns no enhanced for - SINTAXE REMOVIDA
 *
 * <p>Status: Segunda Preview no Java 20. Este trecho específico foi REMOVIDO no Java 21 (JEP 440):
 * a versão definitiva de record patterns não aceita patterns no cabeçalho do {@code for}.
 * Por isso este arquivo não compila no JDK 25 (fica em legacy/).</p>
 *
 * <p>O que era: {@code for (Point(var x, var y) : points)} desestruturava cada elemento.
 * Se um elemento fosse {@code null}, lançava {@code MatchException}.</p>
 * <p>Por que saiu: a JEP 440 removeu o recurso para reavaliá-lo em uma proposta futura
 * (ex.: patterns em declarações em geral).</p>
 * <p>Forma atual equivalente: laço normal + {@code if (element instanceof Point(var x, var y))}
 * ou um {@code switch} no corpo.</p>
 *
 * <p>Execução (requer JDK 20 - não validado: JDK 20 indisponível neste ambiente):</p>
 * <pre>
 * java --enable-preview --source 20 legacy/java20/recordpatterns/RecordPatternsInEnhancedFor20.java
 * </pre>
 */
public class RecordPatternsInEnhancedFor20 {

    record Item(String product, int quantity, double unitPrice) { }

    public static void main(String[] args) {
        List<Item> cart = List.of(
                new Item("Caneta", 3, 2.50),
                new Item("Caderno", 2, 15.90),
                new Item("Mochila", 1, 129.00));

        System.out.println("=== Record pattern no cabeçalho do for (só Java 20) ===");
        double total = 0;
        for (Item(var product, var quantity, var unitPrice) : cart) {
            double subtotal = quantity * unitPrice;
            total += subtotal;
            System.out.printf("%-8s %d x %6.2f = %7.2f%n", product, quantity, unitPrice, subtotal);
        }
        System.out.printf("Total: %.2f%n", total);

        System.out.println();
        System.out.println("=== Forma que continua válida (Java 21+) ===");
        for (Item item : cart) {
            if (item instanceof Item(var product, var quantity, var unitPrice) && quantity > 1) {
                System.out.println(product + " tem " + quantity + " unidades");
            }
        }
    }
}
