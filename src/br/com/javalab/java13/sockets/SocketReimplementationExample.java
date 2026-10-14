package br.com.javalab.java13.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Java 13 - Reimplementacao da API de Socket legada (JEP 353)
 *
 * <p>Status: Produto (JVM/biblioteca). A API publica nao mudou.</p>
 *
 * <p>O que e: {@code java.net.Socket} e {@code ServerSocket} passaram a usar uma nova
 * implementacao interna, {@code sun.nio.ch.NioSocketImpl}, baseada na infraestrutura
 * do NIO, no lugar da antiga {@code PlainSocketImpl} (codigo Java + C dos anos 90).
 * No Java 13 era possivel voltar a antiga com {@code -Djdk.net.usePlainSocketImpl=true};
 * a implementacao antiga foi removida no Java 18.</p>
 * <p>Beneficio: codigo mais simples de manter, usa locks java.util.concurrent em vez
 * de monitores nativos e preparou o caminho para as virtual threads (Java 21).</p>
 *
 * <p>Este exemplo cria um servidor de eco local (porta efemera) e, enquanto ele esta
 * bloqueado em accept(), inspeciona a pilha da thread para mostrar qual classe de
 * implementacao esta em uso.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java13/sockets/SocketReimplementationExample.java
 * </pre>
 */
public class SocketReimplementationExample {

    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
            System.out.println("=== Servidor de eco em localhost:" + server.getLocalPort() + " ===");

            Thread serverThread = new Thread(() -> serveOneClient(server), "echo-server");
            serverThread.start();
            Thread.sleep(300); // da tempo para a thread ficar bloqueada em accept()

            System.out.println("=== Implementacao interna (pilha da thread em accept) ===");
            for (StackTraceElement frame : serverThread.getStackTrace()) {
                if (frame.getClassName().contains("SocketImpl")) {
                    System.out.println("  " + frame.getClassName() + "." + frame.getMethodName());
                }
            }

            System.out.println("=== Cliente ===");
            try (Socket client = new Socket(InetAddress.getLoopbackAddress(), server.getLocalPort());
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true, StandardCharsets.UTF_8);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {
                out.println("pedido 1001: status?");
                System.out.println("Resposta: " + in.readLine());
            }
            serverThread.join();
        }
    }

    private static void serveOneClient(ServerSocket server) {
        try (Socket socket = server.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {
            String line = in.readLine();
            System.out.println("Servidor recebeu: " + line);
            out.println("ECO -> " + line + " PAID");
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
}
