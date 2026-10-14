package br.com.javalab.java18.simplewebserver;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;
import com.sun.net.httpserver.SimpleFileServer.OutputLevel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Java 18 - Simple Web Server (JEP 408)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: um servidor HTTP mínimo, somente leitura, que serve arquivos estáticos de um
 * diretório. Vem como ferramenta de linha de comando ({@code jwebserver}) e como API
 * ({@link SimpleFileServer}, no módulo {@code jdk.httpserver}).</p>
 * <p>Benefício: prototipação, testes e "servir esta pasta rapidinho" sem instalar nada.
 * NÃO é para produção (só HTTP/1.1, sem HTTPS, só GET/HEAD).</p>
 * <p>Antes: {@code python -m http.server} ou escrever um {@code HttpHandler} manualmente.</p>
 *
 * <p>Pela linha de comando (bloqueia até Ctrl+C):</p>
 * <pre>
 * jwebserver                              # serve o diretório atual em 127.0.0.1:8000
 * jwebserver -p 9000 -d C:\caminho\absoluto -o verbose
 * java -m jdk.httpserver                  # equivalente ao jwebserver
 * </pre>
 *
 * <p>Este exemplo NÃO é uma aplicação web: ele sobe o servidor em localhost na porta 0
 * (efêmera), serve um diretório temporário, faz duas requisições com {@link HttpClient} e para.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java18/simplewebserver/SimpleWebServerExample.java
 * </pre>
 */
public class SimpleWebServerExample {

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("javalab-web");
        Files.writeString(root.resolve("index.html"),
                "<html><body><h1>Catálogo de produtos</h1></body></html>", StandardCharsets.UTF_8);
        Files.writeString(root.resolve("produtos.csv"), "id;nome;preco\n1;Teclado;199.90\n2;Mouse;89.90\n");

        // O diretório precisa ser um caminho ABSOLUTO.
        HttpServer server = SimpleFileServer.createFileServer(
                new InetSocketAddress("127.0.0.1", 0), root.toAbsolutePath(), OutputLevel.INFO);
        server.start();
        int port = server.getAddress().getPort();
        System.out.println("=== Servidor iniciado em http://127.0.0.1:" + port + "/ servindo " + root + " ===");

        try {
            HttpClient client = HttpClient.newHttpClient();
            for (String path : new String[] {"/produtos.csv", "/index.html", "/nao-existe.txt"}) {
                HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println();
                System.out.println("GET " + path + " -> " + response.statusCode()
                        + " (" + response.headers().firstValue("Content-Type").orElse("?") + ")");
                if (response.statusCode() == 200) {
                    System.out.println(response.body());
                }
            }
        } finally {
            server.stop(0);
            System.out.println();
            System.out.println("=== Servidor parado ===");
            deleteRecursively(root);
        }
    }

    private static void deleteRecursively(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(p);
            }
        }
    }
}
