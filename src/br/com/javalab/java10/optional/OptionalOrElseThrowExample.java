package br.com.javalab.java10.optional;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Java 10 - Optional.orElseThrow() sem argumentos
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code orElseThrow()} devolve o valor ou lança
 * {@code NoSuchElementException} — exatamente o que {@code get()} faz, mas com um nome
 * que deixa explícito que pode lançar exceção.</p>
 * <p>Benefício: código mais honesto; {@code get()} é tão curto que parecia "seguro" e
 * escondia o risco. Hoje {@code orElseThrow()} é a alternativa recomendada a {@code get()}.</p>
 * <p>Antes: {@code optional.get()} ou {@code orElseThrow(NoSuchElementException::new)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/optional/OptionalOrElseThrowExample.java
 * </pre>
 */
public class OptionalOrElseThrowExample {

    private static final Map<String, String> CUSTOMERS = Map.of("123", "Ana Souza");

    public static void main(String[] args) {
        System.out.println("=== Antes: get() (nome não indica o risco) ===");
        System.out.println(findCustomer("123").get());

        System.out.println();
        System.out.println("=== Java 10: orElseThrow() ===");
        System.out.println(findCustomer("123").orElseThrow());
        try {
            findCustomer("999").orElseThrow();
        } catch (NoSuchElementException e) {
            System.out.println("Cliente 999 -> NoSuchElementException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Com exceção de negócio: orElseThrow(Supplier) (Java 8) ===");
        try {
            findCustomer("999").orElseThrow(() -> new IllegalArgumentException("Cliente 999 não cadastrado"));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Optional<String> findCustomer(String id) {
        return Optional.ofNullable(CUSTOMERS.get(id));
    }
}
