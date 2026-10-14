package br.com.javalab.java16.sealed;

import java.util.Arrays;

/**
 * Java 16 - Sealed Classes, segunda Preview (JEP 397)
 *
 * <p>Status: Segunda Preview. Preview no Java 15 (JEP 360); Definitivo no Java 17
 * (JEP 409). No Java 16 exigia {@code --enable-preview --source 16}; o codigo abaixo
 * compila no JDK 25 sem flags.</p>
 *
 * <p>O que mudou no 16 (o basico esta em java15/sealed):</p>
 * <ul>
 *   <li>Reflexao: {@code Class.getPermittedSubclasses()} passa a devolver
 *       {@code Class<?>[]} (no 15 era {@code permittedSubclasses()} com ClassDesc[]).</li>
 *   <li>{@code sealed}, {@code non-sealed} e {@code permits} especificados como
 *       palavras-chave contextuais (continuam validos como nomes de variaveis).</li>
 *   <li>Classes locais nao podem estender tipos sealed.</li>
 *   <li>Conversoes de referencia mais rigorosas: o compilador usa a hierarquia selada
 *       para rejeitar casts impossiveis.</li>
 *   <li>Combinacao natural com records (definitivos no 16): {@code permits} pode ser
 *       omitido quando os subtipos estao no mesmo arquivo.</li>
 * </ul>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/sealed/SealedClassesSecondPreviewExample.java
 * </pre>
 */
public class SealedClassesSecondPreviewExample {

    /** permits omitido: subtipos inferidos por estarem no mesmo arquivo. */
    sealed interface OrderEvent {}

    record Created(int orderId) implements OrderEvent {}

    record Paid(int orderId, double amount) implements OrderEvent {}

    record Cancelled(int orderId, String reason) implements OrderEvent {}

    /** Classe comum (nao final) sem relacao com OrderEvent. */
    static class Customer {}

    public static void main(String[] args) {
        System.out.println("=== Reflexao nova: getPermittedSubclasses() -> Class<?>[] ===");
        System.out.println("OrderEvent e sealed? " + OrderEvent.class.isSealed());
        Arrays.stream(OrderEvent.class.getPermittedSubclasses())
                .forEach(c -> System.out.println("  permitido: " + c.getSimpleName() + " (record? " + c.isRecord() + ")"));

        System.out.println("=== Palavras contextuais ainda podem ser identificadores ===");
        String sealed = "lacrado";
        int permits = 3;
        System.out.println("sealed=" + sealed + ", permits=" + permits);

        System.out.println("=== Processando eventos (Java 16: instanceof com pattern) ===");
        OrderEvent[] events = {new Created(1001), new Paid(1001, 4500.0), new Cancelled(1002, "sem estoque")};
        for (OrderEvent event : events) {
            System.out.println(describe(event));
        }

        System.out.println("=== Cast impossivel detectado pelo compilador ===");
        Customer customer = new Customer();
        Object asObject = customer;
        // Com uma interface comum, (Interface) customer compilaria: uma subclasse de Customer poderia
        // implementar a interface. Como OrderEvent so permite records (finais), o
        // compilador sabe que nenhum Customer pode ser OrderEvent.
        // OrderEvent e = (OrderEvent) customer;        // ERRO: incompatible types
        // class LocalEvent implements OrderEvent {}    // ERRO: classe local nao pode estender sealed
        System.out.println("Customer e OrderEvent? " + (asObject instanceof OrderEvent) + " (veja os comentarios no codigo)");
    }

    private static String describe(OrderEvent event) {
        if (event instanceof Created c) {
            return "Pedido " + c.orderId() + " criado";
        } else if (event instanceof Paid p) {
            return "Pedido " + p.orderId() + " pago: R$ " + p.amount();
        } else if (event instanceof Cancelled x) {
            return "Pedido " + x.orderId() + " cancelado: " + x.reason();
        }
        throw new IllegalStateException(); // switch exaustivo sobre sealed so no Java 21
    }
}
