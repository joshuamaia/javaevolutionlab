package br.com.javalab.java09.streams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 9 - Stream.ofNullable
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code Stream.ofNullable(valor)} devolve um stream com um elemento, ou um
 * stream vazio se o valor for {@code null}.</p>
 * <p>Benefício: integrar valores possivelmente nulos em pipelines (especialmente dentro
 * de {@code flatMap}) sem {@code if (x != null)}.</p>
 * <p>Antes: {@code valor == null ? Stream.empty() : Stream.of(valor)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/streams/StreamOfNullableExample.java
 * </pre>
 */
public class StreamOfNullableExample {

    public static void main(String[] args) {
        Map<String, String> emailByCustomer = new HashMap<>();
        emailByCustomer.put("Ana", "ana@exemplo.com");
        emailByCustomer.put("Carla", "carla@exemplo.com");
        List<String> customers = List.of("Ana", "Bruno", "Carla");

        System.out.println("=== Abordagem antiga: operador ternário ===");
        List<String> oldEmails = customers.stream()
                .flatMap(name -> {
                    String email = emailByCustomer.get(name);
                    return email == null ? Stream.<String>empty() : Stream.of(email);
                })
                .collect(Collectors.toList());
        System.out.println(oldEmails);

        System.out.println();
        System.out.println("=== Java 9: Stream.ofNullable ===");
        List<String> emails = customers.stream()
                .flatMap(name -> Stream.ofNullable(emailByCustomer.get(name)))
                .collect(Collectors.toList());
        System.out.println(emails + "  (Bruno não tem e-mail e foi ignorado)");

        System.out.println();
        System.out.println("Stream.ofNullable(null).count() = " + Stream.ofNullable(null).count());
        System.out.println("Stream.ofNullable(\"x\").count() = " + Stream.ofNullable("x").count());
    }
}
