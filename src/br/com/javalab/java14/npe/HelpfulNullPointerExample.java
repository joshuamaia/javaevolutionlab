package br.com.javalab.java14.npe;

/**
 * Java 14 - Helpful NullPointerExceptions (JEP 358)
 *
 * <p>Status: Produto (JVM). No Java 14 era desligado por padrao e precisava de
 * {@code -XX:+ShowCodeDetailsInExceptionMessages}; ligado por padrao desde o Java 15.
 * Para desligar: {@code -XX:-ShowCodeDetailsInExceptionMessages}.</p>
 *
 * <p>O que e: a JVM analisa o bytecode que falhou e descreve na mensagem da NPE
 * EXATAMENTE qual referencia era nula ("because the return value of ...getAddress()
 * is null").</p>
 * <p>Beneficio: em expressoes encadeadas (a.b().c().d) nao e mais preciso adivinhar qual
 * elo era nulo apenas pelo numero da linha.</p>
 * <p>Antes: mensagem nula ({@code getMessage() == null}), so o numero da linha.</p>
 *
 * <p>Obs.: nomes de variaveis locais aparecem apenas se o codigo foi compilado com
 * informacao de depuracao ({@code javac -g}); caso contrario a JVM mostra
 * "&lt;local1&gt;". Campos e metodos sempre aparecem pelo nome.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java14/npe/HelpfulNullPointerExample.java
 * java -XX:-ShowCodeDetailsInExceptionMessages src/br/com/javalab/java14/npe/HelpfulNullPointerExample.java
 * </pre>
 */
public class HelpfulNullPointerExample {

    static class Address {
        String city;
        Address(String city) { this.city = city; }
        String getCity() { return city; }
    }

    static class Customer {
        Address address;
        Customer(Address address) { this.address = address; }
        Address getAddress() { return address; }
    }

    static class Order {
        Customer customer;
        Order(Customer customer) { this.customer = customer; }
        Customer getCustomer() { return customer; }
    }

    // campos estaticos: seus nomes sempre aparecem na mensagem (variaveis locais so com javac -g)
    static Order withoutAddress = new Order(new Customer(null));
    static Order withoutCity = new Order(new Customer(new Address(null)));
    static int[][] matrix = new int[2][];
    static Integer quantity = null;

    public static void main(String[] args) {
        Order complete = new Order(new Customer(new Address("Recife")));
        System.out.println("Cidade do pedido completo: " + complete.getCustomer().getAddress().getCity());

        System.out.println("=== NPE em cadeia de metodos ===");
        try {
            System.out.println(withoutAddress.getCustomer().getAddress().getCity().toUpperCase());
        } catch (NullPointerException e) {
            print(e);
        }

        System.out.println("=== NPE em cadeia de campos ===");
        try {
            System.out.println(withoutCity.customer.address.city.length());
        } catch (NullPointerException e) {
            print(e);
        }

        System.out.println("=== NPE em array ===");
        try {
            matrix[1][0] = 10;
        } catch (NullPointerException e) {
            print(e);
        }

        System.out.println("=== NPE em unboxing ===");
        try {
            int q = quantity;
            System.out.println(q);
        } catch (NullPointerException e) {
            print(e);
        }

        System.out.println("=== Variavel local: nome depende de javac -g ===");
        Order local = null;
        try {
            System.out.println(local.getCustomer());
        } catch (NullPointerException e) {
            print(e);
        }
    }

    private static void print(NullPointerException e) {
        System.out.println("Mensagem: " + (e.getMessage() == null
                ? "null (detalhes desligados: -XX:-ShowCodeDetailsInExceptionMessages)"
                : e.getMessage()));
    }
}
