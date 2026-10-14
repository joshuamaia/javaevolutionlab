package br.com.javalab.java11.collections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Java 11 - Collection.toArray(IntFunction)
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: novo método default {@code toArray(IntFunction<T[]> generator)}, que recebe
 * um construtor de array: {@code lista.toArray(String[]::new)}.</p>
 * <p>Benefício: forma idiomática e simétrica com {@code Stream.toArray(String[]::new)};
 * dispensa criar um array "modelo".</p>
 * <p>Antes: {@code lista.toArray(new String[0])} ou
 * {@code lista.toArray(new String[lista.size()])}; {@code toArray()} sem argumentos devolve
 * {@code Object[]} (cast para {@code String[]} lança {@code ClassCastException}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/collections/CollectionToArrayExample.java
 * </pre>
 */
public class CollectionToArrayExample {

    public static void main(String[] args) {
        List<String> skus = List.of("TECLADO", "MOUSE", "MONITOR");

        System.out.println("=== toArray() sem argumentos: Object[] ===");
        Object[] objects = skus.toArray();
        System.out.println(objects.getClass().getSimpleName() + " " + Arrays.toString(objects));

        System.out.println();
        System.out.println("=== Antes: toArray(new String[0]) ===");
        String[] old = skus.toArray(new String[0]);
        System.out.println(old.getClass().getSimpleName() + " " + Arrays.toString(old));

        System.out.println();
        System.out.println("=== Java 11: toArray(String[]::new) ===");
        String[] array = skus.toArray(String[]::new);
        System.out.println(array.getClass().getSimpleName() + " " + Arrays.toString(array));

        Integer[] quantities = Set.of(3, 1, 2).toArray(Integer[]::new);
        Arrays.sort(quantities);
        System.out.println("Quantidades ordenadas: " + Arrays.toString(quantities));

        System.out.println();
        System.out.println("=== Simetria com Stream.toArray (Java 8) ===");
        String[] lower = skus.stream().map(String::toLowerCase).toArray(String[]::new);
        System.out.println(Arrays.toString(lower));
    }
}
