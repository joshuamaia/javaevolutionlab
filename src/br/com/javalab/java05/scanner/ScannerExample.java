package br.com.javalab.java05.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Java 5 - java.util.Scanner
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: leitor de texto que quebra a entrada em tokens (por padrão, espaços em
 * branco ou um delimitador regex configurável) e converte para tipos primitivos:
 * {@code nextInt}, {@code nextDouble}, {@code nextLine}, com {@code hasNextXxx} para
 * validar antes de ler. Aceita {@code InputStream}, {@code File}, {@code Readable} ou
 * {@code String}.</p>
 * <p>Benefício: leitura de console/arquivos sem montar {@code BufferedReader} +
 * {@code StringTokenizer} + {@code Integer.parseInt}.</p>
 * <p>Obs.: este exemplo lê de uma {@code String} de propósito: {@code new Scanner(System.in)}
 * ficaria esperando input e o exemplo precisa terminar sozinho.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/scanner/ScannerExample.java
 * </pre>
 */
public class ScannerExample {

    static final String ORDERS_CSV =
            "PED-1;Ana;3;19.90\n"
            + "PED-2;Bruno;1;249.00\n"
            + "PED-3;Carla;dois;10.00\n"   // quantidade inválida
            + "PED-4;Davi;5;4.50\n";

    public static void main(String[] args) throws IOException {
        System.out.println("=== Abordagem antiga: BufferedReader + StringTokenizer + parse ===");
        BufferedReader reader = new BufferedReader(new StringReader(ORDERS_CSV));
        String line;
        while ((line = reader.readLine()) != null) {
            StringTokenizer tokens = new StringTokenizer(line, ";");
            String id = tokens.nextToken();
            tokens.nextToken();
            try {
                int qty = Integer.parseInt(tokens.nextToken());
                double price = Double.parseDouble(tokens.nextToken());
                System.out.println(id + " -> " + qty * price);
            } catch (NumberFormatException e) {
                System.out.println(id + " -> linha inválida (" + e.getMessage() + ")");
            }
        }
        reader.close();

        System.out.println();
        System.out.println("=== Abordagem nova: Scanner com delimitador e hasNextInt ===");
        Scanner lines = new Scanner(ORDERS_CSV);
        while (lines.hasNextLine()) {
            Scanner fields = new Scanner(lines.nextLine());
            fields.useDelimiter(";");
            fields.useLocale(Locale.US); // ponto como separador decimal em nextDouble
            String id = fields.next();
            String customer = fields.next();
            if (fields.hasNextInt()) {   // valida sem lançar exceção
                int qty = fields.nextInt();
                double price = fields.nextDouble();
                System.out.printf("%s (%s): %d x %.2f = %.2f%n", id, customer, qty, price, qty * price);
            } else {
                System.out.println(id + " (" + customer + "): quantidade inválida '" + fields.next() + "'");
            }
            fields.close();
        }
        lines.close();

        System.out.println();
        System.out.println("=== Scanner com tokens mistos e findInLine (regex) ===");
        Scanner mixed = new Scanner("cliente Ana idade 31 saldo 1500,75 ativo true");
        // vírgula decimal; construtor da época (deprecated no Java 19, mas ainda funciona)
        mixed.useLocale(new Locale("pt", "BR"));
        int ints = 0;
        double decimals = 0;
        while (mixed.hasNext()) {
            if (mixed.hasNextInt()) {
                ints += mixed.nextInt();
            } else if (mixed.hasNextDouble()) {
                decimals += mixed.nextDouble();
            } else if (mixed.hasNextBoolean()) {
                System.out.println("booleano: " + mixed.nextBoolean());
            } else {
                mixed.next(); // palavra comum: descarta
            }
        }
        mixed.close();
        System.out.println("Soma dos inteiros: " + ints + "; soma dos decimais: " + decimals);

        Scanner log = new Scanner("2004-09-30 ERROR [pedido=PED-9] falha no pagamento");
        System.out.println("Pedido extraído com regex: " + log.findInLine("PED-\\d+"));
        log.close();
    }
}
