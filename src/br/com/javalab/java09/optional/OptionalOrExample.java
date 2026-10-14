package br.com.javalab.java09.optional;

import java.util.Map;
import java.util.Optional;

/**
 * Java 9 - Optional.or
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code or(supplier)} devolve o próprio {@code Optional} se tiver valor, ou
 * o {@code Optional} produzido pelo fornecedor (avaliado de forma preguiçosa).</p>
 * <p>Benefício: encadear fontes alternativas (cache, banco, valor padrão) mantendo o
 * resultado como {@code Optional}.</p>
 * <p>Antes: {@code orElseGet} devolve o valor "cru" (não um Optional), então encadear
 * várias buscas exigia {@code if}s aninhados.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/optional/OptionalOrExample.java
 * </pre>
 */
public class OptionalOrExample {

    private static final Map<String, String> CACHE = Map.of("ana", "Ana (cache)");
    private static final Map<String, String> DATABASE = Map.of("ana", "Ana (banco)", "bruno", "Bruno (banco)");

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (Java 8) ===");
        Optional<String> customer = findInCache("bruno");
        if (!customer.isPresent()) {
            customer = findInDatabase("bruno");
        }
        System.out.println("bruno -> " + customer);

        System.out.println();
        System.out.println("=== Java 9: cache.or(banco) ===");
        for (String login : new String[] {"ana", "bruno", "carla"}) {
            Optional<String> result = findInCache(login)
                    .or(() -> findInDatabase(login));
            System.out.println(login + " -> " + result);
        }
    }

    private static Optional<String> findInCache(String login) {
        System.out.println("  consultando cache para " + login);
        return Optional.ofNullable(CACHE.get(login));
    }

    private static Optional<String> findInDatabase(String login) {
        System.out.println("  consultando banco para " + login);
        return Optional.ofNullable(DATABASE.get(login));
    }
}
