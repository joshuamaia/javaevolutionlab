package br.com.javalab.java08.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Java 8 - Expressões Lambda (JEP 126 / JSR 335)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: funções anônimas concisas {@code (parâmetros) -> corpo} que implementam
 * uma interface funcional (interface com um único método abstrato).</p>
 * <p>Benefício: passar comportamento como argumento sem a cerimônia de classes anônimas;
 * base das Streams, Optional, CompletableFuture etc.</p>
 * <p>Antes: classes anônimas (ver {@code AnonymousClassVsLambdaComparison}).</p>
 * <p>Regras: variáveis locais capturadas devem ser final ou "effectively final";
 * {@code this} dentro do lambda refere-se à classe envolvente.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/lambda/LambdaBasicExample.java
 * </pre>
 */
public class LambdaBasicExample {

    public static void main(String[] args) {
        System.out.println("=== Formas de sintaxe ===");
        Runnable noArgs = () -> System.out.println("Sem parâmetros");
        noArgs.run();

        Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());
        System.out.println("Tipos inferidos: compare(\"abc\", \"z\") = " + byLength.compare("abc", "z"));

        Comparator<String> explicit = (String a, String b) -> a.compareToIgnoreCase(b);
        System.out.println("Tipos explícitos: compare(\"a\", \"B\") = " + explicit.compare("a", "B"));

        Comparator<Double> block = (a, b) -> {
            double diff = a - b;
            return diff > 0 ? 1 : diff < 0 ? -1 : 0;
        };
        System.out.println("Corpo em bloco: compare(2.5, 1.0) = " + block.compare(2.5, 1.0));

        System.out.println();
        System.out.println("=== Lambdas com coleções ===");
        List<String> products = new ArrayList<>(Arrays.asList("Teclado", "Mouse", "Monitor", "Cabo HDMI"));
        products.sort((a, b) -> a.compareTo(b));
        System.out.println("Ordenados: " + products);
        products.forEach(p -> System.out.println("  produto: " + p));

        System.out.println();
        System.out.println("=== Captura de variáveis (effectively final) ===");
        String prefix = "SKU-"; // não é declarada final, mas nunca é reatribuída
        products.forEach(p -> System.out.println(prefix + p.toUpperCase().replace(' ', '_')));
        // prefix = "X"; // descomentar quebra a compilação: lambda exige effectively final

        System.out.println();
        System.out.println("=== Lambda guardado em variável e reutilizado ===");
        Runnable greeter = () -> System.out.println("Olá de " + Thread.currentThread().getName());
        greeter.run();
        Thread worker = new Thread(greeter, "worker-1");
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
