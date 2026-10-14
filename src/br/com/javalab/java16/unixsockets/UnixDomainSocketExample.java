package br.com.javalab.java16.unixsockets;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Java 16 - Unix-Domain Socket Channels (JEP 380)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que e: {@code SocketChannel} e {@code ServerSocketChannel} passam a suportar
 * sockets de dominio Unix (AF_UNIX): a comunicacao acontece por um arquivo no sistema
 * de arquivos ({@code UnixDomainSocketAddress}), sem pilha TCP/IP. Funciona em Linux,
 * macOS e Windows 10 / Server 2019 ou superior.</p>
 * <p>Beneficio: comunicacao entre processos na mesma maquina mais rapida e segura
 * (protegida por permissoes de arquivo, nao exposta na rede) - ex.: sidecars, Docker.</p>
 * <p>Antes: TCP em localhost ou bibliotecas nativas (JNI/junixsocket).</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/unixsockets/UnixDomainSocketExample.java
 * </pre>
 */
public class UnixDomainSocketExample {

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("uds");
        Path socketFile = dir.resolve("orders.sock");
        UnixDomainSocketAddress address = UnixDomainSocketAddress.of(socketFile);
        try {
            try (ServerSocketChannel server = ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
                server.bind(address);
                System.out.println("=== Servidor escutando em " + socketFile + " ===");
                System.out.println("Arquivo de socket existe? " + Files.exists(socketFile));

                Thread serverThread = new Thread(() -> serveOne(server), "uds-server");
                serverThread.start();

                System.out.println("=== Cliente ===");
                try (SocketChannel client = SocketChannel.open(address)) {
                    client.write(StandardCharsets.UTF_8.encode("status pedido 1001"));
                    client.shutdownOutput();
                    System.out.println("Resposta: " + readAll(client));
                }
                serverThread.join();
            }
        } finally {
            Files.deleteIfExists(socketFile); // o arquivo nao e apagado automaticamente
            Files.delete(dir);
        }
        System.out.println("Arquivo de socket removido no final.");
    }

    private static void serveOne(ServerSocketChannel server) {
        try (SocketChannel channel = server.accept()) {
            String request = readAll(channel);
            System.out.println("Servidor recebeu: " + request + " (local: " + channel.getLocalAddress() + ")");
            channel.write(StandardCharsets.UTF_8.encode("pedido 1001 = PAID"));
        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    private static String readAll(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (channel.read(buffer) != -1 && buffer.hasRemaining()) {
            // le ate o outro lado fechar a escrita
        }
        buffer.flip();
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }
}
