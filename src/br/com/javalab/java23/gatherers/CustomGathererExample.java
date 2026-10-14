package br.com.javalab.java23.gatherers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

/**
 * Java 23 - Stream Gatherers, segunda Preview (JEP 473)
 *
 * <p>Status: Segunda Preview (sem mudanças de API em relação ao Java 22 - JEP 461);
 * definitivo no Java 24 (JEP 485). Compila no JDK 25 sem flags.</p>
 *
 * <p>O que é: além dos gatherers prontos (vistos no exemplo do Java 22: janelas e fold),
 * qualquer um pode escrever o seu com {@code Gatherer.ofSequential(...)}, informando um
 * <b>inicializador</b> (estado), um <b>integrador</b> (recebe cada elemento e decide o que
 * empurrar para baixo - e se continua) e, opcionalmente, um <b>finalizador</b>.</p>
 * <p>Este exemplo mostra o que o do Java 22 não mostrou: gatherer customizado com estado
 * ({@code distinctBy}), gatherer que interrompe o stream ({@code takeWhileTotalBelow}),
 * {@code Gatherers.scan} (acumulado progressivo) e {@code Gatherers.mapConcurrent}
 * (map em paralelo usando virtual threads, preservando a ordem).</p>
 * <p>Antes: {@code distinct()} só por {@code equals}; parar por estado acumulado exigia
 * laço manual; map concorrente exigia ExecutorService + lista de Futures.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java23/gatherers/CustomGathererExample.java
 * </pre>
 */
public class CustomGathererExample {

    record Order(String id, String customer, double total) {}

    /** Gatherer customizado: deixa passar só o primeiro elemento de cada chave. */
    static <T, K> Gatherer<T, ?, T> distinctBy(Function<? super T, ? extends K> key) {
        return Gatherer.ofSequential(
                HashSet<K>::new,                                         // estado
                Gatherer.Integrator.ofGreedy((Set<K> seen, T element, Gatherer.Downstream<? super T> downstream) -> {
                    if (seen.add(key.apply(element))) {
                        return downstream.push(element);                 // emite
                    }
                    return true;                                         // descarta e continua
                }));
    }

    /** Gatherer que interrompe o stream quando a soma acumulada ultrapassaria o limite. */
    static Gatherer<Order, ?, Order> takeWhileTotalBelow(double limit) {
        class Sum { double value; }
        return Gatherer.ofSequential(Sum::new, (sum, order, downstream) -> {
            if (sum.value + order.total() > limit) {
                return false;                                            // short-circuit
            }
            sum.value += order.total();
            return downstream.push(order);
        });
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("O1", "ana", 120), new Order("O2", "bruno", 80),
                new Order("O3", "ana", 300), new Order("O4", "carla", 50),
                new Order("O5", "bruno", 40));

        System.out.println("=== Gatherer customizado: primeiro pedido de cada cliente ===");
        orders.stream().gather(distinctBy(Order::customer)).forEach(System.out::println);

        System.out.println();
        System.out.println("=== Gatherer com short-circuit: pedidos até somar R$ 250 ===");
        orders.stream().gather(takeWhileTotalBelow(250)).forEach(System.out::println);

        System.out.println();
        System.out.println("=== Gatherers.scan: faturamento acumulado pedido a pedido ===");
        orders.stream()
                .gather(Gatherers.scan(() -> 0.0, (acc, order) -> acc + order.total()))
                .forEach(acc -> System.out.print(acc + " "));
        System.out.println();

        System.out.println();
        System.out.println("=== Gatherers.mapConcurrent: consulta de frete em paralelo (máx. 3) ===");
        long start = System.nanoTime();
        List<String> quotes = orders.stream()
                .gather(Gatherers.mapConcurrent(3, CustomGathererExample::quoteShipping))
                .toList();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        quotes.forEach(System.out::println);
        System.out.println("5 consultas de ~200 ms em " + elapsedMs + " ms (ordem preservada)");
    }

    static String quoteShipping(Order order) {
        try {
            Thread.sleep(200); // simula chamada remota (roda em virtual thread)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return order.id() + " -> frete R$ " + (10 + order.total() * 0.05);
    }
}
