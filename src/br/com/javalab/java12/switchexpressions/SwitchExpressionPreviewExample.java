package br.com.javalab.java12.switchexpressions;

/**
 * Java 12 - Switch Expressions (JEP 325)
 *
 * <p>Status: Preview. Segunda Preview no Java 13 (JEP 354, troca "break valor" por
 * "yield valor"); Definitivo no Java 14 (JEP 361).</p>
 *
 * <p>O que e: o switch passa a ser tambem uma EXPRESSAO, com rotulos multiplos
 * ("case A, B") e a forma com seta ("case X -&gt; valor"), que nao tem fall-through.</p>
 * <p>Beneficio: codigo mais curto, sem variavel temporaria e sem "break" esquecido.</p>
 * <p>Antes: switch instrucao, com atribuicao em cada case e "break" obrigatorio.</p>
 *
 * <p>Este arquivo usa apenas a parte da sintaxe do Java 12 que permaneceu igual
 * (seta + rotulos multiplos), por isso compila no JDK 25 sem flags. A forma original
 * com "break valor;" esta em legacy/java12/switchexpressions/SwitchBreakValueExample.java.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java12/switchexpressions/SwitchExpressionPreviewExample.java
 * </pre>
 */
public class SwitchExpressionPreviewExample {

    enum OrderStatus { NEW, PAID, SHIPPED, DELIVERED, CANCELLED }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (switch instrucao) ===");
        for (OrderStatus status : OrderStatus.values()) {
            String label;
            switch (status) {
                case NEW:
                case PAID:
                    label = "em processamento";
                    break;
                case SHIPPED:
                    label = "em transporte";
                    break;
                default:
                    label = "finalizado";
            }
            System.out.println(status + " -> " + label);
        }

        System.out.println("=== Java 12: switch como expressao (Preview) ===");
        for (OrderStatus status : OrderStatus.values()) {
            String label = switch (status) {
                case NEW, PAID -> "em processamento";
                case SHIPPED -> "em transporte";
                case DELIVERED, CANCELLED -> "finalizado";
            };
            System.out.println(status + " -> " + label);
        }

        System.out.println("=== Seta tambem em switch instrucao (sem fall-through) ===");
        OrderStatus status = OrderStatus.PAID;
        switch (status) {
            case NEW -> System.out.println("Aguardando pagamento");
            case PAID -> System.out.println("Separando produtos no estoque");
            default -> System.out.println("Nada a fazer");
        }
    }
}
