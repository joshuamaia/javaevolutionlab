package br.com.javalab.java07.objects;

import java.util.Comparator;
import java.util.Objects;

/**
 * Java 7 - Classe utilitária java.util.Objects
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: métodos estáticos null-safe para tarefas comuns: {@code equals}, {@code hash},
 * {@code hashCode}, {@code toString(o, default)}, {@code requireNonNull}, {@code compare},
 * {@code deepEquals}.</p>
 * <p>Benefício: equals/hashCode corretos em poucas linhas e validação de argumentos
 * com mensagens claras.</p>
 * <p>Antes: comparações manuais {@code a == null ? b == null : a.equals(b)} e hashCode
 * com multiplicações por 31 escritas à mão.</p>
 * <p>Obs.: {@code isNull}, {@code nonNull} e {@code requireNonNull(obj, Supplier)} vieram no Java 8;
 * {@code requireNonNullElse} e {@code checkIndex} no Java 9.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/objects/ObjectsExample.java
 * </pre>
 */
public class ObjectsExample {

    static final class Customer {
        private final String name;
        private final String email; // pode ser null

        Customer(String name, String email) {
            this.name = Objects.requireNonNull(name, "name não pode ser null");
            this.email = email;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Customer)) {
                return false;
            }
            Customer other = (Customer) o;
            return Objects.equals(name, other.name) && Objects.equals(email, other.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, email);
        }

        @Override
        public String toString() {
            return "Customer{" + name + ", email=" + Objects.toString(email, "<sem e-mail>") + "}";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: equals null-safe manual ===");
        String a = null;
        String b = "x";
        boolean oldEquals = (a == null) ? (b == null) : a.equals(b);
        System.out.println("manual: " + oldEquals);

        System.out.println();
        System.out.println("=== Java 7: Objects.equals / hash / toString ===");
        Customer c1 = new Customer("Alice", null);
        Customer c2 = new Customer("Alice", null);
        System.out.println("Objects.equals(null, \"x\"): " + Objects.equals(a, b));
        System.out.println("c1.equals(c2): " + c1.equals(c2));
        System.out.println("hash iguais?   " + (c1.hashCode() == c2.hashCode()));
        System.out.println("toString:      " + c1);
        System.out.println("Objects.hashCode(null): " + Objects.hashCode(null));

        System.out.println();
        System.out.println("=== requireNonNull ===");
        try {
            new Customer(null, "bob@example.com");
        } catch (NullPointerException e) {
            System.out.println("NPE com mensagem clara: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== compare e deepEquals ===");
        Comparator<String> byLength = new Comparator<String>() {
            @Override
            public int compare(String x, String y) {
                return Integer.compare(x.length(), y.length()); // Integer.compare também é do Java 7
            }
        };
        System.out.println("Objects.compare(\"abc\", \"de\", byLength): " + Objects.compare("abc", "de", byLength));
        int[][] m1 = {{1, 2}, {3}};
        int[][] m2 = {{1, 2}, {3}};
        System.out.println("Objects.deepEquals(matrizes): " + Objects.deepEquals(m1, m2));
    }
}
