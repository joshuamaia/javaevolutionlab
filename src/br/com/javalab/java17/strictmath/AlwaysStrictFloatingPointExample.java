package br.com.javalab.java17.strictmath;

/**
 * Java 17 - Restore Always-Strict Floating-Point Semantics (JEP 306)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: desde o Java 17 TODA operação de ponto flutuante é avaliada de forma estrita
 * (IEEE 754, resultados idênticos em qualquer plataforma). O modificador {@code strictfp}
 * continua válido mas é redundante - o javac emite um aviso ({@code -Xlint:strictfp}).</p>
 * <p>Benefício: resultados reproduzíveis em qualquer CPU sem precisar lembrar de {@code strictfp};
 * fim da distinção entre "default" e "strict" (que existia por causa da FPU x87 de 80 bits).</p>
 * <p>Antes (Java 1.2 a 16): sem {@code strictfp}, a JVM podia usar precisão estendida em
 * cálculos intermediários; {@code StrictMath} e {@code strictfp} garantiam portabilidade.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java17/strictmath/AlwaysStrictFloatingPointExample.java
 * </pre>
 * <p>Para ver o aviso do compilador:
 * {@code javac -Xlint:strictfp -d <tmp> src/br/com/javalab/java17/strictmath/AlwaysStrictFloatingPointExample.java}</p>
 */
public class AlwaysStrictFloatingPointExample {

    /** Antes do Java 17: strictfp garantia semântica IEEE estrita. Hoje é redundante. */
    @SuppressWarnings("strictfp")
    static strictfp double compoundInterestStrict(double principal, double rate, int months) {
        double amount = principal;
        for (int i = 0; i < months; i++) {
            amount *= 1 + rate;
        }
        return amount;
    }

    /** Desde o Java 17: mesmo comportamento sem o modificador. */
    static double compoundInterest(double principal, double rate, int months) {
        double amount = principal;
        for (int i = 0; i < months; i++) {
            amount *= 1 + rate;
        }
        return amount;
    }

    public static void main(String[] args) {
        System.out.println("=== Juros compostos: R$ 1000 a 1,1% ao mês por 120 meses ===");
        double strict = compoundInterestStrict(1000, 0.011, 120);
        double normal = compoundInterest(1000, 0.011, 120);
        System.out.println("Com strictfp : " + strict);
        System.out.println("Sem strictfp : " + normal);
        System.out.println("Bit a bit iguais? " + (Double.doubleToRawLongBits(strict) == Double.doubleToRawLongBits(normal)));

        System.out.println();
        System.out.println("=== Casos clássicos do IEEE 754 (iguais em qualquer plataforma) ===");
        System.out.println("0.1 + 0.2       = " + (0.1 + 0.2));
        System.out.println("Double.MAX * 2  = " + (Double.MAX_VALUE * 2));
        System.out.println("Math.sqrt(-1)   = " + Math.sqrt(-1));
        System.out.println("Math.sin(1e10)  = " + Math.sin(1e10) + " | StrictMath: " + StrictMath.sin(1e10));
    }
}
