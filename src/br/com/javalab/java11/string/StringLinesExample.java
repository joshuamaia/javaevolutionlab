package br.com.javalab.java11.string;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Java 11 - String.lines
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code lines()} devolve um {@code Stream<String>} com as linhas do texto,
 * reconhecendo {@code \n}, {@code \r} e {@code \r\n} (arquivos Windows, Unix e Mac antigo).</p>
 * <p>Benefício: processamento preguiçoso de texto multilinha com a API de streams, sem
 * regex nem linhas vazias "fantasmas" no final.</p>
 * <p>Antes: {@code texto.split("\\r?\\n")} (cria array inteiro, regex, e pode errar com
 * {@code \r} isolado) ou {@code BufferedReader(new StringReader(texto)).lines()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/string/StringLinesExample.java
 * </pre>
 */
public class StringLinesExample {

    public static void main(String[] args) {
        String log = "INFO pedido 1 criado\r\nERROR falha no pagamento do pedido 2\nINFO pedido 3 criado\rWARN estoque baixo\n";

        System.out.println("=== Antes: split com regex ===");
        String[] parts = log.split("\\r?\\n");
        System.out.println(parts.length + " partes (o \\r isolado não foi reconhecido):");
        for (String part : parts) {
            System.out.println("  [" + part.replace("\r", "\\r") + "]");
        }

        System.out.println();
        System.out.println("=== Java 11: lines() ===");
        System.out.println(log.lines().count() + " linhas:");
        log.lines().forEach(line -> System.out.println("  [" + line + "]"));

        System.out.println();
        System.out.println("=== Contando por nível de log ===");
        Map<String, Long> byLevel = log.lines()
                .map(line -> line.substring(0, line.indexOf(' ')))
                .collect(Collectors.groupingBy(level -> level, Collectors.counting()));
        System.out.println(byLevel);

        System.out.println();
        System.out.println("=== Somente erros ===");
        List<String> errors = log.lines().filter(line -> line.startsWith("ERROR")).collect(Collectors.toList());
        System.out.println(errors);
    }
}
