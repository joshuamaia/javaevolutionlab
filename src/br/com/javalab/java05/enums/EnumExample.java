package br.com.javalab.java05.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Java 5 - Enums (JSR 201)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: tipos enumerados de verdade. Cada constante é um objeto único (singleton)
 * de uma classe que estende {@code java.lang.Enum}; o enum pode ter campos,
 * construtor, métodos e até métodos abstratos implementados por constante.</p>
 * <p>Benefício: segurança de tipo (não dá para passar um int qualquer), nomes legíveis
 * em logs ({@code toString()}), {@code values()}/{@code valueOf()}, uso em
 * {@code switch} e coleções especializadas e eficientes: {@code EnumSet} (vetor de bits)
 * e {@code EnumMap} (array indexado pelo ordinal).</p>
 * <p>Antes: constantes {@code public static final int} (o "int enum pattern") ou o
 * padrão "typesafe enum" escrito à mão (Effective Java, 1ª edição).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/enums/EnumExample.java
 * </pre>
 */
public class EnumExample {

    // ---- Antes: constantes int ----
    static final int STATUS_NEW = 0;
    static final int STATUS_PAID = 1;
    static final int STATUS_SHIPPED = 2;

    static String describeOld(int status) {
        switch (status) {
            case STATUS_NEW: return "Novo";
            case STATUS_PAID: return "Pago";
            case STATUS_SHIPPED: return "Enviado";
            default: return "??? (valor inválido: " + status + ")";
        }
    }

    // ---- Depois: enum com campo, construtor, método concreto e método abstrato ----
    enum OrderStatus {
        NEW("Novo") {
            OrderStatus next() { return PAID; }
        },
        PAID("Pago") {
            OrderStatus next() { return SHIPPED; }
        },
        SHIPPED("Enviado") {
            OrderStatus next() { return DELIVERED; }
        },
        DELIVERED("Entregue") {
            OrderStatus next() { return this; }
        },
        CANCELLED("Cancelado") {
            OrderStatus next() { return this; }
        };

        private final String label;

        OrderStatus(String label) {
            this.label = label;
        }

        String getLabel() {
            return label;
        }

        /** Cada constante fornece sua própria implementação (corpo específico da constante). */
        abstract OrderStatus next();

        boolean isFinal() {
            return this == DELIVERED || this == CANCELLED;
        }
    }

    static String customerMessage(OrderStatus status) {
        switch (status) { // switch em enum: case usa só o nome da constante
            case NEW: return "Recebemos seu pedido.";
            case PAID: return "Pagamento confirmado.";
            case SHIPPED: return "Seu pedido está a caminho.";
            case DELIVERED: return "Pedido entregue. Obrigado!";
            default: return "Pedido cancelado.";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: constantes int ===");
        System.out.println("STATUS_PAID -> " + describeOld(STATUS_PAID));
        System.out.println("42 -> " + describeOld(42) + "  (compila sem reclamar!)");
        System.out.println("NEW + SHIPPED -> " + describeOld(STATUS_NEW + STATUS_SHIPPED) + "  (aritmética sem sentido)");
        System.out.println("Log mostra só o número: status=" + STATUS_SHIPPED);

        System.out.println();
        System.out.println("=== Abordagem nova: enum ===");
        OrderStatus status = OrderStatus.NEW;
        while (!status.isFinal()) {
            System.out.printf("%-9s (%s, ordinal=%d) -> %s%n",
                    status, status.getLabel(), status.ordinal(), customerMessage(status));
            status = status.next();
        }
        System.out.println(status + " -> " + customerMessage(status));
        System.out.println("valueOf(\"PAID\"): " + OrderStatus.valueOf("PAID").getLabel());
        try {
            OrderStatus.valueOf("PAGO");
        } catch (IllegalArgumentException e) {
            System.out.println("valueOf inválido -> " + e.getClass().getSimpleName());
        }
        System.out.println("Comparação por == é segura: " + (OrderStatus.valueOf("NEW") == OrderStatus.NEW));

        System.out.println();
        System.out.println("=== EnumSet ===");
        EnumSet<OrderStatus> inProgress = EnumSet.range(OrderStatus.NEW, OrderStatus.SHIPPED);
        EnumSet<OrderStatus> finished = EnumSet.complementOf(inProgress);
        System.out.println("Em andamento: " + inProgress);
        System.out.println("Finalizados:  " + finished);
        System.out.println("Todos:        " + EnumSet.allOf(OrderStatus.class));

        System.out.println();
        System.out.println("=== EnumMap: contagem de pedidos por status ===");
        OrderStatus[] orders = {OrderStatus.PAID, OrderStatus.NEW, OrderStatus.PAID,
                OrderStatus.DELIVERED, OrderStatus.CANCELLED, OrderStatus.PAID};
        Map<OrderStatus, Integer> count = new EnumMap<OrderStatus, Integer>(OrderStatus.class);
        for (OrderStatus s : orders) {
            Integer current = count.get(s);
            count.put(s, current == null ? 1 : current + 1);
        }
        System.out.println(count + "  (ordem = ordem de declaração)");
    }
}
