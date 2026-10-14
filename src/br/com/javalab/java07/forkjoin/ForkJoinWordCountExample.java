package br.com.javalab.java07.forkjoin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Java 7 - Fork/Join Framework: contando palavras (JSR 166y)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: uma {@link RecursiveTask} que divide uma lista de linhas de log ao meio até
 * um limite, conta palavras em cada parte e combina os mapas parciais.</p>
 * <p>Benefício: mostra o padrão fork/join com resultado não numérico (merge de mapas)
 * e o uso de {@code invokeAll}.</p>
 * <p>Antes: threads manuais com estruturas compartilhadas sincronizadas.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/forkjoin/ForkJoinWordCountExample.java
 * </pre>
 */
public class ForkJoinWordCountExample {

    static class WordCountTask extends RecursiveTask<Map<String, Integer>> {
        private static final long serialVersionUID = 1L;
        private static final int THRESHOLD = 2;
        private final String[] lines;
        private final int start;
        private final int end;

        WordCountTask(String[] lines, int start, int end) {
            this.lines = lines;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (end - start <= THRESHOLD) {
                Map<String, Integer> counts = new HashMap<>();
                for (int i = start; i < end; i++) {
                    for (String word : lines[i].toLowerCase().split("\\W+")) {
                        if (!word.isEmpty()) {
                            Integer current = counts.get(word);
                            counts.put(word, current == null ? 1 : current + 1);
                        }
                    }
                }
                System.out.println("  " + Thread.currentThread().getName()
                        + " contou linhas [" + start + ", " + end + ")");
                return counts;
            }
            int middle = (start + end) / 2;
            WordCountTask left = new WordCountTask(lines, start, middle);
            WordCountTask right = new WordCountTask(lines, middle, end);
            invokeAll(left, right); // fork de ambas e espera
            return merge(left.join(), right.join());
        }

        private static Map<String, Integer> merge(Map<String, Integer> a, Map<String, Integer> b) {
            Map<String, Integer> result = new HashMap<>(a);
            for (Map.Entry<String, Integer> entry : b.entrySet()) {
                Integer current = result.get(entry.getKey());
                result.put(entry.getKey(), current == null ? entry.getValue() : current + entry.getValue());
            }
            return result;
        }
    }

    public static void main(String[] args) {
        String[] logLines = {
                "ERROR payment timeout",
                "INFO order created",
                "WARN payment retry",
                "ERROR payment declined",
                "INFO order shipped",
                "INFO order delivered",
                "ERROR stock unavailable",
                "INFO payment approved"
        };

        System.out.println("=== Contagem de palavras com Fork/Join ===");
        ForkJoinPool pool = new ForkJoinPool(4);
        try {
            Map<String, Integer> counts = pool.invoke(new WordCountTask(logLines, 0, logLines.length));
            System.out.println();
            System.out.println("payment: " + counts.get("payment"));
            System.out.println("order:   " + counts.get("order"));
            System.out.println("error:   " + counts.get("error"));
            System.out.println("info:    " + counts.get("info"));
            System.out.println("Total de palavras distintas: " + counts.size());
        } finally {
            pool.shutdown();
        }
    }
}
