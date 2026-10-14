package br.com.javalab.java09.jshell;

import java.util.List;

import jdk.jshell.Diag;
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import jdk.jshell.VarSnippet;

/**
 * Java 9 - JShell: o REPL do Java (JEP 222)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: uma ferramenta interativa (Read-Eval-Print Loop) para testar trechos de
 * código Java sem criar classe nem método {@code main}. Além do comando {@code jshell},
 * existe a API {@code jdk.jshell}, usada por IDEs e por este exemplo.</p>
 * <p>Benefício: aprendizado e prototipação rápidos — experimentar uma API em segundos.</p>
 * <p>Antes: criar uma classe, compilar com {@code javac} e executar com {@code java}
 * só para testar uma linha.</p>
 *
 * <p>Uso do comando (interativo):</p>
 * <pre>
 * $ jshell
 * jshell&gt; int total = 3 * 40
 * total ==&gt; 120
 * jshell&gt; List.of("a", "b").size()
 * $2 ==&gt; 2
 * jshell&gt; /vars        (lista variáveis)
 * jshell&gt; /methods     (lista métodos)
 * jshell&gt; /imports     (imports padrão)
 * jshell&gt; /exit
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/jshell/JShellApiExample.java
 * </pre>
 */
public class JShellApiExample {

    public static void main(String[] args) {
        System.out.println("=== Avaliando snippets com a API jdk.jshell ===");
        try (JShell shell = JShell.create()) {
            evaluate(shell, "double price = 49.90;");
            evaluate(shell, "int quantity = 3;");
            evaluate(shell, "double total(double p, int q) { return p * q; }");
            evaluate(shell, "total(price, quantity)");
            evaluate(shell, "String.join(\", \", java.util.List.of(\"pedido-1\", \"pedido-2\"))");

            System.out.println();
            System.out.println("=== Snippet com erro de compilação ===");
            evaluate(shell, "int broken = \"texto\";");

            System.out.println();
            System.out.println("=== Snippet que lança exceção ===");
            evaluate(shell, "Integer.parseInt(\"abc\")");

            System.out.println();
            System.out.println("=== Variáveis definidas na sessão (equivalente a /vars) ===");
            shell.variables().forEach(var ->
                    System.out.println(var.typeName() + " " + var.name() + " = " + shell.varValue(var)));
        }
    }

    private static void evaluate(JShell shell, String code) {
        System.out.println("jshell> " + code);
        List<SnippetEvent> events = shell.eval(code);
        for (SnippetEvent event : events) {
            if (event.exception() != null) {
                System.out.println("  Exceção: " + event.exception().getMessage());
            } else if (event.status() == Snippet.Status.REJECTED) {
                shell.diagnostics(event.snippet()).forEach((Diag diag) ->
                        System.out.println("  Erro: " + diag.getMessage(null).split("\\R")[0]));
            } else if (event.value() != null) {
                String label = event.snippet() instanceof VarSnippet
                        ? ((VarSnippet) event.snippet()).name()
                        : event.snippet().kind().toString();
                System.out.println("  " + label + " ==> " + event.value());
            } else {
                System.out.println("  " + event.snippet().kind() + " definido");
            }
        }
    }
}
