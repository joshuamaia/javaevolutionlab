package br.com.javalab.java10.collectors;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Java 10 - Collectors.toUnmodifiableList / toUnmodifiableSet / toUnmodifiableMap
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: coletores que produzem coleções imutáveis (mesmas regras de
 * {@code List.of}/{@code Set.of}/{@code Map.of}: sem {@code null}, escrita proibida).</p>
 * <p>Benefício: o resultado do stream já sai imutável, sem passo extra.</p>
 * <p>Antes: {@code Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)}.
 * E {@code Collectors.toList()} não garante mutabilidade nem imutabilidade.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/collectors/ToUnmodifiableCollectorsExample.java
 * </pre>
 */
public class ToUnmodifiableCollectorsExample {

    public static void main(String[] args) {
        List<String> orders = List.of("P1:Ana:150", "P2:Bruno:80", "P3:Ana:45", "P4:Carla:300");

        System.out.println("=== Abordagem antiga: collectingAndThen ===");
        List<String> oldIds = orders.stream()
                .map(o -> o.split(":")[0])
                .collect(Collectors.collectingAndThen(Collectors.toList(), java.util.Collections::unmodifiableList));
        System.out.println("IDs: " + oldIds);

        System.out.println();
        System.out.println("=== Java 10: toUnmodifiableList ===");
        List<String> ids = orders.stream().map(o -> o.split(":")[0]).collect(Collectors.toUnmodifiableList());
        System.out.println("IDs: " + ids);
        try {
            ids.add("P5");
        } catch (UnsupportedOperationException e) {
            System.out.println("ids.add -> UnsupportedOperationException");
        }

        System.out.println();
        System.out.println("=== toUnmodifiableSet ===");
        Set<String> customers = orders.stream().map(o -> o.split(":")[1]).collect(Collectors.toUnmodifiableSet());
        System.out.println("Clientes: " + customers);

        System.out.println();
        System.out.println("=== toUnmodifiableMap (com função de merge para chaves repetidas) ===");
        Map<String, Integer> totalByCustomer = orders.stream()
                .collect(Collectors.toUnmodifiableMap(
                        o -> o.split(":")[1],
                        o -> Integer.parseInt(o.split(":")[2]),
                        Integer::sum));
        System.out.println("Total por cliente: " + totalByCustomer);

        System.out.println();
        System.out.println("=== null não é permitido ===");
        try {
            java.util.stream.Stream.of("A", null).collect(Collectors.toUnmodifiableList());
        } catch (NullPointerException e) {
            System.out.println("Stream com null -> NullPointerException");
        }
    }
}
