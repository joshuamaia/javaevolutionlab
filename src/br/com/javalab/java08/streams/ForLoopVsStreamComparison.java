package br.com.javalab.java08.streams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Java 8 - Comparação: for tradicional vs Streams (JEP 107)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: a mesma consulta ("top 3 clientes por valor gasto em pedidos pagos")
 * escrita de forma imperativa e com Streams + Collectors.</p>
 * <p>Benefício das Streams: intenção explícita, menos variáveis mutáveis, encadeamento.</p>
 * <p>Quando o for ainda é melhor: laços com {@code break}/{@code continue} complexos,
 * exceções checadas, alteração de variáveis locais, hot paths onde cada alocação
 * importa, ou quando a stream ficaria menos legível.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/streams/ForLoopVsStreamComparison.java
 * </pre>
 */
public class ForLoopVsStreamComparison {

    static class Order {
        final String customer;
        final double total;
        final boolean paid;

        Order(String customer, double total, boolean paid) {
            this.customer = customer;
            this.total = total;
            this.paid = paid;
        }
    }

    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
                new Order("Alice", 300, true), new Order("Bob", 80, true),
                new Order("Carol", 500, false), new Order("Alice", 120, true),
                new Order("Davi", 220, true), new Order("Bob", 400, true),
                new Order("Eva", 50, true));

        System.out.println("=== for tradicional ===");
        Map<String, Double> totals = new HashMap<>();
        for (Order o : orders) {
            if (o.paid) {
                Double current = totals.get(o.customer);
                totals.put(o.customer, current == null ? o.total : current + o.total);
            }
        }
        List<Map.Entry<String, Double>> entries = new ArrayList<>(totals.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b) {
                return Double.compare(b.getValue(), a.getValue());
            }
        });
        List<String> topOld = new ArrayList<>();
        for (int i = 0; i < entries.size() && i < 3; i++) {
            topOld.add(entries.get(i).getKey() + "=" + entries.get(i).getValue());
        }
        System.out.println("Top 3: " + topOld);

        System.out.println();
        System.out.println("=== Streams ===");
        List<String> topNew = orders.stream()
                .filter(o -> o.paid)
                .collect(Collectors.groupingBy(o -> o.customer, Collectors.summingDouble(o -> o.total)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList());
        System.out.println("Top 3: " + topNew);
        System.out.println("Mesmo resultado? " + topOld.equals(topNew));

        System.out.println();
        System.out.println("=== Caso em que o for é mais simples: parar no primeiro estouro de limite ===");
        double budget = 600;
        double spent = 0;
        int processed = 0;
        for (Order o : orders) {
            if (spent + o.total > budget) {
                break; // Streams do Java 8 não têm takeWhile (só no Java 9)
            }
            spent += o.total;
            processed++;
        }
        System.out.println("Pedidos processados dentro do orçamento: " + processed + " (R$ " + spent + ")");
    }
}
