package br.com.javalab.java15.datagram;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Java 15 - Reimplementacao da API DatagramSocket legada (JEP 373)
 *
 * <p>Status: Produto (biblioteca). A API publica nao mudou.</p>
 *
 * <p>O que e: continuando o trabalho do Java 13 (JEP 353, Socket/ServerSocket),
 * {@code DatagramSocket} e {@code MulticastSocket} passaram a delegar para a
 * infraestrutura NIO ({@code sun.nio.ch.DatagramChannelImpl}) em vez da antiga
 * {@code PlainDatagramSocketImpl}. No 15 era possivel voltar a antiga com
 * {@code -Djdk.net.usePlainDatagramSocketImpl=true}; removida depois.</p>
 * <p>Beneficio: codigo mais simples e compativel com virtual threads.</p>
 *
 * <p>Este exemplo faz um eco UDP em localhost (porta efemera) e inspeciona a pilha da
 * thread bloqueada em receive() para mostrar as classes internas usadas.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/datagram/DatagramSocketReimplementationExample.java
 * </pre>
 */
public class DatagramSocketReimplementationExample {

    public static void main(String[] args) throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        try (DatagramSocket server = new DatagramSocket(new InetSocketAddress(loopback, 0));
             DatagramSocket client = new DatagramSocket(new InetSocketAddress(loopback, 0))) {
            System.out.println("=== Servidor UDP em localhost:" + server.getLocalPort() + " ===");

            Thread serverThread = new Thread(() -> echoOnce(server), "udp-server");
            serverThread.start();
            Thread.sleep(300);

            System.out.println("=== Implementacao interna (pilha da thread em receive) ===");
            for (StackTraceElement frame : serverThread.getStackTrace()) {
                if (frame.getClassName().startsWith("sun.nio.ch") || frame.getClassName().contains("Datagram")) {
                    System.out.println("  " + frame.getClassName() + "." + frame.getMethodName());
                }
            }

            System.out.println("=== Cliente envia metrica de pedido ===");
            byte[] data = "orders.paid:1|c".getBytes(StandardCharsets.UTF_8);
            client.send(new DatagramPacket(data, data.length, loopback, server.getLocalPort()));
            DatagramPacket reply = new DatagramPacket(new byte[256], 256);
            client.setSoTimeout(2000);
            client.receive(reply);
            System.out.println("Resposta: " + new String(reply.getData(), 0, reply.getLength(), StandardCharsets.UTF_8));
            serverThread.join();
        }
    }

    private static void echoOnce(DatagramSocket server) {
        try {
            DatagramPacket packet = new DatagramPacket(new byte[256], 256);
            server.receive(packet);
            String text = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            System.out.println("Servidor recebeu: " + text);
            byte[] answer = ("ACK " + text).getBytes(StandardCharsets.UTF_8);
            server.send(new DatagramPacket(answer, answer.length, packet.getSocketAddress()));
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }
}
