package br.com.javalab.java14.switchexpressions;

/**
 * Java 14 - Switch Expressions (JEP 361)
 *
 * <p>Status: Definitivo. Historico: Preview no Java 12 (JEP 325, "break valor"),
 * segunda Preview no Java 13 (JEP 354, "yield valor").</p>
 *
 * <p>O que e: switch pode ser expressao (produz valor) ou instrucao; a forma com seta
 * nao tem fall-through; um switch expression precisa ser EXAUSTIVO (para enums, cobrir
 * todas as constantes dispensa o default: o compilador insere um default que lanca
 * erro se surgir uma constante nova em tempo de execucao).</p>
 * <p>Beneficio: menos codigo, menos bugs (break esquecido, caso esquecido).</p>
 * <p>Antes: switch somente como instrucao; comparacao detalhada em
 * {@code SwitchStatementVsExpressionComparison}.</p>
 *
 * <p>Este exemplo foca no que o definitivo consolidou: exaustividade, uso direto como
 * argumento/retorno, "throw" em um ramo e seta em switch instrucao.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java14/switchexpressions/SwitchExpressionExample.java
 * </pre>
 */
public class SwitchExpressionExample {

    enum Size { SMALL, MEDIUM, LARGE }

    public static void main(String[] args) {
        System.out.println("=== Exaustivo com enum: sem default ===");
        for (Size size : Size.values()) {
            System.out.println(size + " -> frete R$ " + shippingCost(size));
        }

        System.out.println("=== Switch expression como argumento de metodo ===");
        int month = 2;
        System.out.println("Mes " + month + " tem " + switch (month) {
            case 2 -> "28 ou 29";
            case 4, 6, 9, 11 -> "30";
            default -> "31";
        } + " dias");

        System.out.println("=== throw em um ramo ===");
        for (String code : new String[]{"BRL", "USD", "XYZ"}) {
            try {
                System.out.println(code + " -> " + currencySymbol(code));
            } catch (IllegalArgumentException e) {
                System.out.println(code + " -> erro: " + e.getMessage());
            }
        }

        System.out.println("=== Seta em switch instrucao (multiplas instrucoes em bloco) ===");
        Size size = Size.LARGE;
        switch (size) {
            case SMALL, MEDIUM -> System.out.println("Cabe em envelope");
            case LARGE -> {
                System.out.println("Precisa de caixa");
                System.out.println("Agendar coleta");
            }
        }
    }

    private static double shippingCost(Size size) {
        return switch (size) {   // se um novo Size for criado, isto deixa de compilar
            case SMALL -> 10.0;
            case MEDIUM -> 18.5;
            case LARGE -> 32.0;
        };
    }

    private static String currencySymbol(String code) {
        return switch (code) {
            case "BRL" -> "R$";
            case "USD" -> "US$";
            default -> throw new IllegalArgumentException("moeda desconhecida");
        };
    }
}
