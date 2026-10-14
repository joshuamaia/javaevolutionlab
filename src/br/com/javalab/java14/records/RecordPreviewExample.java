package br.com.javalab.java14.records;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Java 14 - Records (JEP 359)
 *
 * <p>Status: Preview. Segunda Preview no Java 15 (JEP 384); Definitivo no Java 16
 * (JEP 395). No Java 14 exigia {@code --enable-preview --source 14}; a sintaxe usada
 * aqui permaneceu igual e compila no JDK 25 sem flags.</p>
 *
 * <p>O que e: {@code record} declara uma classe portadora de dados imutavel. A partir
 * dos componentes do cabecalho o compilador gera: campos private final, construtor
 * canonico, accessors (sem prefixo "get"), equals, hashCode e toString.</p>
 * <p>Beneficio: elimina dezenas de linhas de boilerplate e torna a intencao explicita.</p>
 * <p>Antes: classe final com campos, construtor, getters, equals/hashCode/toString
 * escritos a mao (ou gerados por IDE/Lombok).</p>
 *
 * <p>Novidades das versoes seguintes: java15/records/RecordSecondPreviewExample.java e
 * java16/records/RecordExample.java.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java14/records/RecordPreviewExample.java
 * </pre>
 */
public class RecordPreviewExample {

    /** Record basico: tudo gerado pelo compilador. */
    record Customer(String name, String email) {}

    /** Construtor compacto: validacao/normalizacao sem repetir parametros e atribuicoes. */
    record Money(double amount, String currency) {
        Money {
            if (amount < 0) {
                throw new IllegalArgumentException("valor negativo: " + amount);
            }
            Objects.requireNonNull(currency);
            currency = currency.toUpperCase(); // reatribui o PARAMETRO antes da atribuicao automatica
        }

        // metodos extras e fabricas estaticas sao permitidos
        Money plus(Money other) {
            return new Money(amount + other.amount, currency);
        }

        static Money brl(double amount) {
            return new Money(amount, "brl");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Accessors e toString gerados ===");
        Customer ana = new Customer("Ana", "ana@shop.com");
        System.out.println(ana);
        System.out.println("name(): " + ana.name() + " | email(): " + ana.email());

        System.out.println("=== equals/hashCode por valor ===");
        Customer sameAna = new Customer("Ana", "ana@shop.com");
        System.out.println("ana == sameAna?      " + (ana == sameAna));
        System.out.println("ana.equals(sameAna)? " + ana.equals(sameAna));
        Set<Customer> customers = new HashSet<>();
        customers.add(ana);
        customers.add(sameAna);
        System.out.println("Tamanho do Set: " + customers.size());

        System.out.println("=== Construtor compacto ===");
        Money price = new Money(4500.0, "brl");
        System.out.println(price + " (moeda normalizada)");
        System.out.println("Soma: " + price.plus(Money.brl(89.9)));
        try {
            new Money(-1, "BRL");
        } catch (IllegalArgumentException e) {
            System.out.println("Validacao: " + e.getMessage());
        }

        System.out.println("=== Imutavel: nao ha setters; 'alterar' = criar outro ===");
        Customer updated = new Customer(ana.name(), "ana.silva@shop.com");
        System.out.println(updated);
        System.out.println("Record e final e estende java.lang.Record: "
                + Customer.class.getSuperclass().getName());
    }
}
