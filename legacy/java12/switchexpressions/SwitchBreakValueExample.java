import java.time.DayOfWeek;

/**
 * Java 12 - Switch Expressions (JEP 325) - sintaxe ORIGINAL da primeira Preview
 *
 * <p>Status: Preview (Java 12). A forma "break valor;" usada aqui para devolver o
 * resultado de um switch expression foi SUBSTITUIDA por "yield valor;" no Java 13
 * (JEP 354) e NAO compila em nenhum JDK a partir do 13.</p>
 *
 * <p>O que e: switch passa a poder ser usado como EXPRESSAO (produz um valor), com
 * rotulos "case A, B" e a forma com seta "case X -&gt; ...". Em blocos e na forma
 * tradicional com dois-pontos o valor era devolvido com "break valor;".</p>
 * <p>Beneficio: elimina variaveis temporarias e o fall-through acidental.</p>
 * <p>Antes: switch era apenas instrucao; era preciso declarar a variavel antes e
 * atribuir em cada case, lembrando de cada "break".</p>
 *
 * <p>Validacao: NAO executado neste projeto (JDK 12 indisponivel no ambiente de
 * desenvolvimento); revisado contra os exemplos de codigo da JEP.</p>
 *
 * <p>Execucao (requer JDK 12 - nao roda no JDK 13+):</p>
 * <pre>
 * java --enable-preview --source 12 legacy/java12/switchexpressions/SwitchBreakValueExample.java
 * </pre>
 * <p>Forma atual equivalente: src/br/com/javalab/java12/switchexpressions/SwitchExpressionPreviewExample.java
 * e src/br/com/javalab/java13/switchexpressions/SwitchYieldExample.java</p>
 */
public class SwitchBreakValueExample {

    public static void main(String[] args) {
        System.out.println("=== Seta com valor direto (sobreviveu ate hoje) ===");
        for (DayOfWeek day : DayOfWeek.values()) {
            int letters = switch (day) {
                case MONDAY, FRIDAY, SUNDAY -> 6;
                case TUESDAY -> 7;
                case THURSDAY, SATURDAY -> 8;
                case WEDNESDAY -> 9;
            };
            System.out.println(day + " -> " + letters);
        }

        System.out.println("=== Bloco com 'break valor' (somente Java 12) ===");
        DayOfWeek day = DayOfWeek.WEDNESDAY;
        int code = switch (day) {
            case MONDAY -> 0;
            case TUESDAY -> 1;
            default -> {
                int k = day.toString().length();
                break k * 10; // no Java 13+ isto vira: yield k * 10;
            }
        };
        System.out.println("Codigo de " + day + ": " + code);

        System.out.println("=== Forma com dois-pontos e 'break valor' (somente Java 12) ===");
        String status = "PAID";
        int priority = switch (status) {
            case "NEW":
                break 1;
            case "PAID":
                System.out.println("Pedido pago, prioridade alta");
                break 2;
            default:
                break 0;
        };
        System.out.println("Prioridade: " + priority);
    }
}
