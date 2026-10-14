package br.com.javalab.java25.compactheaders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 25 - Compact Object Headers (JEP 519)
 *
 * <p>Status: Produto (JVM), desligado por padrão. Histórico: Experimental no Java 24
 * (JEP 450, exigia {@code -XX:+UnlockExperimentalVMOptions}). Origem: Projeto Lilliput.</p>
 *
 * <p>O que é: em HotSpot 64 bits todo objeto tem um cabeçalho de 12 bytes (mark word de 8 +
 * class pointer comprimido de 4). Com {@code -XX:+UseCompactObjectHeaders} o cabeçalho cai
 * para 8 bytes (class pointer embutido na mark word). Um objeto pequeno como
 * {@code record Point(int x, int y)} passa de 24 bytes (12 + 8, alinhado a 8) para 16 bytes.</p>
 * <p>Benefício: menos memória no heap (tipicamente 10-20% em apps com muitos objetos
 * pequenos), melhor uso de cache e menos trabalho de GC - sem mudar uma linha de código.</p>
 *
 * <p>Exemplo de observação: o programa relança a si mesmo duas vezes - com
 * {@code -XX:-UseCompactObjectHeaders} e com {@code -XX:+UseCompactObjectHeaders} - e em cada
 * processo filho cria 3 milhões de objetos pequenos, força um GC e mede o heap usado. Se o
 * relançamento falhar, mede só a JVM atual.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/compactheaders/CompactObjectHeadersObservation.java
 * # manualmente:
 * java -XX:+UseCompactObjectHeaders -Xlog:gc src/br/com/javalab/java25/compactheaders/CompactObjectHeadersObservation.java child
 * # no JDK 24 (experimental):
 * java -XX:+UnlockExperimentalVMOptions -XX:+UseCompactObjectHeaders ...
 * </pre>
 */
public class CompactObjectHeadersObservation {

    record Point(int x, int y) {}

    static final int COUNT = 3_000_000;

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("child")) {
            measure();
            return;
        }
        System.out.println("=== Esta JVM ===");
        System.out.println("UseCompactObjectHeaders = " + currentFlag());

        System.out.println();
        System.out.println("=== Relançando com cada configuração (" + COUNT + " objetos Point) ===");
        try {
            String off = runChild("-XX:-UseCompactObjectHeaders");
            String on = runChild("-XX:+UseCompactObjectHeaders");
            System.out.println("Cabeçalho padrão (12 bytes): " + off);
            System.out.println("Cabeçalho compacto (8 bytes): " + on);
        } catch (Exception e) {
            System.out.println("Não foi possível relançar (" + e.getMessage() + "); medindo só esta JVM:");
            measure();
        }
    }

    static void measure() {
        Runtime rt = Runtime.getRuntime();
        System.gc();
        long before = rt.totalMemory() - rt.freeMemory();
        Point[] points = new Point[COUNT];
        for (int i = 0; i < COUNT; i++) {
            points[i] = new Point(i, -i);
        }
        System.gc();
        long after = rt.totalMemory() - rt.freeMemory();
        double bytesPerObject = (after - before - (double) COUNT * 4) / COUNT; // desconta o array de refs
        System.out.printf("heap +%d MB, ~%.1f bytes por Point (flag=%s, %d)%n",
                (after - before) / (1024 * 1024), bytesPerObject, currentFlag(), points.length);
    }

    static String runChild(String flag) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(ProcessHandle.current().info().command().orElse("java"));
        cmd.addAll(List.of(flag, "-Xms512m", "-Xmx512m", "-XX:+UseG1GC"));
        String sourceFile = System.getProperty("jdk.launcher.sourcefile"); // modo código-fonte
        if (sourceFile != null) {
            cmd.add(sourceFile);
        } else {
            cmd.addAll(List.of("-cp", System.getProperty("java.class.path"),
                    CompactObjectHeadersObservation.class.getName()));
        }
        cmd.add("child");
        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            output = String.join(" ", reader.lines().toList());
        }
        if (process.waitFor() != 0) throw new IllegalStateException("processo filho falhou: " + output);
        return output;
    }

    static String currentFlag() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(a -> a.contains("UseCompactObjectHeaders")).findFirst().orElse("padrão (desligado)");
    }
}
