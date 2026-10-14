package br.com.javalab.java08.optional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Java 8 - Optional
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: contêiner que pode ou não conter um valor não nulo; torna explícito, no tipo
 * de retorno, que "pode não haver resultado".</p>
 * <p>Benefício: encadear transformações ({@code map}, {@code flatMap}, {@code filter}) e
 * definir alternativas ({@code orElse}, {@code orElseGet}, {@code orElseThrow}) sem
 * cadeias de {@code if (x != null)}.</p>
 * <p>Antes: retornar {@code null} e torcer para o chamador verificar.</p>
 * <p>Boas práticas: usar como tipo de retorno; evitar como campo/parâmetro;
 * nunca chamar {@code get()} sem verificar. ({@code ifPresentOrElse}, {@code or} e
 * {@code stream()} vieram no Java 9; {@code isEmpty()} no 11.)</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/optional/OptionalExample.java
 * </pre>
 */
public class OptionalExample {

    static class Address {
        final String city;
        final String zipCode; // pode ser null

        Address(String city, String zipCode) {
            this.city = city;
            this.zipCode = zipCode;
        }

        Optional<String> getZipCode() {
            return Optional.ofNullable(zipCode);
        }
    }

    static class Customer {
        final String name;
        final Address address; // pode ser null

        Customer(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }
    }

    private static final Map<Integer, Customer> DATABASE = new HashMap<>();

    static {
        DATABASE.put(1, new Customer("Alice", new Address("São Paulo", "01000-000")));
        DATABASE.put(2, new Customer("Bob", new Address("Recife", null)));
        DATABASE.put(3, new Customer("Carol", null));
    }

    static Optional<Customer> findCustomer(int id) {
        return Optional.ofNullable(DATABASE.get(id));
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: null checks aninhados ===");
        Customer c = DATABASE.get(2);
        String oldZip = "não informado";
        if (c != null && c.address != null && c.address.zipCode != null) {
            oldZip = c.address.zipCode;
        }
        System.out.println("CEP do cliente 2: " + oldZip);

        System.out.println();
        System.out.println("=== Java 8: map/flatMap/orElse ===");
        for (int id = 1; id <= 4; id++) {
            String zip = findCustomer(id)
                    .flatMap(Customer::getAddress)
                    .flatMap(Address::getZipCode)
                    .orElse("não informado");
            String city = findCustomer(id)
                    .flatMap(Customer::getAddress)
                    .map(a -> a.city.toUpperCase())
                    .orElse("?");
            System.out.println("Cliente " + id + ": cidade=" + city + ", CEP=" + zip);
        }

        System.out.println();
        System.out.println("=== filter e ifPresent ===");
        findCustomer(1)
                .filter(cu -> cu.name.startsWith("A"))
                .ifPresent(cu -> System.out.println("Cliente VIP encontrado: " + cu.name));

        System.out.println();
        System.out.println("=== orElse vs orElseGet (avaliação preguiçosa) ===");
        findCustomer(1).map(cu -> cu.name).orElse(expensiveDefault("orElse"));
        findCustomer(1).map(cu -> cu.name).orElseGet(() -> expensiveDefault("orElseGet"));
        System.out.println("(orElseGet não chamou o default porque havia valor)");

        System.out.println();
        System.out.println("=== orElseThrow ===");
        try {
            findCustomer(99).orElseThrow(() -> new IllegalArgumentException("Cliente 99 não existe"));
        } catch (IllegalArgumentException e) {
            System.out.println("Exceção: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Criação ===");
        System.out.println("Optional.of(\"x\"):        " + Optional.of("x"));
        System.out.println("Optional.empty():        " + Optional.empty());
        System.out.println("Optional.ofNullable(null): " + Optional.ofNullable(null));
        try {
            Optional.of(null);
        } catch (NullPointerException e) {
            System.out.println("Optional.of(null) lança NullPointerException");
        }
    }

    private static String expensiveDefault(String origin) {
        System.out.println("  default calculado via " + origin);
        return "desconhecido";
    }
}
