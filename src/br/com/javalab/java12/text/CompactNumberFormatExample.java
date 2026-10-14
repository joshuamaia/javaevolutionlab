package br.com.javalab.java12.text;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Java 12 - Compact Number Formatting
 *
 * <p>Status: Definitivo (API nova em java.text, sem JEP).</p>
 *
 * <p>O que e: {@code NumberFormat.getCompactNumberInstance(locale, style)} formata
 * numeros grandes de forma abreviada e sensivel ao idioma ("1,2 mil", "1.2K").</p>
 * <p>Beneficio: exibir contadores (seguidores, vendas, downloads) sem logica propria.</p>
 * <p>Antes: dividir por 1000/1000000 manualmente e concatenar sufixos fixos.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java12/text/CompactNumberFormatExample.java
 * </pre>
 */
public class CompactNumberFormatExample {

    public static void main(String[] args) {
        long[] values = {950, 1_200, 15_300, 2_500_000, 7_800_000_000L};
        Locale ptBR = Locale.forLanguageTag("pt-BR");

        System.out.println("=== Abordagem antiga: sufixos manuais ===");
        for (long v : values) {
            System.out.println(v + " -> " + manualFormat(v));
        }

        NumberFormat ptShort = NumberFormat.getCompactNumberInstance(ptBR, NumberFormat.Style.SHORT);
        NumberFormat ptLong = NumberFormat.getCompactNumberInstance(ptBR, NumberFormat.Style.LONG);
        NumberFormat usShort = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        NumberFormat usLong = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.LONG);
        usShort.setMaximumFractionDigits(1);
        ptShort.setMaximumFractionDigits(1);
        ptLong.setMaximumFractionDigits(1);
        usLong.setMaximumFractionDigits(1);

        System.out.println("=== Java 12: getCompactNumberInstance ===");
        System.out.printf("%-14s | %-12s | %-18s | %-8s | %s%n", "valor", "pt-BR SHORT", "pt-BR LONG", "US SHORT", "US LONG");
        for (long v : values) {
            System.out.printf("%-14d | %-12s | %-18s | %-8s | %s%n",
                    v, ptShort.format(v), ptLong.format(v), usShort.format(v), usLong.format(v));
        }
    }

    private static String manualFormat(long value) {
        if (value >= 1_000_000_000L) return (value / 1_000_000_000L) + "B";
        if (value >= 1_000_000) return (value / 1_000_000) + "M";
        if (value >= 1_000) return (value / 1_000) + "K";
        return String.valueOf(value);
    }
}
