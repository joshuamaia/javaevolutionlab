package br.com.javalab.java21.generationalzgc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Java 21 - Generational ZGC (JEP 439)
 *
 * <p>Status: Produto (JVM). Histórico: ZGC experimental no Java 11 (JEP 333), produto no 15 (JEP 377);
 * modo geracional opcional no 21 (JEP 439); padrão do ZGC no 23 (JEP 474); modo não geracional
 * removido no 24 (JEP 490).</p>
 *
 * <p>O que é: o ZGC (coletor de pausas sub-milissegundo) passou a separar objetos jovens e velhos,
 * coletando a geração jovem com mais frequência - a maioria dos objetos morre cedo.</p>
 * <p>Benefício: menos CPU e memória para o mesmo tamanho de heap, menor risco de "allocation stalls",
 * mantendo as pausas curtas.</p>
 * <p>Antes (ZGC 11-20): uma única geração; todo ciclo marcava o heap inteiro.</p>
 *
 * <p>Não há API para "demonstrar" um GC; este é um exemplo de observação: imprime o GC em uso pelos
 * MXBeans e relança {@code java -XX:+UseZGC -Xlog:gc -version} para mostrar a mensagem da JVM.</p>
 * <p>Flags:</p>
 * <pre>
 * Java 21-22: java -XX:+UseZGC -XX:+ZGenerational -Xlog:gc ...   (sem ZGenerational: ZGC antigo)
 * Java 23   : java -XX:+UseZGC ...                                (geracional é o padrão)
 * Java 24+  : java -XX:+UseZGC ...                                (ZGenerational obsoleta: é ignorada com aviso)
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/generationalzgc/GenerationalZgcObservationExample.java
 * java -XX:+UseZGC src/br/com/javalab/java21/generationalzgc/GenerationalZgcObservationExample.java
 * </pre>
 */
public class GenerationalZgcObservationExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== GC desta JVM (" + System.getProperty("java.version") + ") ===");
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("- " + gc.getName() + " | pools: " + String.join(", ", gc.getMemoryPoolNames()));
        }
        System.out.println("(Com ZGC geracional aparecem 'ZGC Minor/Major Cycles/Pauses' e pools 'ZGC Young/Old Generation'.)");

        System.out.println();
        System.out.println("=== Gerando lixo de vida curta (cenário em que a geração jovem ajuda) ===");
        long start = System.nanoTime();
        long checksum = 0;
        for (int i = 0; i < 2_000_000; i++) {
            byte[] temporaryBuffer = new byte[256];
            temporaryBuffer[0] = (byte) i;
            checksum += temporaryBuffer[0];
        }
        System.out.printf("2 milhões de buffers temporários em %d ms (checksum %d)%n",
                (System.nanoTime() - start) / 1_000_000, checksum);
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("- " + gc.getName() + ": " + gc.getCollectionCount() + " coletas, "
                    + gc.getCollectionTime() + " ms");
        }

        System.out.println();
        System.out.println("=== Processo filho: java -XX:+UseZGC -Xlog:gc -version ===");
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process child = new ProcessBuilder(List.of(java, "-XX:+UseZGC", "-Xlog:gc", "-version"))
                .redirectErrorStream(true).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()))) {
            reader.lines().filter(line -> line.contains("gc") || line.contains("version"))
                    .forEach(line -> System.out.println("  " + line));
        }
        if (!child.waitFor(20, TimeUnit.SECONDS)) {
            child.destroyForcibly();
        }
    }
}
