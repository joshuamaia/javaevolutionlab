package br.com.javalab.java08.collectors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Java 8 - Collectors.partitioningBy (JEP 107)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: caso especial de agrupamento com um {@code Predicate}: o resultado é sempre
 * um {@code Map<Boolean, ...>} com as chaves {@code true} e {@code false} (mesmo vazias).</p>
 * <p>Benefício: dividir uma coleção em dois grupos (aprovados/reprovados, em estoque/esgotado)
 * numa única passada.</p>
 * <p>Antes: duas listas e um if/else dentro do for.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/collectors/PartitioningByExample.java
 * </pre>
 */
public class PartitioningByExample {

    static class Transaction {
        final String id;
        final double amount;
        final String country;

        Transaction(String id, double amount, String country) {
            this.id = id;
            this.amount = amount;
            this.country = country;
        }

        boolean isSuspicious() {
            return amount > 5000 || !country.equals("BR");
        }

        @Override
        public String toString() {
            return id;
        }
    }

    public static void main(String[] args) {
        List<Transaction> transactions = Arrays.asList(
                new Transaction("T1", 120, "BR"),
                new Transaction("T2", 9800, "BR"),
                new Transaction("T3", 45, "US"),
                new Transaction("T4", 300, "BR"),
                new Transaction("T5", 7000, "AR"));

        System.out.println("=== partitioningBy simples ===");
        Map<Boolean, List<Transaction>> partitioned = transactions.stream()
                .collect(Collectors.partitioningBy(Transaction::isSuspicious));
        System.out.println("Suspeitas: " + partitioned.get(true));
        System.out.println("Normais:   " + partitioned.get(false));

        System.out.println();
        System.out.println("=== partitioningBy com downstream ===");
        Map<Boolean, Long> counts = transactions.stream()
                .collect(Collectors.partitioningBy(Transaction::isSuspicious, Collectors.counting()));
        Map<Boolean, Double> totals = transactions.stream()
                .collect(Collectors.partitioningBy(Transaction::isSuspicious,
                        Collectors.summingDouble(t -> t.amount)));
        System.out.println("Quantidade: " + counts);
        System.out.println("Valor total: " + totals);

        System.out.println();
        System.out.println("=== As duas chaves sempre existem ===");
        Map<Boolean, List<Transaction>> huge = transactions.stream()
                .collect(Collectors.partitioningBy(t -> t.amount > 1_000_000));
        System.out.println("Acima de 1 milhão: " + huge.get(true) + " | demais: " + huge.get(false));
    }
}
