package br.com.javalab.java15.jvm;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.util.List;

/**
 * Java 15 - Mudancas de JVM: ZGC Produto (JEP 377), Shenandoah Produto (JEP 379),
 * Biased Locking desativado (JEP 374) e remocao do Nashorn (JEP 372)
 *
 * <p>Status: ZGC e Shenandoah: Produto (JVM) - deixam de exigir
 * {@code -XX:+UnlockExperimentalVMOptions} (o G1 continua o padrao).
 * Biased locking: Deprecated no 15 (desligado por padrao), flag removida no Java 18.
 * Nashorn: Removido (modulos jdk.scripting.nashorn; hoje existe como projeto
 * separado "standalone Nashorn").</p>
 *
 * <p>O que e / beneficio:</p>
 * <ul>
 *   <li>ZGC (Java 11 experimental): pausas de milissegundos independentes do tamanho do
 *       heap. {@code java -XX:+UseZGC ...}</li>
 *   <li>Shenandoah (Java 12 experimental): compactacao concorrente.
 *       {@code java -XX:+UseShenandoahGC ...} (ausente em alguns builds, ex.: Oracle JDK).</li>
 *   <li>Biased locking: otimizacao de locks sem disputa que deixou de compensar e
 *       complicava a JVM.</li>
 *   <li>Nashorn: motor JavaScript do JDK 8, deprecated no 11 e removido no 15.</li>
 * </ul>
 *
 * <p>Este exemplo relanca o "java" atual com cada flag (apenas "-version") e consulta
 * os motores de script disponiveis.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/jvm/JvmChangesObservationExample.java
 * java -XX:+UseZGC -Xlog:gc -version
 * </pre>
 */
public class JvmChangesObservationExample {

    public static void main(String[] args) throws Exception {
        String java = ProcessHandle.current().info().command().orElse("java");

        System.out.println("=== GCs como produto (sem UnlockExperimentalVMOptions) ===");
        report(java, "-XX:+UseZGC");
        report(java, "-XX:+UseShenandoahGC");

        System.out.println("=== JEP 374: biased locking ===");
        report(java, "-XX:+UseBiasedLocking");
        System.out.println("  (no Java 15 a flag era aceita com aviso de deprecacao; removida no Java 18)");

        System.out.println("=== JEP 372: Nashorn removido ===");
        ScriptEngineManager manager = new ScriptEngineManager();
        System.out.println("Motor 'nashorn': " + manager.getEngineByName("nashorn"));
        List<ScriptEngineFactory> factories = manager.getEngineFactories();
        System.out.println("Motores de script disponiveis: " + (factories.isEmpty() ? "nenhum" : factories));
    }

    private static void report(String java, String flag) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(java, flag, "-version").redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes());
        int exit = process.waitFor();
        String firstLine = output.lines().findFirst().orElse("");
        System.out.printf("  %-24s -> %s%n", flag, exit == 0
                ? "aceita (exit 0)"
                : "rejeitada (exit " + exit + "): " + firstLine);
    }
}
