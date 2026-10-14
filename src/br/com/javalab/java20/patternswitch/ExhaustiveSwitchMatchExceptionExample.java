package br.com.javalab.java20.patternswitch;

/**
 * Java 20 - Pattern Matching for switch, quarta Preview (JEP 433)
 *
 * <p>Status: Quarta Preview no Java 20. Histórico: Previews no 17 (406), 18 (420), 19 (427);
 * definitivo no 21 (JEP 441). Este código usa sintaxe que permaneceu igual e compila no JDK 25 sem flags.</p>
 *
 * <p>Mudanças do Java 20 (foco deste exemplo):</p>
 * <ul>
 *   <li><b>{@link MatchException}</b>: nova exceção lançada quando um switch exaustivo não encontra
 *       nenhum caso em tempo de execução (ex.: classe sealed recompilada com novo subtipo), e também
 *       quando o método de acesso de um record lança exceção durante o matching de um record pattern
 *       (a exceção original vira a causa). Antes era {@code IncompatibleClassChangeError}.</li>
 *   <li><b>Exaustividade com sealed + record patterns aninhados</b>: o compilador combina os
 *       subtipos permitidos com as componentes dos records para provar que todos os casos foram cobertos.</li>
 *   <li>Inferência de argumentos de tipo em record patterns dentro do switch (ver JEP 432).</li>
 * </ul>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java20/patternswitch/ExhaustiveSwitchMatchExceptionExample.java
 * </pre>
 */
public class ExhaustiveSwitchMatchExceptionExample {

    sealed interface Customer permits Person, Company { }
    record Person(String name) implements Customer { }
    record Company(String name, String taxId) implements Customer { }

    sealed interface Shipping permits Standard, Express { }
    record Standard() implements Shipping { }
    record Express(int hours) implements Shipping { }

    record Order(Customer customer, Shipping shipping) { }

    /** Record com accessor customizado que pode falhar (simula um dado corrompido). */
    record Invoice(String number) {
        @Override
        public String number() {
            if (number.isEmpty()) {
                throw new IllegalStateException("número da nota vazio");
            }
            return number;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Exaustividade: sealed x sealed dentro de record pattern (sem default) ===");
        Order[] orders = {
                new Order(new Person("Ana"), new Standard()),
                new Order(new Person("Bruno"), new Express(24)),
                new Order(new Company("ACME", "12.345.678/0001-90"), new Standard()),
                new Order(new Company("Globex", "98.765.432/0001-10"), new Express(4))};
        for (Order order : orders) {
            System.out.println(plan(order));
        }

        System.out.println();
        System.out.println("=== MatchException: accessor lança exceção durante o matching ===");
        for (Invoice invoice : new Invoice[] {new Invoice("NF-001"), new Invoice("")}) {
            try {
                System.out.println(describe(invoice));
            } catch (MatchException e) {
                System.out.println("MatchException! causa: " + e.getCause());
            }
        }
    }

    /** As 4 combinações (Person|Company) x (Standard|Express) estão cobertas: nenhum default é necessário. */
    static String plan(Order order) {
        return switch (order) {
            case Order(Person p, Standard s) -> p.name() + ": entrega padrão para pessoa física";
            case Order(Person p, Express e) -> p.name() + ": expresso em " + e.hours() + "h";
            case Order(Company c, Standard s) -> c.name() + ": padrão com nota para CNPJ " + c.taxId();
            case Order(Company c, Express(int hours)) when hours < 12 -> c.name() + ": expresso prioritário";
            case Order(Company c, Express e) -> c.name() + ": expresso corporativo";
        };
    }

    static String describe(Object document) {
        return switch (document) {
            case Invoice(String number) -> "Nota fiscal " + number;
            default -> "Documento desconhecido";
        };
    }
}
