import java.util.List;

/**
 * Java 17 - Pattern Matching for switch (JEP 406) - SINTAXE ORIGINAL DA PRIMEIRA PREVIEW
 *
 * <p>Status: Preview no Java 17. Depois: segunda Preview no 18 (JEP 420), terceira no 19 (JEP 427),
 * quarta no 20 (JEP 433) e definitivo no Java 21 (JEP 441).</p>
 *
 * <p>O que é: {@code case} com type patterns ({@code case String s}), {@code case null} e
 * exaustividade com sealed. Nesta primeira versão havia duas construções que foram REMOVIDAS:</p>
 * <ul>
 *   <li><b>Guarded patterns com {@code &&}</b>: {@code case Order o && o.total() > 1000}.
 *       Substituído por {@code when} no Java 19 (JEP 427).</li>
 *   <li><b>Parenthesized patterns</b>: {@code case (String s && s.isBlank())}.
 *       Removido no Java 21 (JEP 441).</li>
 * </ul>
 * <p>Por isso este arquivo NÃO compila no JDK 25 e fica em {@code legacy/} (sem pacote).</p>
 *
 * <p>Execução (requer JDK 17):</p>
 * <pre>
 * java --enable-preview --source 17 legacy/java17/patternswitch/PatternMatchingSwitchPreview17.java
 * </pre>
 */
public class PatternMatchingSwitchPreview17 {

    record Order(String id, double total) { }

    sealed interface Notification permits Email, Sms { }
    record Email(String address) implements Notification { }
    record Sms(String phone) implements Notification { }

    public static void main(String[] args) {
        System.out.println("=== Pattern matching em switch (Java 17, 1a Preview) ===");
        List<Object> inputs = List.of(
                new Order("A-1", 1500.0), new Order("A-2", 80.0), "  ", "pedido urgente", 42);
        for (Object input : inputs) {
            System.out.println(describe(input));
        }
        System.out.println(describe(null));

        System.out.println();
        System.out.println("=== Exaustividade com sealed (sem default) ===");
        System.out.println(send(new Email("ana@loja.com")));
        System.out.println(send(new Sms("+55 11 99999-0000")));
    }

    static String describe(Object input) {
        return switch (input) {
            case null -> "nulo (case null era novidade desta Preview)";
            // Guarded pattern com && (sintaxe removida depois)
            case Order o && o.total() > 1000 -> "Pedido grande " + o.id();
            case Order o -> "Pedido comum " + o.id();
            // Parenthesized pattern (sintaxe removida no Java 21)
            case (String s && s.isBlank()) -> "Texto vazio";
            case String s -> "Texto: " + s;
            default -> "Outro: " + input;
        };
    }

    static String send(Notification notification) {
        return switch (notification) {
            case Email e -> "Enviando e-mail para " + e.address();
            case Sms s -> "Enviando SMS para " + s.phone();
        };
    }
}
