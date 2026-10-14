package br.com.javalab.java07.multicatch;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Java 7 - Multi-catch (Project Coin, JSR 334)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: um único bloco {@code catch} pode tratar vários tipos de exceção,
 * separados por {@code |}. O parâmetro é implicitamente {@code final}.</p>
 * <p>Benefício: elimina blocos catch duplicados com o mesmo tratamento.</p>
 * <p>Antes: um catch por tipo (código repetido) ou capturar {@code Exception} (genérico demais).</p>
 * <p>Restrição: os tipos não podem ter relação de herança entre si.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/multicatch/MultiCatchExample.java
 * </pre>
 */
public class MultiCatchExample {

    public static void main(String[] args) {
        String[] inputs = {"2011-07-28;10", "data-invalida;5", "2011-07-28;abc", "IO"};

        System.out.println("=== Abordagem antiga: um catch por tipo ===");
        for (String input : inputs) {
            try {
                parseOrder(input);
            } catch (ParseException e) {
                log(e);
            } catch (NumberFormatException e) {
                log(e); // mesmo tratamento repetido
            } catch (IOException e) {
                log(e); // e de novo...
            }
        }

        System.out.println();
        System.out.println("=== Java 7: multi-catch ===");
        for (String input : inputs) {
            try {
                parseOrder(input);
            } catch (ParseException | NumberFormatException | IOException e) {
                // e = null; // não compila: o parâmetro de multi-catch é implicitamente final
                log(e);
            }
        }
    }

    private static void parseOrder(String line) throws ParseException, IOException {
        if ("IO".equals(line)) {
            throw new IOException("Falha ao ler arquivo de pedidos");
        }
        String[] parts = line.split(";");
        new SimpleDateFormat("yyyy-MM-dd").parse(parts[0]);
        int quantity = Integer.parseInt(parts[1]);
        System.out.println("Pedido OK: data=" + parts[0] + ", quantidade=" + quantity);
    }

    private static void log(Exception e) {
        System.out.println("Erro tratado (" + e.getClass().getSimpleName() + "): " + e.getMessage());
    }
}
