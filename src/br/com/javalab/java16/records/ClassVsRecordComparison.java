package br.com.javalab.java16.records;

import java.util.Objects;

/**
 * Java 16 - Comparacao: classe tradicional vs record (JEP 395)
 *
 * <p>Status: Definitivo (Java 16).</p>
 *
 * <p>O que e: o mesmo "Produto" escrito como classe imutavel tradicional (cerca de 45
 * linhas) e como record (1 linha), mostrando que o comportamento e equivalente:
 * construcao, leitura, igualdade por valor, hashCode e toString.</p>
 * <p>Diferencas importantes: accessors sem "get" ({@code name()}), record e sempre
 * final, nao pode estender outra classe (ja estende java.lang.Record) e nao pode ter
 * campos de instancia alem dos componentes.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/records/ClassVsRecordComparison.java
 * </pre>
 */
public class ClassVsRecordComparison {

    /** Antes do Java 16: classe imutavel escrita a mao. */
    static final class ProductClass {
        private final Long id;
        private final String name;
        private final double price;

        ProductClass(Long id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ProductClass)) return false;
            ProductClass other = (ProductClass) o;
            return Double.compare(price, other.price) == 0
                    && Objects.equals(id, other.id)
                    && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, price);
        }

        @Override
        public String toString() {
            return "ProductClass[id=" + id + ", name=" + name + ", price=" + price + "]";
        }
    }

    /** Java 16: o mesmo em uma linha. */
    record ProductRecord(Long id, String name, double price) {}

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: classe ===");
        ProductClass c1 = new ProductClass(1L, "Notebook", 4500.00);
        ProductClass c2 = new ProductClass(1L, "Notebook", 4500.00);
        System.out.println(c1);
        System.out.println("getName(): " + c1.getName());
        System.out.println("equals? " + c1.equals(c2) + " | hashCode iguais? " + (c1.hashCode() == c2.hashCode()));

        System.out.println("=== Java 16: record ===");
        ProductRecord r1 = new ProductRecord(1L, "Notebook", 4500.00);
        ProductRecord r2 = new ProductRecord(1L, "Notebook", 4500.00);
        System.out.println(r1);
        System.out.println("name(): " + r1.name());
        System.out.println("equals? " + r1.equals(r2) + " | hashCode iguais? " + (r1.hashCode() == r2.hashCode()));

        System.out.println("=== Metadados ===");
        System.out.println("ProductClass.isRecord()? " + ProductClass.class.isRecord());
        System.out.println("ProductRecord.isRecord()? " + ProductRecord.class.isRecord()
                + " | componentes: " + ProductRecord.class.getRecordComponents().length);
        System.out.println("Linhas de codigo: classe ~45, record 1");
    }
}
