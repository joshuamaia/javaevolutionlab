package br.com.javalab.java06.console;

import java.io.Console;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Java 6 - java.io.Console
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: acesso ao terminal (console) associado à JVM via {@code System.console()}:
 * {@code printf/format}, {@code readLine(prompt)} e principalmente
 * {@code readPassword(prompt)}, que lê sem eco e devolve {@code char[]} (pode ser zerado
 * depois, ao contrário de uma {@code String} imutável).</p>
 * <p>Benefício: ler senhas com segurança e interagir com o usuário sem montar
 * {@code BufferedReader(new InputStreamReader(System.in))}.</p>
 * <p>Cuidados (por que este exemplo NUNCA bloqueia):</p>
 * <ul>
 *   <li>Até o JDK 21, {@code System.console()} retorna {@code null} quando a entrada ou a
 *   saída estão redirecionadas (IDE, pipeline, CI).</li>
 *   <li>No JDK 22+ (implementação baseada em JLine como padrão) pode retornar um Console
 *   mesmo sem terminal, dependendo da versão e da propriedade {@code jdk.console}; o
 *   método {@code Console.isTerminal()} (API do Java 22) diz se há terminal de verdade.
 *   Como este é um exemplo do Java 6, chamamos {@code isTerminal()} por REFLEXÃO quando
 *   ele existe; em JDKs antigos, console não nulo já significa terminal.</li>
 *   <li>Mesmo com terminal, só lemos input se o usuário pedir explicitamente com o
 *   argumento {@code --interativo}. Sem ele, o exemplo apenas mostra a saída formatada.</li>
 * </ul>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/console/ConsoleExample.java
 * java src/br/com/javalab/java06/console/ConsoleExample.java --interativo   (num terminal real)
 * </pre>
 */
public class ConsoleExample {

    /** true se há terminal interativo de verdade, em qualquer versão de JDK. */
    static boolean isInteractive(Console console) {
        if (console == null) {
            return false;
        }
        try {
            Method isTerminal = Console.class.getMethod("isTerminal"); // existe só no Java 22+
            return (Boolean) isTerminal.invoke(console);
        } catch (NoSuchMethodException e) {
            return true; // JDK < 22: se System.console() não é null, há terminal
        } catch (Exception e) {
            return false; // na dúvida, nunca bloquear
        }
    }

    public static void main(String[] args) {
        boolean wantsInput = Arrays.asList(args).contains("--interativo");
        Console console = System.console();
        boolean interactive = isInteractive(console);

        System.out.println("=== Detecção do console ===");
        System.out.println("System.console() = " + (console == null ? "null" : console.getClass().getName()));
        System.out.println("Terminal interativo? " + interactive + "; argumento --interativo? " + wantsInput);

        System.out.println();
        System.out.println("=== Abordagem antiga (antes do Java 6) ===");
        System.out.println("new BufferedReader(new InputStreamReader(System.in)).readLine()");
        System.out.println("-> senhas apareciam na tela (eco) e ficavam em uma String imutável");

        System.out.println();
        System.out.println("=== Saída formatada ===");
        if (console != null) {
            console.printf("%-10s %8s%n", "Produto", "Preço");
            console.format("%-10s %8.2f%n", "Caneta", 2.5);
            console.flush();
        } else {
            System.out.printf("%-10s %8s%n", "Produto", "Preço");
            System.out.printf("%-10s %8.2f%n", "Caneta", 2.5);
            System.out.println("(sem Console: usando System.out como alternativa)");
        }

        System.out.println();
        System.out.println("=== Leitura de dados ===");
        if (!interactive || !wantsInput) {
            System.out.println("Leitura pulada para não bloquear (IDE, pipeline ou sem --interativo).");
            System.out.println("Num terminal real, rode com --interativo para testar readLine/readPassword.");
            return;
        }
        String user = console.readLine("Usuário: ");
        char[] password = console.readPassword("Senha (sem eco): ");
        if (user == null || password == null) {
            System.out.println("Fim da entrada (EOF).");
            return;
        }
        console.printf("Olá, %s! Senha com %d caracteres recebida.%n", user, password.length);
        Arrays.fill(password, ' '); // apaga a senha da memória assim que possível
        console.printf("Senha apagada do array: [%s]%n", new String(password).trim());
    }
}
