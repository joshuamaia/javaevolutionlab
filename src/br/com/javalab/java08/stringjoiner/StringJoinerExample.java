package br.com.javalab.java08.stringjoiner;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Java 8 - StringJoiner e String.join
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@link StringJoiner} monta textos com separador, prefixo, sufixo e valor
 * para o caso vazio; {@code String.join} é o atalho estático; {@code Collectors.joining}
 * usa StringJoiner internamente.</p>
 * <p>Benefício: acabou o "tirar a última vírgula" com StringBuilder.</p>
 * <p>Antes: StringBuilder + controle manual de separador.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/stringjoiner/StringJoinerExample.java
 * </pre>
 */
public class StringJoinerExample {

    public static void main(String[] args) {
        List<String> columns = Arrays.asList("id", "customer", "total", "status");

        System.out.println("=== Abordagem antiga: StringBuilder ===");
        StringBuilder sb = new StringBuilder("SELECT ");
        for (int i = 0; i < columns.size(); i++) {
            sb.append(columns.get(i));
            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" FROM orders");
        System.out.println(sb);

        System.out.println();
        System.out.println("=== StringJoiner com prefixo e sufixo ===");
        StringJoiner select = new StringJoiner(", ", "SELECT ", " FROM orders");
        for (String c : columns) {
            select.add(c);
        }
        System.out.println(select);

        System.out.println();
        System.out.println("=== setEmptyValue e merge ===");
        StringJoiner tags = new StringJoiner(", ", "[", "]");
        tags.setEmptyValue("(sem tags)");
        System.out.println("Vazio: " + tags);
        tags.add("promo").add("frete-gratis");
        StringJoiner extra = new StringJoiner(", ");
        extra.add("black-friday");
        tags.merge(extra);
        System.out.println("Com merge: " + tags + " (length=" + tags.length() + ")");

        System.out.println();
        System.out.println("=== String.join ===");
        System.out.println(String.join(" | ", "SP", "RJ", "MG"));
        System.out.println(String.join("/", columns));

        System.out.println();
        System.out.println("=== Collectors.joining (usa StringJoiner) ===");
        System.out.println(columns.stream().map(String::toUpperCase).collect(Collectors.joining(";", "<", ">")));
    }
}
