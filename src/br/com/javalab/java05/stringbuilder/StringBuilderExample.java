package br.com.javalab.java05.stringbuilder;

/**
 * Java 5 - StringBuilder
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: sequência de caracteres mutável com a mesma API do {@code StringBuffer},
 * porém SEM sincronização (não é thread-safe). Ambos implementam
 * {@code java.lang.Appendable} (também do Java 5).</p>
 * <p>Benefício: concatenação em laços sem criar uma {@code String} nova a cada passo e
 * sem o custo dos locks do {@code StringBuffer}. A partir do Java 5 o próprio javac
 * passou a traduzir {@code a + b} para {@code StringBuilder} (no Java 9+ usa
 * {@code invokedynamic}, JEP 280).</p>
 * <p>Antes: {@code String +=} em laço (O(n²) em cópias) ou {@code StringBuffer}
 * (sincronizado mesmo quando usado por uma só thread).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/stringbuilder/StringBuilderExample.java
 * </pre>
 */
public class StringBuilderExample {

    static final int LINES = 20000;

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga 1: String += em laço ===");
        long t0 = System.nanoTime();
        String csv = "";
        for (int i = 0; i < LINES; i++) {
            csv += "PED-" + i + ";"; // cria uma String nova (e copia tudo) a cada iteração
        }
        long t1 = System.nanoTime();
        System.out.println("Tamanho: " + csv.length() + " em " + (t1 - t0) / 1000000 + " ms");

        System.out.println();
        System.out.println("=== Abordagem antiga 2: StringBuffer (sincronizado) ===");
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < LINES; i++) {
            buffer.append("PED-").append(i).append(';');
        }
        long t2 = System.nanoTime();
        System.out.println("Tamanho: " + buffer.length() + " em " + (t2 - t1) / 1000000 + " ms");

        System.out.println();
        System.out.println("=== Abordagem nova: StringBuilder ===");
        StringBuilder builder = new StringBuilder(LINES * 10); // capacidade inicial evita realocações
        for (int i = 0; i < LINES; i++) {
            builder.append("PED-").append(i).append(';');
        }
        long t3 = System.nanoTime();
        System.out.println("Tamanho: " + builder.length() + " em " + (t3 - t2) / 1000000 + " ms");
        System.out.println("(tempos ilustrativos; String += é ordens de grandeza mais lento)");

        System.out.println();
        System.out.println("=== Outras operações do StringBuilder ===");
        StringBuilder receipt = new StringBuilder("Itens: caneta, caderno, ");
        receipt.setLength(receipt.length() - 2);          // remove ", " final
        receipt.insert(0, "[RECIBO] ");
        receipt.append(" | total=").append(12.5);
        System.out.println(receipt);
        int pos = receipt.indexOf("caderno");
        receipt.replace(pos, pos + "caderno".length(), "agenda");
        System.out.println(receipt);
        System.out.println("Invertido: " + new StringBuilder("ABC-123").reverse());
        receipt.deleteCharAt(0).delete(receipt.indexOf("]"), receipt.length());
        System.out.println("Após delete: " + receipt);
    }
}
