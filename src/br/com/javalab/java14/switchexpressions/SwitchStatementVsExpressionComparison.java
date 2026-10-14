package br.com.javalab.java14.switchexpressions;

/**
 * Java 14 - Comparacao: switch tradicional (instrucao) vs switch expression (JEP 361)
 *
 * <p>Status: Definitivo (Java 14).</p>
 *
 * <p>O que e: comparacao lado a lado dos problemas classicos do switch com dois-pontos
 * e de como a forma com seta / switch expression os resolve:</p>
 * <ul>
 *   <li>fall-through: sem "break", a execucao "cai" no case seguinte;</li>
 *   <li>break esquecido: bug silencioso que compila normalmente;</li>
 *   <li>exaustividade: o switch instrucao aceita ignorar constantes do enum; o switch
 *       expression obriga a cobrir todas (ou ter default);</li>
 *   <li>atribuicao definida: com instrucao, a variavel precisa ser declarada antes e
 *       atribuida em todo caminho.</li>
 * </ul>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java14/switchexpressions/SwitchStatementVsExpressionComparison.java
 * </pre>
 */
public class SwitchStatementVsExpressionComparison {

    enum OrderStatus { NEW, PAID, SHIPPED, DELIVERED }

    public static void main(String[] args) {
        System.out.println("=== 1) Fall-through intencional (antigo) vs rotulos multiplos (novo) ===");
        for (OrderStatus s : OrderStatus.values()) {
            System.out.printf("%-10s antigo: %-10s novo: %s%n", s, canCancelOld(s), canCancelNew(s));
        }

        System.out.println("=== 2) Break esquecido: bug silencioso no switch tradicional ===");
        System.out.println("Antigo (bugado) PAID -> " + messageWithForgottenBreak(OrderStatus.PAID));
        System.out.println("Novo            PAID -> " + messageNew(OrderStatus.PAID));

        System.out.println("=== 3) Exaustividade com enum ===");
        System.out.println("Antigo DELIVERED -> " + labelOld(OrderStatus.DELIVERED) + "  (caso esquecido, ninguem avisou)");
        System.out.println("Novo   DELIVERED -> " + labelNew(OrderStatus.DELIVERED) + "  (esquecer um caso = erro de compilacao)");
    }

    // ---------- 1) fall-through ----------
    private static boolean canCancelOld(OrderStatus s) {
        boolean result;
        switch (s) {
            case NEW:            // cai para o proximo de proposito
            case PAID:
                result = true;
                break;
            default:
                result = false;
        }
        return result;
    }

    private static boolean canCancelNew(OrderStatus s) {
        return switch (s) {
            case NEW, PAID -> true;
            case SHIPPED, DELIVERED -> false;
        };
    }

    // ---------- 2) break esquecido ----------
    private static String messageWithForgottenBreak(OrderStatus s) {
        String msg = "";
        switch (s) {
            case NEW:
                msg += "Aguardando pagamento. ";
                break;
            case PAID:
                msg += "Pagamento confirmado. ";
                // break esquecido! cai no SHIPPED
            case SHIPPED:
                msg += "Pedido enviado. ";
                break;
            default:
                msg += "Concluido. ";
        }
        return msg.trim();
    }

    private static String messageNew(OrderStatus s) {
        return switch (s) {     // com seta nao existe fall-through
            case NEW -> "Aguardando pagamento.";
            case PAID -> "Pagamento confirmado.";
            case SHIPPED -> "Pedido enviado.";
            case DELIVERED -> "Concluido.";
        };
    }

    // ---------- 3) exaustividade ----------
    private static String labelOld(OrderStatus s) {
        String label = "(sem rotulo)";  // valor padrao necessario para compilar
        switch (s) {
            case NEW: label = "Novo"; break;
            case PAID: label = "Pago"; break;
            case SHIPPED: label = "Enviado"; break;
            // DELIVERED esquecido: compila sem aviso
        }
        return label;
    }

    private static String labelNew(OrderStatus s) {
        return switch (s) {
            case NEW -> "Novo";
            case PAID -> "Pago";
            case SHIPPED -> "Enviado";
            case DELIVERED -> "Entregue"; // remova esta linha: "the switch expression does not cover all possible input values"
        };
    }
}
