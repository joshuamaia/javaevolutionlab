package br.com.javalab.java09.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Java 9 - Unified JVM Logging (JEP 158) e Unified GC Logging (JEP 271)
 *
 * <p>Status: Produto (JVM).</p>
 *
 * <p>O que é: um sistema único de log da JVM controlado por {@code -Xlog}, com
 * <i>tags</i> (gc, class+load, safepoint, os...), níveis (error..trace), destinos
 * (stdout, stderr, arquivo com rotação) e decorações (uptime, pid, nível, tags).</p>
 * <p>Benefício: uma sintaxe para todos os subsistemas e reconfiguração em tempo de
 * execução com {@code jcmd <pid> VM.log}.</p>
 * <p>Antes: dezenas de flags avulsas: {@code -XX:+PrintGCDetails},
 * {@code -XX:+PrintGCDateStamps}, {@code -Xloggc:arquivo}, {@code -verbose:class}...
 * (removidas/convertidas no Java 9).</p>
 *
 * <p>Comandos para experimentar:</p>
 * <pre>
 * java -Xlog:help
 * java -Xlog:gc -version
 * java -Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=5,filesize=10m MinhaApp
 * java -Xlog:class+load=info -version
 * jcmd &lt;pid&gt; VM.log list
 * </pre>
 *
 * <p>O exemplo relança o próprio {@code java} com {@code -Xlog} e também consulta a
 * configuração de log da JVM atual pelo MBean de diagnóstico (equivalente a
 * {@code jcmd VM.log list}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/logging/UnifiedJvmLoggingExample.java
 * </pre>
 */
public class UnifiedJvmLoggingExample {

    public static void main(String[] args) throws Exception {
        String java = ProcessHandle.current().info().command().orElse("java");

        System.out.println("=== java -Xlog:gc -version ===");
        run(List.of(java, "-Xlog:gc", "-version"), 10);

        System.out.println();
        System.out.println("=== java -Xlog:class+load=info -version (primeiras linhas) ===");
        run(List.of(java, "-Xlog:class+load=info", "-version"), 5);

        System.out.println();
        System.out.println("=== Configuração de log desta JVM (DiagnosticCommand vmLog list) ===");
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName diagnostic = new ObjectName("com.sun.management:type=DiagnosticCommand");
        Object result = server.invoke(diagnostic, "vmLog",
                new Object[] {new String[] {"list"}}, new String[] {String[].class.getName()});
        String[] lines = String.valueOf(result).split("\\R");
        for (int i = 0; i < Math.min(lines.length, 8); i++) {
            System.out.println("  " + lines[i]);
        }
    }

    private static void run(List<String> command, int maxLines) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
            String line;
            int printed = 0;
            while ((line = reader.readLine()) != null) {
                if (printed++ < maxLines) {
                    System.out.println("  " + line);
                }
            }
        }
        process.waitFor(30, TimeUnit.SECONDS);
    }
}
