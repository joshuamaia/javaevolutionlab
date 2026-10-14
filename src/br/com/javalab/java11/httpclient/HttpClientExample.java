package br.com.javalab.java11.httpclient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Java 11 - HTTP Client (JEP 321)
 *
 * <p>Status: Definitivo. Histórico: Incubadora no Java 9 e 10
 * ({@code jdk.incubator.http}, JEP 110).</p>
 *
 * <p>O que é: {@code java.net.http.HttpClient}, cliente HTTP moderno com API fluente
 * (builders imutáveis), HTTP/1.1 e HTTP/2, WebSocket, chamadas síncronas
 * ({@code send}) e assíncronas ({@code sendAsync} com {@code CompletableFuture}).</p>
 * <p>Benefício: dispensa bibliotecas externas (Apache HttpClient, OkHttp) para a maioria
 * dos casos.</p>
 * <p>Antes: {@code HttpURLConnection} (Java 1.1) — API verbosa, só síncrona, sem HTTP/2
 * (ver {@code HttpUrlConnectionVsHttpClientComparison}).</p>
 *
 * <p>O exemplo cria um servidor local ({@code com.sun.net.httpserver}) na porta 0; nada
 * depende de internet.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/httpclient/HttpClientExample.java
 * </pre>
 */
public class HttpClientExample {

    public static void main(String[] args) throws Exception {
        HttpServer server = startServer();
        ExecutorService clientExecutor = Executors.newFixedThreadPool(2);
        try {
            String baseUrl = "http://localhost:" + server.getAddress().getPort();
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .executor(clientExecutor)
                    .build();

            System.out.println("=== GET síncrono ===");
            HttpRequest get = HttpRequest.newBuilder(URI.create(baseUrl + "/orders/42"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status : " + response.statusCode());
            System.out.println("Versão : " + response.version());
            System.out.println("Tipo   : " + response.headers().firstValue("Content-Type").orElse("?"));
            System.out.println("Corpo  : " + response.body());

            System.out.println();
            System.out.println("=== POST com corpo JSON ===");
            HttpRequest post = HttpRequest.newBuilder(URI.create(baseUrl + "/orders"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"customer\":\"Ana\",\"total\":150.0}"))
                    .build();
            HttpResponse<String> created = client.send(post, HttpResponse.BodyHandlers.ofString());
            System.out.println(created.statusCode() + " " + created.body());

            System.out.println();
            System.out.println("=== sendAsync: 3 requisições em paralelo ===");
            List<CompletableFuture<String>> futures = List.of(1, 2, 3).stream()
                    .map(id -> HttpRequest.newBuilder(URI.create(baseUrl + "/orders/" + id)).build())
                    .map(request -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .thenApply(HttpResponse::body))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
            futures.forEach(future -> System.out.println("  " + future.join()));

            System.out.println();
            System.out.println("=== 404 não lança exceção: basta checar o status ===");
            HttpResponse<Void> missing = client.send(
                    HttpRequest.newBuilder(URI.create(baseUrl + "/inexistente")).build(),
                    HttpResponse.BodyHandlers.discarding());
            System.out.println("Status: " + missing.statusCode());
        } finally {
            server.stop(0);
            clientExecutor.shutdownNow();
        }
    }

    private static HttpServer startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/orders", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            if (method.equals("POST")) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                respond(exchange, 201, "{\"id\":99,\"received\":" + body + "}");
            } else {
                String id = path.substring(path.lastIndexOf('/') + 1);
                respond(exchange, 200, "{\"id\":" + id + ",\"status\":\"ENVIADO\"}");
            }
        });
        server.start();
        return server;
    }

    private static void respond(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
