package br.com.javalab.java13.switchexpressions;

/**
 * Java 13 - Switch Expressions, segunda Preview (JEP 354): a palavra {@code yield}
 *
 * <p>Status: Segunda Preview. Preview no Java 12 (JEP 325, com "break valor");
 * Definitivo no Java 14 (JEP 361) com a mesma sintaxe deste arquivo.</p>
 *
 * <p>O que mudou no 13: para devolver um valor de dentro de um bloco "{ }" na forma
 * com seta, ou na forma tradicional com dois-pontos, usa-se {@code yield valor;}
 * em vez de {@code break valor;}. Assim "break" volta a ter um unico significado
 * (sair de laco/switch) e "yield" passa a ser o "return" do switch expression.
 * {@code yield} e um identificador restrito (contextual), nao uma palavra reservada.</p>
 * <p>Beneficio: sintaxe sem ambiguidade entre break de laco e valor do switch.</p>
 * <p>Antes (Java 12): legacy/java12/switchexpressions/SwitchBreakValueExample.java</p>
 *
 * <p>Esta sintaxe compila no JDK 25 sem flags.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java13/switchexpressions/SwitchYieldExample.java
 * </pre>
 */
public class SwitchYieldExample {

    enum PaymentMethod { PIX, CREDIT_CARD, BOLETO }

    public static void main(String[] args) {
        System.out.println("=== yield em bloco da forma com seta ===");
        double amount = 1000.0;
        for (PaymentMethod method : PaymentMethod.values()) {
            double fee = switch (method) {
                case PIX -> 0.0;
                case CREDIT_CARD -> {
                    double base = amount * 0.0299;
                    System.out.println("  (cartao: taxa percentual calculada)");
                    yield base + 0.50; // Java 12 usava: break base + 0.50;
                }
                case BOLETO -> 3.49;
            };
            System.out.printf("%-12s taxa: R$ %.2f%n", method, fee);
        }

        System.out.println("=== yield na forma tradicional com dois-pontos ===");
        String status = "SHIPPED";
        int step = switch (status) {
            case "NEW":
                yield 1;
            case "PAID":
                yield 2;
            case "SHIPPED":
                System.out.println("  (pedido ja saiu do estoque)");
                yield 3;
            default:
                yield 0;
        };
        System.out.println("Etapa do pedido " + status + ": " + step);

        System.out.println("=== break voltou a significar so 'sair' ===");
        for (int i = 1; i <= 5; i++) {
            String kind = switch (i % 2) {
                case 0 -> "par";
                default -> "impar";
            };
            if (i == 4) {
                System.out.println("  break do laco em i=4");
                break;
            }
            System.out.println("  " + i + " e " + kind);
        }
    }
}
