package br.com.javalab.java11.string;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 11 - String.isBlank
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code isBlank()} devolve {@code true} se a String está vazia ou contém
 * apenas espaços em branco (segundo {@code Character.isWhitespace}, inclusive tabs,
 * quebras de linha e espaços Unicode).</p>
 * <p>Benefício: validação de entrada sem {@code trim().isEmpty()} (que cria uma String nova
 * e não reconhece todos os espaços Unicode) nem bibliotecas como Apache Commons
 * ({@code StringUtils.isBlank}).</p>
 * <p>Antes: {@code s.trim().isEmpty()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/string/StringIsBlankExample.java
 * </pre>
 */
public class StringIsBlankExample {

    public static void main(String[] args) {
        List<String> customerNames = List.of("Ana", "", "   ", "\t\n", "\u2003", " Bruno ");

        System.out.println("=== isEmpty x trim().isEmpty() x isBlank ===");
        System.out.printf("%-12s %-8s %-15s %-8s%n", "valor", "isEmpty", "trim().isEmpty", "isBlank");
        for (String name : customerNames) {
            System.out.printf("%-12s %-8s %-15s %-8s%n",
                    visible(name), name.isEmpty(), name.trim().isEmpty(), name.isBlank());
        }
        System.out.println("(\\u2003 = EM SPACE: trim() não remove, isBlank() reconhece)");

        System.out.println();
        System.out.println("=== Uso realista: descartar nomes em branco ===");
        List<String> valid = customerNames.stream()
                .filter(name -> !name.isBlank())
                .map(String::strip)
                .collect(Collectors.toList());
        System.out.println("Clientes válidos: " + valid);
    }

    private static String visible(String value) {
        return "\"" + value.replace("\t", "\\t").replace("\n", "\\n").replace("\u2003", "\\u2003") + "\"";
    }
}
