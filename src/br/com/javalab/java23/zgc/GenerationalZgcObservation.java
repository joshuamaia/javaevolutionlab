package br.com.javalab.java23.zgc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 23 - ZGC: Generational Mode by Default (JEP 474)
 *
 * <p>Status: Produto (JVM). Histórico: ZGC experimental no Java 11 (JEP 333), produto no 15
 * (JEP 377), modo generacional opcional no 21 (JEP 439, {@code -XX:+ZGenerational}); no 23
 * passa a ser o PADRÃO quando se usa {@code -XX:+UseZGC} e o modo não generacional fica
 * deprecated; no 24 o modo não generacional é removido (JEP 490).</p>
 *
 * <p>O que é: o ZGC (coletor de pausas curtíssimas, independentes do tamanho do heap) separa
 * objetos jovens e velhos e coleta os jovens com mais frequência - a "hipótese geracional":
 * a maioria dos objetos morre cedo.</p>
 * <p>Benefício: menos CPU e memória gastas pelo GC, sem perder as pausas sub-milissegundo.</p>
 *
 * <p>Exemplo de observação: o GC não é "programável"; este programa imprime os coletores
 * ativos (MXBeans) e, se não estiver rodando com ZGC, relança a si mesmo com
 * {@code -XX:+UseZGC} para mostrar os coletores "ZGC Minor/Major" do modo generacional.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java23/zgc/GenerationalZgcObservation.java
 * java -XX:+UseZGC -Xlog:gc src/br/com/javalab/java23/zgc/GenerationalZgcObservation.java
 * </pre>
 * <p>No JDK 21-23, {@code -XX:+UseZGC -XX:-ZGenerational} ainda selecionava o modo antigo
 * (no 23 com aviso de deprecated; no 24+ a opção é ignorada/obsoleta).</p>
 */
public class GenerationalZgcObservation {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Coletores desta JVM ===");
        allocateGarbage();
        boolean usingZgc = false;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("  " + gc.getName() + ": " + gc.getCollectionCount() + " coletas");
            usingZgc |= gc.getName().startsWith("ZGC");
        }

        if (!usingZgc && args.length == 0) {
            System.out.println();
            System.out.println("=== Relançando com -XX:+UseZGC ===");
            Process process = new ProcessBuilder(relaunchCommand("-XX:+UseZGC")).inheritIO().start();
            System.out.println("Processo filho terminou com código " + process.waitFor());
        }
    }

    /** Monta "java flag (arquivo-fonte | -cp classpath classe) child" para relançar este programa. */
    static List<String> relaunchCommand(String jvmFlag) {
        List<String> cmd = new ArrayList<>();
        cmd.add(ProcessHandle.current().info().command().orElse("java"));
        cmd.add(jvmFlag);
        String sourceFile = System.getProperty("jdk.launcher.sourcefile"); // definida no modo código-fonte
        if (sourceFile != null) {
            cmd.add(sourceFile);
        } else {
            cmd.addAll(List.of("-cp", System.getProperty("java.class.path"),
                    GenerationalZgcObservation.class.getName()));
        }
        cmd.add("child");
        return cmd;
    }

    /** Gera lixo de vida curta - o cenário que o modo generacional trata melhor. */
    static void allocateGarbage() {
        byte[][] recent = new byte[1024][]; // mantém só os últimos 1024 vivos; o resto vira lixo
        long checksum = 0;
        for (int i = 0; i < 4_000_000; i++) {
            recent[i & 1023] = new byte[256];
            checksum += recent[i & 1023].length;
        }
        System.out.println("  (alocados ~" + checksum / 1_000_000 + " MB de objetos temporários)");
    }
}
