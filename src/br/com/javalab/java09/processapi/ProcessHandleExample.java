package br.com.javalab.java09.processapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java 9 - Process API Updates: ProcessHandle (JEP 102)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code ProcessHandle} representa um processo nativo do sistema operacional:
 * PID, comando, argumentos, usuário, horário de início, CPU usada, pai e filhos.
 * {@code Process} ganhou {@code pid()}, {@code toHandle()} e {@code onExit()}.</p>
 * <p>Benefício: gerenciar processos de forma portátil (Windows/Linux/macOS).</p>
 * <p>Antes: obter o PID exigia truques como
 * {@code ManagementFactory.getRuntimeMXBean().getName().split("@")[0]} ou código nativo;
 * não havia como listar filhos nem ser avisado do término sem bloquear em
 * {@code waitFor()}.</p>
 *
 * <p>O processo filho é o próprio {@code java -version} do JDK em execução, descoberto via
 * {@code ProcessHandle.current().info().command()} — funciona em qualquer SO.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/processapi/ProcessHandleExample.java
 * </pre>
 */
public class ProcessHandleExample {

    public static void main(String[] args) throws IOException, InterruptedException,
            ExecutionException, TimeoutException {
        System.out.println("=== Processo atual ===");
        ProcessHandle current = ProcessHandle.current();
        ProcessHandle.Info info = current.info();
        System.out.println("PID       : " + current.pid());
        System.out.println("Comando   : " + info.command().orElse("?"));
        System.out.println("Usuário   : " + info.user().orElse("?"));
        System.out.println("Início    : " + info.startInstant().map(Object::toString).orElse("?"));
        System.out.println("CPU usada : " + info.totalCpuDuration().map(Object::toString).orElse("?"));
        System.out.println("PID do pai: " + current.parent().map(p -> String.valueOf(p.pid())).orElse("?"));

        System.out.println();
        System.out.println("=== Iniciando processo filho: java -version ===");
        String javaExecutable = info.command().orElse("java");
        ProcessBuilder builder = new ProcessBuilder(javaExecutable, "-version");
        builder.redirectErrorStream(true); // java -version escreve em stderr
        Process child = builder.start();
        System.out.println("PID do filho: " + child.pid());
        System.out.println("Filhos do processo atual: " + current.children().count());

        CompletableFuture<Process> exitFuture = child.onExit();
        exitFuture.thenAccept(p -> System.out.println(
                "[onExit] processo " + p.pid() + " terminou com código " + p.exitValue()));

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(child.getInputStream(), Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  filho> " + line);
            }
        }

        exitFuture.get(30, TimeUnit.SECONDS);
        Thread.sleep(100); // dá tempo ao callback de onExit imprimir
        System.out.println("Filho ainda vivo? " + child.toHandle().isAlive());

        System.out.println();
        System.out.println("=== Alguns processos do sistema (ProcessHandle.allProcesses) ===");
        System.out.println("Total visível: " + ProcessHandle.allProcesses().count());
        ProcessHandle.allProcesses()
                .filter(p -> p.info().command().isPresent())
                .limit(3)
                .forEach(p -> System.out.println("  " + p.pid() + " " + p.info().command().get()));
    }
}
