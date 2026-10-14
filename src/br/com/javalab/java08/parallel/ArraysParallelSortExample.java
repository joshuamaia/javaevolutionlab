package br.com.javalab.java08.parallel;

import java.util.Arrays;
import java.util.Random;

/**
 * Java 8 - Arrays.parallelSort, parallelPrefix e parallelSetAll (JEP 103)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: ordenação paralela baseada em Fork/Join (merge-sort paralelo). Abaixo de
 * 8192 elementos (MIN_ARRAY_SORT_GRAN) ela simplesmente usa o {@code Arrays.sort}
 * sequencial. O Java 8 também trouxe {@code parallelPrefix} (soma acumulada) e
 * {@code parallelSetAll}/{@code setAll} (inicialização por índice).</p>
 * <p>Benefício: ordenar arrays grandes usando todos os núcleos sem código extra.</p>
 * <p>Antes: apenas {@code Arrays.sort} (single-thread).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/parallel/ArraysParallelSortExample.java
 * </pre>
 */
public class ArraysParallelSortExample {

    public static void main(String[] args) {
        System.out.println("=== Arrays.sort vs Arrays.parallelSort (10 milhões de longs) ===");
        Random random = new Random(7);
        long[] data = new long[10_000_000];
        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextLong();
        }

        for (int round = 1; round <= 2; round++) {
            long[] a = data.clone();
            long[] b = data.clone();
            long t0 = System.nanoTime();
            Arrays.sort(a);
            long t1 = System.nanoTime();
            Arrays.parallelSort(b);
            long t2 = System.nanoTime();
            System.out.printf("Rodada %d: sort=%d ms | parallelSort=%d ms | iguais=%b%n",
                    round, (t1 - t0) / 1_000_000, (t2 - t1) / 1_000_000, Arrays.equals(a, b));
        }

        System.out.println();
        System.out.println("=== parallelSort com intervalo e com objetos ===");
        int[] partial = {9, 8, 7, 6, 5, 4, 3};
        Arrays.parallelSort(partial, 2, 5); // ordena apenas os índices [2, 5)
        System.out.println("Ordenação parcial [2,5): " + Arrays.toString(partial));
        String[] customers = {"Davi", "ana", "Bruno", "carla"};
        Arrays.parallelSort(customers, String.CASE_INSENSITIVE_ORDER);
        System.out.println("Clientes: " + Arrays.toString(customers));

        System.out.println();
        System.out.println("=== parallelSetAll e parallelPrefix ===");
        int[] dailySales = new int[7];
        Arrays.parallelSetAll(dailySales, day -> (day + 1) * 10);
        System.out.println("Vendas por dia:   " + Arrays.toString(dailySales));
        Arrays.parallelPrefix(dailySales, Integer::sum);
        System.out.println("Vendas acumuladas: " + Arrays.toString(dailySales));
    }
}
