package br.com.javalab.java16.records;

/**
 * Java 16 - Records (JEP 395)
 *
 * <p>Status: Definitivo. Historico: Preview no Java 14 (JEP 359),
 * segunda Preview no Java 15 (JEP 384).</p>
 *
 * <p>O que e: {@code record} e uma forma concisa de declarar uma classe imutavel
 * portadora de dados. A partir do cabecalho {@code Product(Long id, String name,
 * double price)} o compilador gera campos private final, construtor canonico,
 * accessors {@code id()}, {@code name()}, {@code price()}, alem de equals, hashCode
 * e toString baseados em todos os componentes.</p>
 * <p>Beneficio: menos boilerplate, intencao explicita ("isto e um dado") e igualdade
 * por valor correta por construcao.</p>
 * <p>Antes: classe com campos, construtor, getters, equals, hashCode e toString
 * escritos a mao (ver {@code ClassVsRecordComparison}).</p>
 *
 * <p>No definitivo (JEP 395), alem de records, foram relaxadas as regras de membros
 * estaticos em classes internas (ver {@code StaticMembersInInnerClassesExample}).
 * Evolucao: java14/records (basico) e java15/records (locais, interfaces, anotacoes).</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/records/RecordExample.java
 * </pre>
 */
public class RecordExample {

    record Product(Long id, String name, double price) {}

    public static void main(String[] args) {
        Product product = new Product(1L, "Notebook", 4500.00);

        System.out.println(product);
        System.out.println(product.name());
        System.out.println(product.price());
    }
}
