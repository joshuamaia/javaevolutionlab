package br.com.javalab.java11.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

/**
 * Java 11 - Comparação: HttpURLConnection x HttpClient (JEP 321)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: a mesma tarefa (POST de um pedido em JSON e leitura da resposta) feita com a
 * API antiga ({@code HttpURLConnection}, Java 1.1) e com o {@code HttpClient} do Java 11.</p>
 * <p>Diferenças principais:</p>
 * <ul>
 *   <li>HttpURLConnection: objeto mutável, configuração por setters, {@code setDoOutput},
 *       streams manuais, erro 4xx/5xx vira {@code IOException} em {@code getInputStream()}
 *       (precisa ler {@code getErrorStream()}), só síncrono, só HTTP/1.1.</li>
 *   <li>HttpClient: requisição imutável via builder, {@code BodyPublishers}/
 *       {@code BodyHandlers} prontos, status sempre disponível, síncrono e assíncrono,
 *       HTTP/2, cliente reutilizável e thread-safe.</li>
 * </ul>
 *
 * <p>Servidor local na porta 0 criado no próprio exemplo.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/httpclient/HttpUrlConnectionVsHttpClientComparison.java
 * </pre>
 */
public class HttpUrlConnectionVsHttpClientComparison {

    private static final String ORDER_JSON = "{\"customer\":\"Ana\",\"total\":150.0}";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/orders", exchange -> {
            byte[] request = exchange.getRequestBody().readAllBytes();
            boolean valid = new String(request, StandardCharsets.UTF_8).contains("customer");
            byte[] response = (valid ? "{\"id\":7,\"status\":\"CRIADO\"}" : "{\"error\":\"cliente obrigatório\"}")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(valid ? 201 : 400, response.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response);
            }
        });
        server.start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        String url = "http://localhost:" + server.getAddress().getPort() + "/orders";
        try {
            System.out.println("=== Antes: HttpURLConnection ===");
            System.out.println("Válido  : " + postWithHttpUrlConnection(url, ORDER_JSON));
            System.out.println("Inválido: " + postWithHttpUrlConnection(url, "{}"));

            System.out.println();
            System.out.println("=== Java 11: HttpClient ===");
            HttpClient client = HttpClient.newBuilder().executor(executor).build();
            System.out.println("Válido  : " + postWithHttpClient(client, url, ORDER_JSON));
            System.out.println("Inválido: " + postWithHttpClient(client, url, "{}"));
        } finally {
            server.stop(0);
            executor.shutdownNow();
        }
    }

    private static String postWithHttpUrlConnection(String url, String json) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5_000);
            try (OutputStream out = connection.getOutputStream()) {
                out.write(json.getBytes(StandardCharsets.UTF_8));
            }
            int status = connection.getResponseCode();
            // 4xx/5xx: getInputStream() lançaria IOException; é preciso usar getErrorStream()
            InputStream body = status < 400 ? connection.getInputStream() : connection.getErrorStream();
            try (InputStream in = body) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                in.transferTo(buffer);
                return status + " " + buffer.toString(StandardCharsets.UTF_8.name());
            }
        } finally {
            connection.disconnect();
        }
    }

    private static String postWithHttpClient(HttpClient client, String url, String json)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() + " " + response.body();
    }
}
