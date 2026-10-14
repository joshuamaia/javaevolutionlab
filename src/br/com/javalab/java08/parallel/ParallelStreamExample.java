package br.com.javalab.java08.parallel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Java 8 - Parallel Streams (JEP 107)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code parallelStream()} ou {@code .parallel()} executa o pipeline em
 * várias threads do {@code ForkJoinPool.commonPool()} (Fork/Join do Java 7).</p>
 * <p>Benefício: paralelizar processamento CPU-bound de grandes volumes trocando uma palavra.</p>
 * <p>QUANDO NÃO USAR: coleções pequenas; fontes difíceis de dividir ({@code LinkedList},
 * {@code Stream.iterate}); operações de I/O ou bloqueantes (ocupam o pool comum de toda a
 * aplicação); efeitos colaterais em estruturas não thread-safe; quando a ordem importa
 * ({@code forEachOrdered} anula o ganho); dentro de servidores já saturados de threads.
 * Sempre meça.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/parallel/ParallelStreamExample.java
 * </pre>
 */
public class ParallelStreamExample {

    public static void main(String[] args) {
        System.out.println("Processadores: " + Runtime.getRuntime().availableProcessors()
                + " | paralelismo do commonPool: " + ForkJoinPool.commonPool().getParallelism());

        System.out.println();
        System.out.println("=== Trabalho CPU-bound: contar primos até 3_000_000 ===");
        for (int round = 1; round <= 2; round++) { // 2 rodadas: a 1ª inclui aquecimento do JIT
            long t0 = System.nanoTime();
            long seq = LongStream.rangeClosed(2, 3_000_000).filter(ParallelStreamExample::isPrime).count();
            long t1 = System.nanoTime();
            long par = LongStream.rangeClosed(2, 3_000_000).parallel().filter(ParallelStreamExample::isPrime).count();
            long t2 = System.nanoTime();
            System.out.printf("Rodada %d: sequencial=%d (%d ms) | paralelo=%d (%d ms)%n",
                    round, seq, (t1 - t0) / 1_000_000, par, (t2 - t1) / 1_000_000);
        }

        System.out.println();
        System.out.println("=== Quais threads trabalham? ===");
        String threads = IntStream.range(0, 16).parallel()
                .mapToObj(i -> Thread.currentThread().getName())
                .distinct().sorted()
                .collect(Collectors.joining(", "));
        System.out.println(threads);

        System.out.println();
        System.out.println("=== ALERTA: efeito colateral em ArrayList não thread-safe ===");
        List<Integer> unsafe = new ArrayList<>();
        try {
            IntStream.range(0, 100_000).parallel().forEach(unsafe::add); // ERRADO
            System.out.println("ArrayList compartilhado: " + unsafe.size() + " elementos (esperado 100000)");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayList compartilhado lançou " + e.getClass().getSimpleName());
        }
        List<Integer> safe = IntStream.range(0, 100_000).parallel().boxed().collect(Collectors.toList());
        System.out.println("Com collect (correto): " + safe.size() + " elementos");
        List<Integer> synced = Collections.synchronizedList(new ArrayList<Integer>());
        IntStream.range(0, 100_000).parallel().forEach(synced::add);
        System.out.println("Lista sincronizada (correto, mas com contenção): " + synced.size());

        System.out.println();
        System.out.println("=== ALERTA: ordem não garantida com forEach ===");
        System.out.print("forEach:        ");
        IntStream.rangeClosed(1, 10).parallel().forEach(i -> System.out.print(i + " "));
        System.out.println();
        System.out.print("forEachOrdered: ");
        IntStream.rangeClosed(1, 10).parallel().forEachOrdered(i -> System.out.print(i + " "));
        System.out.println();
    }

    private static boolean isPrime(long n) {
        if (n < 2) {
            return false;
        }
        for (long d = 2; d * d <= n; d++) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }
}
