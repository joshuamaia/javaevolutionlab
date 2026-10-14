package br.com.javalab.java07.forkjoin;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Java 7 - Fork/Join Framework: somando um array (JSR 166y)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@link ForkJoinPool} executa tarefas que se dividem recursivamente
 * ("dividir para conquistar"). {@link RecursiveTask} retorna um valor; as threads ociosas
 * "roubam" trabalho das filas das outras (work-stealing).</p>
 * <p>Benefício: paralelismo eficiente de problemas recursivos usando todos os núcleos.
 * É a base das parallel streams do Java 8.</p>
 * <p>Antes: dividir manualmente o trabalho em um ExecutorService e juntar Futures.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/forkjoin/ForkJoinSumExample.java
 * </pre>
 */
public class ForkJoinSumExample {

    /** Soma valores de vendas de um intervalo do array, dividindo enquanto for grande. */
    static class SumTask extends RecursiveTask<Long> {
        private static final long serialVersionUID = 1L;
        private static final int THRESHOLD = 10_000;
        private final int[] values;
        private final int start;
        private final int end;

        SumTask(int[] values, int start, int end) {
            this.values = values;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += values[i];
                }
                return sum;
            }
            int middle = (start + end) >>> 1;
            SumTask left = new SumTask(values, start, middle);
            SumTask right = new SumTask(values, middle, end);
            left.fork();                       // executa a metade esquerda de forma assíncrona
            long rightResult = right.compute(); // calcula a direita na thread atual
            long leftResult = left.join();      // espera a esquerda
            return leftResult + rightResult;
        }
    }

    public static void main(String[] args) {
        int[] sales = new int[5_000_000];
        Random random = new Random(42);
        for (int i = 0; i < sales.length; i++) {
            sales[i] = random.nextInt(100);
        }

        System.out.println("=== Soma sequencial ===");
        long t0 = System.nanoTime();
        long sequential = 0;
        for (int value : sales) {
            sequential += value;
        }
        long t1 = System.nanoTime();
        System.out.println("Resultado: " + sequential + " em " + (t1 - t0) / 1_000_000 + " ms");

        System.out.println();
        System.out.println("=== Soma com Fork/Join ===");
        ForkJoinPool pool = new ForkJoinPool(); // paralelismo = número de processadores
        try {
            long t2 = System.nanoTime();
            long parallel = pool.invoke(new SumTask(sales, 0, sales.length));
            long t3 = System.nanoTime();
            System.out.println("Resultado: " + parallel + " em " + (t3 - t2) / 1_000_000 + " ms");
            System.out.println("Paralelismo do pool: " + pool.getParallelism());
            System.out.println("Roubos de tarefa (steal count): " + pool.getStealCount());
            System.out.println("Resultados iguais? " + (parallel == sequential));
        } finally {
            pool.shutdown();
        }
        System.out.println("(tempos são ilustrativos; inclui aquecimento da JVM)");
    }
}
