package br.com.javalab.java11.string;

import java.util.Collections;
import java.util.Map;

/**
 * Java 11 - String.repeat
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code "ab".repeat(3)} devolve {@code "ababab"}.</p>
 * <p>Benefício: separadores, indentação, barras de progresso e preenchimento em uma
 * chamada, com implementação otimizada.</p>
 * <p>Antes: laço com {@code StringBuilder}, {@code String.join("", Collections.nCopies(n, s))}
 * ou {@code new String(new char[n]).replace("\0", s)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/string/StringRepeatExample.java
 * </pre>
 */
public class StringRepeatExample {

    public static void main(String[] args) {
        System.out.println("=== Antes ===");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            builder.append('-');
        }
        System.out.println(builder);
        System.out.println(String.join("", Collections.nCopies(30, "=")));

        System.out.println();
        System.out.println("=== Java 11: repeat ===");
        System.out.println("-".repeat(30));
        System.out.println("RELATÓRIO DE VENDAS");
        System.out.println("=".repeat(30));

        Map<String, Integer> salesByRegion = Map.of("Norte", 4, "Sul", 9, "Sudeste", 15);
        salesByRegion.forEach((region, sales) ->
                System.out.printf("%-8s %s %d%n", region, "#".repeat(sales), sales));

        System.out.println();
        System.out.println("\"ab\".repeat(0) = \"" + "ab".repeat(0) + "\"");
        try {
            "ab".repeat(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("\"ab\".repeat(-1) -> IllegalArgumentException: " + e.getMessage());
        }
    }
}
