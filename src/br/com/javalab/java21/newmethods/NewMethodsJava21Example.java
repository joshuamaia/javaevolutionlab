package br.com.javalab.java21.newmethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java 21 - Novos métodos em String, StringBuilder, Character, Math e List (sem JEP)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: pequenas adições de API do Java 21:</p>
 * <ul>
 *   <li>{@code StringBuilder.repeat(CharSequence, int)} e {@code repeat(int codePoint, int)};</li>
 *   <li>{@code Character.isEmoji(int)} (e {@code isEmojiPresentation}, {@code isExtendedPictographic}...);</li>
 *   <li>{@code Math.clamp(valor, min, max)} para int/long/float/double;</li>
 *   <li>{@code String.indexOf(ch, beginIndex, endIndex)} - busca limitada a um intervalo;</li>
 *   <li>{@code String.splitWithDelimiters(regex, limit)} - split que mantém os delimitadores;</li>
 *   <li>{@code List.reversed()} (de Sequenced Collections, JEP 431).</li>
 * </ul>
 * <p>Benefício: menos utilitários caseiros e menos erros de índice.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/newmethods/NewMethodsJava21Example.java
 * </pre>
 */
public class NewMethodsJava21Example {

    public static void main(String[] args) {
        System.out.println("=== StringBuilder.repeat ===");
        StringBuilder table = new StringBuilder();
        table.append('+').repeat("-", 20).append('+').append('\n');
        table.append('|').append(String.format("%-20s", " Relatório")).append('|').append('\n');
        table.append('+').repeat('=', 20).append('+'); // repeat(int codePoint, int count)
        System.out.println(table);
        System.out.println("Avaliação: " + new StringBuilder().repeat(0x2B50, 4)); // estrela ⭐ como code point

        System.out.println();
        System.out.println("=== Character.isEmoji ===");
        String review = "Ótimo produto 👍 entrega rápida 🚚";
        long emojis = review.codePoints().filter(Character::isEmoji).count();
        System.out.println("Emojis na avaliação: " + emojis + " | 'A' é emoji? " + Character.isEmoji('A'));
        // Cuidado: pelo Unicode, os dígitos 0-9, '#' e '*' também têm a propriedade Emoji.
        System.out.println("isEmoji('7') = " + Character.isEmoji('7'));
        System.out.println("Sem emojis: " + review.codePoints()
                .filter(cp -> !Character.isEmoji(cp) || Character.isDigit(cp))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString().strip());

        System.out.println();
        System.out.println("=== Math.clamp ===");
        int requestedDiscount = 75;
        System.out.println("Antes : Math.min(Math.max(75, 0), 50) = " + Math.min(Math.max(requestedDiscount, 0), 50));
        System.out.println("Agora : Math.clamp(75, 0, 50)        = " + Math.clamp(requestedDiscount, 0, 50));
        System.out.println("long -> int: Math.clamp(10_000_000_000L, 0, 1000) = " + Math.clamp(10_000_000_000L, 0, 1000));
        System.out.println("double: Math.clamp(-0.3, 0.0, 1.0) = " + Math.clamp(-0.3, 0.0, 1.0));

        System.out.println();
        System.out.println("=== String.indexOf(ch, begin, end) ===");
        String logLine = "2023-09-19 INFO pedido=PED-21 status=PAGO";
        int afterDate = logLine.indexOf(' ') + 1;
        System.out.println("'=' entre os índices 0 e 10 (só a data): " + logLine.indexOf('=', 0, 10));
        System.out.println("'=' a partir de " + afterDate + " até o fim: " + logLine.indexOf('=', afterDate, logLine.length()));

        System.out.println();
        System.out.println("=== String.splitWithDelimiters ===");
        String expression = "10+20-5+3";
        System.out.println("split              : " + Arrays.toString(expression.split("[+-]")));
        System.out.println("splitWithDelimiters: " + Arrays.toString(expression.splitWithDelimiters("[+-]", 0)));

        System.out.println();
        System.out.println("=== List.reversed ===");
        List<String> history = new ArrayList<>(List.of("criado", "pago", "enviado", "entregue"));
        System.out.println("Linha do tempo  : " + history);
        System.out.println("Mais recente 1o : " + history.reversed());
    }
}
