package br.com.javalab.java07.switchstrings;

/**
 * Java 7 - Strings no switch (Project Coin, JSR 334)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o {@code switch} passa a aceitar {@code String} como seletor. A comparação
 * usa {@code equals} (sensível a maiúsculas/minúsculas); internamente o compilador usa
 * {@code hashCode()} + {@code equals()}.</p>
 * <p>Benefício: substitui cadeias longas de {@code if/else if} com {@code equals}.</p>
 * <p>Antes: if/else com {@code "X".equals(valor)} ou conversão para enum.</p>
 * <p>Cuidado: seletor {@code null} lança NullPointerException.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/switchstrings/StringSwitchExample.java
 * </pre>
 */
public class StringSwitchExample {

    public static void main(String[] args) {
        String[] statuses = {"NEW", "PAID", "SHIPPED", "CANCELLED", "paid"};

        System.out.println("=== Abordagem antiga: if/else com equals ===");
        for (String status : statuses) {
            System.out.println(status + " -> " + describeOld(status));
        }

        System.out.println();
        System.out.println("=== Java 7: switch com String ===");
        for (String status : statuses) {
            System.out.println(status + " -> " + describe(status));
        }

        System.out.println();
        System.out.println("=== Cuidado com null ===");
        try {
            describe(null);
        } catch (NullPointerException e) {
            System.out.println("switch(null) lança NullPointerException");
        }
    }

    private static String describeOld(String status) {
        if ("NEW".equals(status)) {
            return "Pedido criado";
        } else if ("PAID".equals(status)) {
            return "Pagamento confirmado";
        } else if ("SHIPPED".equals(status) || "DELIVERED".equals(status)) {
            return "Em transporte/entregue";
        } else {
            return "Status desconhecido";
        }
    }

    private static String describe(String status) {
        switch (status) {
            case "NEW":
                return "Pedido criado";
            case "PAID":
                return "Pagamento confirmado";
            case "SHIPPED":
            case "DELIVERED":
                return "Em transporte/entregue";
            default:
                return "Status desconhecido (comparação é case-sensitive)";
        }
    }
}
