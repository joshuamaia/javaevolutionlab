package br.com.javalab.java24.constructors;

/**
 * Java 24 - Flexible Constructor Bodies, terceira Preview (JEP 492)
 *
 * <p>Status: Terceira Preview (sem mudanças em relação ao 23). Histórico: Preview no 22
 * (JEP 447), segunda Preview no 23 (JEP 482); definitivo no Java 25 (JEP 513). Compila no
 * JDK 25 sem flags (no JDK 24 exigia {@code --enable-preview}).</p>
 *
 * <p>O que é: o "prólogo" (código antes de {@code super(...)} OU {@code this(...)}) pode
 * validar, calcular e atribuir campos, sem usar a instância em construção.</p>
 * <p>Foco deste exemplo (os de 22/23 mostraram {@code super(...)}): delegação com
 * {@code this(...)}, inclusive em RECORDS - construtores não canônicos de record são
 * obrigados a delegar via {@code this(...)}, e agora podem preparar os argumentos antes.</p>
 * <p>Benefício: um construtor alternativo pode fazer parsing uma única vez e repassar
 * vários valores derivados, sem métodos estáticos auxiliares nem parsing duplicado.</p>
 * <p>Antes: {@code this(parseCents(text), parseCurrency(text))} - o texto era quebrado duas
 * vezes, em dois métodos estáticos, porque nada podia vir antes do {@code this(...)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/constructors/FlexibleConstructorThisDelegationExample.java
 * </pre>
 */
public class FlexibleConstructorThisDelegationExample {

    // ---- Antes: dois helpers estáticos, cada um repetindo o parsing ----
    record OldMoney(long cents, String currency) {
        OldMoney(String text) {
            this(parseCents(text), parseCurrency(text));
        }

        private static long parseCents(String text) {
            return Math.round(Double.parseDouble(text.trim().split("\\s+")[0]) * 100);
        }

        private static String parseCurrency(String text) {
            return text.trim().split("\\s+")[1].toUpperCase();
        }
    }

    // ---- Java 24 (Preview): prólogo antes de this(...) ----
    record Money(long cents, String currency) {
        Money {
            if (cents < 0) throw new IllegalArgumentException("valor negativo");
        }

        Money(String text) {
            String[] parts = text.trim().split("\\s+");          // parsing feito uma única vez
            if (parts.length != 2) {
                throw new IllegalArgumentException("formato esperado: '<valor> <moeda>', recebido: " + text);
            }
            long cents = Math.round(Double.parseDouble(parts[0]) * 100);
            String currency = parts[1].toUpperCase();
            this(cents, currency);                               // delega ao construtor canônico
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (helpers estáticos) ===");
        System.out.println(new OldMoney("19.90 brl"));

        System.out.println();
        System.out.println("=== Prólogo antes de this(...) em um record ===");
        System.out.println(new Money("19.90 brl"));
        System.out.println(new Money("  7.5   usd "));
        try {
            new Money("19.90");
        } catch (IllegalArgumentException e) {
            System.out.println("Rejeitado antes de delegar: " + e.getMessage());
        }
    }
}
