package br.com.javalab.java25.moduleimport;

import module java.base;
import module java.net.http;    // HttpClient, HttpRequest, HttpResponse
import module jdk.httpserver;   // HttpServer, HttpExchange (módulo do JDK, não do Java SE)

/**
 * Java 25 - Module Import Declarations (JEP 511)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 23 (JEP 476) e segunda Preview no 24
 * (JEP 494, que trouxe importação transitiva via {@code java.se} e a precedência dos imports
 * sob demanda). Sem mudanças no 25.</p>
 *
 * <p>O que é: {@code import module M;} importa todos os pacotes exportados por M (e pelos
 * módulos que M exige transitivamente). Funciona em qualquer classe, inclusive no classpath.
 * Em arquivos de código compacto (JEP 512), {@code import module java.base;} é implícito.</p>
 * <p>Benefício: protótipos e exemplos com APIs de vários pacotes ficam com 2-3 linhas de
 * import em vez de 10-20.</p>
 * <p>Antes: {@code import java.net.URI; import java.net.InetSocketAddress;
 * import java.net.http.HttpClient; import java.net.http.HttpRequest; ...} - um por tipo.</p>
 *
 * <p>Foco (23: só java.base; 24: ambiguidades e transitividade): combinar módulos de
 * áreas diferentes - um servidor HTTP local ({@code jdk.httpserver}) e um cliente
 * ({@code java.net.http}) - com apenas três linhas de import.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/moduleimport/ModuleImportFinalExample.java
 * </pre>
 */
public class ModuleImportFinalExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Servidor HTTP local (porta efêmera) ===");
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/estoque", exchange -> {
            byte[] body = "{\"produto\":\"caneta\",\"qtd\":120}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        int port = server.getAddress().getPort();
        System.out.println("Servidor em http://localhost:" + port + "/estoque");

        try (HttpClient client = HttpClient.newHttpClient()) {
            System.out.println();
            System.out.println("=== Cliente HTTP consultando o servidor ===");
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/estoque"))
                    .timeout(Duration.ofSeconds(5)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println("Content-Type: " + response.headers().firstValue("Content-Type").orElse("?"));
            System.out.println("Corpo: " + response.body());
        } finally {
            server.stop(0);
        }

        System.out.println();
        System.out.println("Tipos usados vieram de 3 módulos: "
                + Stream.of(HttpServer.class, HttpClient.class, Duration.class)
                        .map(c -> c.getModule().getName()).distinct().toList());
    }
}
