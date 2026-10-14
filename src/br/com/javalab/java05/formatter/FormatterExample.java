package br.com.javalab.java05.formatter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Java 5 - java.util.Formatter, printf e String.format
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: formatação no estilo {@code printf} do C: {@code %s}, {@code %d},
 * {@code %f}, {@code %x}, {@code %n}, {@code %t...} (datas), com largura, precisão,
 * alinhamento, flags ({@code ,} {@code 0} {@code -} {@code +}) e {@code Locale}.
 * Disponível em {@code java.util.Formatter}, {@code String.format},
 * {@code PrintStream.printf/format}. Depende de varargs, também do Java 5.</p>
 * <p>Benefício: relatórios e mensagens legíveis numa única linha de código.</p>
 * <p>Antes: concatenação + {@code DecimalFormat}/{@code SimpleDateFormat}/
 * {@code MessageFormat} e alinhamento manual com espaços.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/formatter/FormatterExample.java
 * </pre>
 */
public class FormatterExample {

    public static void main(String[] args) {
        String[] products = {"Caneta", "Caderno universitário", "Mochila"};
        int[] quantities = {10, 2, 1};
        double[] prices = {2.5, 27.9, 1349.0};
        Locale ptBR = new Locale("pt", "BR"); // construtor da época (deprecated no Java 19)

        System.out.println("=== Abordagem antiga: concatenação + DecimalFormat ===");
        DecimalFormat money = new DecimalFormat("#,##0.00");
        for (int i = 0; i < products.length; i++) {
            System.out.println(products[i] + " x" + quantities[i] + " = " + money.format(prices[i] * quantities[i]));
        }

        System.out.println();
        System.out.println("=== Abordagem nova: printf com largura, alinhamento e Locale ===");
        System.out.printf("%-25s %5s %12s%n", "Produto", "Qtd", "Subtotal");
        double total = 0;
        for (int i = 0; i < products.length; i++) {
            double subtotal = prices[i] * quantities[i];
            total += subtotal;
            System.out.printf(ptBR, "%-25s %5d %,12.2f%n", products[i], quantities[i], subtotal);
        }
        System.out.printf(ptBR, "%-25s %5s %,12.2f%n", "TOTAL", "", total);

        System.out.println();
        System.out.println("=== String.format: outras conversões ===");
        System.out.println(String.format("Pedido nº %06d", 42));             // zeros à esquerda
        System.out.println(String.format("Hexa: %x / %X / octal: %o", 255, 255, 8));
        System.out.println(String.format(Locale.US, "US: %,.2f | %+.1f%%", 1234567.891, 12.5));
        System.out.println(String.format(ptBR, "BR: %,.2f", 1234567.891));
        System.out.println(String.format("Booleano: %b, caractere: %c, nulo: %s", true, 'A', null));
        System.out.println(String.format("Reuso de argumento: %1$s-%1$s-%2$s", "AB", "C"));

        Calendar date = new GregorianCalendar(2004, Calendar.SEPTEMBER, 30, 14, 5); // lançamento do Java 5
        System.out.println(String.format(ptBR, "Data: %1$td/%1$tm/%1$tY %1$tH:%1$tM (%1$tA)", date));

        System.out.println();
        System.out.println("=== java.util.Formatter escrevendo em um StringBuilder ===");
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format("%s;%d;%.2f%n", "Caneta", 10, 25.0);
        formatter.format("%s;%d;%.2f%n", "Mochila", 1, 1349.0);
        formatter.close(); // Formatter implementa Closeable (try-with-resources só no Java 7)
        System.out.print("CSV gerado:\n" + sb);
    }
}
