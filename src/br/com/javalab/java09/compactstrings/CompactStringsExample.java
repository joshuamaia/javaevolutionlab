package br.com.javalab.java09.compactstrings;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Java 9 - Compact Strings (JEP 254)
 *
 * <p>Status: Definitivo (otimização interna da JVM, sem mudança de API).</p>
 *
 * <p>O que é: {@code String} deixou de usar {@code char[]} (2 bytes por caractere) e passou
 * a usar {@code byte[]} + um "coder": textos que cabem em Latin-1 ocupam <b>1 byte por
 * caractere</b>; os demais continuam em UTF-16 (2 bytes).</p>
 * <p>Benefício: menos memória e menos GC — Strings costumam ser a maior parte do heap.</p>
 * <p>Antes: todo caractere ocupava 2 bytes.</p>
 *
 * <p>Este é um exemplo de observação: mede (aproximadamente) o heap usado por muitas
 * Strings Latin-1 versus Strings com um caractere fora do Latin-1.</p>
 *
 * <p>Flags para experimentar:</p>
 * <pre>
 * java -XX:-CompactStrings src/br/com/javalab/java09/compactstrings/CompactStringsExample.java
 *   (desliga a otimização: as duas medições ficam parecidas)
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/compactstrings/CompactStringsExample.java
 * </pre>
 */
public class CompactStringsExample {

    private static final int COUNT = 200_000;
    private static final int LENGTH = 100;

    public static void main(String[] args) {
        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println("Argumentos da JVM: " + jvmArgs);
        System.out.println("CompactStrings desligado? " + jvmArgs.contains("-XX:-CompactStrings"));

        System.out.println();
        System.out.println("=== Medindo heap (aproximado) para " + COUNT + " Strings de " + LENGTH + " caracteres ===");
        long latin1 = measure('a');   // cabe em Latin-1 -> 1 byte/char
        long utf16 = measure('\u20AC'); // símbolo do euro: fora do Latin-1 -> 2 bytes/char

        System.out.printf("Latin-1 (\"aaaa...\")      : ~%d MB%n", latin1 / (1024 * 1024));
        System.out.printf("UTF-16  (\"aaa...\\u20AC\") : ~%d MB%n", utf16 / (1024 * 1024));
        System.out.printf("Razão UTF-16 / Latin-1   : %.2f%n", (double) utf16 / latin1);
        System.out.println("Com Compact Strings a razão fica perto de 1,7 (cabeçalhos incluídos); com -XX:-CompactStrings, perto de 1,0.");
    }

    private static long measure(char lastChar) {
        String[] holder = new String[COUNT];
        long before = usedMemory();
        for (int i = 0; i < COUNT; i++) {
            char[] chars = new char[LENGTH];
            java.util.Arrays.fill(chars, 'a');
            chars[LENGTH - 1] = lastChar;
            holder[i] = new String(chars);
        }
        long after = usedMemory();
        System.out.println("  último caractere '" + lastChar + "' -> " + holder[0].length() + " chars por String");
        return after - before;
    }

    private static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        for (int i = 0; i < 3; i++) {
            System.gc();
        }
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
